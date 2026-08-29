package me.VanadeysHaven.Skuddbot.Commands;

import me.VanadeysHaven.Skuddbot.Commands.Managers.Command;
import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSetting;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

/**
 * Command used for viewing server information.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.0
 */
public class ServerInfoCommand extends Command {

    public ServerInfoCommand(){
        super(new String[]{"serverinfo", "sinfo"}, "View the information about the current server.",null, Location.SERVER);
    }

    @Override
    public void run(CommandRequest request) {
        Guild server = request.getGuild();
        EmbedBuilder eb = new EmbedBuilder();
        Member owner = server.getOwner(); assert owner != null;

        eb.setAuthor(server.getName(), null, server.getIconUrl());
        eb.setThumbnail(server.getIconUrl());
        eb.setFooter("Skuddbot " + Main.getSkuddbot().getGlobalSettings().getString(GlobalSetting.VERSION));
        eb.addField("__Server ID:__", server.getId(), false);
        eb.addField("__Owner:__", owner.getUser().getName(), true);
        eb.addField("__Member count:__", server.getMemberCount()+"", true);
        eb.addField("__Role count:__", server.getRoles().size()+"", true);
        eb.addField("__Category count:__", server.getCategories().size()+"", true);
        eb.addField("__Channel count:__", (server.getTextChannels().size() + server.getVoiceChannels().size()) + " (" + server.getTextChannels().size() + " Text / " + server.getVoiceChannels().size() + " Voice)", true);
        TextChannel sys = server.getSystemChannel();
        if(sys != null)
            eb.addField("__System channel:__", sys.getAsMention(), true);
        VoiceChannel afk = server.getAfkChannel();
        if(afk != null){
            eb.addField("__AFK channel:__", afk.getName(), true);
            eb.addField("__AFK timeout:__", server.getAfkTimeout().getSeconds() + " seconds", true);
        }
        eb.addField("__Emoji count:__", server.getEmojis().size()+"", true);
        eb.addField("__Explicit Content Filter:__", server.getExplicitContentLevel().name(), true);
        eb.addField("__Verification Level:__", server.getVerificationLevel().name(), true);
        eb.addField("__Default Notifications:__", server.getDefaultNotificationLevel().name(), false);

        MessagesUtils.sendEmbed(request.getChannel(), eb);
    }

}
