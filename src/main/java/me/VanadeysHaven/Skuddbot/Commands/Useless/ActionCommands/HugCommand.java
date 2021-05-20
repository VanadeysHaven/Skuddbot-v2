package me.VanadeysHaven.Skuddbot.Commands.Useless.ActionCommands;

/**
 * Hug hug hug!!!
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.0
 */
public class HugCommand extends ActionCommand {

    private static final String DEFAULT_ACTION_STRING = "*{0} hugs {1}*";

    public HugCommand() {
        super(new String[]{"hug"}, "Hug all the people, and make them your best friends!");
    }

    @Override
    protected ActionProperties getActionProperties(long userId) {
        if(userId == 148376320726794240L){
            return new ActionProperties("FUCK YOU {1}", true);
        } else if (userId == 76593288865394688L) {
            return new ActionProperties("Task failed successfully.", false);
        } else {
            return new ActionProperties(DEFAULT_ACTION_STRING, false);
        }
    }

}
