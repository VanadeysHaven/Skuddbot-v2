package me.Cooltimmetje.Skuddbot.Commands.HelpCommand;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.PagedMessage;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

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
            ">>> *View different pages using " + Emoji.ARROW_LEFT.getUnicode() + " and " + Emoji.ARROW_RIGHT.getUnicode() + " reactions.*\n*{5}*";
    private static final String DM_HELP = "To view commands for a server, head to that server, and type `!help` in any channel.";
    private static final String SERVER_HELP = "To view DM commands, click the " + Emoji.ARROW_LEFT_HOOK.getUnicode() + " reaction.\n" + DM_HELP;
    private static final int PAGE_SIZE = 5;
    private static final HelpGenerator hg = Main.getSkuddbot().getHelpGenerator();

    private List<Overview> overviews = new ArrayList<>();

    public HelpCommand() {
        super(new String[]{"help"}, "Lists all commands available for use.", "https://wiki.skuddbot.xyz/commands/help-command",PermissionLevel.DEFAULT, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        User author = message.getUserAuthor().orElse(null); assert author != null;
        long serverId = -1;
        Server server = message.getServer().orElse(null);
        if(server != null)
            serverId = server.getId();

        TextChannel channel = author.getPrivateChannel().orElse(author.openPrivateChannel().join());
        int pages = getPageAmount(author.getId(), serverId);

        if(hasActiveOverview(author.getId())) {
            if(serverId == -1)
                MessagesUtils.addReaction(message, Emoji.X, "You already have a active overview.");
            else {
                getOverview(author.getId()).setServer(server);
                MessagesUtils.addReaction(message, Emoji.MAILBOX_WITH_MAIL, "Head back to your DM's, the help message has been updated.");
                MessagesUtils.sendPlain(channel, "ding").delete();
            }
        } else {
            if (serverId == -1)
                overviews.add(new Overview(pages, channel, author));
            else
                overviews.add(new Overview(pages, channel, author, server));

            if (message.getChannel().getType() != ChannelType.PRIVATE_CHANNEL)
                MessagesUtils.addReaction(message, Emoji.MAILBOX_WITH_MAIL, "Sliding into the DM's... :smirk:");
        }
    }

    private boolean hasActiveOverview(long userId){
        for(Overview overview : overviews)
            if(overview.getUser().getId() == userId)
                return true;

        return false;
    }

    private Overview getOverview(long userId){
        for(Overview overview : overviews)
            if(overview.getUser().getId() == userId)
                return overview;

        return null;
    }

    public int getPageAmount(long userId, long serverId){
        int commandAmount = hg.getCommandAmount(userId, serverId);
        return (int) Math.ceil((double) commandAmount / PAGE_SIZE);
    }

    private class Overview extends PagedMessage {

        @Getter private User user;
        private Server server;

        private Overview(int maxPages, TextChannel channel, User user, Server server){
            super(maxPages, channel, user.getId());
            this.user = user;
            this.server = server;
            construct();
            setAutoExpire(1800);
            addButton(Emoji.ARROW_LEFT_HOOK, e -> returnToDm(), e -> returnToDm());
        }

        private Overview(int maxPages, TextChannel channel, User user) {
            this(maxPages, channel, user, null);
        }

        public String getContent(){
            long serverId = -1;
            if(server != null)
                serverId = server.getId();

            String username = user.getName();
            String serverName = serverId == -1 ? "DM's" : server.getName();
            int curPage = getPage();
            int maxPage = getMaxPage();
            String commands = hg.getHelp(user.getId(), serverId, PAGE_SIZE, (getPage() - 1) * PAGE_SIZE);
            String help = serverId == -1 ? DM_HELP : SERVER_HELP;

            return MessageFormat.format(MESSAGE_FORMAT, username, serverName, curPage, maxPage, commands, help);
        }

        private void returnToDm(){
            if(server != null) {
                server = null;
                setMaxPage(getPageAmount(user.getId(), -1));
                setPage(1);
            }
        }

        private void setServer(Server server){
            this.server = server;
            setMaxPage(getPageAmount(user.getId(), server.getId()));
            setPage(1);
        }

        @Override
        public void deactivate() {
            overviews.remove(this);
            super.deactivate();
        }
    }


}
