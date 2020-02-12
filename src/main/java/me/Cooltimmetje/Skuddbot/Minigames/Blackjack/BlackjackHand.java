package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import me.Cooltimmetje.Skuddbot.Enums.Emoji;

import java.util.ArrayList;

/**
 * Represents a hand in Blackjack.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class BlackjackHand {

    private ArrayList<Card> cards;

    public BlackjackHand(){
        cards = new ArrayList<>();
    }

    public void addCard(Card card){
        cards.add(card);
    }

    public int getHandValue(){
        int value = 0;
        int aces = 0;
        for(Card card : cards){
            if(card.getRank() == Card.Rank.ACE) aces++;
            else value += card.getRank().getValue();
        }

        for(int i=0; i < aces; i++){
            if((value + 11) <= 21) value += 11;
            else value++;
        }

        return value;
    }

    public boolean containsCard(Card card){
        for(Card handCard : cards)
            if(handCard.equals(card)) return true;

        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Card card : cards)
            sb.append(" | ").append(card.toString());

        if(cards.size() == 1)
            sb.append(" | ").append(Emoji.QUESTION.getUnicode()).append(Emoji.QUESTION.getUnicode());

        return sb.toString().substring(3);
    }
}
