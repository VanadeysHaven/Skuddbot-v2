package me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Hands;

import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.Card;

/**
 * [class description]
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.2.1
 */
public class DealerHand extends BlackjackHand {

    @Setter private Card holeCard;

    public void revealHoleCard(){
        addCard(ONE, holeCard);
    }

}
