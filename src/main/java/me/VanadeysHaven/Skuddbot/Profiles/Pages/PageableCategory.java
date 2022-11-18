package me.VanadeysHaven.Skuddbot.Profiles.Pages;

import java.util.List;

/**
 * Interface for categories in pageables.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3.24
 * @since 2.3.24
 */
public interface PageableCategory<T> {

    /**
     * Get the name of the category.
     *
     * @return The name of the category.
     */
    String getName();

    /**
     * Gets a list of the items in the category.
     *
     * @return List of items.
     */
    List<T> getItems();

    /**
     * Specifies whether the category and it's items should be shown.
     *
     * @return Whether the category should be shown.
     */
    boolean isShow();

    /**
     * Specifies whether the header of a category should be shown.
     *
     * @return Whether the header should be shown.
     */
    boolean isShowHeader();

}
