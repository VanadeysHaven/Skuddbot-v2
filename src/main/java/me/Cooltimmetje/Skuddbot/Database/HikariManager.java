package me.Cooltimmetje.Skuddbot.Database;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Holds the hikari instance.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.0
 * @since 2.0
 */
public class HikariManager {

    private static HikariDataSource hikari = null;

    public static void setup(String user, String pass){
        hikari = new HikariDataSource();
        hikari.setMaximumPoolSize(10);

        hikari.setJdbcUrl("jdbc:mysql://localhost:3306/skuddbot_v2?serverTimezone=UTC&useLegacyDatetimeCode=false");
//        hikari.addDataSourceProperty("serverName", "localhost");
//        hikari.addDataSourceProperty("port", 3306);
//        hikari.addDataSourceProperty("databaseName", "skuddbot_v2");
        hikari.addDataSourceProperty("user", user);
        hikari.addDataSourceProperty("password", pass);
    }

    static Connection getConnection() throws SQLException {
        return hikari.getConnection();
    }

}
