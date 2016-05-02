package stans.resourcerecord.dao;

import stans.resourcerecord.model.*;
import stans.db.Query;
import java.util.HashMap;

public class TagPersister {
    
    /*
     * Calls the Query.insert method to INSERT a new Tag row (where parent_id is null).
     * 
     * If successful, returns a new Tag object initialized with the new row's pk1.
     * 
     * Otherwise, returns null
     * 
     */
    public static Tag createNew (String value, int tagtype_id) {
        Tag rtn = null;
                
        value = "'" + value + "'";
        
        HashMap cols_and_vals = new HashMap();
        cols_and_vals.put("value", value);
        cols_and_vals.put("tagtype_id", tagtype_id);

        int pk1 = Query.insert("moe_tag", cols_and_vals);
        
        if (pk1 >= 0)
        {
            rtn = new Tag(pk1);
        }
        
        return rtn;
    }
    /*
     * Calls the Query.insert method to INSERT a new Tag row.
     * 
     * If successful, returns a new Tag object initialized with the new row's pk1.
     * 
     * Otherwise, returns null
     * 
     */
    public static Tag createNew (String value, int tagtype_id, int parent_id) {
        
        Tag rtn = null;
                
        value = "'" + value + "'";
        
        HashMap cols_and_vals = new HashMap();
        cols_and_vals.put("value", value);
        cols_and_vals.put("tagtype_id", tagtype_id);
        cols_and_vals.put("parent_id", parent_id);

        int pk1 = Query.insert("moe_tag", cols_and_vals);
        
        if (pk1 >= 0)
        {
            rtn = new Tag(pk1);
        }
        
        return rtn;
    }
    
    
    public static Boolean updateTag (int tag_id, String value, int tagtype_id)
    {
        Boolean rtn = true;
        
        value = "'" + value + "'";
        
        if (Query.update("moe_tag", "value", tag_id, value) == 0)
        { rtn = false; }
        if (Query.update("moe_tag", "tagtype_id", tag_id, Integer.toString(tagtype_id)) == 0)
        { rtn = false; }
        
        return rtn;
    }   
    
    
    public static Boolean updateTag (int tag_id, String value, int tagtype_id, int parent_id, int information_id)
    {
        Boolean rtn = true;
        
        value = "'" + value + "'";
        
        if (Query.update("moe_tag", "value", tag_id, value) == 0)
        { rtn = false; }
        if (Query.update("moe_tag", "tagtype_id", tag_id, Integer.toString(tagtype_id)) == 0)
        { rtn = false; }
        if (Query.update("moe_tag", "parent_id", tag_id, Integer.toString(parent_id)) == 0)
        { rtn = false; }
        if (Query.update("moe_tag", "information_id", tag_id, Integer.toString(information_id)) == 0)
        { rtn = false; }
        
        return rtn;
    }   

}
