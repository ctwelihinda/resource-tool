/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import stans.db.Enumerators.BBComparisonOperator;
import stans.resourcerecord.helpers.TagComparator;
import stans.resourcerecord.model.Tag;
import stans.resourcerecord.model.TagType;
import stans.db.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author peter
 */
public class TagLoader {

    public static ArrayList<Tag> loadByResourceDBID(int res_db_id) {
        ArrayList<Tag> results = new ArrayList<Tag>();
        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> join_ids;

        join_args.add(Integer.toString(res_db_id));
        join_ids = Query.find("moe_resource_tag", "resource_id = ?", join_args);

        for (int join_id : join_ids) {
            results.add(new Tag((Integer) Query.select("moe_resource_tag", "tag_id", join_id)));
        }

        return results;
    }
    
    public static ArrayList<Tag> loadRootOnlyByPublisherDBID(int pub_db_id) {
        ArrayList<Tag> results = new ArrayList<Tag>();
        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> join_ids;

        join_args.add(Integer.toString(pub_db_id));
        join_ids = Query.find("moe_publisher_tag", "publisher_id = ?", join_args);

        for (int join_id : join_ids) {
            if ( ( ( Integer )Query.select( "moe_publisher_tag", "parent_id", join_id ) )==0 ) {
                results.add(new Tag( ( Integer )Query.select( "moe_publisher_tag", "tag_id", join_id ), join_id ) );
            }
        }

        return results;
    }

    /*
     * Returns only tags that have no parent
     *  i.e. just standalone tags and top-level group nodes
     */
    public static ArrayList<Tag> loadRootOnlyByResourceDBID(int res_db_id) {
        ArrayList<Tag> results = new ArrayList<Tag>();
        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> join_ids;

        join_args.add(Integer.toString(res_db_id));
        join_ids = Query.find("moe_resource_tag", "resource_id = ?", join_args);

        for (int join_id : join_ids) {
            if ( ( ( Integer )Query.select( "moe_resource_tag", "parent_id", join_id ) )==0 ) {
                results.add(new Tag( ( Integer )Query.select( "moe_resource_tag", "tag_id", join_id ), join_id ) );
            }
        }

        return results;
    }

    public static ArrayList<Tag> loadChildrenByDBID( int res_db_id, int tag_db_id ) {
        ArrayList<Tag> results = new ArrayList<Tag>();

        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> join_ids;

        join_args.add(Integer.toString(res_db_id));
        join_args.add(Integer.toString(tag_db_id));
        join_ids = Query.find("moe_resource_tag", "resource_id = ? AND parent_id = ?", join_args);

        for (int join_id : join_ids) {
            results.add(new Tag( (Integer)Query.select( "moe_resource_tag", "tag_id", join_id ), join_id ) );
        }

        return results;
    }

    public static ArrayList<Tag> loadByRecommendationDBID(int rec_db_id) {
        ArrayList<Tag> results = new ArrayList<Tag>();
        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> join_ids;

        join_args.add(Integer.toString(rec_db_id));
        join_ids = Query.find("moe_recommendation_tag", "recommendation_id = ?", join_args);

        for (int join_id : join_ids) {
            results.add(new Tag((Integer) Query.select("moe_recommendation_tag", "tag_id", join_id)));
        }

        return results;
    }

    public static ArrayList<Tag> loadByType (TagType type) {
        ArrayList<Tag> results = new ArrayList<Tag>();
        ArrayList<String> args = new ArrayList<String>();
        ArrayList<Integer> result_ids;

        args.add(Integer.toString(type.getDBID()));
        result_ids = Query.find("moe_tag", "tagtype_id = ?", args);

        for (int id : result_ids) {
            results.add(new Tag(id));
        }
        Collections.sort(results, new TagComparator());

        return results;
    }

    public static ArrayList<Tag> loadByTypeName (String type) {
        ArrayList<Tag> results = new ArrayList<Tag>();
        ArrayList<String> args = new ArrayList<String>();
        ArrayList<Integer> result_ids;

        args.add(Integer.toString(TagTypeLoader.loadByType(type).getDBID()));
        result_ids = Query.find("moe_tag", "tagtype_id = ?", args);

        for (int id : result_ids) {
            results.add(new Tag(id));
        }
        Collections.sort(results, new TagComparator());

        return results;
    }
    
    public static ArrayList<Tag> loadByTypeID(int type_db_id) {
        ArrayList<Tag> results = new ArrayList<Tag>();
        ArrayList<String> args = new ArrayList<String>();
        ArrayList<Integer> result_ids;

        args.add(Integer.toString(type_db_id));
        result_ids = Query.find("moe_tag", "tagtype_id = ?", args);

        for (int id : result_ids) {
            results.add(new Tag(id));
        }
        Collections.sort(results, new TagComparator());

        return results;
    }

    public static ArrayList<Tag> loadAll() {
        ArrayList<Tag> results = new ArrayList<Tag>();

        ArrayList<Integer> ids = Query.find("moe_tag", null, null);

        if (ids != null) {
            //System.out.println(ids.size() + " tag rows loaded");

            for (int id : ids) {
                results.add(new Tag(id));
            }
        } else {
            //System.out.println("no tags found!");
        }

        return results;
    }

    public static Tag loadByTagTypeAndTagValue(String value, TagType tagtype) {
        ArrayList<String> args = new ArrayList<String>(Arrays.asList(
            value,
            Integer.toString(tagtype.getDBID())
        ));

        ArrayList<Integer> result_ids = Query.find("moe_tag", "value = ? AND tagtype_id = ?", args);
        if (!result_ids.isEmpty()) {
            return new Tag(result_ids.get(0));
        }
        else
        { return new Tag(0); }
    }
    
    public static ArrayList<Tag> loadByTagTypeAndTagValueAndOperator(TagType tagtype, String tagValue, BBComparisonOperator operator) {
        ArrayList<Integer> result_ids = new ArrayList<Integer>();
        ArrayList<Tag> result_tags = new ArrayList<Tag>();

        result_ids = Query.findWithOperator("moe_tag", "value", tagValue, operator);
        if (result_ids != null) {
            //System.out.println("Tag Ids Found: " + result_ids.size());
            for (int id : result_ids) {
                Tag temp_tag = new Tag(id);
                if (temp_tag.getTypeID() == tagtype.getDBID()) {
                    result_tags.add(temp_tag);
                }
            }
        }
        return result_tags;
    }
    
    public static ArrayList<Tag> loadByTagTypeNameAndTagValueAndOperator(String tagTypeName, String tagValue, BBComparisonOperator operator) {
        ArrayList<Tag> result_tags= new ArrayList<Tag>();

        TagType tagType = TagTypeLoader.loadByType(tagTypeName);
        if (tagType != null) {
           // System.out.println("TagType found: " + tagType.getType());
            result_tags = TagLoader.loadByTagTypeAndTagValueAndOperator(tagType, tagValue, operator);
           // System.out.println("Tags found: " + result_tags.size());            
        }
        return result_tags;
    }

 
}
