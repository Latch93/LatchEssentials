package lmp;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.UUID;

public class XPFarm {

    public static void teleportPlayerToXPFarm(PlayerInteractEvent e) throws IOException {
        FileConfiguration xpFarmCfg = Api.getFileConfiguration(Api.getConfigFile("xpFarm"));
        double xpFarmWarpButtonX = xpFarmCfg.getDouble("xpFarmWarpButtonX");
        double xpFarmWarpButtonY = xpFarmCfg.getDouble("xpFarmWarpButtonY");
        double xpFarmWarpButtonZ = xpFarmCfg.getDouble("xpFarmWarpButtonZ");
        Location buttonToWarpToFarmLocation = new Location(Bukkit.getWorld("world"), xpFarmWarpButtonX, xpFarmWarpButtonY, xpFarmWarpButtonZ);
        if (e.getClickedBlock() != null && e.getClickedBlock().getLocation().equals(buttonToWarpToFarmLocation)){
            if (Boolean.TRUE.equals(xpFarmCfg.getBoolean("isFarmInUse"))){
                e.getPlayer().sendMessage(ChatColor.YELLOW + "XP Farm is currently being used by " + ChatColor.AQUA + xpFarmCfg.get("playerNameUsingFarm"));
                e.getPlayer().sendMessage(ChatColor.YELLOW + "You have to wait for them to leave before you can use the farm.");
            } else {
                Player player = e.getPlayer();
                OfflinePlayer olp = Bukkit.getOfflinePlayer(player.getUniqueId());
                double farmCost = xpFarmCfg.getDouble("cost");
                UUID farmOwnerMCID = UUID.fromString(Objects.requireNonNull(xpFarmCfg.getString("xpFarmOwnerMinecraftID")));
                String farmOwnerDCID = xpFarmCfg.getString("xpFarmOwnerDiscordID");
                if (farmCost > Api.getEconomy().getBalance(olp)) {
                    player.sendMessage(ChatColor.RED + "You need at least " + ChatColor.GOLD + "$" + farmCost + ChatColor.RED + " to warp to this Spider XP Farm.");
                } else {
                    double xpFarmLocationX = xpFarmCfg.getDouble("xpFarmLocationX");
                    double xpFarmLocationY = xpFarmCfg.getDouble("xpFarmLocationY");
                    double xpFarmLocationZ = xpFarmCfg.getDouble("xpFarmLocationZ");
                    File playerDataFile = new File("plugins/Essentials/userdata", player.getUniqueId() + ".yml");
                    FileConfiguration playerDataCfg = Api.getFileConfiguration(playerDataFile);
                    playerDataCfg.set("lastlocation.x", xpFarmLocationX);
                    playerDataCfg.set("lastlocation.y", xpFarmLocationY);
                    playerDataCfg.set("lastlocation.z", xpFarmLocationZ);
                    playerDataCfg.save(playerDataFile);
                    Location lastLocation = new Location(Bukkit.getWorld("world"), playerDataCfg.getDouble("lastlocation.x"), playerDataCfg.getDouble("lastlocation.y"), playerDataCfg.getDouble("lastlocation.z"));

                    Location xpFarmLocation = new Location(Bukkit.getWorld("world"), xpFarmLocationX, xpFarmLocationY, xpFarmLocationZ);
                    player.teleport(xpFarmLocation);
                    Api.getEconomy().withdrawPlayer(olp, farmCost);
                    Api.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(farmOwnerMCID), farmCost);
                    assert farmOwnerDCID != null;
                    Guild guild = LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID);
                    assert guild != null;
                    net.dv8tion.jda.api.entities.Member member = guild.getMemberById(farmOwnerDCID);
                    assert member != null;
                    User xpFarmOwnerUser = member.getUser();
                    xpFarmOwnerUser.openPrivateChannel().queue((privateChannel -> privateChannel.sendMessage(player.getName() + " has warped to use your xp farm!").queue()));
                    xpFarmCfg.set("playerNameUsingFarm", player.getName());
                    xpFarmCfg.set("playerIDUsingFarm", player.getUniqueId().toString());
                    xpFarmCfg.set("isFarmInUse", true);
                    DateTime dateOne = new DateTime();
                    xpFarmCfg.set("timeStarted", dateOne.getMillis());
                    xpFarmCfg.save(Api.getConfigFile("xpFarm"));
                }
            }
        }

    }

    public static void teleportPlayerToSpawn(PlayerInteractEvent e) throws IOException {
        FileConfiguration xpFarmCfg = Api.getFileConfiguration(Api.getConfigFile("xpFarm"));
        double spawnButtonX = xpFarmCfg.getDouble("spawnButtonX");
        double spawnButtonY = xpFarmCfg.getDouble("spawnButtonY");
        double spawnButtonZ = xpFarmCfg.getDouble("spawnButtonZ");
        Location buttonToWarpToSpawnLocation = new Location(Bukkit.getWorld("world"), spawnButtonX, spawnButtonY, spawnButtonZ);
        if (e.getClickedBlock() != null && e.getClickedBlock().getLocation().equals(buttonToWarpToSpawnLocation)) {
            double spawnX = xpFarmCfg.getDouble("spawnX");
            double spawnY = xpFarmCfg.getDouble("spawnY");
            double spawnZ = xpFarmCfg.getDouble("spawnZ");
            Location spawnLocation = new Location(Bukkit.getWorld("world"), spawnX, spawnY, spawnZ);
            e.getPlayer().teleport(spawnLocation);
            xpFarmCfg.set("isFarmInUse", false);
            xpFarmCfg.save(Api.getConfigFile("xpFarm"));
        }
    }

}
