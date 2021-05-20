package me.VanadeysHaven.Skuddbot.Profiles;

import me.VanadeysHaven.Skuddbot.Database.Query;
import me.VanadeysHaven.Skuddbot.Database.QueryExecutor;
import me.VanadeysHaven.Skuddbot.Main;
import me.VanadeysHaven.Skuddbot.Profiles.Server.ServerSettingsSapling;
import me.VanadeysHaven.Skuddbot.Profiles.Server.SkuddServer;
import org.javacord.api.entity.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class is used for managing server profiles.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.1.1
 * @since 2.0
 */
public class ServerManager {

    private static ServerManager instance;

    public static ServerManager getInstance(){
        if(instance == null)
            instance = new ServerManager();

        return instance;
    }

    private static ArrayList<SkuddServer> servers = new ArrayList<>();
    private final static Logger logger = LoggerFactory.getLogger(ServerManager.class);

    private ServerManager(){}

    public SkuddServer getServer(long id){
        logger.info("Requested server profile for id " + id);
        Server server = Main.getSkuddbot().getApi().getServerById(id).orElse(null); assert server != null;

        SkuddServer ss = searchList(id);
        if(ss != null){
            logger.info("Profile found, returning...");
            return ss;
        }

        logger.info("No profile, loading...");
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
            if(qe != null)
                qe.close();
        }
    }

    private SkuddServer searchList(long id){
        for(SkuddServer server : servers) if(server.getServerId() == id) return server;
        return null;
    }

    public Iterator<SkuddServer> getServers(){
        return servers.iterator();
    }


}
