package me.Cooltimmetje.Skuddbot.Utilities;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButtonClickedCallback;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButtonRemovedCallback;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility to quickly create paged messages. Meant to be extended so specific behaviour can be added easily.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.2.1
 */
public abstract class PagedMessage {

    private static List<PagedMessage> autoExpireMessages = new ArrayList<>();

    public static void runAutoExpire(){
        Iterator<PagedMessage> it = autoExpireMessages.iterator();

        while(it.hasNext()) {
            PagedMessage message = it.next();
            if (message.getExpireAt() < System.currentTimeMillis()) {
                message.deactivate();
                it.remove();
            }
        }
    }

    @Getter private int page;
    @Getter @Setter private int maxPage;

    @Getter private long expireAt;

    private TextChannel channel;
    private Message message;
    private List<ReactionButton> buttons;
    private long[] userLocks;

    protected PagedMessage(int maxPage, TextChannel channel, long... userLocks) {
        page = 1;
        this.maxPage = maxPage;
        this.channel = channel;
        buttons = new ArrayList<>();
        this.userLocks = userLocks;
    }

    protected void construct(){
        sendMessage();
        addButton(Emoji.ARROW_LEFT, e -> prevPage(), e -> prevPage());
        addButton(Emoji.ARROW_RIGHT, e -> nextPage(), e -> nextPage());
    }

    public ReactionButton addButton(Emoji emoji, ReactionButtonClickedCallback clickCallback, ReactionButtonRemovedCallback removedCallback, long... userLocks){
        ReactionButton button = ReactionUtils.registerButton(message, emoji, clickCallback, removedCallback, userLocks);
        buttons.add(button);
        return button;
    }

    public ReactionButton addButton(Emoji emoji, ReactionButtonClickedCallback clickedCallback, long... userLocks){
        return addButton(emoji, clickedCallback, null, userLocks);
    }

    public ReactionButton addButton(Emoji emoji, ReactionButtonClickedCallback clickedCallback, ReactionButtonRemovedCallback removedCallback){
        return addButton(emoji, clickedCallback, removedCallback, userLocks);
    }

    public ReactionButton addButton(Emoji emoji, ReactionButtonClickedCallback clickedCallback){
        return addButton(emoji, clickedCallback, null, userLocks);
    }

    private void sendMessage(){
        if(message == null)
            message = MessagesUtils.sendPlain(channel, getContent());
        else
            MessagesUtils.edit(message, getContent());
    }

    private void prevPage(){
        setPage(page - 1);
    }

    private void nextPage(){
        setPage(page + 1);
    }

    protected void setPage(int newPage){
        if(checkBounds(newPage)) {
            page = newPage;
            sendMessage();
        }
    }

    private boolean checkBounds(int newPage){
        return newPage >= 1 && newPage <= maxPage;
    }

    public void deactivate(){
        for(ReactionButton button : buttons)
            button.unregister();

        message.removeAllReactions();
        autoExpireMessages.remove(this);
    }

    private void setAutoExpire(long expireAfter){ //millis
        expireAt = System.currentTimeMillis() + expireAfter;
        autoExpireMessages.add(this);
    }

    public void setAutoExpire(int expireAfter){ //seconds
        setAutoExpire((long)expireAfter * 1000);
    }

    public abstract String getContent();


}
