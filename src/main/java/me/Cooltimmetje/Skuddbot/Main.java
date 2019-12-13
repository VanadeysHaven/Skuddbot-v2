package me.Cooltimmetje.Skuddbot;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {

    public static void main(String[] args){
        String token = args[0];
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        api.addMessageCreateListener(event -> {
            if(event.getMessageContent().toLowerCase().startsWith("!ping")){
                event.getChannel().sendMessage("PONG!");
            }
            if(event.getMessageContent().toLowerCase().startsWith("!logout")){
                api.disconnect();
            }
        });
    }

}
