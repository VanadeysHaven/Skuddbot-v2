package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Managers.Command;
import me.Cooltimmetje.Skuddbot.Enums.GlobalSetting;
import me.Cooltimmetje.Skuddbot.Main;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

import java.util.Optional;

/**
 * Command used for viewing server information.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ServerInfoCommand extends Command {

    public ServerInfoCommand(){
        super(new String[]{"serverinfo", "sinfo"}, "View the information about the current server.", Location.SERVER);
    }

    @Override
    public void run(Message message, String content) {
        Server server = message.getServer().orElse(null); assert server != null;
        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(server.getName(), null, server.getIcon().orElse(null));
        eb.setThumbnail(server.getIcon().orElse(null));
        eb.setFooter("Skuddbot " + Main.getSkuddbot().getGlobalSettings().getString(GlobalSetting.VERSION));
        eb.addField("__Server ID:__", server.getId()+"");
        eb.addInlineField("__Owner:__", server.getOwner().getDiscriminatedName());
        eb.addInlineField("__Member count:__", server.getMemberCount()+"");
        eb.addInlineField("__Role count:__", server.getRoles().size()+"");
        eb.addInlineField("__Region:__", server.getRegion().getName());
        eb.addInlineField("__Category count:__", server.getChannelCategories().size()+"");
        eb.addInlineField("__Channel count:__", (server.getTextChannels().size() + server.getVoiceChannels().size()) + " (" + server.getTextChannels().size() + " Text / " + server.getVoiceChannels().size() + " Voice)");
        Optional<ServerTextChannel> sysO = server.getSystemChannel();
        sysO.ifPresent(serverTextChannel -> eb.addInlineField("__System channel:__", serverTextChannel.getMentionTag()));
        Optional<ServerVoiceChannel> afkO = server.getAfkChannel();
        if(afkO.isPresent()){
            eb.addInlineField("__AFK channel:__", afkO.get().getName());
            eb.addInlineField("__AFK timeout:__", server.getAfkTimeoutInSeconds() + " seconds");
        }
        eb.addInlineField("__Emoji count:__", server.getCustomEmojis().size()+"");
        eb.addInlineField("__Explicit Content Filter:__", server.getExplicitContentFilterLevel().name());
        eb.addInlineField("__Verification Level:__", server.getVerificationLevel().name());
        eb.addField("__Default Notifications:__", server.getDefaultMessageNotificationLevel().name());

        message.getChannel().sendMessage(eb);
    }

}
