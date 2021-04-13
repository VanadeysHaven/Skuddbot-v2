package me.Cooltimmetje.Skuddbot.Profiles.DataContainers;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;

import java.sql.SQLException;

/**
 * Data container with support for server data.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3
 * @since 2.3
 */
public abstract class ServerDataContainer<T extends Data> extends DataContainer<T> {

    @Getter private long serverId;

    public ServerDataContainer(long serverId){
        super();
        this.serverId = serverId;
    }

    @Override
    public void save(T field){ //identifier id //id identifier value value
        QueryExecutor qe = null;
        if (getString(field).equals(field.getDefaultValue())) {
            try {
                qe = new QueryExecutor(field.getDeleteQuery()).setLong(1, serverId).setString(2, field.getDbReference());
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                assert qe != null;
                qe.close();
            }
        } else {
            try {
                qe = new QueryExecutor(field.getUpdateQuery()).setString(1, field.getDbReference()).setLong(2, serverId).setString(3, getString(field)).and(4);
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                assert qe != null;
                qe.close();
            }
        }
    }

    @Override
    public int getIdentifier() {
        return -1;
    }

}
