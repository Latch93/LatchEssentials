package lmp.discord.channelCommands;

import lmp.Constants;
import lmp.LatchDiscord;
import lmp.Main;
import lmp.api.Api;
import lmp.constants.YmlFileNames;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static lmp.LatchDiscord.*;

public class GeneralDiscordCommands extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        messageChannel = e.getChannel();
        messageContents = e.getMessage().getContentRaw();
        senderDiscordUser = e.getAuthor();
        senderDiscordMember = e.getMember();
        senderDiscordUsername = senderDiscordUser.getName();
        senderDiscordUserID = senderDiscordUser.getId();
        jda = getJDA();
        FileConfiguration enabledEventsCfg = Api.getFileConfiguration(YmlFileNames.YML_ENABLED_EVENTS_FILE_NAME);
        if (enabledEventsCfg.getBoolean("enableGeneralDiscordCommands") && senderDiscordMember != null && !senderDiscordUserID.equalsIgnoreCase(Constants.LATCH93BOT_USER_ID)) {
            if (messageContents.equalsIgnoreCase("!discordID")) {
                messageChannel.sendMessage("Hi " + senderDiscordUsername + "! Your Discord UserID is " + senderDiscordUserID).queue();
            }
            if (messageContents.equalsIgnoreCase("!joinTime")) {
                messageChannel.sendMessage(senderDiscordUsername + " joined on " + senderDiscordMember.getTimeJoined().toString().split("T")[0]).queue();
            }
            if (messageContents.equalsIgnoreCase("!link")) {
                senderDiscordUser.openPrivateChannel().flatMap(dm -> dm.sendMessage("IP = latch.ddns.net\n" +
                        "Java Port Number = 60\n" +
                        "Bedrock Port Number = 19132\n" +
                        "Run the following command in your minecraft client chat after you join:\n" +
                        "/lmp link " + e.getAuthor().getId())).queue(null, new ErrorHandler()
                        .handle(ErrorResponse.CANNOT_SEND_TO_USER,
                                (ex) -> Main.log.warning("Cannot send link message to " + senderDiscordUsername)));
                messageChannel.sendMessage(senderDiscordUsername + " --- Check your Discord for a private message from my bot containing your link command. <:LatchPOG:957363669388386404>").queue();
                if (doesDiscordMemberHasRole(senderDiscordMember, Constants.MEMBER_ROLE_ID) == null){
                    Objects.requireNonNull(jda.getGuildById(lmp.Constants.GUILD_ID)).addRoleToMember(UserSnowflake.fromId(senderDiscordUserID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.MEMBER_ROLE_ID))).queue();
                }
            }
            if (messageContents.equalsIgnoreCase("!linkTwitch")) {
                messageChannel.sendMessage(senderDiscordUsername + " --- Type the following command into Latch's Twitch chat to connect your accounts -> !lmp link " + senderDiscordUser.getId()).queue();
            }
            if (messageContents.contains("!findusers")) {
                ArrayList<String> membersWithRole = new ArrayList<>();
                String[] splitArr = messageContents.split(" ");
                String roleID = splitArr[1];
                String roleName = LatchDiscord.getJDA().getRoleById(roleID).getName();
                for (Member member : LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getMembersWithRoles()){
                    for (Role role : member.getRoles()){
                        if (role.getId().equals(roleID)){
                            Main.log.info(member.getEffectiveAvatarUrl());
                            if (Api.getMinecraftIdFromDCid(member.getId()) != null) {
                                membersWithRole.add(member.getEffectiveName());
                                try {
                                    URL url = new URL(member.getEffectiveAvatarUrl());
                                    InputStream in = new BufferedInputStream(url.openStream());
                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    byte[] buf = new byte[1024];
                                    int n = 0;
                                    while (-1 != (n = in.read(buf))) {
                                        out.write(buf, 0, n);
                                    }
                                    out.close();
                                    in.close();
                                    byte[] response = out.toByteArray();
                                    FileOutputStream fos = new FileOutputStream("E:\\Test Server\\plugins\\LatchEssentials\\profiles\\" + member.getEffectiveName() + ".png");
                                    fos.write(response);
                                    fos.close();
                                } catch (RuntimeException | IOException error) {
                                    Main.log.warning(error.getMessage());
                                }
                            }
                        }
                    }
                }
                Main.log.info("Role Name: " + roleName);
                Main.log.info(membersWithRole.toString());
            }
            if (messageContents.contains("!getAllLinkedUsers")) {
                Main.log.info(Api.getDiscordNamesOfLinkedPlayersStillInDiscord().toString());
            }
            if (messageContents.contains("!getProfilePicture")) {
                String[] splitArr = messageContents.split(" ");
                String memberID = splitArr[1];
                Member member = LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getMemberById(memberID);
                try {
                    URL url = new URL(member.getEffectiveAvatarUrl());
                    InputStream in = new BufferedInputStream(url.openStream());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int n = 0;
                    while (-1 != (n = in.read(buf))) {
                        out.write(buf, 0, n);
                    }
                    out.close();
                    in.close();
                    byte[] response = out.toByteArray();
                    FileOutputStream fos = new FileOutputStream("E:\\Test Server\\plugins\\LatchEssentials\\profiles\\" + member.getEffectiveName() + ".png");
                    fos.write(response);
                    fos.close();
                } catch (RuntimeException | IOException error) {
                    Main.log.warning(error.getMessage());
                }
            }
            if (!messageContents.equalsIgnoreCase("!link") && senderDiscordMember != null && doesDiscordMemberHasRole(senderDiscordMember, Constants.MEMBER_ROLE_ID) == null) {
                messageChannel.sendMessage("Type !link in this channel to get your link command and the Server IP dm'd to you by my bot.").queue();
                Objects.requireNonNull(jda.getGuildById(lmp.Constants.GUILD_ID)).addRoleToMember(UserSnowflake.fromId(senderDiscordUserID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.MEMBER_ROLE_ID))).queue();
            }
        }
    }

    public Role doesDiscordMemberHasRole(Member senderDiscordMember, String roleID) {
        List<Role> roles = senderDiscordMember.getRoles();
        return roles.stream()
                .filter(role -> role.getId().equals(roleID)) // filter by role name
                .findFirst() // take first result
                .orElse(null); // else return null
    }
}
