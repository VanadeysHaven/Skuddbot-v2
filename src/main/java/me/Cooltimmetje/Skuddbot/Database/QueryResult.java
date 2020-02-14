package me.Cooltimmetje.Skuddbot.Database;

import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableArrayGenerator;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableDrawer;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Result of query
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class QueryResult {

    private static final Logger logger = LoggerFactory.getLogger(QueryResult.class);

    private String id;
    private ResultSet rs;
    private boolean verboseLogging;

    public QueryResult(String id, ResultSet rs, boolean verboseLogging) throws SQLException {
        this.id = id;
        this.rs = rs;
        this.verboseLogging = verboseLogging;

        logger.info(formatId() + "QueryResult Created: \n" + generateTable());
    }

    public QueryResult(String id, ResultSet rs) throws SQLException {
        this(id, rs, false);
    }

    public boolean nextResult() throws SQLException {
        if(rs.next()) {
            if(verboseLogging) logger.info(formatId() + "Getting next result");
            return true;
        } else {
            if(verboseLogging) logger.info(formatId() + "Result set is empty...");
            return false;
        }
    }

    public int getInt(String columnReference) throws SQLException {
        int val = rs.getInt(columnReference);
        if(verboseLogging) logger.info(formatId() + "Returning INT value " + val + " from column " + columnReference);
        return val;
    }

    public long getLong(String columnReference) throws SQLException {
        long val = rs.getLong(columnReference);
        if(verboseLogging) logger.info(formatId() + "Returning LONG value " + val + " from column " + columnReference);
        return val;
    }

    public String getString(String columnReference) throws SQLException {
        String val = rs.getString(columnReference);
        if(verboseLogging) logger.info(formatId() + "Returning STRING value " + val + " from column " + columnReference);
        return val;
    }


    private String generateTable() throws SQLException {
        rs.beforeFirst();
        ResultSetMetaData rsmd = rs.getMetaData();
        int numOfColumns = rsmd.getColumnCount();
        TableArrayGenerator tag = new TableArrayGenerator();
        TableRow topRow = new TableRow();
        for(int i=1; i <= numOfColumns; i++){
            topRow.add(rsmd.getColumnLabel(i));
        }
        tag.addRow(topRow);
        while (rs.next()){
            TableRow tr = new TableRow();
            for(int i=1; i <= numOfColumns; i++){
                tr.add(rs.getString(i));
            }
            tag.addRow(tr);
        }

        rs.beforeFirst();
        return new TableDrawer(tag).drawTable();
    }

    private String formatId(){
        return "[ID: " +  id + "] ";
    }



}
