package me.VanadeysHaven.Skuddbot.Profiles.GlobalSettings;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Enums.ValueType;
import me.VanadeysHaven.Skuddbot.Utilities.EnvironmentVariables.EnvVariable;

/**
 * Settings that apply to the bot globally.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.21
 * @since 2.0
 */
@Getter
public enum GlobalSetting {

    COMMIT         ("commit",          "Deployed from",   ValueType.STRING,  "abc123",                     false, EnvVariable.COMMIT     ),
    WIKI           ("wiki_url",        "Wiki",            ValueType.STRING,  "https://wiki.skuddbot.xyz/", false                         ),
    DEPLOY_TIME    ("deploy_time",     "Deployed at",     ValueType.STRING,  "02/02/2020 20:02 (GMT)",     true,  EnvVariable.DEPLOY_TIME),
    BUILD_TIME     ("build_time",      "Built at",        ValueType.STRING,  "02/02/2020 20:02 (GMT)",     true                          ),
    BRANCH         ("branch",          "Branch",          ValueType.STRING,  "master",                     false, EnvVariable.BRANCH     ),
    CURRENT_AVATAR ("current_avatar",  "Current avatar",  ValueType.STRING,  "DEFAULT",                    false                         ),
    VERSION        ("version",         "Version",         ValueType.STRING,  "2.0",                        true,  EnvVariable.VERSION    ),
    SALUTE_COOLDOWN("salute_cooldown", "Salute Cooldown", ValueType.BOOLEAN, "false",                      false                         );

    private final String dbReference;
    private final String name;
    private final ValueType type;
    private final String defaultValue;
    private final boolean allowSpaces;
    private final EnvVariable linkedEnvVariable;

    GlobalSetting(String dbReference, String name, ValueType type, String defaultValue, boolean allowSpaces){
        this(dbReference, name, type, defaultValue, allowSpaces, null);
    }

    GlobalSetting(String dbReference, String name, ValueType type, String defaultValue, boolean allowSpaces, EnvVariable linkedEnvVariable){
        this.dbReference = dbReference;
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.allowSpaces = allowSpaces;
        this.linkedEnvVariable = linkedEnvVariable;
    }

    public boolean hasLinkedEnvVariable(){
        return this.linkedEnvVariable != null;
    }

    public static GlobalSetting getByDbReference(String reference){
        for(GlobalSetting setting : values())
            if(setting.getDbReference().equals(reference))
                return setting;

        return null;
    }


}
