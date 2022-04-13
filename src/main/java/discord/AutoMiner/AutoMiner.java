package discord.AutoMiner;

import discord.Constants;
import discord.Main;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class AutoMiner {
    private static final File autoMinerFile = Main.getConfigFile(Constants.YML_AUTO_MINER_FILE_NAME);
    private static final FileConfiguration autoMinerCfg = Main.getFileConfiguration(autoMinerFile);
    public static void setChestLocation(PlayerInteractEvent e) throws IOException {
        ItemStack itemInPlayersHand = e.getPlayer().getInventory().getItemInMainHand();
        if (itemInPlayersHand.getType().toString().equalsIgnoreCase("STICK") && itemInPlayersHand.getEnchantments().toString().contains("fire") && Objects.requireNonNull(e.getClickedBlock()).getType().toString().equalsIgnoreCase("CHEST")){
            Player player = e.getPlayer();
            autoMinerCfg.set(Constants.YML_PLAYERS + player.getName() + ".chestLocation.world", player.getWorld().getName());
            autoMinerCfg.set(Constants.YML_PLAYERS + player.getName() + ".chestLocation.x", e.getClickedBlock().getLocation().getBlockX());
            autoMinerCfg.set(Constants.YML_PLAYERS + player.getName() + ".chestLocation.y", e.getClickedBlock().getLocation().getBlockY());
            autoMinerCfg.set(Constants.YML_PLAYERS + player.getName() + ".chestLocation.z", e.getClickedBlock().getLocation().getBlockZ());
            autoMinerCfg.save(autoMinerFile);
        }
    }

    public static void mineBlocks(BlockPlaceEvent e){
        if (e.getBlockPlaced().getType().toString().equalsIgnoreCase("NETHERITE_BLOCK")){
            Player player = e.getPlayer();

            int mineDistanceX = autoMinerCfg.getInt(Constants.YML_PLAYERS + player.getName() + ".mine.x");
            int mineDistanceY = autoMinerCfg.getInt(Constants.YML_PLAYERS + player.getName() + ".mine.y");
            int mineDistanceZ = autoMinerCfg.getInt(Constants.YML_PLAYERS + player.getName() + ".mine.z");
            int chestX = autoMinerCfg.getInt(Constants.YML_PLAYERS + player.getName() + ".chestLocation.x");
            int chestY = autoMinerCfg.getInt(Constants.YML_PLAYERS + player.getName() + ".chestLocation.y");
            int chestZ = autoMinerCfg.getInt(Constants.YML_PLAYERS + player.getName() + ".chestLocation.z");
            World world = Bukkit.getServer().getWorld(Objects.requireNonNull(autoMinerCfg.getString(Constants.YML_PLAYERS + player.getName() + ".chestLocation.world")));
            int mineX = e.getBlock().getX();
            int mineY = e.getBlock().getY();
            int mineZ = e.getBlock().getZ();
            double counter = 1.0;
            if (Boolean.TRUE.equals(isMiningAreaValid(mineDistanceX, mineDistanceY, mineDistanceZ))){
                for (double y = 1.0; y <= setAbsoluteValue(mineDistanceX); y++){
                    for (double x = 1.0; x <= setAbsoluteValue(mineDistanceY); x++){
                        for (double z = 1.0; z <= setAbsoluteValue(mineDistanceZ); z++){
                            Location dig = new Location(world, setDistance(e.getBlock().getX(), mineX ,x), setDistance(e.getBlock().getY(), mineY, y), setDistance(e.getBlock().getZ(), mineZ, z));
                            System.out.println("dig: " + dig);
                            Location chestLocation = new Location(Bukkit.getWorld("world"), chestX, chestY, chestZ);
                            Chest chest = (Chest) chestLocation.getBlock().getState();
                            ItemStack im = new ItemStack(dig.getBlock().getType(), 1);
                            Block blockToDelete = dig.getBlock();
                            blockToDelete.setType(Material.AIR);
                            Inventory inv = chest.getInventory();
                            inv.addItem(im);
                        }
                    }
                    counter = counter + 1.0;
                }
            } else {
                player.sendMessage(ChatColor.RED + "Mine area too large. Max size of mine is 4096 blocks. Please reduce mine size and try again.");
            }
        }
    }



    public static double setDistance(int mineCoordinate, int distance, double loopCount){
        double digDistance = 0;
        if (distance < 0){
            digDistance = mineCoordinate - loopCount;
        } else {
            digDistance = mineCoordinate + loopCount;
        }
        return digDistance;
    }

    public static boolean isMiningAreaValid(int X, int Y, int Z){
        boolean isValid = false;
        int x = Math.abs(X);
        int y = Math.abs(Y);
        int z = Math.abs(Z);
        int area = x * y * z;
        if (area < 4097){
            isValid = true;
        }
        return isValid;
    }

    public static int setAbsoluteValue(int coordinate){
        if (coordinate < 0) {
            coordinate *= -1;
        }
        return coordinate;
    }
}
