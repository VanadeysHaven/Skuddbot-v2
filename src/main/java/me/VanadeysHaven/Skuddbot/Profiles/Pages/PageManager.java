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
public abstract class PageManager<T extends Pageable<C>, C extends PageableCategory<T>> {

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
     * @param categories The categories of the items that should be paginated
     */
    public void calculatePages(C[] categories) {
        Page<T,C> currentPage = null; //initialize current page

        for (C category : categories){ //iterate over all categories
            if(!category.isShow()) continue; //if category is hidden, skip it.
            List<T> items = category.getItems(); //get all items in the category.
            items.removeIf(i -> !i.isShow()); //remove items that are hidden.
            Page<T,C> page = constructNewPage(-1, items, this); //construct new page from the items.

            if(currentPage == null) {
                currentPage = page; //if current page is null, set it to the new page.
                continue; //continue to next iteration.
            }

            while(currentPage.exceedsMaxLength()) { //while current page exceeds max size
                Page<T,C> nextPage = currentPage.trimPage(); //trim current page
                addPage(currentPage); //add current page to list of pages.
                currentPage = nextPage; //set current page to next page.
            }

            if(currentPage.canMerge(page)) { //if current page can merge with the new page
                currentPage.mergeWith(page); //merge current page with new page.
            } else {
                addPage(currentPage); //add current page to list of pages.
                currentPage = page; //set current page to new page.
            }
        }

        if(currentPage == null) throw new UnsupportedOperationException("No pages were created. This is a bug.");
        addPage(currentPage); //add last page to list of pages.
    }

    /**
     * Adds a page to the list of pages.
     * @param page The page to add.
     */
    private void addPage(Page<T,C> page) {
        page.setPageNumber(pages.size() + 1); //set page number
        this.pages.add(page); //add page to pages list.
    }

    /**
     * Gets the amount of pages in this page manager.
     *
     * @return The amount of pages.
     */
    public int getPageAmount() {
        return pages.size(); //Returns the amount of pages.
    }

    public abstract Page<T,C> constructNewPage(int pageNumber, List<T> items, PageManager<T,C> pageManager);


}
