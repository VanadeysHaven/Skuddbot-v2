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

    INSERT_SERVER_SETTING("ignore into server_settings (setting_name) value (?);"),
    UPDATE_SERVER_SETTING_VALUE("insert into server_has_settings (setting_id, server_id, setting_value) value ((select id from server_settings where setting_name=?), ?, ?) on duplicate key update setting_value=?;"),
    DELETE_SERVER_SETTING_VALUE("delete shs from server_has_settings shs join server_settings ss on shs.setting_id = ss.id where shs.server_id=? AND ss.setting_name=?;"),
    SELECT_SERVER_SETTINGS("select setting_name, setting_value from server_has_settings shs join server_settings ss on shs.setting_id = ss.id where server_id=?;"),
    INSERT_SERVER("insert into servers (id, server_name) value (?,?) on duplicate key update server_name=?;");

    private String query;

    Query(String query){
        this.query = query;
    }

}
