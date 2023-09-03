package lmp.listeners.playerBreakBlockEvents;

import lmp.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.sql.*;
import java.util.Objects;

public class AddBlockBrokenToDB implements Listener {
    public AddBlockBrokenToDB(Main plugin) { plugin.getServer().getPluginManager().registerEvents(this, plugin);}

    @EventHandler
    private void addBlockBrokenToDB(BlockBreakEvent e){
        String connectionUrl  = "jdbc:sqlserver://DESKTOP-CQKVEGP:1433;databaseName=lmp;trustServerCertificate=true;encrypt=true;username=minecraft;password=password";
        Connection connection = null;
        if (e.getBlock().getType() != Material.AIR && e.getBlock().getType() == Material.OBSIDIAN && e.getBlock().getWorld().getName().contains("world")) {
            try {
                connection = DriverManager.getConnection(connectionUrl);
                String insertsql = " insert into block_break (minecraft_id, block_type, world_id, world_name, x, y, z, timestamp)"
                        + " values (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStmt = connection.prepareStatement(insertsql);
                Location location = e.getBlock().getLocation();
                preparedStmt.setString(1, e.getPlayer().getUniqueId().toString());
                preparedStmt.setString(2, e.getBlock().getType().toString());
                preparedStmt.setString(3, Objects.requireNonNull(location.getWorld()).getUID().toString());
                preparedStmt.setString(4, location.getWorld().getName());
                preparedStmt.setDouble(5, location.getBlockX());
                preparedStmt.setDouble(6, location.getBlockY());
                preparedStmt.setDouble(7, location.getBlockZ());
                preparedStmt.setTimestamp(8, new Timestamp(new java.util.Date().getTime()));
                preparedStmt.execute();
                connection.close();

            } catch (SQLException err) {
                throw new RuntimeException(err);
            }
        }
    }
}
