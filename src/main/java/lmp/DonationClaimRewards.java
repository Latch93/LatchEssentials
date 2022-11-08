package lmp;

import lmp.api.Api;
import lmp.constants.YmlFileNames;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DonationClaimRewards {
    public static void addNewPlayerToDonationFile(Player player) throws IOException {

        String playerUUID = player.getUniqueId().toString();
        String playerName = player.getName();
        FileConfiguration userDonationCfg = Api.getFileConfiguration(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME);
        String players = "players.";
        List<String> itemsToGive = new ArrayList<>();
        if (userDonationCfg.get(players + playerUUID + ".uuid") == null) {
            userDonationCfg.set(players + playerUUID + ".itemsToGive", itemsToGive);
            userDonationCfg.set(players + playerUUID + ".uuid", playerUUID);
            userDonationCfg.set(players + playerUUID + ".playerName", playerName);
            userDonationCfg.save(Api.getConfigFile(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME));
            Main.log.info("New player added to the donationClaim file. Name: " + playerName);
        }
    }

    public static void createDonationUserFile() throws IOException {


        FileConfiguration userDonationCfg = Api.getFileConfiguration(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME);
        FileConfiguration whitelistCfg = Api.getFileConfiguration(YmlFileNames.YML_WHITELIST_FILE_NAME);
        for (String user : Objects.requireNonNull(whitelistCfg.getConfigurationSection("players")).getKeys(false)) {
            String players = "players.";
            List<String> itemsToGive = new ArrayList<>();
            if (whitelistCfg.getBoolean(players + user + ".isPlayerInDiscord")){
                userDonationCfg.set(players + user + ".itemsToGive", itemsToGive);
                userDonationCfg.set(players + user + ".minecraftID", user);
                userDonationCfg.set(players + user + ".playerName", whitelistCfg.getString(players + user + ".minecraftName"));
                userDonationCfg.set(players + user + ".discordID", whitelistCfg.getString(players + user + ".discordId"));
                Main.log.info("New player added to the donationClaim file. Name: " + whitelistCfg.getString(players + user + ".minecraftName"));
            }
        }
        userDonationCfg.save(Api.getConfigFile(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME));
    }

    public static void addItemToClaim(ItemStack itemToGive) throws IOException {
        FileConfiguration userDonationCfg = Api.getFileConfiguration(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME);
        for (File file : Objects.requireNonNull(new File("plugins/Essentials/userdata").listFiles())) {
            FileConfiguration conf = Api.getFileConfiguration(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME);
            String[] temp = file.getName().split(".yml");
            String userUUID = temp[0];
            String players = "players.";
            List<String> itemsToGive = new ArrayList<>();
            if (!userDonationCfg.getStringList(players + userUUID + ".itemsToGive").isEmpty()) {
                itemsToGive = userDonationCfg.getStringList(players + userUUID + ".itemsToGive");
            }
            itemsToGive.add(serializeItemStack(itemToGive));
            userDonationCfg.set(players + userUUID + ".itemsToGive", itemsToGive);
        }
        userDonationCfg.save(Api.getConfigFile(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME));
    }

    public static void claimItems(Player player) throws IOException, ClassNotFoundException {
        FileConfiguration userDonationCfg = Api.getFileConfiguration(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME);
        String userUUID = player.getUniqueId().toString();
        String players = "players.";
        List<String> itemsToGive = new ArrayList<>();
        if (!userDonationCfg.getStringList(players + userUUID + ".itemsToGive").isEmpty()) {
            itemsToGive = userDonationCfg.getStringList(players + userUUID + ".itemsToGive");
        } else {
            player.sendMessage(ChatColor.RED + "You don't have any items to claim.");
        }
        if (!itemsToGive.isEmpty()) {
            for (String itemToGive : itemsToGive) {
                ItemStack item = deserializeItemStack(itemToGive);
                World world = player.getWorld();
                Location dropLocation = player.getLocation();
                assert item != null;
                world.dropItem(dropLocation, item);
                player.sendMessage(ChatColor.GREEN + "You were given " + ChatColor.GOLD + item.getAmount() + " " + item.getType());
            }
        }
        userDonationCfg.set(players + userUUID + ".itemsToGive", null);
        userDonationCfg.save(Api.getConfigFile(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME));
    }

    private static String serializeItemStack(ItemStack item) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private static ItemStack deserializeItemStack(String item) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(item));
        try {
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            dataInput.close();
            return (ItemStack) dataInput.readObject();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }
}

