package me.VanadeysHaven.Skuddbot.Minigames.DoubleOrNothing;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Utilities.CooldownManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.util.ArrayList;

/**
 * Class for managing Double or Nothing games on a server level.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
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
            if(game.getUser().getIdLong() == userId)
                return true;

        return false;
    }

    public void startGame(User user, int bet, MessageChannel channel) {
        DonGame game = new DonGame(user, bet, channel, getServerInstance(), this);
        games.add(game);
    }

    private Guild getServerInstance(){
        Guild server = Main.getSkuddbot().getApi().getGuildById(serverId); assert server != null;
        return server;
    }


    public void endGame(DonGame game){
        endGame(game, true);
    }

    public void endGame(DonGame game, boolean startCooldown){
        games.remove(game);
        if(startCooldown)
            cooldownManager.startCooldown(game.getUser().getIdLong());
    }

}
