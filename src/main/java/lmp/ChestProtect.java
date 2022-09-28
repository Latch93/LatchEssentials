package lmp;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static lmp.LatchDiscord.jda;

public class ChestProtect extends ListenerAdapter {
    public final long channelId;
    public final long authorId;
    public final String thiefMinecraftName;
    public final String itemTaken;
    public final Location chestLocation;
    public final Block block;
    public List<String> chestProtectQuestions = new LinkedList<>();

    public ChestProtect(MessageChannel channel, User chestOwner, String itemTaken, String thiefMinecraftName, Location chestLocation, Block block) {
        this.channelId = channel.getIdLong();
        this.authorId = chestOwner.getIdLong();
        this.thiefMinecraftName = thiefMinecraftName;
        this.itemTaken = itemTaken;
        this.chestLocation = chestLocation;
        this.block = block;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getAuthor().getIdLong() != this.authorId) return;
        if (event.getChannel().getIdLong() != this.channelId) return;
        chestProtectQuestions.add("Minecraft User Name: " + thiefMinecraftName + "\nDiscord User Name: " + Api.getDiscordNameFromMCid(Api.getMinecraftIdFromMinecraftName(thiefMinecraftName)) + "\n" +
                "Stole: " + itemTaken + "\nLocation: " + chestLocation.toString() + "\n" +
                "ChestType: " + block.getType() + "\n* Type -> !approveAll | If you are ok with this user accessing any of your chests and want to add them to your list of approved chest users\n"+
                "* Type -> !approveChest | If you want to approve this user to be able to use only this chest\n" +
                "* Type -> !deny | If are NOT ok with this user accessing your chests and want to report this player\n" +
                "* Type -> !ignorechest | If you no longer want to be notified if someone takes from this chest\n" +
                "* Type -> !ignoreall | If you no longer want to be notified if someone takes from any of your chests");
        TextChannel adminChannel = jda.getTextChannelById(Constants.JUNIOR_MOD_CHANNEL_ID);
        FileConfiguration chestProtectCfg = Api.getFileConfiguration(Api.getConfigFile(Constants.YML_CHEST_PROTECT_FILE_NAME));
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!approveAll")) {
            assert adminChannel != null;
            try {
                setApprovedAllPlayer(event.getAuthor().getId(), thiefMinecraftName, chestProtectCfg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            event.getChannel().sendMessage("\n-------------------\nThis user has been added to your approved list of chest users :smile:").queue();
        } else if (event.getMessage().getContentRaw().equalsIgnoreCase("!ignoreall")) {
            Api.addPlayerToPermissionGroup(Api.getMinecraftIdFromDCid(event.getAuthor().getId()), "chests");
            event.getChannel().sendMessage("\n-------------------\nYou will no longer be notified if someone takes from any of your chests! :smile:").queue();
        } else if (event.getMessage().getContentRaw().equalsIgnoreCase("!approveChest")) {
            assert adminChannel != null;
            try {
                setApprovedChestPlayer(event.getAuthor().getId(), thiefMinecraftName, chestProtectCfg, chestLocation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            event.getChannel().sendMessage("\n-------------------\nThis user has been approved to use this one chest :smile:").queue();
        } else if (event.getMessage().getContentRaw().equalsIgnoreCase("!ignorechest")) {
            try {
                setIgnoredChests(chestLocation, chestProtectCfg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            event.getChannel().sendMessage("\n-------------------\nYou will no longer be notified if someone takes from this chest! :smile:").queue();
        } else if (event.getMessage().getContentRaw().equalsIgnoreCase("!deny")){
            event.getChannel().sendMessage("\n-------------------\nLatch and his staff will review this and get back to you! :smile:").queue();
            adminChannel.sendMessage("Discord User: " + event.getAuthor().getName() + " --- Minecraft User: " + Bukkit.getOfflinePlayer(UUID.fromString(Api.getMinecraftIdFromDCid(event.getAuthor().getId()))).getName()  + " had their stuff stolen by\nMinecraft User Name: " + thiefMinecraftName + "\nDiscord User Name: " + Api.getDiscordNameFromMCid(Api.getMinecraftIdFromMinecraftName(thiefMinecraftName)) + "" +
                    "\nLocation: " + chestLocation).queue();
        } else {
            event.getChannel().sendMessage("\n-------------------\nSomething went wrong. Make sure you type the command correctly").queue();
        }
        try {
            chestProtectCfg.save(Api.getConfigFile(Constants.YML_CHEST_PROTECT_FILE_NAME));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        chestProtectQuestions.clear();
        event.getJDA().removeEventListener(this);
    }

    public static void setApprovedAllPlayer(String chestOwnerDcId, String thiefMinecraftName, FileConfiguration chestProtectCfg) throws IOException {
        List<String> allowedPlayers;
        allowedPlayers = chestProtectCfg.getStringList(Constants.YML_PLAYERS + Api.getMinecraftIdFromDCid(chestOwnerDcId) + ".approvedPlayers");
        if (!allowedPlayers.contains(Api.getMinecraftIdFromMinecraftName(thiefMinecraftName))){
            allowedPlayers.add(Api.getMinecraftIdFromMinecraftName(thiefMinecraftName));
        }
        String minecraftID = Api.getMinecraftIdFromDCid(chestOwnerDcId);
        chestProtectCfg.set(Constants.YML_PLAYERS + minecraftID + ".approvedPlayers", allowedPlayers);
        chestProtectCfg.save(Api.getConfigFile(Constants.YML_CHEST_PROTECT_FILE_NAME));
    }

    public static void setApprovedChestPlayer(String chestOwnerDcId, String thiefMinecraftName, FileConfiguration chestProtectCfg, Location chestLocation) throws IOException {
        List<String> approvedPlayersForOneChest;
        approvedPlayersForOneChest = chestProtectCfg.getStringList(Constants.YML_PLAYERS + Api.getMinecraftIdFromDCid(chestOwnerDcId) + "." + chestLocation.toString());
        if (!approvedPlayersForOneChest.contains(Api.getMinecraftIdFromMinecraftName(thiefMinecraftName))){
            approvedPlayersForOneChest.add(Api.getMinecraftIdFromMinecraftName(thiefMinecraftName));
        }
        String minecraftID = Api.getMinecraftIdFromDCid(chestOwnerDcId);
        chestProtectCfg.set(Constants.YML_PLAYERS + minecraftID +  "." + chestLocation.toString(), approvedPlayersForOneChest);
        chestProtectCfg.save(Api.getConfigFile(Constants.YML_CHEST_PROTECT_FILE_NAME));
    }

    public static void setIgnoredChests(Location chestLocation, FileConfiguration chestProtectCfg) throws IOException {
        List<String> ignoredChests;
        ignoredChests = chestProtectCfg.getStringList("ignoredChests");
        ignoredChests.add(chestLocation.toString());
        chestProtectCfg.set("ignoredChests", ignoredChests);
        chestProtectCfg.save(Api.getConfigFile(Constants.YML_CHEST_PROTECT_FILE_NAME));
    }
}
