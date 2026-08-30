package me.VanadeysHaven.Skuddbot.Commands.SuperAdmin;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Donator.DonatorManager;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;
import me.VanadeysHaven.Skuddbot.Utilities.UserUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * Used to manage donators.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class ManageDonatorsCommand extends Command {

    private static final String DM_MESSAGE = "Hello {0},\n\n" +
            "You have become a Skuddbot Donator! This means you have either donated to the bot, or contributed something significant to it! Either way, this is our way to say thank you and that you are awesome!\n" +
            "This comes with several fun perks! These perks come in the form of some commands, and have been added to your `!help` command automatically!\n" +
            "If any questions arise, you can refer to the wiki or ask Timmy!\n\n" +
            "Thank you for your continued support. " + Emoji.HEART.getUnicode();
    private static final DonatorManager dm = new DonatorManager();
    private static HashMap<Long, Long> confirm = new HashMap<>();

    public ManageDonatorsCommand() {
        super(new String[]{"donators"}, "Used to add and remove donators.", null, PermissionLevel.BOT_ADMIN, Location.BOTH);
    }

    @Override
    public void run(CommandRequest request) {
        String[] args = request.getContent().split(" ");
        Message message = request.getMessage();

        if(args.length < 3){
            MessagesUtils.addReaction(message, Emoji.X, "Not enough arguments! `!donators <add/remove> <id>`");
            return;
        }

        User user;
        long id;
        if(MiscUtils.isLong(args[2])){
            id = Long.parseLong(args[2]);
            if(!UserUtils.getInstance().doesUserExist(id)){
                MessagesUtils.addReaction(message, Emoji.X, "Could not find a user with the ID " + id);
                return;
            }
            user = UserUtils.getInstance().getUser(id);
        } else {
            MessagesUtils.addReaction(message, Emoji.X, args[2] + " is not a valid ID.");
            return;
        }

        switch(args[1].toLowerCase()){
            case "add":
                if(dm.isDonator(id)){
                    MessagesUtils.addReaction(message, Emoji.WARNING, "This user is already a donator!");
                    return;
                }
                dm.addDonator(id);
                MessagesUtils.sendPlain(user.openPrivateChannel().complete(), MessageFormat.format(DM_MESSAGE, user.getName()));
                MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "User `" + user.getName() + "` has been added as donator!");
                break;
            case "remove":
                if(!dm.isDonator(id)){
                    MessagesUtils.addReaction(message, Emoji.WARNING, "This user is not a donator.");
                    return;
                }
                if(!confirm.containsKey(message.getAuthor().getIdLong()) || confirm.get(message.getAuthor().getIdLong()) != id){ //TODO: BETTER CONFIRMATION
                    MessagesUtils.addReaction(message, Emoji.WARNING, "This action will delete all donator data associated with " + user.getName() + "'s account and remove them as a donator! If you continue, there's no way back. **Run this command again to confirm you action!**");
                    confirm.put(message.getAuthor().getIdLong(), id);
                    return;
                }
                confirm.remove(message.getAuthor().getIdLong());
                dm.removeDonator(id);
                MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "User `" + user.getName() + "` has been removed as a donator!");
                break;
        }
    }

}
