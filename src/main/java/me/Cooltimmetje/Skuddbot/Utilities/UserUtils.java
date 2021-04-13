package me.Cooltimmetje.Skuddbot.Utilities;

import me.Cooltimmetje.Skuddbot.Main;
import org.javacord.api.entity.user.User;

import java.util.concurrent.CompletionException;

/**
 * Utilities to do with Users.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.2.1
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
            User user = Main.getSkuddbot().getApi().getUserById(userId).join();
            return true;
        } catch (CompletionException e){
            return false;
        }
    }

    public boolean doesUserExist(String userId){
        return doesUserExist(Long.parseLong(userId));
    }

    public User getUser(long userId){
        return Main.getSkuddbot().getApi().getUserById(userId).join();
    }

    public User getUser(String userId){
        return getUser(Long.parseLong(userId));
    }

}
