package me.Cooltimmetje.Skuddbot.Commands.HelpCommand;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * Help for commands
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.0
 */
public class HelpCommand extends Command {

    private static final String MESSAGE_FORMAT = "Commands for **{0}** in **{1}** | `[Page {2}/{3}]`\n\n" +
            "{4}" +
            "> *View different pages using `!help [page]`.*\n> *{5}*";
    private static final int PAGE_SIZE = 5;
    private static final HelpGenerator hg = Main.getSkuddbot().getHelpGenerator();

    private HashMap<Long,Long> currentServer = new HashMap<>();

    public HelpCommand() {
        super(new String[]{"help"}, "Lists all commands available for use.", "https://wiki.skuddbot.xyz/commands/help-command",PermissionLevel.DEFAULT, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        String[] args = content.split( " ");
        int page = 1;
        if(args.length >= 2){
            if(MiscUtils.isInt(args[1])){
                page = Integer.parseInt(args[1]);
            } else if (args[1].equalsIgnoreCase("dm")) {
                currentServer.remove(message.getAuthor().getId());
            } else {
                MessagesUtils.addReaction(message, Emoji.X, args[1] + " is not a number.");
            }
        }
        String commands;
        Server server = message.getServer().orElse(null);
        HelpGenerator hg = Main.getSkuddbot().getHelpGenerator();
        long serverId = -1;
        User author = message.getAuthor().asUser().orElse(null);
        assert author != null;
        String helpLocation = "DM's";

        if(server == null) {
            if(currentServer.containsKey(author.getId())){
                serverId = currentServer.get(author.getId());
            }

            server = Main.getSkuddbot().getApi().getServerById(serverId).orElse(null);
        }

        if (server != null) {
            serverId = server.getId();
            helpLocation = server.getName();
            currentServer.put(author.getId(), serverId);
        }

        commands = hg.getHelp(author.getId(), serverId, PAGE_SIZE, (page - 1) * PAGE_SIZE);
        int commandAmount = hg.getCommandAmount(author.getId(), serverId);
        int pages = (int) Math.ceil((double) commandAmount / PAGE_SIZE);
        if(page > pages){
            MessagesUtils.addReaction(message, Emoji.X, "Page " + page + " does not exist.");
            return;
        }

        if (message.getChannel().getType() != ChannelType.PRIVATE_CHANNEL) {
            MessagesUtils.addReaction(message, Emoji.MAILBOX_WITH_MAIL, "Sliding into the dm's... :smirk:");
        }
        MessagesUtils.sendPlain(author.getPrivateChannel().orElse(author.openPrivateChannel().join()), MessageFormat.format(MESSAGE_FORMAT,
                author.getDiscriminatedName(), helpLocation, page, pages, commands, helpLocation.equals("DM's") ? "Use the help command in a server to view the commands available in that server." : "Use `!help dm` to return to DM help."));
    }


}
