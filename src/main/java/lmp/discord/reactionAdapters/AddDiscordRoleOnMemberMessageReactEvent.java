package lmp.discord.reactionAdapters;

import lmp.LatchDiscord;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class AddDiscordRoleOnMemberMessageReactEvent extends ListenerAdapter {
    private final JDA jda = LatchDiscord.getJDA();

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        MessageChannel channel = event.getChannel();
        String userID = event.getUserId();
        if (channel.getId().equalsIgnoreCase(lmp.Constants.GET_ROLE_CHANNEL_ID)) {
            JDA jda = LatchDiscord.getJDA();
            // If Xbox
            if (event.getMessageId().equalsIgnoreCase(lmp.Constants.XBOX_MESSAGE_ID)) {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.XBOX_ROLE_ID))).queue();
            }
            // If Playstation
            if (event.getMessageId().equalsIgnoreCase(lmp.Constants.PLAYSTATION_MESSAGE_ID)) {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.PLAYSTATION_ROLE_ID))).queue();
            }
            // If Switch
            if (event.getMessageId().equalsIgnoreCase(lmp.Constants.SWITCH_MESSAGE_ID)) {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.SWITCH_ROLE_ID))).queue();
            }
            // If Mobile
            if (event.getMessageId().equalsIgnoreCase(lmp.Constants.MOBILE_MESSAGE_ID)) {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.MOBILE_ROLE_ID))).queue();
            }
            // If Java
            if (event.getMessageId().equalsIgnoreCase(lmp.Constants.JAVA_MESSAGE_ID)) {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.JAVA_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(lmp.Constants.HE_HIM_MESSAGE_ID)) {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.HE_HIM_ROLE_ID))).queue();
            }
            // If She/Her
            if (event.getMessageId().equalsIgnoreCase(lmp.Constants.SHE_HER_MESSAGE_ID)) {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.SHE_HER_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(lmp.Constants.THEY_THEM_MESSAGE_ID)) {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.THEY_THEM_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(lmp.Constants.HE_THEM_MESSAGE_ID)) {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.HE_THEM_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(lmp.Constants.SHE_THEM_MESSAGE_ID)) {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.SHE_THEM_ROLE_ID))).queue();
            }
            // If He/Him
            if (event.getMessageId().equalsIgnoreCase(lmp.Constants.ZE_HIR_MESSAGE_ID)) {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.ZE_HIR_ROLE_ID))).queue();
            }
            if (event.getMessageId().equalsIgnoreCase(lmp.Constants.MEMBER_PLUS_MESSAGE_ID)) {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(userID), Objects.requireNonNull(jda.getRoleById(lmp.Constants.MEMBER_PLUS_ROLE_ID))).queue();
            }

        }
    }
}
