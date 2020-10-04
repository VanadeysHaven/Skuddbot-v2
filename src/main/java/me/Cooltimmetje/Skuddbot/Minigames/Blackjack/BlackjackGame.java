package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands.BlackjackHand;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands.DealerHand;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands.PlayerHand;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private static final String HEADER = Emoji.BLACK_JOKER.getUnicode() + " **BLACKJACK** [beta] | *{0}*";
    private static final String DEALER_FORMAT = "**DEALER HAND:** ({1}) *Dealer draws to 16, stands on 17.*\n" +
            "{2}";
    private static final String ONE_HANDED_PLAYER_FORMAT = "**PLAYER HAND:** ({3}) | **BET:** {4}\n" +
            "{5}";
    private static final String TWO_HANDED_PLAYER_FORMAT = "**PLAYER HANDS:** ({3}/{4}) | **BET:** {5}/{6}\n" +
            "{7} {8}\n" +
            "{9} {10}";
    private static final String XP_ICON = "<:xp_icon:458325613015466004>";
    private static final String REWARDS_FORMAT = "{0} | *+{1}* " + XP_ICON + " | *+{2} Skuddbux*";
    private static final String LOST_FORMAT = "{0} | **ADDED TO JACKPOT:** *{1} Skuddbux*";

    //Complete formats
    private static final String NORMAL_FORMAT = HEADER + "\n\n" +
            DEALER_FORMAT + "\n\n" +
            ONE_HANDED_PLAYER_FORMAT + "\n\n" +
            ">>> {6}";
    private static final String SPLIT_FORMAT = HEADER + "\n\n" +
            DEALER_FORMAT + "\n\n" +
            TWO_HANDED_PLAYER_FORMAT + "\n\n" +
            ">>> {11}";

    //Playing instructions
    private enum PlayingInstruction {

        PLAYER_PLAYING("[playing instructions here]"),
        PLAYER_GOT_21("**You got 21! Dealer playing...**"),
        PLAYER_GOT_BJ("**BLACKJACK! You win!**"),
        PLAYER_HIGHER_THAN_DEALER("**You win! Your hand has a higher value than that of the dealer.**"),
        PLAYER_WIN_DEALER_BUSTED("**You win! The dealer busted.**"),
        PLAYER_WIN_21("**You got 21! You win!**"),
        PLAYER_LOWER_THAN_DEALER("**You lose! Your hand has a lower value than that of the dealer.**"),
        PLAYER_TIED_WITH_DEALER_21("**You got 21, but tied with the dealer.**"),
        PLAYER_TIED_WITH_DEALER("**PUSH! You tied with the dealer.**"),
        PLAYER_STANDING("**Standing. Dealer playing...**"),
        PLAYER_DOUBLED_DOWN("**Doubled down... Dealer playing.**"),
        PLAYER_BUSTED("**You busted, better luck next time.**"),
        GAME_ENDED("*Game ended, see outcomes and rewards above.*");

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

        playerHand.addCard(BlackjackHand.ONE, manager.drawCard());
        dealerHand.addCard(BlackjackHand.ONE, manager.drawCard());
        playerHand.addCard(BlackjackHand.ONE, manager.drawCard());
        dealerHand.setHoleCard(manager.drawCard());

//        if (handInstruction == null) {
//        playerHand.addCard(BlackjackHand.ONE, manager.drawCard());
//        dealerHand.addCard(BlackjackHand.ONE, manager.drawCard());
//        playerHand.addCard(BlackjackHand.ONE, manager.drawCard());
//        dealerHand.setHoleCard(manager.drawCard());
//            return;
//        } else if (handInstruction.equalsIgnoreCase("double")) {
//            playerHand.addCard(BlackjackHand.ONE, new Card(Card.Rank.FOUR, Card.Suit.HEARTS));
//            playerHand.addCard(BlackjackHand.ONE, new Card(Card.Rank.SIX, Card.Suit.CLUBS));
//        } else if (handInstruction.equalsIgnoreCase("split")) {
//            playerHand.addCard(BlackjackHand.ONE, new Card(Card.Rank.TWO, Card.Suit.HEARTS));
//            playerHand.addCard(BlackjackHand.ONE, new Card(Card.Rank.TWO, Card.Suit.CLUBS));
//        }
//
//        dealerHand.addCard(BlackjackHand.ONE, manager.drawCard());
//        dealerHand.setHoleCard(manager.drawCard());
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
        if(playerHand.isHandSplitted())
            return formatSplitHand();
        else
            return formatSingleHand();
    }

    private String formatSingleHand(){
        String playerName = player.getDisplayName();
        int dealerHandValue = dealerHand.getHandValue(BlackjackHand.ONE);
        String dealerHandStr = dealerHand.formatHand(BlackjackHand.ONE);
        int playerHandValue = playerHand.getHandValue(BlackjackHand.ONE);
        int playerBet = playerHand.getBet(BlackjackHand.ONE);
        String playerHandStr = playerHand.formatHand(BlackjackHand.ONE);
        String playingInstruction = formatPlayingInstructions();

        return MessageFormat.format(NORMAL_FORMAT, playerName, dealerHandValue, dealerHandStr, playerHandValue, playerBet, playerHandStr, playingInstruction);
    }

    private String formatSplitHand(){
        String playerName = player.getDisplayName();
        int dealerHandValue = dealerHand.getHandValue(BlackjackHand.ONE);
        String dealerHandStr = dealerHand.formatHand(BlackjackHand.ONE);
        int firstPlayerHandValue = playerHand.getHandValue(BlackjackHand.ONE);
        int secondPlayerHandValue = playerHand.getHandValue(BlackjackHand.TWO);
        int firstBet = playerHand.getBet(BlackjackHand.ONE);
        int secondBet = playerHand.getBet(BlackjackHand.TWO);
        String firstPlayerHandStr = playerHand.formatHand(BlackjackHand.ONE);
        String firstPlayerHandState = currentHand() == BlackjackHand.ONE ? Emoji.ARROW_LEFT.getUnicode() : (gameState == GameState.GAME_ENDED ? playerHand.getRewardString(BlackjackHand.ONE) : "");
        String secondPlayerHandStr = playerHand.formatHand(BlackjackHand.TWO);
        String secondPlayerHandState = currentHand() == BlackjackHand.TWO ? Emoji.ARROW_LEFT.getUnicode() : (gameState == GameState.GAME_ENDED ? playerHand.getRewardString(BlackjackHand.TWO) : "");
        String playingInstruction = formatPlayingInstructions();

        return MessageFormat.format(SPLIT_FORMAT, playerName, dealerHandValue, dealerHandStr, firstPlayerHandValue, secondPlayerHandValue, firstBet, secondBet, firstPlayerHandStr, firstPlayerHandState, secondPlayerHandStr, secondPlayerHandState, playingInstruction);
    }

    private String formatPlayingInstructions(){
        if(playingInstruction == PlayingInstruction.PLAYER_PLAYING){
            StringBuilder sb = new StringBuilder(HIT_STAND);
            sb.append("\n");
            if(doubleDownAllowed)
                sb.append(DOUBLE_DOWN).append(" ");
            if(splitAllowed)
                sb.append(SPLIT);

            return sb.toString().trim();
        }

        if(gameState == GameState.GAME_ENDED && !playerHand.isHandSplitted())
            return playerHand.getRewardString(BlackjackHand.ONE);
        else
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
        event.undoReaction();
        hit();
    }

    private void hit(){
        playerHand.addCard(currentHand(), manager.drawCard());

        postMoveChecks();

        int currentHand = currentHand();
        if(currentHand == 1 || currentHand == 2)
            if(playerHand.getHandValue(currentHand) < 20)
                sendMessage();
    }

    private void standButton(ReactionButtonClickedEvent event){
        playingInstruction = PlayingInstruction.PLAYER_STANDING;
        event.undoReaction();

        stand();
    }

    private void stand(){
        if(gameState == GameState.NORMAL_HAND_PLAYING || gameState == GameState.SECOND_SPLIT_HAND_PLAYING) {
            gameState = GameState.DEALER_PLAYING;
            sendMessage();
            setButtonStates();
            playDealer();
            endGame();
        } else if (gameState == GameState.FIRST_SPLIT_HAND_PLAYING) {
            nextHand();
            postMoveChecks();
        } else throw new IllegalStateException("Not allowed to call this function now, player is not playing");
    }


    private void doubleDownButton(ReactionButtonClickedEvent event){
        event.undoReaction();
        SkuddUser su = player.asSkuddUser();

        if(su.getCurrencies().hasEnoughBalance(Currency.SKUDDBUX, playerHand.getBet(currentHand())))
            su.getCurrencies().incrementInt(Currency.SKUDDBUX, playerHand.getBet(currentHand()) * -1);
        else
            return;

        playingInstruction = PlayingInstruction.PLAYER_DOUBLED_DOWN;

        doubleDown();
    }

    private void doubleDown(){
        playerHand.doubleDown(currentHand(), manager.drawCard());
        stand();
    }

    private void splitButton(ReactionButtonClickedEvent event){
        SkuddUser su = player.asSkuddUser();

        if(su.getCurrencies().hasEnoughBalance(Currency.SKUDDBUX, playerHand.getBet(currentHand())))
            su.getCurrencies().incrementInt(Currency.SKUDDBUX, playerHand.getBet(currentHand()) * -1);
        else {
            event.undoReaction();
            return;
        }

        split();
    }

    private void split(){
        playerHand = playerHand.splitHand();
        playerHand.addCard(BlackjackHand.ONE, manager.drawCard());
        playerHand.addCard(BlackjackHand.TWO, manager.drawCard());
        gameState = GameState.FIRST_SPLIT_HAND_PLAYING;
        postMoveChecks();
        sendMessage();
    }

    private void postMoveChecks(){
        doubleDownAllowed = playerHand.isDoubleDownAllowed(currentHand());
        splitAllowed = playerHand.isSplitAllowed();

        setButtonStates();

        if(playerHand.getHandValue(currentHand()) == 21) {
            playingInstruction = PlayingInstruction.PLAYER_GOT_21;
            stand();
        } else if (playerHand.getHandValue(currentHand()) > 21){
            if(!playerHand.isHandSplitted()) {
                playingInstruction = PlayingInstruction.PLAYER_BUSTED;
                endGame();
            } else {
                if(gameState == GameState.SECOND_SPLIT_HAND_PLAYING)
                    playingInstruction = PlayingInstruction.PLAYER_STANDING;
                stand();
            }
        }
    }

    private void playDealer(){
        dealerHand.revealHoleCard();
        while(dealerHand.getHandValue(BlackjackHand.ONE) < 17)
            dealerHand.addCard(BlackjackHand.ONE, manager.drawCard());
    }

    private void endGame() {
        gameState = GameState.GAME_ENDED;
        setButtonStates();
        if(message != null)
            message.removeAllReactions();
        calculateResult(BlackjackHand.ONE);
        if (playerHand.isHandSplitted())
            calculateResult(BlackjackHand.TWO);

        if (!dealerHand.isHoleCardRevealed())
            dealerHand.revealHoleCard();

        int handValue = playerHand.getHandValue(BlackjackHand.ONE);
        boolean delayReveal = playerHand.isHandSplitted() || (handValue <= 21 && !playerHand.isBlackjack(BlackjackHand.ONE));

        if (delayReveal) {
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            message.getChannel().type();
            exec.schedule(this::showAndReward, 5, TimeUnit.SECONDS);
        } else {
            showAndReward();
        }
    }

    private void showAndReward(){
        if(playerHand.isHandSplitted())
            playingInstruction = PlayingInstruction.GAME_ENDED;
        sendMessage();
        SkuddUser su = player.asSkuddUser();

        int sbReward = 0;
        int xpReward = 0;
        int jackpot = 0;
        HashMap<Stat,Integer> statAmounts = new HashMap<>();

        sbReward += playerHand.getSbReward(BlackjackHand.ONE);
        xpReward += playerHand.getXpReward(BlackjackHand.ONE);
        jackpot += playerHand.getJackpot(BlackjackHand.ONE);
        for(Stat stat : playerHand.getIncrementStats(BlackjackHand.ONE)) {
            if(statAmounts.containsKey(stat))
                statAmounts.put(stat, statAmounts.get(stat) + 1);
            else
                statAmounts.put(stat, 1);
        }

        if(playerHand.isHandSplitted()){
            sbReward += playerHand.getSbReward(BlackjackHand.TWO);
            xpReward += playerHand.getXpReward(BlackjackHand.TWO);
            jackpot += playerHand.getJackpot(BlackjackHand.TWO);
            for(Stat stat : playerHand.getIncrementStats(BlackjackHand.TWO)) {
                if(statAmounts.containsKey(stat))
                    statAmounts.put(stat, statAmounts.get(stat) + 1);
                else
                    statAmounts.put(stat, 1);
            }
        }

        if(sbReward > 0)
            su.getCurrencies().incrementInt(Currency.SKUDDBUX, sbReward);
        if(xpReward > 0)
            su.getStats().incrementInt(Stat.EXPERIENCE, xpReward);
        if(jackpot > 0) {
            ServerManager.getInstance().getServer(su.getId().getServerId()).getSettings().incrementInt(ServerSetting.JACKPOT, jackpot);
        }
        for(Stat stat : statAmounts.keySet())
            su.getStats().incrementInt(stat, statAmounts.get(stat));

        manager.wrapUp(player);
    }

    @Getter
    private enum Outcome {

        BLACKJACK       (375, 2.5, PlayingInstruction.PLAYER_GOT_BJ,              Stat.BJ_BLACKJACKS,  Stat.BJ_TWENTY_ONES, Stat.BJ_WINS),
        PLAYER_WINS_21  (225, 2,   PlayingInstruction.PLAYER_WIN_21,              Stat.BJ_TWENTY_ONES, Stat.BJ_WINS                     ),
        PLAYER_WINS     (150, 2,   PlayingInstruction.PLAYER_HIGHER_THAN_DEALER,  Stat.BJ_WINS                                          ),
        DEALER_BUSTS    (150, 2,   PlayingInstruction.PLAYER_WIN_DEALER_BUSTED,   Stat.BJ_WINS                                          ),
        PUSH_21         (100, 1,   PlayingInstruction.PLAYER_TIED_WITH_DEALER_21, Stat.BJ_PUSHES,      Stat.BJ_TWENTY_ONES              ),
        PUSH            (50,  1,   PlayingInstruction.PLAYER_TIED_WITH_DEALER,    Stat.BJ_PUSHES                                        ),
        PLAYER_BUSTS    (0,   0,   PlayingInstruction.PLAYER_BUSTED,              Stat.BJ_LOSSES                                        ),
        PLAYER_LOSES    (0,   0,   PlayingInstruction.PLAYER_LOWER_THAN_DEALER,   Stat.BJ_LOSSES                                        );

        private int xpReward;
        private double payoutModifier;
        private PlayingInstruction playingInstruction;
        private Stat[] incrementStats;

        Outcome(int xpReward, double payoutModifier, PlayingInstruction playingInstruction, Stat... incrementStats) {
            this.xpReward = xpReward;
            this.payoutModifier = payoutModifier;
            this.playingInstruction = playingInstruction;
            this.incrementStats = incrementStats;
        }

        public ArrayList<Stat> getIncrementStatsAsList(){
            return new ArrayList<>(Arrays.asList(incrementStats.clone()));
        }

    }

    private void calculateResult(int hand) {
        Outcome outcome = Outcome.PLAYER_LOSES;
        if(playerHand.getHandValue(hand) == 21){
            if(playerHand.isBlackjack(hand)) {
                outcome = Outcome.BLACKJACK;
            } else {
                if(dealerHand.getHandValue(BlackjackHand.ONE) == playerHand.getHandValue(hand)) {
                    outcome = Outcome.PUSH_21;
                } else {
                    outcome = Outcome.PLAYER_WINS_21;
                }
            }
        } else if (playerHand.getHandValue(hand) > 21){
            outcome = Outcome.PLAYER_BUSTS;
        } else if (playerHand.getHandValue(hand) < 21) {
            if(dealerHand.getHandValue(BlackjackHand.ONE) > playerHand.getHandValue(hand)) {
                if(dealerHand.getHandValue(BlackjackHand.ONE) <= 21) {
                    outcome = Outcome.PLAYER_LOSES;
                } else {
                    outcome = Outcome.DEALER_BUSTS;
                }
            } else if (dealerHand.getHandValue(BlackjackHand.ONE) == playerHand.getHandValue(hand)){
                outcome = Outcome.PUSH;
            } else if (dealerHand.getHandValue(BlackjackHand.ONE) < playerHand.getHandValue(hand)) {
                outcome = Outcome.PLAYER_WINS;
            }
        }

        if(playerHand.isDoubled(hand)){
            ArrayList<Stat> stats = outcome.getIncrementStatsAsList();
            if(outcome == Outcome.BLACKJACK || outcome == Outcome.PLAYER_WINS_21 || outcome == Outcome.DEALER_BUSTS)
                stats.add(Stat.BJ_DD_WINS);
            if(outcome == Outcome.PUSH || outcome == Outcome.PUSH_21)
                stats.add(Stat.BJ_DD_PUSHES);
            if(outcome == Outcome.PLAYER_BUSTS || outcome == Outcome.PLAYER_LOSES)
                stats.add(Stat.BJ_DD_LOSSES);

            Stat[] arr = new Stat[stats.size()];
            arr = stats.toArray(arr);
            playerHand.setIncrementStats(hand, arr);
        } else {
            playerHand.setIncrementStats(hand, outcome.getIncrementStats());
        }

        if(playerHand.isDoubled(hand)) {
            playerHand.setXpReward(hand, outcome.getXpReward() * 2);
        } else {
            playerHand.setXpReward(hand, outcome.getXpReward());
        }
        int betPayout = (int) (outcome.getPayoutModifier() * playerHand.getBet(hand));
        playerHand.setSbReward(hand, betPayout);
        if(outcome != Outcome.PLAYER_BUSTS && outcome != Outcome.PLAYER_LOSES)
            playerHand.setRewardString(hand, MessageFormat.format(REWARDS_FORMAT, outcome.playingInstruction.getInstruction(), playerHand.isDoubled(hand) ? outcome.getXpReward() * 2 : outcome.getXpReward(), betPayout));
        else {
            playerHand.setJackpot(hand, playerHand.getBet(hand));
            playerHand.setRewardString(hand, MessageFormat.format(LOST_FORMAT, outcome.playingInstruction.getInstruction(), playerHand.getBet(hand)));
        }

    }

    private void nextHand(){
        if(gameState != GameState.FIRST_SPLIT_HAND_PLAYING) throw new IllegalStateException("Cannot cycle to next hand now");
        gameState = GameState.SECOND_SPLIT_HAND_PLAYING;
        playingInstruction = PlayingInstruction.PLAYER_PLAYING;
        sendMessage();
        setButtonStates();
    }

    private void setButtonStates() {
        if (gameState == GameState.DEALER_PLAYING || gameState == GameState.GAME_ENDED) {
            for (ReactionButton button : buttons) if (button.isRegistered()) button.unregister();
        } else {
            hitButton.setEnabled(true);
            standButton.setEnabled(true);

            if(doubleDownButton != null)
                doubleDownButton.setEnabled(doubleDownAllowed);
            if(splitButton != null)
                splitButton.setEnabled(splitAllowed);
        }
    }

    private int currentHand() {
        if (gameState == GameState.NORMAL_HAND_PLAYING || gameState == GameState.FIRST_SPLIT_HAND_PLAYING)
            return 1;
        else if (gameState == GameState.SECOND_SPLIT_HAND_PLAYING)
            return 2;
        else
            return -1;
    }

}
