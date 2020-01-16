package me.Cooltimmetje.Skuddbot.Commands.ImageCommands;

import me.Cooltimmetje.Skuddbot.Donator.DonatorMessage;

/**
 * Cake hype!
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class CakeCommand extends ImageCommand {

    public CakeCommand(){
        super(new String[]{"cake"}, "Spits out a random cake image!", DonatorMessage.Type.CAKE);
    }

}
