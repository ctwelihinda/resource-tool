package stans.resourcerecord.model;

import stans.resourcerecord.dao.TagLoader;
import stans.resourcerecord.helpers.TagComparator;
import stans.db.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Tag {

    private int db_id;

    private ArrayList<Integer> resource_ids;
    private String type;
    private String value;
    private Tag parent;
	private Integer joinID;		// the id of the join table row for this tag's association to a particular resource. Used occasionally, for deleting joins.

    public Tag( int id ) {
        db_id = id;
		joinID = null;

        resource_ids = null;
        type = null;
        value = null;
        parent = null;
    }
    public Tag( int id, int join_id ) {
        db_id = id;
		joinID = join_id;
		
        resource_ids = null;
        type = null;
        value = null;
        parent = null;
    }

    public int getDBID() {
        return db_id;
    }

    public void setValue(String v) {
        value = v;
    }

    public String getValue() {
        if (value == null) {
            if ((String) Query.select("moe_tag", "value", db_id) != null) {
                value = (String) Query.select("moe_tag", "value", db_id);
            } else {
                value = "No value given";
            }
        }
        return value;
    }

	// might return null if joinID has not been set
	public Integer getJoinID() {
		return joinID;
	}
	
    public ArrayList<Integer> getResourceIDs() {
        if (resource_ids == null) {
            resource_ids = new ArrayList<Integer>();
            ArrayList<String> join_args = new ArrayList<String>();
            join_args.add(Integer.toString(getDBID()));
            ArrayList<Integer> join_ids = Query.find("moe_resource_tag", "tag_id = ?", join_args);

            for (int join_id : join_ids) {

                int temp_id = (Integer) Query.select("moe_resource_tag", "resource_id", join_id);
                if (!resource_ids.contains(temp_id)) {
                    resource_ids.add(temp_id);
                }
            }
        }
        //System.out.println("Found: " + resource_ids.size() + " resource IDs attached to tag: " + getType() + "==" + getValue());
        return resource_ids;
    }

    public void setType(String t) {
        type = t;
    }

    public Integer getTypeID() {
        Integer rtn;
        rtn = (Integer) Query.select("moe_tag", "tagtype_id", db_id);

        return rtn;
    }

    public String getType() {
        if (type == null) {
            if (Query.select("moe_tag", "tagtype_id", db_id) != null) {
                int tagtype_pk1 = (Integer) Query.select("moe_tag", "tagtype_id", db_id);
                type = (String) Query.select("moe_tagtype", "type", tagtype_pk1);
            } else {
                type = "Unknown type";
            }
        }
        return type;
    }

    public void setInfo(String info) {

        HashMap cols_and_vals = new HashMap();
        cols_and_vals.put("information", info);

        Integer new_info_pk1 = Query.insert("moe_tag_information", cols_and_vals);
        if ((new_info_pk1 != null) && (new_info_pk1 > 0)) {
            Query.update("moe_tag", "information_id", db_id, Integer.toString(new_info_pk1));
        }
    }

    public int getInfoID() {
        Integer rtn = null;
        rtn = (Integer) Query.select("moe_tag", "information_id", db_id);

        return rtn;
    }

    public String getInfo() {
        String rtn = null;

        rtn = (String) Query.select("moe_tag_information", "information", this.getInfoID());

        return rtn;
    }
    /* 
     * Needs to be updated. Originally just used parent_id column in moe_tag table, 
     * but this has been superseded by the parent_id in the moe_resource_tag join table
     public void setParent (Tag p)
     {
     parent = p;
     }
     public Tag getParent ()
     {
     if (parent == null)
     {
     parent = new Tag((Integer) Query.select("moe_tag", "parent_id", db_id));
     }
     return parent;
     }
     */
    public ArrayList<Tag> getPubChildren(int publisher_id) {
        ArrayList<Tag> rtn = TagLoader.loadPubChildByDBID( publisher_id, this.db_id );
        Collections.sort( rtn, new TagComparator() );

        return rtn;
    }
    public ArrayList<Tag> getChildren(int resource_id) {
        ArrayList<Tag> rtn = TagLoader.loadChildrenByDBID( resource_id, this.db_id );
        Collections.sort( rtn, new TagComparator() );

        return rtn;
    }

    public String getJoinCreatedBy (int res_id)
    {
        String created_by = "unknown";
        String user_id = null;
        
        String constraints = "resource_id = ? AND tag_id = ?";
        ArrayList<String> args = new ArrayList<String>();
        args.add(Integer.toString(res_id));
        args.add(Integer.toString(db_id));
        
        ArrayList<Integer> join_ids = Query.find("moe_resource_tag", constraints, args);
        if (!join_ids.isEmpty())
        {
             user_id = (String) Query.select("moe_resource_tag", "created_by", join_ids.get(0));
        }
        
        if (user_id != null) {
            int user_pk1 = Integer.parseInt(user_id);
            created_by = (String) Query.select("users", "user_id", user_pk1);
        } else {
            created_by = "unknown";
            user_id = null;
            
            constraints = "publisher_id = ? AND tag_id = ?";
            args = new ArrayList<String>();
            args.add(Integer.toString(res_id));
            args.add(Integer.toString(db_id));
            
            join_ids = Query.find("moe_publisher_tag", constraints, args);
            if (!join_ids.isEmpty())
            {
                 user_id = (String) Query.select("moe_publisher_tag", "created_by", join_ids.get(0));
            }
            
            if (user_id != null) {
                int user_pk1 = Integer.parseInt(user_id);
                created_by = (String) Query.select("users", "user_id", user_pk1);
            }
        }
            
        return created_by;
    }

    public Timestamp getJoinCreatedAt (int res_id)
    {
        Timestamp created_at = new Timestamp(0);
        
        String constraints = "resource_id = ? AND tag_id = ?";
        ArrayList<String> args = new ArrayList<String>();
        args.add(Integer.toString(res_id));
        args.add(Integer.toString(db_id));
        
        ArrayList<Integer> join_ids = Query.find("moe_resource_tag", constraints, args);
        if (!join_ids.isEmpty())
        {
             created_at = ((Timestamp) Query.select("moe_resource_tag", "created_at", join_ids.get(0)));
        } else {
            constraints = "publisher_id = ? AND tag_id = ?";
            args = new ArrayList<String>();
            args.add(Integer.toString(res_id));
            args.add(Integer.toString(db_id));
            
            join_ids = Query.find("moe_publisher_tag", constraints, args);
            if (!join_ids.isEmpty())
            {
                 created_at = ((Timestamp) Query.select("moe_publisher_tag", "created_at", join_ids.get(0)));
            } 
        }
        
        return created_at;
    }
}
