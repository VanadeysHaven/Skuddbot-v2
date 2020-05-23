package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButtonClickedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Represents a game of Free for All
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2
 * @since ALPHA-2.2
 */
public class FfaGame {

    private static final ServerManager sm = ServerManager.getInstance();

    private static final String HEADER = Emoji.CROSSED_SWORDS.getUnicode() + " **FREE FOR ALL** | *{0}*\n";
    private static final String OUTSTANDING_FORMAT = HEADER + "\n" +
            "{1} is opened for a Free for all fight! " +
            "\n\n**CURRENT ENTRANTS:**\n" +
            "{2}\n\n" +
            ">>> {3}";
    private static final String IN_PROGRESS_FORMAT = HEADER + "\n" +
            "{1}";
    private static final String ENTER_INSTRUCTION_REACTION = "*Press the " + Emoji.CROSSED_SWORDS.getUnicode() + " reaction to enter without a bet, press the " + Emoji.MONEYBAG.getUnicode() + " reaction to enter with your default bet.*";
    private static final String ENTER_INSTRUCTION_COMMAND = "*Use `!ffa <bet>` to enter with a bet, use `!ffa all` to go all-in.*";
    private static final String START_INSTRUCTION = "*{0} can start the fight using the " + Emoji.WHITE_CHECK_MARK.getUnicode() + " reaction.*";
    private static final String FIGHT_STARTED_FORMAT = "{0} step into {1} for a EPIC free for all battle. Who will win? *3*... *2*... *1*... **FIGHT!**";
    private static final String FIGHT_ENDED_FORMAT = "A furious battle is happening in {0}, many combatants have fallen and {1} the last man standing!";

    private ArrayList<FfaPlayer> entrants;
    private TextChannel channel;
    private SkuddServer server;
    private ServerMember host;
    private Message message;
    private ArrayList<ReactionButton> buttons;
    private ReactionButton startButton;
    private State state;
    private String log;

    public FfaGame(TextChannel channel, ServerMember host){
        entrants = new ArrayList<>();
        buttons = new ArrayList<>();
        this.channel = channel;
        this.host = host;
        server = sm.getServer(host.getServer().getId());
        state = State.OUTSTANDING;

        sendMessage();
        buttons.add(ReactionUtils.registerButton(message, Emoji.CROSSED_SWORDS, e -> enterGame(e.getUserAsMember())));
        buttons.add(ReactionUtils.registerButton(message, Emoji.MONEYBAG, this::enterGameWithDefaultBet));
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
            sbInstr.append(ENTER_INSTRUCTION_COMMAND);
            if (entrants.size() >= 3)
                sbInstr.append("\n").append(MessageFormat.format(START_INSTRUCTION, host.getDisplayName()));

            return MessageFormat.format(OUTSTANDING_FORMAT, server.getName(), server.getSettings().getString(ServerSetting.ARENA_NAME), formatEntrants(true), sbInstr.toString());
        } else if (state == State.IN_PROGRESS) {
            return MessageFormat.format(IN_PROGRESS_FORMAT, server.getName(), log);
        }

        return "something went wrong, i'm sorry";
    }

    private void enterGameWithDefaultBet(ReactionButtonClickedEvent event){
        ServerMember member = event.getUserAsMember();
        SkuddUser su = member.asSkuddUser();
        int bet = su.getSettings().getInt(UserSetting.DEFAULT_BET);
        if(!su.getCurrencies().hasEnoughBalance(Currency.SKUDDBUX, bet)) {
            event.undoReaction();
            return;
        }

        enterGame(member, bet);
    }

    public void enterGame(ServerMember member){
        enterGame(member, 0);
    }

    public void enterGame(ServerMember member, int bet){
        if(isInGame(member))
            return;

        member.asSkuddUser().getCurrencies().incrementInt(Currency.SKUDDBUX, -bet);
        entrants.add(new FfaPlayer(member, bet));
        sendMessage();

        if(entrants.size() >= 3)
            startButton.setEnabled(true);
    }

    private void startGame(){
        unregisterButtons();
        message.delete();
        message = null;
        state = State.IN_PROGRESS;
        log += MessageFormat.format(FIGHT_STARTED_FORMAT, formatEntrants(false), server.getSettings().getString(ServerSetting.ARENA_NAME));
        sendMessage();
        FfaPlayer winner = simulateFight();

    }

    public FfaPlayer simulateFight(){

    }

    public boolean isInGame(ServerMember member){
        for(FfaPlayer player : entrants)
            if(player.getPlayer().equals(member))
                return true;

        return false;
    }

    private String formatEntrants(boolean withBet){
        if(entrants.isEmpty())
            return "No one yet \\:(";

        String[] entrantNames = new String[entrants.size()];
        for(int i=0; i < entrants.size(); i++){
            if(withBet)
                entrantNames[i] = entrants.get(i).getNameAndBet();
            else
                entrantNames[i] = entrants.get(i).getName();
        }

        return MiscUtils.glueStrings("**", "**, **", "** and **", "**", entrantNames);
    }

    private void unregisterButtons(){
        for(ReactionButton button : buttons)
            ReactionUtils.unregisterButton(button);

        ReactionUtils.unregisterButton(startButton);
    }

    private enum State {
        OUTSTANDING, IN_PROGRESS
    }

}
