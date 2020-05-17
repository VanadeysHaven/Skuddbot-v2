package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
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

    private static final String HEADER = Emoji.CROSSED_SWORDS.getUnicode() + " **FREE FOR ALL** | {0}\n";
    private static final String OUTSTANDING_FORMAT = HEADER + "\n" +
            "{0} is opened for a Free for all fight! " +
            "\n\n**CURRENT ENTRANTS:**\n" +
            "{1}\n\n" +
            ">>> {2}";
    private static final String ENTER_INSTRUCTION_REACTION = "*Press the " + Emoji.CROSSED_SWORDS.getUnicode() + " reaction to enter without a bet, press the " + Emoji.MONEYBAG.getUnicode() + " reaction to enter with your default bet.*";
    private static final String ENTER_INSTRUCTION_COMMAND = "*Use `!ffa <bet>` to enter with a bet, use `!ffa all` to go all-in.*";
    private static final String START_INSTRUCTION = "*{0} can start the fight using the " + Emoji.WHITE_CHECK_MARK.getUnicode() + " reaction.*";

    private ArrayList<FfaPlayer> entrants;
    private TextChannel channel;
    private SkuddServer server;
    private ServerMember host;
    private Message message;


    public FfaGame(TextChannel channel, ServerMember host){
        entrants = new ArrayList<>();
        this.channel = channel;
        this.host = host;
        server = sm.getServer(host.getServer().getId());
    }

    private void sendMessage(){

    }

    private String formatMessage(){
        StringBuilder sbInstr = new StringBuilder();
        sbInstr.append(ENTER_INSTRUCTION_REACTION).append("\n");
        sbInstr.append(ENTER_INSTRUCTION_COMMAND);
        if(entrants.size() >= 3)
            sbInstr.append("\n").append(MessageFormat.format(START_INSTRUCTION, host.getDisplayName()));

        return MessageFormat.format(OUTSTANDING_FORMAT, server.getSettings().getString(ServerSetting.ARENA_NAME), formatEntrants(), sbInstr.toString());
    }

    public void enterGame(ServerMember member){
        enterGame(member, 0);
    }

    public void enterGame(ServerMember member, int bet){
        entrants.add(new FfaPlayer(member, bet));
    }

    private String formatEntrants(){
        StringBuilder sb = new StringBuilder();
        for(FfaPlayer player : entrants){
            sb.append(", ").append(player.getName());
        }

        return sb.toString().substring(2).trim();
    }

}
