package me.VanadeysHaven.Skuddbot.Profiles.Server;

import me.VanadeysHaven.Skuddbot.Profiles.Pages.Page;
import me.VanadeysHaven.Skuddbot.Profiles.Pages.PageManager;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;
import org.javacord.api.entity.Icon;

import java.util.List;

/**
 * Represents a server settings page.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.24
 * @since 2.3.24
 */

public class ServerSettingPage extends Page<ServerSetting, ServerSetting.Category> {

    /**
     * Constructor for ServerSettingPage.
     *
     * @param pageNumber The page number.
     * @param pageItems The items in the page.
     * @param pageManager The page manager.
     */
    public ServerSettingPage(int pageNumber, List<ServerSetting> pageItems, PageManager<ServerSetting, ServerSetting.Category> pageManager) {
        super(pageNumber, pageItems, pageManager);
    }

    /** @inheritDoc */
    @Override
    public String getPageTitle() {
        return "Server settings for: $server";
    }

    /** @inheritDoc */
    @Override
    public String getPageDescription() {
        return "Type `!serversettings <setting>` for more information about that setting. Type `!serversettings <setting> <newValue>` to change it.";
    }

    /** @inheritDoc */
    @Override
    public String getPageAuthorImage(SkuddUser user, SkuddServer server) {
        Icon icon = server.asDiscordServer().getIcon().orElse(null); assert icon != null; //get the icon and assert it's not null
        return icon.getUrl().toString(); // return the icon url as a string
    }

    /**
     * {@inheritDoc}
     *
     * @return The value of the setting, trimmed to 1024 characters if it is longer.
     */
    @Override
    public String getData(ServerSetting item, SkuddUser user, SkuddServer server) {
        String value = server.getSettings().getString(item); //Gets the value for the item.
        value = value == null ? "null" : value; //If the value is null, set it to "null".

        if(value.length() > 1024) //If the value is longer than 1024 characters.
            value = value.substring(0, 1021) + "..."; //Shorten it to 1021 characters and add "...".

        return value; //Return the value.
    }

}
