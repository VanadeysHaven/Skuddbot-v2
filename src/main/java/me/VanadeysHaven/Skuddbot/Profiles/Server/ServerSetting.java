package me.VanadeysHaven.Skuddbot.Profiles.Server;

import lombok.Getter;
import me.VanadeysHaven.Skuddbot.Database.Query;
import me.VanadeysHaven.Skuddbot.Database.QueryExecutor;
import me.VanadeysHaven.Skuddbot.Database.QueryResult;
import me.VanadeysHaven.Skuddbot.Enums.ValueType;
import me.VanadeysHaven.Skuddbot.Profiles.DataContainers.Data;
import me.VanadeysHaven.Skuddbot.Profiles.Pages.Pageable;
import me.VanadeysHaven.Skuddbot.Profiles.Pages.PageableCategory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Settings for servers.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.24
 * @since 2.0
 */
@Getter
public enum ServerSetting implements Data, Pageable<ServerSetting.Category> {

    XP_MIN                      ("xp_min",                  "Minimum amount of XP gain per message on Discord.",                            ValueType.INTEGER, "10",                   Category.XP,              false),
    XP_MAX                      ("xp_max",                  "Maximum amount of XP gain per message on Discord.",                            ValueType.INTEGER, "15",                   Category.XP,              false),
    XP_MIN_TWITCH               ("xp_min_twitch",           "Minimum amount of XP gain per message on Twitch.",                             ValueType.INTEGER, "10",                   Category.XP,              false), //TODO
    XP_MAX_TWITCH               ("xp_max_twitch",           "Maximum amount of XP gain per message on Twitch.",                             ValueType.INTEGER, "15",                   Category.XP,              false), //TODO
    XP_BASE                     ("xp_base",                 "Amount of XP an user will need to level up from level 1 to level 2.",          ValueType.INTEGER, "1500",                 Category.XP,              false),
    XP_MULTIPLIER               ("xp_multiplier",           "Multiplier for XP levels. The higher this number, the steeper the XP curve.",  ValueType.DOUBLE,  "1.2",                  Category.XP,              false),
    TWITCH_CHANNEL              ("twitch_channel",          "The Twitch channel this bot should keep track of stats.",                      ValueType.STRING,  null,                   Category.TWITCH,          false), //TODO
    WELCOME_MESSAGE             ("welcome_message",         "This message will be posted when a new user joins the server.",                ValueType.STRING,  null,                   Category.WELCOME_GOODBYE, true ),
    GOODBYE_MESSAGE             ("goodbye_message",         "This message will be posted when a user leaves the server.",                   ValueType.STRING,  null,                   Category.WELCOME_GOODBYE, true ),
    WELCOME_GOODBYE_CHANNEL     ("welcome_goodbye_channel", "This is the channel where the welcome/goodbye messages will be posted to.",    ValueType.LONG,    "-1",                   Category.WELCOME_GOODBYE, false),
    ADMIN_ROLE                  ("admin_role",              "This role will have access to commands that require elevated permissions.",    ValueType.STRING,  null,                   Category.DISCORD,         true ),
    ROLE_ON_JOIN                ("role_on_join",            "This role will be granted to new users when they join the server.",            ValueType.STRING,  null,                   Category.DISCORD,         true ),
    ALLOW_MSG_LVL_UP_NOTIFY     ("allow_msg_lvl_up_notify", "When set to false, users will not be notified by message when they level up.", ValueType.BOOLEAN, "true",                 Category.DISCORD,         false),
    ARENA_NAME                  ("arena_name",              "This is the name of the arena used in various minigames.",                     ValueType.STRING,  "Skuddbot's Colosseum", Category.MINIGAMES,       true ),
    JACKPOT                     ("jackpot",                 "Defines the Jackpot amount. (Value updates automatically)",                    ValueType.INTEGER, "0",                    Category.MINIGAMES,       false),
    COMMAND_PREFIX              ("command_prefix",          "The command prefix you can change this to avoid conflicts with other bots.",   ValueType.STRING,  "!",                    Category.COMMANDS,        false),
    ALLOW_MULTI_IMG             ("allow_multi_img",         "Enables the use of multi images in commands like !puppy and !kitty.",          ValueType.BOOLEAN, "true",                 Category.COMMANDS,        false),
    DAILY_BASE_CURRENCY_BONUS   ("daily_currency_bonus",    "Defines the base amount of currency a user gets per daily bonus claim.",       ValueType.INTEGER, "250",                  Category.DAILY_BONUS,     false),
    DAILY_BASE_EXPERIENCE_BONUS ("daily_xp_bonus",          "Defines the base amount of experience a user gets per daily bonus claim.",     ValueType.INTEGER, "500",                  Category.DAILY_BONUS,     false),
    DAILY_BONUS_MODIFIER        ("daily_bonus_multiplier",  "Defines the modifier applied to the bonuses after a streak claim.",            ValueType.DOUBLE,  "1.05",                 Category.DAILY_BONUS,     false),
    DAILY_BONUS_MULTIPLIER_CAP  ("daily_bonus_cap",         "Defines the cap of the multiplier.",                                           ValueType.INTEGER, "30",                   Category.DAILY_BONUS,     false);

    private static final ServerSettingPageManager PAGE_MANAGER = new ServerSettingPageManager();

    private final String dbReference;
    private final String description;
    private final ValueType type;
    private final String defaultValue;
    private final Category category;
    private final boolean allowSpaces;

    ServerSetting(String dbReference, String description, ValueType type, String defaultValue, Category category, boolean allowSpaces){
        this.dbReference = dbReference;
        this.description = description;
        this.type = type;
        this.defaultValue = defaultValue;
        this.category = category;
        this.allowSpaces = allowSpaces;
    }

    public static ServerSetting getByDbReference(String reference){
        for(ServerSetting setting : values())
            if(setting.getDbReference().equals(reference))
                return setting;

        return null;
    }

    /**
     * Get all settings in a category.
     *
     * @param category The category to get the settings from.
     * @return All settings in the category.
     */
    public static List<ServerSetting> getByCategory(Category category){
        return Arrays.stream(values()) // create stream from values
                .filter(s -> s.getCategory() == category) // filter out all settings that are not in the category
                .collect(Collectors.toList()); // collect the stream into a list and return it
    }

    public static ServerSetting[] grab(int length, int offset){
        int arrLength = Math.min(length, values().length - offset);
        if(arrLength < 1)
            throw new IndexOutOfBoundsException("There are no server settings available for length " + length + " and offset " + offset);

        ServerSetting[] arr = new ServerSetting[arrLength];

        for(int i=0; i < length; i++){
            if(i + offset >= values().length)
                break;

            arr[i] = (values()[i + offset]);
        }

        return arr;
    }

    /**
     * Setup server settings in database and calculate the pages.
     */
    public static void setup(){
        saveToDatabase(); // save all settings to database
        PAGE_MANAGER.calculatePages(Category.values()); // calculate pages
    }

    public static void saveToDatabase(){
        QueryExecutor qe = null;
        ArrayList<String> settings = new ArrayList<>();
        try {
            qe = new QueryExecutor(Query.SELECT_ALL_SERVER_SETTINGS);
            QueryResult qr = qe.executeQuery();
            while(qr.nextResult()){
                settings.add(qr.getString("setting_name"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            if(qe != null) qe.close();
        }
        for(ServerSetting setting : values()){
            if(settings.contains(setting.getDbReference())) continue;
            try {
                qe = new QueryExecutor(Query.INSERT_SERVER_SETTING).setString(1, setting.getDbReference());
                qe.execute();
            } catch (SQLException e){
                e.printStackTrace();
            } finally {
                if(qe != null) qe.close();
            }
        }
    }

    @Override
    public String getTechnicalName() {
        return this.toString();
    }

    @Override
    public String getTerminology() {
        return "settings";
    }

    @Override
    public boolean hasBound() {
        return false;
    }

    @Override
    public boolean checkBound(int i) {
        return true;
    }

    @Override
    public int getMinBound() {
        return -1;
    }

    @Override
    public int getMaxBound() {
        return -1;
    }

    @Override
    public boolean hasCooldown() {
        return false;
    }

    @Override
    public int getCooldown() {
        return -1;
    }

    @Override
    public Query getUpdateQuery() {
        return Query.UPDATE_SERVER_SETTING_VALUE;
    }

    @Override
    public Query getDeleteQuery() {
        return Query.DELETE_SERVER_SETTING_VALUE;
    }

    /** @inheritDoc */
    @Override
    public String getName() {
        return this.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @return Returns always true here because all server settings are always shown.
     */
    @Override
    public boolean isShow() {
        return true; // All server settings are always shown, so just return true.
    }

    public enum Category implements PageableCategory<ServerSetting> {
        XP, DISCORD, TWITCH, WELCOME_GOODBYE, MINIGAMES, COMMANDS, DAILY_BONUS;

        /** @inheritDoc */
        @Override
        public String getName() {
            return this.toString();
        }

        /** @inheritDoc */
        @Override
        public List<ServerSetting> getItems() {
            return getByCategory(this);
        }

        /**
         * {@inheritDoc}
         *
         * @return Returns always true here because all server settings categories are always shown.
         */
        @Override
        public boolean isShow() {
            return true; // All server settings categories are always shown, so just return true.
        }

        /**
         * {@inheritDoc}
         *
         * @return Returns always true here because all server settings categories headers are always shown.
         */
        public boolean isShowHeader(){
            return true; // All server settings categories headers are always shown, so just return true.
        }

    }

    /**
     * Get the page manager for the server settings.
     *
     * @return The page manager for the server settings.
     */
    public static ServerSettingPageManager getPageManager() {
        return PAGE_MANAGER; // return the page manager
    }

}
