package stans.resourcerecord.model;

import stans.resourcerecord.dao.CriterionLoader;
import stans.resourcerecord.dao.RecommendationLoader;
import stans.resourcerecord.dao.TagLoader;
import stans.db.Query;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Recommendation {

    private int db_id;
    
    private Integer resource_id;
    private String created_by;
    private Timestamp created_at;    
    private int recommended;    
    private String comments;    

    public Recommendation (int id) {
        
        db_id = id;
        
        resource_id = null;
        created_by = null;
        created_at = null;
        recommended = -1;
        comments = null;
    }

    public int getDBID ()
    {
        return db_id;
    }
    
    public int getResourceID ()
    {
        if (resource_id == null)
        {
            resource_id = (Integer) Query.select("moe_recommendation", "resource_id", db_id);
        }
        return resource_id;
    }

    public String getCreatedBy ()
    {
        if (created_by == null)
        {
            String q_result = (String) Query.select("moe_recommendation", "created_by", db_id);
            if (q_result != null)
            {
                int user_pk1 = Integer.parseInt(q_result);
                created_by = (String) Query.select("users", "user_id", user_pk1);
            }
        }
        return created_by;
    }

    public Timestamp getCreatedAt ()
    {
        if (created_at == null)
        {
            created_at = (Timestamp) Query.select("moe_recommendation", "created_at", db_id);
        }
        return created_at;
    }

    public int getRecommended ()
    {
        if (recommended == -1)
        {
            recommended = (Integer) Query.select("moe_recommendation", "recommended", db_id);
        }
        return recommended;
    }

    public String getComments ()
    {
        if (comments == null)
        {
            comments = (String) Query.select("moe_recommendation", "comments", db_id);
        }
        return comments;
    }
}
