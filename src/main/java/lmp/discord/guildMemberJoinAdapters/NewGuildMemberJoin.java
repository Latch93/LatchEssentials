package lmp.discord.guildMemberJoinAdapters;

import lmp.LatchDiscord;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;

import static lmp.LatchDiscord.senderDiscordUsername;

public class NewGuildMemberJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        TextChannel generalChannel = LatchDiscord.getJDA().getTextChannelById(lmp.Constants.GENERAL_CHANNEL_ID);
        assert generalChannel != null;
        event.getUser().openPrivateChannel().flatMap(dm -> dm.sendMessage("""
                Welcome to LMP (Latch Multiplayer)
                Users on PS4|XBox|Switch|Mobile are able to join
                Type in the General Channel to get the member role - Just say Hi or introduce yourself :)
                Doing this assigns you the member role and access to all of the channels.
                For Bedrock|Console users, you need to download the mobile app Bedrock Together:
                Android: https://play.google.com/store/apps/details?id=pl.extollite.bedrocktogetherapp&hl=en_US&gl=US
                iOS: https://apps.apple.com/us/app/bedrocktogether/id1534593376
                When you first join the server, you won't be able to move because you need to link your Discord account with your Minecraft Account.
                I link accounts so you can use the features I coded into the game.
                View our wiki with commands and other information here -> https://github.com/Latch93/DiscordText/wiki/Server-Commands
                """)).queue(null, new ErrorHandler()
                .handle(ErrorResponse.CANNOT_SEND_TO_USER,
                        (ex) -> {
                            generalChannel.sendMessage("Cannot send link message to " + senderDiscordUsername).queue();
                        }));
        event.getUser().openPrivateChannel().flatMap(dm -> dm.sendMessage("When you first join the server, you won't be able to move because you need to link your Discord account with your Minecraft Account.\n" +
                "IP Address = latch.ddns.net\n" +
                "Java Port Number = 60\n" +
                "Bedrock Port Number = 19132\n" +
                "Run the following command in your minecraft client chat after you join the server:\n" +
                "/lmp link " + event.getUser().getId() + "\n" +
                " If you have any questions, feel free to post them in the Discord and Happy Mining!!!")).queue(null, new ErrorHandler()
                .handle(ErrorResponse.CANNOT_SEND_TO_USER,
                        (ex) -> generalChannel.sendMessage("Cannot send link message to " + senderDiscordUsername).queue()));
        generalChannel.sendMessage("Welcome <@" + event.getUser().getId() + "> Glad to have you <:LatchPOG:957363669388386404> Check your Discord Inbox for a DM from my Bot with your link command.").queue();
    }
}
