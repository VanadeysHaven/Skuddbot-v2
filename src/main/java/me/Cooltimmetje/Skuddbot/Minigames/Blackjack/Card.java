package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;

/**
 * Represents a card in blackjack.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.0
 * @since 2.0
 */
public class Card {

    @Getter
    public enum Rank {

        ACE  (-1, "Ace",   Emoji.A    ),
        TWO  (2,  "Two",   Emoji.TWO  ),
        THREE(3,  "Three", Emoji.THREE),
        FOUR (4,  "Four",  Emoji.FOUR ),
        FIVE (5,  "Five",  Emoji.FIVE ),
        SIX  (6,  "Six",   Emoji.SIX  ),
        SEVEN(7,  "Seven", Emoji.SEVEN),
        EIGHT(8,  "Eight", Emoji.EIGHT),
        NINE (9,  "Nine",  Emoji.NINE ),
        TEN  (10, "Ten",   Emoji.TEN  ),
        JACK (10, "Jack",  Emoji.J    ),
        QUEEN(10, "Queen", Emoji.Q    ),
        KING (10, "King",  Emoji.K    );

        private int value;
        private String rankName;
        private Emoji emoji;

        Rank(int value, String rankName, Emoji emoji){
            this.value = value;
            this.rankName = rankName;
            this.emoji = emoji;
        }

        public static Rank random(RNGManager random){
            return values()[random.integer(0, values().length - 1)];
        }

    }

    @Getter
    private enum Suit {

        SPADES  ("Spades",   Emoji.SPADES  ),
        CLUBS   ("Clubs",    Emoji.CLUBS   ),
        DIAMONDS("Diamonds", Emoji.DIAMONDS),
        HEARTS  ("Hearts",   Emoji.HEARTS  );

        private String suitName;
        private Emoji emoji;

        Suit(String suitName, Emoji emoji){
            this.suitName = suitName;
            this.emoji = emoji;
        }

        public static Suit random(RNGManager random){
            return values()[random.integer(0, values().length - 1)];
        }

    }

    @Getter private Rank rank;
    @Getter private Suit suit;

    public Card(Rank rank, Suit suit){
        this.rank = rank;
        this.suit = suit;
    }

    public Card(RNGManager random){
        this(Rank.random(random), Suit.random(random));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return rank == card.rank &&
                suit == card.suit;
    }

    @Override
    public String toString() {
        return rank.getEmoji().getUnicode() + " " + suit.getEmoji().getUnicode();
    }
}
