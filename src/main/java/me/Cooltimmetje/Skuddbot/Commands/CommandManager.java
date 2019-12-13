package me.Cooltimmetje.Skuddbot.Commands;

import org.javacord.api.entity.message.Message;

import java.util.ArrayList;

public class CommandManager {

    private ArrayList<Command> commands;

    public CommandManager(){
        this.commands = new ArrayList<>();
    }

    public void registerCommand(Command command){
        commands.add(command);
    }

    public void process(Message message){
        String commandPrefix = ">>";
        String requestedInvoker = message.getContent().split(" ")[0].toLowerCase();
        if(!requestedInvoker.startsWith(commandPrefix)) return;
        requestedInvoker = requestedInvoker.substring(commandPrefix.length());
        for(Command command : commands){
            for(String invoker : command.getInvokers()){
                if (requestedInvoker.equals(invoker)) {
                    command.run(message);
                    return;
                }
            }
        }
    }

}
