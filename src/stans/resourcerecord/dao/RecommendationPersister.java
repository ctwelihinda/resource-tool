package stans.resourcerecord.dao;

import blackboard.data.user.User;

import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManager;
import blackboard.platform.context.ContextManagerFactory;
import stans.resourcerecord.model.*;
import stans.db.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

public class RecommendationPersister {
        
    /*
     * Calls the Query.insert method to INSERT a new TagType row (where description is null).
     * 
     * If successful, returns a new TagType object initialized with the new row's pk1.
     * 
     * Otherwise, returns null
     * 
     */
    public static Recommendation createNew (int resource_id, int recommended, String comments) {
        
        Recommendation rtn = null;
        
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
        cols_and_vals.put("recommended", recommended);
        comments = "'" + comments + "'";
        cols_and_vals.put("comments", comments);
        
        if (user_results.size() > 0)
        {
            cols_and_vals.put("created_by", user_results.get(0));
        }
        if (curr_time != null)
        {
            cols_and_vals.put("created_at", curr_time);
        }
        
    // insert new record
        int pk1 = Query.insert("moe_recommendation", cols_and_vals);
        
        if (pk1 >= 0)
        {
            rtn = new Recommendation(pk1);
            ResourcePersister.updateResourceTimestamp(resource_id);
        }
        
        return rtn;
    }
    public static void delete (int recommendation_id) {
        
        ArrayList<String> args = new ArrayList<String>();
        args.add(Integer.toString(recommendation_id));
        Query.delete("moe_recommendation", "pk1 = ?", args);
    }
}
