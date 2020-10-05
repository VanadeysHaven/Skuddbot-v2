package me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands;

import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Card;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;

import java.util.ArrayList;

/**
 * Represents a hand of a player.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.2.1
 */
public class PlayerHand extends BlackjackHand {

    protected int oneBet;
    protected boolean oneIsDoubled;
    protected String oneRewardString;
    protected int oneXpReward;
    protected int oneSbReward;
    protected int oneJackpot;
    protected Stat[] oneIncrementStats;

    public PlayerHand(int bet){
        oneBet = bet;
    }

    public boolean isDoubleDownAllowed(int hand){
        ArrayList<Card> handList = getHand(hand);
        if(handList.size() > 2)  return false;

        return getHandValue(hand) <= 10;
    }

    public boolean isSplitAllowed(){
        if(this instanceof SplitPlayerHand) return false;
        ArrayList<Card> handList = getHand(ONE);
        if(handList.size() != 2) return false;
        Card cardOne = handList.get(0);
        Card cardTwo = handList.get(1);

        return cardOne.getRank() == cardTwo.getRank() || (cardOne.getRank().isFaceCard() && cardTwo.getRank().isFaceCard());
    }

    public int getBet(int hand){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");
        return oneBet;
    }

    public void doubleDown(int hand, Card card){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");

        oneBet *= 2;
        oneIsDoubled = true;
        addCard(1, card);
    }

    public boolean isDoubled(int hand){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");
        return oneIsDoubled;
    }

    public String getRewardString(int hand){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");
        return oneRewardString;
    }

    public void setRewardString(int hand, String rewardString){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");
        oneRewardString = rewardString;
    }

    public int getXpReward(int hand){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");
        return oneXpReward;
    }

    public void setXpReward(int hand, int xpReward){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");
        oneXpReward = xpReward;
    }

    public int getSbReward(int hand){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");
        return oneSbReward;
    }

    public void setSbReward(int hand, int sbReward){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");
        oneSbReward = sbReward;
    }

    public int getJackpot(int hand){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");
        return oneJackpot;
    }

    public void setJackpot(int hand, int jackpot){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");
        oneJackpot = jackpot;
    }

    public Stat[] getIncrementStats(int hand){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");
        return oneIncrementStats;
    }

    public void setIncrementStats(int hand, Stat[] incrementStats){
        if(hand != 1 && hand != 2) throw new IllegalArgumentException("Hand must be either 1 or 2.");
        if(hand == 2 && !isHandSplitted()) throw new IllegalArgumentException("Hand must be 1 on non-splitted hands");
        oneIncrementStats = incrementStats;
    }

    public SplitPlayerHand splitHand(){
        if (isHandSplitted()) throw new IllegalStateException("You cannot split an already splitted hand");
        return new SplitPlayerHand(this);
    }

    public boolean isHandSplitted(){
        return this instanceof SplitPlayerHand;
    }

}
