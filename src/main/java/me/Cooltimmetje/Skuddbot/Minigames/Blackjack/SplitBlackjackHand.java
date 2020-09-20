package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import java.util.ArrayList;

/**
 * A hand of Blackjack that has been split.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.2.1
 */
public class SplitBlackjackHand extends BlackjackHand {

    public static final int TWO = 2;

    protected ArrayList<Card> handTwo;
    protected int betTwo;

    public SplitBlackjackHand(BlackjackHand hand) {
        handTwo = new ArrayList<>();
        ArrayList<Card> initialHand = hand.handOne;
        int bet = hand.betOne;

        Card secondCard = initialHand.get(1);
        initialHand.remove(secondCard);
        handOne = initialHand;
        betOne = bet;

        handTwo.add(secondCard);
        betTwo = bet;
    }

    protected ArrayList<Card> getHand(int hand){
        if(hand == 2)
            return handTwo;

        return super.getHand(hand);
    }

}
