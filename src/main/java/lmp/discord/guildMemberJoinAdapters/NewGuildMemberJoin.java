package lmp.discord.guildMemberJoinAdapters;

import lmp.LatchDiscord;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class NewGuildMemberJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        event.getUser().openPrivateChannel().flatMap(dm -> dm.sendMessage("""
                Welcome to LMP (Latch Multiplayer)
                Users on PS4|XBox|Switch|Mobile are able to join
                Type in the General Channel to get the member role - Just say Hi or introduce yourself :)
                Doing this assigns you the member role and access to all of the channels.
                For Bedrock|Console users, you need to download the mobile app Bedrock Together:
                Android: https://play.google.com/store/apps/details?id=pl.extollite.bedrocktogetherapp&hl=en_US&gl=US
                iOS: https://apps.apple.com/us/app/bedrocktogether/id1534593376
                When you first join the server, you won't be able to move because you need to link your Discord account with your Minecraft Account.
                In order to link accounts and move around the server freely, go to the General channel in the Discord Server and type the following command -> !link
                My bot will generate a command for you run on your minecraft client chat.
                Copy and paste this command or type it out, its formatted like this -> /lmp link [discordID]
                I link accounts so you can use the features I coded into the game.
                View our wiki with commands and other information here -> https://github.com/Latch93/DiscordText/wiki/Server-Commands
                If you have any questions, feel free to post them in the Discord and Happy Mining!!!""")).queue();
        TextChannel generalChannel = LatchDiscord.getJDA().getTextChannelById(lmp.Constants.GENERAL_CHANNEL_ID);
        assert generalChannel != null;
        generalChannel.sendMessage("Welcome <@" + event.getUser().getId() + "> Glad to have you <:LatchPOG:957363669388386404>").queue();
    }
}
