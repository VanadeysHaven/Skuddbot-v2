package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.Emoji;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;

/**
 * Represents a card in blackjack.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.0
 */
public class Card {

    @Getter
    public enum Rank {

        ACE  (-1, "Ace",   Emoji.A,              "A",  false),
        TWO  (2,  "Two",   Emoji.TWO,            "2",  false),
        THREE(3,  "Three", Emoji.THREE,          "3",  false),
        FOUR (4,  "Four",  Emoji.FOUR,           "4",  false),
        FIVE (5,  "Five",  Emoji.FIVE,           "5",  false),
        SIX  (6,  "Six",   Emoji.SIX,            "6",  false),
        SEVEN(7,  "Seven", Emoji.SEVEN,          "7",  false),
        EIGHT(8,  "Eight", Emoji.EIGHT,          "8",  false),
        NINE (9,  "Nine",  Emoji.NINE,           "9",  false),
        TEN  (10, "Ten",   Emoji.TEN,            "10", false),
        JACK (10, "Jack",  Emoji.J,     Emoji.B, "J",  true ),
        QUEEN(10, "Queen", Emoji.Q,     Emoji.S, "Q",  true ),
        KING (10, "King",  Emoji.K,     Emoji.G, "K",  true );

        private int value;
        private String rankName;
        private Emoji normalEmoji;
        private Emoji gnEmoji;
        private String abbreviation;
        private boolean faceCard;

        Rank(int value, String rankName, Emoji emoji, String abbreviation, boolean faceCard){
            this.value = value;
            this.rankName = rankName;
            this.normalEmoji = emoji;
            this.abbreviation = abbreviation;
            this.faceCard = faceCard;
        }

        Rank(int value, String rankName, Emoji emoji, Emoji gnEmoji, String abbreviation, boolean faceCard){
            this.value = value;
            this.rankName = rankName;
            this.normalEmoji = emoji;
            this.gnEmoji = gnEmoji;
            this.abbreviation = abbreviation;
            this.faceCard = faceCard;
        }

        public Emoji getEmoji(boolean genderNeutral){
            if(genderNeutral)
                return getGnEmoji();

            return getNormalEmoji();
        }

        public Emoji getGnEmoji(){
            if(gnEmoji == null)
                return getNormalEmoji();

            return gnEmoji;
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

    public String toString(boolean genderNeutral) {
        return rank.getEmoji(genderNeutral).getUnicode() + " " + suit.getEmoji().getUnicode();
    }

    @Override
    public String toString(){
        return toString(true);
    }

    public String toSimpleString() {
        return rank.getAbbreviation() + suit.getAbbreviation();
    }

}
