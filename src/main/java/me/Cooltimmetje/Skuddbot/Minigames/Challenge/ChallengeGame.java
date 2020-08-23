package me.Cooltimmetje.Skuddbot.Minigames.Challenge;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.CurrenciesContainer;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.StatsContainer;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Represents a game of challenge between 2 users.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.1
 */
public class ChallengeGame {

    private static final ProfileManager pm = ProfileManager.getInstance();
    private static final ServerManager sm = ServerManager.getInstance();
    private static final RNGManager random = new RNGManager();

    private static final String HEADER = Emoji.DAGGER.getUnicode() + " **CHALLENGE** | ";
    private static final String OPEN_HEADER = HEADER + "*{0}*";
    private static final String NORMAL_HEADER = HEADER + "*{0} vs {1}*";

    private static final String NORMAL_FORMAT_BET = NORMAL_HEADER + "\n\n" +
            "**{0}** has challenged **{1}** to a fight.\n" +
            "**{0}** has placed a bet of **{2}** Skuddbux!\n\n" +
            ">>> *Cost to play: {2} Skuddbux*{3}\n{4}";
    private static final String NORMAL_FORMAT_NO_BET = NORMAL_HEADER + "\n\n" +
            "**{0}** has challenged **{1}** to a fight.\n\n" +
            ">>> {2}";

    private static final String OPEN_FORMAT_BET = OPEN_HEADER + "\n\n" +
            "**{0}** has put down an open fight! Anyone can accept it!\n" +
            "**{0}** has placed a bet of **{1}** Skuddbux.\n\n" +
            ">>> *Cost to play: {1} Skuddbux*\n{2}";
    private static final String OPEN_FORMAT_NO_BET = OPEN_HEADER + "\n\n" +
            "**{0}** has put down an open fight! Anyone can accept it!\n\n" +
            ">>> {1}";

    private static final String IN_PROGRESS_FORMAT = NORMAL_HEADER + "\n\n" +
            "{2}";
    private static final String PLAYING_INSTRUCTION = "Click the " + Emoji.DAGGER.getUnicode() + " reaction to accept, ";
    private static final String ADDITIONAL_CLOSED_PLAYING_INSTRUCTION = "click the " + Emoji.X.getUnicode() + " reaction to decline/cancel.";
    private static final String ADDITIONAL_OPEN_PLAYING_INSTRUCTION = "click the " + Emoji.X.getUnicode() + " reaction to cancel.";
    private static final int XP_WIN_REWARD = 100;
    private static final int SB_WIN_REWARD = 50;
    private static final int XP_STREAK_BONUS = 50;
    private static final int SB_STREAK_BONUS = 25;

    private Server server;
    private ChallengeGameManager manager;
    @Getter private ChallengePlayer challengerOne;
    @Getter private ChallengePlayer challengerTwo;
    @Getter private Message initialMessage;
    private Message gameMessage;
    private TextChannel channel;
    private ArrayList<Message> messages;
    private String log;
    private ReactionButton acceptButton;
    private ReactionButton delcineButton;

    public ChallengeGame(ChallengePlayer challengerOne, ChallengePlayer challengerTwo, Message message, Server server, ChallengeGameManager manager){
        this.server = server;
        this.manager = manager;
        this.challengerOne = challengerOne;
        this.challengerTwo = challengerTwo;

        if(isOpen()) {
            if (challengerOne.hasBetted())
                initialMessage = MessagesUtils.sendPlain(message.getChannel(), MessageFormat.format(OPEN_FORMAT_BET, challengerOne.getName(), challengerOne.getBet(), PLAYING_INSTRUCTION + ADDITIONAL_OPEN_PLAYING_INSTRUCTION));
            else
                initialMessage = MessagesUtils.sendPlain(message.getChannel(), MessageFormat.format(OPEN_FORMAT_NO_BET, challengerOne.getName(), PLAYING_INSTRUCTION + ADDITIONAL_OPEN_PLAYING_INSTRUCTION));
        } else{
            if (challengerOne.hasBetted()) {
                String allInText = (challengerOne.getBet() == challengerTwo.getMember().asSkuddUser().getCurrencies().getInt(Currency.SKUDDBUX)) ? (" | " + Emoji.WARNING.getUnicode() + " " + challengerTwo.getMember().getDisplayName() + ": this bet is **all-in**!") : ("");
                initialMessage = MessagesUtils.sendPlain(message.getChannel(), MessageFormat.format(NORMAL_FORMAT_BET, challengerOne.getMember().getDisplayName(), challengerTwo.getMember().getDisplayName(), challengerOne.getBet(), allInText, PLAYING_INSTRUCTION + ADDITIONAL_CLOSED_PLAYING_INSTRUCTION));
            } else
                initialMessage = MessagesUtils.sendPlain(message.getChannel(), MessageFormat.format(NORMAL_FORMAT_NO_BET, challengerOne.getName(), challengerTwo.getName(), PLAYING_INSTRUCTION + ADDITIONAL_CLOSED_PLAYING_INSTRUCTION));
        }



        if(isOpen()) {
            acceptButton = ReactionUtils.registerButton(initialMessage, Emoji.DAGGER, this::acceptReactionClicked);
            delcineButton = ReactionUtils.registerButton(initialMessage, Emoji.X, this::cancelReactionClicked, challengerOne.getMember().getId().getDiscordId());
        } else {
            assert challengerTwo != null;
            acceptButton = ReactionUtils.registerButton(initialMessage, Emoji.DAGGER, this::acceptReactionClicked, challengerTwo.getMember().getId().getDiscordId());
            delcineButton = ReactionUtils.registerButton(initialMessage, Emoji.X, this::cancelReactionClicked, challengerOne.getMember().getId().getDiscordId(), challengerTwo.getMember().getId().getDiscordId());
        }
        messages = new ArrayList<>();
        addMessage(message);
        channel = message.getChannel();
        messages.add(initialMessage);
        log = "";
    }

    public ChallengeGame(ChallengePlayer challengerOne, Message message, Server server, ChallengeGameManager manager){
        this(challengerOne, null, message, server, manager);
    }

    public void acceptReactionClicked(ReactionButtonClickedEvent event){
        if(event.getUser().getId() == challengerOne.getMember().getId().getDiscordId()) {
            event.undoReaction();
            return;
        }

        SkuddUser su = pm.getUser(server.getId(), event.getUser().getId());
        if(!su.getCurrencies().hasEnoughBalance(Currency.SKUDDBUX, getChallengerOne().getBet())){
            event.undoReaction();
            return;
        }

        if(isOpen()) setChallengerTwo(new ChallengePlayer(server.getId(), event.getUserId().getDiscordId()));
        fight();
    }

    public void cancelReactionClicked(ReactionButtonClickedEvent event) {
        if(event.getUser().getId() == challengerOne.getMember().getId().getDiscordId()){
            cancel();
        } else {
            decline();
        }
    }

    public void fight(){
        if(isOpen()) throw new IllegalStateException("Challenge is still open, must add user before fight can be started.");
        SkuddUser su = pm.getUser(server.getId(), challengerTwo.getMember().getId().getDiscordId());
        su.getCurrencies().incrementInt(Currency.SKUDDBUX, challengerOne.getBet() * -1);
        challengerTwo.setBet(challengerOne.getBet());

        unregisterButtons();
        manager.startCooldown(this);
        TextChannel channel = initialMessage.getChannel();
        deleteMessages();

        log += "**" + challengerOne.getName() + "** and **" + challengerTwo.getName() + "** go head to head in " + sm.getServer(server.getId()).getSettings().getString(ServerSetting.ARENA_NAME)
                + "! Who will win? *3*... *2*... *1*... **FIGHT!**";

        sendMessage(channel);
        channel.type();

        int winnerInt = random.integer(1,2);
        ChallengePlayer winner = winnerInt == 1 ? challengerOne : challengerTwo;
        ChallengePlayer loser = winnerInt == 1 ? challengerTwo : challengerOne;
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(() -> {
            log += "\n" + "A cloud of dust appears in " + sm.getServer(server.getId()).getSettings().getString(ServerSetting.ARENA_NAME) + ", when the dust settles it becomes clear that **" + winner.getName() + "** has won the fight!";
            log += "\n" + award(winner, loser);

            sendMessage(channel);
            manager.removeGame(this);
        }, 5, TimeUnit.SECONDS);
    }

    public void decline(){
        if(isOpen()) throw new IllegalStateException("Open challenges cannot be declined.");
        deleteMessages(false);
        unregisterButtons();
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(challengerTwo.getName()).append(" has declined the fight.* ");
        if(challengerOne.hasBetted())
            sb.append(challengerOne.getMember().getDisplayName()).append(", your bet has been refunded.");

        initialMessage.edit(MessageFormat.format(IN_PROGRESS_FORMAT, challengerOne.getName(), challengerTwo.getName(), sb.toString().trim()));
        initialMessage.removeAllReactions();

        refund(challengerOne);
        manager.removeGame(this);
    }

    public void cancel(){
        deleteMessages();
        unregisterButtons();

        refund(challengerOne);
        manager.removeGame(this);
    }

    public void refund(ChallengePlayer player){
        SkuddUser su = player.getMember().asSkuddUser();
        su.getCurrencies().incrementInt(Currency.SKUDDBUX, player.getBet());
    }

    public void deleteMessages(){
        deleteMessages(true);
    }

    public void deleteMessages(boolean deleteInitial) {
        if(!deleteInitial)
            messages.remove(initialMessage);

        channel.deleteMessages(messages);
        messages.clear();
    }

    private String award(ChallengePlayer winner, ChallengePlayer loser){
        StringBuilder sb = new StringBuilder();
        StatsContainer winnerStats = pm.getUser(server.getId(), winner.getMember().getId().getDiscordId()).getStats();
        CurrenciesContainer winnerCurrencies = pm.getUser(server.getId(), winner.getMember().getId().getDiscordId()).getCurrencies();
        StatsContainer loserStats = pm.getUser(server.getId(), loser.getMember().getId().getDiscordId()).getStats();

        int sbWinnings = SB_WIN_REWARD;

        loserStats.incrementInt(Stat.CHALLENGE_LOSSES);
        loserStats.setInt(Stat.CHALLENGE_WINSTREAK, 0);
        if(loser.hasBetted())
            loserStats.incrementInt(Stat.CHALLENGE_BETS_LOST);

        sb.append("**").append(winner.getName()).append(":** ").append("*+").append(XP_WIN_REWARD).append("* <:xp_icon:458325613015466004>, *+").append(SB_WIN_REWARD).append(" Skuddbux*");

        winnerStats.incrementInt(Stat.CHALLENGE_WINS);
        winnerStats.incrementInt(Stat.CHALLENGE_WINSTREAK);

        int winStreak = winnerStats.getInt(Stat.CHALLENGE_WINSTREAK);
        int bonusXp = (winStreak - 1) * XP_STREAK_BONUS;
        int bonusSb = (winStreak - 1) * SB_STREAK_BONUS;
        sbWinnings += bonusSb;
        winnerStats.incrementInt(Stat.EXPERIENCE, XP_WIN_REWARD + bonusXp);
        if(winStreak > 1) {
            sb.append(" | **Winstreak ");
            if (winStreak == 2) {
                sb.append("started");
            } else {
                sb.append("continued");
            }
            sb.append(":** *").append(winnerStats.getInt(Stat.CHALLENGE_WINSTREAK)).append(" wins* (+").append(bonusXp).append("<:xp_icon:458325613015466004>, +").append(bonusSb).append(" Skuddbux)");
        }


        if(winStreak > winnerStats.getInt(Stat.CHALLENGE_LONGEST_WINSTREAK) && winStreak > 1){
            winnerStats.setInt(Stat.CHALLENGE_LONGEST_WINSTREAK, winStreak);
            sb.append(" | **New longest winstreak!**");
        }

        if(winner.hasBetted()){
            winnerStats.incrementInt(Stat.CHALLENGE_BETS_WON);
            sb.append(" | **Bet won:** *+").append(winner.getBet() * 2).append(" Skuddbux*");
            sbWinnings += (winner.getBet() * 2);
        }
        winnerCurrencies.incrementInt(Currency.SKUDDBUX, sbWinnings);

        return sb.toString().trim();
    }

    private void sendMessage(TextChannel channel){
        if(gameMessage == null)
            gameMessage = MessagesUtils.sendPlain(channel, getMessage());
        else
            gameMessage.edit(getMessage());
    }

    private String getMessage(){
        return MessageFormat.format(IN_PROGRESS_FORMAT, challengerOne.getName(), challengerTwo.getName(), log);
    }

    public void setChallengerTwo(ChallengePlayer challengerTwo){
        if(!isOpen()) throw new IllegalStateException("Challenge is not open.");
        this.challengerTwo = challengerTwo;
    }

    public boolean isMatch(User user1, User user2, boolean ignoreOpen){
        if(ignoreOpen && isOpen()) return false;
        if(user1.getId() == user2.getId()) return false;
        if(isOpen() && user2.getId() == challengerOne.getMember().getId().getDiscordId()) return true;
        return (user2.getId() == challengerOne.getMember().getId().getDiscordId()) && (user1.getId() == challengerTwo.getMember().getId().getDiscordId());
    }

    public boolean isMatch(User user1, User user2){
        return isMatch(user1, user2, false);
    }

    public boolean isOpen(){
        return challengerTwo == null;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public long getServerId(){
        return server.getId();
    }

    private void unregisterButtons(){
        acceptButton.unregister();
        delcineButton.unregister();
    }
}
