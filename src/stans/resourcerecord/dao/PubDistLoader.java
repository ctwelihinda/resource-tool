package stans.resourcerecord.dao;

import java.util.ArrayList;

import stans.db.Query;
import stans.resourcerecord.model.Resource;
import stans.resourcerecord.model.PubDistRecord;

public class PubDistLoader {
	 public static ArrayList<PubDistRecord> loadByTagID(int tag_id) {
	        ArrayList<PubDistRecord> rtn = new ArrayList<PubDistRecord>();

	        ArrayList<String> join_args = new ArrayList<String>();
	        join_args.add(Integer.toString(tag_id));

	        ArrayList<Integer> join_results = Query.find("moe_publisher_tag", "tag_id = ?", join_args);

	        for (Integer join_id : join_results) {
	            Integer r_id = (Integer) Query.select("moe_pushlisher_tag", "publisher_id", join_id);
	            if (r_id != null) {
	                rtn.add(new PubDistRecord(r_id));
	            }
	        }

	        return rtn;
	    }
	 
	 public static PubDistRecord loadByDBID(int id) {
		 PubDistRecord rtn = null;

	        ArrayList<String> args = new ArrayList<String>();
	        args.add(Integer.toString(id));

	        ArrayList<Integer> results = Query.find("moe_publisher", "pk1 = ?", args);

	        if (!results.isEmpty()) {
	            rtn = new PubDistRecord(results.get(0));
	        }

	        return rtn;
	    }
	    public static ArrayList<PubDistRecord> loadAllByTagName(String tagName) {
	        ArrayList<PubDistRecord> rtn = new ArrayList<PubDistRecord>();

	        ArrayList<String> join_args = new ArrayList<String>();
	        join_args.add(tagName);

	        ArrayList<Integer> join_results = Query.find("moe_publisher_tag", "lower(tag_name) = ?", join_args);

	        for (Integer join_id : join_results) {
	            Integer r_id = (Integer) Query.select("moe_publisher_tag", "publisher_id", join_id);
	            if (r_id != null) {
	                rtn.add(new PubDistRecord(r_id));
	            }
	        }

	        return rtn;
	    }
}
