package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;

/**
 * Represents a card in blackjack.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.0
 */
public class Card {

    @Getter
    public enum Rank {

        ACE  (-1, "Ace",   Emoji.A,     "A" ),
        TWO  (2,  "Two",   Emoji.TWO,   "2" ),
        THREE(3,  "Three", Emoji.THREE, "3" ),
        FOUR (4,  "Four",  Emoji.FOUR,  "4" ),
        FIVE (5,  "Five",  Emoji.FIVE,  "5" ),
        SIX  (6,  "Six",   Emoji.SIX,   "6" ),
        SEVEN(7,  "Seven", Emoji.SEVEN, "7" ),
        EIGHT(8,  "Eight", Emoji.EIGHT, "8" ),
        NINE (9,  "Nine",  Emoji.NINE,  "9" ),
        TEN  (10, "Ten",   Emoji.TEN,   "10"),
        JACK (10, "Jack",  Emoji.J,     "J" ),
        QUEEN(10, "Queen", Emoji.Q,     "Q" ),
        KING (10, "King",  Emoji.K,     "K" );

        private int value;
        private String rankName;
        private Emoji emoji;
        private String abbreviation;

        Rank(int value, String rankName, Emoji emoji, String abbreviation){
            this.value = value;
            this.rankName = rankName;
            this.emoji = emoji;
            this.abbreviation = abbreviation;
        }

        public static Rank random(RNGManager random){
            return values()[random.integer(0, values().length - 1)];
        }


    }

    @Getter
    public enum Suit {

        SPADES  ("Spades",   Emoji.SPADES,   "S"),
        CLUBS   ("Clubs",    Emoji.CLUBS,    "C"),
        DIAMONDS("Diamonds", Emoji.DIAMONDS, "D"),
        HEARTS  ("Hearts",   Emoji.HEARTS,   "H");

        private String suitName;
        private Emoji emoji;
        private String abbreviation;

        Suit(String suitName, Emoji emoji, String abbreviation){
            this.suitName = suitName;
            this.emoji = emoji;
            this.abbreviation = abbreviation;
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

    public String toSimpleString() {
        return rank.getAbbreviation() + suit.getAbbreviation();
    }

}
