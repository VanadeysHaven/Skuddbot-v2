package me.Cooltimmetje.Skuddbot.Utilities;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import java.util.ArrayList;

/**
 * Utilities to do with Messages.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class MessagesUtils {

    public static ArrayList<DebugReaction> reactions = new ArrayList<>(); //TODO: CLEANUP

    public static void addReaction(Message message, Emoji emoji, String output, long expireTime){
        message.addReaction(emoji.getUnicode());
        reactions.add(new DebugReaction(output, emoji, message, message.getChannel(), System.currentTimeMillis() + expireTime));
    }

    public static void addReaction(Message message, Emoji emoji, String output){
        addReaction(message, emoji, output, 30*60*1000);
    }

    public static void sendPlain(TextChannel channel, String text){
        sendPlain(channel, text, false);
    }

    public static void sendPlain(TextChannel channel, String text, boolean allowEveryone) {
        if(!allowEveryone) text = text.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere");
        channel.sendMessage(text);
    }

    public static void sendEmoji(TextChannel channel, Emoji emoji, String text){
        sendEmoji(channel, emoji, text, false);
    }

    public static void sendEmoji(TextChannel channel, Emoji emoji, String text, boolean allowEveryone){
        sendPlain(channel, emoji.getUnicode() + " " + text, allowEveryone);
    }

}
