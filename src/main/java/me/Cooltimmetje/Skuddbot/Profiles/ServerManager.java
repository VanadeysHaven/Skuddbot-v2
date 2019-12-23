package me.Cooltimmetje.Skuddbot.Profiles;

import me.Cooltimmetje.Skuddbot.Database.QueryExecutor;
import me.Cooltimmetje.Skuddbot.Enums.Query;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.Server.ServerSettingsSapling;
import me.Cooltimmetje.Skuddbot.Profiles.Server.SkuddServer;
import org.javacord.api.entity.server.Server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * This class is used for managing server profiles.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class ServerManager {

    private static ArrayList<SkuddServer> servers = new ArrayList<>();

    public SkuddServer getServer(long id){
        Optional<Server> oServer =  Main.getSkuddbot().getApi().getServerById(id);
        if(!oServer.isPresent()) throw new IllegalArgumentException("Server id " + id + " does not exist");
        Server server = oServer.get();

        SkuddServer ss = searchList(id);
        if(ss != null){
            return ss;
        }

        insertServerKey(id, server.getName());
        ss = new SkuddServer(id, new ServerSettingsSapling(id));
        servers.add(ss);
        return ss;
    }

    private void insertServerKey(long id, String name){
        QueryExecutor qe = null;
        try {
            qe = new QueryExecutor(Query.INSERT_SERVER).setLong(1, id).setString(2, name).and(3);
            qe.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            assert qe != null;
            qe.close();
        }
    }

    private SkuddServer searchList(long id){
        for(SkuddServer server : servers) if(server.getServerId() == id) return server;
        return null;
    }


}
