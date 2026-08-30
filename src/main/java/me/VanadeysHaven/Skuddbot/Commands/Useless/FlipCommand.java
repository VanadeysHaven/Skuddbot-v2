package me.VanadeysHaven.Skuddbot.Commands.Useless;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;
import me.VanadeysHaven.Skuddbot.Utilities.UserUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

/**
 * (╯°□°）╯︵ n ou
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class FlipCommand extends Command {

    public FlipCommand(){
        super(new String[]{"flip"}, "(╯°□°）╯︵ n ou", null, Location.BOTH);
    }

    @Override
    public void run(CommandRequest request) {
        Message message = request.getMessage();
        Guild server = request.getGuild();
        String[] args = request.getContent().split(" ");
        if(args.length < 2) {
            MessagesUtils.sendPlain(message.getChannel(), "(╯°□°）╯︵ " + MiscUtils.flipText("WHAT DO YOU WANT TO FLIP?!"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        int currentMention = 0;
        for(int i=1; i < args.length; i++) {
            if(!message.getMentions().getUsers().isEmpty() && currentMention < message.getMentions().getUsers().size()) {
                User user = message.getMentions().getUsers().get(currentMention);
                if (user.getIdLong() == Main.getSkuddbot().getApi().getSelfUser().getIdLong()) user = message.getAuthor();
                assert user != null;
                sb.append("@");
                if (server != null) {
                    sb.append(UserUtils.getDisplayName(server, user));
                } else {
                    sb.append(user.getName());
                }
                sb.append(" ");
                currentMention++;
            } else {
                sb.append(args[i]).append(" ");
            }
        }
        MessagesUtils.sendPlain(message.getChannel(), "(╯°□°）╯︵ " + MiscUtils.flipText(sb.toString().trim()));
    }
}
