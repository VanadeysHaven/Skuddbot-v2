package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Stack;

/**
 * Represents a deck of cards.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.2.1
 */
public class CardStack {

    private static final int AMOUNT_IN_DECK = 52;
    private static final Logger logger = LoggerFactory.getLogger(CardStack.class);
    private int deckAmount;
    private RNGManager rng;
    private DeckShuffler shuffler;

    private Stack<Card> stack;

    public CardStack(int deckAmount, RNGManager rng){
        shuffler = new DeckShuffler();
        this.deckAmount = deckAmount;
        this.rng = rng;
    }

    public synchronized Card nextCard(){
        if(stack == null || stack.isEmpty())
            reshuffle();

        return stack.pop();
    }

    public int getCardsRemaining(){
        return stack.size();
    }

    private void reshuffle(){
        stack = shuffler.get();
    }

    private class DeckShuffler {

        private Stack<Card> generatedStack;

        private DeckShuffler() {}

        private void generate() {
            generatedStack = new Stack<>();
            int finalAmount = deckAmount * AMOUNT_IN_DECK;
            logger.info("Generating new card deck with" + deckAmount + "decks and final amount of " + finalAmount + "card...");
            for (Card.Rank rank : Card.Rank.values())
                for (Card.Suit suit : Card.Suit.values()) {
                    Card card = new Card(rank, suit);
                    for (int i = 0; i < deckAmount; i++) {
                        generatedStack.push(card);
                        logger.info("Added " + card.toSimpleString() + " to stack. (" + (i + 1) + "/" + deckAmount + " - " + (finalAmount - generatedStack.size()) + " remaining).");
                    }
                }
        }

        private synchronized Stack<Card> get(){
            generate();
            Collections.shuffle(generatedStack);
            return generatedStack;
        }

    }

}

