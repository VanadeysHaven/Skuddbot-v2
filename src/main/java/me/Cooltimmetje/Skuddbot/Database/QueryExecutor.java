package me.Cooltimmetje.Skuddbot.Database;

import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Query builder and executor for the database.
 *
 * @author Tim (Cooltimmetje)
 * @version ALPHA-2.0
 * @since ALPHA-2.0
 */
public class QueryExecutor {

    private static final Logger logger = LoggerFactory.getLogger(QueryExecutor.class);

    private enum Operation {
        INT, LONG, STRING
    }

    private String id;
    private ArrayList<Integer> usedPositions;
    private Connection c;
    private PreparedStatement ps;
    private ResultSet rs;
    private Operation lastOperation;
    private Object lastValue;

    public QueryExecutor(Query query) throws SQLException {
        id = MiscUtils.randomString(10); //ID is only used for differentiating queries in the logs, it's fine if there are duplicates.
        logger.info("Making new query of type " + query + " with id " + id);
        c = HikariManager.getConnection();
        ps = c.prepareStatement(query.getQuery());
        usedPositions = new ArrayList<>();
    }

    public QueryExecutor setInt(int position, int value) throws SQLException {
        if(isPositionUsed(position)) throw new IllegalArgumentException("Position " + position + " is not free.");
        logger.info("[ID: " +  id + "] Setting INT at position " + position + " with value " + value);
        usedPositions.add(position);
        ps.setInt(position, value);
        lastOperation = Operation.INT;
        lastValue = value;
        return this;
    }

    public QueryExecutor setLong(int position, long value) throws SQLException {
        if(isPositionUsed(position)) throw new IllegalArgumentException("Position " + position + " is not free.");
        logger.info("[ID: " +  id + "] Setting LONG at position " + position + " with value " + value);
        usedPositions.add(position);
        ps.setLong(position, value);
        lastOperation = Operation.LONG;
        lastValue = value;
        return this;
    }

    public QueryExecutor setString(int position, String value) throws SQLException {
        if(isPositionUsed(position)) throw new IllegalArgumentException("Position " + position + " is not free.");
        logger.info("[ID: " +  id + "] Setting STRING at position " + position + " with value " + value);
        usedPositions.add(position);
        ps.setString(position, value);
        lastOperation = Operation.STRING;
        lastValue = value;
        return this;
    }

    public QueryExecutor and(int position) throws SQLException {
        if(isPositionUsed(position)) throw new IllegalArgumentException("Position " + position + " is not free.");
        logger.info("[ID: " +  id + "] Repeating last " + lastOperation + " value at position " + position);
        if(lastOperation == Operation.INT) ps.setInt(position, (int) lastValue);
        if(lastOperation == Operation.LONG) ps.setLong(position, (long) lastValue);
        if(lastOperation == Operation.STRING) ps.setString(position, (String) lastValue);
        return this;
    }

    private boolean isPositionUsed(int position){
        return usedPositions.contains(position);
    }

    public void execute() throws SQLException {
        logger.info("[ID: " +  id + "] Executing query " + ps.toString());
        ps.execute();
    }

    public QueryResult executeQuery() throws SQLException {
        logger.info("[ID: " +  id + "] Executing query " + ps.toString());
        rs = ps.executeQuery();
        return new QueryResult(id, rs);
    }

    public void close(){
        if(c != null){
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(ps != null){
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String formatId(){
        return "[ID: " +  id + "] ";
    }


}
