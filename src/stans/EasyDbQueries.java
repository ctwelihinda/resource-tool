package stans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import blackboard.platform.db.JdbcServiceFactory;
import blackboard.db.BbDatabase;
import blackboard.db.ConnectionManager;
import blackboard.db.ConnectionNotAvailableException;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class EasyDbQueries {

    //connection stuff
    private BbDatabase bbDb;
    private ConnectionManager cm;
    private Connection dbCon;
    
    // current query stuff
    private String query;
    public List<String> constraint_keys;
    public List<sqlConstraintType> constraint_types;
    public List<String> constraint_values;
    private Statement statement;
    private ResultSet results;

    public enum sqlConstraintType {

        INTEGER, VARCHAR, BOOLEAN, CHAR, FLOAT, DATE, TIME
    }
    public EasyDbQueries() throws ConnectionNotAvailableException {
        bbDb = JdbcServiceFactory.getInstance().getDefaultDatabase();
        cm = bbDb.getConnectionManager();
        dbCon = cm.getConnection();
    }
//    public static List<EasyUser> getEasyUsersFromResultSet(ResultSet rs, HttpServletRequest request) throws SQLException, KeyNotFoundException, PersistenceException {
//        List<EasyUser> users = new ArrayList<EasyUser>();
//        while (rs.next()) {
//            String username = rs.getString("user_id");
//            EasyUser u = new EasyUser(username, request);
//            if(u.exists()){
//                users.add(u);
//            }
//        }
//        return users;
//    }
    public String getQuery() throws SQLException {
        //construct query
        for (int i = 0; i < constraint_keys.size(); i++) {
            switch (constraint_types.get(i)) {
                case VARCHAR:
                    if (i > 0) {
                        query += " AND";
                    }
                    query += " " + constraint_keys.get(i) + " = '" + constraint_values.get(i) + "'";
                    break;
                default:
                    if (i > 0) {
                        query += " AND";
                    }
                    query += " " + constraint_keys.get(i) + " = " + constraint_values.get(i);
            }
        }
        
        return query;
    }
    
    public void newSelectQuery(String tableName) {
        query = "SELECT * FROM " + tableName + " WHERE";
        constraint_keys = new ArrayList<String>();
        constraint_types = new ArrayList<sqlConstraintType>();
        constraint_values = new ArrayList<String>();
    }
    public void addConstraint(String key, String value, sqlConstraintType sqlType) {
        constraint_keys.add(key);
        constraint_types.add(sqlType);
        constraint_values.add(value);
    }
    public ResultSet execute() throws SQLException {
        statement = dbCon.createStatement();
        results = statement.executeQuery(query);        
        return results;
    }
    public void close() throws SQLException{
        statement.close();
        results.close();
    }
   
    
}
//        PreparedStatement sqlQuery = dbCon.prepareStatement(query);
//
//        for (int j = 0; j < constraint_values.size(); j++) {
//
//            switch (constraint_types.get(j)) {
//                case INTEGER:
//                    sqlQuery.setInt(j+1, Integer.parseInt(constraint_values.get(j)));
//                    break;
//                case VARCHAR:
//                    sqlQuery.setString(j+1, constraint_values.get(j));
//                    break;
//                case BOOLEAN:
//                    sqlQuery.setBoolean(j+1, Boolean.parseBoolean(constraint_values.get(j)));
//                    break;
//                case CHAR:
//                    sqlQuery.setString(j+1, constraint_values.get(j));
//                    break;
//                case FLOAT:
//                    sqlQuery.setFloat(j+1, Float.parseFloat(constraint_values.get(j)));
//                    break;
//                case DATE:
//                    sqlQuery.setDate(j+1, Date.valueOf(constraint_values.get(j)));
//                    break;
//                case TIME:
//                    sqlQuery.setTime(j+1, Time.valueOf(constraint_values.get(j)));
//                    break;
//                default:
//                    throw new UnsupportedOperationException();
//
//            }
//    }