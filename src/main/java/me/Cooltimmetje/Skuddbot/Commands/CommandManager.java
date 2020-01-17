package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * This class is responsible for registering commands and process incoming requests for commands.
 *
 * @author Tim (Cooltimmetje)
 * @since ALPHA-2.0
 * @version ALPHA-2.0
 */
public class CommandManager {

    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);
    private static final ServerManager sm = new ServerManager();
    private static final ProfileManager pm = new ProfileManager();

    private ArrayList<Command> commands;

    public CommandManager(){
        commands = new ArrayList<>();
    }

    public void registerCommand(Command command){
        logger.info("Registering command " + command.toString() + " with invokers " + String.join(",", command.getInvokers()) + " and required permission " + command.getRequiredPermission());
        commands.add(command);
    }

    public void registerCommand(Command... commands){
        for(Command command : commands){
            registerCommand(command);
        }
    }

    public void process(Message message){
        Server server = message.getServer().orElse(null);
        String commandPrefix = "!";
        if(server != null) {
            commandPrefix = sm.getServer(server.getId()).getSettings().getString(ServerSetting.COMMAND_PREFIX).replace("_", " ");
        }
        if(!message.getContent().startsWith(commandPrefix)) return;
        String messageContent = message.getContent().substring(commandPrefix.length());
        String requestedInvoker = messageContent.split(" ")[0];
        SkuddUser su = pm.getUser(server.getId(), message.getAuthor().getId());

        for(Command command : commands){
            for(String invoker : command.getInvokers()){
                if (requestedInvoker.equals(invoker)) {
                    if(su.hasPermissionLevel(command.getRequiredPermission())) {
                        command.run(message, messageContent);
                    } else {
                        MessagesUtils.addReaction(message, Emoji.X, "You do not have the required permission to use this command. Permission required: " + command.getRequiredPermission());
                    }
                    return;
                }
            }
        }
    }

}
