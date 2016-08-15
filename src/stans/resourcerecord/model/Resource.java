package stans.resourcerecord.model;

import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import stans.resourcerecord.dao.FlagLoader;
import stans.resourcerecord.dao.RecommendationLoader;
import stans.resourcerecord.dao.TagLoader;
import stans.resourcerecord.helpers.TagComparator;
import stans.db.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import stans.EasyCourse;
import stans.EasyTest;
import stans.EasyUser;
import stans.resourcerecord.helpers.ValidationHelpers;


public class Resource implements Comparable<Resource> {

    private int db_id;
    private String r_number;
    private String created_by;
    private Timestamp created_at;
    private Timestamp updated_at;
    private Integer parent_id;
    private EasyTest test;
    private Boolean is_support;

    public enum SearchableResourceField{
        RNUMBER, CREATEDBY
    }
    
    public Resource() {
        this(100000); // use my test resource if no db id given
    }
    
    public Resource(int id) {
        db_id = id;

        r_number = null;
        created_by = null;
        created_at = null;
        updated_at = null;
        parent_id = null;
        is_support = null;
    }

    public int getDBID() {
        return db_id;
    }

    public void setRNumber(String r) {
        r_number = r;
    }

    public String getRNumber() {
        if (r_number == null) {
            r_number = (String) Query.select("moe_resource", "entry_id", db_id);
            if (r_number == null)
            { r_number = "none"; }
        }
        return r_number;
    }

    public Resource getParent() {
        Resource parent = null;
        if (parent_id == null) {
            parent_id = (Integer) Query.select("moe_resource", "parent_id", db_id);
            if ((parent_id != null) && (parent_id > 0)) {
                parent = new Resource(parent_id);
            }
        }
        return parent;
    }

    public ArrayList<Integer> getChildIDs() {
        ArrayList<String> args = new ArrayList<String>();
        args.add(Integer.toString(db_id));
        ArrayList<Integer> results = Query.find("moe_resource", "parent_id = ?", args);
        return results;
    }
    
    public ArrayList<Integer> getPubDistJoinIDs(){
    	ArrayList<String> args = new ArrayList<String>();
    	args.add(Integer.toString(db_id));
    	ArrayList<Integer> results = Query.find("moe_resource_publisher", "resource_id = ?", args);
    	return results;
    }
    public ArrayList<Integer> getPubDistIDs(ArrayList<Integer> join_ids){
    	ArrayList<Integer> results = new ArrayList<Integer>();
    	for (int i = 0; i < join_ids.size(); i++){
    		results.add((Integer) Query.select("moe_resource_publisher", "publisher_id", join_ids.get(i)));
    		
    	}
    	return results;
    }
	public ArrayList<Resource> getChildren() {
		ArrayList<Resource> children = new ArrayList<Resource>();
		for( int i : this.getChildIDs() ) {
			children.add( new Resource( i ) );
		}
		return children;
	}
	
    public String getCreatedBy() {
        if (created_by == null) {
            String q_result = (String) Query.select("moe_resource", "created_by", db_id);
            if (q_result != null) {
                int user_pk1 = Integer.parseInt(q_result);
                created_by = (String) Query.select("users", "user_id", user_pk1);
            }
        }
        if (created_by == null) {
            return "";
        }
        return created_by;
    }
    public String getCreatedByFullName() throws KeyNotFoundException, PersistenceException {
        if (created_by == null) {
            String q_result = (String) Query.select("moe_resource", "created_by", db_id);
            if (q_result != null) {
                int user_pk1 = Integer.parseInt(q_result);
                created_by = (String) Query.select("users", "user_id", user_pk1);
            }
        }
        if (created_by == null) {
            return "";
        }

        EasyUser cb = new EasyUser(created_by);
        String fn = cb.blackboard.getGivenName();
        String ln = cb.blackboard.getFamilyName();
        
        StringBuilder sb = new StringBuilder();
        sb.append(fn);
        sb.append(" ");
        sb.append(ln);
        sb.append(" (");
        sb.append(created_by);
        sb.append(" )");
        
        return sb.toString();
    }

    public Timestamp getCreatedAt() {
        if (created_at == null) {
            created_at = ((Timestamp) Query.select("moe_resource", "created_at", db_id));
        }
        return created_at;
    }
    
    public Timestamp getUpdatedAt() {
        if (updated_at == null) {
            updated_at = ((Timestamp) Query.select("moe_resource", "updated_at", db_id));
        }
        return updated_at;
    }

    public ArrayList<Tag> getTags() {
        ArrayList<Tag> tags = TagLoader.loadByResourceDBID(db_id);
        Collections.sort(tags, new TagComparator());
        return tags;
    }

    public ArrayList<Flag> getFlags() {
        return FlagLoader.loadAllByResourceDBID(getDBID());

    }

   
    /*
     * Returns only tags that have no parent
     *  i.e. just standalone tags and top-level group nodes
     */
    public ArrayList<Tag> getRootTags() {
        ArrayList<Tag> tags = TagLoader.loadRootOnlyByResourceDBID(db_id);
        Collections.sort(tags, new TagComparator());

        return tags;
    }

    public ArrayList<Recommendation> getRecommendations() {
        ArrayList<Recommendation> recommendations = RecommendationLoader.loadByResourceDBID(db_id);
        return recommendations;
    }

    public ArrayList<Recommendation> getPositiveRecommendations() {
        ArrayList<Recommendation> recommendations = RecommendationLoader.loadByResourceDBID(db_id);
        ArrayList<Recommendation> positiveRecommendations = new ArrayList<Recommendation>();

        for (Recommendation r : recommendations) {
            if (r.getRecommended() == 1) {
                positiveRecommendations.add(r);
            }
        }
        return positiveRecommendations;
    }

    public ArrayList<Recommendation> getNegativeRecommendations() {
        ArrayList<Recommendation> recommendations = RecommendationLoader.loadByResourceDBID(db_id);
        ArrayList<Recommendation> negativeRecommendations = new ArrayList<Recommendation>();

        for (Recommendation r : recommendations) {
            if (r.getRecommended() == 0) {
                negativeRecommendations.add(r);
            }
        }
        return negativeRecommendations;
    }

    public EasyTest getTest() throws PersistenceException {
        if (test == null) {
            try {
                test = new EasyTest(new EasyCourse("STANS1000"), getRNumber());
            } catch (Exception e) {
                //todo write error to log
                test = null;
            }
        }
        return test;
    }

    /*
     * Normally you'd use getTags and find the title in the returned list, but the title is used more often than any other tag
     * 
     */
    public String getTitle() {
        String default_title = "*no title available*";

        String title = (String) Query.select("moe_resource", "title", db_id);

        if (title == null)
        { title = default_title; }
        
        return title;
    }
    public String getTitleAndSubtitle() {
        String default_title = "*no title available*";

        String title = (String) Query.select("moe_resource", "title", db_id);
        String subtitle = (String) Query.select("moe_resource", "subtitle", db_id);
        String full_title = default_title;
        
        if ((title != null) && (!title.equals("")))
        {
            full_title = title;
            
            if ((subtitle != null) && (!subtitle.equals("")))
            {
                full_title = title + ": " + subtitle;
            }
        }
        
        return full_title;
    }
    public String getTitleAndSubtitleAndEdition() {
        String default_title = "*no title available*";

        String title = (String) Query.select("moe_resource", "title", db_id);
        String subtitle = (String) Query.select("moe_resource", "subtitle", db_id);
        String edition = (String) Query.select("moe_resource", "edition", db_id);
        String full_title = default_title;
        
        if ((title != null) && (!title.equals("")))
        {
            full_title = title;
            
            if ((subtitle != null) && (!subtitle.equals("")))
            {
                full_title = full_title + ": " + subtitle;
            }
            if ((edition != null) && (!edition.equals("")))
            {
                full_title = full_title + " (" + edition + ")";
            }
        }
        
        return full_title;
    }
    
    
    public boolean isOutOfPrint()
    {
        boolean rtn = false;
        Integer oop_status = (Integer)Query.select("moe_resource", "out_of_print", db_id);
        
        if ((oop_status != null) && (oop_status == 1))
        {
            rtn = true;
        }
        
        return rtn;
    }
    
    public int getFinalRecommendation()
    {
        Integer rtn = (Integer)Query.select("moe_resource", "recommendation", db_id);
        
        if (rtn == null)
        {
            rtn = 0;
        }
        
        return rtn;
    }
    
    public String getQuickData (String type)
    {
        String data = "";
        String column = null;
        
        if (type != null)
        {
            if (type.equals("title"))
            { column = "quick_title"; }
            if (type.contains("desc"))
            { column = "quick_description"; }
            if (type.contains("image"))
            { column = "quick_pic"; }
            if (type.equals(""))
            { column = "quick_info"; }
            
            if (column != null)
            { data = ((String) Query.select("moe_resource", column, db_id)); }
        }
        return data;
    }
    
    public boolean hideIfChild()
    {
        Integer val = (Integer)Query.select("moe_resource", "dont_show_if_child", db_id);
        
        if ((val == null) || (val == 0))    { return false;  }
        else                                { return true; }
    }
    
    public boolean isSupportMaterial()
    {
        String support_tag_id = "117372";
        
        if (is_support == null)
        {
            ArrayList<String> args = new ArrayList<String>();
            args.add(Integer.toString(db_id));
            args.add(support_tag_id);
            
            if (!Query.find("moe_resource_tag", "resource_id = ? AND tag_id = ?", args).isEmpty()) // is there a join row that joins this resource and the support material tag?
            { is_support = true; }
            else
            { is_support = false; }
        }
        return is_support;
    }
    
    public boolean isROVER()
    {
        Boolean is_rover = false;
        String is_rover_string = (String)Query.select("moe_resource", "is_rover", db_id);
        if (is_rover_string != null)
        {
            if ((ValidationHelpers.isPositiveInteger(is_rover_string)) && (Integer.parseInt(is_rover_string) > 0))
            { is_rover = true; }
        }
        return is_rover;
    }
    public boolean isCore()
    {
        Boolean is_core = false;
        String is_core_string = (String)Query.select("moe_resource", "is_core", db_id);
        if (is_core_string != null)
        {
            if ((ValidationHelpers.isPositiveInteger(is_core_string)) && (Integer.parseInt(is_core_string) > 0))
            { is_core = true; }
        }
        return is_core;
    }
    
    public ArrayList<ResourceRelationship> getResourceRelationships()
    {
        ArrayList<ResourceRelationship> rtn = new ArrayList<ResourceRelationship>();
        
        ArrayList<String> args = new ArrayList<String>();
        args.add(Integer.toString(db_id));
        args.add(Integer.toString(db_id));
        
        for (Integer result : Query.find("moe_resource_resource", "resource_1_id = ? OR resource_2_id = ?", args))
        {
            Integer id_1 = (Integer)Query.select("moe_resource_resource", "resource_1_id", result);
            Integer id_2 = (Integer)Query.select("moe_resource_resource", "resource_2_id", result);
            String type_1 = (String)Query.select("moe_resource_resource", "resource_1_type", result);
            String type_2 = (String)Query.select("moe_resource_resource", "resource_2_type", result);
            
            ResourceRelationship rr = new ResourceRelationship();
            rr.this_id = db_id;
            rr.db_id = result;
            if (id_1 == db_id)
            {
                rr.other_id = id_2;
                rr.other_type = type_2;
                rr.this_type = type_1;
            }
            else
            {
                rr.other_id = id_1;
                rr.other_type = type_1;
                rr.this_type = type_2;
            }
            rtn.add(rr);
        }
        
        return rtn;
    }
	
	@Override
	public int compareTo( Resource r ) {
		return( this.getTitle().compareTo( r.getTitle() ) );
	}
}
