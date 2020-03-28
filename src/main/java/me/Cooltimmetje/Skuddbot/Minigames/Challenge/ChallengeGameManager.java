package me.Cooltimmetje.Skuddbot.Minigames.Challenge;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Manager for managing challenge games on a server level.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.1
 * @since ALPHA-2.1
 */
public class ChallengeGameManager {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeGameManager.class);

    private static final int COOLDOWN = 60;

    @Getter private long serverId;
    private ArrayList<ChallengeGame> games;
    private CooldownManager cooldownManager;

    public ChallengeGameManager(long serverId){
        logger.info("Creating ChallengeGameManager for server id " + serverId);
        this.serverId = serverId;
        games = new ArrayList<>();

        cooldownManager = new CooldownManager(COOLDOWN);
    }

    public void process(User user1, User user2, Message message){
        for(ChallengeGame game : games)
            if(game.isMatch(user1, user2)) {
                if(game.isOpen()) game.setChallengerTwo(user1);
                game.addMessage(message);
                game.fight();
                return;
            }

        if(hasOutstandingGame(user1)){
            MessagesUtils.addReaction(message, Emoji.X, "You have an outstanding challenge, you can cancel it with `!challenge cancel`.");
        } else {
            addGame(user1, user2, message);
        }
    }

    public void process(User user1, Message message){
        addGame(user1, null, message);
    }

    public void processReaction(Message message, User user2){
        for(ChallengeGame game : games){
            if(game.isMatch(message, user2)){
                if(game.isOpen()) game.setChallengerTwo(user2);
                game.fight();
            }
        }
    }

    public void addGame(User user1, User user2, Message message){
        ChallengeGame game = new ChallengeGame(user1, user2, message, getServer());
        games.add(game);
    }

    private Server getServer(){
        Server server = Main.getSkuddbot().getApi().getServerById(serverId).orElse(null); assert server != null;
        return server;
    }

    public void cancelGame(User user1){
        ChallengeGame toRemove = null;
        for(ChallengeGame game : games)
            if(game.getChallengerOne().getId() == user1.getId())
                toRemove = game;

        if(toRemove == null)
            return;

        removeGame(toRemove);
    }

    public boolean hasOutstandingGame(User user1){
        for(ChallengeGame game : games)
            if(game.getChallengerOne().getId() == user1.getId())
                return true;

        return false;
    }

    public void removeGame(ChallengeGame game){
        game.deleteMessages();
        games.remove(game);
    }

    public boolean isOnCooldown(long identifier){
        return cooldownManager.isOnCooldown(identifier);
    }

    public void startCooldown(ChallengeGame game) {
        cooldownManager.startCooldown(game.getChallengerOne().getId());
        cooldownManager.startCooldown(game.getChallengerTwo().getId());
    }

}
