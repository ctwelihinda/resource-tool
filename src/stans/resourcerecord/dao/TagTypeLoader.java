/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import stans.db.Enumerators.BBComparisonOperator;
import stans.resourcerecord.model.Tag;
import stans.resourcerecord.model.TagType;
import stans.db.Query;
import java.util.ArrayList;

/**
 *
 * @author peter
 */
public class TagTypeLoader {
    
    public static ArrayList<TagType> loadAll()
    {
        ArrayList<TagType> results = new ArrayList<TagType>();
        
        ArrayList<Integer> ids = Query.find("moe_tagtype", null, null);
        
        if (ids != null)
        {
            //System.out.println(ids.size() + " tagtype rows loaded");
        
            for (int id : ids)
            {
                results.add(new TagType(id));
            }
        }
        else
        {
            //System.out.println("no tagtypes found!");
        }
                
        return results;
    }
    
    public static TagType loadByType(String type)
    {        
        ArrayList<String> args = new ArrayList<String>();
        ArrayList<Integer> result_id;
        
        args.add(type);

        result_id = Query.find("moe_tagtype", "type = ?", args);
                        
        if (result_id.size() > 0)
        {
            return new TagType(result_id.get(0));
        }
        else
        {
            return new TagType(0);
        }
    }
    
    public static TagType loadByTypeAndOperator(String type, BBComparisonOperator operator)
    {        
        ArrayList<String> args = new ArrayList<String>();
        ArrayList<Integer> result_id;
        
        args.add(type);

        result_id = Query.find("moe_tagtype", "type = ?", args);
                        
        if (result_id.size() > 0)
        {
            return new TagType(result_id.get(0));
        }
        else
        {
            return new TagType(0);
        }
    }
    

    
}
