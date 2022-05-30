package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSetting;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSettingsContainer;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Shows information about the bot.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.21
 * @since 2.0
 */
public class AboutCommand extends Command {

    public AboutCommand() {
        super(new String[]{"about", "botinfo", "binfo"}, "View information about the bot.", null, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        EmbedBuilder eb = new EmbedBuilder();
        GlobalSettingsContainer gsc = Main.getSkuddbot().getGlobalSettings();
        eb.setAuthor("Skuddbot " + gsc.getString(GlobalSetting.VERSION), null, Main.getSkuddbot().getApi().getYourself().getAvatar());
        eb.setThumbnail(Main.getSkuddbot().getApi().getYourself().getAvatar());

        for (GlobalSetting gs : new ArrayList<>(Arrays.asList(GlobalSetting.DEPLOY_TIME, GlobalSetting.BRANCH, GlobalSetting.COMMIT, GlobalSetting.WIKI)))
            eb.addInlineField("__" + gs.getName() + ":__", gsc.getString(gs));

        message.getChannel().sendMessage(eb);
    }
}
