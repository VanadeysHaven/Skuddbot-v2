package me.VanadeysHaven.Skuddbot.Profiles.Server;

import me.VanadeysHaven.Skuddbot.Profiles.Pages.Page;
import me.VanadeysHaven.Skuddbot.Profiles.Pages.PageManager;

import java.util.List;

/**
 * Manager for server setting pages
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.24
 * @since 2.3.24
 */
public class ServerSettingPageManager extends PageManager<ServerSetting, ServerSetting.Category> {

    /** @inheritDoc */
    @Override
    public Page<ServerSetting, ServerSetting.Category> constructNewPage(int pageNumber, List<ServerSetting> items, PageManager<ServerSetting, ServerSetting.Category> pageManager) {
        return new ServerSettingPage(pageNumber, items, pageManager); // Create new page
    }

}
