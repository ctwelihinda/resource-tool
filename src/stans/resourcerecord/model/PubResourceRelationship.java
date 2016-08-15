package stans.resourcerecord.model;

import java.sql.Timestamp;

import stans.db.Query;

public class PubResourceRelationship {
	private int join_id;
	private int publisher_id;
	private String publisher_name;
	private String submitted;
	private Timestamp created_at;
	private Timestamp updated_at;
	private String created_by;
	private String type;
	
	public PubResourceRelationship(){
		join_id = -1;
		publisher_id = -1;
		publisher_name = null;
		submitted = null;
		type = null;
	}
	public PubResourceRelationship(int join_id){
		this.join_id = join_id;
		publisher_id = -1;
		publisher_name = null;
		submitted = null;
		type = null;
	}
	
    public String getType(){
	    if (type == null && join_id != -1) {
	    
	    type = (String) Query.select("moe_resource_publisher", "pubdist_type", join_id);
	    }
	    if (type == null) {
	       return "";
	    } else if (type.equals("0")){
	    	type = "Publisher";
	    } else if (type.equals("1")){
	    	type = "Distributor";
	    }
	       return type;
    }
    public String getCreatedBy(){
        
	    if (created_by == null) {
	    String q_result = (String) Query.select("moe_resource_publisher", "created_by", join_id);
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
    
    public Timestamp getCreatedAt() {
    	Timestamp rtnStamp = new Timestamp(0);
       
            created_at = ((Timestamp) Query.select("moe_resource_publisher", "created_at", join_id));
            if (created_at == null) {
            return rtnStamp;
            }
        return created_at;
    }
    
    public Timestamp getUpdatedAt() {
    	Timestamp rtnStamp = new Timestamp(0);
    	 updated_at = ((Timestamp) Query.select("moe_resource_publisher", "updated_at", join_id));
        if (updated_at == null) {
           return rtnStamp;
        }
        return updated_at;
    }
    
    public String isSubmittedBy(){
    	String result = "unknown";
    	if(submitted == null && join_id != -1){
    		submitted = (String) Query.select("moe_resource_publisher", "submitted_by", join_id);
    		System.out.println("submitted " + submitted);
    	}
	    	if( submitted != null && submitted.equals("0") ){
	    		result = "";
	    	}
	    	else if (submitted != null && submitted.equals("1")){
	    		result = "\tx";
	    		}
    	
    	return result;
    }
    public String getPubName(){
    	if(publisher_id != -1 && join_id != -1) {
    		publisher_name = (String) Query.select("moe_publisher", "name", publisher_id);
    	} 
    	
    	if(publisher_id == -1 || publisher_name == null){
    		publisher_name = "";
    	}
    	
    	return publisher_name;
    }
    
    public int getPubId(){
    	//String	q_string = null; 
    	int pub_id = 0;
    	
    	if(publisher_id == -1 && join_id != -1) {
    		if( Query.select("moe_resource_publisher", "publisher_id", join_id) != null)
    			pub_id = (Integer) Query.select("moe_resource_publisher", "publisher_id", join_id);
    	}
    	if (pub_id > 0){
    		publisher_id = pub_id;
    	}
    	return publisher_id;
    }
}
