package stans.resourcerecord.dao;
import java.util.ArrayList;
import java.util.HashMap;
import stans.db.Query;
import stans.helpers.UserHelpers;
import stans.resourcerecord.model.*;

public class PubDistPersister {

        
    /*
     * Calls the Query.insert method to INSERT a new PubDist row
     * 
     * If successful, returns a new Resource object initialized with the new row's pk1.
     * 
     * Otherwise, returns null
     * 
     */
    public static PubDistRecord createNew () {
        
        PubDistRecord rtn = null;
        
        String username = UserHelpers.getCurrentUsername();
        
        ArrayList<String> user_args = new ArrayList<String>();
        user_args.add(username);
        ArrayList<Integer> user_results = Query.find("users", "user_id = ?", user_args);

        
    // get timestamp
        String curr_time = Query.getCurrentTime().toString();
        if (curr_time != null)
        { 
        	curr_time = "CAST(CAST('" + curr_time + "' as varchar(max)) as datetime)";
        }
    // set values
        HashMap cols_and_vals = new HashMap();
        
        System.out.println("User Results returned size: ");
        System.out.println(user_results.size());
        System.out.println(username);
        System.out.println(curr_time);
        if (user_results.size() > 0)
        {
            cols_and_vals.put("created_by", user_results.get(0));
            System.out.println("Created by :" + user_results.get(0));
        }            
        if (curr_time != null)
        {
            cols_and_vals.put("created_at", curr_time);
            cols_and_vals.put("updated_at", curr_time);
        }
        
    // insert new record
        int pk1 = Query.insert("moe_publisher", cols_and_vals);
        
        //entry_id = "'R" + Integer.toString(pk1) + "'";
        
        if (pk1 >= 0)
        {
            //Query.update("moe_resource", "entry_id", pk1, entry_id);
            rtn = new PubDistRecord(pk1);
        }
        
        return rtn;
    }
    



    public static boolean setActive (int publisher_id, int oop_value)
    {
        boolean result = false;
        
        if (Query.update("moe_publisher", "is_active", publisher_id, Integer.toString(oop_value)) > 0)
        {
            updateResourceTimestamp(publisher_id);
            result = true;
        }
        
        return result;
    }
    
    public static boolean setName(int resource_id, String title)
    {
		System.out.println( "PubDistPersister.setName:" + title );
        if( Query.update( "moe_publisher", "name", resource_id, "'" + title + "'" )>0 ) {
            updateResourceTimestamp(resource_id);
            return true;
        }
        
        return false;
    }


    
    public static void updateResourceTimestamp (int resource_id)
    {
        String curr_time = Query.getCurrentTime().toString();
        if (curr_time != null)
        { 
        	curr_time = "CAST(CAST('" + curr_time + "' as varchar(max)) as datetime)";
        }
        Query.update("moe_publisher", "updated_at", resource_id, curr_time);
    }



}
