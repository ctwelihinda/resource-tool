/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import stans.db.Enumerators.BBComparisonOperator;
import stans.resourcerecord.model.Recommendation;
import stans.db.Query;
import java.util.ArrayList;

/**
 *
 * @author peter
 */
public class RecommendationLoader {
    
    public static ArrayList<Recommendation> loadByResourceDBID(int res_db_id)
    {
        ArrayList<Recommendation> results = new ArrayList<Recommendation>();
        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> rec_ids;
        
        join_args.add(Integer.toString(res_db_id));
        rec_ids = Query.find("moe_recommendation", "resource_id = ?", join_args);
        
        for (int rec_id : rec_ids)
        {
            results.add(new Recommendation(rec_id));
        }
                
        return results;

    }
    
    public static ArrayList<Recommendation> loadAll()
    {
        ArrayList<Recommendation> results = new ArrayList<Recommendation>();
        
        ArrayList<Integer> ids = Query.find("moe_recommendation", null, null);
        
        if (ids != null)
        {
            //System.out.println(ids.size() + " recommendation rows loaded");
        
            for (int id : ids)
            {
                results.add(new Recommendation(id));
            }
        }
        else
        {
            //System.out.println("no recommendations found!");
        }
                
        return results;
    }
    
     public static ArrayList<Recommendation> loadByCreatedByUsernameAndOperator(String username, BBComparisonOperator operator) {
        ArrayList<Recommendation> result_recommendations = new ArrayList<Recommendation>();
        for (int id : Query.findWithOperator("users", "user_id", username, operator)) {
            result_recommendations.addAll(loadByCreatedBy(id));
        }
        return result_recommendations;
    }

    public static ArrayList<Recommendation> loadByFieldNameAndFieldValueAndOperator(String dbFieldName, String fieldValue, BBComparisonOperator operator) {
        ArrayList<Recommendation> result_recommendations = new ArrayList<Recommendation>();
        for (int id : Query.findWithOperator("moe_recommendation", dbFieldName, fieldValue, operator)) {
            result_recommendations.add(new Recommendation(id));
        }
        return result_recommendations;
    }

    private static ArrayList<Recommendation> loadByCreatedBy(int user_id) {
        ArrayList<Recommendation> result_recommendations = new ArrayList<Recommendation>();
        ArrayList<String> args = new ArrayList<String>();
        args.add(Integer.toString(user_id));
        ArrayList<Integer> result_ids = Query.find("moe_recommendation", "created_by = ?", args);
        for (int id : result_ids) {
            result_recommendations.add(new Recommendation(id));
        }
        return result_recommendations;
    }

}
