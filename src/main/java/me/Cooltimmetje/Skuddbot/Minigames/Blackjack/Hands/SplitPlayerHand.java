package me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands;

import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Card;

import java.util.ArrayList;

/**
 * A hand of Blackjack that has been split.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.2.1
 */
public class SplitPlayerHand extends PlayerHand {

    public static final int TWO = 2;

    protected ArrayList<Card> handTwo;
    protected int betTwo;

    public SplitPlayerHand(PlayerHand hand) {
        super(hand.betOne);
        handTwo = new ArrayList<>();
        ArrayList<Card> initialHand = hand.handOne;
        int bet = hand.betOne;

        Card secondCard = initialHand.get(1);
        initialHand.remove(secondCard);
        handOne = initialHand;

        handTwo.add(secondCard);
        betTwo = bet;
    }

    protected ArrayList<Card> getHand(int hand){
        if(hand == 2)
            return handTwo;

        return super.getHand(hand);
    }

}
