package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.GlobalSettings.GlobalSetting;
import me.Cooltimmetje.Skuddbot.Profiles.GlobalSettings.GlobalSettingsContainer;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Shows information about the bot.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class AboutCommand extends Command {

    public AboutCommand() {
        super(new String[]{"about", "botinfo", "binfo"}, "View information about the bot.", Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        EmbedBuilder eb = new EmbedBuilder();
        GlobalSettingsContainer gsc = Main.getSkuddbot().getGlobalSettings();
        eb.setAuthor("Skuddbot " + gsc.getString(GlobalSetting.VERSION), null, Main.getSkuddbot().getApi().getYourself().getAvatar());
        eb.setThumbnail(Main.getSkuddbot().getApi().getYourself().getAvatar());

        for (GlobalSetting gs : new ArrayList<>(Arrays.asList(GlobalSetting.BUILD_TIME, GlobalSetting.DEPLOY_TIME, GlobalSetting.BRANCH, GlobalSetting.COMMIT, GlobalSetting.WIKI))) {
            eb.addInlineField("__" + gs.getName() + ":__", gsc.getString(gs));
        }

        message.getChannel().sendMessage(eb);
    }
}
