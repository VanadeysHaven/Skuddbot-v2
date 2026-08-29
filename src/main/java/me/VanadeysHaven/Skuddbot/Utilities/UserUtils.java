package me.VanadeysHaven.Skuddbot.Utilities;

import me.VanadeysHaven.Skuddbot.Main;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

/**
 * Utilities to do with Users.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.4
 * @since 2.2.1
 */
public class UserUtils {

    private static UserUtils instance;

    public static UserUtils getInstance(){
        if(instance == null)
            instance = new UserUtils();

        return instance;
    }

    private UserUtils(){

    }

    public boolean doesUserExist(long userId){
        try {
            Main.getSkuddbot().getApi().retrieveUserById(userId).complete();
            return true;
        } catch (ErrorResponseException e){
            return false;
        }
    }

    public boolean doesUserExist(String userId){
        return doesUserExist(Long.parseLong(userId));
    }

    public User getUser(long userId){
        return Main.getSkuddbot().getApi().retrieveUserById(userId).complete();
    }

    public User getUser(String userId){
        return getUser(Long.parseLong(userId));
    }

}
