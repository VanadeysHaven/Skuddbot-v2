package me.VanadeysHaven.Skuddbot.Commands.Useless;

import me.VanadeysHaven.Skuddbot.Commands.Managers.CommandRequest;
import me.VanadeysHaven.Skuddbot.Commands.Managers.NoPrefixCommand;
import me.VanadeysHaven.Skuddbot.Enums.PermissionLevel;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings.GlobalSetting;
import me.VanadeysHaven.Skuddbot.Utilities.CooldownManager;
import me.VanadeysHaven.Skuddbot.Utilities.MessagesUtils;
import me.VanadeysHaven.Skuddbot.Utilities.RNGManager;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * o7 CMDR
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.23
 * @since 2.0
 */
public class SaluteCommand extends NoPrefixCommand {

    private static final int COOLDOWN = 15;
    private final HashMap<Long, CooldownManager> cooldowns = new HashMap<>();
    private final static RNGManager random = new RNGManager();

    public SaluteCommand() {
        super(new String[]{"o7"}, "o7 CMDR", null, PermissionLevel.DEFAULT, Location.BOTH);
    }

    @Override
    public void run(CommandRequest request) {
        Server server = request.getServer();
        long serverId = -1;
        if (server == null) {
            if(isOnCooldown(serverId, request.getSender().getId())) return;
            User user = request.getUser();
            MessagesUtils.sendPlain(user.getPrivateChannel().orElse(user.openPrivateChannel().join()), "o7");
            startCooldown(serverId, user.getId());
            return;
        }
        serverId = server.getId();

        if(isOnCooldown(serverId, request.getSender().getId())) return;
        ArrayList<KnownCustomEmoji> emojis = new ArrayList<>(server.getCustomEmojis());
        if(emojis.isEmpty()){
            MessagesUtils.sendPlain(request.getChannel(), "o7");
            startCooldown(serverId, request.getSender().getId());
            return;
        }
        KnownCustomEmoji emoji;
        do {
            emoji = emojis.get(random.integer(0, emojis.size() - 1));
        } while (emoji.isManaged());

        MessagesUtils.sendPlain(request.getChannel(), emoji.getMentionTag() + "7");
        startCooldown(serverId, request.getSender().getId());
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
