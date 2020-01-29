package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * Settings that apply to the bot globally.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
@Getter
public enum GlobalSetting {

    COMMIT        ("commit",         "Deployed from",  ValueType.STRING, "abc123",                     false),
    WIKI          ("wiki_url",       "Wiki",           ValueType.STRING, "https://wiki.skuddbot.xyz/", false),
    DEPLOY_TIME   ("deploy_time",    "Deployed at",    ValueType.STRING, "4/20",                       true ),
    BUILD_TIME    ("build_time",     "Built at",       ValueType.STRING, "4/20",                       false),
    BRANCH        ("branch",         "Branch",         ValueType.STRING, "master",                     false),
    CURRENT_AVATAR("current_avatar", "Current avatar", ValueType.STRING, "DEFAULT",                    false);

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
