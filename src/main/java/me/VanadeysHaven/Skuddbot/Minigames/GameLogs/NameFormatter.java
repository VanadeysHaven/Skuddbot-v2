package me.VanadeysHaven.Skuddbot.Minigames.GameLogs;

import me.VanadeysHaven.Skuddbot.Profiles.ProfileManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.Settings.UserSetting;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Class responsible for filling in usernames in the game logs, will also anonymize any usernames of users that have chosen to have their name anonymized.
 * When adding names to game logs, they should be in the format $<server_id>-<user_id>
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.1
 * @since 2.3.1
 */
public final class NameFormatter {

    private static final ProfileManager pm = ProfileManager.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(NameFormatter.class);

    private int anonymousNumber;
    private HashMap<String,String> cachedNames;

    public NameFormatter(){
        anonymousNumber = 0;
        cachedNames = new HashMap<>();
    }

    public String format(String input){
        String[] lines = input.split("\n");
        for(String line : lines) for(String word : line.split(" ")) if (word.startsWith("$")) input = input.replace(word, formatName(word.substring(1)));

        return input;
    }

    private String formatName(String idString){
        try {
            String[] ids = idString.split("-");
            long serverId = Long.parseLong(ids[0]);
            long userId = Long.parseLong(ids[1]);
            SkuddUser su = pm.getUser(serverId, userId);

            if(su.getSettings().getBoolean(UserSetting.ANON_GAME_LOG)){
                anonymousNumber++;
                return "AnAnonymousUser" + anonymousNumber;
            } else {
                return su.asMember().getDisplayName();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return idString;
        }
    }

}
