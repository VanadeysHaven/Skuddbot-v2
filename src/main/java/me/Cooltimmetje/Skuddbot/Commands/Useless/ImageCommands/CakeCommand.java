package me.Cooltimmetje.Skuddbot.Commands.Useless.ImageCommands;

import me.Cooltimmetje.Skuddbot.Donator.DonatorMessage;

/**
 * Cake hype!
 *
 * @author Tim (Cooltimmetje)
 * @version 2.0
 * @since 2.0
 */
public class CakeCommand extends ImageCommand {

    public CakeCommand(){
        super(new String[]{"cake"}, "Spits out a random cake image!", DonatorMessage.Type.CAKE);
    }

}
