package me.Cooltimmetje.Skuddbot.Profiles.GlobalSettings;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;

/**
 * Settings that apply to the bot globally.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.0
 * @since 2.0
 */
@Getter
public enum GlobalSetting {

    COMMIT         ("commit",          "Deployed from",   ValueType.STRING,  "abc123",                     false),
    WIKI           ("wiki_url",        "Wiki",            ValueType.STRING,  "https://wiki.skuddbot.xyz/", false),
    DEPLOY_TIME    ("deploy_time",     "Deployed at",     ValueType.STRING,  "02/02/2020 20:02 (GMT)",     true ),
    BUILD_TIME     ("build_time",      "Built at",        ValueType.STRING,  "02/02/2020 20:02 (GMT)",     true ),
    BRANCH         ("branch",          "Branch",          ValueType.STRING,  "master",                     false),
    CURRENT_AVATAR ("current_avatar",  "Current avatar",  ValueType.STRING,  "DEFAULT",                    false),
    VERSION        ("version",         "Version",         ValueType.STRING,  "2.0",                        false),
    SALUTE_COOLDOWN("salute_cooldown", "Salute Cooldown", ValueType.BOOLEAN, "false",                      false);

    private String dbReference;
    private String name;
    private ValueType type;
    private String defaultValue;
    private boolean allowSpaces;

    GlobalSetting(String dbReference, String name, ValueType type, String defaultValue, boolean allowSpaces){
        this.dbReference = dbReference;
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.allowSpaces = allowSpaces;
    }

    public static GlobalSetting getByDbReference(String reference){
        for(GlobalSetting setting : values())
            if(setting.getDbReference().equals(reference))
                return setting;

        return null;
    }


}
