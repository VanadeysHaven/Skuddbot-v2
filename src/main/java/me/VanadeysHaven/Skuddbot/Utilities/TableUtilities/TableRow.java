package me.VanadeysHaven.Skuddbot.Utilities.TableUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * This class represents a row in a table.
 *
 * @author Tim (Vanadey's Haven)
 * @version 2.0
 * @since 1.4.62
 */
public class TableRow {

    private ArrayList<String> strings;

    public TableRow(){
        this.strings = new ArrayList<>();
    }

    public TableRow(String... strings){
        this.strings = new ArrayList<>(Arrays.asList(strings));
    }

    public TableRow add(String string){
        this.strings.add(string);
        return this;
    }

    public TableRow add(int i){
        return add(i+"");
    }

    public Iterator<String> getIterator(){
        return strings.iterator();
    }

    public int getLength(){
        return strings.size();
    }

}
