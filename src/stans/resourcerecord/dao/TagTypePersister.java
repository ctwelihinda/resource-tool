/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import stans.resourcerecord.model.TagType;
import stans.db.Query;
import java.util.HashMap;

/**
 *
 * @author peter
 */
public class TagTypePersister {
    
    /*
     * Calls the Query.insert method to INSERT a new TagType row (where description is null).
     * 
     * If successful, returns a new TagType object initialized with the new row's pk1.
     * 
     * Otherwise, returns null
     * 
     */
    public static TagType createNew (String type) {
        
        TagType rtn = null;
                
        type = "'" + type + "'";
        
        HashMap cols_and_vals = new HashMap();
        cols_and_vals.put("type", type);

        int pk1 = Query.insert("moe_tagtype", cols_and_vals);
        
        if (pk1 >= 0)
        {
            rtn = new TagType(pk1);
        }
        
        return rtn;
    }
    
    /*
     * Calls the Query.insert method to INSERT a new TagType row (with a description).
     * 
     * If successful, returns a new TagType object initialized with the new row's pk1.
     * 
     * Otherwise, returns null
     * 
     */
    public static TagType createNew (String type, String description) {
        
        TagType rtn = null;
                
        type = "'" + type + "'";
        description = "'" + description + "'";
        
        HashMap cols_and_vals = new HashMap();
        cols_and_vals.put("type", type);
        cols_and_vals.put("description", description);

        int pk1 = Query.insert("moe_tagtype", cols_and_vals);
        
        if (pk1 >= 0)
        {
            rtn = new TagType(pk1);
        }
        
        return rtn;
    }
    
    
    public static Boolean updateTagType (int tagtype_id, String name)
    {
        Boolean rtn = true;
        
        name = "'" + name + "'";
        
        if (Query.update("moe_tagtype", "type", tagtype_id, name) == 0)
        { rtn = false; }
        
        return rtn;
    }   
}
