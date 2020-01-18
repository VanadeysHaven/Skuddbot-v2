package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Enums.ServerSetting;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Profiles.Users.PermissionManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.channel.ChannelType;
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

    public String getHelp(Identifier id, int amount, int offset){
        return getHelp(id.getDiscordId(), id.getServerId(), amount, offset);
    }

    public String getHelp(long userId, long serverId, int amount, int offset){
        PermissionManager permissions;
        Command.Location location;
        String commandPrefix = "!";
        if(serverId == -1){
            permissions = new PermissionManager(userId);
            location = Command.Location.DM;
        } else {
            permissions = pm.getUser(serverId, userId).getPermissions();
            location = Command.Location.SERVER;
            commandPrefix = sm.getServer(serverId).getSettings().getString(ServerSetting.COMMAND_PREFIX).replace("_", " ");
        }

        StringBuilder sb = new StringBuilder();

        int position = 0;
        for(int i = 0; i < commands.size() && position < amount + offset; i++){
            Command command = commands.get(i);
            if(permissions.hasPermission(command.getRequiredPermission())) {
                if(command.getAllowedLocation() == Command.Location.BOTH || command.getAllowedLocation() == location) {
                    if(position >= offset)
                        sb.append(command.formatHelp(commandPrefix)).append("\n");
                    position++;
                }
            }
        }

        return sb.toString();
    }

    public int getCommandAmount(long userId, long serverId){
        PermissionManager permissions;
        Command.Location location;
        int amount = 0;
        if(serverId == -1) {
            permissions = new PermissionManager(userId);
            location = Command.Location.DM;
        } else {
            permissions = pm.getUser(serverId, userId).getPermissions();
            location = Command.Location.SERVER;
        }

        for(Command command : commands){
            if(permissions.hasPermission(command.getRequiredPermission())){
                if(command.getAllowedLocation() == Command.Location.BOTH || command.getAllowedLocation() == location){
                    amount++;
                }
            }
        }

        return amount;
    }

    public int getCommandAmount(Identifier id){
        return getCommandAmount(id.getDiscordId(), id.getServerId());
    }

    public void process(Message message){
        if(message.getAuthor().isBotUser()) return;
        if(message.getChannel().getType() == ChannelType.PRIVATE_CHANNEL){
            processPrivate(message);
            return;
        }
        Server server = message.getServer().orElse(null);
        assert server != null;
        String commandPrefix = sm.getServer(server.getId()).getSettings().getString(ServerSetting.COMMAND_PREFIX).replace("_", " ");
        if(!message.getContent().startsWith(commandPrefix)) return;
        String messageContent = message.getContent().substring(commandPrefix.length());
        String requestedInvoker = messageContent.split(" ")[0];
        SkuddUser su = pm.getUser(server.getId(), message.getAuthor().getId());
        PermissionManager permissions = su.getPermissions();

        for(Command command : commands){
            for(String invoker : command.getInvokers()){
                if (requestedInvoker.equals(invoker)) {
                    if (command.getAllowedLocation() == Command.Location.BOTH || command.getAllowedLocation() == Command.Location.SERVER) {
                        if (permissions.hasPermission(command.getRequiredPermission())) {
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

    private void processPrivate(Message message){
        String commandPrefix = "!";
        if(!message.getContent().startsWith(commandPrefix)) return;
        String messageContent = message.getContent().substring(commandPrefix.length());
        String requestedInvoker = messageContent.split(" ")[0];
        PermissionManager permissions = new PermissionManager(message.getAuthor().getId());
        for(Command command : commands){
            for(String invoker : command.getInvokers()){
                if(requestedInvoker.equals(invoker)){
                    if(command.getAllowedLocation() == Command.Location.BOTH || command.getAllowedLocation() == Command.Location.DM){
                        if(permissions.hasPermission(command.getRequiredPermission())){
                            command.run(message, messageContent);
                        }
                    } else {
                        MessagesUtils.addReaction(message, Emoji.X, "You do not have the required permission to use this command. Permission required: " + command.getRequiredPermission());
                    }
                }
            }
        }
    }

}
