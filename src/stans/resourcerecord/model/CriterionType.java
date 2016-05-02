/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.model;

import stans.db.Query;

/**
 *
 * @author peter
 */
public class CriterionType {
    
    private int db_id;
    
    private String response_type;
    private String name;
    private String description;
    
    public CriterionType (int id)
    {
        db_id = id;
        
        response_type = null;
        name = null;
        description = null;
    }

    public int getDBID ()
    {
        return db_id;
    }

    public void setResponseType (String t)
    {
        response_type = t;
    }
    public String getResponseType ()
    {
        if (response_type == null)
        {
            response_type = (String) Query.select("moe_criteriontype", "response_type", db_id);
        }
        return response_type;
    }

    public void setName (String n)
    {
        name = n;
    }
    public String getName ()
    {
        if (name == null)
        {
            name = (String) Query.select("moe_criteriontype", "name", db_id);
        }
        return name;
    }
        
    public void setDescription (String d)
    {
        description = d;
    }
    public String getDescription ()
    {
        if (description == null)
        {
            description = (String) Query.select("moe_criteriontype", "description", db_id);
        }
        return description;
    }
    
}
