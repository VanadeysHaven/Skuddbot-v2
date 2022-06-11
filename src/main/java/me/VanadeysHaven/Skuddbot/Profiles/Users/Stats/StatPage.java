package me.VanadeysHaven.Skuddbot.Profiles.Users.Stats;

import me.VanadeysHaven.Skuddbot.Profiles.Pages.Page;
import me.VanadeysHaven.Skuddbot.Profiles.Pages.PageManager;
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
    public StatPage(int pageNumber, List<Stat> pageItems, PageManager pageManager) {
        super(pageNumber, pageItems, pageManager);
    }

    /**
     * @inheritDoc
     */
    @Override
    protected Page<Stat, Stat.Category> constructNewPage(int pageNumber, List<Stat> pageItems, PageManager pageManager) {
        return new StatPage(pageNumber, pageItems, pageManager); //Constructs new StatPage and return it.
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getPageTitle() {
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getData(Stat item, SkuddUser user) {
        StatsContainer stats = user.getStats();
        if(item == Stat.EXPERIENCE)
            return stats.formatLevel();

        int value = stats.getInt(item);
        if(ite)
        return null;
    }
}
