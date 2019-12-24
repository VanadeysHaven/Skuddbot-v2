package me.Cooltimmetje.Skuddbot.Database;

import me.Cooltimmetje.Skuddbot.Enums.Query;

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

    private enum Operation {
        INT, LONG, STRING
    }

    private ArrayList<Integer> usedPositions;
    private Connection c;
    private PreparedStatement ps;
    private ResultSet rs;
    private Operation lastOperation;
    private Object lastValue;

    public QueryExecutor(Query query) throws SQLException {
        c = HikariManager.getConnection();
        ps = c.prepareStatement(query.getQuery());
        usedPositions = new ArrayList<>();
    }

    public QueryExecutor setInt(int position, int value) throws SQLException {
        if(isPositionUsed(position)) throw new IllegalArgumentException("Position " + position + " is not free.");
        usedPositions.add(position);
        ps.setInt(position, value);
        lastOperation = Operation.INT;
        lastValue = value;
        return this;
    }

    public QueryExecutor setLong(int position, long value) throws SQLException {
        if(isPositionUsed(position)) throw new IllegalArgumentException("Position " + position + " is not free.");
        usedPositions.add(position);
        ps.setLong(position, value);
        lastOperation = Operation.LONG;
        lastValue = value;
        return this;
    }

    public QueryExecutor setString(int position, String value) throws SQLException {
        if(isPositionUsed(position)) throw new IllegalArgumentException("Position " + position + " is not free.");
        usedPositions.add(position);
        ps.setString(position, value);
        lastOperation = Operation.STRING;
        lastValue = value;
        return this;
    }

    public QueryExecutor and(int position) throws SQLException {
        if(isPositionUsed(position)) throw new IllegalArgumentException("Position " + position + " is not free.");
        if(lastOperation == Operation.INT) ps.setInt(position, (int) lastValue);
        if(lastOperation == Operation.LONG) ps.setLong(position, (long) lastValue);
        if(lastOperation == Operation.STRING) ps.setString(position, (String) lastValue);
        return this;
    }

    private boolean isPositionUsed(int position){
        return usedPositions.contains(position);
    }

    public void execute() throws SQLException {
        ps.execute();
    }

    public ResultSet executeQuery() throws SQLException {
        rs = ps.executeQuery();
        return rs;
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


}
