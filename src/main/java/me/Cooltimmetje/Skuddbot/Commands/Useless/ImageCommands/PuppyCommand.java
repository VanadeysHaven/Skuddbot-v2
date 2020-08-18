package me.Cooltimmetje.Skuddbot.Commands.Useless.ImageCommands;

import me.Cooltimmetje.Skuddbot.Donator.DonatorManager;
import me.Cooltimmetje.Skuddbot.Donator.DonatorMessage;

/**
 * Puppy pictures! At random for your enjoyment!
 *
 * @author Tim (Cooltimmetje)
 * @version 2.0
 * @since 2.0
 */
public class PuppyCommand extends ImageCommand {

    private DonatorManager dm = new DonatorManager();

    public PuppyCommand() {
        super(new String[]{"puppy", "emergencypuppy", "wuff", "dogger", "doggo", "dog", "pupper", "riit", "rogged", "woowoo",
                "dogo", "dogggo", "doogo", "dogoo", "owo", "doggerino", "addit", "doggy", "defectius"},
                "Spits out a random puppy picture!", DonatorMessage.Type.PUPPY);
    }

}
