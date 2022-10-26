package lmp.runnable;

import lmp.LatchDiscord;
import lmp.Lottery;
import lmp.api.Api;
import lmp.commands.LMPCommand;
import lmp.constants.Constants;
import lmp.constants.YmlFileNames;
import lmp.donationBot.Donation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class LMPTimer extends BukkitRunnable {
    static int seconds = 0;

    public static void runTimer() {
        Bukkit.getScheduler().runTaskAsynchronously(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> {
            new LMPTimer().runTaskTimer(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), 0, 20);
        });
    }

    public static void broadcastTwitch() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "You can connect your Twitch Stream to your minecraft chat. Type " + ChatColor.AQUA + "/twitch help");
    }

    public static void broadcastLotto() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "Daily Lottery occurs every day. To buy in, type -> " + ChatColor.AQUA + "/lmp lotto buyin  " + ChatColor.GOLD +
                "See the total Lottery Prize with -> " + ChatColor.AQUA + "/lmp lotto total");
    }

    public static void broadcastInvite() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "You can invite your friends to the server. Copy and send this link -> " + ChatColor.AQUA + "https://discord.gg/NCDVnRsu3X");
    }

    public static void broadcastStaffApply() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "Do you want to help moderate players?\nDo you want to help grow this server?\nDo you want to watch over new players and vet them?\nWell you can apply for staff in discord. Look for the " + ChatColor.AQUA + "#apply-for-staff " + ChatColor.GOLD + "channel.");
    }

    public static void broadcastQuests() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "Suggest Quests in " + ChatColor.AQUA + "#suggestions");
    }

    public static void broadcastLTS() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "Nominate items for the Limited Time Shop in discord. Shop resets every week.");
    }

    public static void setEasyBloodMoon() throws IOException {
        File bloodmoonFile = new File("plugins/Bloodmoon/world", "config.yml");
        FileConfiguration bloodmoonCfg = YamlConfiguration.loadConfiguration(bloodmoonFile);
        List<String> hordeList = new ArrayList<>();
        hordeList.add("ZOMBIE");
        hordeList.add("SKELETON");
        hordeList.add("MAGMA_CUBE");
        bloodmoonCfg.set("HordeMobWhitelist", hordeList);
        bloodmoonCfg.set("HordeMinPopulation", 5);
        bloodmoonCfg.set("HordeMaxPopulation", 10);
        bloodmoonCfg.set("EnableZombieBoss", false);
        bloodmoonCfg.set("BloodMoonSpawnMobRate", 10);
        bloodmoonCfg.set("MobHealthMultiplicator", 1.5);
        bloodmoonCfg.set("MobDamageMultiplicator", 1.5);
        bloodmoonCfg.set("ExperienceDropMult", 3);
        bloodmoonCfg.set("BaselineHordeSpawnrate", 1200);
        List<String> dropList = new ArrayList<>();
        dropList.add("DIAMOND:2:90");
        dropList.add("GOLD_BLOCK:2:75");
        dropList.add("IRON_BLOCK:2:50");
        dropList.add("GOLD_INGOT:5:25");
        dropList.add("APPLE:10:0");
        bloodmoonCfg.set("DropItemList", dropList);
        bloodmoonCfg.save(bloodmoonFile);
        FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
        configCfg.set("deathBalancePercentage", 5.00);
        configCfg.save(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
        File bloodmoonLocalesFile = new File("plugins/Bloodmoon", "locales.yml");
        FileConfiguration bloodmoonLocalesCfg = YamlConfiguration.loadConfiguration(bloodmoonLocalesFile);
        bloodmoonLocalesCfg.set("BloodMoonTitleBar", "Easy Bloodmoon");
        bloodmoonLocalesCfg.save(bloodmoonLocalesFile);
    }

    public static void setMediumBloodMoon() throws IOException {
        File bloodmoonFile = new File("plugins/Bloodmoon/world", "config.yml");
        FileConfiguration bloodmoonCfg = YamlConfiguration.loadConfiguration(bloodmoonFile);
        List<String> hordeList = new ArrayList<>();
        hordeList.add("ZOMBIE");
        hordeList.add("SKELETON");
        hordeList.add("WITCH");
        hordeList.add("CREEPER");
        hordeList.add("PILLAGER");
        hordeList.add("MAGMA_CUBE");
        bloodmoonCfg.set("HordeMobWhitelist", hordeList);
        bloodmoonCfg.set("HordeMinPopulation", 4);
        bloodmoonCfg.set("HordeMaxPopulation", 8);
        bloodmoonCfg.set("EnableZombieBoss", true);
        bloodmoonCfg.set("BloodMoonSpawnMobRate", 20);
        bloodmoonCfg.set("MobHealthMultiplicator", 3.5);
        bloodmoonCfg.set("MobDamageMultiplicator", 2);
        bloodmoonCfg.set("ExperienceDropMult", 6);
        bloodmoonCfg.set("ZombieBossDamage", 5);
        bloodmoonCfg.set("ZombieBossExpMultiplier", 25);
        bloodmoonCfg.set("ZombieBossHealth", 50);
        bloodmoonCfg.set("ZombieBossItemMultiplier", 10);
        bloodmoonCfg.set("BaselineHordeSpawnrate", 1800);
        List<String> dropList = new ArrayList<>();
        dropList.add("ANCIENT_DEBRIS:1:95");
        dropList.add("DIAMOND:4:90");
        dropList.add("GOLD_BLOCK:4:75");
        dropList.add("IRON_BLOCK:5:40");
        dropList.add("GOLD_INGOT:7:10");
        dropList.add("GOLDEN_APPLE:3:0");
        bloodmoonCfg.set("DropItemList", dropList);
        bloodmoonCfg.save(bloodmoonFile);
        FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
        configCfg.set("deathBalancePercentage", 10.00);
        configCfg.save(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
        File bloodmoonLocalesFile = new File("plugins/Bloodmoon", "locales.yml");
        FileConfiguration bloodmoonLocalesCfg = YamlConfiguration.loadConfiguration(bloodmoonLocalesFile);
        bloodmoonLocalesCfg.set("BloodMoonTitleBar", "Medium Bloodmoon");
        bloodmoonLocalesCfg.save(bloodmoonLocalesFile);
    }

    public static void setHardBloodMoon() throws IOException {
        File bloodmoonFile = new File("plugins/Bloodmoon/world", "config.yml");
        FileConfiguration bloodmoonCfg = YamlConfiguration.loadConfiguration(bloodmoonFile);
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
        dropList.add("NETHERITE_INGOT:1:99");
        dropList.add("ANCIENT_DEBRIS:1:90");
        dropList.add("DIAMOND_BLOCK:2:75");
        dropList.add("GOLD_BLOCK:9:50");
        dropList.add("IRON_BLOCK:10:20");
        dropList.add("GOLD_INGOT:15:0");
        bloodmoonCfg.set("DropItemList", dropList);
        bloodmoonCfg.set("HordeMinPopulation", 2);
        bloodmoonCfg.set("HordeMaxPopulation", 6);
        bloodmoonCfg.set("EnableZombieBoss", true);
        bloodmoonCfg.set("BloodMoonSpawnMobRate", 35);
        bloodmoonCfg.set("MobHealthMultiplicator", 3);
        bloodmoonCfg.set("MobDamageMultiplicator", 4);
        bloodmoonCfg.set("ExperienceDropMult", 10);
        bloodmoonCfg.set("ItemDropsMinimum", 4);
        bloodmoonCfg.set("ItemDropsMaximum", 10);
        bloodmoonCfg.set("ZombieBossDamage", 10);
        bloodmoonCfg.set("ZombieBossExpMultiplier", 100);
        bloodmoonCfg.set("ZombieBossHealth", 100);
        bloodmoonCfg.set("ZombieBossItemMultiplier", 25);
        bloodmoonCfg.set("BaselineHordeSpawnrate", 3000);
        bloodmoonCfg.save(bloodmoonFile);
        FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
        configCfg.set("deathBalancePercentage", 15.00);
        configCfg.save(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
        File bloodmoonLocalesFile = new File("plugins/Bloodmoon/world", "locales.yml");
        FileConfiguration bloodmoonLocalesCfg = YamlConfiguration.loadConfiguration(bloodmoonLocalesFile);
        bloodmoonLocalesCfg.set("BloodMoonTitleBar", "Hard Bloodmoon");
        bloodmoonLocalesCfg.save(bloodmoonLocalesFile);
    }

    public static void startBloodmoon() throws IOException {
        Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "bloodmoon start world"));
        File bloodmoonLocalesFile = new File("plugins/Bloodmoon", "locales.yml");
        FileConfiguration bloodmoonLocalesCfg = YamlConfiguration.loadConfiguration(bloodmoonLocalesFile);
        File bloodmoonFile = new File("plugins/Bloodmoon/world", "config.yml");
        FileConfiguration bloodmoonCfg = YamlConfiguration.loadConfiguration(bloodmoonFile);
        bloodmoonCfg.set("enabled", true);
        bloodmoonCfg.save(bloodmoonFile);
        LatchDiscord.getJDA().getGuildById(lmp.Constants.GUILD_ID).getTextChannelById(lmp.Constants.GENERAL_CHANNEL_ID).sendMessage(bloodmoonLocalesCfg.getString("BloodMoonTitleBar") + " has started!").queue();
    }

    public static void stopBloodmoon() throws IOException {
//        FileConfiguration configCfg = Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME);
//        configCfg.set("deathBalancePercentage", configCfg.getDouble("defaultDeathBalancePercentage"));
//        configCfg.save(Api.getConfigFile(YmlFileNames.YML_CONFIG_FILE_NAME));
//        File bloodmoonLMPFile = new File("plugins/Bloodmoon", "lmp.yml");
//        FileConfiguration bloodmoonLMPCfg = YAMLbloodmoonLMPFile);
//        File bloodmoonFile = new File("plugins/Bloodmoon/world", "config.yml");
//        FileConfiguration bloodmoonCfg = Api.getFileConfiguration(bloodmoonFile);
//        bloodmoonCfg.set("enabled", false);
//        bloodmoonCfg.save(bloodmoonFile);
//        bloodmoonLMPCfg.save(bloodmoonLMPFile);
//        for (String mobUUID : bloodmoonLMPCfg.getStringList("hordeMobs")) {
//            try {
//                Objects.requireNonNull(Bukkit.getEntity(UUID.fromString(mobUUID))).remove();
//
//            } catch (NullPointerException ignored) {
//
//            }
//        }
//        bloodmoonLMPCfg.set("hordeMobs", null);
    }

    public static void broadcastEasyBloodmoon() throws IOException {
        LatchDiscord.getJDA().getGuildById(lmp.Constants.GUILD_ID).getTextChannelById(lmp.Constants.GENERAL_CHANNEL_ID).sendMessage("Easy Bloodmoon starts in 30 minutes.").queue();
        Bukkit.broadcastMessage(ChatColor.RED + "Easy Bloodmoon starts in 30 minutes.");
    }

    public static void broadcastMediumBloodmoon() throws IOException {
        LatchDiscord.getJDA().getGuildById(lmp.Constants.GUILD_ID).getTextChannelById(lmp.Constants.GENERAL_CHANNEL_ID).sendMessage("Medium Bloodmoon starts in 30 minutes.").queue();
        Bukkit.broadcastMessage(ChatColor.RED + "Medium Bloodmoon starts in 30 minutes.");
    }

    public static void broadcastHardBloodmoon() throws IOException {
        LatchDiscord.getJDA().getGuildById(lmp.Constants.GUILD_ID).getTextChannelById(lmp.Constants.GENERAL_CHANNEL_ID).sendMessage("Hard Bloodmoon starts in 30 minutes.").queue();
        Bukkit.broadcastMessage(ChatColor.RED + "Hard Bloodmoon starts in 30 minutes.");
    }

    @Override
    public void run() {
        FileConfiguration xpFarmCfg = Api.getFileConfiguration(YmlFileNames.YML_XP_FARM_FILE_NAME);
        if (Boolean.TRUE.equals(xpFarmCfg.getBoolean("isFarmInUse"))) {
            try {
                Api.stopXPFarm();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // creating a Calendar object
        Calendar c1 = Calendar.getInstance();
        // creating a date object with specified time.
        DateTime dateOne = new DateTime();
        int hourOfDay = dateOne.getHourOfDay();
        int minuteOfHour = dateOne.getMinuteOfHour();
        int secondOfMinute = dateOne.getSecondOfMinute();
        int dayOfWeek = dateOne.getDayOfWeek();
        if (secondOfMinute == 0 || secondOfMinute == 15 || secondOfMinute == 30 || secondOfMinute == 45) {
            if (minuteOfHour < 61) {
                Donation.getDonations();
            }
        }
        if (secondOfMinute == 0) {
            if (minuteOfHour % 5 == 0) {
                if (Boolean.TRUE.equals(Api.getFileConfiguration(YmlFileNames.YML_CONFIG_FILE_NAME).getBoolean("randomSpectate"))) {
                    LMPCommand.spectateInsideRandomPlayer(Objects.requireNonNull(Bukkit.getPlayer("latch93")));
                }
            }
            if (hourOfDay == 0) {
                if (minuteOfHour == 0) {
                    broadcastStaffApply();
                    broadcastLTS();
                }
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        if (Boolean.TRUE.equals(Api.arePlayersOnline())) {
//                            try {
//                                setHardBloodMoon();
//                                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Config.PLUGIN_NAME)), () -> Api.getOnlinePlayer().performCommand("bloodmoon reload"));
//                                startBloodmoon();
//                            } catch (NullPointerException | IOException ignored) {
//
//                            }
//                        }
//                    }

//                else if (minuteOfHour == 13){
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        try {
//                            stopBloodmoon();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
            } else if (hourOfDay == 1) {
                if (minuteOfHour == 0) {
                    broadcastLotto();
                }
            } else if (hourOfDay == 2) {
                if (minuteOfHour == 0) {
//                    broadcastTwitch();
                    broadcastLTS();
                }
//                else if (minuteOfHour == 30) {
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        try {
//                            broadcastMediumBloodmoon();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
            } else if (hourOfDay == 3) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                    broadcastLTS();
                }
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        if (Boolean.TRUE.equals(Api.arePlayersOnline())) {
//                            try {
//                                setMediumBloodMoon();
//                                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Config.PLUGIN_NAME)), () -> Api.getOnlinePlayer().performCommand("bloodmoon reload"));
//                                startBloodmoon();
//                            } catch (NullPointerException | IOException ignored) {
//
//                            }
//                        }
//                    }

//                if (minuteOfHour == 13){
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        try {
//                            stopBloodmoon();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
            } else if (hourOfDay == 4) {
                if (minuteOfHour == 0) {
                    broadcastLotto();
                }
            } else if (hourOfDay == 5) {
                if (minuteOfHour == 0) {
                    broadcastStaffApply();
                }
//                else if (minuteOfHour == 30) {
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        try {
//                            broadcastEasyBloodmoon();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
            } else if (hourOfDay == 6) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                    broadcastLTS();
                }
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        if (Boolean.TRUE.equals(Api.arePlayersOnline())) {
//                            try {
//                                setEasyBloodMoon();
//                                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Config.PLUGIN_NAME)), () -> Api.getOnlinePlayer().performCommand("bloodmoon reload"));
//                                startBloodmoon();
//                            } catch (NullPointerException | IOException ignored) {
//
//                            }
//                        }
//                    }
//                if (minuteOfHour == 13){
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        try {
//                            stopBloodmoon();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
            } else if (hourOfDay == 7) {
                if (minuteOfHour == 0) {
                    broadcastLTS();
                    broadcastLotto();
                }
            } else if (hourOfDay == 8) {
                if (minuteOfHour == 0) {
                    broadcastLTS();
//                    broadcastTwitch();
                }
            } else if (hourOfDay == 9) {
                if (minuteOfHour == 0) {
                    broadcastLTS();
                    broadcastInvite();
                }
            } else if (hourOfDay == 10) {
                if (minuteOfHour == 0) {
                    broadcastStaffApply();
                }
            } else if (hourOfDay == 11) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                    broadcastLTS();
                }
//                else if (minuteOfHour == 30) {
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        try {
//                            broadcastMediumBloodmoon();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
            } else if (hourOfDay == 12) {
                if (minuteOfHour == 0) {
                    broadcastLotto();
                    broadcastStaffApply();
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        if (Boolean.TRUE.equals(Api.arePlayersOnline())) {
//                            try {
//                                setMediumBloodMoon();
//                                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Config.PLUGIN_NAME)), () -> Api.getOnlinePlayer().performCommand("bloodmoon reload"));
//                                startBloodmoon();
//                            } catch (NullPointerException | IOException ignored) {
//
//                            }
//                        }
//                    }
                }
//                if (minuteOfHour == 13){
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        try {
//                            stopBloodmoon();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
            } else if (hourOfDay == 13) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                }
            } else if (hourOfDay == 14) {
                if (minuteOfHour == 0) {
//                    broadcastTwitch();
                    broadcastLTS();
                }
//                else if (minuteOfHour == 30) {
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        try {
//                            broadcastHardBloodmoon();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
            } else if (hourOfDay == 15) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                    broadcastStaffApply();
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        if (Boolean.TRUE.equals(Api.arePlayersOnline())) {
//                            try {
//                                setHardBloodMoon();
//                                Bukkit.getScheduler().runTask(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin(Config.PLUGIN_NAME)), () -> Api.getOnlinePlayer().performCommand("bloodmoon reload"));
//                                startBloodmoon();
//                            } catch (NullPointerException | IOException ignored) {
//
//                            }
//                        }
                }
//                if (minuteOfHour == 13){
//                    if (dayOfWeek == 1 || dayOfWeek == 4 || dayOfWeek == 6) {
//                        try {
//                            stopBloodmoon();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
            } else if (hourOfDay == 16) {
                if (minuteOfHour == 0) {
                    broadcastLotto();
                    broadcastLTS();
                }
            } else if (hourOfDay == 17) {
                if (minuteOfHour == 0) {
                    broadcastStaffApply();
                }
            } else if (hourOfDay == 18) {
                if (minuteOfHour == 0) {
                    broadcastLotto();
                    broadcastLTS();
                }
            } else if (hourOfDay == 19) {
                if (minuteOfHour == 0) {
                    broadcastInvite();
                }
            } else if (hourOfDay == 20) {
                if (minuteOfHour == 0) {
//                    broadcastTwitch();
                    broadcastLTS();
                }
            } else if (hourOfDay == 21) {
                if (minuteOfHour == 0) {
                    broadcastStaffApply();
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
                //                else if (minuteOfHour == 30) {
                //                    if (dayOfWeek == 0 || dayOfWeek == 3 || dayOfWeek == 5) {
                //                        try {
                //                            broadcastHardBloodmoon();
                //                        } catch (IOException e) {
                //                            e.printStackTrace();
                //                        }
                //                    }
                //                }
                //            }
            }
        }
    }
}
