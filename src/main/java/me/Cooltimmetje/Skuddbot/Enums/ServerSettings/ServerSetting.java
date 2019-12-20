package me.Cooltimmetje.Skuddbot.Enums.ServerSettings;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;

/**
 * Settings for servers.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
@Getter
public enum ServerSetting {

    XP_MIN("xp_min", "Minimum amount of XP gain per message on Discord.", ValueType.INTEGER, "10", ServerSettingCategory.XP),
    XP_MAX("xp_max", "Maximum amount of XP gain per message on Discord.", ValueType.INTEGER, "15", ServerSettingCategory.XP),
    XP_MIN_TWITCH("xp_min_twitch", "Minimum amount of XP gain per message on Twitch.", ValueType.INTEGER, "10", ServerSettingCategory.XP),
    XP_MAX_TWITCH("xp_max_twitch", "Maximum amount of XP gain per message on Twitch.", ValueType.INTEGER, "15", ServerSettingCategory.XP),
    XP_BASE("xp_base", "Amount of XP an user will need to level up from level 1 to level 2.", ValueType.INTEGER, "1500", ServerSettingCategory.XP),
    XP_MULTIPLIER("xp_multiplier", "Multiplier for XP levels. The higher this number, the steeper the XP curve.", ValueType.DOUBLE, "1.2", ServerSettingCategory.XP),
    TWITCH_CHANNEL("twitch_channel", "The Twitch channel this bot should keep track of stats.", ValueType.STRING, "null", ServerSettingCategory.TWITCH);

    private String dbReference;
    private String description;
    private ValueType type;
    private String defaultValue;
    private ServerSettingCategory category;

    ServerSetting(String dbReference, String description, ValueType type, String defaultValue, ServerSettingCategory category){
        this.dbReference = dbReference;
        this.description = description;
        this.type = type;
        this.defaultValue = defaultValue;
        this.category = category;
    }

}
