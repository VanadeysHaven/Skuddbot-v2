package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;
import org.javacord.api.entity.channel.TextChannel;

import java.util.Iterator;

/**
 * Game manager, for managing data that needs to persist between games.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2
 * @since ALPHA-2.2
 */
public class FfaGameManager {

    private static final int COOLDOWN = 300;

    @Getter private long serverId;
    private FfaGame currentGame;
    private CooldownManager cooldownManager = new CooldownManager(COOLDOWN);

    public FfaGameManager(long serverId){
        this.serverId = serverId;
    }

    public boolean gameIsActive(){
        return currentGame != null;
    }

    private void createNewGame(TextChannel channel, ServerMember host){
        currentGame = new FfaGame(channel, host, this);
    }

    public void enterGame(TextChannel channel, ServerMember player){
        enterGame(channel, player, 0);
    }

    public void enterGame(TextChannel channel, ServerMember player, int bet){
        if(!gameIsActive()) {
            createNewGame(channel, player);
            if(bet == 0) return;
        }

        currentGame.enterGame(player, bet);
    }

    public boolean isInGame(ServerMember member){
        if(!gameIsActive())
            return false;

        return currentGame.isInGame(member);
    }

    public boolean isOnCooldown(long userId){
        if(gameIsActive()){
            return false;
        }

        return cooldownManager.isOnCooldown(userId);
    }

    public void startCooldown(long userId){
        cooldownManager.startCooldown(userId);
    }


    public void finishGame() {
        Iterator<FfaPlayer> it = currentGame.getPlayers();
        while(it.hasNext()){
            startCooldown(it.next().getPlayer().getId().getDiscordId());
        }

        currentGame = null;
    }

    public void runReminder() {
        if(currentGame != null)
            currentGame.runReminder();
    }
}
