package discord;

import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTeleport {
    public static void randomTp(Player player){ ;
        World world = player.getWorld();
        OfflinePlayer olp = Bukkit.getOfflinePlayer(player.getUniqueId());
        double playerBalance = Main.getEconomy().getBalance(olp);
        DecimalFormat df = new DecimalFormat("0.00");
        if (playerBalance >= 2500){
            if (player.getWorld().getName().equalsIgnoreCase("world")){
                player.sendMessage(ChatColor.AQUA + "Trying to find a safe spot to teleport. Please wait...");
                int largestLocation = 2999999;
                int smallestLocation = -2999999;
                int highestLocation = 200;
                int lowestLocation = 50;
                int x =  ThreadLocalRandom.current().nextInt(smallestLocation, largestLocation + 1);
                int y = ThreadLocalRandom.current().nextInt(lowestLocation, highestLocation + 1);
                int z = ThreadLocalRandom.current().nextInt(smallestLocation, largestLocation + 1);
                Location randomLocation = new Location(player.getWorld(), x, y, z);
                Block blockToTeleporttTo = world.getHighestBlockAt(randomLocation);
                Location finalLocation = null;
                if (blockToTeleporttTo.getType().isSolid()){
                    finalLocation = blockToTeleporttTo.getLocation();
                    finalLocation.setY(finalLocation.getY()+2.0);
                }
                if (finalLocation != null){
                    Main.getEconomy().withdrawPlayer(olp, 2500);
                    world.getChunkAt(finalLocation).load();
                    player.sendMessage(ChatColor.GREEN + "Random Teleport successful!!! Your new balance is " + ChatColor.GOLD + "$" + df.format(Main.getEconomy().getBalance(olp)));
                    player.teleport(finalLocation);
                    TextChannel chatChannel = LatchDiscord.jda.getTextChannelById(Constants.MINECRAFT_CHAT_CHANNEL_ID);
                    String biome = finalLocation.getBlock().getBiome().getKey().getKey();
                    if (biome.contains("_")){
                        String[] arr = biome.split("_");
                        biome = WordUtils.capitalizeFully(arr[0]) + " " + WordUtils.capitalizeFully(arr[1]) + " Spawner";
                    }
                    biome = biome.replace(" Spawner", "");
                    assert chatChannel != null;
                    chatChannel.sendMessage(player.getName() + " was randomly teleported to a " + WordUtils.capitalizeFully(biome) + " biome.").queue();
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Unable to send you to a safe random spot. Please try again");
                }
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "You need at least $2500 to teleport to a random location.");
        }
    }
}
