package me.Cooltimmetje.Skuddbot.Minigames.Blackjack;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import org.javacord.api.entity.message.Message;

/**
 * Command used for invoking the blackjack game.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.2.1
 * @since 2.0
 */
public class BlackjackCommand extends Command {

    public BlackjackCommand() {
        super(new String[]{"blackjack", "bj", "21", "deal"}, "Play a game of blackjack against the dealer.", Location.SERVER);
    }

    @Override
    public void run(Message message, String content) {

    }

}
