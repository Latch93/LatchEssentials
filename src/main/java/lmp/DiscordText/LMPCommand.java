package lmp.DiscordText;

import lmp.*;

import io.ipgeolocation.api.Geolocation;
import io.ipgeolocation.api.GeolocationParams;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.md_5.bungee.protocol.packet.Chat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LMPCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        try {
            if (args[0] != null){
                if (args[0].equalsIgnoreCase("stat")) {
                    sender.sendMessage(ChatColor.GREEN + "Total deaths: " + ChatColor.GOLD + player.getStatistic(Statistic.DEATHS));
                    sender.sendMessage(ChatColor.GREEN + "Total mobs killed: " + ChatColor.GOLD + player.getStatistic(Statistic.MOB_KILLS));
                    sender.sendMessage(ChatColor.GREEN + "Number of times jumped: " + ChatColor.GOLD + player.getStatistic(Statistic.JUMP));
                } else if (args[0].equalsIgnoreCase("rtp")){
                    RandomTeleport.randomTp(player);
                } else if (args[0].equalsIgnoreCase("lotto")) {
                    try {
                        Lottery.lottoCommands(player, args[1], sender);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        player.sendMessage(ChatColor.RED + "Invalid command. Please use this command as follows -> " + ChatColor.AQUA + "[/dt lotto check] [/dt lotto buyin] [/dt lotto total]");
                    }
                } else if (args[0].equalsIgnoreCase("withdraw")){
                    double amount = Double.parseDouble(args[1]);
                    if (amount > 499) {
                        if (amount <= Api.getEconomy().getBalance(Api.getOfflinePlayerFromPlayer(player))) {
                            Api.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), amount);
                            ItemStack paper = new ItemStack(Material.PAPER, 1);
                            ItemMeta im = paper.getItemMeta();
                            assert im != null;
                            im.setLore(Collections.singletonList("MoneyOrder - " + player.getName() + " - " + amount));
                            paper.setItemMeta(im);
                            World world = player.getWorld();
                            Location dropLocation = player.getLocation();
                            world.dropItem(dropLocation, paper);
                            player.sendMessage(ChatColor.GREEN + "You have withdrawn " + ChatColor.GOLD + "$" + amount);
                            FileConfiguration moneyOrderLogCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_MONEY_ORDER_LOG_FILE_NAME));
                            Date date = new Date();
                            moneyOrderLogCfg.set(player.getUniqueId().toString() + ".playerName", player.getName());
                            moneyOrderLogCfg.set(player.getUniqueId().toString() + "." + date + ".type", "withdraw");
                            moneyOrderLogCfg.set(player.getUniqueId().toString() + "." + date + ".amount", amount);
                            moneyOrderLogCfg.save(Api.getConfigFile(Constants.YML_MONEY_ORDER_LOG_FILE_NAME));
                        } else {
                            player.sendMessage(ChatColor.RED + "Can't withdraw more than your current balance.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Can't withdraw less than $500.");
                    }
                } else if (args[0].equalsIgnoreCase("deposit")){
                    Inventory playerInv = player.getInventory();
                    int count = 0;
                    File essentialsFile = new File("plugins/Essentials", "config.yml");
                    for (ItemStack is : playerInv){
                        if (is != null){
                            ItemMeta im = is.getItemMeta();
                            if (im != null && im.getLore() != null && im.getLore().get(0).contains("MoneyOrder")){
                                String[] lore = im.getLore().get(0).split(" - ");
                                String playerName = lore[1];
                                double amount = Double.parseDouble(lore[2]) * is.getAmount();
                                double maxAmount = YamlConfiguration.loadConfiguration(essentialsFile).getDouble("max-money");
                                double playerBalance = Api.getEconomy().getBalance(player);
                                double sumOfBalanceAndAmount = Double.sum(playerBalance, amount);
                                if (Double.compare(sumOfBalanceAndAmount, maxAmount) <= 0){
                                    Api.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), amount);
                                    player.sendMessage(ChatColor.GREEN + "Deposited " + ChatColor.GOLD + "$" + amount + ChatColor.GREEN + " from " + ChatColor.GOLD + playerName);
                                    is.setAmount(0);
                                    player.getInventory().setItem(count, is);
                                    FileConfiguration moneyOrderLogCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_MONEY_ORDER_LOG_FILE_NAME));
                                    Date date = new Date();
                                    moneyOrderLogCfg.set(player.getUniqueId().toString() + ".playerName", player.getName());
                                    moneyOrderLogCfg.set(player.getUniqueId().toString() + "." + date + ".from", playerName);
                                    moneyOrderLogCfg.set(player.getUniqueId().toString() + "." + date + ".type", "deposit");
                                    moneyOrderLogCfg.set(player.getUniqueId().toString() + "." + date + ".amount", amount);
                                    moneyOrderLogCfg.save(Api.getConfigFile(Constants.YML_MONEY_ORDER_LOG_FILE_NAME));
                                } else {
                                    player.sendMessage(ChatColor.RED + "Can't deposit more MoneyOrders because it will exceed the server's maximum amount.");
                                }
                            }
                        }
                        count++;
                    }
                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("switch") && args[1] != null){
                    FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
                    Inventory playerInventory = player.getInventory();
                    Location creativeChestLocation = new Location(Bukkit.getWorld("world"), configCfg.getDouble("creativeChest.x"), configCfg.getDouble("creativeChest.y"), configCfg.getDouble("creativeChest.z"));
                    Block creativeChestBlock = creativeChestLocation.getBlock();
                    Chest creativeChest = (Chest) creativeChestBlock.getState();
                    Location survivalChestLocation = new Location(Bukkit.getWorld("world"), configCfg.getDouble("survivalChest.x"), configCfg.getDouble("survivalChest.y"), configCfg.getDouble("survivalChest.z"));
                    Block survivalChestBlock = survivalChestLocation.getBlock();
                    Chest survivalChest = (Chest) survivalChestBlock.getState();
                    if (args[1].equalsIgnoreCase("creative") && player.getGameMode().equals(GameMode.SURVIVAL)){
                        int count = 0;
                        for(ItemStack is : playerInventory){
                            survivalChest.getInventory().setItem(count, is);
                            playerInventory.setItem(count, creativeChest.getInventory().getItem(count));
                            creativeChest.getInventory().setItem(count, new ItemStack(Material.AIR, 0));
                            count++;
                        }
                        player.setGameMode(GameMode.CREATIVE);
                    } else if (args[1].equalsIgnoreCase("survival") && player.getGameMode().equals(GameMode.CREATIVE)){
                        int count = 0;
                        for(ItemStack is : playerInventory){
                            creativeChest.getInventory().setItem(count, is);
                            playerInventory.setItem(count, survivalChest.getInventory().getItem(count));
                            survivalChest.getInventory().setItem(count, new ItemStack(Material.AIR, 0));
                            count++;
                        }
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                } else if (args[0].equalsIgnoreCase("transferPet")){
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
                        if (tameableEntities.contains(entity.getType())){
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
                } else if (args[0].equalsIgnoreCase("clearBoss") && args[1] != null){
                    FileConfiguration bossCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_BOSS_FILE_NAME));
                    bossCfg.set("bossEnabled", false);
                    player.sendMessage(ChatColor.GREEN + "Boss has been terminated.");
                    try {
                        bossCfg.save(Api.getConfigFile(Constants.YML_BOSS_FILE_NAME));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("killBoss")){
                    FileConfiguration bossCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_BOSS_FILE_NAME));
                    try {
                        Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(bossCfg.getString("bossUUID"))))).remove();
                        bossCfg.set("bossEnabled", false);
                        player.sendMessage(ChatColor.GREEN + "Boss has been terminated.");
                        try {
                            bossCfg.save(Api.getConfigFile(Constants.YML_BOSS_FILE_NAME));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (NullPointerException ignored){

                    }
                } else if (player.getName().equalsIgnoreCase("latch93") && args[0].equalsIgnoreCase("removeHomes")){
                    for (File file : Objects.requireNonNull(new File("plugins/Essentials/userdata").listFiles())) {
                        FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
                        if (conf.isSet("homes")){
                            conf.set("homes", null);
                        }
                        if (conf.isSet("logoutlocation")){
                            conf.set("logoutlocation.world", "4326982f-e536-46cb-8360-aa3de8a064fb");
                            conf.set("logoutlocation.x", 17323.5);
                            conf.set("logoutlocation.y", 72.0);
                            conf.set("logoutlocation.z", -4279.5);
                            conf.set("logoutlocation.yaw", 89.13036346435547);
                            conf.set("logoutlocation.pitch", 22.564151763916016);
                            conf.set("logoutlocation.world-name", "world");
                        }
                        if (conf.isSet("lastlocation")){
                            conf.set("lastlocation.world", "4326982f-e536-46cb-8360-aa3de8a064fb");
                            conf.set("lastlocation.x", 17323.5);
                            conf.set("lastlocation.y", 72.0);
                            conf.set("lastlocation.z", -4279.5);
                            conf.set("lastlocation.yaw", 89.13036346435547);
                            conf.set("lastlocation.pitch", 22.564151763916016);
                            conf.set("lastlocation.world-name", "world");
                        }
                        try {
                            conf.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (args[0].equalsIgnoreCase("link") && args[1] != null){
                    boolean hasMemberRole = false;
                    Member discordMember = Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID)).getMemberById(args[1]);
                    for (Role role : Objects.requireNonNull(discordMember.getRoles())){
                        if ("Member".equalsIgnoreCase(role.getName())){
                            hasMemberRole = true;
                        }
                    }
                    if (Boolean.TRUE.equals(hasMemberRole)){
                        User user = Main.luckPerms.getUserManager().getUser(player.getUniqueId());
                        assert user != null;
                        user.setPrimaryGroup("member");
                        InheritanceNode member = InheritanceNode.builder("member").value(true).build();
                        InheritanceNode defaultNode = InheritanceNode.builder("default").build();
                        user.data().add(member);
                        user.data().remove(defaultNode);
                        Main.luckPerms.getUserManager().saveUser(user);
                        GeolocationParams geoParams = new GeolocationParams();
                        geoParams.setFields("geo,time_zone,currency");
                        geoParams.setIncludeSecurity(true);
                        File playerDataFile = new File("plugins/Essentials/userdata", player.getUniqueId() + ".yml");
                        FileConfiguration playerDataCfg = Api.getFileConfiguration(playerDataFile);
                        geoParams.setIPAddress(playerDataCfg.getString("ip-address"));
                        Geolocation geolocation = Main.ipApi.getGeolocation(geoParams);
                        String ipInfo = ".ip-info.";
                        FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".discordName", discordMember.getUser().getName());
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".discordId", discordMember.getId());
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".discordNickname", discordMember.getNickname());
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".minecraftName", player.getName());
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".minecraftId", player.getUniqueId().toString());
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".joinTime", discordMember.getTimeJoined().toLocalDateTime().toString());
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".isPlayerInDiscord", true);
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ipInfo + "countryName", geolocation.getCountryName());
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ipInfo + "cityName", geolocation.getCity());
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ipInfo + "currencyName", geolocation.getCurrency().getName());
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ipInfo + "currencySymbol", geolocation.getCurrency().getSymbol());
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ipInfo + "offsetTime", geolocation.getTimezone().getOffset());
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ipInfo + "timezoneName", geolocation.getTimezone().getName());
                        whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ipInfo + "ipAddress", geolocation.getIPAddress());
                        try {
                            whitelistCfg.save(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        player.sendMessage(ChatColor.GREEN + "You are now linked up and have perms. Happy Mining!!!");
                    } else {
                        player.sendMessage(ChatColor.RED + "You need to react to the rules in the " + ChatColor.AQUA + "Discord Rules Channel " + ChatColor.RED + "before you can link your account.");
                    }
                } else if (args[0].equalsIgnoreCase("help")){
                    player.sendMessage(ChatColor.GREEN + "View our Wiki here -> " + ChatColor.AQUA + "https://github.com/Latch93/DiscordText/wiki/LMP-Wiki");
                } else if (player.getName().equalsIgnoreCase(Constants.SERVER_OWNER_NAME) && args[0].equalsIgnoreCase("spectate")){
                    spectateInsideRandomPlayer(player);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e){
            player.sendMessage(ChatColor.RED + "An error has occurred. Please review your command and try again.");
        } catch (NullPointerException e){
            player.sendMessage(ChatColor.RED + "An error has occurred. If you are linking your Discord and Minecraft accounts, you need to be a member in Discord.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void spectateInsideRandomPlayer(Player player) {
        Random rand = new Random();
        int n = rand.nextInt(Bukkit.getOnlinePlayers().size());
        ArrayList<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (!onlinePlayers.get(n).getName().equalsIgnoreCase("latch93") && Boolean.FALSE.equals(Api.isPlayerInvisible(onlinePlayers.get(n).getUniqueId().toString()))){
            player.setSpectatorTarget(onlinePlayers.get(n));
        } else {
            spectateInsideRandomPlayer(player);
        }
    }

}
