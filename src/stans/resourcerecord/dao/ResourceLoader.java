/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import blackboard.persist.PersistenceException;
import stans.db.Enumerators.BBComparisonOperator;
import stans.resourcerecord.model.Flag;
import stans.resourcerecord.model.Recommendation;
import stans.resourcerecord.model.Resource;
import stans.resourcerecord.model.ResourceText;
import stans.resourcerecord.model.Tag;
import stans.db.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author peter
 */
public class ResourceLoader {

    public static Resource loadByRNumber(String r_number) {
        Resource rtn = null;

        ArrayList<String> args = new ArrayList<String>();
        args.add(r_number);

        ArrayList<Integer> results = Query.find("moe_resource", "entry_id = ?", args);
      
        if (!results.isEmpty()) {
        	
            rtn = new Resource(results.get(0));
        	
        }

        return rtn;
    }

    public static ArrayList<Resource> loadByTagID(int tag_id) {
        ArrayList<Resource> rtn = new ArrayList<Resource>();

        ArrayList<String> join_args = new ArrayList<String>();
        join_args.add(Integer.toString(tag_id));

        ArrayList<Integer> join_results = Query.find("moe_resource_tag", "tag_id = ?", join_args);

        for (Integer join_id : join_results) {
            Integer r_id = (Integer) Query.select("moe_resource_tag", "resource_id", join_id);
            if (r_id != null) {
                rtn.add(new Resource(r_id));
            }
        }

        return rtn;
    }

    public static ArrayList<Resource> loadRecent(int limit) {
        //System.out.println("Loading recent resources");
        ArrayList<Resource> results = new ArrayList<Resource>();

        ArrayList<String> ids = Query.freefind("SELECT * FROM (SELECT pk1 FROM moe_resource ORDER BY created_at desc) WHERE rownum <= " + String.valueOf(limit) + " ");
        //ArrayList<String> ids = Query.freefind("SELECT pk1 FROM moe_resource ORDER BY moe_resource.created_at desc WHERE ROWNUM <= " + String.valueOf(limit) + " ");

        if (ids != null) {
            //System.out.println(ids.size() + " resource rows loaded");

            for (String id : ids) {
                results.add(new Resource(Integer.parseInt(id)));
            }
        } else {
            //System.out.println("no resources found!");
        }

        return results;
    }

    public static Resource loadByDBID(int id) {
        Resource rtn = null;

        ArrayList<String> args = new ArrayList<String>();
        args.add(Integer.toString(id));

        ArrayList<Integer> results = Query.find("moe_resource", "pk1 = ?", args);

        if (!results.isEmpty()) {
            rtn = new Resource(results.get(0));
        }

        return rtn;
    }

    public static ArrayList<Resource> loadAll() {
        ArrayList<Resource> results = new ArrayList<Resource>();

/*        ArrayList<Integer> ids = Query.find("moe_resource", null, null);

        if (ids != null) {
            //System.out.println(ids.size() + " resource rows loaded");

            for (int id : ids) {
                results.add(new Resource(id));
            }
        } else {
            //System.out.println("no resources found!");
        }
*/
        return results;
    }

    public static ArrayList<Resource> loadAllByTagName(String tagName) {
        ArrayList<Resource> rtn = new ArrayList<Resource>();

        ArrayList<String> join_args = new ArrayList<String>();
        join_args.add(tagName);

        ArrayList<Integer> join_results = Query.find("moe_resource_tag", "lower(tag_name) = ?", join_args);

        for (Integer join_id : join_results) {
            Integer r_id = (Integer) Query.select("moe_resource_tag", "resource_id", join_id);
            if (r_id != null) {
                rtn.add(new Resource(r_id));
            }
        }

        return rtn;
    }

    public static ArrayList<Resource> loadByCreatedBy(int user_id) {
        ArrayList<Resource> result_resources = new ArrayList<Resource>();
        ArrayList<String> args = new ArrayList<String>();
        args.add(Integer.toString(user_id));

        ArrayList<Integer> result_ids = Query.find("moe_resource", "created_by = ?", args);

        for (int id : result_ids) {
            result_resources.add(new Resource(id));
        }
        return result_resources;
    }

    public static ArrayList<Resource> loadByCreatedByUsernameAndOperator(String username, BBComparisonOperator operator) throws PersistenceException {
        ArrayList<Resource> result_resources = new ArrayList<Resource>();
        for (int id : Query.findWithOperator("users", "user_id", username, operator)) {
            result_resources.addAll(loadByCreatedBy(id));
        }
        return result_resources;
    }

    public static ArrayList<Resource> loadByFieldNameAndFieldValueAndOperator(String dbFieldName, String fieldValue, BBComparisonOperator operator) {
        ArrayList<Resource> result_resources = new ArrayList<Resource>();
        for (int id : Query.findWithOperator("moe_resource", dbFieldName, fieldValue, operator)) {
            result_resources.add(new Resource(id));
        }
        return result_resources;
    }

    public static ArrayList<Resource> loadByTag(Tag tag) {

        ArrayList<Resource> results = new ArrayList<Resource>();

        for (int id : tag.getResourceIDs()) {
            results.add(new Resource(id));
        }

        return results;
    }

    public static ArrayList<Resource> loadByTags(ArrayList<Tag> tags) {
        ArrayList<Resource> results = new ArrayList<Resource>();
        for (Tag rt : tags) {
            results.addAll(loadByTag(rt));
        }
        return results;
    }

    public static ArrayList<Resource> loadByFlag(Flag flag) {
        ArrayList<Resource> results = new ArrayList<Resource>();
        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> join_ids;

        try{
            join_args.add(Integer.toString(flag.getResourceID()));
            join_ids = Query.find("moe_resource", "pk1 = ?", join_args);

            for (int join_id : join_ids) {
                results.add(new Resource(join_id));
            }
        } catch(Exception e) {
            // flag without a parent reosurce
            System.out.println("Warning: skipping flag ID:" + flag.getDBID() + ", no parent resource attached");
            System.out.println("error: " + e.toString());
            System.out.println("stack trace:");
            for(StackTraceElement st : e.getStackTrace()){
                System.out.println(st.toString());
            }
        }
        return results;
    }

    public static ArrayList<Resource> loadByFlags(ArrayList<Flag> flags) {
        ArrayList<Resource> results = new ArrayList<Resource>();
        for (Flag f : flags) {
            results.addAll(loadByFlag(f));
        }
        return results;
    }

    public static ArrayList<Resource> loadByRecommendation(Recommendation recommendation) {
        ArrayList<Resource> results = new ArrayList<Resource>();
        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> join_ids;

        join_args.add(Integer.toString(recommendation.getResourceID()));
        join_ids = Query.find("moe_resource", "pk1 = ?", join_args);

        for (int join_id : join_ids) {
            results.add(new Resource(join_id));
        }

        return results;
    }

    public static ArrayList<Resource> loadByRecommendations(ArrayList<Recommendation> recommendations) {
        ArrayList<Resource> results = new ArrayList<Resource>();
        for (Recommendation f : recommendations) {
            results.addAll(loadByRecommendation(f));
        }
        return results;
    }

    public static ArrayList<Resource> loadByResourceText(ResourceText resourceText) {
        ArrayList<Resource> results = new ArrayList<Resource>();
        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> join_ids;

        join_args.add(Integer.toString(resourceText.getResourceID()));
        join_ids = Query.find("moe_resource", "pk1 = ?", join_args);

        for (int join_id : join_ids) {
            results.add(new Resource(join_id));
        }

        return results;
    }

    public static ArrayList<Resource> loadByResourceTexts(ArrayList<ResourceText> resourceTexts) {
        ArrayList<Resource> results = new ArrayList<Resource>();
        for (ResourceText rt : resourceTexts) {
            results.addAll(loadByResourceText(rt));
        }
        return results;
    }

    public static ArrayList<Resource> loadByTagIntersection (ArrayList<Tag> tags) {
        
        HashMap<Integer, Integer> results = new HashMap<Integer, Integer>();
        
        ArrayList<Resource> return_list = new ArrayList<Resource>();
        
        int this_count = 1;
        for (Tag t : tags)
        {
            ArrayList<String> constraints = new ArrayList<String>();
            constraints.add(Integer.toString(t.getDBID()));
            for (Integer id : Query.findAndSelectIDs("moe_resource_tag", "resource_id", "tag_id = ?", constraints))
            {
                Integer found_count = results.get(id);
                if (found_count == null)    { found_count = 0; }
                if (
                        ((found_count == 0) && (this_count == 1)) ||    // only worry about setting this value if we're on the first tag
                        (found_count == this_count-1)                   // only update the value if it was found for the last tag as well
                )
                { results.put(id, this_count); }
            }
            this_count++;
        }
        
        Iterator it = results.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<Integer, Integer> this_set = (Map.Entry<Integer,Integer>)it.next();
            if (this_set.getValue() == tags.size())
            { return_list.add(new Resource(this_set.getKey())); }
        }
        
        return return_list;
    }

    public static ArrayList<Resource> loadByTagGroupContent (ArrayList<Tag> tags_in_group) {
        
        System.out.println("Searching for the following tags:");
        System.out.println("---------------------------------");
        for (Tag t : tags_in_group)
        {
            System.out.println("    *" + t.getType() + ": " + t.getValue());
        }
        System.out.println("---------------------------------");
        
        HashMap<Integer, Integer> group_ids = new HashMap<Integer, Integer>(); 
        
        HashMap<Integer, Boolean> return_hash = new HashMap<Integer, Boolean>();
        ArrayList<Resource> return_list = new ArrayList<Resource>();
        
        int num_tags = tags_in_group.size();
        
        for (Tag t : tags_in_group)
        {
            HashMap<Integer, Boolean> group_hash_tracker = new HashMap<Integer, Boolean>(); // to avoid duplicates (if a tag is added twice to the same group)
            
            ArrayList<String> constraints = new ArrayList<String>();
            constraints.add(Integer.toString(t.getDBID()));
            for (Integer join_id : Query.find("moe_resource_tag", "tag_id = ?", constraints))
            {
            	//System.out.println("join id " + join_id);
                //Integer resource_id = (Integer) Query.select("moe_resource_tag", "resource_id", join_id);
                Integer group_id = (Integer) Query.select("moe_resource_tag", "parent_id", join_id);
                //System.out.println("group " + group_id);
                
                if (group_id != null)
                {
                    Integer group_found_count = group_ids.get(group_id);
                    if (group_found_count == null)    { group_found_count = 0; }
                    
                    if ((group_hash_tracker.get(group_id) == null) || (!group_hash_tracker.get(group_id)))
                    {
                        group_ids.put(group_id, group_found_count+1);
                        group_hash_tracker.put(group_id, true);
                       // System.out.println("put group " + group_id + " on the hash map");
                    }
                }
            }
        }
        
        Iterator it = group_ids.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<Integer, Integer> this_group = (Map.Entry<Integer,Integer>)it.next();
            if (this_group.getValue() == num_tags) // if all the tags (i.e. the same number of them) are part of this group...
            { 
                ArrayList<String> constraints = new ArrayList<String>();
                constraints.add(Integer.toString(this_group.getKey()));
                for (Integer res_id : Query.findAndSelectIDs("moe_resource_tag", "resource_id", "tag_id = ?", constraints))
                {
                    if ((return_hash.get(res_id) == null) || (!return_hash.get(res_id)))
                    {
                        return_hash.put(res_id, true);
                        return_list.add(new Resource(res_id));
                    }
                }
            }
        }
        
        return return_list;
    }
    
    
    public static ArrayList<Resource> loadWithTests() throws PersistenceException {
        ArrayList<Resource> results = new ArrayList<Resource>();
        for (Resource r : loadAll()) {
            if (r.getTest() != null) {
                results.add(r);
            }
        }
        return results;
    }

    public static ArrayList<Resource> loadWithoutTests() throws PersistenceException {
        ArrayList<Resource> results = new ArrayList<Resource>();
        for (Resource r : loadAll()) {
            if (r.getTest() == null) {
                results.add(r);
            }
        }
        return results;
    }

}
