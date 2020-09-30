package me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands;

import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Card;

/**
 * Represents the hand of the dealer.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.2.1
 */
public class DealerHand extends BlackjackHand {

    @Setter private Card holeCard;

    public void revealHoleCard(){
        if(holeCard == null) throw new IllegalStateException("There is no hole card.");

        Card card = holeCard;
        holeCard = null;
        addCard(ONE, card);
    }

    public boolean isHoleCardRevealed(){
        return holeCard == null;
    }

}
