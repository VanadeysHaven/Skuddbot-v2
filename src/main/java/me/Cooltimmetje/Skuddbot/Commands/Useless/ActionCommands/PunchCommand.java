package me.Cooltimmetje.Skuddbot.Commands.Useless.ActionCommands;

/**
 * Punch punch punch!!!
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
 * @since 2.0
 */
public class PunchCommand extends ActionCommand {

    private static final String DEFAULT_ACTION_STRING = "*{0} punches {1}*";

    public PunchCommand() {
        super(new String[]{"punch"}, "I don't like this person, punch em!");
    }

    @Override
    protected ActionProperties getActionProperties(long userId) {
        if(userId == 148376320726794240L){
            return new ActionProperties("*{0} lights {1} on fire* :fire:", false);
        } else if (userId == 131382094457733120L) {
            return new ActionProperties("*{0} unleashes his lightning punch on {1}* :zap:", false);
        } else if (userId == 76593288865394688L) {
            return new ActionProperties("*{0} banhammers {1}* :hammer:", false);
        } else {
            return new ActionProperties(DEFAULT_ACTION_STRING, false);
        }
    }

}
