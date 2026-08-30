package me.VanadeysHaven.Skuddbot.Minigames.Challenge;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Enums.Emoji;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.ProfileManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Currencies.Currency;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import me.VanadeysHaven.Skuddbot.Utilities.CooldownManager;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.UserUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Manager for managing challenge games on a server level.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
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
                SkuddUser su = pm.getUser(serverId, user1.getIdLong());
                su.getCurrencies().incrementInt(Currency.SKUDDBUX, placedBet);
                if(!su.getCurrencies().hasEnoughBalance(Currency.SKUDDBUX, game.getChallengerOne().getBet())){
                    MessagesUtils.addReaction(message, Emoji.X, "You do not have enough Skuddbux to make this bet: " + game.getChallengerOne().getBet());
                    return;
                }

                su.getCurrencies().incrementInt(Currency.SKUDDBUX, game.getChallengerOne().getBet() * -1);
                if(game.isOpen()) game.setChallengerTwo(new ChallengePlayer(serverId, user1.getIdLong()));
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
            MessagesUtils.addReaction(message, Emoji.X, "No fight found with user " + UserUtils.getDisplayName(getServer(), user2) + " that can be declined.");
            return;
        }

        MessagesUtils.addReaction(message, Emoji.WHITE_CHECK_MARK, "Challenge declined.");
    }

    private void addGame(User user1, User user2, Message message, int placedBet){
        SkuddUser su1 = pm.getUser(serverId, user1.getIdLong());

        int actualBet = placedBet;
        if(user2 != null) {
            SkuddUser su2 = pm.getUser(serverId, user2.getIdLong());
            int user2bal = su2.getCurrencies().getInt(Currency.SKUDDBUX);
            actualBet = Math.min(placedBet, user2bal);
        }

        ChallengePlayer challengerOne = new ChallengePlayer(serverId, user1.getIdLong(), actualBet);
        if(actualBet < placedBet)
            su1.getCurrencies().incrementInt(Currency.SKUDDBUX, placedBet - actualBet);

        ChallengePlayer challengerTwo = null;
        if(user2 != null)
            challengerTwo = new ChallengePlayer(serverId, user2.getIdLong());

        ChallengeGame game = new ChallengeGame(challengerOne, challengerTwo, message, getServer(), this);
        games.add(game);
    }

    private Guild getServer(){
        Guild server = Main.getSkuddbot().getApi().getGuildById(serverId); assert server != null;
        return server;
    }

    public void cancelGame(User user1){
        ChallengeGame toCancel = null;
        for(ChallengeGame game : games)
            if(game.getChallengerOne().getMember().getId().getDiscordId() == user1.getIdLong())
                toCancel = game;

        if(toCancel != null)
            toCancel.cancel();
    }

    public boolean hasOutstandingGame(User user1){
        for(ChallengeGame game : games)
            if(game.getChallengerOne().getMember().getId().getDiscordId() == user1.getIdLong())
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
