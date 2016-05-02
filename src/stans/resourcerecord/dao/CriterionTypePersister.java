/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import stans.resourcerecord.model.CriterionType;
import stans.db.Query;
import java.util.HashMap;

/**
 *
 * @author peter
 */
public class CriterionTypePersister {
    
    /*
     * Calls the Query.insert method to INSERT a new CriterionType row.
     * 
     * If successful, returns a new CriterionType object initialized with the new row's pk1.
     * 
     * Otherwise, returns null
     * 
     */
    public static CriterionType createNew (String response_type, String name, String description) {
        
        CriterionType rtn = null;
                
        response_type = "'" + response_type + "'";
        name = "'" + name + "'";
        description = "'" + description + "'";
        
        HashMap cols_and_vals = new HashMap();
        cols_and_vals.put("response_type", response_type);
        cols_and_vals.put("name", name);
        cols_and_vals.put("description", description);

        int pk1 = Query.insert("moe_criteriontype", cols_and_vals);
        
        if (pk1 >= 0)
        {
            rtn = new CriterionType(pk1);
        }
        
        return rtn;
    }
    
}
