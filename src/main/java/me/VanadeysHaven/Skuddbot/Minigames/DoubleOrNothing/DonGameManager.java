package me.VanadeysHaven.Skuddbot.Minigames.DoubleOrNothing;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Utilities.CooldownManager;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;

/**
 * Class for managing Double or Nothing games on a server level.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.1.1
 */
public class DonGameManager {

    private static final int COOLDOWN = 60;

    @Getter private long serverId;
    private CooldownManager cooldownManager;
    private ArrayList<DonGame> games;

    public DonGameManager(long serverId){
        this.serverId = serverId;
        cooldownManager = new CooldownManager(COOLDOWN);
        games = new ArrayList<>();
    }

    public boolean isOnCooldown(long userId){
        return cooldownManager.isOnCooldown(userId);
    }

    public boolean hasGameInProgress(long userId) {
        for(DonGame game : games)
            if(game.getUser().getId() == userId)
                return true;

        return false;
    }

    public void startGame(User user, int bet, TextChannel channel) {
        DonGame game = new DonGame(user, bet, channel, getServerInstance(), this);
        games.add(game);
    }

    private Server getServerInstance(){
        Server server = Main.getSkuddbot().getApi().getServerById(serverId).orElse(null); assert server != null;
        return server;
    }


    public void endGame(DonGame game){
        endGame(game, true);
    }

    public void endGame(DonGame game, boolean startCooldown){
        games.remove(game);
        if(startCooldown)
            cooldownManager.startCooldown(game.getUser().getId());
    }

}
