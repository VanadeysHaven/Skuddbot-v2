package me.VanadeysHaven.Skuddbot.Profiles.Pages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for pages for settings/stats etc
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.24
 * @since 2.2.1
 */
public final class PageManager<T extends Pageable<C>, C extends PageableCategory> {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(PageManager.class);

    /**
     * The maximum amount of elements per page.
     */
    public static final int MAX_SIZE = 21;

    /**
     * List that contains the pages.
     */
    private final List<Page<T, C>> pages;

    /**
     * Constructor for PageManager.
     */
    public PageManager() {
        this.pages = new ArrayList<>(); //Initialize pages list.
    }

    /**
     * Calculates all the pages.
     * @param items The items to be paginated.
     */
    public void calculatePages(T[] items) {

    }

    /**
     * Gets the amount of pages in this page manager.
     *
     * @return The amount of pages.
     */
    public int getPageAmount() {
        return pages.size(); //Returns the amount of pages.
    }

}
