package me.VanadeysHaven.Skuddbot.Utilities.EnvironmentVariables;

import lombok.Getter;

/**
 * Defines the available environment variables.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.21
 * @since 2.3.21
 */
@Getter
public enum EnvVariable {

    MYSQL_USER     ("skuddbot"       ),
    MYSQL_PASSWORD ("password"       ),
    MYSQL_HOST     ("localhost"      ),
    MYSQL_DATABASE ("skuddbot_v2"    ),
    MYSQL_PORT     ("3306"           ),
    DISCORD_TOKEN  ("your_token_here");

    private final String defaultValue;

    EnvVariable(String defaultValue){
        this.defaultValue = defaultValue;
    }

}
