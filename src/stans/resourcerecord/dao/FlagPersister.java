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
public class FlagPersister {
    
    public static boolean addFlagToResource(int resource_id, int reason_code, String comments)
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
        {// curr_time = "TO_TIMESTAMP('" + curr_time + "','YYYY-MM-DD HH24:MI:SS.FF6')";
        	curr_time = "CAST(CAST('" + curr_time + "' as varchar(max)) as datetime)";
        }
        
    // set values
        HashMap cols_and_vals = new HashMap();
        cols_and_vals.put("resource_id", resource_id);
        cols_and_vals.put("reason_code", reason_code);
        if (user_results.size() > 0)
        {
            cols_and_vals.put("user_id", user_results.get(0));
        }
        if (curr_time != null)
        {
            cols_and_vals.put("created_at", curr_time);
        }
        if (comments != null)
        {
            cols_and_vals.put("comments", comments);
        }
        
    // insert new record
        int pk1 = Query.insert("moe_resource_flag", cols_and_vals);
        
        if (pk1 >= 0)
        {
            rtn = true;
        }

        return rtn;
    }   
    
    public static int updateComments(int flag_id, String comments)
    {
        int results = Query.update("moe_resource_flag", "comments", flag_id, comments);
        
        return results;
    }   
    
    public static int deleteFlag(int flag_id)
    {
        int rtn = 0;
        
        ArrayList<String> del_args = new ArrayList<String>();
        
        del_args.add(Integer.toString(flag_id));
        
        rtn = Query.delete("moe_resource_flag", "pk1 = ?", del_args);
        
        return rtn;
    }
}
