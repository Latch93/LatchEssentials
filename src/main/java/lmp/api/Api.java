package lmp.api;

import lmp.LatchDiscord;
import lmp.Main;
import lmp.constants.Constants;
import lmp.constants.YmlFileNames;
import lmp.runnable.LatchTwitchBotRunnable;
import lmp.twitch.LatchTwitchBot;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.util.Tristate;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class Api {
    public static LatchTwitchBot twitchBot;
    private static Economy econ = null;
    private static Permission perm = null;

    // Formats chat message in Minecraft to send to Discord channel
    public static String convertMinecraftMessageToDiscord(String senderName, String senderMessage) {
        ArrayList<String> colorCodes = new ArrayList<>();
        colorCodes.add("&a");
        colorCodes.add("&b");
        colorCodes.add("&c");
        colorCodes.add("&d");
        colorCodes.add("&e");
        colorCodes.add("&f");
        colorCodes.add("&0");
        colorCodes.add("&1");
        colorCodes.add("&2");
        colorCodes.add("&3");
        colorCodes.add("&4");
        colorCodes.add("&5");
        colorCodes.add("&6");
        colorCodes.add("&7");
        colorCodes.add("&8");
        colorCodes.add("&9");
        colorCodes.add("&k");
        colorCodes.add("&l");
        colorCodes.add("&m");
        colorCodes.add("&n");
        colorCodes.add("&o");
        colorCodes.add("&r");
        colorCodes.add("§a");
        colorCodes.add("§b");
        colorCodes.add("§c");
        colorCodes.add("§d");
        colorCodes.add("§e");
        colorCodes.add("§f");
        colorCodes.add("§0");
        colorCodes.add("§1");
        colorCodes.add("§2");
        colorCodes.add("§3");
        colorCodes.add("§4");
        colorCodes.add("§5");
        colorCodes.add("§6");
        colorCodes.add("§7");
        colorCodes.add("§8");
        colorCodes.add("§9");
        colorCodes.add("§k");
        colorCodes.add("§l");
        colorCodes.add("§m");
        colorCodes.add("§n");
        colorCodes.add("§o");
        colorCodes.add("§r");
        String finalMessage = "";
        for (String colorCode : colorCodes) {
            if (senderName != null) {
                senderName = senderName.replace(colorCode, "");
            }
            senderMessage = senderMessage.replace(colorCode, "");
        }
        if (senderMessage.toLowerCase().contains("@everyone") || senderMessage.toLowerCase().contains("@here")) {
            senderMessage = "I tried to @ everyone or @ here. I shouldn't do that, but I did";
        }
        if (senderName != null) {
            finalMessage = senderName + " » " + senderMessage;
        } else {
            finalMessage = senderMessage;
        }
        return finalMessage;
    }

    public static OfflinePlayer getOfflinePlayerFromPlayer(Player player) {
        return Bukkit.getOfflinePlayer(player.getUniqueId());
    }

    // Economy Setup
    public static void setupEconomy(Plugin plugin) {
        if (plugin == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
        setEconomy(econ);
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static void setEconomy(Economy economy) {
        econ = economy;
    }

    public static File getConfigFile(String fileName) {
        return new File(getMainPlugin().getDataFolder(), fileName + ".yml");
    }

    public static FileConfiguration getFileConfiguration(String fileName) {
        return YamlConfiguration.loadConfiguration(getConfigFile(fileName));
    }

    public static FileConfiguration loadConfig(String fileName) {
        return YamlConfiguration.loadConfiguration(getConfigFile(fileName));
    }

    public static FileConfiguration getFileConfigurationFromFile(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void messageInConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static Main getMainPlugin() {
        return getPlugin(Main.class);
    }

    public static void cancelEventsInPreviousSeason(String worldName, String player, BlockBreakEvent blockBreakEvent, BlockPlaceEvent blockPlaceEvent, InventoryClickEvent inventoryClickEvent, PlayerPortalEvent playerPortalEvent) {
        ArrayList<String> deniedWorlds = new ArrayList<>();
        deniedWorlds.add("season1");
        deniedWorlds.add("season3");
        deniedWorlds.add("season4");
        deniedWorlds.add("season5");
        deniedWorlds.add("season6");
        deniedWorlds.add("season7");

        if (deniedWorlds.contains(worldName)) {
            if (player == null || !player.equalsIgnoreCase("Latch93")) {
                if (blockBreakEvent != null) {
                    blockBreakEvent.setCancelled(true);
                } else if (blockPlaceEvent != null) {
                    blockPlaceEvent.setCancelled(true);
                } else if (inventoryClickEvent != null) {
                    inventoryClickEvent.setCancelled(true);
                } else if (playerPortalEvent != null) {
                    playerPortalEvent.setCancelled(true);
                }
            }
        }
    }

    // Calculate amount of EXP needed to level up
    public static int getExpToLevelUp(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        } else if (level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }

    // Calculate total experience up to a level
    public static int getExpAtLevel(int level) {
        if (level <= 16) {
            return (int) (Math.pow(level, 2) + 6 * level);
        } else if (level <= 31) {
            return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360.0);
        } else {
            return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220.0);
        }
    }

    // Calculate player's current EXP amount
    public static int getPlayerExp(Player player) {
        int exp = 0;
        int level = player.getLevel();

        // Get the amount of XP in past levels
        exp += getExpAtLevel(level);

        // Get amount of XP towards next level
        exp += Math.round(getExpToLevelUp(level) * player.getExp());

        return exp;
    }

    // Give or take EXP
    public static int changePlayerExp(Player player, int exp) {
        // Get player's current exp
        int currentExp = getPlayerExp(player);

        // Reset player's current exp to 0
        player.setExp(0);
        player.setLevel(0);

        // Give the player their exp back, with the difference
        int newExp = currentExp + exp;
        player.giveExp(newExp);

        // Return the player's new exp amount
        return newExp;
    }

    public static boolean isPlayerInvisible(String uuid) {
        File superVanishFile = new File("plugins/SuperVanish", "data.yml");
        return YamlConfiguration.loadConfiguration(superVanishFile).getStringList("InvisiblePlayers").contains(uuid);
    }

    public static String getTwitchUsername(String minecraftUsername) {
        String twitchUsername = null;
        FileConfiguration twitchCfg = Api.getFileConfiguration(YmlFileNames.YML_TWITCH_FILE_NAME);
        if (twitchCfg.isSet("players")) {
            for (String user : twitchCfg.getConfigurationSection("players").getKeys(false)) {
                if (twitchCfg.getString(lmp.Constants.YML_PLAYERS + user + ".minecraftUsername").equalsIgnoreCase(minecraftUsername)) {
                    twitchUsername = twitchCfg.getString(lmp.Constants.YML_PLAYERS + user + ".twitchUsername");
                }
            }
        }
        return twitchUsername;
    }

    public static String getPlayerTime(String minecraftId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String time = "";
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        try {
            sdf.setTimeZone(TimeZone.getTimeZone(whitelistCfg.getString("players." + minecraftId + ".ip-info.timezoneName")));
            time = sdf.format(calendar.getTime());
        } catch (NullPointerException ignore) {
        }
        return time;
    }

    public static double getPlayerBalance(Player player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        Economy econ = Api.getEconomy();
        return econ.getBalance(offlinePlayer);
    }

    public static Long getSecondsPlayedInSession(String playerId) {
        FileConfiguration bankCfg = Api.getFileConfiguration(YmlFileNames.YML_BANK_FILE_NAME);
        long loginTime = bankCfg.getLong(lmp.Constants.YML_PLAYERS + playerId + ".loginTime");
        Date date = new Date();
        long logoutTime = date.getTime();
        long totalTimePlayedMilli = logoutTime - loginTime;
        return TimeUnit.MILLISECONDS.toSeconds(totalTimePlayedMilli);
    }

    public static boolean cancelJrModEvent(UUID uniqueId) {
        boolean isInvisibleJrMod = false;
        if (Api.isPlayerInvisible(uniqueId.toString())) {
            net.luckperms.api.model.user.User user = Main.luckPerms.getUserManager().getUser(uniqueId);
            assert user != null;
            if (user.data().contains(InheritanceNode.builder("jr-mod").value(true).build(), NodeEqualityPredicate.EXACT).equals(Tristate.TRUE)) {
                isInvisibleJrMod = true;
                Objects.requireNonNull(Bukkit.getPlayer(uniqueId)).sendMessage(ChatColor.RED + "Jr Mods are not allowed to Break or Place blocks while invisible.");
            }
        }
        return isInvisibleJrMod;
    }

    public static LuckPerms getLuckPerms() {
        return Main.luckPerms;
    }

    public static void stopTwitchBot(List<LatchTwitchBotRunnable> twitchBotList, Player player) {
        Iterator<LatchTwitchBotRunnable> iter = twitchBotList.iterator();
        while (iter.hasNext()) {
            LatchTwitchBotRunnable runBot = iter.next();
            if (runBot.getMinecraftName().equalsIgnoreCase(player.getName())) {
                runBot.getTwitchClient().close();
                player.sendMessage(ChatColor.GREEN + "Your TwitchBot has been " + ChatColor.RED + "terminated.");
                Api.messageInConsole(ChatColor.RED + "Terminated " + ChatColor.GOLD + player.getName() + "'s " + ChatColor.RED + "TwitchBot.");
                iter.remove();
            }
        }
    }

    public static String getPlayerChatWorldPrefix(String worldName) {
        String worldPrefix = "[LMP] - ";
        if (worldName.contains("hardcore")) {
            worldPrefix = "[Hardcore] - ";
        }
        if (worldName.contains("anarchy")) {
            worldPrefix = "[Anarchy] - ";
        }
        return worldPrefix;
    }

    public static void stopAllTwitchBots(List<LatchTwitchBotRunnable> twitchBotList) {
        Iterator<LatchTwitchBotRunnable> iter = twitchBotList.iterator();
        while (iter.hasNext()) {
            LatchTwitchBotRunnable runBot = iter.next();
            runBot.getTwitchClient().close();
            Api.messageInConsole(ChatColor.RED + "Terminated " + ChatColor.GOLD + "ALL " + ChatColor.RED + "TwitchBots.");
            iter.remove();
        }
        Api.messageInConsole(ChatColor.RED + "Terminated " + ChatColor.GOLD + "all " + ChatColor.RED + "TwitchBots.");
    }

    public static LatchTwitchBot getPlayerTwitchBot(List<LatchTwitchBotRunnable> twitchBotList, String minecraftName) {
        Iterator<LatchTwitchBotRunnable> iter = twitchBotList.iterator();
        LatchTwitchBot bot = null;
        while (iter.hasNext()) {
            LatchTwitchBotRunnable iterBot = iter.next();
            if (iterBot.getMinecraftName().equalsIgnoreCase(minecraftName)) {
                bot = iterBot.getBot();
            }
        }
        return bot;
    }


    public static String getChannelNameFromId(String channelID) {
        String channelName = null;
        List<TextChannel> textChannelList = LatchDiscord.jda.getTextChannels();
        for (TextChannel textChannel : textChannelList) {
            if (channelID.equalsIgnoreCase(textChannel.getId())) {
                channelName = textChannel.getName();
            }
        }
        return channelName;
    }

    public static String getPlayerNameFromId(String channelID) {
        String playerName = null;
        List<User> userList = LatchDiscord.jda.getUsers();
        for (User user : userList) {
            if (channelID.equalsIgnoreCase(user.getId())) {
                playerName = user.getName();
            }
        }
        return playerName;
    }

    public static int getPlayerPing(Player player) {
        return player.getPing();
    }

    // Luck Perms Api
    public static Boolean doesPlayerHavePermission(String minecraftId, String groupName) throws ExecutionException, InterruptedException {
        CompletableFuture<net.luckperms.api.model.user.User> userFuture = Api.getLuckPerms().getUserManager().loadUser(UUID.fromString(minecraftId));
        return userFuture.thenApplyAsync(user -> {
            Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
            return inheritedGroups.stream().anyMatch(g -> g.getName().equals(groupName));
        }).get();
    }

    public static void addPlayerToPermissionGroup(String minecraftId, String groupName) {
        InheritanceNode node = InheritanceNode.builder(groupName).value(true).build();
        CompletableFuture<net.luckperms.api.model.user.User> userFuture = Api.getLuckPerms().getUserManager().loadUser(UUID.fromString(minecraftId));
        userFuture.thenAcceptAsync(user -> {
            user.data().add(node);
            Main.luckPerms.getUserManager().saveUser(user);
        });
    }

    public static void removePlayerFromPermissionGroup(String minecraftId, String groupName) {
        InheritanceNode node = InheritanceNode.builder(groupName).value(true).build();
        CompletableFuture<net.luckperms.api.model.user.User> userFuture = Api.getLuckPerms().getUserManager().loadUser(UUID.fromString(minecraftId));
        userFuture.thenAcceptAsync(user -> {
            user.data().remove(node);
            Main.luckPerms.getUserManager().saveUser(user);
        });
    }

    public static String getTwitchChannelName(String minecraftUsername) {
        FileConfiguration twitchCfg = Api.getFileConfiguration(YmlFileNames.YML_TWITCH_FILE_NAME);
        String twitchUsername = null;
        for (String user : twitchCfg.getConfigurationSection("players").getKeys(false)) {
            if (twitchCfg.getString(lmp.Constants.YML_PLAYERS + user + ".minecraftUsername").equalsIgnoreCase(minecraftUsername)) {
                twitchUsername = twitchCfg.getString(lmp.Constants.YML_PLAYERS + user + ".twitchUsername");
            }
        }
        return "#" + twitchUsername;
    }

    public static boolean arePlayersOnline() {
        return !Bukkit.getOnlinePlayers().isEmpty();
    }

    public static Player getOnlinePlayer() {
        Player overworldPlayer = null;
        Bukkit.getOnlinePlayers();
        for (Player player : Bukkit.getOnlinePlayers()) {
            overworldPlayer = player;
            break;
        }
        return overworldPlayer;
    }


    public static void denyBackIntoXPFarm(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        File playerDataFile = new File("plugins/Essentials/userdata", player.getUniqueId() + ".yml");
        FileConfiguration playerDataCfg = YamlConfiguration.loadConfiguration(playerDataFile);
        Location lastLocation = new Location(Bukkit.getWorld("world"), playerDataCfg.getDouble("lastlocation.x"), playerDataCfg.getDouble("lastlocation.y"), playerDataCfg.getDouble("lastlocation.z"));
        if (player.getWorld().equals(Bukkit.getWorld("world"))) {
            FileConfiguration xpFarmCfg = Api.getFileConfiguration(YmlFileNames.YML_XP_FARM_FILE_NAME);
            double lesserX = xpFarmCfg.getDouble("lesserX");
            double greaterX = xpFarmCfg.getDouble("greaterX");
            double lesserY = xpFarmCfg.getDouble("lesserY");
            double greaterY = xpFarmCfg.getDouble("greaterY");
            double lesserZ = xpFarmCfg.getDouble("lesserZ");
            double greaterZ = xpFarmCfg.getDouble("greaterZ");
            if (lastLocation.getBlockX() >= lesserX && lastLocation.getBlockX() <= greaterX) {
                if (lastLocation.getBlockY() >= lesserY && lastLocation.getBlockY() <= greaterY) {
                    if (lastLocation.getBlockZ() >= lesserZ && lastLocation.getBlockZ() <= greaterZ) {
                        e.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You can't use /back into the XP Farm.");
                    }
                }
            }
        }
    }

    public static void creeperBGone(EntitySpawnEvent e) {
        if (e.getEntity().getType().equals(EntityType.CREEPER)) {
            Location creeperSpawnLocation = e.getLocation();
            FileConfiguration creeperCfg = Api.getFileConfiguration(YmlFileNames.YML_CREEPERS_B_GONE_FILE_NAME);
            List<String> creeperLocationList = new ArrayList<>();
            if (!creeperCfg.getStringList("locations").isEmpty()) {
                creeperLocationList = creeperCfg.getStringList("locations");
            }
            for (int i = 0; i < creeperLocationList.size(); i++) {
                String creeperBGoneLocationString = creeperLocationList.get(i);
                String[] firstCreeperArr = creeperBGoneLocationString.split(",");
                String[] xCreeperLocationArr = firstCreeperArr[1].split("=");
                String[] yCreeperLocationArr = firstCreeperArr[2].split("=");
                String[] zCreeperLocationArr = firstCreeperArr[3].split("=");
                double xCreeperBGoneLocation = Double.parseDouble(xCreeperLocationArr[1]);
                double yCreeperBGoneLocation = Double.parseDouble(yCreeperLocationArr[1]);
                double zCreeperBGoneLocation = Double.parseDouble(zCreeperLocationArr[1]);
                double distance = Math.sqrt(Math.pow(xCreeperBGoneLocation - e.getLocation().getBlockX(), 2) +
                        Math.pow(yCreeperBGoneLocation - e.getLocation().getBlockY(), 2) +
                        Math.pow(zCreeperBGoneLocation - e.getLocation().getBlockZ(), 2));
                if (distance <= 150) {
                    e.setCancelled(true);
                }

            }
        }
    }

    public static void spawnStopper(EntitySpawnEvent e) {
        FileConfiguration spawnStopperCfg = Api.getFileConfiguration(YmlFileNames.YML_SPAWN_STOPPER_FILE_NAME);
        List<String> mobsToStopSpawningList = spawnStopperCfg.getStringList("denySpawnList");
        if (mobsToStopSpawningList.contains(e.getEntity().getType().toString())) {
            List<String> mobLocationList = new ArrayList<>();
            if (!spawnStopperCfg.getStringList("locations").isEmpty()) {
                mobLocationList = spawnStopperCfg.getStringList("locations");
            }
            for (int i = 0; i < mobLocationList.size(); i++) {
                String spawnStopperLocationString = mobLocationList.get(i);
                String[] firstMobArr = spawnStopperLocationString.split(",");
                String[] xMobLocationArr = firstMobArr[1].split("=");
                String[] yMobLocationArr = firstMobArr[2].split("=");
                String[] zMobLocationArr = firstMobArr[3].split("=");
                double xMobLocation = Double.parseDouble(xMobLocationArr[1]);
                double yMobLocation = Double.parseDouble(yMobLocationArr[1]);
                double zMobLocation = Double.parseDouble(zMobLocationArr[1]);
                double distance = Math.sqrt(Math.pow(xMobLocation - e.getLocation().getBlockX(), 2) +
                        Math.pow(yMobLocation - e.getLocation().getBlockY(), 2) +
                        Math.pow(zMobLocation - e.getLocation().getBlockZ(), 2));
                if (distance <= 250) {
                    e.setCancelled(true);
                }

            }
        }
    }

    public static void denyCommandUseInXPFarm(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        ArrayList<String> playerBypassList = new ArrayList<>();
        String message = e.getMessage().toLowerCase();
        playerBypassList.add("f4c77e52-de47-4174-8282-0d962d089301");
        ArrayList<String> commandBypassList = new ArrayList<>();
        commandBypassList.add("/bal");
        commandBypassList.add("/lmp deposit");
        commandBypassList.add("/lmp withdraw");
        commandBypassList.add("/co i");
        commandBypassList.add("/geyser offhand");
        if (!commandBypassList.contains(message) && !playerBypassList.contains(e.getPlayer().getUniqueId().toString()) && player.getWorld().equals(Bukkit.getWorld("world"))) {
            FileConfiguration xpFarmCfg = Api.getFileConfiguration(YmlFileNames.YML_XP_FARM_FILE_NAME);
            Location lastLocation = e.getPlayer().getLocation();
            double lesserX = xpFarmCfg.getDouble("lesserX");
            double greaterX = xpFarmCfg.getDouble("greaterX");
            double lesserY = xpFarmCfg.getDouble("lesserY");
            double greaterY = xpFarmCfg.getDouble("greaterY");
            double lesserZ = xpFarmCfg.getDouble("lesserZ");
            double greaterZ = xpFarmCfg.getDouble("greaterZ");
            if (lastLocation.getBlockX() >= lesserX && lastLocation.getBlockX() <= greaterX) {
                if (lastLocation.getBlockY() >= lesserY && lastLocation.getBlockY() <= greaterY) {
                    if (lastLocation.getBlockZ() >= lesserZ && lastLocation.getBlockZ() <= greaterZ) {
                        e.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You can't use commands when at the farm");
                    }
                }
            }
        }
    }

    public static void stopXPFarm() throws IOException {
        FileConfiguration xpFarmCfg = Api.getFileConfiguration(YmlFileNames.YML_XP_FARM_FILE_NAME);
        Long timeStartedFarm = xpFarmCfg.getLong("timeStarted");
        DateTime dateOne = new DateTime();
        Long currentTime = dateOne.getMillis();
        long timeDifference = currentTime - timeStartedFarm;
        long timerLimit = xpFarmCfg.getLong("timer");
        if (timeDifference > timerLimit) {
            Player playerAtFarm = Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(xpFarmCfg.getString("playerIDUsingFarm"))));
            double spawnX = xpFarmCfg.getDouble("spawnX");
            double spawnY = xpFarmCfg.getDouble("spawnY");
            double spawnZ = xpFarmCfg.getDouble("spawnZ");
            Location spawnLocation = new Location(Bukkit.getWorld("world"), spawnX, spawnY, spawnZ);
            assert playerAtFarm != null;
            playerAtFarm.teleport(spawnLocation);
            playerAtFarm.sendMessage(ChatColor.AQUA + "Your time is up!");
            xpFarmCfg.set("isFarmInUse", false);
            xpFarmCfg.save(Api.getConfigFile(YmlFileNames.YML_XP_FARM_FILE_NAME));
        }
    }

    public static void removeFlyCommand() throws IOException {
        FileConfiguration flyListCfg = Api.getFileConfiguration(YmlFileNames.YML_FLY_LIST_FILE_NAME);
        for (String mcID : flyListCfg.getConfigurationSection("players").getKeys(false)) {
            if (flyListCfg.getBoolean(lmp.Constants.YML_PLAYERS + mcID + ".fly.enabled")){
                Long timeStartedCommand = flyListCfg.getLong(lmp.Constants.YML_PLAYERS + mcID + ".fly.commandTime");
                long timerLimit = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getLong("flyCommandLength");
                DateTime dateOne = new DateTime();
                Long currentTime = dateOne.getMillis();
                long timeDifference = currentTime - timeStartedCommand;
                if (timeDifference > timerLimit) {
                    Player player = Bukkit.getPlayer(UUID.fromString(mcID));
                    if (player != null) {
                        Api.removePlayerLuckPermPermission(player, "essentials.fly");
                        player.sendMessage(ChatColor.GREEN + "You're time is up. Fly privileges have been revoked. Thank you for flying " + ChatColor.GOLD + "Air Latch" +ChatColor.GREEN + ".");
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        flyListCfg.set(lmp.Constants.YML_PLAYERS + mcID + ".fly.enabled", false);
                        flyListCfg.save(Api.getConfigFile(YmlFileNames.YML_FLY_LIST_FILE_NAME));
                    }
                }
            }
        }
    }

    public static void stopDiscordBot() {
        LatchDiscord.getJDA().shutdown();
    }

    public static void placeBlockLog(BlockPlaceEvent event) throws IOException {
        if ((Objects.requireNonNull(event.getBlock().getLocation().getWorld()).getName().equalsIgnoreCase("world")) && (event.getBlock().getType().equals(Material.TNT) || event.getBlock().getType().equals(Material.END_CRYSTAL))) {
            FileConfiguration blockPlaceLogCfg = Api.getFileConfiguration(YmlFileNames.YML_BLOCK_PLACE_LOG_FILE_NAME);
            Date date = new Date();
            Player player = event.getPlayer();
            blockPlaceLogCfg.set(player.getUniqueId().toString() + ".playerName", event.getPlayer().getName());
            blockPlaceLogCfg.set(player.getUniqueId().toString() + "." + date + ".blockType", event.getBlock().getType().toString());
            blockPlaceLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.x", event.getBlock().getLocation().getBlockX());
            blockPlaceLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.y", event.getBlock().getLocation().getBlockY());
            blockPlaceLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.z", event.getBlock().getLocation().getBlockZ());
            blockPlaceLogCfg.save(Api.getConfigFile(YmlFileNames.YML_BLOCK_PLACE_LOG_FILE_NAME));
        }
    }

    public static void blockBreakLog(BlockBreakEvent event) throws IOException, ExecutionException, InterruptedException {
        ArrayList<Material> materialBreakList = new ArrayList<>();
        materialBreakList.add(Material.DIAMOND_ORE);
        materialBreakList.add(Material.DEEPSLATE_DIAMOND_ORE);
        materialBreakList.add(Material.IRON_ORE);
        materialBreakList.add(Material.DEEPSLATE_IRON_ORE);
        materialBreakList.add(Material.ANCIENT_DEBRIS);
        String worldName = event.getPlayer().getWorld().getName();
        if (materialBreakList.contains(event.getBlock().getType())) {
            if (!Api.doesPlayerHavePermission(event.getPlayer().getUniqueId().toString(), "helper")) {
                FileConfiguration blockBreakLogCfg = Api.getFileConfiguration(YmlFileNames.YML_BLOCK_BREAK_LOG_FILE_NAME);
                Date date = new Date();
                Player player = event.getPlayer();
                Location location = event.getBlock().getLocation();
                blockBreakLogCfg.set(player.getUniqueId().toString() + ".playerName", event.getPlayer().getName());
                blockBreakLogCfg.set(player.getUniqueId().toString() + "." + date + ".blockType", event.getBlock().getType().toString());
                blockBreakLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.worldName", worldName);
                blockBreakLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.x", location.getBlockX());
                blockBreakLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.y", location.getBlockY());
                blockBreakLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.z", location.getBlockZ());
                blockBreakLogCfg.save(Api.getConfigFile(YmlFileNames.YML_BLOCK_BREAK_LOG_FILE_NAME));
            }
            if (!Api.doesPlayerHavePermission(event.getPlayer().getUniqueId().toString(), "mod")) {
                if (event.getBlock().getType().equals(Material.DIAMOND_ORE) || event.getBlock().getType().equals(Material.DEEPSLATE_DIAMOND_ORE)) {
                    Objects.requireNonNull(LatchDiscord.getJDA().getTextChannelById(lmp.Constants.TEST_CHANNEL_ID)).sendMessage("MC Name: " + event.getPlayer().getName() + " | DC Name: " + Api.getDiscordNameFromMCid(event.getPlayer().getUniqueId().toString()) + " | Broke 1 " + event.getBlock().getType().toString() + " block at [" + event.getBlock().getLocation().getBlockX() + ", " + event.getBlock().getLocation().getBlockY() + ", " + event.getBlock().getLocation().getBlockZ() + "] --- World: " + worldName).queue();
                }
                if (event.getBlock().getType().equals(Material.ANCIENT_DEBRIS)) {
                    Objects.requireNonNull(LatchDiscord.getJDA().getTextChannelById(lmp.Constants.DISCORD_STAFF_CHAT_CHANNEL_ID)).sendMessage("MC Name: " + event.getPlayer().getName() + " | DC Name: " + Api.getDiscordNameFromMCid(event.getPlayer().getUniqueId().toString()) + " | Broke an Ancient Debris block at [" + event.getBlock().getLocation().getBlockX() + ", " + event.getBlock().getLocation().getBlockY() + ", " + event.getBlock().getLocation().getBlockZ() + "] --- World: " + worldName).queue();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (Api.doesPlayerHavePermission(p.getUniqueId().toString(), "mod")) {
                            p.sendMessage("[" + ChatColor.YELLOW + "XRAY CHECK" + ChatColor.WHITE + "] - " + ChatColor.RED + event.getPlayer().getName() + ChatColor.WHITE + " » " + ChatColor.YELLOW + "Just mined 1 Ancient Debris. World: " + worldName);
                        }
                    }
                }
            }
        }
    }

    public static void combineChestplateAndElytra(PrepareAnvilEvent e) {
        ItemStack leftSideItem = null;
        ItemStack rightSideItem = null;
        if (e.getInventory().getItem(0) != null && e.getInventory().getItem(1) != null) {
            leftSideItem = e.getInventory().getItem(0);
            rightSideItem = e.getInventory().getItem(1);
            if (rightSideItem != null) {
                assert leftSideItem != null;
                if (leftSideItem.getType().equals(Material.NETHERITE_CHESTPLATE) && rightSideItem.getType().equals(Material.ELYTRA)) {
                    leftSideItem.getEnchantments();
                    rightSideItem.getEnchantments();
                    Map<Enchantment, Integer> chestplateEnchantments = leftSideItem.getEnchantments();
                    Map<Enchantment, Integer> elytraEnchantments = rightSideItem.getEnchantments();
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

                    ItemStack elytra = new ItemStack(Material.ELYTRA, 1);
                    elytra.addUnsafeEnchantments(finalEnchantments);
                    ItemMeta elytraIM = null;
                    if (elytra.getItemMeta() != null){
                        elytraIM = elytra.getItemMeta();
                        elytraIM.setAttributeModifiers(Objects.requireNonNull(leftSideItem.getItemMeta()).getAttributeModifiers());
                        elytraIM.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                        elytraIM.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", 8, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                        elytraIM.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", .1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                        elytra.setItemMeta(elytraIM);
                    }

                    Bukkit.getServer().getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> e.getInventory().setRepairCost(50));
                    e.setResult(elytra);
                }
            }
        }
    }



    public static void denyOpenChestDuringBossBattle(PlayerInteractEvent e) {
        FileConfiguration bossCfg = Api.getFileConfiguration(YmlFileNames.YML_BOSS_FILE_NAME);
        double chestLeftX = bossCfg.getDouble("chestLeftX");
        double chestLeftY = bossCfg.getDouble("chestLeftY");
        double chestLeftZ = bossCfg.getDouble("chestLeftZ");
        double chestRightX = bossCfg.getDouble("chestRightX");
        double chestRightY = bossCfg.getDouble("chestRightY");
        double chestRightZ = bossCfg.getDouble("chestRightZ");
        Location challengerChestLeftLocation = new Location(Bukkit.getWorld("world"), chestLeftX, chestLeftY, chestLeftZ);
        Location challengerChestRightLocation = new Location(Bukkit.getWorld("world"), chestRightX, chestRightY, chestRightZ);

        if (e.getClickedBlock() != null) {
            if (Objects.requireNonNull(e.getClickedBlock()).getLocation().equals(challengerChestLeftLocation) || e.getClickedBlock().getLocation().equals(challengerChestRightLocation)) {
                if (!e.getPlayer().hasPermission("group.jr-mod") && !e.getPlayer().getName().equalsIgnoreCase(bossCfg.getString("playerName"))) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.RED + "This isn't your chest. It belongs to " + ChatColor.GOLD + bossCfg.getString("playerName"));
                }
            }
        }
    }

    public static String getMinecraftIdFromDCid(String discordId) {
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        String minecraftId = "";
        for (String mcID : whitelistCfg.getConfigurationSection(lmp.Constants.YML_PLAYERS).getKeys(false)) {
            if (discordId.equalsIgnoreCase(whitelistCfg.getString(lmp.Constants.YML_PLAYERS + mcID + ".discordId"))) {
                minecraftId = whitelistCfg.getString(lmp.Constants.YML_PLAYERS + mcID + ".minecraftId");
            }
        }
        return minecraftId;
    }

    public static String getDiscordIdFromMCid(String minecraftID) {
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        String discordID = "";
        for (String mcID : whitelistCfg.getConfigurationSection(lmp.Constants.YML_PLAYERS).getKeys(false)) {
            if (minecraftID.equalsIgnoreCase(mcID)) {
                discordID = whitelistCfg.getString(lmp.Constants.YML_PLAYERS + mcID + ".discordId");
            }
        }
        return discordID;
    }

    public static String getMinecraftIdFromMinecraftName(String minecraftName) {
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        String minecraftID = "";
        for (String mcID : whitelistCfg.getConfigurationSection(lmp.Constants.YML_PLAYERS).getKeys(false)) {
            if (minecraftName.equalsIgnoreCase(whitelistCfg.getString(lmp.Constants.YML_PLAYERS + mcID + ".minecraftName"))) {
                minecraftID = whitelistCfg.getString(lmp.Constants.YML_PLAYERS + mcID + ".minecraftId");
            }
        }
        return minecraftID;
    }

    public static String getMinecraftNameFromMinecraftId(String minecraftID) {
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        String minecraftName = "";
        for (String mcID : whitelistCfg.getConfigurationSection(lmp.Constants.YML_PLAYERS).getKeys(false)) {
            if (minecraftID.equalsIgnoreCase(whitelistCfg.getString(lmp.Constants.YML_PLAYERS + mcID + ".minecraftId"))) {
                minecraftName = whitelistCfg.getString(lmp.Constants.YML_PLAYERS + mcID + ".minecraftName");
            }
        }
        return minecraftName;
    }

    public static String getDiscordNameFromMCid(String minecraftID) {
        return Objects.requireNonNull(Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(lmp.Constants.GUILD_ID)).getMemberById(Api.getDiscordIdFromMCid(minecraftID))).getUser().getName();
    }

    public static void setIsPlayerInDiscord() throws IOException {
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        for (String minecraftId : whitelistCfg.getConfigurationSection(lmp.Constants.YML_PLAYERS).getKeys(false)) {
            String discordId = whitelistCfg.getString(lmp.Constants.YML_PLAYERS + minecraftId + ".discordId");
            if (LatchDiscord.getJDA().getGuildById(lmp.Constants.GUILD_ID).getMemberById(discordId) != null) {
                whitelistCfg.set(lmp.Constants.YML_PLAYERS + minecraftId + ".isPlayerInDiscord", true);
            } else {
                whitelistCfg.set(lmp.Constants.YML_PLAYERS + minecraftId + ".isPlayerInDiscord", false);
            }
        }
        whitelistCfg.save(Api.getConfigFile(YmlFileNames.YML_WHITELIST_FILE_NAME));
    }

    public static ArrayList<String> getEssentialsIDs() {
        ArrayList<String> essentialsPlayerDataIDS = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File("plugins/Essentials/userdata").listFiles())) {
            essentialsPlayerDataIDS.add(file.getName().split(".yml")[0]);
        }
        return essentialsPlayerDataIDS;
    }

    public static ArrayList<String> getMinecraftIDOfLinkedPlayersNotInDiscord() {
        ArrayList<String> whitelistedPlayerIDSNotInDiscord = new ArrayList<>();
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        for (String user : Objects.requireNonNull(whitelistCfg.getConfigurationSection("players")).getKeys(false)) {
            if (Boolean.FALSE.equals(whitelistCfg.getBoolean(lmp.Constants.YML_PLAYERS + user + ".isPlayerInDiscord"))) {
                whitelistedPlayerIDSNotInDiscord.add(user);
            }
        }
        return whitelistedPlayerIDSNotInDiscord;
    }

    public static ArrayList<String> getMinecraftIDOfLinkedPlayersInDiscord() {
        ArrayList<String> whitelistedPlayerIDSInDiscord = new ArrayList<>();
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        for (String user : Objects.requireNonNull(whitelistCfg.getConfigurationSection("players")).getKeys(false)) {
            if (Boolean.TRUE.equals(whitelistCfg.getBoolean(lmp.Constants.YML_PLAYERS + user + ".isPlayerInDiscord"))) {
                whitelistedPlayerIDSInDiscord.add(user);
            }
        }
        return whitelistedPlayerIDSInDiscord;
    }
    public static ArrayList<String> getDiscordNamesOfLinkedPlayersStillInDiscord() {
        ArrayList<String> whitelistedPlayerDiscordNames = new ArrayList<>();
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        for (String user : Objects.requireNonNull(whitelistCfg.getConfigurationSection("players")).getKeys(false)) {
            if (Boolean.TRUE.equals(whitelistCfg.getBoolean(lmp.Constants.YML_PLAYERS + user + ".isPlayerInDiscord"))) {
                whitelistedPlayerDiscordNames.add(whitelistCfg.getString(lmp.Constants.YML_PLAYERS + user + ".discordName"));
            }
        }
        return whitelistedPlayerDiscordNames;
    }

    public static ArrayList<String> getListOfMinecraftIDPlayersNotInDiscordOrLinked() {
        ArrayList<String> whitelistedPlayerIDSNotInDiscord = getMinecraftIDOfLinkedPlayersNotInDiscord();
        ArrayList<String> essentialsPlayerDataIDS = getEssentialsIDs();
        ArrayList<String> minecraftIDListOfLinkedPlayers = getAllMinecraftIDOfLinkedPlayers();
        ArrayList<String> listOfPlayersNotHereAnymore = new ArrayList<>();
//        for (String idNotInDiscord : )
        return listOfPlayersNotHereAnymore;
    }

    public static void deleteUnlinkedEssentialAccountsFiles() {
        ArrayList<String> linkedPlayersInDiscord = getMinecraftIDOfLinkedPlayersInDiscord();
        for (File file : Objects.requireNonNull(new File("plugins/Essentials/userdata").listFiles())) {
            String[] splitArr = file.getName().split("\\.");
            if (!linkedPlayersInDiscord.contains(splitArr[0])) {
                file.delete();
            }
        }
    }

    public static void removePlayerFromDonationList(ArrayList<String> minecraftIDListToRemove) throws IOException {
        FileConfiguration donationClaimCfg = Api.getFileConfiguration(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME);
        for (String playerID : minecraftIDListToRemove) {
            if (!playerID.contains(".")) {
                if (Bukkit.getOfflinePlayer(UUID.fromString(playerID)).getName() != null) {
                    donationClaimCfg.set(lmp.Constants.YML_PLAYERS + playerID, null);
                }
            }
        }
        donationClaimCfg.save(Api.getConfigFile(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME));
    }

    public static ArrayList<String> getAllMinecraftIDOfLinkedPlayers() {
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        return new ArrayList<>(Objects.requireNonNull(whitelistCfg.getConfigurationSection("players")).getKeys(false));
    }

    public static void updateDiscordRolesFile() {
        FileConfiguration discordRoleCfg = Api.getFileConfiguration(YmlFileNames.YML_DISCORD_ROLES_FILE_NAME);
        for (Role role : LatchDiscord.getJDA().getGuildById(lmp.Constants.GUILD_ID).getRoles()) {
            discordRoleCfg.set(lmp.Constants.YML_ROLES + "name", role.getName());
            discordRoleCfg.set(lmp.Constants.YML_ROLES + "id", role.getId());
        }
    }

    public static void addRoleFromDiscord(String roleName) {
        Main.luckPerms.getGroupManager().loadAllGroups();
        Main.luckPerms.getGroupManager().loadAllGroups();
        CompletableFuture<Group> futureGroup = Main.luckPerms.getGroupManager().createAndLoadGroup(roleName);
        futureGroup.thenAcceptAsync(group -> {
            Main.luckPerms.getGroupManager().saveGroup(group);
        });
    }

    public static void setDoSpawnersSpawn(Boolean doSpawnersSpawn) throws IOException {
        FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
        configCfg.set("areSpawnersActive", doSpawnersSpawn);
        configCfg.save(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
    }

    public static boolean denyCommandInMultiverseWorlds(@NotNull PlayerCommandPreprocessEvent e) {
        boolean denyCommand = true;
        FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
        List<String> globalAllowedCommandList = configCfg.getStringList("globalAllowedCommandList");
        List<String> oneblockAllowedCommandList = configCfg.getStringList("oneblockAllowedCommandList");
        List<String> skyblockAllowedCommandList = configCfg.getStringList("skyblockAllowedCommandList");

        List<String> allowedCommandList = globalAllowedCommandList;

        if (e.getPlayer().getWorld().getName().contains("OneBlock")){
            allowedCommandList = Stream.concat(allowedCommandList.stream(), oneblockAllowedCommandList.stream()).toList();
        }
        if (e.getPlayer().getWorld().getName().contains("Skyblock")){
            allowedCommandList = Stream.concat(allowedCommandList.stream(), skyblockAllowedCommandList.stream()).toList();
        }
        String command = e.getMessage().toLowerCase();
        for (String commandToCheck : allowedCommandList) {
            if (command.contains(commandToCheck)) {
                denyCommand = false;
                break;
            }
        }
        if (Boolean.TRUE.equals(denyCommand)) {
            e.getPlayer().sendMessage(ChatColor.RED + "You can't use that command in this world.");
        }
        return denyCommand;
    }

    public static boolean denyAdditionalCommandsInOneBlockWorld(@NotNull PlayerCommandPreprocessEvent e) {
        boolean denyCommand = true;
        ArrayList<String> allowedCommandList = new ArrayList<>();
        allowedCommandList.add("/ob join");
        String command = e.getMessage().toLowerCase();
        for (String commandToCheck : allowedCommandList) {
            if (command.contains(commandToCheck)) {
                denyCommand = false;
                break;
            }
        }
        if (Boolean.TRUE.equals(denyCommand)) {
            e.getPlayer().sendMessage(ChatColor.RED + "You can't use that command in this world.");
        }
        return denyCommand;
    }
    public static boolean denyInteractInHardcore(Player p) {
        boolean denyInteract = true;
        FileConfiguration hardcoreCfg = Api.getFileConfiguration(YmlFileNames.YML_HARDCORE_FILE_NAME);
        if (Boolean.FALSE.equals(hardcoreCfg.getBoolean(p.getUniqueId().toString() + ".isAlive"))) {
            p.setGameMode(GameMode.SPECTATOR);
        } else {
            denyInteract = false;
        }
        return denyInteract;
    }

    public static void addPlayerToHardcoreList(PlayerCommandPreprocessEvent e) throws IOException {
        FileConfiguration hardcoreCfg = Api.getFileConfiguration(YmlFileNames.YML_HARDCORE_FILE_NAME);
        if (hardcoreCfg.getString(e.getPlayer().getUniqueId().toString()) == null) {
            Player player = e.getPlayer();
            String uuid = player.getUniqueId().toString();
            hardcoreCfg.set(uuid + ".isAlive", true);
            hardcoreCfg.set(uuid + ".uuid", uuid);
            hardcoreCfg.set(uuid + ".name", player.getName());
            hardcoreCfg.save(Api.getConfigFile(YmlFileNames.YML_HARDCORE_FILE_NAME));
        }
    }

    public static void setHardcorePlayerLocation(PlayerCommandPreprocessEvent e) throws IOException {
        FileConfiguration hardcoreCfg = Api.getFileConfiguration(YmlFileNames.YML_HARDCORE_FILE_NAME);
        String uuid = e.getPlayer().getUniqueId().toString();
        hardcoreCfg.set(uuid + ".lastLocation", e.getPlayer().getLocation());
        hardcoreCfg.save(Api.getConfigFile(YmlFileNames.YML_HARDCORE_FILE_NAME));
    }

    public static void setAnarchyPlayerLocation(PlayerCommandPreprocessEvent e) throws IOException {
        FileConfiguration anarchyCfg = Api.getFileConfiguration(YmlFileNames.YML_ANARCHY_FILE_NAME);
        String uuid = e.getPlayer().getUniqueId().toString();
        anarchyCfg.set(uuid + ".lastLocation", e.getPlayer().getLocation());
        anarchyCfg.save(Api.getConfigFile(YmlFileNames.YML_ANARCHY_FILE_NAME));
    }

    public static void setCreativePlayerLocation(PlayerCommandPreprocessEvent e) throws IOException {
        FileConfiguration creativeCfg = Api.getFileConfiguration(YmlFileNames.YML_CREATIVE_FILE_NAME);
        String uuid = e.getPlayer().getUniqueId().toString();
        creativeCfg.set(uuid + ".lastLocation", e.getPlayer().getLocation());
        creativeCfg.save(Api.getConfigFile(YmlFileNames.YML_CREATIVE_FILE_NAME));
    }

    public static void setClassicPlayerLocation(PlayerCommandPreprocessEvent e) throws IOException {
        FileConfiguration classicCfg = Api.getFileConfiguration(YmlFileNames.YML_CLASSIC_FILE_NAME);
        String uuid = e.getPlayer().getUniqueId().toString();
        classicCfg.set(uuid + ".lastLocation", e.getPlayer().getLocation());
        classicCfg.save(Api.getConfigFile(YmlFileNames.YML_CLASSIC_FILE_NAME));
    }

    public static void setOneBlockPlayerLocation(PlayerCommandPreprocessEvent e) throws IOException {
        FileConfiguration oneBlockCfg = Api.getFileConfiguration(YmlFileNames.YML_ONEBLOCK_FILE_NAME);
        String uuid = e.getPlayer().getUniqueId().toString();
        oneBlockCfg.set(uuid + ".lastLocation", e.getPlayer().getLocation());
        oneBlockCfg.save(Api.getConfigFile(YmlFileNames.YML_ONEBLOCK_FILE_NAME));
    }

    public static void setSkyBlockPlayerLocation(PlayerCommandPreprocessEvent e) throws IOException {
        FileConfiguration skyBlockCfg = Api.getFileConfiguration(YmlFileNames.YML_SKYBLOCK_FILE_NAME);
        String uuid = e.getPlayer().getUniqueId().toString();
        skyBlockCfg.set(uuid + ".lastLocation", e.getPlayer().getLocation());
        skyBlockCfg.save(Api.getConfigFile(YmlFileNames.YML_SKYBLOCK_FILE_NAME));
    }

    public static void teleportHardcorePlayerToLastLocation(Player player) {
        FileConfiguration hardcoreCfg = Api.getFileConfiguration(YmlFileNames.YML_HARDCORE_FILE_NAME);
        String uuid = player.getUniqueId().toString();
        if (hardcoreCfg.getLocation(uuid + ".lastLocation") != null) {
            player.teleport(Objects.requireNonNull(hardcoreCfg.getLocation(uuid + ".lastLocation")));
        }
    }

    public static void teleportClassicPlayerToLastLocation(Player player) {
        FileConfiguration classicCfg = Api.getFileConfiguration(YmlFileNames.YML_CLASSIC_FILE_NAME);
        String uuid = player.getUniqueId().toString();
        if (classicCfg.getLocation(uuid + ".lastLocation") != null) {
            player.teleport(Objects.requireNonNull(classicCfg.getLocation(uuid + ".lastLocation")));
        }
    }

    public static void teleportOneBlockPlayerToLastLocation(Player player) {
        FileConfiguration oneBlockCfg = Api.getFileConfiguration(YmlFileNames.YML_ONEBLOCK_FILE_NAME);
        String uuid = player.getUniqueId().toString();
        if (oneBlockCfg.getLocation(uuid + ".lastLocation") != null) {
            player.teleport(Objects.requireNonNull(oneBlockCfg.getLocation(uuid + ".lastLocation")));
        }
    }

    public static void teleportSkyBlockPlayerToLastLocation(Player player) {
        FileConfiguration skyBlockCfg = Api.getFileConfiguration(YmlFileNames.YML_SKYBLOCK_FILE_NAME);
        String uuid = player.getUniqueId().toString();
        if (skyBlockCfg.getLocation(uuid + ".lastLocation") != null) {
            player.teleport(Objects.requireNonNull(skyBlockCfg.getLocation(uuid + ".lastLocation")));
        }
    }

    public static void teleportAnarchyPlayerToLastLocation(Player player) {
        FileConfiguration anarchyCfg = Api.getFileConfiguration(YmlFileNames.YML_ANARCHY_FILE_NAME);
        String uuid = player.getUniqueId().toString();
        if (anarchyCfg.getLocation(uuid + ".lastLocation") != null) {
            player.teleport(Objects.requireNonNull(anarchyCfg.getLocation(uuid + ".lastLocation")));
        }
    }

    public static void teleportCreativePlayerToLastLocation(Player player) {
        FileConfiguration creativeCfg = Api.getFileConfiguration(YmlFileNames.YML_CREATIVE_FILE_NAME);
        String uuid = player.getUniqueId().toString();
        if (creativeCfg.getLocation(uuid + ".lastLocation") != null) {
            player.teleport(Objects.requireNonNull(creativeCfg.getLocation(uuid + ".lastLocation")));
        } else {
            player.teleport(Objects.requireNonNull(creativeCfg.getLocation("spawnLocation")));
        }
    }

    public static void teleportPlayerToWorldFromHub(PlayerInteractEvent e) throws IOException {
        FileConfiguration hubCfg = Api.getFileConfiguration(YmlFileNames.YML_HUB_FILE_NAME);
        Player player = e.getPlayer();
        String worldName = "";
        if (e.getClickedBlock() != null) {
            for (String world : hubCfg.getKeys(false)) {
                int buttonX = hubCfg.getInt(world + ".x");
                int buttonY = hubCfg.getInt(world + ".y");
                int buttonZ = hubCfg.getInt(world + ".z");
                int buttonClickedX = e.getClickedBlock().getX();
                int buttonClickedY = e.getClickedBlock().getY();
                int buttonClickedZ = e.getClickedBlock().getZ();
                if (buttonX == buttonClickedX && buttonY == buttonClickedY && buttonZ == buttonClickedZ ){
                    worldName = world;
                }
            }
            if (worldName.equalsIgnoreCase("anarchy")){
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
            if (worldName.equalsIgnoreCase("creative")){
                Api.teleportCreativePlayerToLastLocation(player);
            }
            if (worldName.equalsIgnoreCase("oneblock")) {
                FileConfiguration classicCfg = Api.getFileConfiguration(YmlFileNames.YML_ONEBLOCK_FILE_NAME);
                String playerUUID = player.getUniqueId().toString();
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
            }
            if (worldName.equalsIgnoreCase("skyblock")) {
                FileConfiguration skyblockCfg = Api.getFileConfiguration(YmlFileNames.YML_SKYBLOCK_FILE_NAME);
                String playerUUID = player.getUniqueId().toString();
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
            }
            if (worldName.equalsIgnoreCase("classic")) {
                FileConfiguration classicCfg = Api.getFileConfiguration(YmlFileNames.YML_CLASSIC_FILE_NAME);
                String playerUUID = player.getUniqueId().toString();
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
            }
        }
    }

    public static void givePlayerLuckPermPermission(Player player, String permission) {
        // Add the permission
        net.luckperms.api.model.user.User user = Main.luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            user.data().add(Node.builder(permission).build());
            // Now we need to save changes.
            Main.getLuckPerms().getUserManager().saveUser(user);
            player.sendMessage(ChatColor.GREEN + "You were granted " + ChatColor.AQUA + permission + ChatColor.GREEN + " permission.");
        }
    }

    public static void removePlayerLuckPermPermission(Player player, String permission) {
        // Remove the permission
        net.luckperms.api.model.user.User user = Main.luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            if (getPlayerPermissions(player.getUniqueId()).contains(permission)) {
                user.data().remove(Node.builder(permission).build());
                // Now we need to save changes.
                Main.getLuckPerms().getUserManager().saveUser(user);
                player.sendMessage(ChatColor.GREEN + "You were revoked " + ChatColor.AQUA + permission + ChatColor.GREEN + " permission.");
            }
        }
    }

    public static Set<String> getPlayerPermissions(UUID uuid) {
        return Objects.requireNonNull(Main.getLuckPerms().getUserManager().getUser(uuid)).getNodes().stream()
                .filter(NodeType.PERMISSION::matches)
                .map(NodeType.PERMISSION::cast)
                .map(PermissionNode::getPermission)
                .collect(Collectors.toSet());
    }

    public static void setBankSessionToAFK(Boolean isPlayerAFKExempt, Player player) throws IOException {
        FileConfiguration bankCfg = Api.getFileConfiguration(YmlFileNames.YML_BANK_FILE_NAME);
        bankCfg.set(lmp.Constants.YML_PLAYERS + player.getUniqueId() + ".isAFK", isPlayerAFKExempt);
        bankCfg.save(Api.getConfigFile(YmlFileNames.YML_BANK_FILE_NAME));
    }

    public static void givePlayerMoney(String minecraftId, double amount) {
        Api.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(UUID.fromString(minecraftId)), amount);
    }

    public static void takePlayerMoney(String minecraftId, double amount) {
        Api.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(UUID.fromString(minecraftId)), amount);
    }

    public static String getMinecraftIDFromTwitchName(String twitchName) {
        String minecraftID = null;
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        for (String mcID : Objects.requireNonNull(whitelistCfg.getConfigurationSection("players")).getKeys(false)) {
            if (twitchName.equalsIgnoreCase(whitelistCfg.getString(lmp.Constants.YML_PLAYERS + mcID + ".twitchName"))) {
                minecraftID = mcID;
            }
        }
        return minecraftID;
    }
    public static void transferBackpackContents() throws IOException {
        FileConfiguration backpackCfg = Api.getFileConfiguration(YmlFileNames.YML_BACK_PACK_FILE_NAME);
        for (String mcID : backpackCfg.getKeys(false)) {
            File playerBPFile = new File(getMainPlugin().getDataFolder() + "/playerBackpacks/", mcID + ".yml");
            FileConfiguration playerBPCfg = YamlConfiguration.loadConfiguration(playerBPFile);
            if (backpackCfg.isSet(mcID + ".size")) {
                playerBPCfg.set(mcID + ".size", backpackCfg.getInt(mcID + ".size"));
            }
            if (backpackCfg.isSet(mcID + ".isOpen")) {
                playerBPCfg.set(mcID + ".isOpen", backpackCfg.getBoolean(mcID + ".isOpen"));
            }
            if (backpackCfg.isSet(mcID + ".name")) {
                playerBPCfg.set(mcID + ".name", backpackCfg.getString(mcID + ".name"));
            }
            if (backpackCfg.isSet(mcID + ".slotTickets")) {
                playerBPCfg.set(mcID + ".slotTickets", backpackCfg.getInt(mcID + ".slotTickets"));
            }
            if (backpackCfg.isSet(mcID + ".slots")) {
                for (String slotNumber : Objects.requireNonNull(backpackCfg.getConfigurationSection(mcID + ".slots")).getKeys(false)) {
                    playerBPCfg.set(mcID + ".slots." + slotNumber, backpackCfg.getItemStack(mcID + ".slots." + slotNumber));
                }
            }
            playerBPCfg.save(playerBPFile);
        }
    }

    public static void removeIllegalItemsFromBackpacks() throws IOException {
        int shulkerCount = 0;
        int totalItemCount = 0;
        int paperCount = 0;
        int enchantedBookCount = 0;
        ArrayList<String> linkedPlayerIDList = Api.getAllMinecraftIDOfLinkedPlayers();
        ArrayList<String> bpContainingMoneyOrders = new ArrayList<>();
        ArrayList<String> bpContainingMendingBooks = new ArrayList<>();
        ArrayList<String> playersWithSlotTickets = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(getMainPlugin().getDataFolder() + "/playerBackpacks/").listFiles())) {
            int slotCount = 0;
            boolean doesPlayerHaveBPSlotTickets = false;
            String mcID = file.getName().split(".yml")[0];
            FileConfiguration backpackCfg = YamlConfiguration.loadConfiguration(file);
            if (backpackCfg.isSet(mcID + ".slotTickets")){
                doesPlayerHaveBPSlotTickets = true;
                playersWithSlotTickets.add(backpackCfg.getString(mcID + ".name"));
            }
            if (backpackCfg.isSet(mcID + ".slots")) {
                for (String slotNumber : Objects.requireNonNull(backpackCfg.getConfigurationSection(mcID + ".slots")).getKeys(false)) {
                    slotCount++;
                    ItemStack is = backpackCfg.getItemStack(mcID + ".slots." + slotNumber);
                    assert is != null;
                    if (is.getType().equals(Material.PAPER)){
                        if (is.getItemMeta() != null && is.getItemMeta().hasLore()){
                            if (Objects.requireNonNull(is.getItemMeta().getLore()).get(0).contains("MoneyOrder")){
//                                Main.log.info(backpackCfg.getString(mcID + ".name") + "--- Money Order --- Default Slot: " + slotNumber);
                                bpContainingMoneyOrders.add(backpackCfg.getString(mcID + ".name"));
                                paperCount++;
                            }
                        }
                    }
                    if (is.getItemMeta() != null && is.getItemMeta() instanceof EnchantmentStorageMeta &&  is.getType().equals(Material.ENCHANTED_BOOK)){
                        EnchantmentStorageMeta enchantmentMeta = (EnchantmentStorageMeta) is.getItemMeta();
                        if (enchantmentMeta.getStoredEnchants().toString().toLowerCase().contains("mending")){
//                            Main.log.info(backpackCfg.getString(mcID + ".name") + "--- Mending Book --- Default Slot: " + slotNumber);
                            bpContainingMendingBooks.add(backpackCfg.getString(mcID + ".name"));
                        }
                        enchantedBookCount++;
                    }
                    if (is.getType().toString().contains("SHULKER")){
                        shulkerCount++;
                        totalItemCount++;
                        if(is.getItemMeta() instanceof BlockStateMeta) {
                            BlockStateMeta im = (BlockStateMeta) is.getItemMeta();
                            ShulkerBox sb = (ShulkerBox) im.getBlockState();
                            for (ItemStack boxIS : sb.getInventory()) {
                                if (boxIS != null) {
                                    if (boxIS.getType().equals(Material.PAPER)) {
                                        if (is.getItemMeta() != null && is.getItemMeta().hasLore()){
                                            if (Objects.requireNonNull(is.getItemMeta().getLore()).get(0).contains("MoneyOrder")){
                                                bpContainingMoneyOrders.add(backpackCfg.getString(mcID + ".name"));
//                                                Main.log.info(backpackCfg.getString(mcID + ".name") + "--- Money Order in Shulker --- Default Slot: " + slotNumber);
                                                paperCount++;
                                            }
                                        }
                                    }
                                    if (boxIS.getItemMeta() != null && boxIS.getType().equals(Material.ENCHANTED_BOOK)){
                                        EnchantmentStorageMeta enchantmentMeta = (EnchantmentStorageMeta) boxIS.getItemMeta();
                                        if (enchantmentMeta.getStoredEnchants().toString().toLowerCase().contains("mending")){
                                            bpContainingMendingBooks.add(backpackCfg.getString(mcID + ".name"));
//                                            Main.log.info(backpackCfg.getString(mcID + ".name") + "--- Mending Book in Shulker --- Default Slot: " + slotNumber);
                                        }
                                        enchantedBookCount++;
                                    }
                                }
                            }
                        }
                    }
                    totalItemCount++;
                }
            }
            if (slotCount < 10){
                backpackCfg.set(mcID + ".size", 9);
            }
            if (backpackCfg.getInt(mcID + ".size") > 18){
                backpackCfg.set(mcID + ".size", 18);
            }
            if (Boolean.FALSE.equals(doesPlayerHaveBPSlotTickets)){
                file.delete();
            }
            if (!linkedPlayerIDList.contains(mcID)) {
                file.delete();
            }
            backpackCfg.save(file);
            Main.log.info(backpackCfg.getString(mcID + ".name") + "has " + slotCount + " backpack slots and a backpack size of " + backpackCfg.getInt(mcID + ".size") + " and DoesOwnBackPackSlots: " + doesPlayerHaveBPSlotTickets);
        }
//        Main.log.info("Shulker Count: " + shulkerCount);
//        Main.log.info("Paper Count: " + paperCount);
//        Main.log.info("Total Item Count: " + totalItemCount);
//        Main.log.info("Enchanted Book Count: " + enchantedBookCount);
//        Object[] moArr = Arrays.stream(bpContainingMoneyOrders.toArray()).distinct().toArray(Object[]::new);
//        Object[] mendArr = Arrays.stream(bpContainingMendingBooks.toArray()).distinct().toArray(Object[]::new);
//        Main.log.info("Players with MoneyOrders in their backpack: " + Arrays.toString(moArr));
//        Main.log.info("Players with Mending Books in their backpack: " + Arrays.toString(mendArr));
//        Main.log.info("Players with Back Pack Slots: " + playersWithSlotTickets);
    }
}
