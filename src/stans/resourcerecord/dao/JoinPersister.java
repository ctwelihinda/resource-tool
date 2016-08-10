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
import stans.resourcerecord.model.Resource;
import stans.resourcerecord.model.PubDistRecord;
import stans.resourcerecord.dao.PubDistPersister;
/**
 *
 * @author peter
 */
public class JoinPersister {
	
	public static int addPublisherTagJoin(int publisher_id, int tag_id)
    {       
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
        cols_and_vals.put("publisher_id", publisher_id);
        cols_and_vals.put("tag_id", tag_id);
        if (user_results.size() > 0)
        {
            cols_and_vals.put("created_by", user_results.get(0));
        }
        if (curr_time != null)
        {
            cols_and_vals.put("created_at", curr_time);
        }
        
    // insert new record
        int pk1 = Query.insert("moe_publisher_tag", cols_and_vals);
        PubDistPersister.updateResourceTimestamp(publisher_id);
        
        return pk1;
    }
	
    public static int addPublisherTagJoin(int resource_id, int tag_id, int parent_id)
    {
        
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
        cols_and_vals.put("publisher_id", resource_id);
        cols_and_vals.put("tag_id", tag_id);
        cols_and_vals.put("parent_id", parent_id);
        if (user_results.size() > 0)
        {
            cols_and_vals.put("created_by", user_results.get(0));
        }
        if (curr_time != null)
        {
            cols_and_vals.put("created_at", curr_time);
        }
        
    // insert new record
        int pk1 = Query.insert("moe_publisher_tag", cols_and_vals);
        if (pk1 >= 0)
        {
            PubDistPersister.updateResourceTimestamp(resource_id);
        }

        return pk1;
    }
	
    
    public static int addResourceTagJoin(int resource_id, int tag_id)
    {       
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
        cols_and_vals.put("tag_id", tag_id);
        if (user_results.size() > 0)
        {
            cols_and_vals.put("created_by", user_results.get(0));
        }
        if (curr_time != null)
        {
            cols_and_vals.put("created_at", curr_time);
        }
        
    // insert new record
        int pk1 = Query.insert("moe_resource_tag", cols_and_vals);
        ResourcePersister.updateResourceTimestamp(resource_id);
        
        return pk1;
    }
    
    public static int addResourceTagJoin(int resource_id, int tag_id, int parent_id)
    {
        
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
        cols_and_vals.put("tag_id", tag_id);
        cols_and_vals.put("parent_id", parent_id);
        if (user_results.size() > 0)
        {
            cols_and_vals.put("created_by", user_results.get(0));
        }
        if (curr_time != null)
        {
            cols_and_vals.put("created_at", curr_time);
        }
        
    // insert new record
        int pk1 = Query.insert("moe_resource_tag", cols_and_vals);
        if (pk1 >= 0)
        {
            ResourcePersister.updateResourceTimestamp(resource_id);
        }

        return pk1;
    }
    
    public static int removeResourceTagJoin( int join_id, Resource r )
    {
        int rtn = 0;
        
        ArrayList<String> del_args = new ArrayList<String>();
        
        del_args.add( Integer.toString( join_id ) );
        
        rtn = Query.delete( "moe_resource_tag", "pk1 = ?", del_args );
        ResourcePersister.updateResourceTimestamp( r.getDBID() );
        
        return rtn;
    }
    public static int removeResourceTagJoin(int resource_id, int tag_id)
    {
        int rtn = 0;
        
        ArrayList<String> del_args = new ArrayList<String>();
        
        del_args.add(Integer.toString(resource_id));
        del_args.add(Integer.toString(tag_id));
        
        rtn = Query.delete("moe_resource_tag", "resource_id = ? AND tag_id = ?", del_args);
        ResourcePersister.updateResourceTimestamp(resource_id);
        
        return rtn;
    }
    public static int removePublisherTagJoin( int join_id, PubDistRecord r )
    {
        int rtn = 0;
        
        ArrayList<String> del_args = new ArrayList<String>();
        
        del_args.add( Integer.toString( join_id ) );
        
        rtn = Query.delete( "moe_publisher_tag", "pk1 = ?", del_args );
        PubDistPersister.updateResourceTimestamp( r.getDBID() );
        
        return rtn;
    }
    public static int removePublisherTagJoin(int resource_id, int tag_id)
    {
        int rtn = 0;
        
        ArrayList<String> del_args = new ArrayList<String>();
        
        del_args.add(Integer.toString(resource_id));
        del_args.add(Integer.toString(tag_id));
        
        rtn = Query.delete("moe_publisher_tag", "publisher_id = ? AND tag_id = ?", del_args);
        PubDistPersister.updateResourceTimestamp(resource_id);
        
        return rtn;
    }
    
    
    public static boolean addRecommendationTagJoin(int recommendation_id, int tag_id)
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
        cols_and_vals.put("recommendation_id", recommendation_id);
        cols_and_vals.put("tag_id", tag_id);
        if (user_results.size() > 0)
        {
            cols_and_vals.put("created_by", user_results.get(0));
        }
        if (curr_time != null)
        {
            cols_and_vals.put("created_at", curr_time);
        }
        
    // insert new record
        int pk1 = Query.insert("moe_recommendation_tag", cols_and_vals);
        
        if (pk1 >= 0)
        {
            rtn = true;
        }

        return rtn;
    }
    
    public static boolean addCriterionRecommendationJoin(int criterion_id, int recommendation_id)
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
        cols_and_vals.put("recommendation_id", recommendation_id);
        cols_and_vals.put("criterion_id", criterion_id);
        if (user_results.size() > 0)
        {
            cols_and_vals.put("created_by", user_results.get(0));
        }
        if (curr_time != null)
        {
            cols_and_vals.put("created_at", curr_time);
        }
        
    // insert new record
        int pk1 = Query.insert("moe_crit_rec", cols_and_vals);
        
        if (pk1 >= 0)
        {
            rtn = true;
        }

        return rtn;
    }
    
    public static int addResourceRelation(int resource_1_id, String resource_1_type, int resource_2_id, String resource_2_type)
    {       
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
        resource_1_type = "'" + resource_1_type + "'";
        resource_2_type = "'" + resource_2_type + "'";
        cols_and_vals.put("resource_1_id", resource_1_id);
        cols_and_vals.put("resource_2_id", resource_2_id);
        cols_and_vals.put("resource_1_type", resource_1_type);
        cols_and_vals.put("resource_2_type", resource_2_type);
        if (user_results.size() > 0)
        {
            cols_and_vals.put("created_by", user_results.get(0));
        }
        if (curr_time != null)
        {
            cols_and_vals.put("created_at", curr_time);
        }
        
    // insert new record
        int pk1 = Query.insert("moe_resource_resource", cols_and_vals);
        ResourcePersister.updateResourceTimestamp(resource_1_id);
        ResourcePersister.updateResourceTimestamp(resource_2_id);
        
        return pk1;
    }
    
}
