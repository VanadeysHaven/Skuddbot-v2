package me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands;

import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Card;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Stats.Stat;

import java.util.ArrayList;

/**
 * A hand of Blackjack that has been split.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.2.1
 */
public class SplitPlayerHand extends PlayerHand {

    protected ArrayList<Card> twoHand;
    protected int twoBet;
    protected boolean twoIsDoubled;
    protected String twoRewardString;
    protected int twoXpReward;
    protected int twoSbReward;
    protected int twoJackpot;
    protected Stat[] twoIncrementStats;


    public SplitPlayerHand(PlayerHand hand, boolean useGenderNeutralCards) {
        super(hand.oneBet, useGenderNeutralCards);
        twoHand = new ArrayList<>();
        ArrayList<Card> initialHand = hand.oneHand;
        int bet = hand.oneBet;

        Card secondCard = initialHand.get(1);
        initialHand.remove(secondCard);
        oneHand = initialHand;

        twoHand.add(secondCard);
        twoBet = bet;
    }

    @Override
    protected ArrayList<Card> getHand(int hand){
        if(hand == 2)
            return twoHand;

        return super.getHand(hand);
    }

    @Override
    public int getBet(int hand) {
        if(hand == 2)
            return twoBet;

        return super.getBet(hand);
    }

    @Override
    public void doubleDown(int hand, Card card) {
        if(hand == 2) {
            twoBet *= 2;
            twoIsDoubled = true;
            addCard(2, card);
            return;
        }

        super.doubleDown(hand, card);
    }

    @Override
    public boolean isDoubled(int hand) {
        if(hand == 2)
            return twoIsDoubled;

        return super.isDoubled(hand);
    }

    @Override
    public String getRewardString(int hand) {
        if(hand == 2)
            return twoRewardString;

        return super.getRewardString(hand);
    }

    @Override
    public void setRewardString(int hand, String rewardString) {
        if(hand == 2) {
            twoRewardString = rewardString;
            return;
        }

        super.setRewardString(hand, rewardString);
    }

    @Override
    public int getXpReward(int hand) {
        if(hand == 2)
            return twoXpReward;

        return super.getXpReward(hand);
    }

    @Override
    public void setXpReward(int hand, int xpReward) {
        if(hand == 2) {
            twoXpReward = xpReward;
            return;
        }

        super.setXpReward(hand, xpReward);
    }

    @Override
    public int getSbReward(int hand) {
        if(hand == 2)
            return twoSbReward;

        return super.getSbReward(hand);
    }

    @Override
    public void setSbReward(int hand, int sbReward) {
        if(hand == 2) {
            twoSbReward = sbReward;
            return;
        }


        super.setSbReward(hand, sbReward);
    }

    @Override
    public int getJackpot(int hand) {
        if(hand == 2)
            return twoJackpot;

        return super.getJackpot(hand);
    }

    @Override
    public void setJackpot(int hand, int jackpot) {
        if(hand == 2){
            twoJackpot = jackpot;
        }
        super.setJackpot(hand, jackpot);
    }

    @Override
    public Stat[] getIncrementStats(int hand) {
        if(hand == 2)
            return twoIncrementStats;

        return super.getIncrementStats(hand);
    }

    @Override
    public void setIncrementStats(int hand, Stat[] incrementStats) {
        if(hand == 2) {
            twoIncrementStats = incrementStats;
            return;
        }

        super.setIncrementStats(hand, incrementStats);
    }

    @Override
    public boolean isSplitAllowed() {
        return false;
    }

}
