package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * Queries for the database.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
@Getter
public enum Query {

    //USER IDENTIFIERS
    SELECT_USER_ID("select id from identifier where server_id=? and discord_id=?;"),
    SAVE_USER_ID("insert into identifier (server_id, discord_id) value (?,?);"),

    //SERVER SETTINGS
    INSERT_SERVER_SETTING("insert ignore into server_settings (setting_name) value (?);"),
    UPDATE_SERVER_SETTING_VALUE("insert into server_has_settings (setting_id, server_id, setting_value) value ((select get_server_setting_id(?)),?,?) on duplicate key update setting_value=?;"),
    DELETE_SERVER_SETTING_VALUE("delete shs from server_has_settings shs join server_settings ss on shs.setting_id = ss.id where shs.server_id=? AND ss.setting_name=?;"),
    SELECT_SERVER_SETTINGS("select setting_name, setting_value from server_has_settings shs join server_settings ss on shs.setting_id = ss.id where server_id=?;"),
    INSERT_SERVER("insert into servers (id, server_name) value (?,?) on duplicate key update server_name=?;"),
    SELECT_ALL_SERVER_SETTINGS("select setting_name from server_settings;"),

    //USER SETTINGS
    INSERT_USER_SETTING("insert ignore into user_settings (setting_name) value (?);"),
    SELECT_ALL_USER_SETTINGS("select setting_name from user_settings;"),
    SELECT_USER_SETTINGS("select us.setting_name, ush.setting_value from user_has_settings ush join user_settings us on ush.setting_id = us.id where user_id=?;"),
    DELETE_USER_SETTING_VALUE("delete ush from user_has_settings ush join user_settings us on ush.setting_id = us.id where ush.user_id=? and us.setting_name=?;"),
    UPDATE_USER_SETTING_VALUE("insert into user_has_settings (setting_id, user_id, setting_value) values ((select get_user_setting_id(?)),?,?) on duplicate key update setting_value=?;"),

    //STATS
    INSERT_STAT("insert ignore into stats (stat_name) value (?);"),
    SELECT_ALL_STATS("select stat_name from stats;"),
    SELECT_STATS("select s.stat_name, uhs.stat_value from user_has_stats uhs join stats s on uhs.stat_id = s.id where uhs.user_id=?;"),
    DELETE_STAT_VALUE("delete uhs from user_has_stats uhs join stats s on uhs.stat_id = s.id where uhs.user_id=? and s.stat_name=?;"),
    UPDATE_STAT_VALUE("insert into user_has_stats(stat_id, user_id, stat_value) value ((select get_stat_id(?)),?,?) on duplicate key update stat_value=?;"),

    //DONATORS
    LOAD_ALL_DONATORS("select * from donators;"),
    LOAD_ALL_MESSAGES("select data_name, discord_id, data_value from donator_has_data dhd join donator_data dd on dhd.data_id = dd.id;"),
    UPDATE_DONATOR("insert into donators(id, ping_message) value (?,?) on duplicate key update ping_message=?;"),
    PURGE_DONATOR_DATA("delete from donator_has_data where discord_id=?;"),
    DELETE_DONATOR("delete from donators where id=?;"),

    //ADMINS
    ADD_ADMIN("insert into admin_users (discord_id) value (?);"),
    DELETE_ADMIN("delete from admin_users where discord_id=?;"),
    LOAD_ADMINS("select * from admin_users;");

    private String query;

    Query(String query){
        this.query = query;
    }

}