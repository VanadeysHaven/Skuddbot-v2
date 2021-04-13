package me.Cooltimmetje.Skuddbot.Commands;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.Events.ReactionButtonClickedEvent;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionButton;
import me.Cooltimmetje.Skuddbot.Listeners.Reactions.ReactionUtils;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.StatPageManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import me.Cooltimmetje.Skuddbot.Utilities.UserUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

/**
 * Used to view and edit stats.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.0
 */
public class StatsCommand extends Command {

    private static final int OVERVIEW_EXPIRE_TIME = 10; //in minutes

    private static final Logger logger = LoggerFactory.getLogger(StatsCommand.class);
    private static final StatPageManager spm = StatPageManager.getInstance();
    private static final UserUtils uu = UserUtils.getInstance();

    private static final String INVALID_ARGS = "Invalid argument usage:\n" +
            "View: `!stats [mention/userId]`\n" +
            "Edit: `!stats <mention/userId> <stat> <add/remove/set> <amount>`";

    public StatsCommand(){
        super(new String[]{"stats"}, "View/edit stats of users with this command.", "https://wiki.skuddbot.xyz/features/stats#view-and-edit");
    }

    @Override
    public void run(Message message, String content) {
        String[] args = content.split(" ");
        if(args.length >= 5)
            editValue(message, content);
        else if(args.length >= 1)
            showStats(message, content);
        else
            MessagesUtils.addReaction(message, Emoji.X, INVALID_ARGS);
    }

    private void showStats(Message message, String content){
        String[] args = content.split(" ");
        Server server = message.getServer().orElse(null); assert server != null;
        User author = message.getAuthor().asUser().orElse(null); assert author != null;

        User target = message.getAuthor().asUser().orElse(null); assert target != null;
        if(args.length > 1) {
            if(!message.getMentionedUsers().isEmpty()){
                target = message.getMentionedUsers().get(0);
            } else if (MiscUtils.isLong(args[1])) {
                if(uu.doesUserExist(args[1])) {
                    target = uu.getUser(args[1]);
                } else {
                    MessagesUtils.addReaction(message, Emoji.X, "The user with ID `" + args[1] + "` doesn't exist.");
                    return;
                }
            } else {
                MessagesUtils.addReaction(message, Emoji.X, "The string `" + args[1] + "` cannot be parsed to a user. - Please provide a mention or ID.");
                return;
            }
        }

        deactivateOverview(author.getId());
        overviews.add(new Overview(message, pm.getUser(server.getId(), target.getId()), author.getId()));
    }

    private static ArrayList<Overview> overviews = new ArrayList<>();

    private void deactivateOverview(long userId){
        Iterator<Overview> it = overviews.iterator();
        while(it.hasNext()){
            Overview cur = it.next();
            if(cur.getAuthor() == userId){
                cur.unregisterButtons(true);
                it.remove();
            }
        }
    }

    public static void clearOverviews(){
        Iterator<Overview> it = overviews.iterator();
        while(it.hasNext()) {
            Overview cur = it.next();

            long expireTimeMillis = OVERVIEW_EXPIRE_TIME * 60 * 1000;
            if(expireTimeMillis < cur.getTimeSinceInitiation()) {
                cur.unregisterButtons(true);
                it.remove();
            }
        }
    }

    private class Overview {

        private final int MAX_PAGE = spm.getPageAmount();

        private int page;
        @Getter private final long author;
        private final SkuddUser user;
        private final TextChannel textChannel;
        private final Server server;
        private final long timeInitiated;
        private final Message message;
        private final ArrayList<ReactionButton> buttons;

        private Overview(Message message, SkuddUser user){
            this(message, user, user.getId().getDiscordId());
        }

        private Overview(Message message, SkuddUser user, long author) {
            page = 1;
            this.author = author;
            this.user = user;
            textChannel = message.getChannel();
            server = message.getServer().orElse(null);
            assert server != null;
            timeInitiated = System.currentTimeMillis();
            message.delete();

            this.message = MessagesUtils.sendEmbed(textChannel, spm.getPage(page).generateOverview(user));
            buttons = new ArrayList<>();
            buttons.add(ReactionUtils.registerButton(this.message, Emoji.ARROW_LEFT, this::prevPage, user.getId().getDiscordId(), author));
            buttons.add(ReactionUtils.registerButton(this.message, Emoji.ARROW_RIGHT, this::nextPage, user.getId().getDiscordId(), author));
            buttons.add(ReactionUtils.registerButton(this.message, Emoji.ARROWS_CC, this::refresh, user.getId().getDiscordId(), author));
        }

        private void nextPage(ReactionButtonClickedEvent event){
            event.undoReaction();

            int newPage = page + 1;
            if(checkPage(newPage)){
                page = newPage;
                update();
            }
        }

        private void prevPage(ReactionButtonClickedEvent event){
            event.undoReaction();

            int newPage = page - 1;
            if(checkPage(newPage)){
                page = newPage;
                update();
            }
        }

        private void refresh(ReactionButtonClickedEvent event){
            event.undoReaction();
            update();
        }

        private boolean checkPage(int newPage){
            return newPage >= 1 && newPage <= MAX_PAGE;
        }

        private void update(){
            message.edit(spm.getPage(page).generateOverview(user));
        }

        private void unregisterButtons(boolean removeReactions) {
            if(removeReactions) message.removeAllReactions();

            for(ReactionButton button : buttons)
                button.unregister();
        }

        private long getTimeSinceInitiation(){
            return System.currentTimeMillis() - timeInitiated;
        }

    }

    private void editValue(Message message, String content) { // command name stat operation amount
        String[] args = content.split(" ");
        Server server = message.getServer().orElse(null); assert server != null;
        User author = message.getUserAuthor().orElse(null); assert author != null;
        if(!pm.getUser(server.getId(), author.getId()).getPermissions().hasPermission(PermissionLevel.SERVER_ADMIN)){
            MessagesUtils.addReaction(message, Emoji.X, "You are missing a required permission to do this; " + PermissionLevel.SERVER_ADMIN);
            return;
        }

        User user;
        if(!message.getMentionedUsers().isEmpty()){
            user = message.getMentionedUsers().get(0);
            if(!args[1].replace("<@!", "<@").equalsIgnoreCase(user.getMentionTag())) {
                MessagesUtils.addReaction(message, Emoji.X, INVALID_ARGS);
                return;
            }
        } else if (MiscUtils.isLong(args[1])){
            try {
                user = Main.getSkuddbot().getApi().getUserById(Long.parseLong(args[1])).get();
            } catch (InterruptedException | ExecutionException e) {
                MessagesUtils.addReaction(message, Emoji.X, "User with ID " + args[1] + " not found.");
                return;
            }
        } else {
            MessagesUtils.addReaction(message, Emoji.X, INVALID_ARGS);
            return;
        }
        if(user.isBot()){
            MessagesUtils.addReaction(message, Emoji.X, "User is a bot.");
            return;
        }

        Stat stat;
        try {
            stat = Stat.valueOf(args[2].toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException ignored){
            MessagesUtils.addReaction(message, Emoji.X, "`" + args[2] + "` is not a stat.");
            return;
        }
        if(!stat.isCanBeEdited()) {
            MessagesUtils.addReaction(message, Emoji.X, "`" + stat + "` cannot be edited.");
            return;
        }

        SkuddUser su = pm.getUser(server.getId(), user.getId());
        if(args[3].equalsIgnoreCase("set")){
            try {
                su.getStats().setString(stat, args[4]);
                MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Set stat `" + stat + "` to `" + args[4] + "` for user `" + user.getDisplayName(server) + "`");
                return;
            } catch (IllegalArgumentException e){
                MessagesUtils.addReaction(message, Emoji.X, e.getMessage());
            }
        }


        if(stat.getType() == ValueType.INTEGER) {
            if(!MiscUtils.isInt(args[4])){
                MessagesUtils.addReaction(message, Emoji.X, "`" + args[4] + "` is not an valid number for stat `" + stat + "`");
                return;
            }
            int mutationAmount = Integer.parseInt(args[4]);

            switch (args[3].toLowerCase()) {
                case "add":
                    su.getStats().incrementInt(stat, mutationAmount);
                    MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Added `" + mutationAmount + "` to stat `" + stat + "` for user `" + user.getDisplayName(server) + "`");
                    break;
                case "remove":
                    su.getStats().incrementInt(stat, mutationAmount * -1);
                    MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Removed `" + mutationAmount + "` from stat `" + stat + "` for user `" + user.getDisplayName(server) + "`");
                    break;
                default:
                    MessagesUtils.addReaction(message, Emoji.X, "`" + args[3] + "` is not an valid operation.");
                    break;
            }
        } else if (stat.getType() == ValueType.LONG){
            if(!MiscUtils.isLong(args[4])){
                MessagesUtils.addReaction(message, Emoji.X, "`" + args[4] + "` is not an number.");
                return;
            }
            long mutationAmount = Long.parseLong(args[4]);

            switch (args[3].toLowerCase()) {
                case "add":
                    su.getStats().incrementLong(stat, mutationAmount);
                    MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Added `" + mutationAmount + "` to stat `" + stat + "` for user `" + user.getDisplayName(server) + "`");
                    break;
                case "remove":
                    su.getStats().incrementLong(stat, mutationAmount * -1);
                    MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Removed `" + mutationAmount + "` from stat `" + stat + "` for user `" + user.getDisplayName(server) + "`");
                    break;
                default:
                    MessagesUtils.addReaction(message, Emoji.X, "`" + args[3] + "` is not an valid operation.");
                    break;
            }
        } else {
            MessagesUtils.addReaction(message, Emoji.X, "`" + args[3] + "` is not a valid operation for type " + stat.getType());
        }
    }
}
