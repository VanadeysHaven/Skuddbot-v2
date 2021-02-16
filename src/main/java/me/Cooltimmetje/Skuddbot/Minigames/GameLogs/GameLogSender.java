package me.Cooltimmetje.Skuddbot.Minigames.GameLogs;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import org.javacord.api.entity.message.Message;

import java.io.File;
import java.util.ArrayList;

/**
 * Responsible for sending the game log to Discord based on reaction adds.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3.1
 * @since 2.3.1
 */
public final class GameLogSender {

    private static ArrayList<GameLogSender> senders = new ArrayList<>();

    public static void runExpire(){
        for(GameLogSender sender : senders) sender.expire();
    }

    private File file;
    private Message message;
    private ReactionButton button;
    private long expireAt;

    public GameLogSender(File file, Message message, Emoji emoji, long expireAfter){
        this.file = file;
        this.message = message;
        button = ReactionUtils.registerButton(message, emoji, e -> uploadFile());
        expireAt = System.currentTimeMillis() + expireAfter;
        senders.add(this);
    }

    private void uploadFile(){
        message.getChannel().sendMessage(file).join();
        file.delete();
        button.unregister();
        senders.remove(this);
    }

    private void expire() {
        if(System.currentTimeMillis() < expireAt) return;

        file.delete();
        button.unregister();
        senders.remove(this);
    }

}
