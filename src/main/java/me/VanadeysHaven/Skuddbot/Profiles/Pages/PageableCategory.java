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

    String getName();

    List<T> getItems();

    boolean isShow();

}
