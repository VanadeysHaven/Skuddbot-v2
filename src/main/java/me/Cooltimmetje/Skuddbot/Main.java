package me.Cooltimmetje.Skuddbot;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    final static Logger logger = LoggerFactory.getLogger(Main.class);

    @Getter private static Skuddbot skuddbot;

    public static void main(String[] args){
        logger.info("Starting Skuddbot v2...");
        String token = args[0];
        skuddbot = new Skuddbot(token);
        skuddbot.registerCommands();
        skuddbot.buildAndLogin();
        skuddbot.registerListeners();
    }

}
