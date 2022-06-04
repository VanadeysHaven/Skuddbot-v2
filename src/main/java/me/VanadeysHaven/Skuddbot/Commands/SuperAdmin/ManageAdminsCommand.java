package me.VanadeysHaven.Skuddbot.Commands.SuperAdmin;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Database.Query;
import me.VanadeysHaven.Skuddbot.Database.QueryExecutor;
import me.VanadeysHaven.Skuddbot.Database.QueryResult;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Utilities.Constants;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Command used to add admins.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.23
 * @since 2.0
 */
public class ManageAdminsCommand extends Command {

    private static final Logger logger = LoggerFactory.getLogger(ManageAdminsCommand.class);

    private static final String DM_MESSAGE = "Hello there,\n\n" +
            "Congratulations! You are now an Skuddbot admin. This means you can override almost all permissions. Please note that comes with some level of responsibility, don't abuse it!\n" +
            "If you have any questions regarding this, please contact Timmy.";

    public ManageAdminsCommand() {
        super(new String[]{"admins"}, "Used to add and remove admins.", null, PermissionLevel.TIMMY, Location.BOTH);

        if(Constants.adminUsers.isEmpty()){
            logger.info("No admins present, loading from database...");
            loadAdmins();
        }
    }

    @Override
    public void run(CommandRequest request) {
        String[] args = request.getContent().split(" ");
        Message message = request.getMessage();

        if(args.length < 3){
            MessagesUtils.addReaction(message, Emoji.X, "Not enough arguments! `!admins <add/remove> <id>`");
            return;
        }
        User user;
        long id;
        if(MiscUtils.isLong(args[2])){
            id = Long.parseLong(args[2]);
            user = Main.getSkuddbot().getApi().getUserById(id).join();
            if(user == null){
                MessagesUtils.addReaction(message, Emoji.X, "Could not find a user with the ID " + id);
                return;
            }
        } else {
            MessagesUtils.addReaction(message, Emoji.X, args[2] + " is not a valid ID.");
            return;
        }
        switch (args[1].toLowerCase()){
            case "add":
                if(Constants.adminUsers.contains(id)){
                    MessagesUtils.addReaction(message, Emoji.WARNING, "This user is already a admin.");
                    return;
                }
                Constants.adminUsers.add(id);

                QueryExecutor qe = null;
                try {
                    qe = new QueryExecutor(Query.ADD_ADMIN).setLong(1, id);
                    qe.execute();
                } catch (SQLException e){
                    e.printStackTrace();
                } finally {
                    assert qe != null;
                    qe.close();
                }

                MessagesUtils.sendPlain(user.getPrivateChannel().orElse(user.openPrivateChannel().join()), DM_MESSAGE);
                MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "User `" + user.getDiscriminatedName() + "` is now a admin.");
                break;
            case "remove":
                if(!Constants.adminUsers.contains(id)){
                    MessagesUtils.addReaction(message, Emoji.WARNING, "This user is not an admin.");
                    return;
                }
                Constants.adminUsers.remove(id);

                qe = null;
                try {
                    qe = new QueryExecutor(Query.DELETE_ADMIN).setLong(1, id);
                    qe.execute();
                } catch (SQLException e){
                    e.printStackTrace();
                } finally {
                    qe.close();
                }

                MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "User `" + user.getDiscriminatedName() + "` is no longer a admin.");
                break;
            default:
                break;
        }
    }

    private void loadAdmins(){
        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.LOAD_ADMINS);
            QueryResult qr = qe.executeQuery();
            while(qr.nextResult()){
                Constants.adminUsers.add(qr.getLong("discord_id"));
                logger.info(qr.getLong("discord_id") + " is a admin.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }
    }
}
