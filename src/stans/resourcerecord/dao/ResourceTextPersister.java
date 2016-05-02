/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import blackboard.data.user.User;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManager;
import blackboard.platform.context.ContextManagerFactory;
import stans.db.Query;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author peter
 */
public class ResourceTextPersister {
    
    
    public static boolean addTextToResource(int resource_id, String text, String type)
    {
        boolean rtn = false;
        
    // get user
        ContextManager contextManager = ContextManagerFactory.getInstance();
        Context ctx = contextManager.getContext();

        User user = ctx.getUser();
        String username = user.getUserName();
        
        ArrayList<String> user_args = new ArrayList<String>();
        ArrayList<Integer> user_results;
        user_args.add(username);

        user_results = Query.find("users", "user_id = ?", user_args);

    // get timestamp
        String curr_time = Query.getCurrentTime().toString();
        if (curr_time != null)
        { //curr_time = "TO_TIMESTAMP('" + curr_time + "','YYYY-MM-DD HH24:MI:SS.FF6')";
        	curr_time = "CAST(CAST('" + curr_time + "' as varchar(max)) as datetime)";
        }
        
    // set values
        HashMap cols_and_vals = new HashMap();
        cols_and_vals.put("resource_id", resource_id);
        cols_and_vals.put("text", text);
        cols_and_vals.put("type", type);
        if (user_results.size() > 0)
        {
            cols_and_vals.put("created_by", user_results.get(0));
        }
        if (curr_time != null)
        {
            cols_and_vals.put("created_at", curr_time);
        }
        
    // insert new record
        int pk1 = Query.insert("moe_resource_text", cols_and_vals);
        
        if (pk1 >= 0)
        {
            rtn = true;
            ResourcePersister.updateResourceTimestamp(resource_id);
        }

        return rtn;        
    }
    
    public static boolean replaceText(int text_id, String new_text)
    {
        boolean rtn = false;
        
    // get user
        ContextManager contextManager = ContextManagerFactory.getInstance();
        Context ctx = contextManager.getContext();

        User user = ctx.getUser();
        String username = user.getUserName();
        
        ArrayList<String> user_args = new ArrayList<String>();
        ArrayList<Integer> user_results;
        user_args.add(username);

        user_results = Query.find("users", "user_id = ?", user_args);

    // get timestamp
        String curr_time = Query.getCurrentTime().toString();
        if (curr_time != null)
        { //curr_time = "TO_TIMESTAMP('" + curr_time + "','YYYY-MM-DD HH24:MI:SS.FF6')";
        	curr_time = "CAST(CAST('" + curr_time + "' as varchar(max)) as datetime)";
        }
        
        int text_result_count = 0;
        int user_result_count = 0;
        int time_result_count = 0;
        
    // update text
        text_result_count = Query.update("moe_resource_text", "text", text_id, new_text);
    // update user
        if (user_results.size() > 0)
        {
            user_result_count = Query.update("moe_resource_text", "created_by", text_id, Integer.toString(user_results.get(0)));
        }
    // update time
        if (curr_time != null)
        {
            curr_time = "'" + curr_time + "'";
            time_result_count = Query.update("moe_resource_text", "created_at", text_id, curr_time);
        }
        
        if ((text_result_count >= 0) && (time_result_count >= 0) && (user_result_count >= 0))
        {
            Integer resource_id = (Integer) Query.select("moe_resource_text", "resource_id", text_id);
            ResourcePersister.updateResourceTimestamp(resource_id);
            rtn = true;
        }

        return rtn;        
    
    }
    
    public static void delete(int text_id)
    {
        Integer resource_id = (Integer) Query.select("moe_resource_text", "resource_id", text_id);
        ArrayList<String> args = new ArrayList<String>();
        args.add(Integer.toString(text_id));

        Query.delete("moe_resource_text", "pk1 = ?", args);

        ResourcePersister.updateResourceTimestamp(resource_id);
    }
    
}
