package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands.BlackjackHand;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands.DealerHand;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands.PlayerHand;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands.SplitPlayerHand;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Represents a game of blackjack.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.0
 */
public class BlackjackGame {

    private enum GameState {
        NORMAL_HAND_PLAYING, FIRST_SPLIT_HAND_PLAYING, SECOND_SPLIT_HAND_PLAYING, DEALER_PLAYING, GAME_ENDED
    }

    //Format pieces
    private static final String HEADER = Emoji.BLACK_JOKER.getUnicode() + " **BLACKJACK** | *{0}*";
    private static final String DEALER_FORMAT = "**DEALER HAND:** ({1}) *Dealer draws to 16, stands on 17.*" +
            "{2}";
    private static final String ONE_HANDED_PLAYER_FORMAT = "**PLAYER HAND:** ({2})\n" +
            "{3}";
    private static final String TWO_HANDED_PLAYER_FORMAT = "**PLAYER HANDS:** ({2}/{3})\n" +
            "{4} {5}\n" +
            "{6} {7}\n";

    //Complete formats
    private static final String NORMAL_FORMAT = HEADER + "\n\n" +
            DEALER_FORMAT + "\n\n" +
            ONE_HANDED_PLAYER_FORMAT + "\n\n" +
            ">>> {4}";
    private static final String SPLIT_FORMAT = HEADER + "\n\n" +
            DEALER_FORMAT + "\n\n" +
            TWO_HANDED_PLAYER_FORMAT + "\n\n" +
            ">>> {8}";

    //Playing instructions
    private static final String HIT_STAND = "Press the " + Emoji.H.getUnicode() + " reaction to hit, press the " + Emoji.S.getUnicode() + " reaction to stand.";
    private static final String DOUBLE_DOWN = "Press the " + Emoji.D.getUnicode() + " reaction to double down.";
    private static final String SPLIT = "Press the " + Emoji.ARROWS_LR.getUnicode() + " reaction to split.";

    @Getter private ServerMember player;
    private int initialBet;
    private BlackjackGameManager manager;
    private GameState gameState;
    private PlayerHand playerHand;
    private DealerHand dealerHand;
    private Message message;

    private ReactionButton hitButton;
    private ReactionButton standButton;
    private ReactionButton doubleDownButton;
    private ReactionButton splitButton;
    private ArrayList<ReactionButton> buttons;

    public BlackjackGame(ServerMember player, int initialBet, BlackjackGameManager manager, TextChannel channel){
        this.player = player;
        this.initialBet = initialBet;
        this.manager = manager;
        gameState = GameState.NORMAL_HAND_PLAYING;
        setupHands();
        preGameChecks();
        sendMessage(channel);
    }

    private void setupHands(){
        playerHand = new PlayerHand(initialBet);
        dealerHand = new DealerHand();

        playerHand.addCard(BlackjackHand.ONE, manager.drawCard());
        dealerHand.addCard(BlackjackHand.ONE, manager.drawCard());
        playerHand.addCard(BlackjackHand.ONE, manager.drawCard());
        dealerHand.setHoleCard(manager.drawCard());
    }

    private void preGameChecks(){

    }

    private void sendMessage(){
        if(message == null) throw new IllegalStateException("Message has not been sent yet.");
        message.edit(formatMessage());
    }

    private void sendMessage(TextChannel channel){
        if(message == null)
            message = MessagesUtils.sendPlain(channel, formatMessage());
        else
            message.edit(formatMessage());
    }

    private String formatMessage(){
        if(isHandSplit())
            return formatSplitHand();
        else
            return formatSingleHand();
    }

    private String formatSingleHand(){
        String playerName = player.getDisplayName();
        int dealerHandValue = dealerHand.getHandValue(BlackjackHand.ONE);
        String dealerHandStr = dealerHand.formatHand(BlackjackHand.ONE);
        int playerHandValue = playerHand.getHandValue(BlackjackHand.ONE);
        String playerHandStr = playerHand.formatHand(BlackjackHand.ONE);
        String playingInstruction = formatPlayingInstructions();

        return MessageFormat.format(NORMAL_FORMAT, playerName, dealerHandValue, dealerHandStr, playerHandValue, playerHandStr, playingInstruction);
    }

    private String formatSplitHand(){
        String playerName = player.getDisplayName();
        int dealerHandValue = dealerHand.getHandValue(BlackjackHand.ONE);
        String dealerHandStr = dealerHand.formatHand(BlackjackHand.ONE);
        int firstPlayerHandValue = playerHand.getHandValue(BlackjackHand.ONE);
        int secondPlayerHandValue = getSplitHand().getHandValue(SplitPlayerHand.TWO);
        String firstPlayerHandStr = playerHand.formatHand(BlackjackHand.ONE);
        String secondPlayerHandStr = getSplitHand().formatHand(SplitPlayerHand.TWO);
        String playingInstruction = formatPlayingInstructions();

        return MessageFormat.format(SPLIT_FORMAT, playerName, dealerHandValue, dealerHandStr, firstPlayerHandValue, secondPlayerHandValue, firstPlayerHandStr, secondPlayerHandStr, playingInstruction);
    }

    private String formatPlayingInstructions(){
        return "TODO"; //TODO
    }

    private void setupButtons(){

    }

    private void hit(){

    }

    private void stand(){

    }

    private void doubleDown(){

    }

    private void split(){

    }

    private void postMoveChecks(){

    }

    private SplitPlayerHand getSplitHand(){
        if(!isHandSplit()) throw new IllegalStateException("The hand of the player is not split.");
        return (SplitPlayerHand) playerHand;
    }

    private boolean isHandSplit(){
        return playerHand instanceof SplitPlayerHand;
    }

}
