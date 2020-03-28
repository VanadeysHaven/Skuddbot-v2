package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;
import org.javacord.api.entity.channel.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Manages games on a server level.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class BlackjackGameManager {

    private static final Logger logger = LoggerFactory.getLogger(BlackjackGameManager.class);
    private static final int COOLDOWN = 60;

    @Getter private long serverId;
    private ArrayList<BlackjackGame> games;
    private CooldownManager cm;

    public BlackjackGameManager(long serverId){
        logger.info("Creating new BlackjackGameManager for server ID " + serverId);
        this.serverId = serverId;
        games = new ArrayList<>();
        cm = new CooldownManager(COOLDOWN);
    }

    public BlackjackGame getGame(Identifier id){
        for(BlackjackGame game : games)
            if(id.equals(game.getId())) return game;

        return null;
    }

    public void createGame(Identifier id, TextChannel channel){
        BlackjackGame game = new BlackjackGame(id, channel);
        games.add(game);

        game.preGameChecks();
    }

    public boolean hasGameActive(Identifier id){
        return getGame(id) != null;
    }

    public void cleanUp(Identifier id){
        BlackjackGame toRemove = null;
        for(BlackjackGame game : games)
            if(game.getId().equals(id))
                toRemove = game;

        if(toRemove == null)
            return;

        games.remove(toRemove);
        cm.startCooldown(id.getDiscordId());
    }

    public boolean isOnCooldown(Identifier id){
        return cm.isOnCooldown(id.getDiscordId());
    }

}
