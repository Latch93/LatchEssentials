package lmp;

import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.PeriodType;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTeleport {
    public static void randomTp(Player player) throws IOException, ExecutionException, InterruptedException { ;
        World world = player.getWorld();
        OfflinePlayer olp = Bukkit.getOfflinePlayer(player.getUniqueId());
        double playerBalance = Api.getEconomy().getBalance(olp);
        DecimalFormat df = new DecimalFormat("0.00");
        FileConfiguration configCfg = Api.getFileConfiguration(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
        int overworldRTPCost = configCfg.getInt("overworldRTPCost");
        int theEndRTPCost = configCfg.getInt("theEndRTPCost");
        int overworldRadius = configCfg.getInt("overworldRTPRadius");
        int theEndRadius = configCfg.getInt("theEndRTPRadius");
        if (player.getWorld().getName().equalsIgnoreCase("world")){
            if (playerBalance >= overworldRTPCost) {
                teleportPlayerRandomly(player, overworldRTPCost, overworldRadius);
            } else {
                player.sendMessage(ChatColor.RED + "You need at least $" + overworldRTPCost + " to teleport to a random location.");
            }
        }
        if (player.getWorld().getName().equalsIgnoreCase("world_the_end")){
            if (playerBalance >= theEndRTPCost) {
                teleportPlayerRandomly(player, theEndRTPCost, theEndRadius);
            } else {
                player.sendMessage(ChatColor.RED + "You need at least $" + theEndRTPCost + " to teleport to a random location.");
            }
        }
    }

    public static void teleportPlayerRandomly(Player player, int teleportCost, int teleportRadius) throws ExecutionException, InterruptedException, IOException {
        if (Api.doesPlayerHavePermission(player.getUniqueId().toString(), "member")){
            Date date = new Date();
            long timeMilli = date.getTime();
            boolean canPlayerTeleport = false;
            FileConfiguration whitelistCfg = Api.getFileConfiguration(Api.getConfigFile(YmlFileNames.YML_WHITELIST_FILE_NAME));
            String uuid = player.getUniqueId().toString();
            if (whitelistCfg.isSet(Constants.YML_PLAYERS + uuid + ".lastRTP")){
                long timePlayerLastRTP = whitelistCfg.getLong(Constants.YML_PLAYERS + uuid + ".lastRTP");
                long timeSinceLastRTP = timeMilli - timePlayerLastRTP;
                int rtpHourTimer = Api.getFileConfiguration(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME)).getInt("rtpHourTimer");
                long totalTime = 1000 * (rtpHourTimer * 3600L);
                if (timeSinceLastRTP >= totalTime){
                    canPlayerTeleport = true;
                } else {
                    org.joda.time.LocalDateTime currentLocalDateTime = new org.joda.time.LocalDateTime();
                    DateTime timeUntilCanRTP = new DateTime(timePlayerLastRTP, DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Chicago")));
                    timeUntilCanRTP = timeUntilCanRTP.plusHours(rtpHourTimer);
                    org.joda.time.Period p = new org.joda.time.Period(currentLocalDateTime,org.joda.time.LocalDateTime.parse(StringUtils.substring(timeUntilCanRTP.toString(), 0, timeUntilCanRTP.toString().length() - 6)), PeriodType.yearMonthDayTime());
                    int days = p.getDays();
                    int hours = p.getHours();
                    int minutes = p.getMinutes();
                    int seconds = p.getSeconds();
                    int hoursOfTheDay = hours % 24;
                    int minutesOfTheHour = minutes % 60;
                    int secondsOfTheMinute = seconds % 60;
                    if (hours > 0){
                        player.sendMessage(ChatColor.YELLOW + "You have to wait to teleport again. Please try again in " + ChatColor.AQUA + hoursOfTheDay  + " hours | " + minutesOfTheHour + " minute(s) | " + secondsOfTheMinute + " second(s)");
                    } else if (minutes > 0) {
                        player.sendMessage(ChatColor.YELLOW + "You have to wait to teleport again. Please try again in " + ChatColor.AQUA + minutesOfTheHour + " minute(s) | " + secondsOfTheMinute + " second(s)");
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "You have to wait to teleport again. Please try again in " + ChatColor.AQUA + secondsOfTheMinute + " second(s)");

                    }
                }
            } else {
                canPlayerTeleport = true;
            }
            if (Boolean.TRUE.equals(canPlayerTeleport)){
                World world = player.getWorld();
                int smallestLocation = -1 * teleportRadius;
                int highestLocation = 125;
                int lowestLocation = 50;
                int x =  ThreadLocalRandom.current().nextInt(smallestLocation, teleportRadius + 1);
                int y = ThreadLocalRandom.current().nextInt(lowestLocation, highestLocation + 1);
                int z = ThreadLocalRandom.current().nextInt(smallestLocation, teleportRadius + 1);
                Location randomLocation = new Location(player.getWorld(), x, y, z);
                Block blockToTeleporttTo = world.getHighestBlockAt(randomLocation);
                Location finalLocation = null;
                if (blockToTeleporttTo.getType().isSolid()){
                    finalLocation = blockToTeleporttTo.getLocation();
                    finalLocation.setY(finalLocation.getY()+2.0);
                }
                if (finalLocation != null){
                    Api.getEconomy().withdrawPlayer(player, teleportCost);
                    world.getChunkAt(finalLocation).load();
                    player.teleport(finalLocation);
                    String biome = finalLocation.getBlock().getBiome().getKey().getKey();
                    if (biome.contains("_")){
                        String[] arr = biome.split("_");
                        biome = WordUtils.capitalizeFully(arr[0]) + " " + WordUtils.capitalizeFully(arr[1]) + " Spawner";
                    }
                    biome = biome.replace(" Spawner", "");
                    TextChannel chatChannel = LatchDiscord.jda.getTextChannelById(Constants.MINECRAFT_CHAT_CHANNEL_ID);
                    assert chatChannel != null;
                    String worldName = player.getWorld().getName();
                    if (worldName.equalsIgnoreCase("world")){
                        chatChannel.sendMessage(player.getName() + " was randomly teleported to a " + WordUtils.capitalizeFully(biome) + " biome.").queue();
                        Bukkit.broadcastMessage(player.getName() + " was randomly teleported to a " + WordUtils.capitalizeFully(biome) + " biome.");
                    } else if (worldName.equalsIgnoreCase("world_the_end")) {
                        chatChannel.sendMessage(player.getName() + " was randomly teleported to a place in The End").queue();
                        Bukkit.broadcastMessage(player.getName() + " was randomly teleported to a place in The End.");
                    }
                    whitelistCfg.set(Constants.YML_PLAYERS +  uuid + ".lastRTP", timeMilli);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Unable to send you to a safe random spot. Please try again");
                }
            }
            whitelistCfg.save(Api.getConfigFile(YmlFileNames.YML_WHITELIST_FILE_NAME));

        } else {
            player.sendMessage(ChatColor.RED + "You must link your discord account before you can use this command");
        }
    }
}
