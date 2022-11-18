package me.VanadeysHaven.Skuddbot.Profiles.Users.Stats;

import me.VanadeysHaven.Skuddbot.Profiles.Pages.Page;
import me.VanadeysHaven.Skuddbot.Profiles.Pages.PageManager;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import me.VanadeysHaven.Skuddbot.Profiles.Users.SkuddUser;

import java.util.List;

/**
 * Represents a page with Stats.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.24
 * @since 2.3.24
 */
public class StatPage extends Page<Stat, Stat.Category> {

    /**
     * Constructor for StatPage.
     *
     * @param pageNumber The page number.
     * @param pageItems The items in the page.
     * @param pageManager The page manager.
     */
    public StatPage(int pageNumber, List<Stat> pageItems, PageManager<Stat, Stat.Category> pageManager) {
        super(pageNumber, pageItems, pageManager);
    }

    /** @inheritDoc */
    @Override
    public String getPageTitle() {
        return "Stats for: $user\n__Server:__ $server";
    }

    /** @inheritDoc */
    @Override
    public String getPageDescription() {
        return null;
    }

    /** @inheritDoc */
    @Override
    public String getPageAuthorImage(SkuddUser user, SkuddServer server) {
        return user.asMember().getUser().getAvatar().getUrl().toString();
    }

    /** @inheritDoc */
    @Override
    public String getData(Stat item, SkuddUser user, SkuddServer server) {
        StatsContainer stats = user.getStats(); //Gets the stats container for the user.
        if(item == Stat.EXPERIENCE) //If the item is experience.
            return stats.formatLevel(); //Format the level.

        int value = stats.getInt(item); //Gets the value for the item.
        if(!item.isShowAtZero() && value == 0)
            return null; //If the item is not shown at zero and the value is 0, return null.

        return value+""; //Return the value.
    }
}
