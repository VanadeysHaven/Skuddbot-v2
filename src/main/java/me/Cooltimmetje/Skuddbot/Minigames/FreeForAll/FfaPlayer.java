package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;

/**
 * Represents a player in FFA
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.1
 * @since ALPHA-2.1
 */
public class FfaPlayer {

    private ServerMember player;
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

}
