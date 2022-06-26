package lmp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LMPTimer extends BukkitRunnable {
    static int seconds = 0;

    public static void runTimer() {
        Bukkit.getScheduler().runTaskAsynchronously(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> {
            new LMPTimer().runTaskTimer(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), 0, 20);
        });
    }


    @Override
    public void run() {
        // creating a Calendar object
        Calendar c1 = Calendar.getInstance();
        // creating a date object with specified time.
        DateTime dateOne = new DateTime();
        int hourOfDay = dateOne.getHourOfDay();
        int minuteOfHour = dateOne.getMinuteOfHour();
        int secondOfMinute = dateOne.getSecondOfMinute();
        if (secondOfMinute == 0) {
            if (hourOfDay == 0) {
                if (minuteOfHour == 0){
                    broadcastStaffApply();
                    broadcastLTS();
                }
            } else if (hourOfDay == 1) {
                if (minuteOfHour == 0){
                    broadcastLotto();
                    broadcastQuests();

                }
            } else if (hourOfDay == 2) {
                if (minuteOfHour == 0){
                    broadcastTwitch();
                    broadcastLTS();
                }
            } else if (hourOfDay == 3) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                    broadcastLTS();
                }
            } else if (hourOfDay == 4) {
                if (minuteOfHour == 0) {
                    broadcastQuests();
                    broadcastLotto();
                }
            } else if (hourOfDay == 5) {
                if (minuteOfHour == 0) {
                    broadcastStaffApply();
                }
                else if (minuteOfHour == 30) {
                    broadcastEasyBloodmoon();
                }
            } else if (hourOfDay == 6) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                    broadcastLTS();
                    if (Boolean.TRUE.equals(Api.arePlayersOnline())) {
                        try {
                            setEasyBloodMoon();
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Api.getOnlinePlayer().performCommand("bloodmoon reload"));
                            startBloodmoon();
                        } catch (NullPointerException | IOException ignored) {

                        }
                    }
                }
                if (minuteOfHour == 20){
                    try {
                        stopBloodmoon();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (hourOfDay == 7) {
                if (minuteOfHour == 0) {
                    broadcastLTS();
                    broadcastLotto();
                }
            } else if (hourOfDay == 8) {
                if (minuteOfHour == 0) {
                    broadcastLTS();
                    broadcastTwitch();
                } else if (minuteOfHour == 30) {
                    broadcastEasyBloodmoon();
                }
            } else if (hourOfDay == 9) {
                if (minuteOfHour == 0) {
                    broadcastLTS();
                    broadcastInvite();
                    if (Boolean.TRUE.equals(Api.arePlayersOnline())) {
                        try {
                            setEasyBloodMoon();
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Api.getOnlinePlayer().performCommand("bloodmoon reload"));
                            startBloodmoon();
                        } catch (NullPointerException | IOException ignored) {

                        }
                    }
                }
                if (minuteOfHour == 20){
                    try {
                        stopBloodmoon();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (hourOfDay == 10) {
                if (minuteOfHour == 0) {
                    broadcastStaffApply();
                    broadcastQuests();
                }
            } else if (hourOfDay == 11) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                    broadcastLTS();
                } else if (minuteOfHour == 30) {
                    broadcastMediumBloodmoon();
                }
            } else if (hourOfDay == 12) {
                if (minuteOfHour == 0) {
                    broadcastLotto();
                    broadcastStaffApply();
                    if (Boolean.TRUE.equals(Api.arePlayersOnline())) {
                        try {
                            setMediumBloodMoon();
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Api.getOnlinePlayer().performCommand("bloodmoon reload"));
                            startBloodmoon();
                        } catch (NullPointerException | IOException ignored) {

                        }
                    }
                }
                if (minuteOfHour == 20){
                    try {
                        stopBloodmoon();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (hourOfDay == 13) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                    broadcastQuests();
                }
            } else if (hourOfDay == 14) {
                if (minuteOfHour == 0) {
                    broadcastTwitch();
                    broadcastLTS();
                } else if (minuteOfHour == 30) {
                    broadcastEasyBloodmoon();
                }
            } else if (hourOfDay == 15) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                    broadcastStaffApply();
                    if (Boolean.TRUE.equals(Api.arePlayersOnline())) {
                        try {
                            setEasyBloodMoon();
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Api.getOnlinePlayer().performCommand("bloodmoon reload"));
                            startBloodmoon();
                        } catch (NullPointerException | IOException ignored) {

                        }
                    }
                }
                if (minuteOfHour == 20){
                    try {
                        stopBloodmoon();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (hourOfDay == 16) {
                if (minuteOfHour == 0) {
                    broadcastLotto();
                    broadcastLTS();
                }
            } else if (hourOfDay == 17) {
                if (minuteOfHour == 0) {
                    broadcastStaffApply();
                    broadcastQuests();
                } else if (minuteOfHour == 30) {
                    broadcastEasyBloodmoon();
                }
            } else if (hourOfDay == 18) {
                if (minuteOfHour == 0) {
                    broadcastLotto();
                    broadcastLTS();
                    if (Boolean.TRUE.equals(Api.arePlayersOnline())) {
                        try {
                            setEasyBloodMoon();
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Api.getOnlinePlayer().performCommand("bloodmoon reload"));
                            startBloodmoon();
                        } catch (NullPointerException | IOException ignored) {

                        }
                    }
                }
                if (minuteOfHour == 20){
                    try {
                        stopBloodmoon();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (hourOfDay == 19) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                }
            } else if (hourOfDay == 20) {
                if (minuteOfHour == 0) {
                    broadcastTwitch();
                    broadcastLTS();
                } else if (minuteOfHour == 30) {
                    broadcastMediumBloodmoon();
                }
            } else if (hourOfDay == 21) {
                if (minuteOfHour == 0) {
                    broadcastStaffApply();
                    broadcastQuests();
                    if (Boolean.TRUE.equals(Api.arePlayersOnline())) {
                        try {
                            setMediumBloodMoon();
                            Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Api.getOnlinePlayer().performCommand("bloodmoon reload"));
                            startBloodmoon();
                        } catch (NullPointerException | IOException ignored) {

                        }
                    }
                }
                if (minuteOfHour == 20){
                    try {
                        stopBloodmoon();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            } else if (hourOfDay == 22) {
                if (minuteOfHour == 0) {
                    broadcastLotto();
                    broadcastLTS();
                    try {
                        Lottery.executeLotto();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (hourOfDay == 23) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                }
            }
        }
    }

    public static void broadcastTwitch() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "You can connect your Twitch Stream to your minecraft chat. Type " + ChatColor.AQUA + "/twitch help");
    }

    public static void broadcastLotto() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "Daily Lottery exists. To buy in, type -> " + ChatColor.AQUA + "/lmp lotto buyin  " + ChatColor.GOLD +
                "See the total Lottery Prize with -> " + ChatColor.AQUA + "/lmp" +
                " lotto total");
    }

    public static void broadcastInvite(){
        Bukkit.broadcastMessage(ChatColor.GOLD + "You can invite your friends to the server. Copy and send this link -> " + ChatColor.AQUA + "https://discord.gg/3rne7X9sZH");
    }

    public static void broadcastStaffApply(){
        Bukkit.broadcastMessage(ChatColor.GOLD + "You can apply for staff in discord. Look for the " + ChatColor.AQUA + "#apply-for-staff " + ChatColor.GOLD + "channel.");
    }

    public static void broadcastQuests(){
        Bukkit.broadcastMessage(ChatColor.GOLD + "Suggest Quests in " + ChatColor.AQUA + "#suggestions");
    }

    public static void broadcastLTS(){
        Bukkit.broadcastMessage(ChatColor.GOLD + "Nominate items for the Limited Time Shop in discord. Shop resets every week U.S. Time.");
    }

    public static void setEasyBloodMoon() throws IOException {
        File bloodmoonFile = new File("plugins/Bloodmoon/world", "config.yml");
        FileConfiguration bloodmoonCfg = Api.getFileConfiguration(bloodmoonFile);
        File bloodmoonLocalesFile = new File("plugins/Bloodmoon", "locales.yml");
        FileConfiguration bloodmoonLocalesCfg = Api.getFileConfiguration(bloodmoonLocalesFile);
        bloodmoonLocalesCfg.set("BloodMoonTitleBar", "Easy BloodMoon");
        List<String> hordeList = new ArrayList<>();
        hordeList.add("ZOMBIE");
        hordeList.add("SKELETON");
        hordeList.add("WITCH");
        bloodmoonCfg.set("HordeMobWhitelist", hordeList);
        bloodmoonCfg.set("HordeMinPopulation", 1);
        bloodmoonCfg.set("HordeMaxPopulation", 3);
        bloodmoonCfg.set("EnableZombieBoss", false);
        bloodmoonCfg.set("BloodMoonSpawnMobRate", 10);
        bloodmoonCfg.set("MobHealthMultiplicator", 2);
        bloodmoonCfg.set("MobDamageMultiplicator", 2);
        bloodmoonCfg.set("ExperienceDropMult", 3);
        bloodmoonCfg.set("ItemDropsMinimum", 1);
        bloodmoonCfg.set("ItemDropsMaximum", 3);
        bloodmoonCfg.set("BaselineHordeSpawnrate", 600);
        List<String> dropList = new ArrayList<>();
        dropList.add("IRON_INGOT:5:5");
        dropList.add("DIAMOND:1:1");
        dropList.add("GOLD_INGOT:6:7");
        dropList.add("IRON_BLOCK:2:5");
        dropList.add("GOLD_BLOCK:3:5");
        dropList.add("APPLE:3:5");
        bloodmoonCfg.set("DropItemList", dropList);
        bloodmoonCfg.save(bloodmoonFile);
        bloodmoonLocalesCfg.save(bloodmoonLocalesFile);
    }

    public static void setMediumBloodMoon() throws IOException {
        File bloodmoonFile = new File("plugins/Bloodmoon/world", "config.yml");
        FileConfiguration bloodmoonCfg = Api.getFileConfiguration(bloodmoonFile);
        File bloodmoonLocalesFile = new File("plugins/Bloodmoon", "locales.yml");
        FileConfiguration bloodmoonLocalesCfg = Api.getFileConfiguration(bloodmoonLocalesFile);
        bloodmoonLocalesCfg.set("BloodMoonTitleBar", "Medium BloodMoon");
        List<String> hordeList = new ArrayList<>();
        hordeList.add("ZOMBIE");
        hordeList.add("SKELETON");
//        hordeList.add("WITCH");
        hordeList.add("CREEPER");
        hordeList.add("PILLAGER");
        hordeList.add("PHANTOM");
        bloodmoonCfg.set("HordeMobWhitelist", hordeList);
        bloodmoonCfg.set("HordeMinPopulation", 2);
        bloodmoonCfg.set("HordeMaxPopulation", 5);
        bloodmoonCfg.set("EnableZombieBoss", true);
        bloodmoonCfg.set("BloodMoonSpawnMobRate", 20);
        bloodmoonCfg.set("MobHealthMultiplicator", 3);
        bloodmoonCfg.set("MobDamageMultiplicator", 2);
        bloodmoonCfg.set("ExperienceDropMult", 6);
        bloodmoonCfg.set("ItemDropsMinimum", 2);
        bloodmoonCfg.set("ItemDropsMaximum", 5);
        bloodmoonCfg.set("ZombieBossDamage", 10);
        bloodmoonCfg.set("ZombieBossExpMultiplier", 25);
        bloodmoonCfg.set("ZombieBossHealth", 50);
        bloodmoonCfg.set("ZombieBossItemMultiplier", 10);
        bloodmoonCfg.set("BaselineHordeSpawnrate", 500);
        List<String> dropList = new ArrayList<>();
        dropList.add("IRON_INGOT:10:6");
//        dropList.add("DIAMOND_BLOCK:1:1");
        dropList.add("DIAMOND:2:2");
        dropList.add("GOLD_INGOT:7:4");
        dropList.add("IRON_BLOCK:5:2");
        dropList.add("GOLD_BLOCK:6:2");
        dropList.add("APPLE:3:5");
        dropList.add("ANCIENT_DEBRIS:1:1");
        bloodmoonCfg.set("DropItemList", dropList);
        bloodmoonCfg.save(bloodmoonFile);
        bloodmoonLocalesCfg.save(bloodmoonLocalesFile);
    }

    public static void setHardBloodMoon() throws IOException {
        File bloodmoonFile = new File("plugins/Bloodmoon/world", "config.yml");
        FileConfiguration bloodmoonCfg = Api.getFileConfiguration(bloodmoonFile);
        File bloodmoonLocalesFile = new File("plugins/Bloodmoon", "locales.yml");
        FileConfiguration bloodmoonLocalesCfg = Api.getFileConfiguration(bloodmoonLocalesFile);
        bloodmoonLocalesCfg.set("BloodMoonTitleBar", "Hard BloodMoon");
        List<String> hordeList = new ArrayList<>();
        hordeList.add("ZOMBIE");
        hordeList.add("SKELETON");
        hordeList.add("WITCH");
        hordeList.add("CREEPER");
        hordeList.add("PILLAGER");
        hordeList.add("PHANTOM");
        hordeList.add("ILLUSIONER");
        hordeList.add("ENDERMAN");
        hordeList.add("VEX");
        bloodmoonCfg.set("HordeMobWhitelist", hordeList);
        List<String> dropList = new ArrayList<>();
        dropList.add("IRON_INGOT:10:10");
        dropList.add("DIAMOND_BLOCK:1:3");
        dropList.add("DIAMOND:5:2");
        dropList.add("GOLD_INGOT:25:20");
        dropList.add("IRON_BLOCK:10:5");
        dropList.add("GOLD_BLOCK:10:10");
        dropList.add("APPLE:3:5");
        dropList.add("ANCIENT_DEBRIS:1:3");
        bloodmoonCfg.set("DropItemList", dropList);
        bloodmoonCfg.set("HordeMinPopulation", 5);
        bloodmoonCfg.set("HordeMaxPopulation", 10);
        bloodmoonCfg.set("EnableZombieBoss", true);
        bloodmoonCfg.set("BloodMoonSpawnMobRate", 35);
        bloodmoonCfg.set("MobHealthMultiplicator", 5);
        bloodmoonCfg.set("MobDamageMultiplicator", 3);
        bloodmoonCfg.set("ExperienceDropMult", 10);
        bloodmoonCfg.set("ItemDropsMinimum", 4);
        bloodmoonCfg.set("ItemDropsMaximum", 10);
        bloodmoonCfg.set("ZombieBossDamage", 20);
        bloodmoonCfg.set("ZombieBossExpMultiplier", 100);
        bloodmoonCfg.set("ZombieBossHealth", 100);
        bloodmoonCfg.set("ZombieBossItemMultiplier", 25);
        bloodmoonCfg.set("BaselineHordeSpawnrate", 400);
        bloodmoonCfg.save(bloodmoonFile);
        bloodmoonLocalesCfg.save(bloodmoonLocalesFile);
    }

    public static void startBloodmoon() throws IOException {
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule mobGriefing false"));
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bloodmoon start world"));
        Api.setDoSpawnersSpawn(true);
    }

    public static void stopBloodmoon() throws IOException {
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule mobGriefing true"));
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bloodmoon stop world"));
        Api.setDoSpawnersSpawn(false);
    }

    public static void broadcastEasyBloodmoon(){
        Bukkit.broadcastMessage(ChatColor.RED + "Easy Bloodmoon starts in 30 minutes.");
    }
    public static void broadcastMediumBloodmoon(){
        Bukkit.broadcastMessage(ChatColor.RED + "Medium Bloodmoon starts in 30 minutes.");
    }
    public static void broadcastHardBloodmoon(){
        Bukkit.broadcastMessage(ChatColor.RED + "Hard Bloodmoon starts in 30 minutes.");
    }
}
