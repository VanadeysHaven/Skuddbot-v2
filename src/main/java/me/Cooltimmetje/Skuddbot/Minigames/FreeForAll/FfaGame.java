package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.PermissionManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Represents a game of Free for All
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.1
 * @since 2.2
 */
public final class FfaGame {

    private static final ServerManager sm = ServerManager.getInstance();
    private static final RNGManager random = new RNGManager();
    private static final Logger logger = LoggerFactory.getLogger(FfaGame.class);

    private static final String HEADER = Emoji.CROSSED_SWORDS.getUnicode() + " **FREE FOR ALL** | *{0}*\n";
    private static final String OUTSTANDING_FORMAT = HEADER + "\n" +
            "{1} is opened for a Free for all fight! " +
            "\n\n**CURRENT ENTRANTS:**\n" +
            "{2}\n\n" +
            ">>> {3}";
    private static final String IN_PROGRESS_FORMAT = HEADER + "\n" +
            "{1}";
    private static final String ENTER_INSTRUCTION_REACTION = "*Press the " + Emoji.CROSSED_SWORDS.getUnicode() + " reaction to enter without a bounty, press the " + Emoji.COIN.getUnicode() + " reaction to enter with your default bounty. To match the current highest bounty, press the " + Emoji.MONEYBAG.getUnicode() + " reaction.*";
    private static final String ENTER_INSTRUCTION_COMMAND = "*Use `{0}freeforall <bounty>` to enter with a bounty, use `{0}freeforall all` to go all-in.*";
    private static final String START_INSTRUCTION = "*{0} can start the fight using the " + Emoji.WHITE_CHECK_MARK.getUnicode() + " reaction.*";
    private static final String FIGHT_STARTED_FORMAT = "{0} step into {1} for a EPIC free for all battle. Who will win? *3*... *2*... *1*... **FIGHT!**";
    private static final String FIGHT_ENDED_FORMAT = "The crowd witnessed a furious battle in {0}, many combatants have fallen and **{1}** the last man standing!";
    private static final String REMINDER_FORMAT = "Hey, you still got a free for all with **{0} entrants** pending in {1} (**{2}**).\n(**PRO-TIP:** You can use search to quickly find it!)";

    private static final int XP_KILL_REWARD = 50;
    private static final int SB_KILL_REWARD = 10;
    private static final int XP_WIN_REWARD = 100;
    private static final int SB_WIN_REWARD = 50;
    private static final int REMINDER_DELAY = 6; //in hours

    private ArrayList<FfaPlayer> entrants;
    private TextChannel channel;
    private SkuddServer server;
    private ServerMember host;
    private FfaGameManager manager;
    private Message message;
    private ArrayList<ReactionButton> buttons;
    private ReactionButton startButton;
    private State state;
    private String log;
    private String killFeed;

    private long lastReminder;
    private long timeStarted;
    private int entrantsAtLastReminder;

    public FfaGame(TextChannel channel, ServerMember host, FfaGameManager manager){
        entrants = new ArrayList<>();
        buttons = new ArrayList<>();
        this.channel = channel;
        this.host = host;
        this.manager = manager;
        server = sm.getServer(host.getServer().getId());
        state = State.OUTSTANDING;
        log = "";
        killFeed = "";
        lastReminder = System.currentTimeMillis();
        timeStarted = System.currentTimeMillis();
        entrantsAtLastReminder = 0;

        sendMessage();
        buttons.add(ReactionUtils.registerButton(message, Emoji.CROSSED_SWORDS, e -> enterGame(e.getUserAsMember()), e -> leaveGame(e.getUserAsMember())));
        buttons.add(ReactionUtils.registerButton(message, Emoji.COIN, this::enterGameWithDefaultBounty, e -> leaveGame(e.getUserAsMember())));
        buttons.add(ReactionUtils.registerButton(message, Emoji.MONEYBAG, this::enterGameWithHighestBounty, e -> leaveGame(e.getUserAsMember())));
        buttons.add(ReactionUtils.registerButton(message, Emoji.EYES, this::startForcefully, true));
        startButton = ReactionUtils.registerButton(message, Emoji.WHITE_CHECK_MARK, e -> startGame(), host.getId().getDiscordId());
        startButton.setEnabled(false);
    }

    private void sendMessage(){
        if(message == null)
            message = MessagesUtils.sendPlain(channel, formatMessage());
        else
            message.edit(formatMessage());
    }

    private String formatMessage(){
        if(state == State.OUTSTANDING) {
            StringBuilder sbInstr = new StringBuilder();
            sbInstr.append(ENTER_INSTRUCTION_REACTION).append("\n");
            sbInstr.append(MessageFormat.format(ENTER_INSTRUCTION_COMMAND, server.getSettings().getString(ServerSetting.COMMAND_PREFIX).replace("_", " ")));
            if (entrants.size() >= 3)
                sbInstr.append("\n").append(MessageFormat.format(START_INSTRUCTION, host.getDisplayName()));

            return MessageFormat.format(OUTSTANDING_FORMAT, server.getName(), server.getSettings().getString(ServerSetting.ARENA_NAME), formatEntrants(true), sbInstr.toString());
        } else if (state == State.IN_PROGRESS) {
            return MessageFormat.format(IN_PROGRESS_FORMAT, server.getName(), log);
        }

        return "something went wrong, i'm sorry";
    }

    private void enterGameWithDefaultBounty(ReactionButtonClickedEvent event){
        ServerMember member = event.getUserAsMember();
        SkuddUser su = member.asSkuddUser();
        int bounty = su.getSettings().getInt(UserSetting.DEFAULT_BET);
        if(!su.getCurrencies().hasEnoughBalance(Currency.SKUDDBUX, bounty)) {
            return;
        }

        su.getCurrencies().incrementInt(Currency.SKUDDBUX, -bounty);
        enterGame(member, bounty);
    }

    private void enterGameWithHighestBounty(ReactionButtonClickedEvent event) {
        ServerMember member = event.getUserAsMember();
        SkuddUser su = member.asSkuddUser();
        int bounty = manager.getCurrentHighestBounty();
        if(!su.getCurrencies().hasEnoughBalance(Currency.SKUDDBUX, bounty)) {
            return;
        }

        su.getCurrencies().incrementInt(Currency.SKUDDBUX, -bounty);
        enterGame(member, bounty);
    }

    public void enterGame(ServerMember member){
        enterGame(member, 0);
    }

    public void enterGame(ServerMember member, int bounty){
        if(isInGame(member))
            return;

        entrants.add(new FfaPlayer(member, bounty));
        sendMessage();

        if(entrants.size() >= 3)
            startButton.setEnabled(true);
    }

    public void leaveGame(ServerMember member){
        FfaPlayer player = getPlayer(member);
        if(player == null)
            return;

        SkuddUser su = member.asSkuddUser();
        su.getCurrencies().incrementInt(Currency.SKUDDBUX, player.getBounty());
        entrants.remove(player);
        sendMessage();
    }

    private void startForcefully(ReactionButtonClickedEvent e) {
        PermissionManager perms = e.getUserAsMember().asSkuddUser().getPermissions();
        if(perms.hasPermission(PermissionLevel.SERVER_ADMIN) && entrants.size() >= 2) {
            appendToLog("*(Game was force-started by server admin)*");
            startGame();
        } else
            e.undoReaction();
    }

    private void startGame(){
        unregisterButtons();
        message.delete();
        message = null;
        state = State.IN_PROGRESS;
        appendToLog(MessageFormat.format(FIGHT_STARTED_FORMAT, formatEntrants(false), server.getSettings().getString(ServerSetting.ARENA_NAME)));
        sendMessage();
        channel.type();
        FfaPlayer winner = simulateFight();
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(() -> {
            revealResults(winner);
        }, 5, TimeUnit.SECONDS);
    }

    private void revealResults(FfaPlayer winner){
        String rewards = award(winner);
        appendToLog(MessageFormat.format(FIGHT_ENDED_FORMAT, server.getSettings().getString(ServerSetting.ARENA_NAME), winner.getName()));
        appendToLog("");
        appendToLog("> Click the " + Emoji.NOTEPAD_SPIRAL.getUnicode() + " reaction to view the kill feed.");
        appendToLog("> Click the " + Emoji.GIFT.getUnicode() + " reaction to view the rewards.");
        sendMessage();
        MessagesUtils.addReaction(message, Emoji.NOTEPAD_SPIRAL, "**Free for all kill feed:** \n" + killFeed, 6*60*60*1000, true);
        MessagesUtils.addReaction(message, Emoji.GIFT, "**Free for all rewards:** \n" + rewards, 6*60*60*1000, true);
        manager.finishGame();
    }

    private void appendToLog(String append){
        log += append + "\n";
    }

    private void appendToKillFeed(String append){
        killFeed += append + "\n";
    }

    public String award(FfaPlayer winner){ //TODO
        return null;
    }

    public FfaPlayer simulateFight(){ //TODO
        return null;
    }

    public FfaPlayer getRandomAlivePlayer(){
        FfaPlayer randomPlayer;
        do {
            randomPlayer = entrants.get(random.integer(0, entrants.size() - 1));
        } while (!randomPlayer.isAlive());

        return randomPlayer;
    }

    public int getPlayerAliveCount(){
        int count = 0;
        for(FfaPlayer player : entrants)
            if(player.isAlive())
                count++;

        return count;
    }

    public boolean isInGame(ServerMember member){
        for(FfaPlayer player : entrants)
            if(player.getMember().equals(member))
                return true;

        return false;
    }

    public FfaPlayer getPlayer(ServerMember member){
        Iterator<FfaPlayer> it = getPlayers();
        while(it.hasNext()) {
            FfaPlayer player = it.next();
            if (player.getMember().equals(member))
                return player;
        }

        return null;
    }

    private String formatEntrants(boolean withBounty){
        if(entrants.isEmpty())
            return "No one yet \\:(";

        String[] entrantNames = new String[entrants.size()];
        for(int i=0; i < entrants.size(); i++){
            if(withBounty)
                entrantNames[i] = entrants.get(i).getNameAndBounty();
            else
                entrantNames[i] = entrants.get(i).getName();
        }

        return MiscUtils.glueStrings("**", "**, **", "** and **", "**", 5, "other combatants", entrantNames);
    }

    private void unregisterButtons(){
        for(ReactionButton button : buttons)
            ReactionUtils.unregisterButton(button);

        ReactionUtils.unregisterButton(startButton);
    }

    public Iterator<FfaPlayer> getPlayers() {
        return entrants.iterator();
    }

    private enum State {
        OUTSTANDING, IN_PROGRESS
    }

    public void runReminder(){
        long curTime = System.currentTimeMillis();
        if(entrants.size() < 3) return;
        if((curTime - lastReminder) < (REMINDER_DELAY * 60 * 60 * 1000)) return;

        if(entrantsAtLastReminder != entrants.size()){
            if(host.asSkuddUser().getSettings().getBoolean(UserSetting.MINIGAME_REMINDERS))
                host.getUser().sendMessage(MessageFormat.format(REMINDER_FORMAT, entrants.size(), "<#" + channel.getId() + ">", server.getName()));
        } else {
            if((curTime - timeStarted) > (12 * 60 * 60 * 1000)) {
                appendToLog("*(Game was auto-started by the bot)*");
                startGame();
                return;
            }
        }

        lastReminder = System.currentTimeMillis();
        entrantsAtLastReminder = entrants.size();
    }

}
