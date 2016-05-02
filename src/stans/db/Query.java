/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.db;

import blackboard.db.BbDatabase;
import blackboard.db.ConnectionManager;
import blackboard.db.ConnectionNotAvailableException;
import java.sql.Clob;
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
 * version 2016.03.14
 * editor chamath -converting Oracle data types to SQL Server for MB. 
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
		//System.out.println( "Query.update:" + new_value );
		
        try 
        {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement updateQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            updateQuery.setString(1, Integer.toString(row));
            
            //System.out.println("Update query: " + query_string.toString());
            result_count = updateQuery.executeUpdate();

            //System.out.println("***" + result_count + " rows affected");
            
            updateQuery.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("\n---------------------");
            System.out.println("SQLException caught!!");          
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
            System.out.println("   Method: update");
            System.out.println("   Locals:");
            System.out.println("     table: " + table);
            System.out.println("     column: " + column);
            System.out.println("     row: " + Integer.toString(row));
            System.out.println("     new_value: " + new_value);
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

        ArrayList<String> overflow_strings = new ArrayList<String>(); // for inserting strings as clobs if they're longer than 4000 chars
        
    // build the query
        StringBuilder query_string = new StringBuilder("");
        query_string.append("INSERT INTO ");
        query_string.append(table);
        query_string.append(" (");
        //query_string.append(" (pk1");
        Iterator it = cols_and_values.entrySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            if(count > 0){
            query_string.append(", ");
            }
            query_string.append(pairs.getKey());
            count++;
        }
        query_string.append(") VALUES (");
        //query_string.append("NEXT VALUE FOR ");
        //query_string.append(table);
       // query_string.append("_seq");
       // query_string.append("_seq.nextval"); // the automagically-generated sequence
        Iterator it2 = cols_and_values.entrySet().iterator();
        count = 0;
        while (it2.hasNext()) {
            Map.Entry pairs = (Map.Entry)it2.next();
            if(count > 0){
            query_string.append(", ");
            }
            if ((pairs.getValue() instanceof String) && (((String) pairs.getValue()).length() > 4000)) // need to insert as clob below, so put in ? as placeholder for now
            {
                query_string.append("?");
                overflow_strings.add((String) pairs.getValue());
            }
            else
            { query_string.append(pairs.getValue()); }
            count++;
        }
        query_string.append(")");
        
		//System.out.println( "Saving new record: " + query_string.toString() );
		
    // run the query
        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

        // prepare and execute the query
            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
          
            for (String long_string : overflow_strings)
            {
                Clob clob = conn.createClob();
                clob.setString(1, long_string);
                insertQuery.setClob(1, clob);
            }
            
            //System.out.println("Insert query: " + query_string.toString());
            insertQuery.executeUpdate();
            
            insertQuery.close();
            
        // get the seq number of the new pk1
            StringBuilder seq_string = new StringBuilder("");
            seq_string.append("SELECT ");
            //seq_string.append(table);
            seq_string.append("MAX(pk1) FROM ");
            seq_string.append(table);
            
            //seq_string.append("_seq.currval FROM DUAL");
            
            PreparedStatement getSeq = conn.prepareStatement(seq_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            //System.out.println("before getSeq execute");
            ResultSet seq_results = getSeq.executeQuery();           
            while(seq_results.next()){
                return_pk1 = seq_results.getInt(1);
            }
            
            getSeq.close();
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("\n---------------------");
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
            System.out.println("   Method: insert");
            System.out.println("   Locals:");
            System.out.println("     table: " + table);
			Iterator it3 = cols_and_values.entrySet().iterator();
			while (it3.hasNext()) {
				Map.Entry pairs = (Map.Entry)it3.next();
				if( pairs.getValue().getClass()==String.class ) {
					System.out.println( "     " + pairs.getKey() + ": " + ( String )pairs.getValue() );
				} else if( pairs.getValue().getClass()==Integer.class ) {
					System.out.println( "     " + pairs.getKey() + ": " + ( ( Integer )pairs.getValue() ).toString() );
				}
			}
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
     * INSERTs a new row into the specified table and returns the new pk1 if successful. If unsuccessful, returns -1
     * 
     * Since the hashmap passed can contain any type of objects as values, it's the responsibility of the calling code to add surrounding '' to any strings in there
     */
    public static int delete(String table, String constraints, ArrayList<String> arguments)
    {
        int rtn = 0;
        
        ConnectionManager cManager = null;
        Connection conn = null;

    // build the query
        StringBuilder query_string = new StringBuilder("");
        query_string.append("DELETE FROM ");
        query_string.append(table);
        query_string.append(" WHERE "); 
        query_string.append(constraints);

        
    // run the query
        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

        // prepare and execute the query
            PreparedStatement deleteQuery = conn.prepareStatement(query_string.toString());
            
            System.out.println("Delete: " + query_string.toString());
            if (arguments != null)
            {
                System.out.println("  -- where the args are: ");
                int counter = 1;
                for (String arg : arguments)
                {
                    deleteQuery.setString(counter, arg);
                    System.out.println("Arg " + Integer.toString(counter) + ": " + arg);
                    counter++;
                }
            }
            
            rtn = deleteQuery.executeUpdate();
            deleteQuery.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("\n---------------------");
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
            System.out.println("   Method: delete");
            System.out.println("   Locals:");
            System.out.println("     table: " + table);
            System.out.println("     constraints: " + constraints);
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

    /*
     *  Retrieves a single column value from a given table and row, or null if query returned no results
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

            PreparedStatement selectQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            selectQuery.setString(1, Integer.toString(row));
          
            //System.out.println("Select query: " + query_string.toString() + ", where ? = " + Integer.toString(row));
            ResultSet results = selectQuery.executeQuery();
            
            while(results.next()){
                if(
					( column.equals("created_at") ) || 
					( column.equals("updated_at") ) ||
					( column.equals("expiry_date") ) ||
					( column.equals("anticipated_start_date") )
				) {
					query_result = results.getTimestamp(1);
				} else if(
					( (column.contains("_id")) && (!column.equals("entry_id")) && (!column.equals("user_id")) && (!column.equals("moe_id")) && (!column.equals("big_ideas")) && (!column.equals("hierarchy_id")) ) ||
					( column.contains("_pk1") ) ||
					( column.equals("next_number") ) ||
					( column.equals("language_code") ) ||
					( column.equals("section_type") ) ||
					( column.equals("is_active") ) ||
					( column.equals("element_type") ) ||
					( column.equals("position") ) ||
					( column.equals("layer") ) ||
					( column.equals("display_order") ) ||
					( column.equals("grade_lo") ) ||
					( column.equals("grade_hi") ) ||
					( column.equals("recommended") ) ||
					( column.equals("reason_code") ) ||
					( column.equals("recommendation") ) ||
					( column.equals("out_of_print") ) ||
					( column.equals("dont_show_if_child") ) ||
					( column.equals("adapted_adopted") ) ||
					( column.equals("course_renewal") ) ||
					( column.equals("course_exists") ) ||
					( column.equals("is_from_other_province") ) ||
					( column.equals("status") ) ||
					( column.equals("source") ) ||
					( column.equals("area_of_study") ) ||
					( column.equals("current_editor") ) ||
					( column.contains("ministry_approval_") )
				) {
					query_result = results.getInt(1);
					if( ( column.contains("ministry_approval_") )&&( results.wasNull() ) ) {
						query_result = null;
					}
				}
                else if(
					( (column.equals("data")) && (table.equals("qti_asi_data")) ) ||
					( (column.equals("data")) && (table.equals("qti_result_data")) )
				) {
					query_result = results.getBlob(1);
				}
				else {
					query_result = results.getString(1);
					if( query_result!=null ) {
						//System.out.println( "query_result is: " + query_result.toString() );
					} else {
						//System.out.println( "query_result is null" );
					}
				}
            }
            
            selectQuery.close();

            results.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("\n---------------------");
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
            System.out.println("   Method: select");
            System.out.println("   Locals:");
            System.out.println("     table: " + table);
            System.out.println("     column: " + column);
            System.out.println("     row: " + Integer.toString(row));
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
            
            
            //System.out.println("Find query: " + query_string.toString());
            if (arguments != null)
            {
                //System.out.println("  -- where the args are: ");
                int counter = 1;
                for (String arg : arguments)
                {
                    insertQuery.setString(counter, arg.replace("''", "'")); // find doesn't match '' to ', but '' is needed for inserts. Many servlets will have '' passed to them, though
                    //System.out.println("Arg " + Integer.toString(counter) + ": " + arg);
                    counter++;
                }
            }
            ResultSet resultset = insertQuery.executeQuery();

            //System.out.println("***The results were: ");
            while(resultset.next()){
                results.add(resultset.getInt(1));
               // System.out.println(Integer.toString(resultset.getInt(1)));
            }
            
            insertQuery.close();

            resultset.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("\n---------------------");
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
            System.out.println("   Method: find");
            System.out.println("   Locals:");
            System.out.println("     table: " + table);
            System.out.println("     constraints: " + constraints);
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
    
    public static ArrayList<Integer> findAndSelectIDs (String table, String column, String constraints, ArrayList<String> arguments)
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

        //System.out.print("findAndSelectIDs: query is ");
        //System.out.print(query_string.toString());
        //System.out.print("\n");
        //System.out.print("findAndSelectIDs: arguments are ");
        for (String arg : arguments)
        {
            //System.out.println(arg);
            //System.out.println("\n");
        }
        //System.out.print("\n");
        
        
        try 
        {
            
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement selectQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            
            //System.out.println("Find query: " + query_string.toString());
            if (arguments != null)
            {
                //System.out.println("  -- where the args are: ");
                int counter = 1;
                for (String arg : arguments)
                {
                    selectQuery.setString(counter, arg.replace("''", "'")); // find doesn't match '' to ', but '' is needed for inserts. Many servlets will have '' passed to them, though);
                    //System.out.println("Arg " + Integer.toString(counter) + ": " + arg);
                    counter++;
                }
            }
            ResultSet resultset = selectQuery.executeQuery();
            
            while(resultset.next()){
                Integer i = resultset.getInt(1);
                results.add(i);
                //System.out.println("findAndSelectIDs: result ID is ");
                //System.out.println(Integer.toString(i));
            }
            
            selectQuery.close();

            resultset.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("\n---------------------");
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
            System.out.println("   Method: find");
            System.out.println("   Locals:");
            System.out.println("     table: " + table);
            System.out.println("     constraints: " + constraints);
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
    
    
    
    public static ArrayList<Integer> findWithOperator(String tablename, String column, String value, Enumerators.BBComparisonOperator operator) {
        ArrayList<Integer> result_ids = new ArrayList<Integer>();
        ArrayList<String> args = new ArrayList<String>();
        switch (operator) {
            case CONTAINS:
                args.add("%" + value.toLowerCase() + "%");
                result_ids = Query.find(tablename, "lower(" + column + ") LIKE ?", args);
                break;
            case EQUALS:
                args.add(value.toLowerCase());
                result_ids = Query.find(tablename, "lower(" + column + ") = ?", args);
                break;
            case NOTEQUALS:
                args.add(value.toLowerCase());
                result_ids = Query.find(tablename, "lower(" + column + ") != ?", args);
                break;
            case STARTSWITH:
                args.add(value.toLowerCase() + "%");
                result_ids = Query.find(tablename, "lower(" + column + ") LIKE ?", args);
                break;
            case ENDSWITH:
                args.add("%" + value.toLowerCase());
                result_ids = Query.find(tablename, "lower(" + column + ") LIKE ?", args);
                break;
            case NOTBLANK:
                result_ids = Query.find(tablename, column + " IS NOT NULL", null);
                break;
            case BLANK:
                result_ids = Query.find(tablename, column + " IS NULL", args);
                break;
        }
        return result_ids;
    }
    
    public static ArrayList<String> freefind(String query)
    {
        ArrayList<String> results = new ArrayList<String>();
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

//        query_string.append("SELECT pk1 FROM ");
        query_string.append(query);


        try 
        {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement insertQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            //System.out.println("Find query: " + query_string.toString());

            ResultSet resultset = insertQuery.executeQuery();

            //System.out.println("*!*The results were: ");
            while(resultset.next())
            {
                results.add(resultset.getString(1));
                //System.out.println(resultset.getString(1));
            }
            
            insertQuery.close();

            resultset.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("\n---------------------");
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
            System.out.println("   Method: freefind");
            System.out.println("   Locals:");
            System.out.println("   Query: " + query);

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
            System.out.println("\n---------------------");
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
            System.out.println("   Method: freeformUpdate");
            System.out.println("   Locals:");
            System.out.println("   Query: " + query);
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
    
    public static Integer getRowCount (String table)
    {
        Integer count = null;
        
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT COUNT(*) FROM ");
        query_string.append(table);

        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement selectQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
          
            //System.out.println("Select query: " + query_string.toString() + ", where ? = " + Integer.toString(row));
            ResultSet results = selectQuery.executeQuery();
            
            while(results.next()){
                count = results.getInt(1);
            }
            
            selectQuery.close();

            results.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("\n---------------------");
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
            System.out.println("   Method: count");
            System.out.println("   Locals:");
            System.out.println("     table: " + table);
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
        
        return count;    
    }
    
    public static Integer getHighestID (String table)
    {
        Integer count = null;
        
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT MAX(pk1) FROM ");
        query_string.append(table);

        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement selectQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
          
            //System.out.println("Select query: " + query_string.toString() + ", where ? = " + Integer.toString(row));
            ResultSet results = selectQuery.executeQuery();
            
            while(results.next()){
                count = results.getInt(1);
            }
            
            selectQuery.close();

            results.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("\n---------------------");
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
            System.out.println("   Method: count");
            System.out.println("   Locals:");
            System.out.println("     table: " + table);
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
        
        return count;    
    }
    
    public static Timestamp getCurrentTime()
    {
        Timestamp rtn = null;
        
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

       //Oracle Statement query_string.append("SELECT CURRENT_TIMESTAMP FROM DUAL");
        //SQL Server
        query_string.append("SELECT CURRENT_TIMESTAMP");

        try {
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();
            conn = cManager.getConnection();

            PreparedStatement timestampQuery = conn.prepareStatement(query_string.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                      
            ResultSet results = timestampQuery.executeQuery();
            
            while(results.next()){
                rtn = results.getTimestamp(1);
                //System.out.println(rtn.toString());
            }
            
            timestampQuery.close();

            results.close();
            
        } 
        catch (java.sql.SQLException sE) 
        {
            System.out.println("\n---------------------");
            System.out.println("SQLException caught!!");
            System.out.println(sE.getMessage());
            System.out.println(sE.getSQLState());
            System.out.println("   Method: getCurrentTime");
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
     public static ArrayList<Integer> finduserspk(String table, String constraints, ArrayList<String> arguments)
    {
        ArrayList<Integer> results = new ArrayList<Integer>();
        StringBuilder query_string = new StringBuilder("");

        ConnectionManager cManager = null;
        Connection conn = null;

        query_string.append("SELECT users_pk1 FROM ");
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
}
