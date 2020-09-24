package me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands;

import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Card;

import java.util.ArrayList;

/**
 * Represents a hand of a player.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.2.1
 */
public class PlayerHand extends BlackjackHand {

    protected int betOne;

    public PlayerHand(int bet){
        betOne = bet;
    }

    public boolean isDoubleDownAllowed(int hand){
        ArrayList<Card> handList = getHand(hand);
        if(handList.size() > 2)  return false;

        return getHandValue(hand) <= 10;
    }

    public boolean isSplitAllowed(){
        if(this instanceof SplitPlayerHand) return false;
        ArrayList<Card> handList = getHand(ONE);
        Card cardOne = handList.get(0);
        Card cardTwo = handList.get(1);

        return cardOne.getSuit() == cardTwo.getSuit();
    }

    public SplitPlayerHand splitHand(){
        if (this instanceof SplitPlayerHand) throw new IllegalStateException("You cannot split an already splitted hand");
        return new SplitPlayerHand(this);
    }

}
