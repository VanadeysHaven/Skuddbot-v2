package me.Cooltimmetje.Skuddbot.Utilities;

import lombok.Getter;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

/**
 * A reaction on a message a user can click for more information.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class DebugReaction {

    private String output;
    @Getter private String emoji;
    @Getter private Message message;
    private TextChannel channel;
    @Getter private long validUntil;
    @Getter private boolean posted;

    DebugReaction(String output, String emoji, Message message, TextChannel channel, long validUntil){
        this.output = output;
        this.emoji = emoji;
        this.message = message;
        this.channel = channel;
        this.validUntil = validUntil;
        posted = false;
    }

    public void post(){
        if(!posted && System.currentTimeMillis() < validUntil) {
            MessagesUtils.sendPlain(channel, output);
            posted = true;
        }
    }


}
