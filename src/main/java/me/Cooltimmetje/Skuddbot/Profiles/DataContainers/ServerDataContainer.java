package me.Cooltimmetje.Skuddbot.Profiles.DataContainers;

import lombok.Getter;

/**
 * Data container with support for server data.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3
 * @since 2.3
 */
public class ServerDataContainer<T,S> extends DataContainer<T,S> {

    @Getter private long serverId;

}
