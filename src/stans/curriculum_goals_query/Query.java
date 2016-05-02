/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.curriculum_goals_query;

import blackboard.db.BbDatabase;
import blackboard.db.ConnectionManager;
import blackboard.db.ConnectionNotAvailableException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author peter
 */
public class Query {

    /*
     * 
     * Since the hashmap passed can contain any type of objects as values, it's the responsibility of the calling code to add surrounding '' to any strings in there
     */
    public static int update(String table, String column, int row, String new_value)
    {
        int result_count = 0;
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("UPDATE ");
        query_string.append(table);
        query_string.append(" SET ");
        query_string.append(column);
        query_string.append(" = ");
        query_string.append(new_value);
        query_string.append(" WHERE pk1 = ?");

        try 
        {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement updateQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            updateQuery.setString(1, Integer.toString(row));
            
        //    System.out.println("Update query: " + query_string.toString());
            result_count = updateQuery.executeUpdate();

        //    System.out.println("***" + result_count + " rows affected");
            
            updateQuery.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
    
        return result_count;
    }

    
    /*
     * INSERTs a new row into the specified table and returns the new pk1 if successful. If unsuccessful, returns -1
     * 
     * Since the hashmap passed can contain any type of objects as values, it's the responsibility of the calling code to add surrounding '' to any strings in there
     */
    public static int insert(String table, HashMap cols_and_values)
    {
        int return_pk1 = -1;
        
        ConnectionManager cManager = null;
        Connection conn = null;

    // build the query
        StringBuilder query_string = new StringBuilder("");
        query_string.append("INSERT INTO ");
        query_string.append(table);
        query_string.append(" (pk1");        
        Iterator it = cols_and_values.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            query_string.append(", ");
            query_string.append(pairs.getKey());
        }
        query_string.append(") VALUES (");
        query_string.append(table);
        query_string.append("_seq.nextval"); // the automagically-generated sequence
        Iterator it2 = cols_and_values.entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pairs = (Map.Entry)it2.next();
            query_string.append(", ");
            query_string.append(pairs.getValue());
        }
        query_string.append(")");
        
    // run the query
        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

        // prepare and execute the query
            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
          
         //   System.out.println("Insert query: " + query_string.toString());
            insertQuery.executeUpdate();
            
            insertQuery.close();
            
        // get the seq number of the new pk1
            StringBuilder seq_string = new StringBuilder("");
            seq_string.append("SELECT ");
            seq_string.append(table);
            seq_string.append("_seq.currval FROM DUAL");
            
            PreparedStatement getSeq = conn.prepareStatement(seq_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            ResultSet seq_results = getSeq.executeQuery();           
            while(seq_results.next()){
                return_pk1 = seq_results.getInt(1);
            }
            
            getSeq.close();
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }
        
        return return_pk1;
    }

    /*
     *  Retrieves a single column value from a given table and row
     */       
    public static Object select(String table, String column, int row)
    {
        Object query_result = null;
        
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT ");
        query_string.append(column);
        query_string.append(" FROM ");
        query_string.append(table);
        query_string.append(" WHERE pk1 = ?");

        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            insertQuery.setString(1, Integer.toString(row));
          
//            System.out.println("Select query: " + query_string.toString() + ", where ? = " + Integer.toString(row));
            ResultSet results = insertQuery.executeQuery();
            
            while(results.next()){
                if (column.equals("created_at"))
                { query_result = results.getTimestamp(1); }
                else if ((column.contains("_id")) && (!column.equals("entry_id")) && (!column.equals("user_id")))
                { query_result = results.getInt(1); }
                else
                { query_result = results.getString(1); }
            }
            
            insertQuery.close();

            results.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
        
        return query_result;    
    }
    
    public static Object selectint(String table, String column, int row)
    {
        Object query_result = null;
        
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT ");
        query_string.append(column);
        query_string.append(" FROM ");
        query_string.append(table);
        query_string.append(" WHERE pk1 = ?");

        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            insertQuery.setString(1, Integer.toString(row));
          
//            System.out.println("Select query: " + query_string.toString() + ", where ? = " + Integer.toString(row));
            ResultSet results = insertQuery.executeQuery();
            
            while(results.next()){
                query_result = results.getInt(1); 
            }
            
            insertQuery.close();

            results.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
        
        return query_result;    
    }
    
    public static Object selectedit(String table, String column, String val, int row)
    {
        Object query_result = null;
        
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT ");
        query_string.append(column);
        query_string.append(" FROM ");
        query_string.append(table);
        query_string.append(" WHERE ");
        query_string.append(val);
        query_string.append(" = ?");

        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            insertQuery.setString(1, Integer.toString(row));
          
//            System.out.println("Select query: " + query_string.toString() + ", where ? = " + Integer.toString(row));
            ResultSet results = insertQuery.executeQuery();
            
            while(results.next()){
                if (column.equals("created_at"))
                { query_result = results.getTimestamp(1); }
                else if ((column.contains("_id")) && (!column.equals("entry_id")) && (!column.equals("user_id")))
                { query_result = results.getInt(1); }
                else
                { query_result = results.getString(1); }
            }
            
            insertQuery.close();

            results.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
        
        return query_result;    
    }
    
    public static Object selectedit2(String table, String column, String val)
    {
        Object query_result = null;
        
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        System.out.println("val is" +val);
        query_string.append("SELECT ");
        query_string.append(column);
        query_string.append(" FROM ");
        query_string.append(table);
        query_string.append(" WHERE ");
        query_string.append(val);


        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            

          
//            System.out.println("Select query: " + query_string.toString() + ", where ? = " + Integer.toString(row));
            ResultSet results = insertQuery.executeQuery();
            
            while(results.next()){

                 query_result = results.getInt(1); 
            }
            
            insertQuery.close();

            results.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
        
        return query_result;    
    }
    public static Object selectFreeString(String query)
    {
        Object query_result = null;
        
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append(query);


        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            

          
//            System.out.println("Select query: " + query_string.toString() + ", where ? = " + Integer.toString(row));
            ResultSet results = insertQuery.executeQuery();
            
            while(results.next()){

                 query_result = results.getString(1); 
            }
            
            insertQuery.close();

            results.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
        
        return query_result;    
    }
    
    /*
     *  Given a table and a set of constraints (ie. a string containing everything after the WHERE keyword in a query along with a String ArrayList containing all of the values to match),
     *      returns an ArrayList of PK1s of rows that match. Returns a new ArrayList with size 0 if no matches are found.
     */
    public static ArrayList<Integer> find(String table, String constraints, ArrayList<String> arguments)
    {
        ArrayList<Integer> results = new ArrayList<Integer>();
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT pk1 FROM ");
        query_string.append(table);
        if (constraints != null)
        {
            query_string.append(" WHERE ");
            query_string.append(constraints);
        }

        try 
        {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            
      //      System.out.println("Find query: " + query_string.toString());
            if (arguments != null)
            {
        //        System.out.println("  -- where the args are: ");
                int counter = 1;
                for (String arg : arguments)
                {
                    insertQuery.setString(counter, arg);
          //          System.out.println("Arg " + Integer.toString(counter) + ": " + arg);
                    counter++;
                }
            }
            ResultSet resultset = insertQuery.executeQuery();

       //    System.out.println("***The results were: ");
            while(resultset.next()){
                results.add(resultset.getInt(1));
//                System.out.println(Integer.toString(resultset.getInt(1)));
            }
            
            insertQuery.close();

            resultset.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
    
        return results;
    }
    public static ArrayList<Integer> findbyColumnValue(String table, String constraints)
    {
        ArrayList<Integer> results = new ArrayList<Integer>();
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT pk1 FROM ");
        query_string.append(table);
        if (constraints != null)
        {
            query_string.append(" WHERE ");
            query_string.append(constraints);
        }

        try 
        {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            

            ResultSet resultset = insertQuery.executeQuery();

       //    System.out.println("***The results were: ");
            while(resultset.next()){
                results.add(resultset.getInt(1));
//                System.out.println(Integer.toString(resultset.getInt(1)));
            }
            
            insertQuery.close();

            resultset.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
    
        return results;
    }
    public static ArrayList<Integer> findbyAND(String table, String condition1 , String condition2)
    {
        ArrayList<Integer> results = new ArrayList<Integer>();
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT pk1 FROM ");
        query_string.append(table);

            query_string.append(" WHERE ");
            query_string.append(condition1);
            query_string.append(" AND ");
            query_string.append(condition2);


        try 
        {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            

            ResultSet resultset = insertQuery.executeQuery();

       //    System.out.println("***The results were: ");
            while(resultset.next()){
                results.add(resultset.getInt(1));
//                System.out.println(Integer.toString(resultset.getInt(1)));
            }
            
            insertQuery.close();

            resultset.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
    
        return results;
    }
    
    public static ArrayList<Integer> getEntireColumn(String table, String column)
    {
        ArrayList<Integer> results = new ArrayList<Integer>();
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT ");
        query_string.append(column);
        query_string.append(" FROM ");
        query_string.append(table);

        try 
        {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            ResultSet resultset = insertQuery.executeQuery();

       //    System.out.println("***The results were: ");
            while(resultset.next()){
                results.add(resultset.getInt(1));
//                System.out.println(Integer.toString(resultset.getInt(1)));
            }
            
            insertQuery.close();

            resultset.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
    
        return results;
    }
    
    public static ArrayList<String> getEntireColumnString(String table, String column)
    {
        ArrayList<String> results = new ArrayList<String>();
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT ");
        query_string.append(column);
        query_string.append(" FROM ");
        query_string.append(table);

        try 
        {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            ResultSet resultset = insertQuery.executeQuery();

       //    System.out.println("***The results were: ");
            while(resultset.next()){
                results.add(resultset.getString(1));               
//                System.out.println(Integer.toString(resultset.getInt(1)));
            }
            
            insertQuery.close();

            resultset.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
    
        return results;
    }
    
    
    
    public static ArrayList<Integer> findedit(String table, String column, String constraints, ArrayList<String> arguments)
    {
        ArrayList<Integer> results = new ArrayList<Integer>();
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT ");
        query_string.append(column);
        query_string.append(" FROM ");
        query_string.append(table);
        if (constraints != null)
        {
            query_string.append(" WHERE ");
            query_string.append(constraints);
        }

        try 
        {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            
         //   System.out.println("Find query: " + query_string.toString());
            if (arguments != null)
            {
          //      System.out.println("  -- where the args are: ");
                int counter = 1;
                for (String arg : arguments)
                {
                    insertQuery.setString(counter, arg);
             //       System.out.println("Arg " + Integer.toString(counter) + ": " + arg);
                    counter++;
                }
            }
            ResultSet resultset = insertQuery.executeQuery();

//            System.out.println("***The results were: ");
            while(resultset.next()){
                results.add(resultset.getInt(1));
//                System.out.println(Integer.toString(resultset.getInt(1)));
            }
            
            insertQuery.close();

            resultset.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
    
        return results;
    }
    
    
    public static boolean freeformUpdate(String query)
    {
        boolean query_result = true;
        
        ConnectionManager cManager = null;
        Connection conn = null;

        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query);
          
            int insertResult = insertQuery.executeUpdate();
            
            if (insertResult != 1)
            {
            	query_result = false ;
            }
            insertQuery.close();

        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
            query_result = false ;
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
            query_result = false ;
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }
    
        return query_result;
    }
    
    public static Timestamp getCurrentTime()
    {
        Timestamp rtn = null;
        
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT CURRENT_TIMESTAMP FROM DUAL");

        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement timestampQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                      
            ResultSet results = timestampQuery.executeQuery();
            
            while(results.next()){
                rtn = results.getTimestamp(1);
           //     System.out.println(rtn.toString());
            }
            
            timestampQuery.close();

            results.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
        } 
        catch (ConnectionNotAvailableException cE)
        {
            System.out.println("ConnectionNotAvailableException caught!!");
            System.out.println(cE.getMessage());
            System.out.println(cE.getFullMessageTrace());
        } 
        finally 
        {
            if (conn != null)
            {
                cManager.releaseConnection(conn);
            }
        }        
        
        return rtn;
    }
}
