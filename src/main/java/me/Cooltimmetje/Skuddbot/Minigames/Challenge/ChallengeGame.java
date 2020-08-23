package me.Cooltimmetje.Skuddbot.Minigames.Challenge;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.StatsContainer;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.auth.Challenge;

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
    private static final String NORMAL_FORMAT = NORMAL_HEADER + "\n\n" +
            "**{0}** has challenged **{1}** to a fight.\n\n" +
            ">>> {2}";
    private static final String OPEN_FORMAT = OPEN_HEADER + "\n\n" +
            "**{0}** has put down an open fight! Anyone can accept it!\n\n" +
            ">>> {1}";
    private static final String IN_PROGRESS_FORMAT = NORMAL_HEADER + "\n\n" +
            "{2}";
    private static final String PLAYING_INSTRUCTION = "Click the " + Emoji.DAGGER.getUnicode() + " reaction to accept, ";
    private static final String ADDITIONAL_CLOSED_PLAYING_INSTRUCTION = "click the " + Emoji.X.getUnicode() + " reaction to decline/cancel.";
    private static final String ADDITIONAL_OPEN_PLAYING_INSTRUCTION = "click the " + Emoji.X.getUnicode() + " reaction to cancel.";
    private static final int WIN_REWARD = 100;
    private static final int STREAK_BONUS = 50;

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

        if(isOpen()){
            initialMessage = MessagesUtils.sendPlain(message.getChannel(), MessageFormat.format(OPEN_FORMAT, challengerOne.getName(), PLAYING_INSTRUCTION + ADDITIONAL_OPEN_PLAYING_INSTRUCTION));
        } else {
            initialMessage = MessagesUtils.sendPlain(message.getChannel(), MessageFormat.format(NORMAL_FORMAT, challengerOne.getName(), challengerTwo.getName(), PLAYING_INSTRUCTION + ADDITIONAL_CLOSED_PLAYING_INSTRUCTION));
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
        initialMessage.edit(MessageFormat.format(OPEN_HEADER, challengerOne.getName() + " vs " + challengerTwo.getName()) + "\n*" +
                challengerTwo.getName() + " has declined the fight.*");
        initialMessage.removeAllReactions();

        manager.removeGame(this);
    }

    public void cancel(){
        deleteMessages();
        unregisterButtons();

        manager.removeGame(this);
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
        StatsContainer loserStats = pm.getUser(server.getId(), loser.getMember().getId().getDiscordId()).getStats();

        loserStats.incrementInt(Stat.CHALLENGE_LOSSES);
        loserStats.setInt(Stat.CHALLENGE_WINSTREAK, 0);

        sb.append("**").append(winner.getName()).append(":** ").append("*+").append(WIN_REWARD).append("* <:xp_icon:458325613015466004>");

        winnerStats.incrementInt(Stat.CHALLENGE_WINS);
        winnerStats.incrementInt(Stat.CHALLENGE_WINSTREAK);

        int winStreak = winnerStats.getInt(Stat.CHALLENGE_WINSTREAK);
        int bonusXp = winStreak * STREAK_BONUS;
        winnerStats.incrementInt(Stat.EXPERIENCE, WIN_REWARD + bonusXp);
        if(winStreak == 2){
            sb.append(" | **Winstreak started:** *").append(winnerStats.getInt(Stat.CHALLENGE_WINSTREAK)).append( " wins* (+").append(bonusXp).append(" <:xp_icon:458325613015466004>)");
        } else if(winStreak > 2) {
            sb.append(" | **Winstreak continued:** *").append(winnerStats.getInt(Stat.CHALLENGE_WINSTREAK)).append( " wins* (+").append(bonusXp).append(" <:xp_icon:458325613015466004>)");
        }

        if(winStreak > winnerStats.getInt(Stat.CHALLENGE_LONGEST_WINSTREAK) && winStreak > 1){
            winnerStats.setInt(Stat.CHALLENGE_LONGEST_WINSTREAK, winStreak);
            sb.append(" | **New longest winstreak!**");
        }

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
