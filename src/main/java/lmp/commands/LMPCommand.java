package lmp.commands;

import lmp.*;
import lmp.api.Api;
import lmp.constants.Constants;
import lmp.constants.YmlFileNames;
import lmp.listeners.playerJoinEvents.BankLoginEvent;
import lmp.listeners.playerQuitEvents.BankLogoutEvent;
import lmp.runnable.LMPTimer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class LMPCommand implements CommandExecutor {
    public static void resetAllNonLinkedPlayerBalances(ArrayList<String> whitelistArr) throws IOException {
        for (String essPlayerID : whitelistArr) {
            if (!essPlayerID.contains(".")) {
                File playerDataFile = new File("plugins/Essentials/userdata", essPlayerID + ".yml");
                FileConfiguration playerDataCfg = YamlConfiguration.loadConfiguration(playerDataFile);
                if (Bukkit.getOfflinePlayer(UUID.fromString(essPlayerID)).getName() != null) {
                    playerDataCfg.set("money", 5000.00);
                    playerDataCfg.save(playerDataFile);
                }
            }
        }
    }

    public static void resetPlayerBalances(ArrayList<String> arrIDToReset) throws IOException {
        for (String essPlayerID : arrIDToReset) {
            if (!essPlayerID.contains(".")) {
                File playerDataFile = new File("plugins/Essentials/userdata", essPlayerID + ".yml");
                FileConfiguration playerDataCfg = YamlConfiguration.loadConfiguration(playerDataFile);
                Main.log.info("Name to reset: " + Bukkit.getOfflinePlayer(UUID.fromString(essPlayerID)).getName());
//                    playerDataCfg.set("money", 5000.00);
                playerDataCfg.save(playerDataFile);
            }
        }
    }

    public static boolean isPlayerHoldingXPStorageBottle(Player player) {
        boolean isPlayerHoldingXPStorageBottle = false;
        if (player.getInventory().getItemInMainHand().getType() == Material.EXPERIENCE_BOTTLE && player.getInventory().getItemInMainHand().getItemMeta() != null && player.getInventory().getItemInMainHand().getItemMeta().getLore() != null && player.getInventory().getItemInMainHand().getItemMeta().getLore().get(0).equalsIgnoreCase("Experience Storage Bottle")) {
            isPlayerHoldingXPStorageBottle = true;
        }
        return isPlayerHoldingXPStorageBottle;
    }

    public static void spectateInsideRandomPlayer(Player player) {
        Random rand = new Random();
        int n = rand.nextInt(Bukkit.getOnlinePlayers().size());
        ArrayList<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (!onlinePlayers.get(n).getName().equalsIgnoreCase("latch93") && Boolean.FALSE.equals(Api.isPlayerInvisible(onlinePlayers.get(n).getUniqueId().toString()))) {
            player.teleport(onlinePlayers.get(n).getLocation());
            player.setGameMode(GameMode.SPECTATOR);
            player.setSpectatorTarget(onlinePlayers.get(n));
        } else {
            FileConfiguration xpFarmCfg = Api.getFileConfiguration(YmlFileNames.YML_XP_FARM_FILE_NAME);
            double spawnX = xpFarmCfg.getDouble("spawnX");
            double spawnY = xpFarmCfg.getDouble("spawnY");
            double spawnZ = xpFarmCfg.getDouble("spawnZ");
            Location spawnLocation = new Location(Bukkit.getWorld("world"), spawnX, spawnY, spawnZ);
            player.teleport(spawnLocation);
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        try {
            if (args[0] != null) {
                if (args[0].equalsIgnoreCase("stat")) {
                    sender.sendMessage(ChatColor.GREEN + "Total deaths: " + ChatColor.GOLD + player.getStatistic(Statistic.DEATHS));
                    sender.sendMessage(ChatColor.GREEN + "Total mobs killed: " + ChatColor.GOLD + player.getStatistic(Statistic.MOB_KILLS));
                    sender.sendMessage(ChatColor.GREEN + "Number of times jumped: " + ChatColor.GOLD + player.getStatistic(Statistic.JUMP));
                } else if (args[0].equalsIgnoreCase("rtp")) {
                    RandomTeleport.randomTp(player);
                } else if (args[0].equalsIgnoreCase("lotto")) {
                    try {
                        Lottery.lottoCommands(player, args[1], sender);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.RED + "Invalid command. Please use this command as follows -> " + ChatColor.AQUA + "[/dt lotto check] [/dt lotto buyin] [/dt lotto total]");
                    }
                } else if (args[0].equalsIgnoreCase("withdraw")) {
                    double amount = Double.parseDouble(args[1]);
                    if (amount > 499) {
                        if (amount <= Api.getEconomy().getBalance(Api.getOfflinePlayerFromPlayer(player))) {
                            Api.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), amount);
                            ItemStack paper = new ItemStack(Material.PAPER, 1);
                            ItemMeta im = paper.getItemMeta();
                            assert im != null;
                            im.setDisplayName("MoneyOrder - " + player.getName() + " - " + amount);
                            im.setLore(Collections.singletonList("MoneyOrder - " + player.getName() + " - " + amount));
                            paper.setItemMeta(im);
                            World world = player.getWorld();
                            Location dropLocation = player.getLocation();
                            world.dropItem(dropLocation, paper);
                            player.sendMessage(ChatColor.GREEN + "You have withdrawn " + ChatColor.GOLD + "$" + amount);
                            FileConfiguration moneyOrderLogCfg = Api.getFileConfiguration(YmlFileNames.YML_MONEY_ORDER_LOG_FILE_NAME);
                            Date date = new Date();
                            moneyOrderLogCfg.set(player.getUniqueId().toString() + ".playerName", player.getName());
                            moneyOrderLogCfg.set(player.getUniqueId().toString() + "." + date + ".type", "withdraw");
                            moneyOrderLogCfg.set(player.getUniqueId().toString() + "." + date + ".amount", amount);
                            moneyOrderLogCfg.save(Api.getConfigFile(YmlFileNames.YML_MONEY_ORDER_LOG_FILE_NAME));
                        } else {
                            player.sendMessage(ChatColor.RED + "Can't withdraw more than your current balance.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Can't withdraw less than $500.");
                    }
                } else if (args[0].equalsIgnoreCase("deposit")) {
                    Inventory playerInv = player.getInventory();
                    int count = 0;
                    File essentialsFile = new File("plugins/Essentials", "config.yml");
                    for (ItemStack is : playerInv) {
                        if (is != null) {
                            ItemMeta im = is.getItemMeta();
                            if (im != null && im.getLore() != null && im.getLore().get(0).contains("MoneyOrder")) {
                                String[] lore = im.getLore().get(0).split(" - ");
                                String playerName = lore[1];
                                double amount = Double.parseDouble(lore[2]) * is.getAmount();
                                double maxAmount = YamlConfiguration.loadConfiguration(essentialsFile).getDouble("max-money");
                                double playerBalance = Api.getEconomy().getBalance(player);
                                double sumOfBalanceAndAmount = Double.sum(playerBalance, amount);
                                if (Double.compare(sumOfBalanceAndAmount, maxAmount) <= 0) {
                                    Api.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), amount);
                                    player.sendMessage(ChatColor.GREEN + "Deposited " + ChatColor.GOLD + "$" + amount + ChatColor.GREEN + " from " + ChatColor.GOLD + playerName);
                                    is.setAmount(0);
                                    player.getInventory().setItem(count, is);
                                    FileConfiguration moneyOrderLogCfg = Api.getFileConfiguration(YmlFileNames.YML_MONEY_ORDER_LOG_FILE_NAME);
                                    Date date = new Date();
                                    moneyOrderLogCfg.set(player.getUniqueId().toString() + ".playerName", player.getName());
                                    moneyOrderLogCfg.set(player.getUniqueId().toString() + "." + date + ".from", playerName);
                                    moneyOrderLogCfg.set(player.getUniqueId().toString() + "." + date + ".type", "deposit");
                                    moneyOrderLogCfg.set(player.getUniqueId().toString() + "." + date + ".amount", amount);
                                    moneyOrderLogCfg.save(Api.getConfigFile(YmlFileNames.YML_MONEY_ORDER_LOG_FILE_NAME));
                                } else {
                                    player.sendMessage(ChatColor.RED + "Can't deposit more MoneyOrders because it will exceed the server's maximum amount.");
                                }
                            }
                        }
                        count++;
                    }
                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("switch") && args[1] != null) {
                    FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
                    Inventory playerInventory = player.getInventory();
                    Location creativeChestLocation = new Location(Bukkit.getWorld("world"), configCfg.getDouble("creativeChest.x"), configCfg.getDouble("creativeChest.y"), configCfg.getDouble("creativeChest.z"));
                    Block creativeChestBlock = creativeChestLocation.getBlock();
                    Chest creativeChest = (Chest) creativeChestBlock.getState();
                    Location survivalChestLocation = new Location(Bukkit.getWorld("world"), configCfg.getDouble("survivalChest.x"), configCfg.getDouble("survivalChest.y"), configCfg.getDouble("survivalChest.z"));
                    Block survivalChestBlock = survivalChestLocation.getBlock();
                    Chest survivalChest = (Chest) survivalChestBlock.getState();
                    if (args[1].equalsIgnoreCase("creative") && player.getGameMode().equals(GameMode.SURVIVAL)) {
                        int count = 0;
                        for (ItemStack is : playerInventory) {
                            survivalChest.getInventory().setItem(count, is);
                            playerInventory.setItem(count, creativeChest.getInventory().getItem(count));
                            creativeChest.getInventory().setItem(count, new ItemStack(Material.AIR, 0));
                            count++;
                        }
                        player.setGameMode(GameMode.CREATIVE);
                    } else if (args[1].equalsIgnoreCase("survival") && player.getGameMode().equals(GameMode.CREATIVE)) {
                        int count = 0;
                        for (ItemStack is : playerInventory) {
                            creativeChest.getInventory().setItem(count, is);
                            playerInventory.setItem(count, survivalChest.getInventory().getItem(count));
                            survivalChest.getInventory().setItem(count, new ItemStack(Material.AIR, 0));
                            count++;
                        }
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                } else if (args[0].equalsIgnoreCase("transferPet")) {
                    if (args[1] == null && args[2] == null) {
                        return false;
                    }
                    List<EntityType> tameableEntities = new ArrayList<>();
                    tameableEntities.add(EntityType.PARROT);
                    tameableEntities.add(EntityType.CAT);
                    tameableEntities.add(EntityType.WOLF);
                    tameableEntities.add(EntityType.HORSE);
                    tameableEntities.add(EntityType.OCELOT);
                    tameableEntities.add(EntityType.MULE);
                    tameableEntities.add(EntityType.DONKEY);
                    tameableEntities.add(EntityType.FOX);

                    Collection<Entity> nearbyEntities = Objects.requireNonNull(player.getLocation().getWorld()).getNearbyEntities(player.getLocation(), 8, 8, 8);
                    List<Tameable> tamedPets = new ArrayList<>();
                    for (Entity entity : nearbyEntities) {
                        if (tameableEntities.contains(entity.getType())) {
                            tamedPets.add((Tameable) entity);
                        }
                    }
                    if (!tamedPets.isEmpty()) {
                        for (Tameable pet : tamedPets) {
                            if (pet.getName().equalsIgnoreCase(args[1])) {
                                if (pet.getOwner() == player) {
                                    continue;
                                }
                                pet.setOwner(Bukkit.getPlayer(args[2]));
                                player.sendMessage(ChatColor.GREEN + "You transferred ownership of " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " to " + ChatColor.GOLD + args[2]);
                                Objects.requireNonNull(Bukkit.getPlayer(args[2])).sendMessage(ChatColor.GREEN + "You now have ownership of " + ChatColor.GOLD + args[1]);
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("clearBoss") && args[1] != null) {
                    FileConfiguration bossCfg = Api.getFileConfiguration(YmlFileNames.YML_BOSS_FILE_NAME);
                    bossCfg.set("bossEnabled", false);
                    player.sendMessage(ChatColor.GREEN + "Boss has been terminated.");
                    try {
                        bossCfg.save(Api.getConfigFile(YmlFileNames.YML_BOSS_FILE_NAME));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("killBoss")) {
                    FileConfiguration bossCfg = Api.getFileConfiguration(YmlFileNames.YML_BOSS_FILE_NAME);
                    try {
                        Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(bossCfg.getString("bossUUID"))))).remove();
                        bossCfg.set("bossEnabled", false);
                        player.sendMessage(ChatColor.GREEN + "Boss has been terminated.");
                        try {
                            bossCfg.save(Api.getConfigFile(YmlFileNames.YML_BOSS_FILE_NAME));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (NullPointerException ignored) {

                    }
                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("removeHomes")) {
                    for (File file : Objects.requireNonNull(new File("plugins/Essentials/userdata").listFiles())) {
                        FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
                        if (conf.isSet("homes")) {
                            conf.set("homes", null);
                        }
                        if (conf.isSet("logoutlocation")) {
                            conf.set("logoutlocation.world", "cee8accb-f717-4b88-be30-a688b2a195ea");
                            conf.set("logoutlocation.x", -189.5);
                            conf.set("logoutlocation.y", 159.0);
                            conf.set("logoutlocation.z", -106.7);
                            conf.set("logoutlocation.yaw", -91.05023193359375);
                            conf.set("logoutlocation.pitch", 23.699981689453125);
                            conf.set("logoutlocation.world-name", "world");
                        }
                        if (conf.isSet("lastlocation")) {
                            conf.set("lastlocation.world", "cee8accb-f717-4b88-be30-a688b2a195ea");
                            conf.set("lastlocation.x", -189.5);
                            conf.set("lastlocation.y", 159.0);
                            conf.set("lastlocation.z", -106.7);
                            conf.set("lastlocation.yaw", -91.05023193359375);
                            conf.set("lastlocation.pitch", 23.699981689453125);
                            conf.set("lastlocation.world-name", "world");
                        }
                        try {
                            conf.save(file);
                            Main.log.info("Removed Homes for: " + conf.getString("last-account-name"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (args[0].equalsIgnoreCase("createDonationClaimList")){
                    DonationClaimRewards.createDonationUserFile();
                }
                else if (args[0].equalsIgnoreCase("link") && args[1] != null) {
                    Member discordMember = null;
                    try {
                        boolean hasMemberRole = false;
                        discordMember = Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(lmp.Constants.GUILD_ID)).getMemberById(args[1]);
                        for (Role role : Objects.requireNonNull(discordMember.getRoles())) {
                            if ("Member".equalsIgnoreCase(role.getName())) {
                                hasMemberRole = true;
                            }
                        }
                        if (Boolean.TRUE.equals(hasMemberRole)) {
                            User user = Main.luckPerms.getUserManager().getUser(player.getUniqueId());
                            assert user != null;
                            user.setPrimaryGroup("member");
                            InheritanceNode member = InheritanceNode.builder("member").value(true).build();
                            InheritanceNode defaultNode = InheritanceNode.builder("default").build();
                            user.data().add(member);
                            user.data().remove(defaultNode);
                            Main.luckPerms.getUserManager().saveUser(user);
//                            File playerDataFile = new File("plugins/Essentials/userdata", player.getUniqueId() + ".yml");
//                            FileConfiguration playerDataCfg = Api.getFileConfiguration(playerDataFile);
//                            Main.log.info(ASDASD: " + Objects.requireNonNull(player.getAddress()).getAddress().toString());

                            String ipInfo = ".ip-info.";
                            FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
                            whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".discordName", discordMember.getUser().getName());
                            whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".discordId", discordMember.getId());
                            whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".discordNickname", discordMember.getNickname());
                            whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".minecraftName", player.getName());
                            whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".minecraftId", player.getUniqueId().toString());
                            whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".joinTime", discordMember.getTimeJoined().toLocalDateTime().toString());
                            whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".isPlayerInDiscord", true);
                            whitelistCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ipInfo + "ipAddress", Objects.requireNonNull(player.getAddress()).getAddress().toString().substring(1));
                            whitelistCfg.save(Api.getConfigFile(YmlFileNames.YML_WHITELIST_FILE_NAME));
                            try {
                                TextChannel modChatChannel = LatchDiscord.getJDA().getTextChannelById(lmp.Constants.DISCORD_STAFF_CHAT_CHANNEL_ID);
                                assert modChatChannel != null;
                                modChatChannel.sendMessage("<@&971160639932362783> New player has joined the server. Discord Name: " + discordMember.getUser().getName() + " | Minecraft Name: " + player.getName()).queue();
                                whitelistCfg.save(Api.getConfigFile(YmlFileNames.YML_WHITELIST_FILE_NAME));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            DonationClaimRewards.addNewPlayerToDonationFile(player);
                            whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
                            player.sendMessage(ChatColor.GREEN + "You are now linked up and have perms. Happy Mining!!!");
                            whitelistCfg.save(Api.getConfigFile(YmlFileNames.YML_WHITELIST_FILE_NAME));
                        } else {
                            player.sendMessage(ChatColor.RED + "You need to react to the rules in the " + ChatColor.AQUA + "Discord Rules Channel " + ChatColor.RED + "before you can link your account.");
                        }
                    } catch (NullPointerException e) {
                        player.sendMessage(ChatColor.GREEN + "You are now linked up and have perms. Happy Mining!!!");
                        TextChannel modChatChannel = Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(lmp.Constants.GUILD_ID)).getTextChannelById(lmp.Constants.DISCORD_STAFF_CHAT_CHANNEL_ID);
                        assert modChatChannel != null;
                        assert discordMember != null;
                        modChatChannel.sendMessage(discordMember.getEffectiveName() + " maybe didn't link their accounts correctly. They should still be linked up well enough to play with all of the features. This is for Latch to debug. Minecraft Name: " + player.getName()).queue();
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage(ChatColor.GREEN + "View our Wiki here -> " + ChatColor.AQUA + "https://github.com/Latch93/DiscordText/wiki/LMP-Wiki");
                } else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("spectate")) {
                    spectateInsideRandomPlayer(player);
                } else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("resetAllBalances")) {
                    resetPlayerBalances(Api.getAllMinecraftIDOfLinkedPlayers());
                } else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("bmstart")) {
                    LMPTimer.startBloodmoon();
                } else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("easybm")) {
                    LMPTimer.setEasyBloodMoon();
                } else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("mediumbm")) {
                    LMPTimer.setMediumBloodMoon();
                } else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("hardbm")) {
                    LMPTimer.setHardBloodMoon();
                } else if (args[0].equalsIgnoreCase("xpDeposit")) {
                    if (Boolean.TRUE.equals(isPlayerHoldingXPStorageBottle(player))) {
                        if (args[1] != null) {
                            if (Api.getPlayerExp(player) >= Integer.parseInt(args[1])) {
                                String xpAmountString = player.getInventory().getItemInMainHand().getItemMeta().getLore().get(1);
                                int storageAmount = Integer.parseInt(xpAmountString.split(":")[1].replaceAll("\\s+", ""));
                                int finalAmount = storageAmount + Integer.parseInt(args[1]);
                                ItemMeta storageBottleMeta = player.getInventory().getItemInMainHand().getItemMeta();
                                List<String> loreArr = new ArrayList<>();
                                int count = 0;
                                for (String loreLine : Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta().getLore())) {
                                    if (count != 1) {
                                        loreArr.add(loreLine);
                                    } else {
                                        loreArr.add("XP: " + finalAmount);
                                    }
                                    count++;
                                }
                                storageBottleMeta.setLore(loreArr);
                                player.getInventory().getItemInMainHand().setItemMeta(storageBottleMeta);
                                Api.changePlayerExp(player, (-1 * Integer.parseInt(args[1])));
                                player.sendMessage(ChatColor.GREEN + "You now have " + ChatColor.GOLD + Api.getPlayerExp(player) + ChatColor.GREEN + " experience points.");
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough experience to deposit.");
                            }

                        } else {
                            player.sendMessage(ChatColor.RED + "You must give an amount of experience to deposit.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You must be holding an experience storage bottle.");
                    }
                } else if (args[0].equalsIgnoreCase("xpWithdraw")) {
                    if (Boolean.TRUE.equals(isPlayerHoldingXPStorageBottle(player))) {
                        String xpAmountString = player.getInventory().getItemInMainHand().getItemMeta().getLore().get(1);
                        int storageAmount = Integer.parseInt(xpAmountString.split(":")[1].replaceAll("\\s+", ""));
                        if (args[1] != null) {
                            if (storageAmount >= Integer.parseInt(args[1])) {
                                int finalAmount = storageAmount - Integer.parseInt(args[1]);
                                ItemMeta storageBottleMeta = player.getInventory().getItemInMainHand().getItemMeta();
                                List<String> loreArr = new ArrayList<>();
                                int count = 0;
                                for (String loreLine : Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta().getLore())) {
                                    if (count != 1) {
                                        loreArr.add(loreLine);
                                    } else {
                                        loreArr.add("XP: " + finalAmount);
                                    }
                                    count++;
                                }
                                storageBottleMeta.setLore(loreArr);
                                player.getInventory().getItemInMainHand().setItemMeta(storageBottleMeta);
                                Api.changePlayerExp(player, Integer.parseInt(args[1]));
                                player.sendMessage(ChatColor.GREEN + "You now have " + ChatColor.GOLD + Api.getPlayerExp(player) + ChatColor.GREEN + " experience points.");
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough experience in your storage bottle to withdraw that amount.");
                            }

                        } else {
                            player.sendMessage(ChatColor.RED + "You must give an amount of experience to withdraw.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You must be holding an experience storage bottle.");
                    }
                } else if (args[0].equalsIgnoreCase("xp")) {
                    player.sendMessage(ChatColor.GREEN + "You have " + ChatColor.GOLD + Api.getPlayerExp(player) + ChatColor.GREEN + " experience points");
                } else if (player.getUniqueId().toString().equals(Constants.SERVER_OWNER_MINECRAFT_ID) && args[0].equalsIgnoreCase("addItemClaim")) {
                    ItemStack itemToAdd = player.getInventory().getItemInMainHand();
                    Bukkit.getScheduler().runTaskAsynchronously(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> {
                        try {
                            DonationClaimRewards.addItemToClaim(itemToAdd);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    player.sendMessage(ChatColor.GREEN + "You have added " + ChatColor.GOLD + itemToAdd.getAmount() + " " +itemToAdd.getType() + ChatColor.GREEN + " to the claim list.");
                } else if (player.getUniqueId().toString().equals(Constants.SERVER_OWNER_MINECRAFT_ID) && args[0].equalsIgnoreCase("createClaimFile")) {
                    Bukkit.getScheduler().runTaskAsynchronously(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> {
                        try {
                            DonationClaimRewards.createDonationUserFile();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (args[0].equalsIgnoreCase("creative")) {
                    if (!player.getWorld().getName().contains("creative")){
                            Api.teleportCreativePlayerToLastLocation(player);
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "You can't warp to LMP Creative when you are already there.");
                    }
                } else if (args[0].equalsIgnoreCase("money")) {
                    BankLogoutEvent.setPlayerSessionTime(player);
                    BankLoginEvent.setPlayerLoginTime(player);
                } else if (args[0].equalsIgnoreCase("gift")) {
                    if (args[1] != null){
                        List<String> potentialGiftPlayerNameList = new ArrayList<>();
                        List<String> enabledGiftWorlds = new ArrayList<>();
                        enabledGiftWorlds.add("world");
                        enabledGiftWorlds.add("world_nether");
                        enabledGiftWorlds.add("world_the_end");
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            String playerWorld = onlinePlayer.getWorld().getName();
                            if (enabledGiftWorlds.contains(playerWorld)){
                                potentialGiftPlayerNameList.add(onlinePlayer.getName().toLowerCase());
                            }
                        }
                        if (enabledGiftWorlds.contains(player.getWorld().getName())) {
                            if (!player.getName().equalsIgnoreCase(args[1])) {
                                if (potentialGiftPlayerNameList.contains(args[1].toLowerCase())) {
                                    if (!player.getInventory().getItemInMainHand().getType().isAir()) {
                                        ItemStack itemToGift = player.getInventory().getItemInMainHand();
                                        Player playerToGift = Bukkit.getPlayer(args[1]);
                                        if (playerToGift != null) {
                                            playerToGift.getWorld().dropItem(playerToGift.getLocation(), itemToGift);
                                            player.sendMessage(ChatColor.GREEN + "You gifted an item to " + ChatColor.GOLD + playerToGift.getName());
                                            playerToGift.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " gifted you an item!!!");
                                            player.getInventory().setItemInMainHand(null);
                                            player.updateInventory();
                                        } else {
                                            player.sendMessage(ChatColor.YELLOW + "Player either doesn't exist, not online, or not on LMP Community. Please try again.");
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.YELLOW + "You can't gift air. Please try again by holding an item in your main hand then running the command.");

                                    }
                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "Player either doesn't exist, not online, or not on LMP Community. Please try again.");
                                }
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "You can't gift yourself an item.");

                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "You have to be on LMP Community to gift another an item.");
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("online")) {
                    StringBuilder communityWorldList = new StringBuilder();
                    communityWorldList.append(ChatColor.GRAY + "[" + ChatColor.AQUA + "LMP" + ChatColor.GRAY + "]" + ChatColor.WHITE + " - ");
                    StringBuilder anarchyWorldList = new StringBuilder();
                    anarchyWorldList.append(ChatColor.GRAY + "[" + ChatColor.AQUA + "Anarchy" + ChatColor.GRAY + "]" + ChatColor.WHITE + " - ");
                    StringBuilder hardcoreWorldList = new StringBuilder();
                    hardcoreWorldList.append(ChatColor.GRAY + "[" + ChatColor.AQUA + "Hardcore" + ChatColor.GRAY + "]" + ChatColor.WHITE + " - ");
                    StringBuilder creativeWorldList = new StringBuilder();
                    creativeWorldList.append(ChatColor.GRAY + "[" + ChatColor.AQUA + "Creative" + ChatColor.GRAY + "]" + ChatColor.WHITE + " - ");
                    int communityCount = 0;
                    int hardcoreCount = 0;
                    int anarchyCount = 0;
                    int creativeCount = 0;
                    for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()){
                        if (!Api.isPlayerInvisible(onlinePlayer.getUniqueId().toString())){
                            if (onlinePlayer.getWorld().getName().contains("anarchy")){
                                anarchyWorldList.append(onlinePlayer.getName()).append(ChatColor.GOLD + " | " + ChatColor.RESET);
                                anarchyCount++;
                            } else if (onlinePlayer.getWorld().getName().contains("hardcore")){
                                hardcoreWorldList.append(onlinePlayer.getName()).append(ChatColor.GOLD + " | " + ChatColor.RESET);
                                hardcoreCount++;
                            } else if (onlinePlayer.getWorld().getName().contains("creative")) {
                                creativeWorldList.append(onlinePlayer.getName()).append(ChatColor.GOLD + " | " + ChatColor.RESET);
                                creativeCount++;
                            } else {
                                communityWorldList.append(onlinePlayer.getName()).append(ChatColor.GOLD + " | " + ChatColor.RESET);
                                communityCount++;
                            }
                        }
                    }
                    if (communityCount == 0){
                        communityWorldList.append(ChatColor.RED + "None");
                    }
                    if (anarchyCount == 0){
                        anarchyWorldList.append(ChatColor.RED + "None");
                    }
                    if (hardcoreCount == 0){
                        hardcoreWorldList.append(ChatColor.RED + "None");
                    }
                    if (creativeCount == 0){
                        creativeWorldList.append(ChatColor.RED + "None");
                    }
                    String finalCommunityMessage = String.valueOf(communityWorldList);
                    String finalAnarchyMessage = String.valueOf(anarchyWorldList);
                    String finalHardcoreMessage = String.valueOf(hardcoreWorldList);
                    String finalCreativeMessage = String.valueOf(creativeWorldList);
                    if (finalCommunityMessage.contains("|")){
                        finalCommunityMessage = StringUtils.substring(finalCommunityMessage, 0, finalCommunityMessage.length() - 4);
                    }
                    if (finalAnarchyMessage.contains("|")){
                        finalAnarchyMessage = StringUtils.substring(finalAnarchyMessage, 0, finalAnarchyMessage.length() - 4);
                    }
                    if (finalHardcoreMessage.contains("|")){
                        finalHardcoreMessage = StringUtils.substring(finalHardcoreMessage, 0, finalHardcoreMessage.length() - 4);
                    }
                    if (finalCreativeMessage.contains("|")){
                        finalCreativeMessage = StringUtils.substring(finalCreativeMessage, 0, finalCreativeMessage.length() - 4);

                    }
                    player.sendMessage(finalCommunityMessage);
                    player.sendMessage(finalAnarchyMessage);
                    player.sendMessage(finalHardcoreMessage);
                    player.sendMessage(finalCreativeMessage);
                }
                if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("uncraft")) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    ArrayList<ArrayList<ItemStack>> recipes = new ArrayList<>();
                    for (Recipe recipe : Bukkit.getServer().getRecipesFor(item)) {
                        ArrayList<ItemStack> ingredients = new ArrayList<>();
                        if (recipe instanceof ShapedRecipe) {
                            ShapedRecipe shaped = (ShapedRecipe) recipe;
                            for (ItemStack ingredient : shaped.getIngredientMap().values()) {
                                if (ingredient != null) {
                                    ItemStack fixed = new ItemStack(ingredient.getType(), 1);
                                    ingredients.add(fixed);
                                }
                            }
                        } else if (recipe instanceof ShapelessRecipe) {
                            ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
                            for (ItemStack ingredient : shapeless.getIngredientList()) {
                                if (ingredient != null) {
                                    ItemStack fixed = new ItemStack(ingredient.getType(), 1);
                                    ingredients.add(fixed);
                                }
                            }
                        } else if (recipe instanceof FurnaceRecipe) {
                            FurnaceRecipe furnace = (FurnaceRecipe) recipe;
                            ItemStack fixed = new ItemStack(furnace.getInput().getType(), 1);
                            ingredients.add(fixed);
                        }
                        recipes.add(ingredients);
                    }
                    int count = 0;
                    for (ArrayList<ItemStack> recipeIngredients : recipes) {
                        ArrayList<Material> ingredientsInRecipe = new ArrayList<>();
                        for (ItemStack ingredient : recipeIngredients) {
                            Main.log.info("Recipe #" + count + " | " + ingredient.getType() + " | " + Collections.frequency(recipeIngredients, ingredient));
                        }
                        count++;
                    }
                } else if (args[0].equalsIgnoreCase("claim")) {
                    player.sendMessage(ChatColor.GREEN + "Checking if you have unclaimed items. Please wait one moment...");
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
                        public void run() {
                            try {
                                DonationClaimRewards.claimItems(player);
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, 100);

                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            player.sendMessage(ChatColor.RED + "An error has occurred. Please review your command and try again.");
        } catch (NullPointerException | NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "An error has occurred. Please try your command again or let Latch and other players know you are having an issue.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

}
