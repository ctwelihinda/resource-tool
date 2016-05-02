/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.model;

import stans.db.Query;
import java.sql.Timestamp;

/**
 *
 * @author peter
 */
public class Flag {
    
    private int db_id;
    private Integer resource_id;
    private String username;
    private Boolean has_comments;
    private Integer reason_code;
    private Timestamp created_at;

    public Flag (int id) {
        
        db_id = id;
        
        resource_id = null;
        username = null;
        created_at = null;
        reason_code = null;
        has_comments = null;
    }

    public int getDBID ()
    {
        return db_id;
    }
    
    public int getReasonCode ()
    {
        if (reason_code == null)
        {
            reason_code = (Integer) Query.select("moe_resource_flag", "reason_code", db_id);
        }
        return reason_code;
    }

    public String getUsername ()
    {
        if (username == null)
        {
            String q_result = (String) Query.select("moe_resource_flag", "user_id", db_id);
            if (q_result != null)
            {
                int user_pk1 = Integer.parseInt(q_result);
                username = (String) Query.select("users", "user_id", user_pk1);
            }
        }
        return username;
    }

    public Timestamp getCreatedAt ()
    {
        if (created_at == null)
        {
            created_at = ((Timestamp) Query.select("moe_resource_flag", "created_at", db_id));
        }
        return created_at;
    }
    
     public int getResourceID ()
    {
        if (resource_id == null)
        {
            resource_id = (Integer) Query.select("moe_resource_flag", "resource_id", db_id);
        }
        return resource_id;
    }
    
    public String getComments ()
    {
        if (has_comments == null)
        {
            String q_result = (String) Query.select("moe_resource_flag", "comments", db_id);
            if (q_result != null)
            {
                has_comments = true;
                return q_result;
            }
            else
            {
                has_comments = false;
                return "";
            }
        }
        else if (has_comments)
        {
            return (String) Query.select("moe_resource_flag", "comments", db_id);
        }
        else
        {
            return "";
        }
    }
    
}
