package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Represents a game of blackjack.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class BlackjackGame {

    private static final Logger logger = LoggerFactory.getLogger(BlackjackGame.class);
    private static final String MESSAGE_FORMAT =
            "**BLACKJACK** | *{0}*\n\n" +
                    "**DEALER HAND:** (hand value: {1}) *Dealer draws to 16, stands on 17.*\n" +
                    "{2}\n\n" +
                    "**YOUR HAND:** (hand value {3})\n" +
                    "{4}\n\n" +
                    ">>> {5}";
    private static final String PLAYING_INSTRUCTION = "*Press " + Emoji.H.getUnicode() + " to hit, press " + Emoji.S.getUnicode() + " to stand.*";
    private static final String DOUBLE_INSTRUCTION = "*Press " + Emoji.D.getUnicode() + " to double down.*";

    @Getter
    private Identifier id;

    private User user;
    private Server server;
    private TextChannel channel;
    private Message message;
    private RNGManager random;

    private BlackjackHand dealerHand;
    private Card holeCard;
    private BlackjackHand playerHand;

    public BlackjackGame(Identifier id, TextChannel channel) {
        logger.info("Creating new BlackjackGame for user " + id.toString());
        this.id = id;
        user = Main.getSkuddbot().getApi().getUserById(id.getDiscordId()).join();
        server = Main.getSkuddbot().getApi().getServerById(id.getServerId()).orElse(null); assert server != null;
        this.channel = channel;
        dealerHand = new BlackjackHand();
        playerHand = new BlackjackHand();
        random = new RNGManager();

        setupHands();
        message = MessagesUtils.sendPlain(channel, formatMessage());
        message.addReaction(Emoji.H.getUnicode());
        message.addReaction(Emoji.S.getUnicode());
        message.addReaction(Emoji.D.getUnicode());
    }

    private void setupHands() {
        playerHand.addCard(getNewCard());
        dealerHand.addCard(getNewCard());
        playerHand.addCard(getNewCard());
        holeCard = getNewCard();
    }

    private String formatMessage() {
        String playingInstruction = PLAYING_INSTRUCTION + "\n" + DOUBLE_INSTRUCTION;
        return MessageFormat.format(MESSAGE_FORMAT, user.getDisplayName(server), dealerHand.getHandValue(), dealerHand.toString(), playerHand.getHandValue(), playerHand.toString(), playingInstruction);
    }

    private void updateMessage() {
        message.edit(formatMessage());
    }

    private Card getNewCard() {
        Card card;
        do {
            card = new Card(random);
        } while (gameContainsCard(card));

        return card;
    }

    private boolean gameContainsCard(Card card) {
        if (holeCard != null)
            if (holeCard.equals(card)) return true;

        return dealerHand.containsCard(card) || playerHand.containsCard(card);
    }
}
