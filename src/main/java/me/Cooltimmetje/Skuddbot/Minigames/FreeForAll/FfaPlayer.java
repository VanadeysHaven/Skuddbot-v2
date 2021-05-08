package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Profiles.ServerMember;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Represents a player in FFA
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.1
 * @since 2.2
 */
public final class FfaPlayer {

    @Getter private ServerMember member;
    @Getter private int bounty;
    @Getter private int kills;
    @Getter private int collectedBounty;
    private boolean isAlive;

    public FfaPlayer(ServerMember member, int bounty){
        this.member = member;
        this.bounty = bounty;
        kills = 0;
        isAlive = true;
    }

    public boolean hasPlacedBounty(){
        return bounty > 0;
    }

    public Pair<Integer, Integer> kill(FfaPlayer killer){
        killer.incrementKills();

        int allowedCollection = Math.min(killer.getMaxBountyCollection(), bounty);
        killer.collectedBounty += allowedCollection;
        bounty -= allowedCollection;

        int splitCollection = splitCollectedBounty();
        killer.collectedBounty += splitCollection;
        collectedBounty -= splitCollection;

        isAlive = false;

        return new ImmutablePair<>(allowedCollection, splitCollection);
    }

    public void incrementKills(){
        kills++;
    }

    public boolean isAlive(){
        return isAlive;
    }

    public String getNameAndBounty(){
        String ret = member.getDisplayName();
        if(hasPlacedBounty()){
            ret += " (" + bounty + ")";
        }

        return ret;
    }

    public int splitCollectedBounty(){
        int half = collectedBounty / 2;
        collectedBounty -= half;

        return half;
    }

    public int getMaxBountyCollection(){
        return (int) (bounty * 2.5);
    }

    public String getName(){
        return member.getDisplayName();
    }

    public String getGameLogName(){
        return member.getGameLogName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FfaPlayer player1 = (FfaPlayer) o;
        return member.equals(player1.member);
    }
}
