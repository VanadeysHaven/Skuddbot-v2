package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;

/**
 * Represents a player in FFA
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.2
 * @since ALPHA-2.2
 */
public class FfaPlayer {

    @Getter private ServerMember player;
    @Getter private int bet;
    @Getter private int kills;
    private boolean isAlive;

    public FfaPlayer(ServerMember player, int bet){
        this.player = player;
        this.bet = bet;
        kills = 0;
        isAlive = true;
    }

    public boolean hasBetted(){
        return bet > 0;
    }

    public void kill(){
        isAlive = false;
    }

    public void incrementKills(){
        kills++;
    }

    public boolean isAlive(){
        return isAlive;
    }

    public String getNameAndBet(){
        String ret = player.getDisplayName();
        if(hasBetted()){
            ret += " (" + bet + ")";
        }

        return ret;
    }

    public String getName(){
        return player.getDisplayName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FfaPlayer player1 = (FfaPlayer) o;
        return player.equals(player1.player);
    }
}
