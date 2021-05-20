package me.VanadeysHaven.Skuddbot.Minigames.DoubleOrNothing;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionButton;
import me.VanadeysHaven.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.VanadeysHaven.Skuddbot.Profiles.ProfileManager;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSetting;
import me.VanadeysHaven.Skuddbot.Profiles.ServerManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Currency;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Stats.Stat;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.RNGManager;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Represents a game of double or nothing
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3
 * @since 2.1.1
 */
public class DonGame {

    private static final ProfileManager pm = ProfileManager.getInstance();
    private static final ServerManager sm = ServerManager.getInstance();
    private static final RNGManager random = new RNGManager();

    private static final String HEADER = Emoji.GAME_DIE.getUnicode() + " **DOUBLE OR NOTHING** | *{0}*\n";
    private static final String IN_PROGRESS_FORMAT = HEADER + "\n" +
            "{1}\n" +
            "Current amount: {2} Skuddbux\n\n" +
            "> {3}";
    private static final String ENDED_FORMAT = HEADER + "{1}";
    private static final String PLAYING_INSTRUCTION = "Press the " + Emoji.D.getUnicode() + " reaction to double up, press the " + Emoji.MONEYBAG.getUnicode() + " reaction to take the money.";
    private static final int XP_REWARD = 50;

    @Getter private User user;
    private int bet;
    private int initialBet;
    private int moves;
    private TextChannel channel;
    private Server server;
    private Message message;
    private ArrayList<ReactionButton> buttons;
    private DonGameManager manager;


    public DonGame(User user, int bet, TextChannel channel, Server server, DonGameManager manager){
        this.user = user;
        this.bet = bet;
        initialBet = bet;
        moves = 0;
        this.channel = channel;
        this.server = server;
        this.manager = manager;

        sendInProgressFormat("Welcome to Double or Nothing!", PLAYING_INSTRUCTION);

        buttons = new ArrayList<>();
        buttons.add(ReactionUtils.registerButton(message, Emoji.D, this::play, user.getId()));
        ReactionUtils.registerButton(message, Emoji.MONEYBAG, this::takeMoney, user.getId());
    }

    private void sendInProgressFormat(String topLine, String playingInstruction){
        sendMessage(MessageFormat.format(IN_PROGRESS_FORMAT, user.getDisplayName(server), topLine, bet, playingInstruction));
    }

    private void sendEndedFormat(String text){
        sendMessage(MessageFormat.format(ENDED_FORMAT, user.getDisplayName(server), text));
    }

    private void sendMessage(String msg){
        if(message == null)
            message = MessagesUtils.sendPlain(channel, msg);
        else
            message.edit(msg);
    }

    private void play(ReactionButtonClickedEvent event){
        setButtonsEnabled(false);
        event.undoReaction();
        sendInProgressFormat("*Rolling...*", "*Please wait...*");

        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(() -> {
            if(random.integer(1, 2) == 1)
                double_();
            else
                nothing();
        }, 3, TimeUnit.SECONDS);
    }

    private void double_() {
        bet *= 2;
        moves++;

        sendInProgressFormat("**DOUBLE!**", PLAYING_INSTRUCTION);
        setButtonsEnabled(true);
    }

    private void nothing(){
        sendEndedFormat("**NOTHING!** You lost! | **ADDED TO JACKPOT:** *" + initialBet + " Skuddbux*");

        getProfile().getStats().incrementInt(Stat.DON_LOSSES);
        sm.getServer(server.getId()).getSettings().incrementInt(ServerSetting.JACKPOT, initialBet);

        endGame();
    }

    private void takeMoney(ReactionButtonClickedEvent event){
        setButtonsEnabled(false);

        if(moves == 0){
            sendEndedFormat("*Game cancelled, amount is refunded.*");
            pm.getUser(server.getId(), user.getId()).getCurrencies().incrementInt(Currency.SKUDDBUX, bet);
            endGame(false);
        } else {
            sendEndedFormat(award());
            endGame();
        }
    }

    private String award(){
        String retString = "";
        SkuddUser su = getProfile();
        su.getStats().incrementInt(Stat.DON_WINS);

        retString += "+" + bet + " Skuddbux";
        su.getCurrencies().incrementInt(Currency.SKUDDBUX, bet);
        int xpReward = XP_REWARD * moves;
        retString += " | +" + xpReward + " <:xp_icon:458325613015466004>";
        su.getStats().incrementInt(Stat.EXPERIENCE, xpReward);

        if(su.getStats().getInt(Stat.DON_LONGEST_STREAK) < moves){
            su.getStats().setInt(Stat.DON_LONGEST_STREAK, moves);
            retString += " | **New longest double up streak: **" + moves + " times";
        }

        return retString;
    }

    private void endGame(){
        endGame(true);
    }

    private void endGame(boolean startCooldown){
        unregisterButtons();
        message.removeAllReactions();
        manager.endGame(this, startCooldown);
    }

    private void setButtonsEnabled(boolean enabled) {
        for(ReactionButton button : buttons)
            button.setEnabled(enabled);
    }

    private void unregisterButtons(){
        for (ReactionButton button : buttons)
            ReactionUtils.unregisterButton(button);
    }

    private SkuddUser getProfile(){
        return pm.getUser(server.getId(), user.getId());
    }


}
