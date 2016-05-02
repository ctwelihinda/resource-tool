/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import stans.resourcerecord.model.Criterion;
import stans.db.Query;
import java.util.ArrayList;

/**
 *
 * @author peter
 */
public class CriterionLoader {
        
    public static ArrayList<Criterion> loadByRecommendationDBID(int rec_db_id)
    {
        ArrayList<Criterion> results = new ArrayList<Criterion>();
        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> join_ids;
        
        join_args.add(Integer.toString(rec_db_id));
        join_ids = Query.find("moe_crit_rec", "recommendation_id = ?", join_args);
        
        for (int join_id : join_ids)
        {
            results.add(new Criterion((Integer) Query.select("moe_crit_rec", "criterion_id", join_id)));
        }
                
        return results;
    }
    
    public static ArrayList<Criterion> loadByTypeDBID(int crit_type_id)
    {
        ArrayList<Criterion> results = new ArrayList<Criterion>();
        ArrayList<String> args = new ArrayList<String>();
        ArrayList<Integer> result_ids;
        
        args.add(Integer.toString(crit_type_id));
        result_ids = Query.find("moe_criterion", "criteriontype_id = ?", args);
        
        for (int this_id : result_ids)
        {
            results.add(new Criterion(this_id));
        }
        
        return results;
    }
    
    public static ArrayList<Criterion> loadAll()
    {
        ArrayList<Criterion> results = new ArrayList<Criterion>();
        
        ArrayList<Integer> ids = Query.find("moe_criterion", null, null);
        
        if (ids != null)
        {
            //System.out.println(ids.size() + " criterion rows loaded");
        
            for (int id : ids)
            {
                results.add(new Criterion(id));
            }
        }
        else
        {
            //System.out.println("no criteria found!");
        }
                
        return results;
    }

}
