package me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Card;

import java.util.ArrayList;

/**
 * Represents a hand in Blackjack.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.0
 */
public abstract class BlackjackHand {

    public static final int ONE = 1;

    protected ArrayList<Card> handOne;

    protected BlackjackHand(){
        handOne = new ArrayList<>();
    }

    public int getHandValue(int hand){
        int aces = 0;
        int value = 0;
        for(Card card : getHand(hand)){
            if(card.getRank() == Card.Rank.ACE)
                aces++;
            else
                value += card.getRank().getValue();
        }

        while(aces > 0){
            value += 11;
            if(value > 21)
                value -= 10;

            aces--;
        }

        return value;
    }

    public void addCard(int hand, Card card){
        getHand(hand).add(card);
    }

    public boolean isBlackjack(int hand){
        ArrayList<Card> handList = getHand(hand);
        if(handList.size() != 2) return false;
        Card cardOne = handList.get(0);
        Card cardTwo = handList.get(1);

        if(cardOne.getRank() != Card.Rank.ACE && cardOne.getRank().getValue() != 10) return false;
        if(cardTwo.getRank() != Card.Rank.ACE && cardTwo.getRank().getValue() != 10) return false;

        return cardOne.getRank() != cardTwo.getRank();
    }

    public String formatHand(int hand){
        ArrayList<Card> handList = getHand(hand);
        StringBuilder sb = new StringBuilder();
        for(Card card : handList)
            sb.append(" | ").append(card.toString());

        if(handList.size() == 1)
            sb.append(" | ").append(Emoji.QUESTION.getUnicode()).append(Emoji.QUESTION.getUnicode());

        return sb.substring(3);
    }

    protected ArrayList<Card> getHand(int hand){
        if(hand == 1)
            return handOne;

        return null;
    }

}
