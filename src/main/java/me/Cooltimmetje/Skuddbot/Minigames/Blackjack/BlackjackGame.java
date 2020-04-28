package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Represents a game of blackjack.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.1.1
 * @since ALPHA-2.0
 */
public class BlackjackGame {

    private enum GameState {
        PLAYER_PLAYING, DEALER_PLAYING, GAME_ENDED
    }

    private static final Logger logger = LoggerFactory.getLogger(BlackjackGame.class);
    private static final String MESSAGE_FORMAT = Emoji.BLACK_JOKER.getUnicode() + " **BLACKJACK** | *{0}*\n\n" +
                    "**DEALER HAND:** (hand value: {1}) *Dealer draws to 16, stands on 17.*\n" +
                    "{2}\n\n" +
                    "**YOUR HAND:** (hand value: {3})\n" +
                    "{4}\n\n" +
                    ">>> {5}";
    private static final String DEFAULT_PLAYING_INSTRUCTION = "*Press " + Emoji.H.getUnicode() + " to hit, press " + Emoji.S.getUnicode() + " to stand.*";
    private static final String DOUBLE_INSTRUCTION = "*Press " + Emoji.D.getUnicode() + " to double down.*";
    private static final ProfileManager pm = ProfileManager.getInstance();
    private static final int BASE_REWARD = 50;
    private static final int WIN_REWARD = 75;
    private static final int TWENTY_ONE_BONUS = 100;
    private static final int BLACKJACK_BONUS = 150;


    @Getter
    private Identifier id;

    private User user;
    private Server server;
    private TextChannel channel;
    @Getter private Message message;
    private RNGManager random;

    private GameState gameState;
    private BlackjackHand dealerHand;
    private Card holeCard;
    private BlackjackHand playerHand;
    private String playingInstruction;
    private boolean doubledDown;

    public BlackjackGame(Identifier id, TextChannel channel) {
        logger.info("Creating new BlackjackGame for user " + id.toString());
        this.id = id;
        user = Main.getSkuddbot().getApi().getUserById(id.getDiscordId()).join();
        server = Main.getSkuddbot().getApi().getServerById(id.getServerId()).orElse(null); assert server != null;
        this.channel = channel;
        dealerHand = new BlackjackHand();
        playerHand = new BlackjackHand();
        random = new RNGManager();
        gameState = GameState.PLAYER_PLAYING;
        playingInstruction = DEFAULT_PLAYING_INSTRUCTION;
        doubledDown = false;

        setupHands();

        message = MessagesUtils.sendPlain(channel, formatMessage());

        message.addReaction(Emoji.H.getUnicode());
        message.addReaction(Emoji.S.getUnicode());
        if(playerHand.getHandValue() <= 10)
            message.addReaction(Emoji.D.getUnicode());
    }

    private void setupHands() {
        playerHand.addCard(getNewCard());
        dealerHand.addCard(getNewCard());
        playerHand.addCard(getNewCard());
        holeCard = getNewCard();
    }

    public void preGameChecks(){
        if(playerHand.getHandValue() == 21){
            if(playerHand.isBlackjack()) {
                endGame(false);
            } else {
                playingInstruction = "**You got 21! Dealer playing...**";
                updateMessage();
                stand();
            }
        }
    }

    private String formatMessage() {
        String playingInstruction = this.playingInstruction;
        if(playerHand.getHandSize() == 2 && playerHand.getHandValue() <= 10 && gameState == GameState.PLAYER_PLAYING)
            playingInstruction += "\n" + DOUBLE_INSTRUCTION;
        return MessageFormat.format(MESSAGE_FORMAT, user.getDisplayName(server), dealerHand.getHandValue(), dealerHand.toString(), playerHand.getHandValue(), playerHand.toString(), playingInstruction);
    }

    private void updateMessage() {
        message.edit(formatMessage());
    }

    public void hit(){
        if(gameState != GameState.PLAYER_PLAYING)
            return;

        playerHand.addCard(getNewCard());
        updateMessage();

        if(playerHand.getHandValue() > 21)
            endGame(false);

        if(playerHand.getHandValue() == 21){
            playingInstruction = "**You got 21! Dealer playing...**";
            updateMessage();
            stand();
        }

        message.removeReactionByEmoji(user, Emoji.H.getUnicode());
        message.removeReactionByEmoji(Emoji.D.getUnicode());
    }

    public void stand(){
        gameState = GameState.DEALER_PLAYING;
        dealerPlays();
    }

    public void doubleDown(){
        if(playerHand.getHandSize() != 2 || playerHand.getHandValue() > 10)
            return;

        playingInstruction = "**Doubled down. Dealer playing...**";
        playerHand.addCard(getNewCard());
        updateMessage();
        doubledDown = true;
        stand();
    }

    private void dealerPlays() {
        while (dealerHand.getHandValue() < 17)
            dealerHand.addCard(getNewCard());

        endGame(true);
    }

    private void endGame(boolean delay){
        gameState = GameState.GAME_ENDED;
        if(delay) {
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            message.getChannel().type();
            exec.schedule(this::wrapUpGame, 5, TimeUnit.SECONDS);
        } else {
            wrapUpGame();
        }
        BlackjackCommand.cleanUp(id);
    }

    private void wrapUpGame(){
        message.removeAllReactions();
        SkuddUser su = pm.getUser(id);
        int xpReward = 0;
        if(dealerHand.getHandSize() == 1)
            dealerHand.addCard(holeCard);

        if(playerHand.getHandValue() == 21){
            su.getStats().incrementInt(Stat.BJ_TWENTY_ONES);
            xpReward += BASE_REWARD + TWENTY_ONE_BONUS;
            if(playerHand.isBlackjack()){
                su.getStats().incrementInt(Stat.BJ_BLACKJACKS);
                su.getStats().incrementInt(Stat.BJ_WINS);
                playingInstruction = "**BLACKJACK! You win!**";
                xpReward += BLACKJACK_BONUS + WIN_REWARD;
            } else {
                if (dealerHand.getHandValue() == playerHand.getHandValue()) {
                    su.getStats().incrementInt(Stat.BJ_PUSHES);
                    playingInstruction = "**You got 21, but tied with the dealer.**";
                } else {
                    su.getStats().incrementInt(Stat.BJ_WINS);
                    xpReward += WIN_REWARD;
                    playingInstruction = "**21! You win!**";
                }
            }
        } else if(playerHand.getHandValue() > 21){
            su.getStats().incrementInt(Stat.BJ_LOSSES);
            playingInstruction = "**You busted! Better luck next time!**";
        } else if(playerHand.getHandValue() < 21) {
            if(dealerHand.getHandValue() > playerHand.getHandValue()){
                if(dealerHand.getHandValue() <= 21) {
                    su.getStats().incrementInt(Stat.BJ_LOSSES);
                    playingInstruction = "**Your hand value is lower than that of the dealer. You lose!**";
                } else {
                    su.getStats().incrementInt(Stat.BJ_WINS);
                    xpReward += BASE_REWARD + WIN_REWARD;
                    playingInstruction = "**The dealer busted! You win!**";
                }
            } else if (dealerHand.getHandValue() == playerHand.getHandValue()) {
                su.getStats().incrementInt(Stat.BJ_PUSHES);
                xpReward += BASE_REWARD;
                playingInstruction = "**PUSH! You tied with the dealer!**";
            } else if (dealerHand.getHandValue() < playerHand.getHandValue()){
                su.getStats().incrementInt(Stat.BJ_WINS);
                xpReward += BASE_REWARD + WIN_REWARD;
                playingInstruction = "**Your hand has a higher value than that of the dealer. You win!**";
            }
        }

        if(doubledDown)
            xpReward *= 2;

        if(xpReward > 0)
            playingInstruction += " | *Reward: +" + xpReward + "* <:xp_icon:458325613015466004>";

        su.getStats().incrementInt(Stat.EXPERIENCE, xpReward);
        updateMessage();
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
