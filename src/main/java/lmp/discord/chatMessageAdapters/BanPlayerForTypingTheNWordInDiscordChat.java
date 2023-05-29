package lmp.discord.chatMessageAdapters;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BanPlayerForTypingTheNWordInDiscordChat extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        String message = e.getMessage().getContentRaw().toLowerCase();
        // If a user says the n word, then ban them
        if (message.replace(" ", "").contains("nigger") || message.replace(" ", "").contains("nigga")) {
            e.getMessage().delete().queue();
            Objects.requireNonNull(e.getMember()).ban(30, TimeUnit.DAYS).reason("Typed the n-word").queue();
        }
    }
}
