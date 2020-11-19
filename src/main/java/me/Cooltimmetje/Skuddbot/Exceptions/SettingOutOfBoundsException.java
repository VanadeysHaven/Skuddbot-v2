package me.Cooltimmetje.Skuddbot.Exceptions;

/**
 * Exception that can be thrown when a new setting value is out of bounds.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.2.1
 */
public class SettingOutOfBoundsException extends Exception {

    public SettingOutOfBoundsException(String message) {
        super(message);
    }

}
