package lmp.donationBot;

import lmp.Main;
import lmp.api.Api;
import lmp.constants.Constants;
import lmp.constants.YmlFileNames;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Donation {
    public static void getDonations() {
        String[] statuses = {"Completed", "Reversed", "Refunded"};

        CompletableFuture<io.donatebot.api.Donation[]> future = Main.dbClient.getNewDonations(statuses);
// Non-blocking Example
        CompletableFuture.runAsync(() -> {
            FileConfiguration donationsCfg = Api.getFileConfiguration(YmlFileNames.YML_DONATION_FILE_NAME);
            // Array of Donation objects
            List<io.donatebot.api.Donation> list = null;
            try {
                list = Arrays.asList(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            List<io.donatebot.api.Donation> sorted = list.stream()
                    .sorted(Comparator.comparing(io.donatebot.api.Donation::getDate))
                    .collect(Collectors.toList());
            if (donationsCfg.getInt("numberOfDonations") < sorted.size()) {
                int previousDonationCount = donationsCfg.getInt("numberOfDonations");
                int currentDonationCount = sorted.size();
                //for (Donation donation : sorted) {
//                            String discordID = "123";
//                            if (sorted.get(i).getSellerCustoms().get("What is your Discord User ID?") != null && !sorted.get(i).getSellerCustoms().get("What is your Discord User ID?").isEmpty()){
//                                discordID = sorted.get(i).getSellerCustoms().get("What is your Discord User ID?");
//                            }
                int donationCountDifference = currentDonationCount - previousDonationCount;
                for (int i = 0; i < donationCountDifference; i++) {
                    io.donatebot.api.Donation donation = sorted.get(previousDonationCount);
                    String transactionId = donation.getTransactionID();
                    String buyerID = donation.getBuyerID();
                    String buyerEmail = donation.getBuyerEmail();
                    String productID = donation.getProductID();
                    String price = donation.getPrice();
                    Boolean isRecurring = donation.getRecurring();
                    Date purchaseDate = donation.getDate();
                    donationsCfg.set(lmp.Constants.YML_DONATIONS + transactionId + ".transactionID", transactionId);
                    donationsCfg.set(lmp.Constants.YML_DONATIONS + transactionId + ".buyerID", buyerID);
                    donationsCfg.set(lmp.Constants.YML_DONATIONS + transactionId + ".buyerEmail", buyerEmail);
                    donationsCfg.set(lmp.Constants.YML_DONATIONS + transactionId + ".roleID", donation.getRoleID());
                    donationsCfg.set(lmp.Constants.YML_DONATIONS + transactionId + ".productID", productID);
                    donationsCfg.set(lmp.Constants.YML_DONATIONS + transactionId + ".price", price);
                    donationsCfg.set(lmp.Constants.YML_DONATIONS + transactionId + ".isRecurring", isRecurring);
                    donationsCfg.set(lmp.Constants.YML_DONATIONS + transactionId + ".date", purchaseDate);
                    Player latch = null;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getUniqueId().toString().equalsIgnoreCase(Constants.SERVER_OWNER_MINECRAFT_ID)) {
                            latch = player;
                        }
                    }
                    assert latch != null;
                    if (Bukkit.getServer().getPlayer(Constants.SERVER_OWNER_MINECRAFT_ID) != null) {
                        if (productID.equals(lmp.Constants.SPAWN_MOB_PRODUCT_ID) && Double.parseDouble(price) > 4.99) {
                            EntityType mobToSpawn = EntityType.CREEPER;
                            Player finalLatch = latch;
                            if (donation.getSellerCustoms().get("Enter the Mob Name") != null) {
                                mobToSpawn = EntityType.valueOf(donation.getSellerCustoms().get("Enter the Mob Name").toUpperCase());
                            }
                            Location latchLocation = finalLatch.getLocation();
                            EntityType finalMobToSpawn = mobToSpawn;
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Objects.requireNonNull(latchLocation.getWorld()).spawnEntity(latchLocation, finalMobToSpawn));
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> finalLatch.sendMessage("creepy bois"));
                        }
                        if (productID.equals(lmp.Constants.KILL_LATCH_PRODUCT_ID) && Double.parseDouble(price) > 9.99) {
                            Player finalLatch = latch;
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> finalLatch.setHealth(0));
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> finalLatch.sendMessage("You ded"));
                        }
                        if (productID.equals(lmp.Constants.PLAY_CREEPER_SOUND_PRODUCT_ID) && Double.parseDouble(price) > 1.49) {
                            Player finalLatch = latch;
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> finalLatch.playSound(finalLatch.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1, 0));
                        }
                        if (productID.equals(lmp.Constants.BLINDNESS_EFFECT_PRODUCT_ID) && Double.parseDouble(price) > 1.49) {
                            Player finalLatch = latch;
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> finalLatch.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6000, 1)));
                        }
                    }
                    previousDonationCount++;
                }
                donationsCfg.set("numberOfDonations", sorted.size());
                try {
                    donationsCfg.save(Api.getConfigFile(YmlFileNames.YML_DONATION_FILE_NAME));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
