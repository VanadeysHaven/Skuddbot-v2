package me.Cooltimmetje.Skuddbot;

import me.Cooltimmetje.Skuddbot.Commands.CommandManager;
import me.Cooltimmetje.Skuddbot.Commands.PingCommand;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Skuddbot {

    private DiscordApi api;
    private String token;
    private CommandManager commandManager;

    public Skuddbot(String token){
        this.token = token;
        this.commandManager = new CommandManager();
    }

    public void buildAndLogin(){
        this.api = new DiscordApiBuilder().setToken(token).login().join();
    }

    public void registerCommands() {
        this.commandManager.registerCommand(new PingCommand());
    }

    public void registerListeners() {
        api.addMessageCreateListener(event -> {
           this.commandManager.process(event.getMessage());
        });
    }
}
