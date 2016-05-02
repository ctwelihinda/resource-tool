/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.helpers;

import stans.resourcerecord.model.Tag;
import stans.db.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author peter
 */
public class TagComparator implements Comparator<Tag> 
{
    
    public TagComparator()
    {
    }
    
    @Override
    public int compare(Tag t1, Tag t2)
    {
        // old method to sort by timestamp
        /* 
        Timestamp t1_time = null;
        Timestamp t2_time = null;
        
        ArrayList<String> join_args = new ArrayList<String>();
                
        join_args.add(Integer.toString(resource_id));
        ArrayList<Integer> join_ids = Query.find("moe_resource_tag", "resource_id = ?", join_args);
        
        for (int join_id : join_ids)
        {
            if (((Integer) Query.select("moe_resource_tag", "tag_id", join_id)).equals(t1.getDBID()))
            {
                t1_time = (Timestamp) Query.select("moe_resource_tag", "created_at", join_id);
            }
            if (((Integer) Query.select("moe_resource_tag", "tag_id", join_id)).equals(t2.getDBID()))
            {
                t2_time = (Timestamp) Query.select("moe_resource_tag", "created_at", join_id);
            }
        }
        
        if ((t1_time != null) && (t2_time != null))
        {
            return t1_time.compareTo(t2_time);
        }
        else
        {
            return 1;
        }
        */
		
		String t1Simple = t1.getValue()
				.replace( "ê","e" )
				.replace( "é","e" )
				.replace( "É","E" );
		String t2Simple = t2.getValue()
				.replace( "ê","e" )
				.replace( "é","e" )
				.replace( "É","E" );
		
        return t1Simple.compareTo( t2Simple );
    }
}
