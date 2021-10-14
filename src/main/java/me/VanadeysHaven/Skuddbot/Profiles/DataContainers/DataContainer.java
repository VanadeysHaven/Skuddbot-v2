package me.VanadeysHaven.Skuddbot.Profiles.DataContainers;

import me.VanadeysHaven.Skuddbot.Database.QueryExecutor;
import me.VanadeysHaven.Skuddbot.Enums.ValueType;
import me.VanadeysHaven.Skuddbot.Exceptions.CooldownException;
import me.VanadeysHaven.Skuddbot.Exceptions.SettingOutOfBoundsException;
import me.VanadeysHaven.Skuddbot.Profiles.ProfileManager;
import me.VanadeysHaven.Skuddbot.Profiles.ServerManager;
import me.VanadeysHaven.Skuddbot.Utilities.CooldownManager;
import me.VanadeysHaven.Skuddbot.Utilities.MiscUtils;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Main class that serves as a basis for all data containers.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.3.2
 * @since 2.3
 */
public abstract class DataContainer<T extends Data> {

    protected static final ServerManager sm = ServerManager.getInstance();
    protected static final ProfileManager pm = ProfileManager.getInstance();

    private HashMap<T, String> values;
    private HashMap<T, CooldownManager> cooldowns;

    public DataContainer(){
        values = new HashMap<>();
        cooldowns = new HashMap<>();
    }

    public void setString(T field, String value){
        setString(field, value, true, false);
    }

    public void setString(T field, String value, boolean save, boolean bypassCooldown){
        if(isOnCooldown(field) && !bypassCooldown) throw new CooldownException("You can't change " + field.getTerminology() + " `" + field.getTechnicalName() + "` currently. You can change it again in: " + getCooldownManager(field).formatTime(69));
        if(value != null) {
            if(!checkType(field, value)) throw new IllegalArgumentException("Value " + value + " is unsuitable for " + field.getTerminology() + " `" + field.getTechnicalName() + "`; not of type " + field.getType());
            if(field.getType() == ValueType.INTEGER) if(field.hasBound()) if(!field.checkBound(Integer.parseInt(value))) throw new SettingOutOfBoundsException("The value `" + value + "` is out of bounds for " + field.getTechnicalName() + " " + field + ". (Bounds: `" + field.getMinBound() + "` - `" + field.getMaxBound() + "`)");
            if(value.equalsIgnoreCase("null")) value = null;
        }

        this.values.put(field, value);
        if(!bypassCooldown) startCooldown(field);
        if(save) save(field);
    }

    public String getString(T field){
        return this.values.get(field);
    }

    public void setInt(T field, int value){
        setInt(field, value, true, false);
    }

    public void setInt(T field, int value, boolean save, boolean bypassCooldown){
        setString(field, value+"", save, bypassCooldown);
    }

    public void incrementInt(T field){
        incrementInt(field, 1);
    }

    public void incrementInt(T field, int incrementBy){
        if(field.getType() != ValueType.INTEGER) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type INTEGER.");
        setInt(field, getInt(field) + incrementBy);
    }

    public void incrementIntMax(T field, int incrementBy, int maxValue){
        if(field.getType() != ValueType.INTEGER) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type INTEGER.");

        int newValue = getInt(field) + incrementBy;
        setInt(field, Math.min(newValue, maxValue));
    }

    public void incrementIntMin(T field, int incrementBy, int minValue){
        if(field.getType() != ValueType.INTEGER) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type INTEGER.");

        int newValue = getInt(field) + incrementBy;
        setInt(field, Math.max(newValue, minValue));
    }

    public void incrementIntBounds(T field, int incrementBy, int min, int max){
        if(field.getType() != ValueType.INTEGER) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type INTEGER.");

        int newValue = getInt(field) + incrementBy;
        if(newValue < min){
            incrementIntMin(field, incrementBy, min);
        } else if (newValue > max){
            incrementIntMax(field, incrementBy, max);
        } else {
            incrementInt(field, incrementBy);
        }
    }

    public int getInt(T field){
        if(field.getType() != ValueType.INTEGER) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type INTEGER.");
        return Integer.parseInt(getString(field));
    }

    public void setDouble(T field, double value){
        setDouble(field,  value, true, false);
    }

    public void setDouble(T field, double value, boolean save, boolean bypassCooldown){
        setString(field, value+"", save, bypassCooldown);
    }

    public double getDouble(T field){
        if(field.getType() != ValueType.DOUBLE) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type DOUBLE.");
        return Double.parseDouble(getString(field));
    }

    public void setLong(T field, long value){
        setLong(field, value, true, false);
    }

    public void setLong(T field, long value, boolean save, boolean bypassCooldown){
        setString(field, value+"", save, bypassCooldown);
    }

    public void incrementLong(T field){
        incrementLong(field, 1);
    }

    public void incrementLong(T field, long incrementBy){
        if(field.getType() != ValueType.LONG) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type LONG.");
        setLong(field, getLong(field) + incrementBy);
    }

    public long getLong(T field){
        if(field.getType() != ValueType.LONG) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type LONG.");
        return Long.parseLong(getString(field));
    }

    public void setBoolean(T field, boolean value){
        setBoolean(field, value, true, false);
    }

    public void setBoolean(T field, boolean value, boolean save, boolean bypassCooldown){
        setString(field, value+"", save, bypassCooldown);
    }

    public void toggleBoolean(T field) {
        if(field.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type BOOLEAN.");
        setBoolean(field, !getBoolean(field));
    }

    public boolean getBoolean(T field){
        if(field.getType() != ValueType.BOOLEAN) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type BOOLEAN.");
        return Boolean.parseBoolean(getString(field));
    }

    private boolean checkType(T field, String value){
        ValueType type = field.getType();
        switch (type) {
            case INTEGER: return MiscUtils.isInt(value);
            case DOUBLE: return MiscUtils.isDouble(value);
            case LONG: return MiscUtils.isLong(value);
            case BOOLEAN: return MiscUtils.isBoolean(value);
            case JSON: return value.equals("{}");
            default: return type == ValueType.STRING;
        }
    }

    private void startCooldown(T field) {
        if(!field.hasCooldown()) return;

        getCooldownManager(field).startCooldown(69);
    }

    private boolean isOnCooldown(T field){
        if(!field.hasCooldown()) return false;
        return getCooldownManager(field).isOnCooldown(69);
    }

    private CooldownManager getCooldownManager(T field) {
        CooldownManager manager;
        if(!cooldowns.containsKey(field)) {
            manager = new CooldownManager(field.getCooldown(), true);
            cooldowns.put(field, manager);
        } else manager = cooldowns.get(field);

        return manager;
    }

    public void save(){
        for(T field : values.keySet()){
            save(field);
        }
    }

    public void save(T field){ //identifier id //id identifier value value
        String value = getString(field);
        if(value == null) value = "null";
        QueryExecutor qe = null;
        if (getString(field).equals(field.getDefaultValue())) {
            try {
                qe = new QueryExecutor(field.getDeleteQuery()).setInt(1, getIdentifier()).setString(2, field.getDbReference());
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                assert qe != null;
                qe.close();
            }
        } else {
            try {
                qe = new QueryExecutor(field.getUpdateQuery()).setString(1, field.getDbReference()).setInt(2, getIdentifier()).setString(3, value).and(4);
                qe.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                assert qe != null;
                qe.close();
            }
        }
    }

    public abstract int getIdentifier();

}
