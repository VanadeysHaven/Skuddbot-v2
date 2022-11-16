package me.VanadeysHaven.Skuddbot.Profiles.Pages;

import lombok.Getter;
import lombok.Setter;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionButton;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionButtonClickedCallback;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionButtonRemovedCallback;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class for paged embeds.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.24
 * @since 2.3.24
 */
public class PagedEmbed {

    /** List of all paged embeds that are set to expire. */
    private static List<PagedEmbed> autoExpireMessages = new ArrayList<>();

    /**
     * Run the auto-expire method for all paged embeds.
     */
    public static void runAutoExpire(){
        Iterator<PagedEmbed> it = autoExpireMessages.iterator(); // Create an iterator for the list of paged embeds.

        while(it.hasNext()) { // While there are still paged embeds in the list.
            PagedEmbed message = it.next(); // Get the next paged embed.
            if (message.getExpireAt() < System.currentTimeMillis()) { // If the paged embed has expired.
                message.deactivate(); // Deactivate the paged embed.
                it.remove(); // Remove the paged embed from the list.
            }
        }
    }

    /** The number of the page that is currently being displayed. */
    @Getter private int page;
    /** The amount of pages that the paged embed has. */
    @Getter @Setter private int maxPage;

    /** The time at which the paged embed will expire. */
    @Getter private long expireAt;

    /** The pages of the paged embed. */
    private PageManager<?,?> pageManager;
    /** The channel in which the paged embed is displayed. */
    private TextChannel channel;
    /** The message that the paged embed is displayed in. */
    private Message message;
    /** The buttons that are active on the paged embed. */
    private List<ReactionButton> buttons;
    /** The user that the paged embed is for. */
    private SkuddUser user;
    /** The user that initiated the paged embed.*/
    private long callerId;

    /**
     * Constructor for PagedEmbed.
     *
     * @param pageManager The page manager that manages the pages to be displayed.
     * @param channel The channel in which the paged embed will be displayed in.
     * @param user The user that the paged embed is for.
     * @param callerId The user that initiated the paged embed.
     */
    public PagedEmbed(PageManager<?,?> pageManager, TextChannel channel, SkuddUser user, long callerId) {
        this.pageManager = pageManager; // Set the page manager.
        page = 1; // Set the page to 1.
        this.maxPage = pageManager.getPageAmount(); // Set the max page to the amount of pages in the page manager.
        this.channel = channel; // Set the channel.
        buttons = new ArrayList<>(); // Create a new list for the buttons.
        this.user = user; // Set the user.
        this.callerId = callerId; // Set the caller ID.

        setAutoExpire(1800); // Set the auto-expire to 30 minutes.
        construct(); // Construct the paged embed.
    }

    /**
     * Construct the paged embed.
     */
    protected void construct(){
        sendMessage();  // Send the message.
        addButton(Emoji.ARROW_LEFT, this::prevPage); // Add the previous page button.
        addButton(Emoji.ARROW_RIGHT, this::nextPage); // Add the next page button.
        addButton(Emoji.ARROWS_CC, this::refresh); // Add the refresh button.
    }

    /**
     * Add button to the paged embed.
     * @param emoji The emoji of the button.
     * @param clickCallback The callback for when the button is clicked
     * @param removedCallback The callback for when the button is removed
     * @return The created button.
     */
    public ReactionButton addButton(Emoji emoji, ReactionButtonClickedCallback clickCallback, ReactionButtonRemovedCallback removedCallback){
        ReactionButton button = ReactionUtils.registerButton(message, emoji, clickCallback, removedCallback, callerId); // Create the button.
        buttons.add(button); // Add the button to the list of buttons.
        return button; // Return the button.
    }

    /**
     * Short version of {@link #addButton(Emoji, ReactionButtonClickedCallback, ReactionButtonRemovedCallback)} for when there's no removed callback.
     *
     * @param emoji The emoji of the button.
     * @param clickedCallback The callback for when the button is clicked
     * @return The created button.
     */
    public ReactionButton addButton(Emoji emoji, ReactionButtonClickedCallback clickedCallback){
        return addButton(emoji, clickedCallback, null); // Add the button with no removed callback.
    }

    /**
     * Send or edit the message.
     */
    private void sendMessage(){
        if(message == null)  // If the message is null.
            message = MessagesUtils.sendEmbed(channel, getContent()); // Send the message.
        else // If the message is not null.
            message.edit(getContent()); // Edit the message.
    }

    /**
     * Callback for when the previous page button is clicked.
     *
     * @param e The event that triggered the callback.
     */
    private void prevPage(ReactionButtonClickedEvent e){
        setPage(page - 1); // Set the page to the previous page.
        e.undoReaction(); // Undo the reaction.
    }

    /**
     * Callback for when the next page button is clicked.
     *
     * @param e The event that triggered the callback.
     */
    private void nextPage(ReactionButtonClickedEvent e){
        setPage(page + 1); // Set the page to the next page.
        e.undoReaction(); // Undo the reaction.
    }

    /**
     * Callback for when the refresh button is clicked.
     *
     * @param e The event that triggered the callback.
     */
    private void refresh(ReactionButtonClickedEvent e) {
        sendMessage(); // Just resend the message without changing the page.
        e.undoReaction();  // Undo the reaction.
    }

    /**
     * Change the currently displayed page.
     *
     * @param newPage The new page to display.
     */
    protected void setPage(int newPage){
        if(checkBounds(newPage)) { // If the new page is within the bounds.
            page = newPage; // Set the page to the new page.
            sendMessage(); // Send the message.
        }
    }


    /**
     * Check if the page is within the bounds.
     * @param newPage The page to check.
     * @return If the page is within the bounds.
     */
    private boolean checkBounds(int newPage){
        return newPage >= 1 && newPage <= maxPage; // Check if the page is greater than or equal to 1 and less than or equal to the max page.
    }

    /**
     * Deactivate the paged embed.
     */
    public void deactivate(){
        for(ReactionButton button : buttons) // For each button.
            button.unregister(); // Unregister the button.

        message.removeAllReactions(); // Remove all reactions from the message.
        autoExpireMessages.remove(this); // Remove the paged embed from the list of auto-expire messages.
    }

    /**
     * Set the time after which the paged embed will expire.
     * @param expireAfter The time in milliseconds after which the paged embed will expire.
     */
    private void setAutoExpire(long expireAfter){ //millis
        expireAt = System.currentTimeMillis() + expireAfter; // Set the expiry at time.
        autoExpireMessages.add(this); // Add the paged embed to the list of auto-expire messages.
    }

    /**
     * Set the time after which the paged embed will expire.
     * @param expireAfter The time in seconds after which the paged embed will expire.
     */
    public void setAutoExpire(int expireAfter){ //seconds
        setAutoExpire((long)expireAfter * 1000); // Set the expiry at time.
    }

    /**
     * Get the content of the page.
     *
     * @return The content of the page.
     */
    private EmbedBuilder getContent() {
        return pageManager.getPage(page).generatePage(user); // Get the content of the page.
    }

}
