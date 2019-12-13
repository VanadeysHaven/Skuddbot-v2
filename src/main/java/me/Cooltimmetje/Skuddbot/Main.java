package me.Cooltimmetje.Skuddbot;

import lombok.Getter;

public class Main {

    @Getter private static Skuddbot skuddbot;

    public static void main(String[] args){
        String token = args[0];
        skuddbot = new Skuddbot(token);
        skuddbot.registerCommands();
        skuddbot.buildAndLogin();
        skuddbot.registerListeners();
    }

}
