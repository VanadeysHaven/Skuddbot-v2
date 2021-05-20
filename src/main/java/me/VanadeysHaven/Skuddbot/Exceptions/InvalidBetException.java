package me.VanadeysHaven.Skuddbot.Exceptions;

/**
 * Gets thrown whenever the Cashier class encounters an invalid formatted bet.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3
 * @since 2.3
 */
public class InvalidBetException extends Exception {

    public InvalidBetException(String attemptedBet){
        super("`" +  attemptedBet + "` is not a valid bet. See <https://wiki.skuddbot.xyz/minigames/betting-shortcuts> for allowed bets.");
    }


}
