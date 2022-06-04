package me.VanadeysHaven.Skuddbot.Utilities;

import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.DebugReaction;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionButton;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionButtonClickedCallback;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.VanadeysHaven.Skuddbot.Main;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.ArrayList;

/**
 * Utilities to do with Messages.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.22
 * @since 2.0
 */
public final class MessagesUtils {

    @Deprecated public static ArrayList<DebugReaction> reactions = new ArrayList<>(); //TODO: CLEANUP

    public static void addReaction(Message message, Emoji emoji, String output, long expireTime, boolean ignoreUser){
        ReactionButtonClickedCallback callback = event -> sendEmoji(message.getChannel(), emoji, output);

        ReactionButton button;
        if(ignoreUser)
            button = ReactionUtils.registerButton(message, emoji, callback);
        else
            button = ReactionUtils.registerButton(message, emoji, callback, message.getAuthor().getId());

        button.expireAfter(expireTime);
        button.setOneTimeUse();
    }

    public static void addReaction(Message message, Emoji emoji, String output){
        addReaction(message, emoji, output, 30*60*1000, false);
    }

    public static Message sendPlain(TextChannel channel, String text){
        return sendPlain(channel, text, false);
    }

    public static Message sendPlain(TextChannel channel, String text, boolean allowEveryone) {
        if(!allowEveryone) text = text.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere");
        return channel.sendMessage(text).join();
    }

    public static void sendEmoji(TextChannel channel, Emoji emoji, String text){
        sendEmoji(channel, emoji, text, false);
    }

    public static void sendEmoji(TextChannel channel, Emoji emoji, String text, boolean allowEveryone){
        sendPlain(channel, emoji.getUnicode() + " " + text, allowEveryone);
    }

    public static Message sendEmbed(TextChannel channel, EmbedBuilder eb){
        return channel.sendMessage(eb).join();
    }

    public static void edit(Message message, String newContent, boolean allowEveryone){
        if(!allowEveryone) newContent = MiscUtils.stripEveryone(newContent);
        message.edit(newContent);
    }

    public static void edit(Message message, String newContent){
        edit(message, newContent, false);
    }

    public static void log(String text){
        if(Main.getSkuddbot().getApi().getYourself().getId() == 209779500018434058L)
            sendPlain(Constants.getLogChannel(), text);
    }


}
