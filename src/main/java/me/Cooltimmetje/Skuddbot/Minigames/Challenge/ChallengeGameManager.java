package me.Cooltimmetje.Skuddbot.Minigames.Challenge;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Currencies.Currency;
import me.Cooltimmetje.Skuddbot.Profiles.Users.SkuddUser;
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
 * @version 2.2.1
 * @since 2.1
 */
public class ChallengeGameManager {

    private static final Logger logger = LoggerFactory.getLogger(ChallengeGameManager.class);
    private static final ProfileManager pm = ProfileManager.getInstance();

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

    public void processAccept(User user1, User user2, Message message, int placedBet){
        for(ChallengeGame game : games)
            if(game.isMatch(user1, user2)) {
                SkuddUser su = pm.getUser(serverId, user1.getId());
                if(!su.getCurrencies().hasEnoughBalance(Currency.SKUDDBUX, game.getChallengerOne().getBet())){
                    MessagesUtils.addReaction(message, Emoji.X, "You do not have enough Skuddbux to make this bet: " + game.getChallengerOne().getBet());
                    return;
                }

                if(game.isOpen()) game.setChallengerTwo(new ChallengePlayer(serverId, user1.getId()));
                game.addMessage(message);
                game.fight();
                return;
            }

        if(hasOutstandingGame(user1)){
            MessagesUtils.addReaction(message, Emoji.X, "You have an outstanding challenge, you can cancel it with `!challenge cancel`.");
            return;
        }

        addGame(user1, user2, message, placedBet);
    }

    public void processAccept(User user1, Message message, int placedBet){
        addGame(user1, null, message, placedBet);
    }

    public void processDecline(User user1, User user2, Message message) {
        ChallengeGame toDecline = null;
        for(ChallengeGame game : games)
            if (game.isMatch(user1, user2, true))
                toDecline = game;

        if(toDecline != null)
            toDecline.decline();
        else {
            MessagesUtils.addReaction(message, Emoji.X, "No fight found with user " + user2.getDisplayName(getServer()) + " that can be declined.");
            return;
        }

        MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Challenge declined.");
    }

    public void addGame(User user1, User user2, Message message, int placedBet){
        SkuddUser su1 = pm.getUser(serverId, user1.getId());

        if(user2 != null) {
            SkuddUser su2 = pm.getUser(serverId, user2.getId());
            int user2bal = su2.getCurrencies().getInt(Currency.SKUDDBUX);
            placedBet = Math.min(placedBet, user2bal);
        }

        ChallengePlayer challengerOne = new ChallengePlayer(serverId, user1.getId(), placedBet);
        su1.getCurrencies().incrementInt(Currency.SKUDDBUX, placedBet * -1);

        ChallengePlayer challengerTwo = null;
        if(user2 != null)
            challengerTwo = new ChallengePlayer(serverId, user2.getId());

        ChallengeGame game = new ChallengeGame(challengerOne, challengerTwo, message, getServer(), this);
        games.add(game);
    }

    private Server getServer(){
        Server server = Main.getSkuddbot().getApi().getServerById(serverId).orElse(null); assert server != null;
        return server;
    }

    public void cancelGame(User user1){
        ChallengeGame toCancel = null;
        for(ChallengeGame game : games)
            if(game.getChallengerOne().getMember().getId().getDiscordId() == user1.getId())
                toCancel = game;

        if(toCancel != null)
            toCancel.cancel();
    }

    public boolean hasOutstandingGame(User user1){
        for(ChallengeGame game : games)
            if(game.getChallengerOne().getMember().getId().getDiscordId() == user1.getId())
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
        cooldownManager.startCooldown(game.getChallengerOne().getMember().getId().getDiscordId());
        cooldownManager.startCooldown(game.getChallengerTwo().getMember().getId().getDiscordId());
    }
}
