package lmp.discord.guildMemberRoleRemoveAdapters;

import lmp.api.Api;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RemoveServerRoleOnGuildMemberRemove extends ListenerAdapter {

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
        if (e.getRoles().get(0).getId().equalsIgnoreCase(lmp.Constants.ADMIN_ROLE_ID)) {
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "admin");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(lmp.Constants.MOD_ROLE_ID)) {
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "mod");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(lmp.Constants.JR_MOD_ROLE_ID)) {
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "jr-mod");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(lmp.Constants.HELPER_ROLE_ID)) {
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "helper");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(lmp.Constants.BUILDER_ROLE_ID)) {
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "builder");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(lmp.Constants.HARDCORE_ROLE_ID)) {
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "hardcore");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(lmp.Constants.DONOR_ROLE_ID)) {
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "donor");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(lmp.Constants.DONOR_PLUS_ROLE_ID)) {
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "donor+");
        } else if (e.getRoles().get(0).getId().equalsIgnoreCase(lmp.Constants.DONOR_PLUS_PLUS_ROLE_ID)) {
            Api.removePlayerFromPermissionGroup(Api.getMinecraftIdFromDCid(e.getUser().getId()), "donor++");
        }
    }
}
