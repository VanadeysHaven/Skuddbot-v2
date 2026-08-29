package me.VanadeysHaven.Skuddbot.Utilities;

import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionButton;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionButtonClickedCallback;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.VanadeysHaven.Skuddbot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * Utilities to do with Messages.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public final class MessagesUtils {

    public static void addReaction(Message message, Emoji emoji, String output, long expireTime, boolean ignoreUser){
        ReactionButtonClickedCallback callback = event -> sendEmoji(message.getChannel(), emoji, output);

        ReactionButton button;
        if(ignoreUser)
            button = ReactionUtils.registerButton(message, emoji, callback);
        else
            button = ReactionUtils.registerButton(message, emoji, callback, message.getAuthor().getIdLong());

        button.expireAfter(expireTime);
        button.setOneTimeUse();
    }

    public static void addReaction(Message message, Emoji emoji, String output){
        addReaction(message, emoji, output, 30*60*1000, false);
    }

    public static Message sendPlain(MessageChannel channel, String text){
        return sendPlain(channel, text, false);
    }

    public static Message sendPlain(MessageChannel channel, String text, boolean allowEveryone) {
        if(!allowEveryone) text = text.replace("@everyone", "@​everyone").replace("@here", "@​here");
        return channel.sendMessage(text).complete();
    }

    public static void sendEmoji(MessageChannel channel, Emoji emoji, String text){
        sendEmoji(channel, emoji, text, false);
    }

    public static void sendEmoji(MessageChannel channel, Emoji emoji, String text, boolean allowEveryone){
        sendPlain(channel, emoji.getUnicode() + " " + text, allowEveryone);
    }

    public static Message sendEmbed(MessageChannel channel, EmbedBuilder eb){
        return channel.sendMessageEmbeds(eb.build()).complete();
    }

    public static void edit(Message message, String newContent, boolean allowEveryone){
        if(!allowEveryone) newContent = MiscUtils.stripEveryone(newContent);
        message.editMessage(newContent).queue();
    }

    public static void edit(Message message, String newContent){
        edit(message, newContent, false);
    }

    public static void log(String text){
        if(Main.getSkuddbot().getApi().getSelfUser().getIdLong() == 209779500018434058L)
            sendPlain(Constants.getLogChannel(), text);
    }


}
