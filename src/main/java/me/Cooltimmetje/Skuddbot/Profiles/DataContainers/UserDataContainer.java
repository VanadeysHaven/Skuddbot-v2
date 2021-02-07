package me.Cooltimmetje.Skuddbot.Profiles.DataContainers;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Profiles.Users.Identifier;

/**
 * Data container with support for user data.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3
 * @since 2.3
 */
public class UserDataContainer<T extends Data> extends DataContainer<T> {

    @Getter
    private Identifier id;

}
