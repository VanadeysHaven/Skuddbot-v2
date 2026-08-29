package me.VanadeysHaven.Skuddbot.Commands.HelpCommand;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.PagedMessage;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Help for commands
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
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
        super(new String[]{"help"}, "Lists all commands available for use.", "https://wiki.skuddbot.xyz/commands/help-command", PermissionLevel.DEFAULT, Location.BOTH);
    }

    @Override
    public void run(CommandRequest request) {
        User user = request.getUser();
        long serverId = -1;
        Guild server = request.getGuild();
        Message message = request.getMessage();
        if(server != null)
            serverId = server.getIdLong();

        MessageChannel channel = user.openPrivateChannel().complete();
        int pages = getPageAmount(user.getIdLong(), serverId);

        if(hasActiveOverview(user.getIdLong())) {
            if(serverId == -1)
                MessagesUtils.addReaction(message, Emoji.X, "You already have a active overview.");
            else {
                getOverview(user.getIdLong()).setServer(server);
                MessagesUtils.addReaction(message, Emoji.MAILBOX_WITH_MAIL, "Head back to your DM's, the help message has been updated.");
                MessagesUtils.sendPlain(channel, "ding").delete().queue();
            }
        } else {
            if (serverId == -1)
                overviews.add(new Overview(pages, channel, user));
            else
                overviews.add(new Overview(pages, channel, user, server));

            if (request.getChannel().getType() != ChannelType.PRIVATE)
                MessagesUtils.addReaction(message, Emoji.MAILBOX_WITH_MAIL, "Sliding into the DM's... :smirk:");
        }
    }

    private boolean hasActiveOverview(long userId){
        for(Overview overview : overviews)
            if(overview.getUser().getIdLong() == userId)
                return true;

        return false;
    }

    private Overview getOverview(long userId){
        for(Overview overview : overviews)
            if(overview.getUser().getIdLong() == userId)
                return overview;

        return null;
    }

    public int getPageAmount(long userId, long serverId){
        int commandAmount = hg.getCommandAmount(userId, serverId);
        return (int) Math.ceil((double) commandAmount / PAGE_SIZE);
    }

    private class Overview extends PagedMessage {

        @Getter private User user;
        private Guild server;

        private Overview(int maxPages, MessageChannel channel, User user, Guild server){
            super(maxPages, channel, user.getIdLong());
            this.user = user;
            this.server = server;
            construct();
            setAutoExpire(1800);
            addButton(Emoji.ARROW_LEFT_HOOK, e -> returnToDm(), e -> returnToDm());
        }

        private Overview(int maxPages, MessageChannel channel, User user) {
            this(maxPages, channel, user, null);
        }

        public String getContent(){
            long serverId = -1;
            if(server != null)
                serverId = server.getIdLong();

            String username = user.getName();
            String serverName = serverId == -1 ? "DM's" : server.getName();
            int curPage = getPage();
            int maxPage = getMaxPage();
            String commands = hg.getHelp(user.getIdLong(), serverId, PAGE_SIZE, (getPage() - 1) * PAGE_SIZE);
            String help = serverId == -1 ? DM_HELP : SERVER_HELP;

            return MessageFormat.format(MESSAGE_FORMAT, username, serverName, curPage, maxPage, commands, help);
        }

        private void returnToDm(){
            if(server != null) {
                server = null;
                setMaxPage(getPageAmount(user.getIdLong(), -1));
                setPage(1);
            }
        }

        private void setServer(Guild server){
            this.server = server;
            setMaxPage(getPageAmount(user.getIdLong(), server.getIdLong()));
            setPage(1);
        }

        @Override
        public void deactivate() {
            overviews.remove(this);
            super.deactivate();
        }
    }


}
