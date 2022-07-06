package lmp;

import io.netty.util.Constant;
import lmp.LatchTwitchBot.LatchTwitchBot;
import lmp.LatchTwitchBot.LatchTwitchBotRunnable;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.util.Tristate;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class Api {
    private static Economy econ = null;
    private static Permission perm = null;
    public static LatchTwitchBot twitchBot;
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
        for (String colorCode : colorCodes){
            if (senderName != null){
                senderName = senderName.replace(colorCode, "");
            }
            senderMessage = senderMessage.replace(colorCode, "");
        }
        if (senderMessage.toLowerCase().contains("@everyone") || senderMessage.toLowerCase().contains("@here")){
            senderMessage = "I tried to @ everyone or @ here. I shouldn't do that, but I did";
        }
        if (senderName != null) {
            finalMessage = senderName + " » " + senderMessage;
        } else {
            finalMessage = senderMessage;
        }
        return finalMessage;
    }

    public static OfflinePlayer getOfflinePlayerFromPlayer(Player player){
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

    public static Economy getEconomy(){
        return econ;
    }

    public static void setEconomy(Economy economy) {
        econ = economy;
    }

    public static File getConfigFile(String fileName){
        return new File(getMainPlugin().getDataFolder(), fileName + ".yml");
    }

    public static FileConfiguration getFileConfiguration(File file){
        return YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration loadConfig(String fileName) {
        return YamlConfiguration.loadConfiguration(getConfigFile(fileName));
    }

    public static void messageInConsole(String message){
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static Main getMainPlugin(){
        return getPlugin(Main.class);
    }

    public static void cancelEventsInPreviousSeason(String worldName, String player, BlockBreakEvent blockBreakEvent, BlockPlaceEvent blockPlaceEvent, InventoryClickEvent inventoryClickEvent, PlayerPortalEvent playerPortalEvent){
        if (worldName.equalsIgnoreCase("season1") || worldName.equalsIgnoreCase("season4")) {
            if (player == null || !player.equalsIgnoreCase("Latch93")){
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

    public static boolean isPlayerInvisible(String uuid){
        File superVanishFile = new File("plugins/SuperVanish", "data.yml");
        return YamlConfiguration.loadConfiguration(superVanishFile).getStringList("InvisiblePlayers").contains(uuid);
    }

    public static String getTwitchUsername(String minecraftUsername){
        String twitchUsername = null;
        FileConfiguration twitchCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_TWITCH_FILE_NAME));
        if (twitchCfg.isSet("players")){
            for(String user : twitchCfg.getConfigurationSection("players").getKeys(false)) {
                if (twitchCfg.getString(Constants.YML_PLAYERS + user + ".minecraftUsername").equalsIgnoreCase(minecraftUsername)){
                    twitchUsername = twitchCfg.getString(Constants.YML_PLAYERS + user + ".twitchUsername");
                }
            }
        }
        return twitchUsername;
    }

    public static String getPlayerTime(String minecraftId){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String time = "";
        FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
        try {
            sdf.setTimeZone(TimeZone.getTimeZone(whitelistCfg.getString("players." + minecraftId + ".ip-info.timezoneName")));
            time = sdf.format(calendar.getTime());
        } catch (NullPointerException ignore){
        }
        return time;
    }

    public static void checkPlayerMemberStatus(Player player){
        net.luckperms.api.model.user.User user = Main.luckPerms.getUserManager().getUser(player.getUniqueId());
        assert user != null;
        if (!"default".equalsIgnoreCase(user.getPrimaryGroup()) && Boolean.TRUE.equals(Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getBoolean("showJoinMessage"))){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME)).getString("joinMessage")))));
        }
        if (user.data().contains(InheritanceNode.builder("default").value(true).build(), NodeEqualityPredicate.EXACT ).equals(Tristate.TRUE)){
            player.sendMessage(ChatColor.RED + "You need to link your Discord and Minecraft accounts.\n" +
                    "Go to Discord and type the following into the General Channel -> " + ChatColor.AQUA + "!link\n" +
                    ChatColor.RED + "Then copy and paste the command into Minecraft chat and click enter.");
        }
    }

    public static boolean cancelJrModEvent(UUID uniqueId){
        boolean isInvisibleJrMod = false;
        if (Api.isPlayerInvisible(uniqueId.toString())){
            net.luckperms.api.model.user.User user = Main.luckPerms.getUserManager().getUser(uniqueId);
            assert user != null;
            if (user.data().contains(InheritanceNode.builder("jr-mod").value(true).build(), NodeEqualityPredicate.EXACT ).equals(Tristate.TRUE)){
                isInvisibleJrMod = true;
                Objects.requireNonNull(Bukkit.getPlayer(uniqueId)).sendMessage(ChatColor.RED + "Jr Mods are not allowed to Break or Place blocks while invisible.");
            }
        }
        return isInvisibleJrMod;
    }

    public static void updateUserInfo(Player player) throws IOException {
        FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
        if (whitelistCfg.isSet(String.valueOf(player.getUniqueId()))){
            whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".minecraftName", player.getName());
            String discordId = whitelistCfg.getString(Constants.YML_PLAYERS + player.getUniqueId() + ".discordId");
            assert discordId != null;
            if (LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getMemberById(discordId) != null){
                whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".discordName", Objects.requireNonNull(Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID)).getMemberById(discordId)).getUser().getName());
                whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".isPlayerInDiscord", true);
                if (!player.getName().equalsIgnoreCase(whitelistCfg.getString(Constants.YML_PLAYERS + player.getUniqueId() + ".minecraftName"))){
                    whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".minecraftName", player.getName());
                }
            } else {
                whitelistCfg.set(Constants.YML_PLAYERS + player.getUniqueId() + ".isPlayerInDiscord", false);
                player.kickPlayer("You not in Latch's Discord.");
            }
        }
        whitelistCfg.save(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
    }

    public static void stopTwitchBot(List<LatchTwitchBotRunnable> twitchBotList, Player player){
        Iterator<LatchTwitchBotRunnable> iter = twitchBotList.iterator();
        while(iter.hasNext()){
            LatchTwitchBotRunnable runBot = iter.next();
            if (runBot.getMinecraftName().equalsIgnoreCase(player.getName())){
                runBot.getTwitchClient().close();
                player.sendMessage(ChatColor.GREEN + "Your TwitchBot has been " + ChatColor.RED + "terminated.");
                Api.messageInConsole(ChatColor.RED + "Terminated " + ChatColor.GOLD + player.getName() + "'s " + ChatColor.RED + "TwitchBot.");
                iter.remove();
            }
        }
    }

    public static void stopAllTwitchBots(List<LatchTwitchBotRunnable> twitchBotList){
        Iterator<LatchTwitchBotRunnable> iter = twitchBotList.iterator();
        while(iter.hasNext()){
            LatchTwitchBotRunnable runBot = iter.next();
            runBot.getTwitchClient().close();
            Api.messageInConsole(ChatColor.RED + "Terminated " + ChatColor.GOLD + "ALL " + ChatColor.RED + "TwitchBots.");
            iter.remove();
    }
        Api.messageInConsole(ChatColor.RED + "Terminated " + ChatColor.GOLD + "all " + ChatColor.RED + "TwitchBots.");
    }

    public static LatchTwitchBot getPlayerTwitchBot(List<LatchTwitchBotRunnable> twitchBotList, String minecraftName){
        Iterator<LatchTwitchBotRunnable> iter = twitchBotList.iterator();
        LatchTwitchBot bot = null;
        while(iter.hasNext()){
            LatchTwitchBotRunnable iterBot = iter.next();
            if(iterBot.getMinecraftName().equalsIgnoreCase(minecraftName)){
                bot = iterBot.getBot();
            }
        }
        return bot;
    }


    public static String getChannelNameFromId(String channelID){
        String channelName = null;
        List<TextChannel> textChannelList = LatchDiscord.jda.getTextChannels();
        for (TextChannel textChannel : textChannelList){
            if (channelID.equalsIgnoreCase(textChannel.getId())){
                channelName = textChannel.getName();
            }
        }
        return channelName;
    }

    public static String getPlayerNameFromId(String channelID){
        String playerName = null;
        List<User> userList = LatchDiscord.jda.getUsers();
        for (User user : userList){
            if (channelID.equalsIgnoreCase(user.getId())){
                playerName = user.getName();
            }
        }
        return playerName;
    }

    public static int getPlayerPing(Player player){
        return player.getPing();
    }

    public static void addPlayerToPermissionGroup(String minecraftId, String groupName){
        InheritanceNode node = InheritanceNode.builder(groupName).value(true).build();
        @NonNull CompletableFuture<net.luckperms.api.model.user.User> userFuture = Main.getLuckPerms().getUserManager().loadUser(UUID.fromString(minecraftId));
        userFuture.thenAcceptAsync(user -> {
            user.data().add(node);
            Main.luckPerms.getUserManager().saveUser(user);
        });
    }

    public static void removePlayerFromPermissionGroup(String minecraftId, String groupName){
        InheritanceNode node = InheritanceNode.builder(groupName).value(true).build();
        @NonNull CompletableFuture<net.luckperms.api.model.user.User> userFuture = Main.getLuckPerms().getUserManager().loadUser(UUID.fromString(minecraftId));
        userFuture.thenAcceptAsync(user -> {
            user.data().remove(node);
            Main.luckPerms.getUserManager().saveUser(user);
        });
    }

    public static String getTwitchChannelName(String minecraftUsername){
        FileConfiguration twitchCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_TWITCH_FILE_NAME));
        String twitchUsername = null;
        for(String user : twitchCfg.getConfigurationSection("players").getKeys(false)) {
            if (twitchCfg.getString(Constants.YML_PLAYERS + user + ".minecraftUsername").equalsIgnoreCase(minecraftUsername)){
                twitchUsername = twitchCfg.getString(Constants.YML_PLAYERS + user + ".twitchUsername");
            }
        }
        return "#" + twitchUsername;
    }

    public static boolean arePlayersOnline(){
        return !Bukkit.getOnlinePlayers().isEmpty();
    }

    public static Player getOnlinePlayer(){
        Player overworldPlayer = null;
        Bukkit.getOnlinePlayers();
        for (Player player : Bukkit.getOnlinePlayers()){
            overworldPlayer = player;
            break;
        }
        return overworldPlayer;
    }

    public static void denyBackIntoBossArena(PlayerCommandPreprocessEvent e){
        Player player = e.getPlayer();
        File playerDataFile = new File("plugins/Essentials/userdata", player.getUniqueId() + ".yml");
        FileConfiguration playerDataCfg = Api.getFileConfiguration(playerDataFile);
        Location lastLocation = new Location(Bukkit.getWorld("world"), playerDataCfg.getDouble("lastlocation.x"), playerDataCfg.getDouble("lastlocation.y"), playerDataCfg.getDouble("lastlocation.z"));
        if (player.getWorld().equals(Bukkit.getWorld("world"))){
            if (lastLocation.getBlockX() >= -3060 && lastLocation.getBlockX() <= -2972) {
                if (lastLocation.getBlockY() >= 68 && lastLocation.getBlockY() <= 77) {
                    if (lastLocation.getBlockZ() >=  32841 && lastLocation.getBlockZ() <= 32953) {
                        e.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You can't use /back into the arena. Use /warp coliseum to collect your things.");
                    }
                }
            }
        }
    }

    public static void placeBlockLog(BlockPlaceEvent event) throws IOException {
        if ((event.getBlock().getLocation().getWorld().getName().equalsIgnoreCase("world") ) && (event.getBlock().getType().equals(Material.TNT) || event.getBlock().getType().equals(Material.END_CRYSTAL))){
            FileConfiguration blockPlaceLogCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_BLOCK_PLACE_LOG_FILE_NAME));
            Date date = new Date();
            Player player = event.getPlayer();
            blockPlaceLogCfg.set(player.getUniqueId().toString() + ".playerName", event.getPlayer().getName());
            blockPlaceLogCfg.set(player.getUniqueId().toString() + "." + date + ".blockType", event.getBlock().getType().toString());
            blockPlaceLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.x", event.getBlock().getLocation().getBlockX());
            blockPlaceLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.y", event.getBlock().getLocation().getBlockY());
            blockPlaceLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.z", event.getBlock().getLocation().getBlockZ());
            blockPlaceLogCfg.save(Api.getConfigFile(Constants.YML_BLOCK_PLACE_LOG_FILE_NAME));
        }
    }

    public static void blockBreakLog(BlockBreakEvent event) throws IOException {
        if (event.getBlock().getType().toString().contains("ORE") || event.getBlock().getType().equals(Material.ANCIENT_DEBRIS)) {
            if (!event.getPlayer().hasPermission("group.mod")) {
                if (event.getBlock().getType().equals(Material.ANCIENT_DEBRIS)) {
                    LatchDiscord.getJDA().getTextChannelById(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID).sendMessage("MC Name: " + event.getPlayer().getName() + " | DC Name: " + Api.getDiscordNameFromMCid(event.getPlayer().getUniqueId().toString()) + " | Broke an Ancient Debris block at [" + event.getBlock().getLocation().getBlockX() + ", " + event.getBlock().getLocation().getBlockY() + ", " + event.getBlock().getLocation().getBlockZ() + "]").queue();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.hasPermission("group.jr-mod")) {
                            p.sendMessage("[" + ChatColor.YELLOW + "XRAY CHECK" + ChatColor.WHITE + "] - " + ChatColor.RED + event.getPlayer().getName() + ChatColor.WHITE + " » " + ChatColor.YELLOW + "Just mined 1 Ancient Debris.");
                        }
                    }
                }
            }
            if (!event.getPlayer().hasPermission("group.mod")){
                if (event.getBlock().getType().equals(Material.DIAMOND_ORE) || event.getBlock().getType().equals(Material.DEEPSLATE_DIAMOND_ORE)){
                    LatchDiscord.getJDA().getTextChannelById(Constants.TEST_CHANNEL_ID).sendMessage("MC Name: " + event.getPlayer().getName() + " | DC Name: " + Api.getDiscordNameFromMCid(event.getPlayer().getUniqueId().toString()) + " | Broke 1 " + event.getBlock().getType().toString() + " block at [" + event.getBlock().getLocation().getBlockX() + ", "  + event.getBlock().getLocation().getBlockY() + ", " + event.getBlock().getLocation().getBlockZ() + "]").queue();
                }
            }
            FileConfiguration blockBreakLogCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_BLOCK_BREAK_LOG_FILE_NAME));
            Date date = new Date();
            Player player = event.getPlayer();
            blockBreakLogCfg.set(player.getUniqueId().toString() + ".playerName", event.getPlayer().getName());
            blockBreakLogCfg.set(player.getUniqueId().toString() + "." + date + ".blockType", event.getBlock().getType().toString());
            blockBreakLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.x", event.getBlock().getLocation().getBlockX());
            blockBreakLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.y", event.getBlock().getLocation().getBlockY());
            blockBreakLogCfg.set(player.getUniqueId().toString() + "." + date + ".location.z", event.getBlock().getLocation().getBlockZ());
            blockBreakLogCfg.save(Api.getConfigFile(Constants.YML_BLOCK_BREAK_LOG_FILE_NAME));
        }
    }

    public static void combineChestplateAndElytra(PrepareAnvilEvent e){
        ItemStack leftSideItem = null;
        ItemStack rightSideItem = null;
        if (e.getInventory().getItem(0) != null && e.getInventory().getItem(1) != null) {
            leftSideItem = e.getInventory().getItem(0);
            rightSideItem = e.getInventory().getItem(1);
            if (rightSideItem != null){
                assert leftSideItem != null;
                if (leftSideItem.getType().equals(Material.NETHERITE_CHESTPLATE) && rightSideItem.getType().equals(Material.ELYTRA)) {
                    leftSideItem.getEnchantments();
                    rightSideItem.getEnchantments();
                    Map<Enchantment, Integer> chestplateEnchantments = leftSideItem.getEnchantments();
                    Map<Enchantment, Integer> elytraEnchantments = rightSideItem.getEnchantments();
                    Map<Enchantment,Integer> finalEnchantments = new HashMap<>();
                    for (Map.Entry<Enchantment, Integer> chestplateEnchant : chestplateEnchantments.entrySet()) {
                        for (Map.Entry<Enchantment, Integer> elytraEnchant : elytraEnchantments.entrySet()) {
                            if (chestplateEnchant.getKey().equals(elytraEnchant.getKey())){
                                if (chestplateEnchant.getValue() > elytraEnchant.getValue()){
                                    finalEnchantments.put(chestplateEnchant.getKey(), chestplateEnchant.getValue());
                                } else {
                                    finalEnchantments.put(elytraEnchant.getKey(), elytraEnchant.getValue());
                                }
                            }
                            if (!chestplateEnchantments.containsKey(elytraEnchant.getKey())){
                                finalEnchantments.put(elytraEnchant.getKey(), elytraEnchant.getValue());
                            }
                        }
                        if (!elytraEnchantments.containsKey(chestplateEnchant.getKey())){
                            finalEnchantments.put(chestplateEnchant.getKey(), chestplateEnchant.getValue());
                        }
                    }

                    ItemStack elytra = new ItemStack(Material.ELYTRA, 1);
                    elytra.addUnsafeEnchantments(finalEnchantments);
                    Bukkit.getServer().getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> e.getInventory().setRepairCost(50));
                    e.setResult(elytra);
                }
            }
        }
    }

    public static void denyOpenChestDuringBossBattle(PlayerInteractEvent e){
        FileConfiguration bossCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_BOSS_FILE_NAME));
        double chestLeftX = bossCfg.getDouble("chestLeftX");
        double chestLeftY = bossCfg.getDouble("chestLeftY");
        double chestLeftZ = bossCfg.getDouble("chestLeftZ");
        double chestRightX = bossCfg.getDouble("chestRightX");
        double chestRightY = bossCfg.getDouble("chestRightY");
        double chestRightZ = bossCfg.getDouble("chestRightZ");
        Location challengerChestLeftLocation = new Location(Bukkit.getWorld("world"), chestLeftX, chestLeftY, chestLeftZ);
        Location challengerChestRightLocation = new Location(Bukkit.getWorld("world"), chestRightX, chestRightY, chestRightZ);

        if (e.getClickedBlock() != null){
            if (Objects.requireNonNull(e.getClickedBlock()).getLocation().equals(challengerChestLeftLocation) || e.getClickedBlock().getLocation().equals(challengerChestRightLocation)) {
                if (!e.getPlayer().hasPermission("group.jr-mod") && !e.getPlayer().getName().equalsIgnoreCase(bossCfg.getString("playerName"))) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.RED + "This isn't your chest. It belongs to " + ChatColor.GOLD + bossCfg.getString("playerName"));
                }
            }
        }
    }

    public static String getMinecraftIdFromDCid(String discordId){
        FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
        String minecraftId = "";
        for (String mcID : whitelistCfg.getConfigurationSection(Constants.YML_PLAYERS).getKeys(false)) {
            if (discordId.equalsIgnoreCase(whitelistCfg.getString(Constants.YML_PLAYERS + mcID + ".discordId"))){
                minecraftId = whitelistCfg.getString(Constants.YML_PLAYERS + mcID + ".minecraftId");
            }
        }
        return minecraftId;
    }

    public static String getDiscordIdFromMCid(String minecraftID){
        FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
        String discordID = "";
        for (String mcID : whitelistCfg.getConfigurationSection(Constants.YML_PLAYERS).getKeys(false)) {
            if (minecraftID.equalsIgnoreCase(mcID)){
                discordID = whitelistCfg.getString(Constants.YML_PLAYERS + mcID + ".discordId");
            }
        }
        return discordID;
    }

    public static String getMinecraftIdFromMinecraftName(String minecraftName){
        FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
        String minecraftID = "";
        for (String mcID : whitelistCfg.getConfigurationSection(Constants.YML_PLAYERS).getKeys(false)) {
            if (minecraftName.equalsIgnoreCase(whitelistCfg.getString(Constants.YML_PLAYERS + mcID + ".minecraftName"))){
                minecraftID = whitelistCfg.getString(Constants.YML_PLAYERS + mcID + ".minecraftId");
            }
        }
        return minecraftID;
    }

    public static String getDiscordNameFromMCid(String minecraftID){
        return Objects.requireNonNull(Objects.requireNonNull(LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID)).getMemberById(Api.getDiscordIdFromMCid(minecraftID))).getUser().getName();
    }

    public static void setIsPlayerInDiscord() throws IOException {
        FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
        for (String minecraftId : whitelistCfg.getConfigurationSection(Constants.YML_PLAYERS).getKeys(false)) {
            String discordId = whitelistCfg.getString(Constants.YML_PLAYERS + minecraftId + ".discordId");
            if (LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getMemberById(discordId) != null){
                whitelistCfg.set(Constants.YML_PLAYERS + minecraftId + ".isPlayerInDiscord", true);
            } else {
                whitelistCfg.set(Constants.YML_PLAYERS + minecraftId + ".isPlayerInDiscord", false);
            }
        }
        whitelistCfg.save(Api.getConfigFile(Constants.YML_WHITELIST_FILE_NAME));
    }

    public static void updateDiscordRolesFile(){
        FileConfiguration discordRoleCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_DISCORD_ROLES_FILE_NAME));
        for (Role role : LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getRoles()) {
            discordRoleCfg.set(Constants.YML_ROLES + "name", role.getName());
            discordRoleCfg.set(Constants.YML_ROLES + "id", role.getId());
        }
    }

    public static void addRoleFromDiscord(String roleName){
        Main.luckPerms.getGroupManager().loadAllGroups();
        CompletableFuture<Group> futureGroup = Main.luckPerms.getGroupManager().createAndLoadGroup(roleName);
        futureGroup.thenAcceptAsync(group -> {
            Main.luckPerms.getGroupManager().saveGroup(group);
        });
    }

    public static void setDoSpawnersSpawn(Boolean doSpawnersSpawn) throws IOException {
        FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
        configCfg.set("areSpawnersActive", doSpawnersSpawn);
        configCfg.save(Api.getConfigFile(Constants.YML_CONFIG_FILE_NAME));
    }
}
