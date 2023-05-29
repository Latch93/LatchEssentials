package lmp.commands;

import lmp.*;
import lmp.api.Api;
import lmp.constants.Constants;
import lmp.constants.YmlFileNames;
import lmp.listeners.playerJoinEvents.BankLoginEvent;
import lmp.listeners.playerQuitEvents.BankLogoutEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
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
                    sender.sendMessage(ChatColor.GREEN + "Deaths: " + ChatColor.GOLD + player.getStatistic(Statistic.DEATHS));
                    sender.sendMessage(ChatColor.GREEN + "Mobs Killed: " + ChatColor.GOLD + player.getStatistic(Statistic.MOB_KILLS));
                    sender.sendMessage(ChatColor.GREEN + "Times Jumped: " + ChatColor.GOLD + player.getStatistic(Statistic.JUMP));
                    sender.sendMessage(ChatColor.GREEN + "Damage Dealt: " + ChatColor.GOLD + player.getStatistic(Statistic.DAMAGE_DEALT));
                    sender.sendMessage(ChatColor.GREEN + "Damage Taken: " + ChatColor.GOLD + player.getStatistic(Statistic.DAMAGE_TAKEN));
                    sender.sendMessage(ChatColor.GREEN + "Animals Bred: " + ChatColor.GOLD + player.getStatistic(Statistic.ANIMALS_BRED));
                    sender.sendMessage(ChatColor.GREEN + "Traded with Villager: " + ChatColor.GOLD + player.getStatistic(Statistic.TRADED_WITH_VILLAGER));
                    sender.sendMessage(ChatColor.GREEN + "Slept in Bed: " + ChatColor.GOLD + player.getStatistic(Statistic.SLEEP_IN_BED));
                    sender.sendMessage(ChatColor.GREEN + "Damage Blocked by Shield: " + ChatColor.GOLD + player.getStatistic(Statistic.DAMAGE_BLOCKED_BY_SHIELD));
                    sender.sendMessage(ChatColor.GREEN + "Raids Won: " + ChatColor.GOLD + player.getStatistic(Statistic.RAID_WIN));

                } else if (args[0].equalsIgnoreCase("rtp")) {
                    RandomTeleport.randomTp(player);
                } else if (args[0].equalsIgnoreCase("lotto")) {
                    try {
                        Lottery.lottoCommands(player, args[1], sender);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.RED + "Invalid command. Please use this command as follows -> " + ChatColor.AQUA + "[/dt lotto check] [/dt lotto buyin] [/dt lotto total]");
                    }
                } else if (args[0].equalsIgnoreCase("withdraw")) {
                    List<String> enabledWithdrawWorlds = new ArrayList<>();
                    enabledWithdrawWorlds.add("world");
                    enabledWithdrawWorlds.add("world_nether");
                    enabledWithdrawWorlds.add("world_the_end");
                    if (enabledWithdrawWorlds.contains(player.getWorld().getName())) {
                        double amount = Double.parseDouble(args[1]);
                        if (amount > 249) {
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
                            player.sendMessage(ChatColor.RED + "Can't withdraw less than $250.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only use this command in " + ChatColor.GOLD + "LMP Community" + ChatColor.RED + ".");
                    }

                } else if (args[0].equalsIgnoreCase("deposit")) {
                    List<String> enabledDepositWorlds = new ArrayList<>();
                    enabledDepositWorlds.add("world");
                    enabledDepositWorlds.add("world_nether");
                    enabledDepositWorlds.add("world_the_end");
                    if (enabledDepositWorlds.contains(player.getWorld().getName())) {
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
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only use this command in " + ChatColor.GOLD + "LMP Community" + ChatColor.RED + ".");
                    }
                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("switch") && args[1] != null) {
                    FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
                    Inventory playerInventory = player.getInventory();
                    Location creativeChestLocation = new Location(Bukkit.getWorld("season6"), configCfg.getDouble("creativeChest.x"), configCfg.getDouble("creativeChest.y"), configCfg.getDouble("creativeChest.z"));
                    Block creativeChestBlock = creativeChestLocation.getBlock();
                    Chest creativeChest = (Chest) creativeChestBlock.getState();
                    Location survivalChestLocation = new Location(Bukkit.getWorld("season6"), configCfg.getDouble("survivalChest.x"), configCfg.getDouble("survivalChest.y"), configCfg.getDouble("survivalChest.z"));
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
                }
//                else if (args[0].equalsIgnoreCase("clearBoss") && args[1] != null) {
//                    FileConfiguration bossCfg = Api.getFileConfiguration(YmlFileNames.YML_BOSS_FILE_NAME);
//                    bossCfg.set("bossEnabled", false);
//                    player.sendMessage(ChatColor.GREEN + "Boss has been terminated.");
//                    try {
//                        bossCfg.save(Api.getConfigFile(YmlFileNames.YML_BOSS_FILE_NAME));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("killBoss")) {
//                    FileConfiguration bossCfg = Api.getFileConfiguration(YmlFileNames.YML_BOSS_FILE_NAME);
//                    try {
//                        Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(bossCfg.getString("bossUUID"))))).remove();
//                        bossCfg.set("bossEnabled", false);
//                        player.sendMessage(ChatColor.GREEN + "Boss has been terminated.");
//                        try {
//                            bossCfg.save(Api.getConfigFile(YmlFileNames.YML_BOSS_FILE_NAME));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } catch (NullPointerException ignored) {
//
//                    }
//                }
                else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("removeHomes")) {
                    for (File file : Objects.requireNonNull(new File("plugins/Essentials/userdata").listFiles())) {
                        FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
                        if (conf.isSet("homes")) {
                            for (String homeName : Objects.requireNonNull(conf.getConfigurationSection("homes")).getKeys(false)){
                                ArrayList<String> homeWorldsToDelete = new ArrayList<>();
                                homeWorldsToDelete.add("world");
                                homeWorldsToDelete.add("world_nether");
                                homeWorldsToDelete.add("world_the_end");
                                Main.log.info(conf.getString("homes." + homeName + ".world-name"));
                                if (homeWorldsToDelete.contains(conf.getString("homes." + homeName + ".world-name"))){
                                    conf.set("homes." + homeName, null);
                                }
                            }
                        }
                        if (conf.isSet("money")){
                            double balance = Double.parseDouble(Objects.requireNonNull(conf.getString("money")));
                            if (balance > 500000){
                                conf.set("money", "500000.00");
                            }
                        }
                        if (conf.isSet("logoutlocation") && conf.isSet("logoutlocation.world") && Objects.requireNonNull(conf.getString("logoutlocation.world")).equalsIgnoreCase("cee8accb-f717-4b88-be30-a688b2a195ea")) {
                            if (conf.isSet("logoutlocation")) {
                                conf.set("logoutlocation.world", "0ccd6fce-b9c3-44ff-9225-9c2f63c3ceb6");
                                conf.set("logoutlocation.x", -153.4098660271217);
                                conf.set("logoutlocation.y", 118.0);
                                conf.set("logoutlocation.z", 86.11922280135366);
                                conf.set("logoutlocation.yaw", 89.55033874511719);
                                conf.set("logoutlocation.pitch", -5.250002384185791);
                                conf.set("logoutlocation.world-name", "world");
                            }
                        }
                        if (conf.isSet("lastlocation") && conf.isSet("logoutlocation.world") && Objects.requireNonNull(conf.getString("lastlocation.world")).equalsIgnoreCase("cee8accb-f717-4b88-be30-a688b2a195ea")) {
                            if (conf.isSet("lastlocation")) {
                                conf.set("lastlocation.world", "0ccd6fce-b9c3-44ff-9225-9c2f63c3ceb6");
                                conf.set("lastlocation.x", -153.4098660271217);
                                conf.set("lastlocation.y", 118.0);
                                conf.set("lastlocation.z", 86.11922280135366);
                                conf.set("lastlocation.yaw", 89.55033874511719);
                                conf.set("lastlocation.pitch", -5.250002384185791);
                                conf.set("lastlocation.world-name", "world");
                            }
                        }
                        try {
                            conf.save(file);
                            Main.log.info("Removed Homes for: " + conf.getString("last-account-name"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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
                            DonationClaimRewards.createNewPlayerToDonationFile(player);
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
                }
//                else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("spectate")) {
//                    spectateInsideRandomPlayer(player);
//                } else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("resetAllBalances")) {
//                    resetPlayerBalances(Api.getAllMinecraftIDOfLinkedPlayers());
//                } else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("bmstart")) {
//                    LMPTimer.startBloodmoon();
//                } else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("easybm")) {
//                    LMPTimer.setEasyBloodMoon();
//                } else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("mediumbm")) {
//                    LMPTimer.setMediumBloodMoon();
//                } else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_NAME) && args[0].equalsIgnoreCase("hardbm")) {
//                    LMPTimer.setHardBloodMoon();
//                }
                else if (args[0].equalsIgnoreCase("xpDeposit")) {
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
                } else if (args[0].equalsIgnoreCase("hub")) {
                    FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
                    Location hubLocation = new Location(Bukkit.getWorld(Objects.requireNonNull(configCfg.getString("hub.world-name"))), configCfg.getDouble("hub.x"), configCfg.getDouble("hub.y"), configCfg.getDouble("hub.z"));
                    player.teleport(hubLocation);
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
                            DonationClaimRewards.addItemToClaimToAll(itemToAdd);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    player.sendMessage(ChatColor.GREEN + "You have added " + ChatColor.GOLD + itemToAdd.getAmount() + " " +itemToAdd.getType() + ChatColor.GREEN + " to the claim list.");
                } else if (player.getUniqueId().toString().equals(Constants.SERVER_OWNER_MINECRAFT_ID) && args[0].equalsIgnoreCase("createClaimFiles")) {
                    Bukkit.getScheduler().runTaskAsynchronously(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> {
                        try {
                            DonationClaimRewards.createDonationClaimFiles();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                } else if (player.getUniqueId().toString().equals(Constants.SERVER_OWNER_MINECRAFT_ID) && args[0].equalsIgnoreCase("addItemToPlayerClaim") ) {
                    try {
                        ItemStack is = player.getInventory().getItemInMainHand();
                        String minecraftId = Api.getMinecraftIdFromMinecraftName(args[1]);
                        DonationClaimRewards.addItemToClaimToPlayer(minecraftId, is);
                        player.sendMessage(ChatColor.GREEN + "Added " + ChatColor.GOLD + is.getAmount() + " " + is.getType() + ChatColor.GREEN + " to " + ChatColor.GOLD + player.getName() + "'s " + ChatColor.GREEN + "player claim file.");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.RED + "Player Name not given as parameter.");
                    }
                 }
                else if (args[0].equalsIgnoreCase("creative")) {
                    List<String> enabledCreativeWarpWorlds = new ArrayList<>();
                    enabledCreativeWarpWorlds.add("world");
                    enabledCreativeWarpWorlds.add("world_nether");
                    enabledCreativeWarpWorlds.add("world_the_end");
                    if (enabledCreativeWarpWorlds.contains(player.getWorld().getName())) {
                        if (!player.getWorld().getName().contains("creative")) {
                            Api.teleportCreativePlayerToLastLocation(player);
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "You can't warp to LMP Creative when you are already there.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only use this command in " + ChatColor.GOLD + "LMP Community" + ChatColor.RED + ".");
                    }
                } else if (args[0].equalsIgnoreCase("giveaway")){
                    FileConfiguration playerLogCfg = Api.getFileConfiguration("playerLog");
                    ArrayList<String> linkedInDiscordPlayers = Api.getMinecraftIDOfLinkedPlayersInDiscord();
                    ArrayList<String> minecraftIDList = new ArrayList<>(playerLogCfg.getConfigurationSection("players").getKeys(false));
                    for (String id : minecraftIDList){
                        if (linkedInDiscordPlayers.contains(id)){
                            Main.log.info(Api.getDiscordNameFromMCid(id));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("money")) {
                    BankLogoutEvent.setPlayerSessionTime(player);
                    BankLoginEvent.setPlayerLoginTime(player);
                } else if (args[0].equalsIgnoreCase("cr")) {
                    Inventory customRecipeInventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Custom Recipes");
                    ItemStack grayPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
                    customRecipeInventory.setItem(0, grayPane);
                    customRecipeInventory.setItem(1, grayPane);
                    customRecipeInventory.setItem(2, grayPane);
                    customRecipeInventory.setItem(6, grayPane);
                    customRecipeInventory.setItem(7, grayPane);
                    customRecipeInventory.setItem(8, grayPane);
                    customRecipeInventory.setItem(9, grayPane);
                    customRecipeInventory.setItem(10, grayPane);
                    customRecipeInventory.setItem(11, grayPane);
                    customRecipeInventory.setItem(15, grayPane);
                    customRecipeInventory.setItem(17, grayPane);
                    customRecipeInventory.setItem(18, grayPane);
                    customRecipeInventory.setItem(19, grayPane);
                    customRecipeInventory.setItem(20, grayPane);
                    customRecipeInventory.setItem(24, grayPane);
                    customRecipeInventory.setItem(25, grayPane);
                    customRecipeInventory.setItem(26, grayPane);
                    player.openInventory(customRecipeInventory);
                } else if (args[0].equalsIgnoreCase("combine")){
                    ItemStack chestplate = player.getInventory().getItemInOffHand();
                    if (chestplate.getType().equals(Material.NETHERITE_CHESTPLATE)){
                        ItemStack elytra = player.getInventory().getItemInMainHand();
                        if (elytra.getType().equals(Material.ELYTRA)){
                            int playerLevel = player.getLevel();
                            if (playerLevel >= 50){
                                Map<Enchantment, Integer> chestplateEnchantments = chestplate.getEnchantments();
                                Map<Enchantment, Integer> elytraEnchantments = elytra.getEnchantments();
                                Map<Enchantment, Integer> finalEnchantments = new HashMap<>();
                                for (Map.Entry<Enchantment, Integer> chestplateEnchant : chestplateEnchantments.entrySet()) {
                                    for (Map.Entry<Enchantment, Integer> elytraEnchant : elytraEnchantments.entrySet()) {
                                        if (chestplateEnchant.getKey().equals(elytraEnchant.getKey())) {
                                            if (chestplateEnchant.getValue() > elytraEnchant.getValue()) {
                                                finalEnchantments.put(chestplateEnchant.getKey(), chestplateEnchant.getValue());
                                            } else {
                                                finalEnchantments.put(elytraEnchant.getKey(), elytraEnchant.getValue());
                                            }
                                        }
                                        if (!chestplateEnchantments.containsKey(elytraEnchant.getKey())) {
                                            finalEnchantments.put(elytraEnchant.getKey(), elytraEnchant.getValue());
                                        }
                                    }
                                    if (!elytraEnchantments.containsKey(chestplateEnchant.getKey())) {
                                        finalEnchantments.put(chestplateEnchant.getKey(), chestplateEnchant.getValue());
                                    }
                                }

                                ItemStack upgradedElytra = new ItemStack(Material.ELYTRA, 1);
                                upgradedElytra.addUnsafeEnchantments(finalEnchantments);
                                ItemMeta elytraIM = null;
                                if (upgradedElytra.getItemMeta() != null){
                                    elytraIM = upgradedElytra.getItemMeta();
                                    elytraIM.setAttributeModifiers(Objects.requireNonNull(chestplate.getItemMeta()).getAttributeModifiers());
                                    elytraIM.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                                    elytraIM.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                                    elytraIM.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", .1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                                    upgradedElytra.setItemMeta(elytraIM);
                                    player.getInventory().setItemInOffHand(new ItemStack(Material.AIR, 0));
                                    player.getInventory().setItemInMainHand(upgradedElytra);
                                    player.setLevel(ExperienceManager.getPlayerLevel(player, 3965));
                                    player.sendMessage(ChatColor.GREEN + "Your chestplate and elytra have been combined!!!");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You must have at least 50 experience levels to combine these items.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Item in offhand must be an elytra.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Item in offhand must be a netherite chestplate.");
                    }

                } else if (args[0].equalsIgnoreCase("afk")){
                    List<String> enabledAFKWorlds = new ArrayList<>();
                    enabledAFKWorlds.add("world");
                    enabledAFKWorlds.add("world_nether");
                    enabledAFKWorlds.add("world_the_end");
                    enabledAFKWorlds.add("classic");
                    enabledAFKWorlds.add("classic_nether");
                    enabledAFKWorlds.add("classic_the_end");
                    if (enabledAFKWorlds.contains(player.getWorld().getName())) {
                        BankLogoutEvent.setPlayerSessionTime(player);
                        BankLoginEvent.setPlayerLoginTime(player);
                        Api.givePlayerLuckPermPermission(player, "essentials.afk.kickexempt");
                        Api.givePlayerLuckPermPermission(player, "essentials.sleepingignored");
                        BankLogoutEvent.setPlayerSessionTime(player);
                        Api.setBankSessionToAFK(true, player);
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only use this command in " + ChatColor.GOLD + "LMP Community " + ChatColor.RED + "or " + ChatColor.GOLD + "LMP Classic " + ChatColor.RED + "servers." );
                    }
                } else if (args[0].equalsIgnoreCase("fly")){
                    List<String> enabledFlyWorlds = new ArrayList<>();
                    enabledFlyWorlds.add("world");
                    enabledFlyWorlds.add("world_nether");
                    enabledFlyWorlds.add("world_the_end");
                    if (enabledFlyWorlds.contains(player.getWorld().getName())) {
                        double balance = Api.getEconomy().getBalance(Bukkit.getOfflinePlayer(player.getUniqueId()));
                        double flyCost = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getDouble("flyCommandCost");
                        int commandLength = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getInt("flyCommandLength");
                        if (balance >= flyCost){
                            String playerUUID = player.getUniqueId().toString();
                            Api.takePlayerMoney(player.getUniqueId().toString(), flyCost);
                            Api.givePlayerLuckPermPermission(player, "essentials.fly");
                            DateTime dateOne = new DateTime();
                            DecimalFormat df = new DecimalFormat("0.00");
                            FileConfiguration flyListCfg = Api.getFileConfiguration(YmlFileNames.YML_FLY_LIST_FILE_NAME);
                            flyListCfg.set(lmp.Constants.YML_PLAYERS + playerUUID + ".playerName", player.getName());
                            flyListCfg.set(lmp.Constants.YML_PLAYERS + playerUUID + ".fly.enabled", true);
                            flyListCfg.set(lmp.Constants.YML_PLAYERS + playerUUID + ".fly.commandTime", dateOne.getMillis());
                            player.setAllowFlight(true);
                            player.setFlying(true);
                            flyListCfg.save(Api.getConfigFile(YmlFileNames.YML_FLY_LIST_FILE_NAME));
                            int totalFlyTime = (commandLength / 1000) / 60;
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + lmp.Constants.YML_YOUR_NEW_BALANCE_IS + ChatColor.GOLD + "$" + df.format(Api.getEconomy().getBalance(player))));
                            player.sendMessage(ChatColor.GREEN + "You been given access to the " + ChatColor.GOLD + "/fly " + ChatColor.GREEN + "command for " + ChatColor.GOLD + totalFlyTime + ChatColor.GREEN + " minutes.");
                        } else {
                            player.sendMessage(ChatColor.RED + "You need at least " + ChatColor.GOLD + "$" + flyCost + ChatColor.RED + " to use this command.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only use this command in " + ChatColor.GOLD + "LMP Community" + ChatColor.RED + ".");
                    }
                } else if (args[0].equalsIgnoreCase("classic")) {
                    List<String> enabledClassicWarpWorlds = new ArrayList<>();
                    enabledClassicWarpWorlds.add("world");
                    enabledClassicWarpWorlds.add("world_nether");
                    enabledClassicWarpWorlds.add("world_the_end");
                    if (enabledClassicWarpWorlds.contains(player.getWorld().getName())) {
                        FileConfiguration classicCfg = Api.getFileConfiguration(YmlFileNames.YML_CLASSIC_FILE_NAME);
                        String playerUUID = player.getUniqueId().toString();
                        if (!player.getWorld().getName().contains("classic")) {
                            if (!classicCfg.isSet(playerUUID)) {
                                Location classicSpawnLocation = new Location(Bukkit.getWorld("classic"), 0, 92, 0, (float) -3.000, (float) 92.10);
                                classicCfg.set(playerUUID + ".uuid", playerUUID);
                                classicCfg.set(playerUUID + ".name", player.getName());
                                classicCfg.set(playerUUID + ".lastLocation", classicSpawnLocation);
                                player.teleport(classicSpawnLocation);
                                classicCfg.save(Api.getConfigFile(YmlFileNames.YML_CLASSIC_FILE_NAME));
                            } else {
                                Api.teleportClassicPlayerToLastLocation(player);
                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "You can't warp to LMP Classic when you are already there.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only warp to " + ChatColor.GOLD + "LMP Classic " + ChatColor.RED + "from the " + ChatColor.GOLD + "LMP Community " + ChatColor.RED + "server.");
                    }
                } else if (args[0].equalsIgnoreCase("OneBlock")) {
                    List<String> enabledOneBlockWarpWorlds = new ArrayList<>();
                    enabledOneBlockWarpWorlds.add("world");
                    enabledOneBlockWarpWorlds.add("world_nether");
                    enabledOneBlockWarpWorlds.add("world_the_end");
                    if (enabledOneBlockWarpWorlds.contains(player.getWorld().getName())) {
                        FileConfiguration classicCfg = Api.getFileConfiguration(YmlFileNames.YML_ONEBLOCK_FILE_NAME);
                        String playerUUID = player.getUniqueId().toString();
                        if (!player.getWorld().getName().contains("OneBlock")) {
                            if (!classicCfg.isSet(playerUUID)) {
                                Location oneBlockSpawnLocation = new Location(Bukkit.getWorld("OneBlock"), 11.5, 64, -1.5, (float) -3.000, (float) 92.10);
                                classicCfg.set(playerUUID + ".uuid", playerUUID);
                                classicCfg.set(playerUUID + ".name", player.getName());
                                classicCfg.set(playerUUID + ".lastLocation", oneBlockSpawnLocation);
                                player.teleport(oneBlockSpawnLocation);
                                classicCfg.save(Api.getConfigFile(YmlFileNames.YML_ONEBLOCK_FILE_NAME));
                            } else {
                                Api.teleportOneBlockPlayerToLastLocation(player);
                            }
                            player.performCommand("/ob progress_bar true");
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "You can't warp to LMP OneBlock when you are already there.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only warp to " + ChatColor.GOLD + "LMP OneBlock " + ChatColor.RED + "from the " + ChatColor.GOLD + "LMP Community " + ChatColor.RED + "server.");
                    }
                } else if (args[0].equalsIgnoreCase("SkyBlock")) {
                    List<String> enabledOneBlockWarpWorlds = new ArrayList<>();
                    enabledOneBlockWarpWorlds.add("world");
                    enabledOneBlockWarpWorlds.add("world_nether");
                    enabledOneBlockWarpWorlds.add("world_the_end");
                    if (enabledOneBlockWarpWorlds.contains(player.getWorld().getName())) {
                        FileConfiguration skyblockCfg = Api.getFileConfiguration(YmlFileNames.YML_SKYBLOCK_FILE_NAME);
                        String playerUUID = player.getUniqueId().toString();
                        if (!player.getWorld().getName().contains("SkyBlock")) {
                            if (!skyblockCfg.isSet(playerUUID)) {
                                Location skyBlockSpawnLocation = new Location(Bukkit.getWorld("IridiumSkyblock"), 107, 93, -1.5, (float) -3.000, (float) 92.10);
                                skyblockCfg.set(playerUUID + ".uuid", playerUUID);
                                skyblockCfg.set(playerUUID + ".name", player.getName());
                                skyblockCfg.set(playerUUID + ".lastLocation", skyBlockSpawnLocation);
                                player.teleport(skyBlockSpawnLocation);
                                skyblockCfg.save(Api.getConfigFile(YmlFileNames.YML_SKYBLOCK_FILE_NAME));
                            } else {
                                Api.teleportSkyBlockPlayerToLastLocation(player);
                            }
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "You can't warp to LMP SkyBlock when you are already there.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only warp to " + ChatColor.GOLD + "LMP SkyBlock " + ChatColor.RED + "from the " + ChatColor.GOLD + "LMP Community " + ChatColor.RED + "server.");
                    }
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
                                            playerToGift.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " gifted you " + ChatColor.GOLD + itemToGift.getAmount() + " " + itemToGift.getType() + ChatColor.GREEN + " !!!");
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
                } else if (args[0].equalsIgnoreCase("online")) {
                    StringBuilder communityWorldList = new StringBuilder();
                    communityWorldList.append(ChatColor.GRAY + "[" + ChatColor.AQUA + "Community" + ChatColor.GRAY + "]" + ChatColor.WHITE + " - ");
                    StringBuilder classicWorldList = new StringBuilder();
                    classicWorldList.append(ChatColor.GRAY + "[" + ChatColor.AQUA + "Classic" + ChatColor.GRAY + "]" + ChatColor.WHITE + " - ");
                    StringBuilder anarchyWorldList = new StringBuilder();
                    anarchyWorldList.append(ChatColor.GRAY + "[" + ChatColor.AQUA + "Anarchy" + ChatColor.GRAY + "]" + ChatColor.WHITE + " - ");
                    StringBuilder hardcoreWorldList = new StringBuilder();
                    hardcoreWorldList.append(ChatColor.GRAY + "[" + ChatColor.AQUA + "Hardcore" + ChatColor.GRAY + "]" + ChatColor.WHITE + " - ");
                    StringBuilder creativeWorldList = new StringBuilder();
                    creativeWorldList.append(ChatColor.GRAY + "[" + ChatColor.AQUA + "Creative" + ChatColor.GRAY + "]" + ChatColor.WHITE + " - ");
                    StringBuilder oneBlockWorldList = new StringBuilder();
                    oneBlockWorldList.append(ChatColor.GRAY + "[" + ChatColor.AQUA + "OneBlock" + ChatColor.GRAY + "]" + ChatColor.WHITE + " - ");
                    StringBuilder skyBlockWorldList = new StringBuilder();
                    skyBlockWorldList.append(ChatColor.GRAY + "[" + ChatColor.AQUA + "SkyBlock" + ChatColor.GRAY + "]" + ChatColor.WHITE + " - ");
                    int communityCount = 0;
                    int hardcoreCount = 0;
                    int anarchyCount = 0;
                    int creativeCount = 0;
                    int classicCount = 0;
                    int oneBlockCount = 0;
                    int skyBlockCount = 0;
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
                            } else if (onlinePlayer.getWorld().getName().contains("classic")) {
                                classicWorldList.append(onlinePlayer.getName()).append(ChatColor.GOLD + " | " + ChatColor.RESET);
                                classicCount++;
                            } else if (onlinePlayer.getWorld().getName().contains("OneBlock")) {
                                oneBlockWorldList.append(onlinePlayer.getName()).append(ChatColor.GOLD + " | " + ChatColor.RESET);
                                oneBlockCount++;
                            } else if (onlinePlayer.getWorld().getName().contains("Skyblock")) {
                                skyBlockWorldList.append(onlinePlayer.getName()).append(ChatColor.GOLD + " | " + ChatColor.RESET);
                                skyBlockCount++;
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
                    if (classicCount == 0){
                        classicWorldList.append(ChatColor.RED + "None");
                    }
                    if (oneBlockCount == 0){
                        oneBlockWorldList.append(ChatColor.RED + "None");
                    }
                    if (skyBlockCount == 0){
                        skyBlockWorldList.append(ChatColor.RED + "None");
                    }
                    String finalAnarchyMessage = String.valueOf(anarchyWorldList);
                    String finalClassicMessage = String.valueOf(classicWorldList);
                    String finalCommunityMessage = String.valueOf(communityWorldList);
                    String finalCreativeMessage = String.valueOf(creativeWorldList);
                    String finalHardcoreMessage = String.valueOf(hardcoreWorldList);
                    String finalOneBlockMessage = String.valueOf(oneBlockWorldList);
                    String finalSkyBlockMessage = String.valueOf(skyBlockWorldList);
                    if (finalCommunityMessage.contains("|")){
                        finalCommunityMessage = StringUtils.substring(finalCommunityMessage, 0, finalCommunityMessage.length() - 4);
                    }
                    if (finalClassicMessage.contains("|")){
                        finalClassicMessage = StringUtils.substring(finalClassicMessage, 0, finalClassicMessage.length() - 4);
                    }
                    if (finalAnarchyMessage.contains("|")){
                        finalAnarchyMessage = StringUtils.substring(finalAnarchyMessage, 0, finalAnarchyMessage.length() - 4);
                    }
                    if (finalHardcoreMessage.contains("|")){
                        finalHardcoreMessage = StringUtils.substring(finalHardcoreMessage, 0, finalHardcoreMessage.length() - 4);
                    }
                    if (finalOneBlockMessage.contains("|")){
                        finalOneBlockMessage = StringUtils.substring(finalOneBlockMessage, 0, finalOneBlockMessage.length() - 4);
                    }
                    if (finalSkyBlockMessage.contains("|")){
                        finalSkyBlockMessage = StringUtils.substring(finalSkyBlockMessage, 0, finalSkyBlockMessage.length() - 4);
                    }
                    if (finalCreativeMessage.contains("|")){
                        finalCreativeMessage = StringUtils.substring(finalCreativeMessage, 0, finalCreativeMessage.length() - 4);
                    }
                    player.sendMessage(finalAnarchyMessage);
                    player.sendMessage(finalClassicMessage);
                    player.sendMessage(finalCommunityMessage);
                    player.sendMessage(finalCreativeMessage);
                    player.sendMessage(finalHardcoreMessage);
                    player.sendMessage(finalOneBlockMessage);
                    player.sendMessage(finalSkyBlockMessage);
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
                    List<String> enabledClaimWorlds = new ArrayList<>();
                    enabledClaimWorlds.add("world");
                    enabledClaimWorlds.add("world_nether");
                    enabledClaimWorlds.add("world_the_end");
                    if (enabledClaimWorlds.contains(player.getWorld().getName())){
                        player.sendMessage(ChatColor.GREEN + "Checking if you have unclaimed items. Please wait one moment...");
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                            try {
                                DonationClaimRewards.claimItems(player);
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }, 100);
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only claim items in " + ChatColor.GOLD + "LMP Community" + ChatColor.RED + ".");
                    }
                } else if (args[0].equalsIgnoreCase("anarchy")){
                    File anarchyFile = Api.getConfigFile(YmlFileNames.YML_ANARCHY_FILE_NAME);
                    FileConfiguration anarchyCfg = Api.getFileConfiguration(YmlFileNames.YML_ANARCHY_FILE_NAME);
                    String playerUUID = player.getUniqueId().toString();
                    if (!anarchyCfg.isSet(playerUUID)){
                        Location anarchySpawnLocation = new Location(Bukkit.getWorld("anarchy"), 13.5, 63, -25.5, (float) -3.000, (float) 92.10);
                        anarchyCfg.set(playerUUID + ".uuid", playerUUID);
                        anarchyCfg.set(playerUUID + ".name", player.getName());
                        anarchyCfg.set(playerUUID + ".lastLocation", anarchySpawnLocation);
                        player.teleport(anarchySpawnLocation);
                        anarchyCfg.save(anarchyFile);
                    }
                    else {
                        Api.teleportAnarchyPlayerToLastLocation(player);
                    }
                }
                else if (args[0].equalsIgnoreCase("saveSeason7Home")) {
                    FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
                    if (player.getWorld().getUID().equals(UUID.fromString("cee8accb-f717-4b88-be30-a688b2a195ea"))){
                        whitelistCfg.set("players." + player.getUniqueId().toString() + ".season7warp", player.getLocation());
                        player.sendMessage(ChatColor.GREEN + "Your Season 7 home warp has been saved.");
                    } else {
                        player.sendMessage(ChatColor.RED + "You must be in the Season 7 overworld to set your Season 6 home warp.");
                    }
                    whitelistCfg.save(Api.getConfigFile(YmlFileNames.YML_WHITELIST_FILE_NAME));
                }
                else if (args[0].equalsIgnoreCase("season6warp")) {
                    List<String> enabledSeason6WarpWorlds = new ArrayList<>();
                    enabledSeason6WarpWorlds.add("world");
                    enabledSeason6WarpWorlds.add("world_nether");
                    enabledSeason6WarpWorlds.add("world_the_end");
                    if (enabledSeason6WarpWorlds.contains(player.getWorld().getName())) {
                        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
                        if (whitelistCfg.isSet("players." + player.getUniqueId().toString() + ".season6warp")) {
                            if (player.getWorld().getUID().equals(UUID.fromString("e784f96f-9862-439b-971c-4157f532ed4a"))) {
                                Location playerSeason6Warp = whitelistCfg.getLocation("players." + player.getUniqueId().toString() + ".season6warp");
                                if (playerSeason6Warp != null) {
                                    player.teleport(playerSeason6Warp);
                                    player.sendMessage(ChatColor.GREEN + "You were warped to your Season 6 home warp.");
                                } else {
                                    player.sendMessage(ChatColor.RED + "An error has occurred. Please message Latch the issue and he will help as soon as he can..");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You must be in the Season 7 overworld to warp to your Season 6 home warp");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have a Season 6 home warp set.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only warp to your Season 6 home from " + ChatColor.GOLD + "LMP Community" + ChatColor.RED + ".");
                    }
                }
                else if (args[0].equalsIgnoreCase("season7warp")) {
                    List<String> enabledSeason7WarpWorlds = new ArrayList<>();
                    enabledSeason7WarpWorlds.add("world");
                    enabledSeason7WarpWorlds.add("world_nether");
                    enabledSeason7WarpWorlds.add("world_the_end");
                    if (enabledSeason7WarpWorlds.contains(player.getWorld().getName())) {
                        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
                        if (whitelistCfg.isSet("players." + player.getUniqueId().toString() + ".season7warp")) {
                            if (player.getWorld().getUID().equals(UUID.fromString("e784f96f-9862-439b-971c-4157f532ed4a"))) {
                                Location playerSeason7Warp = whitelistCfg.getLocation("players." + player.getUniqueId().toString() + ".season7warp");
                                if (playerSeason7Warp != null) {
                                    player.teleport(playerSeason7Warp);
                                    player.sendMessage(ChatColor.GREEN + "You were warped to your Season 7 home warp.");
                                } else {
                                    player.sendMessage(ChatColor.RED + "An error has occurred. Please message Latch the issue and he will help as soon as he can..");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You must be in the Season 8 overworld to warp to your Season 7 home warp");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have a Season 7 home warp set.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You can only warp to your Season 7 home from " + ChatColor.GOLD + "LMP Community" + ChatColor.RED + ".");
                    }
                }
                else if (args[0].equalsIgnoreCase("shop")) {
                    player.performCommand("bossshop");
                }
//                else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("camel")){
//                    player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.CAMEL_SPAWN_EGG, 1));
//                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("sign")){
//                    player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.BAMBOO_HANGING_SIGN, 1));
//                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("raft")){
//                    player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.BAMBOO_RAFT, 1));
//                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("deleteLink")){
//                    Api.deleteUnlinkedEssentialAccountsFiles();
//                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("bpCheck")){
//                    Api.removeIllegalItemsFromBackpacks();
//                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("transfer")){
//                    Api.transferBackpackContents();
//                }
                else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("att")){
                    ItemStack is = player.getInventory().getItemInMainHand();
                    ItemMeta im = is.getItemMeta();
                    assert im != null;
                    im.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                    im.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                    im.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", .1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                    is.setItemMeta(im);
                    player.updateInventory();
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            player.sendMessage(ChatColor.RED + "An error has occurred. Please review your command and try again.");
        }
//        catch (NullPointerException | NumberFormatException e) {
//
//            player.sendMessage(ChatColor.RED + "An error has occurred. Please try your command again or let Latch and other players know you are having an issue.");
//            Main.log.warning(e.getCause().toString());
//        }
        catch (IOException e) {
            Main.log.warning(e.getCause().toString());
        } catch (ExecutionException | InterruptedException e) {
            Main.log.warning(e.getCause().toString());
            throw new RuntimeException(e);
        }

        return false;
    }

}
