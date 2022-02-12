package me.VanadeysHaven.Skuddbot.Utilities.EnvironmentVariables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Class responsible for reading environment variables.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.21
 * @since 2.3.21
 */
public class EnvVarsManager {

    private static final Logger logger = LoggerFactory.getLogger(EnvVarsManager.class);

    private static EnvVarsManager instance;

    public static EnvVarsManager getInstance() {
        if(instance == null)
            instance = new EnvVarsManager();

        return instance;
    }

    private final HashMap<EnvVariable, String> variables;

    private EnvVarsManager(){
        variables = new HashMap<>();
    }

    public void load(String[] overrides){
        variables.clear();
        for(EnvVariable envVariable : EnvVariable.values()) {
            var value = System.getenv(envVariable.toString());
            if(value == null) variables.put(envVariable, envVariable.getDefaultValue());
            else variables.put(envVariable, value);
        }
        for(String string : overrides) {
            var kv = string.split("=");
            try {
                var envVar = EnvVariable.valueOf(kv[0].toUpperCase());
                variables.put(envVar, kv[1]);
            } catch (IllegalArgumentException e) {
                logger.warn("Skipping EnvVar override " + kv[0] + " because it does not exist.");
            }
        }

    }

    public String getEnvVariable(EnvVariable envVariable){
        return variables.get(envVariable);
    }
}
