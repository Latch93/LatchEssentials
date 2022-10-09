package lmp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DonationClaimRewards {
    public static void createDonationUserFile(String uuid) throws IOException {
        FileConfiguration userDonationCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_USER_DONATION_REWARD_FILE_NAME));
        File playerDataFile = new File("plugins/Essentials/userdata", uuid + ".yml");
        FileConfiguration conf = Api.getFileConfiguration(playerDataFile);

        String players = "players.";
        List<String> itemsToGive = new ArrayList<>();
        if (userDonationCfg.get(players + uuid + ".uuid") == null) {
            userDonationCfg.set(players + uuid + ".itemsToGive", itemsToGive);
            userDonationCfg.set(players + uuid + ".uuid", uuid);
            userDonationCfg.set(players + uuid + ".playerName", conf.getString("last-account-name"));
            userDonationCfg.save(Api.getConfigFile(Constants.YML_USER_DONATION_REWARD_FILE_NAME));
            System.out.println(uuid);
        }
    }

    public static void addItemToClaim(String itemToGive) throws IOException {
        FileConfiguration userDonationCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_USER_DONATION_REWARD_FILE_NAME));
        for (File file : Objects.requireNonNull(new File("plugins/Essentials/userdata").listFiles())) {
            FileConfiguration conf = Api.getFileConfiguration(file);
            String[] temp = file.getName().split(".yml");
            String userUUID = temp[0];
            String players = "players.";
            List<String> itemsToGive = new ArrayList<>();
            if (!userDonationCfg.getStringList(players + userUUID + ".itemsToGive").isEmpty()){
                itemsToGive = userDonationCfg.getStringList(players + userUUID + ".itemsToGive");
            }
            itemsToGive.add(itemToGive);
            userDonationCfg.set(players + userUUID + ".itemsToGive", itemsToGive);
            userDonationCfg.save(Api.getConfigFile(Constants.YML_USER_DONATION_REWARD_FILE_NAME));
        }
    }

    public static void claimItems(Player player) throws IOException {
        FileConfiguration userDonationCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_USER_DONATION_REWARD_FILE_NAME));
        String userUUID = player.getUniqueId().toString();
        String players = "players.";
        List<String> itemsToGive = new ArrayList<>();
        if (!userDonationCfg.getStringList(players + userUUID + ".itemsToGive").isEmpty()){
            itemsToGive = userDonationCfg.getStringList(players + userUUID + ".itemsToGive");
        } else{
            player.sendMessage(ChatColor.RED + "You don't have any items to claim.");
        }
        if (!itemsToGive.isEmpty()){
            for (String itemToGive : itemsToGive){
                ItemStack item = new ItemStack(Material.valueOf(itemToGive));
                World world = player.getWorld();
                Location dropLocation = player.getLocation();
                world.dropItem(dropLocation,item);
                player.sendMessage(ChatColor.GREEN + "You were given a " + ChatColor.GOLD + item.getType().toString());
            }
        }
        userDonationCfg.set(players + userUUID + ".itemsToGive", null);
        userDonationCfg.save(Api.getConfigFile(Constants.YML_USER_DONATION_REWARD_FILE_NAME));
    }
}

