package me.Cooltimmetje.Skuddbot.Commands.Useless.ImageCommands;

import me.Cooltimmetje.Skuddbot.Donator.DonatorMessage;

/**
 * Cats... I hate cats.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.0
 * @since 2.0
 */
public class KittyCommand extends ImageCommand {

    public KittyCommand(){
        super(new String[] {"kitty", "cat", "pussy", "kitten"}, "Spits out a random kitty image!", DonatorMessage.Type.KITTY);
    }

}
