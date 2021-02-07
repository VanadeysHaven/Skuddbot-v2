package me.Cooltimmetje.Skuddbot.Profiles.DataContainers;

import me.Cooltimmetje.Skuddbot.Enums.ValueType;
import me.Cooltimmetje.Skuddbot.Exceptions.CooldownException;
import me.Cooltimmetje.Skuddbot.Exceptions.SettingOutOfBoundsException;
import me.Cooltimmetje.Skuddbot.Utilities.CooldownManager;

import java.util.HashMap;

/**
 * Main class that serves as a basis for all data containers.
 *
 * @author Tim (Cooltimmetje)
 * @version 2.3
 * @since 2.3
 */
public class DataContainer<T extends Data> {

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
        if(!checkType(field, value)) throw new IllegalArgumentException("Value " + value + " is unsuitable for " + field.getTerminology() + " `" + field.getTechnicalName() + "`; not of type " + field.getType());
        if(isOnCooldown(field) && !bypassCooldown) throw new CooldownException("You can't change " + field.getTerminology() + " `" + field.getTechnicalName() + "` currently. You can change it again in: " + getCooldownManager(field).formatTime(69));
        if(field.getType() == ValueType.INTEGER) if(field.hasBound()) if(!field.checkBound(Integer.parseInt(value))) throw new SettingOutOfBoundsException("The value `" + value + "` is out of bounds for " + field.getTechnicalName() + " " + field + ". (Bounds: `" + field.getMinBound() + "` - `" + field.getMaxBound() + "`)");

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
        setInt(field, getInt(field) + incrementBy);
    }

    public int getInt(T field){
        if(field.getType() == ValueType.INTEGER) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type INTEGER.");
        return Integer.parseInt(getString(field));
    }

    public void setDouble(T field, String value){

    }

    public void setDouble(T field, String value, boolean save, boolean bypassCooldown){

    }

    public double getDouble(T field){
        if(field.getType() == ValueType.DOUBLE) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type DOUBLE.");
        return Double.parseDouble(getString(field));
    }

    public void setLong(T field, String value){

    }

    public void setLong(T field, String value, boolean save, boolean bypassCooldown){

    }

    public long getLong(T field){
        if(field.getType() == ValueType.LONG) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type LONG.");
        return Long.parseLong(getString(field));
    }

    public void setBoolean(T field, String value){

    }

    public void setBoolean(T field, String value, boolean save, boolean bypassCooldown){

    }

    public boolean getBoolean(T field){
        if(field.getType() == ValueType.LONG) throw new IllegalArgumentException(field.getTerminology() + " " + field.getTechnicalName() + " is not of type BOOLEAN.");
        return Boolean.parseBoolean(getString(field));
    }

    private boolean checkType(T field, String value){

    }

    private void startCooldown(T field) {

    }

    private boolean isOnCooldown(T field){

    }

    private CooldownManager getCooldownManager(T field) {

    }

    private void save(T field){

    }


}
