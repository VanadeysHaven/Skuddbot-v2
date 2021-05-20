package me.VanadeysHaven.Skuddbot.Exceptions;

/**
 * A exception that can be thrown whenever we encounter a cooldown.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.2.1
 */
public class CooldownException extends RuntimeException {

    public CooldownException(String message){
        super(message);
    }

}
