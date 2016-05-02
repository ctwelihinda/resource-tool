/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import stans.resourcerecord.helpers.ValidationHelpers;
import stans.resourcerecord.model.Tag;
import stans.resourcerecord.model.TagType;
import stans.db.Query;
import java.util.ArrayList;

/**
 *
 * @author peter
 */
public class CacheLoader {
    
    
    public static ArrayList<Tag> getCachedTags (TagType type)
    {
        ArrayList<Tag> return_list = new ArrayList<Tag>();
        ArrayList<String> query_args = new ArrayList<String>();
        
        query_args.add("tag__type=" + Integer.toString(type.getDBID()));
        
        ArrayList<Integer> cache_ids = Query.find("moe_cached_list", "query_name = ?", query_args);
        if (cache_ids.size() > 0)
        {
            Integer cache_id = cache_ids.get(0);
            String id_list = (String) Query.select("moe_cached_list", "pk1_list", cache_id);
        
            for (String id : id_list.split(","))
            {
                if (ValidationHelpers.isPositiveInteger(id))
                {
                    return_list.add(new Tag(Integer.parseInt(id)));
                }
            }        
        }
        
        return return_list;
    }
    
    
    
    public static ArrayList<Integer> getCachedListByName (String query_name)
    {
        ArrayList<Integer> return_list = new ArrayList<Integer>();
        ArrayList<String> query_args = new ArrayList<String>();
        
        query_args.add(query_name);
        
        ArrayList<Integer> cache_ids = Query.find("moe_cached_list", "query_name = ?", query_args);
        if (cache_ids.size() > 0)
        {
            Integer cache_id = cache_ids.get(0);
            String id_list = (String) Query.select("moe_cached_list", "pk1_list", cache_id);
        
            for (String id : id_list.split(","))
            {
                if (ValidationHelpers.isPositiveInteger(id))
                {
                    return_list.add(Integer.parseInt(id));
                }
            }        
        }
        
        return return_list;
    }
}
