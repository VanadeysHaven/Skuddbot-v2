package me.Cooltimmetje.Skuddbot.Profiles.DataContainers;

import me.Cooltimmetje.Skuddbot.Database.Query;
import me.Cooltimmetje.Skuddbot.Enums.ValueType;

/**
 * Interface for Data enums.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3
 * @since 2.3
 */
public interface Data {

    String getDbReference();

    String getTechnicalName();

    String getTerminology();

    ValueType getType();

    boolean hasBound();

    boolean checkBound(int i);

    int getMinBound();

    int getMaxBound();

    boolean hasCooldown();

    int getCooldown();

    Query getUpdateQuery();

    Query getDeleteQuery();

    String getDefaultValue();

}
