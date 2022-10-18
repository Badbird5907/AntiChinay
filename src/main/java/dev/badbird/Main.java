package dev.badbird;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class Main extends ListenerAdapter {
    private static JDA jda;
    private static final List<String> userIds = Arrays.asList(
            "969935460363292702"
    );

    public static void main(String[] args) {
        new Main();
    }
    public Main() {
        try {
            File file = new File("token.txt");
            if (!file.exists()) {
                System.out.println("token.txt not found!");
                file.createNewFile();
                return;
            }
            jda = JDABuilder.createDefault(new String(Files.readAllBytes(file.toPath())))
                    .build()
                    .awaitReady();
            System.out.println("Bot is ready");
            for (Guild guild : jda.getGuilds()) {
                System.out.println(guild.getName());
            }
            jda.addEventListener(this);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onGenericEvent(GenericEvent event) {
        super.onGenericEvent(event);
        System.out.println(event.getClass().getSimpleName());
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        System.out.println("Message event: " + event.getClass().getSimpleName());
        event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
            System.out.println("Message: " + message.getContentRaw());
            if (userIds.contains(message.getAuthor().getId())) {
                System.out.println("User is in list");
                message.addReaction(Emoji.fromUnicode("\uD83C\uDF53")).queue();
            }
        });
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println("Message received: " + event.getMessage().getContentRaw());
        Message message = event.getMessage();
        if (userIds.contains(message.getAuthor().getId())) {
            System.out.println("User is in list");
            message.addReaction(Emoji.fromUnicode("\uD83C\uDF53")).queue();
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        // prevent users from removing the reaction
        event.retrieveMessage().queue(message -> {
            if (userIds.contains(message.getAuthor().getId())) {
                message.addReaction(Emoji.fromUnicode("\uD83C\uDF53")).queue();
            }
        });
    }

    @Override
    public void onMessageReactionRemoveAll(MessageReactionRemoveAllEvent event) {
        event.getGuildChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
            if (userIds.contains(message.getAuthor().getId())) {
                message.addReaction(Emoji.fromUnicode("\uD83C\uDF53")).queue();
            }
        });
    }
}
