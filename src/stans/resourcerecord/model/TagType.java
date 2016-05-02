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
public class TagType {
    
    private int db_id;
    
    private String type;
    private String description;
    
    public TagType (int id)
    {
        db_id = id;
        
        type = null;
        description = null;
    }

    public int getDBID ()
    {
        return db_id;
    }

    public void setType (String t)
    {
        type = t;
    }
    public String getType ()
    {
        if (type == null)
        {
            type = (String) Query.select("moe_tagtype", "type", db_id);
        }
        return type;
    }
        
    public void setDescription (String d)
    {
        description = d;
    }
    public String getDescription ()
    {
        if (description == null)
        {
            description = (String) Query.select("moe_tagtype", "description", db_id);
        }
        return description;
    }
    
}
