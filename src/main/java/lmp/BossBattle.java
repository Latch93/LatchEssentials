package lmp;

import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

public class BossBattle {
    public static void startBossBattle(PlayerInteractEvent event){
        FileConfiguration bossCfg = Api.getFileConfiguration(Api.getConfigFile(YmlFileNames.YML_BOSS_FILE_NAME));
        Player challenger = event.getPlayer();
        if (Boolean.FALSE.equals(bossCfg.getBoolean("bossEnabled"))){
            boolean canSpawn = true;
            String bossType = "";

            for(String boss : Objects.requireNonNull(bossCfg.getConfigurationSection("bosses")).getKeys(false)) {
                double bossButtonXFromFile = bossCfg.getDouble("bosses." + boss + ".buttonX");
                double bossButtonYFromFile = bossCfg.getDouble("bosses." + boss + ".buttonY");
                double bossButtonZFromFile = bossCfg.getDouble("bosses." + boss + ".buttonZ");

                Location buttonLocationFromFile = new Location(Bukkit.getWorld("world"), bossButtonXFromFile, bossButtonYFromFile, bossButtonZFromFile);
                if (Objects.requireNonNull(event.getClickedBlock()).getLocation().equals(buttonLocationFromFile)){
                    bossType = boss;
                }
            }
            if (!bossType.equals("")){
                if (bossCfg.getDouble("bosses." + bossType + ".cost") > Api.getEconomy().getBalance(Api.getOfflinePlayerFromPlayer(challenger))){
                    challenger.sendMessage(ChatColor.RED + "You need at least " + ChatColor.GOLD + "$" + bossCfg.getDouble("bosses." + bossType + ".cost") + ChatColor.RED + " to fight this boss.");
                } else {
                    if (bossCfg.isSet("bosses." + bossType + ".illegalItems")){
                        for (ItemStack is : challenger.getInventory()){
                            if (is != null && (Objects.requireNonNull(bossCfg.getList("bosses." + bossType + ".illegalItems")).contains(is.getType().toString()))){
                                canSpawn = false;
                            }
                        }
                    }
                    if (Boolean.TRUE.equals(canSpawn)){
                        double chestRightX = bossCfg.getDouble("chestRightX");
                        double chestRightY = bossCfg.getDouble("chestRightY");
                        double chestRightZ = bossCfg.getDouble("chestRightZ");
                        Location challengerChestRightLocation = new Location(Bukkit.getWorld("world"), chestRightX, chestRightY, chestRightZ);
                        Chest rightSideChest = (Chest) challengerChestRightLocation.getBlock().getState();
                        boolean isChestEmpty = true;
                        for (int i = 0; i < rightSideChest.getInventory().getStorageContents().length; i++ ){
                            if (rightSideChest.getInventory().getStorageContents()[i] != null){
                                isChestEmpty = false;
                                break;
                            }
                        }
                        if (Boolean.TRUE.equals(isChestEmpty)){
                            String bossName = bossCfg.getString("bosses." + bossType + ".bossName");
                            Api.getEconomy().withdrawPlayer(Api.getOfflinePlayerFromPlayer(challenger), bossCfg.getDouble("bosses." + bossType + ".cost"));
                            challenger.sendMessage(ChatColor.GREEN + "You paid " + ChatColor.GOLD + "$" + bossCfg.getDouble("bosses." + bossType + ".cost") + ChatColor.GREEN + " to fight " + ChatColor.AQUA + bossName);
                            double bossX = bossCfg.getDouble("bossX");
                            double bossY = bossCfg.getDouble("bossY");
                            double bossZ = bossCfg.getDouble("bossZ");
                            double playerX = bossCfg.getDouble("playerX");
                            double playerY = bossCfg.getDouble("playerY");
                            double playerZ = bossCfg.getDouble("playerZ");
                            Location bossSpawnLocation = new Location(Bukkit.getWorld("world"), bossX, bossY, bossZ);
                            Location playerSpawnLocation = new Location(Bukkit.getWorld("world"), playerX, playerY, playerZ);
                            challenger.teleport(playerSpawnLocation);
                            Monster monster = (Monster) challenger.getWorld().spawnEntity(bossSpawnLocation, EntityType.valueOf(bossCfg.getString("bosses." + bossType + ".bossType")));
                            monster.setCustomName(ChatColor.GOLD + bossName);
                            monster.setTarget(challenger);
                            monster.getUniqueId();
                            if (bossCfg.isSet("bosses." + bossType + ".fireResist") && Boolean.TRUE.equals(bossCfg.getBoolean("bosses." + bossType + ".fireResist"))){
                                monster.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100000, 10));
                            }
                            if (bossCfg.isSet("bosses." + bossType + ".invisible") && Boolean.TRUE.equals(bossCfg.getBoolean("bosses." + bossType + ".invisible"))){
                                monster.setInvisible(true);
                            }
                            bossCfg.set("bossEnabled", true);
                            bossCfg.set("playerName", challenger.getName());
                            bossCfg.set("bossUUID", monster.getUniqueId().toString());
                            bossCfg.set("bossKey", bossType);
                            Objects.requireNonNull(monster.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(bossCfg.getDouble("bosses." + bossType + ".bossAttackDamage"));
                            Objects.requireNonNull(monster.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(bossCfg.getDouble("bosses." + bossType + ".bossMaxHealth"));
                            Objects.requireNonNull(monster.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)).setBaseValue(bossCfg.getDouble("bosses." + bossType + ".bossFollowRange"));
                            Objects.requireNonNull(monster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(bossCfg.getDouble("bosses." + bossType + ".bossMovementSpeed"));
                            Objects.requireNonNull(monster.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)).setBaseValue(bossCfg.getDouble("bosses." + bossType + ".bossArmorToughness"));
                            Objects.requireNonNull(monster.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK)).setBaseValue(bossCfg.getDouble("bosses." + bossType + ".bossAttackKnockback"));
                            Objects.requireNonNull(monster.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(bossCfg.getDouble("bosses." + bossType + ".bossKnockbackResistance"));

                            monster.setHealth(bossCfg.getDouble("bosses." + bossType + ".bossMaxHealth"));
                            monster.setTarget(challenger);
                            monster.setCanPickupItems(false);
                            BigDecimal a = BigDecimal.valueOf(monster.getHealth());
                            BigDecimal b = new BigDecimal(100);
                            Bukkit.broadcastMessage(ChatColor.GOLD + challenger.getName() + ChatColor.GREEN + " started a fight with " + bossName + " " +
                                    "Started \nHealth: " + ChatColor.AQUA + (a.multiply(b)).setScale(0, RoundingMode.UP));
                            try {
                                bossCfg.save(Api.getConfigFile(YmlFileNames.YML_BOSS_FILE_NAME));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (bossCfg.isSet("bosses." + bossType + ".bossHelmet")){
                                Objects.requireNonNull(monster.getEquipment()).setHelmet(new ItemStack(Objects.requireNonNull(bossCfg.getItemStack("bosses." + bossType + ".bossHelmet"))));
                            }
                            if (bossCfg.isSet("bosses." + bossType + ".bossChestplate")){
                                Objects.requireNonNull(monster.getEquipment()).setChestplate(Objects.requireNonNull(bossCfg.getItemStack("bosses." + bossType + ".bossChestplate")));
                            }
                            if (bossCfg.isSet("bosses." + bossType + ".bossBoots")){
                                Objects.requireNonNull(monster.getEquipment()).setBoots(Objects.requireNonNull(bossCfg.getItemStack("bosses." + bossType + ".bossBoots")));
                            }
                            if (bossCfg.isSet("bosses." + bossType + ".bossLeggings")){
                                Objects.requireNonNull(monster.getEquipment()).setLeggings(Objects.requireNonNull(bossCfg.getItemStack("bosses." + bossType + ".bossLeggings")));
                            }
                            if (bossCfg.isSet("bosses." + bossType + ".bossWeapon")){
                                Objects.requireNonNull(monster.getEquipment()).setItemInMainHand(Objects.requireNonNull(bossCfg.getItemStack("bosses." + bossType + ".bossWeapon")));
                            }
                            monster.setLootTable(null);
                        } else {
                            challenger.sendMessage(ChatColor.YELLOW + "Can't start the battle because " + ChatColor.GOLD + bossCfg.getString("playerName") + ChatColor.YELLOW + " hasn't claimed their items in their chest.");
                        }

                    } else {
                        challenger.sendMessage(ChatColor.YELLOW + "These items aren't allowed in this boss battle -> " + ChatColor.GOLD + Objects.requireNonNull(bossCfg.getList("bosses." + bossType + ".illegalItems")) + ChatColor.YELLOW + ". Please remove any illegal items and retry.");
                    }
                }
            }

        } else {
            challenger.sendMessage(ChatColor.YELLOW + "A boss is currently in the arena. Please wait until it is defeated or the challenger is vanquished.");
        }
    }

    public static void bossBattleEnded(EntityDeathEvent e) throws IOException {
        FileConfiguration bossCfg = Api.getFileConfiguration(Api.getConfigFile(YmlFileNames.YML_BOSS_FILE_NAME));
        String boss = bossCfg.getString("bossKey");
        if (bossCfg.isSet("bossUUID") && Objects.requireNonNull(bossCfg.getString("bossUUID")).equalsIgnoreCase(String.valueOf(e.getEntity().getUniqueId()))) {
            Bukkit.broadcastMessage(bossCfg.getString("playerName") + " defeated " + ChatColor.GOLD + bossCfg.getString("bosses." + boss + ".bossName"));
            Api.getEconomy().depositPlayer(Api.getOfflinePlayerFromPlayer(Objects.requireNonNull(Bukkit.getPlayer(Objects.requireNonNull(bossCfg.getString("playerName"))))), bossCfg.getDouble("bosses." + boss + ".reward"));
            Objects.requireNonNull(Bukkit.getPlayer(Objects.requireNonNull(bossCfg.getString("playerName")))).sendMessage(ChatColor.GREEN + "Congratulations!!! You have been awarded " + ChatColor.GOLD + "$" +  bossCfg.getDouble("bosses." + boss + ".reward"));
            bossCfg.set("bossEnabled", false);
            e.getDrops().clear();
            double warpX = bossCfg.getDouble("warpX");
            double warpY = bossCfg.getDouble("warpY");
            double warpZ = bossCfg.getDouble("warpZ");
            double chestX = bossCfg.getDouble("chestRightX");
            double chestY = bossCfg.getDouble("chestRightY");
            double chestZ = bossCfg.getDouble("chestRightZ");
            Location challengerChestLocation = new Location(Bukkit.getWorld("world"), chestX, chestY, chestZ);
            Chest challengerChest = (Chest) challengerChestLocation.getBlock().getState();
            for (ItemStack is : challengerChest.getInventory()){
                challengerChest.getInventory().remove(is);
            }
            Location warpLocation = new Location(Bukkit.getWorld("world"), warpX, warpY, warpZ);
            try {
                Objects.requireNonNull(Bukkit.getPlayer(Objects.requireNonNull(bossCfg.getString("playerName")))).teleport(warpLocation);

            } catch (NullPointerException ignored) {

            }
        }
        if (bossCfg.isSet("playerName") && Boolean.TRUE.equals(bossCfg.getBoolean("bossEnabled")) && e.getEntity().getName().equalsIgnoreCase(bossCfg.getString("playerName"))) {
            try {
                Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(Objects.requireNonNull(bossCfg.getString("bossUUID"))))).remove();
                double warpX = bossCfg.getDouble("warpX");
                double warpY = bossCfg.getDouble("warpY");
                double warpZ = bossCfg.getDouble("warpZ");
                Location warpLocation = new Location(Bukkit.getWorld("world"), warpX, warpY, warpZ);
                Objects.requireNonNull(Bukkit.getPlayer(Objects.requireNonNull(bossCfg.getString("playerName")))).teleport(warpLocation);

                double chestX = bossCfg.getDouble("chestRightX");
                double chestY = bossCfg.getDouble("chestRightY");
                double chestZ = bossCfg.getDouble("chestRightZ");
                Location challengerChestLocation = new Location(Bukkit.getWorld("world"), chestX, chestY, chestZ);
                Chest challengerChest = (Chest) challengerChestLocation.getBlock().getState();
                int count = 0;
                for (ItemStack is : e.getDrops()){
                    challengerChest.getInventory().setItem(count, is);
                    count++;
                }
                e.getDrops().clear();
            } catch (NullPointerException ignored){
            }
            bossCfg.set("bossEnabled", false);
        }

        bossCfg.save(Api.getConfigFile(YmlFileNames.YML_BOSS_FILE_NAME));
    }

    public static void bossHurtEvent(EntityDamageEvent e){
        FileConfiguration bossCfg = Api.getFileConfiguration(Api.getConfigFile(YmlFileNames.YML_BOSS_FILE_NAME));
        String bossKey = bossCfg.getString("bossKey");
        if (bossCfg.isSet("bossUUID") && Objects.requireNonNull(bossCfg.getString("bossUUID")).equalsIgnoreCase(String.valueOf(e.getEntity().getUniqueId()))) {
            Monster boss = (Monster) e.getEntity();
            BigDecimal a = BigDecimal.valueOf(boss.getHealth());
            BigDecimal b = new BigDecimal(10);
            Objects.requireNonNull(Bukkit.getPlayer(Objects.requireNonNull(bossCfg.getString("playerName")))).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + bossCfg.getString("bosses." + bossKey + ".bossName") +
                    ChatColor.GRAY + " - " + ChatColor.GREEN + "Health: " + ChatColor.GOLD + (a.multiply(b)).setScale(0, RoundingMode.UP)));
        }
    }
}
