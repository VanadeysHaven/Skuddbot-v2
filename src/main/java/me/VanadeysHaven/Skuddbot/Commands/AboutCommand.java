package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSetting;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSettingsContainer;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Shows information about the bot.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class AboutCommand extends Command {

    public AboutCommand() {
        super(new String[]{"about", "botinfo", "binfo"}, "View information about the bot.", null, Location.BOTH);
    }

    @Override
    public void run(CommandRequest request) {
        EmbedBuilder eb = new EmbedBuilder();
        GlobalSettingsContainer gsc = Main.getSkuddbot().getGlobalSettings();
        eb.setAuthor("Skuddbot " + gsc.getString(GlobalSetting.VERSION), null, Main.getSkuddbot().getApi().getSelfUser().getEffectiveAvatarUrl());
        eb.setThumbnail(Main.getSkuddbot().getApi().getSelfUser().getEffectiveAvatarUrl());

        for (GlobalSetting gs : new ArrayList<>(Arrays.asList(GlobalSetting.DEPLOY_TIME, GlobalSetting.BRANCH, GlobalSetting.COMMIT, GlobalSetting.WIKI)))
            eb.addField("__" + gs.getName() + ":__", gsc.getString(gs), true);

        MessagesUtils.sendEmbed(request.getChannel(), eb);
    }
}
