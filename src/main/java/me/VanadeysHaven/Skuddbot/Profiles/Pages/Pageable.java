package me.VanadeysHaven.Skuddbot.Profiles.Pages;

import me.VanadeysHaven.Skuddbot.Enums.ValueType;

/**
 * Interface for pageable enums
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3.24
 * @since 2.3.24
 */
public interface Pageable<C> {

    /**
     * Get the name of the item.
     *
     * @return The name of the item.
     */
    String getName();

    /**
     * Get the type of the item.
     *
     * @return The type of the item.
     */
    ValueType getType();

    /**
     * Get the category of the item.
     *
     * @return The category of the item.
     */
    C getCategory();

    /**
     * Get whether the item is visible.
     *
     * @return Whether the item is visible.
     */
    boolean isShow();

}
