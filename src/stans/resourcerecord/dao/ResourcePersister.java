package stans.resourcerecord.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import stans.db.Query;
import stans.resourcerecord.helpers.RNumberManager;
import stans.helpers.UserHelpers;
import stans.resourcerecord.model.*;

public class ResourcePersister {
        
    /*
     * Calls the Query.insert method to INSERT a new Resource row
     * 
     * If successful, returns a new Resource object initialized with the new row's pk1.
     * 
     * Otherwise, returns null
     * 
     */
    public static Resource createNew () {
        
        Resource rtn = null;
        
        String username = UserHelpers.getCurrentUsername();
        
        ArrayList<String> user_args = new ArrayList<String>();
        user_args.add(username);
        ArrayList<Integer> user_results = Query.find("users", "user_id = ?", user_args);

        
    // get timestamp
        String curr_time = Query.getCurrentTime().toString();
        if (curr_time != null)
        { //curr_time = "TO_TIMESTAMP('" + curr_time + "','YYYY-MM-DD HH24:MI:SS.FF6')"; 
        	curr_time = "CAST(CAST('" + curr_time + "' as varchar(max)) as datetime)";
        }
    // set values
        HashMap cols_and_vals = new HashMap();
        
        String new_entry_id = "'" + RNumberManager.getNext() + "'";
        
        cols_and_vals.put("entry_id", new_entry_id); 
        if (user_results.size() > 0)
        {
            cols_and_vals.put("created_by", user_results.get(0));
        }            
        if (curr_time != null)
        {
            cols_and_vals.put("created_at", curr_time);
            cols_and_vals.put("updated_at", curr_time);
        }
        
    // insert new record
        int pk1 = Query.insert("moe_resource", cols_and_vals);
        
        //entry_id = "'R" + Integer.toString(pk1) + "'";
        
        if (pk1 >= 0)
        {
            //Query.update("moe_resource", "entry_id", pk1, entry_id);
            rtn = new Resource(pk1);
        }
        
        return rtn;
    }
    
    public static boolean addParent (int child_id, int parent_id) {
        boolean result = false;
        
        if (Query.update("moe_resource", "parent_id", child_id, Integer.toString(parent_id)) > 0)
        {
            updateResourceTimestamp(child_id);
            updateResourceTimestamp(parent_id);
            result = true;
        }
        
        return result;
    }
    public static boolean removeParent (int child_id) {
        boolean result = false;
        
        Integer parent_id = (Integer)Query.select("moe_resource", "parent_id", child_id);
        if (Query.update("moe_resource", "parent_id", child_id, null) > 0)
        {
            updateResourceTimestamp(child_id);
            updateResourceTimestamp(parent_id);
            result = true;
        }
        
        return result;
    }
    
    public static boolean addFinalRecommendation (int resource_id, int rec_value)
    {
        boolean result = false;
        
        
    // get timestamp
        String curr_time = Query.getCurrentTime().toString();
        if (curr_time != null)
        {// curr_time = "TO_TIMESTAMP('" + curr_time + "','YYYY-MM-DD HH24:MI:SS.FF6')"; 
        	curr_time = "CAST(CAST('" + curr_time + "' as varchar(max)) as datetime)";
        }
        
        if (
                (Query.update("moe_resource", "recommendation", resource_id, Integer.toString(rec_value)) > 0) &&
                (Query.update("moe_resource", "go_live_date", resource_id, curr_time) > 0)
        )
        {
            updateResourceTimestamp(resource_id);
            result = true;
        }
            
        return result;
    }
    
    public static boolean setOutOfPrint (int resource_id, int oop_value)
    {
        boolean result = false;
        
        if (Query.update("moe_resource", "out_of_print", resource_id, Integer.toString(oop_value)) > 0)
        {
            updateResourceTimestamp(resource_id);
            result = true;
        }
        
        return result;
    }
    
    public static boolean setTitle (int resource_id, String title)
    {
		System.out.println( "ResourcePersister.setTitle:" + title );
        if( Query.update( "moe_resource", "title", resource_id, "'" + title + "'" )>0 ) {
            updateResourceTimestamp(resource_id);
            return true;
        }
        
        return false;
    }
    public static boolean setSubtitle (int resource_id, String subtitle)
    {
        if (Query.update("moe_resource", "subtitle", resource_id, "'" + subtitle + "'") > 0)
        {
            updateResourceTimestamp(resource_id);
            return true;
        }
        
        return false;
    }
    public static boolean setEdition (int resource_id, String edition)
    {
        if (Query.update("moe_resource", "edition", resource_id, "'" + edition + "'") > 0)
        {
            updateResourceTimestamp(resource_id);
            return true;
        }
        
        return false;
    }
    
    public static boolean hideIfChild (int resource_id, int hic_value)
    {
        boolean result = false;
        
        if (Query.update("moe_resource", "dont_show_if_child", resource_id, Integer.toString(hic_value)) > 0)
        {
            result = true;
        }
        
        return result;
    }
    
    public static void updateResourceTimestamp (int resource_id)
    {
        String curr_time = Query.getCurrentTime().toString();
        if (curr_time != null)
        { //curr_time = "TO_TIMESTAMP('" + curr_time + "','YYYY-MM-DD HH24:MI:SS.FF6')"; 
        	curr_time = "CAST(CAST('" + curr_time + "' as varchar(max)) as datetime)";
        }
        Query.update("moe_resource", "updated_at", resource_id, curr_time);
    }

}
