package me.Cooltimmetje.Skuddbot.Minigames.GameLogs;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import org.javacord.api.entity.message.Message;

import java.io.File;

/**
 * Responsible for sending the game log to Discord based on reaction adds.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3.1
 * @since 2.3.1
 */
public final class GameLogSender {

    private File file;
    private Message message;
    private ReactionButton button;
    private long expireAt;

    public GameLogSender(File file, Message message, Emoji emoji, long expireAfter){
        this.file = file;
        this.message = message;
        ReactionUtils.registerButton(message, emoji, e -> uploadFile());
        expireAt = System.currentTimeMillis() + expireAfter;
    }

    public void uploadFile(){
        message.getChannel().sendMessage(file).join();
        file.delete();
    }



}
