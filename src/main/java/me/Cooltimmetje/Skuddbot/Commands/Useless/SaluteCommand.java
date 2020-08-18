package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Commands.Managers.NoPrefixCommand;
import me.Cooltimmetje.Skuddbot.Enums.PermissionLevel;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.GlobalSettings.GlobalSetting;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.RNGManager;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * o7 CMDR
 *
 * @author Tim (Cooltimmetje)
 * @version 2.0
 * @since 2.0
 */
public class SaluteCommand extends NoPrefixCommand {

    private static final int COOLDOWN = 15;
    private HashMap<Long, CooldownManager> cooldowns = new HashMap<>();
    private static RNGManager random = new RNGManager();

    public SaluteCommand() {
        super(new String[]{"o7"}, "o7 CMDR", PermissionLevel.DEFAULT, Location.BOTH);
    }

    @Override
    public void run(Message message, String content) {
        Server server = message.getServer().orElse(null);
        long serverId = -1;
        if (server == null) {
            if(isOnCooldown(serverId, message.getAuthor().getId())) return;
            User user = message.getUserAuthor().orElse(null); assert user != null;
            MessagesUtils.sendPlain(user.getPrivateChannel().orElse(user.openPrivateChannel().join()), "o7");
            startCooldown(serverId, user.getId());
            return;
        }
        serverId = server.getId();

        if(isOnCooldown(serverId, message.getAuthor().getId())) return;
        ArrayList<KnownCustomEmoji> emojis = new ArrayList<>(server.getCustomEmojis());
        if(emojis.isEmpty()){
            MessagesUtils.sendPlain(message.getChannel(), "o7");
            startCooldown(serverId, message.getAuthor().getId());
            return;
        }
        KnownCustomEmoji emoji;
        do {
            emoji = emojis.get(random.integer(0, emojis.size() - 1));
        } while (emoji.isManaged());

        MessagesUtils.sendPlain(message.getChannel(), emoji.getMentionTag() + "7");
        startCooldown(serverId, message.getAuthor().getId());
    }

    private void startCooldown(long serverId, long userId){
        createCooldownManager(serverId);

        CooldownManager cm = cooldowns.get(serverId);
        cm.startCooldown(userId);
    }

    private boolean isOnCooldown(long serverId, long userId){
        createCooldownManager(serverId);

        CooldownManager cm = cooldowns.get(serverId);
        return cm.isOnCooldown(userId) && Main.getSkuddbot().getGlobalSettings().getBoolean(GlobalSetting.SALUTE_COOLDOWN);
    }

    private void createCooldownManager(long serverId){
        if(!cooldowns.containsKey(serverId)) {
            cooldowns.put(serverId, new CooldownManager(COOLDOWN));
        }
    }

}
