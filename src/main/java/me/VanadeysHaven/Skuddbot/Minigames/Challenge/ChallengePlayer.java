package me.VanadeysHaven.Skuddbot.Minigames.Challenge;

import lombok.Getter;
import lombok.Setter;
import me.VanadeysHaven.Skuddbot.Profiles.ServerMember;

/**
 * represents a player of challenge
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.2.1
 */
public class ChallengePlayer {

    @Getter private ServerMember member;
    @Getter @Setter private int bet;

    public ChallengePlayer(ServerMember member, int bet){
        this.member = member;
        this.bet = bet;
    }

    public ChallengePlayer(ServerMember member){
        this(member, 0);
    }

    public ChallengePlayer(long serverId, long userId, int bet){
        this(new ServerMember(serverId, userId), bet);
    }

    public ChallengePlayer(long serverId, long userId){
        this(serverId, userId, 0);
    }

    public String getName(){
        return getMember().getDisplayName();
    }

    public String getNameAndBet(){
        String ret = getMember().getDisplayName();
        if(hasBetted())
            ret += " (" + getBet() + ")";

        return ret;
    }

    public boolean hasBetted() {
        return bet > 0;
    }
}
