package me.VanadeysHaven.Skuddbot.Profiles.Users.Stats;

import me.VanadeysHaven.Skuddbot.Profiles.Pages.Page;
import me.VanadeysHaven.Skuddbot.Profiles.Pages.PageManager;

import java.util.List;

/**
 * Manager for stat pages
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.24
 * @since 2.3.24
 */
public class StatPageManager extends PageManager<Stat, Stat.Category> {

    /** @inheritDoc */
    @Override
    public Page<Stat, Stat.Category> constructNewPage(int pageNumber, List<Stat> items, PageManager<Stat, Stat.Category> pageManager) {
        return new StatPage(pageNumber, items, pageManager); // Create new page
    }

}
