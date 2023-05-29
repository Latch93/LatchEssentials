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

import static lmp.api.Api.getMainPlugin;

public class DonationClaimRewards {
    public static void createDonationClaimFiles() throws IOException {
        for (String minecraftID : Api.getMinecraftIDOfLinkedPlayersInDiscord()) {
            File donationClaimFile = new File(getMainPlugin().getDataFolder() + "/donationClaimFiles/", minecraftID + ".yml");
            FileConfiguration donationCfg = Api.getFileConfigurationFromFile(donationClaimFile);
            String players = "players.";
            List<String> itemsToGive = new ArrayList<>();
            if (donationCfg.get(players + minecraftID + ".uuid") == null) {
                donationCfg.set(players + minecraftID + ".itemsToGive", itemsToGive);
                donationCfg.set(players + minecraftID + ".uuid", minecraftID);
                donationCfg.set(players + minecraftID + ".playerName", Api.getMinecraftNameFromMinecraftId(minecraftID));
                donationCfg.save(donationClaimFile);
                Main.log.info("New player added to the donationClaim file. Name: " + Api.getMinecraftNameFromMinecraftId(minecraftID));
            }
        }
    }

    public static void createNewPlayerToDonationFile(Player player) throws IOException {
        String playerUUID = player.getUniqueId().toString();
        String playerName = player.getName();
        File donationClaimFile = new File(getMainPlugin().getDataFolder() + "/donationClaimFiles/", playerUUID.toString() + ".yml");
        FileConfiguration donationCfg = Api.getFileConfigurationFromFile(donationClaimFile);
        String players = "players.";
        List<String> itemsToGive = new ArrayList<>();
        if (donationCfg.get(players + playerUUID + ".uuid") == null) {
            donationCfg.set(players + playerUUID + ".itemsToGive", itemsToGive);
            donationCfg.set(players + playerUUID + ".uuid", playerUUID);
            donationCfg.set(players + playerUUID + ".playerName", playerName);
            donationCfg.save(Api.getConfigFile(YmlFileNames.YML_USER_DONATION_REWARD_FILE_NAME));
            donationCfg.save(donationClaimFile);
            Main.log.info("New player added to the donationClaim file. Name: " + playerName);
        }
    }


    public static void addItemToClaimToAll(ItemStack itemToGive) throws IOException {
        for (File file : Objects.requireNonNull(new File(getMainPlugin().getDataFolder() + "/donationClaimFiles").listFiles())) {
            String[] splitArr = file.getName().split("\\.");
            String playerUUID = splitArr[0];
            File donationClaimFile = new File(getMainPlugin().getDataFolder() + "/donationClaimFiles/", playerUUID.toString() + ".yml");
            FileConfiguration donationCfg = Api.getFileConfigurationFromFile(donationClaimFile);
            String players = "players.";
            List<String> itemsToGive = new ArrayList<>();
            if (!donationCfg.getStringList(players + playerUUID + ".itemsToGive").isEmpty()) {
                itemsToGive = donationCfg.getStringList(players + playerUUID + ".itemsToGive");
            }
            itemsToGive.add(serializeItemStack(itemToGive));
            donationCfg.set(players + playerUUID + ".itemsToGive", itemsToGive);
            donationCfg.save(donationClaimFile);
        }
    }

    public static void addItemToClaimToPlayer(String playerUUID, ItemStack itemToGive) throws IOException {
            File donationClaimFile = new File(getMainPlugin().getDataFolder() + "/donationClaimFiles/", playerUUID + ".yml");
            FileConfiguration donationCfg = Api.getFileConfigurationFromFile(donationClaimFile);
            String players = "players.";
            List<String> itemsToGive = new ArrayList<>();
            if (!donationCfg.getStringList(players + playerUUID + ".itemsToGive").isEmpty()) {
                itemsToGive = donationCfg.getStringList(players + playerUUID + ".itemsToGive");
            }
            itemsToGive.add(serializeItemStack(itemToGive));
            donationCfg.set(players + playerUUID + ".itemsToGive", itemsToGive);
            donationCfg.save(donationClaimFile);
    }


    public static void claimItems(Player player) throws IOException, ClassNotFoundException {
        String playerUUID = player.getUniqueId().toString();
        String players = "players.";
        File donationClaimFile = new File(getMainPlugin().getDataFolder() + "/donationClaimFiles/", playerUUID.toString() + ".yml");
        FileConfiguration donationCfg = Api.getFileConfigurationFromFile(donationClaimFile);
        List<String> itemsToGive = new ArrayList<>();
        if (!donationCfg.getStringList(players + playerUUID + ".itemsToGive").isEmpty()) {
            itemsToGive = donationCfg.getStringList(players + playerUUID + ".itemsToGive");
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
                player.sendMessage(ChatColor.GREEN + "You were given " + ChatColor.GOLD + item.getAmount() + " " + item.getType() + ChatColor.GREEN + " | Drop Coords: " + ChatColor.GOLD + dropLocation.getBlockX() + " / " + dropLocation.getBlockY() + " / " + dropLocation.getBlockZ());
            }
        }
        donationCfg.set(players + playerUUID + ".itemsToGive", null);
        donationCfg.save(donationClaimFile);
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

