/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import stans.resourcerecord.helpers.ValidationHelpers;
import stans.resourcerecord.model.Tag;
import stans.resourcerecord.model.TagType;
import stans.db.Query;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author peter
 */
public class CachePersister {

        
    public static boolean saveTagListByType (Integer type_id) // all tags should be the same type
    {
        boolean return_status = false;

        ArrayList<Tag> the_list = TagLoader.loadByTypeID(type_id);
        
        if (the_list.size() > 0)
        {
        
            String string_list = "";
            String query_name = "tag__type=" + Integer.toString(type_id);
            
            StringBuilder sb = new StringBuilder("");
            for (Tag t : the_list)
            {
                if (!sb.toString().equals(""))
                { sb.append(","); }
                sb.append(Integer.toString(t.getDBID()));
            }
            
            
            ArrayList<String> query_args = new ArrayList<String>();        
            query_args.add(query_name);
        
            ArrayList<Integer> cache_ids = Query.find("moe_cached_list", "query_name = ?", query_args); // check to see if we have an old list
            
            string_list = "'" + sb.toString() + "'";
            query_name = "'" + query_name + "'"; // have to do this after we run the find query above, because we don't want the quotes in the query
            
            if (cache_ids.size() > 0) // if we have a list already, update with the new list
            {
                if (Query.update("moe_cached_list", "pk1_list", cache_ids.get(0), string_list) > 0)
                { return_status = true; }
                
                
            }
            else // otherwise, create a new row and save the list
            {
                // get timestamp
                String curr_time = Query.getCurrentTime().toString();
                if (curr_time != null)
                { //curr_time = "TO_TIMESTAMP('" + curr_time + "','YYYY-MM-DD HH24:MI:SS.FF6')";
                	curr_time = "CAST(CAST('" + curr_time + "' as varchar(max)) as datetime)";
                }
             
                HashMap new_vals = new HashMap();
                new_vals.put("pk1_list", string_list);
                new_vals.put("query_name", query_name);
                if (curr_time != null)
                {
                    new_vals.put("created_at", curr_time);
                }

                if (Query.insert("moe_cached_list", new_vals) > 0)
                { return_status = true; }
            }
        
        }
        
        return return_status;
    }


    public static boolean saveNamedQuery (String query_name, ArrayList<Integer> the_list)
    {
        boolean return_status = false;


        StringBuilder sb = new StringBuilder("");
        for (Integer id : the_list)
        {
            if (!sb.toString().equals(""))
            { sb.append(","); }
            sb.append(Integer.toString(id));
        }
        
        ArrayList<String> query_args = new ArrayList<String>();        
        query_args.add(query_name);

        ArrayList<Integer> cache_ids = Query.find("moe_cached_list", "query_name = ?", query_args); // check to see if we have an old list

        query_name = "'" + query_name + "'"; // have to do this after we run the find query above, because we don't want the quotes in the query
        String string_list = "'" + sb.toString() + "'";
        
        if (cache_ids.size() > 0) // if we have a list already, update with the new list
        {
            if (Query.update("moe_cached_list", "pk1_list", cache_ids.get(0), string_list) > 0)
            { return_status = true; }
        }
        else // otherwise, create a new row and save the list
        {
            // get timestamp
            String curr_time = Query.getCurrentTime().toString();
            if (curr_time != null)
            { //curr_time = "TO_TIMESTAMP('" + curr_time + "','YYYY-MM-DD HH24:MI:SS.FF6')"; 
            	curr_time = "CAST(CAST('" + curr_time + "' as varchar(max)) as datetime)";
            }

            HashMap new_vals = new HashMap();
            new_vals.put("pk1_list", string_list);
            new_vals.put("query_name", query_name);
            if (curr_time != null)
            {
                new_vals.put("created_at", curr_time);
            }

            if (Query.insert("moe_cached_list", new_vals) > 0)
            { return_status = true; }
        }

        
        return return_status;
    }

}
