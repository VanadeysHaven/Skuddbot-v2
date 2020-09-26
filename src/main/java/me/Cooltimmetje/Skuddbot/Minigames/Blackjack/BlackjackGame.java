package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
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
    private static final String DEALER_FORMAT = "**DEALER HAND:** ({1}) *Dealer draws to 16, stands on 17.*\n" +
            "{2}";
    private static final String ONE_HANDED_PLAYER_FORMAT = "**PLAYER HAND:** ({3})\n" +
            "{4}";
    private static final String TWO_HANDED_PLAYER_FORMAT = "**PLAYER HANDS:** ({3}/{4})\n" +
            "{5} {6}\n" +
            "{7} {8}";

    //Complete formats
    private static final String NORMAL_FORMAT = HEADER + "\n\n" +
            DEALER_FORMAT + "\n\n" +
            ONE_HANDED_PLAYER_FORMAT + "\n\n" +
            ">>> {5}";
    private static final String SPLIT_FORMAT = HEADER + "\n\n" +
            DEALER_FORMAT + "\n\n" +
            TWO_HANDED_PLAYER_FORMAT + "\n\n" +
            ">>> {8}";

    //Playing instructions
    private enum PlayingInstruction {

        PLAYER_PLAYING("[playing instructions here]"),
        PLAYER_GOT_21("**You got 21! Dealer playing...**"),
        PLAYER_GOT_BJ("**BLACKJACK! You win!**"),
        PLAYER_HIGHER_THAN_DEALER("**You win! Your hand has a higher value than that of the dealer."),
        PLAYER_LOWER_THAN_DEALER("**You lose! Your hand has a lower value than that of the dealer."),
        PLAYER_TIED_WITH_DEALER("**PUSH! You tied with the dealer."),
        PLAYER_STANDING("**Standing. Dealer playing...**"),
        PLAYER_BUSTED("You busted, better luck next time.");

        @Getter private String instruction;

        PlayingInstruction(String instruction){
            this.instruction = instruction;
        }

    }

    private static final String HIT_STAND = "*Press the " + Emoji.H.getUnicode() + " reaction to hit, press the " + Emoji.S.getUnicode() + " reaction to stand.*";
    private static final String DOUBLE_DOWN = "*Press the " + Emoji.D.getUnicode() + " reaction to double down.*";
    private static final String SPLIT = "*Press the " + Emoji.ARROWS_LR.getUnicode() + " reaction to split.*";

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

    private PlayingInstruction playingInstruction;
    private boolean doubleDownAllowed;
    private boolean splitAllowed;

    public BlackjackGame(ServerMember player, int initialBet, BlackjackGameManager manager, TextChannel channel){
        this(player, initialBet, manager, channel, null);
    }

    public BlackjackGame(ServerMember player, int initialBet, BlackjackGameManager manager, TextChannel channel, String handInstruction){
        this.player = player;
        this.initialBet = initialBet;
        this.manager = manager;
        gameState = GameState.NORMAL_HAND_PLAYING;
        playingInstruction = PlayingInstruction.PLAYER_PLAYING;
        buttons = new ArrayList<>();
        setupHands(handInstruction);
        preGameChecks();
        sendMessage(channel);

        if(gameState == GameState.GAME_ENDED)
            return;

        setupButtons();
    }

    private void setupHands(String handInstruction) {
        playerHand = new PlayerHand(initialBet);
        dealerHand = new DealerHand();

        if (handInstruction == null) {
            playerHand.addCard(BlackjackHand.ONE, manager.drawCard());
            dealerHand.addCard(BlackjackHand.ONE, manager.drawCard());
            playerHand.addCard(BlackjackHand.ONE, manager.drawCard());
            dealerHand.setHoleCard(manager.drawCard());
            return;
        } else if (handInstruction.equalsIgnoreCase("double")) {
            playerHand.addCard(BlackjackHand.ONE, new Card(Card.Rank.FOUR, Card.Suit.HEARTS));
            playerHand.addCard(BlackjackHand.ONE, new Card(Card.Rank.SIX, Card.Suit.CLUBS));
        } else if (handInstruction.equalsIgnoreCase("split")) {
            playerHand.addCard(BlackjackHand.ONE, new Card(Card.Rank.ACE, Card.Suit.HEARTS));
            playerHand.addCard(BlackjackHand.ONE, new Card(Card.Rank.ACE, Card.Suit.CLUBS));
        }

        dealerHand.addCard(BlackjackHand.ONE, manager.drawCard());
        dealerHand.setHoleCard(manager.drawCard());
    }

    private void preGameChecks(){
        if(playerHand.isBlackjack(BlackjackHand.ONE)){
            playingInstruction = PlayingInstruction.PLAYER_GOT_BJ;
            endGame();
            return;
        }
        if(playerHand.getHandValue(BlackjackHand.ONE) == 21) {
            playingInstruction = PlayingInstruction.PLAYER_GOT_21;
            stand();
            return;
        }

        doubleDownAllowed = playerHand.isDoubleDownAllowed(BlackjackHand.ONE);
        splitAllowed = playerHand.isSplitAllowed();
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
        if(playingInstruction == PlayingInstruction.PLAYER_PLAYING){
            StringBuilder sb = new StringBuilder(HIT_STAND);
            if(doubleDownAllowed)
                sb.append("\n").append(DOUBLE_DOWN).append(" ");
            if(splitAllowed)
                sb.append(SPLIT);

            return sb.toString().trim();
        }

        return playingInstruction.getInstruction();
    }

    private void setupButtons(){
        ReactionButton hitButton = ReactionUtils.registerButton(message, Emoji.H, this::hitButton, player.getId().getDiscordId());
        this.hitButton = hitButton;
        buttons.add(hitButton);

        ReactionButton standButton = ReactionUtils.registerButton(message, Emoji.S, this::standButton, player.getId().getDiscordId());
        this.standButton = standButton;
        buttons.add(standButton);

        if(doubleDownAllowed || splitAllowed) { //The reason why we check for split here too is to make sure the buttons always appear in the same order. - When double down is not allowed this button is disabled anyway.
            ReactionButton doubleDownButton = ReactionUtils.registerButton(message, Emoji.D, this::doubleDownButton, player.getId().getDiscordId());
            this.doubleDownButton = doubleDownButton;
            buttons.add(doubleDownButton);
        }

        if(splitAllowed) {
            ReactionButton splitButton = ReactionUtils.registerButton(message, Emoji.ARROWS_LR, this::splitButton, player.getId().getDiscordId());
            this.splitButton = splitButton;
            buttons.add(splitButton);
        }

        setButtonStates();
    }

    private void hitButton(ReactionButtonClickedEvent event){

    }

    private void hit(){

    }

    private void standButton(ReactionButtonClickedEvent event){

    }

    private void stand(){

    }


    private void doubleDownButton(ReactionButtonClickedEvent event){
    }

    private void doubleDown(){

    }

    private void splitButton(ReactionButtonClickedEvent event){

    }

    private void split(){

    }

    private void postMoveChecks(){

    }

    private void endGame(){
        gameState = GameState.GAME_ENDED;
    }

    private void nextHand(){

    }

    private SplitPlayerHand getSplitHand(){
        if(!isHandSplit()) throw new IllegalStateException("The hand of the player is not split.");
        return (SplitPlayerHand) playerHand;
    }

    private boolean isHandSplit(){
        return playerHand instanceof SplitPlayerHand;
    }

    private void setButtonStates(){
        if(gameState == GameState.DEALER_PLAYING || gameState == GameState.GAME_ENDED) {
            for (ReactionButton button : buttons)
                button.unregister();
        } else {
            hitButton.setEnabled(true);
            standButton.setEnabled(true);

            if(doubleDownButton != null)
                doubleDownButton.setEnabled(doubleDownAllowed);
            if(splitButton != null)
                splitButton.setEnabled(splitAllowed);
        }
    }

}
