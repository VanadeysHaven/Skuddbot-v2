package me.Cooltimmetje.Skuddbot.Utilities;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
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
        reactions.add(new DebugReaction(emoji.getUnicode() + " " + output, emoji.getUnicode(), message, message.getChannel(), System.currentTimeMillis() + expireTime));
    }

    public static void addReaction(Message message, Emoji emoji, String output){
        addReaction(message, emoji, output, 30*60*1000);
    }

}