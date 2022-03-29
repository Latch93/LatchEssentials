package discord_text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ExperienceManager {

    public static int getPlayerLevel(PlayerInteractEvent event, float xpToSubtract){
        Player player = event.getPlayer();
        double playerLevel = player.getLevel();
        double totalXpRequiredForNextLevel = getTotalXpRequiredForNextLevel(playerLevel);
        double xpLevel = event.getPlayer().getExp();
        xpToSubtract = (int) (xpToSubtract / totalXpRequiredForNextLevel);
        int finalPlayerLevel = player.getLevel();
        if (xpToSubtract > xpLevel){
            finalPlayerLevel = finalPlayerLevel - 1;
        }
        return finalPlayerLevel;
    }

    public static float getPlayerXP(PlayerInteractEvent event){
        Player player = event.getPlayer();
        double playerLevel = player.getLevel();
        float totalXpRequiredForNextLevel = (float) getTotalXpRequiredForNextLevel(playerLevel);
        float xpLevel = player.getExp();
        float xpToSubtract = 5 / totalXpRequiredForNextLevel;
        float finalXpResult;
        if (xpToSubtract < xpLevel){
            finalXpResult = xpLevel - xpToSubtract;
        } else {
            finalXpResult = 1 + (xpLevel - xpToSubtract);
        }
        return finalXpResult;
    }

    public static double getTotalXP(double playerLevel) {
        double totalXP;
        if (playerLevel <= 15) {
            totalXP = (playerLevel * playerLevel) + 6 * playerLevel;
        } else if (playerLevel >= 17 && playerLevel <= 31){
            totalXP = 2.5 * (playerLevel * playerLevel) - 40.5 * playerLevel + 360;
        } else {
            totalXP = 4.5 * (playerLevel * playerLevel) - 162.5 * playerLevel + 2220;
        }
        return totalXP;
    }

    public static double getTotalXpRequiredForNextLevel(double playerLevel) {
        double totalXpRequiredForNextLevel;
        if (playerLevel <= 15) {
            totalXpRequiredForNextLevel = 2 * playerLevel + 7;
        } else if (playerLevel >= 16 && playerLevel <= 30){
            totalXpRequiredForNextLevel = 5 * playerLevel - 38;
        } else {
            totalXpRequiredForNextLevel = 9 * playerLevel - 158;
        }
        return totalXpRequiredForNextLevel;
    }




}