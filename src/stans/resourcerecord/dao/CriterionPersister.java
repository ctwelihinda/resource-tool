package stans.resourcerecord.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import blackboard.db.BbDatabase;
import blackboard.db.ConnectionManager;
import blackboard.db.ConnectionNotAvailableException;
import stans.resourcerecord.model.*;
import stans.db.Query;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

public class CriterionPersister {
    
    /*
     * Calls the Query.insert method to INSERT a new Criterion row (where value_text is null).
     * 
     * If successful, returns a new Criterion object initialized with the new row's pk1.
     * 
     * Otherwise, returns null
     * 
     */
    public static Criterion createNew (String value_code, int criteriontype_id) { // value_code must be one character
        
        Criterion rtn = null;
                
        value_code = "'" + value_code + "'";
        
        HashMap cols_and_vals = new HashMap();
        cols_and_vals.put("value_code", value_code);
        cols_and_vals.put("criteriontype_id", criteriontype_id);

        int pk1 = Query.insert("moe_criterion", cols_and_vals);
        
        if (pk1 >= 0)
        {
            rtn = new Criterion(pk1);
        }
        
        return rtn;
    }
    
    /*
     * Calls the Query.insert method to INSERT a new Criterion row.
     * 
     * If successful, returns a new Criterion object initialized with the new row's pk1.
     * 
     * Otherwise, returns null
     * 
     */
    public static Criterion createNew (String value_code, String value_text, int criteriontype_id) {
        
        Criterion rtn = null;
                
        value_code = "'" + value_code + "'";
        value_text = "'" + value_text + "'";
        
        HashMap cols_and_vals = new HashMap();
        cols_and_vals.put("value_code", value_code);
        cols_and_vals.put("value_text", value_text);
        cols_and_vals.put("criteriontype_id", criteriontype_id);

        int pk1 = Query.insert("moe_criterion", cols_and_vals);
        
        if (pk1 >= 0)
        {
            rtn = new Criterion(pk1);
        }
        
        return rtn;
    }
    
}
