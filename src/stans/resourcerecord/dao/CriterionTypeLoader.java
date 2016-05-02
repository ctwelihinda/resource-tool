/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import stans.resourcerecord.model.CriterionType;
import stans.db.Query;
import java.util.ArrayList;

/**
 *
 * @author peter
 */
public class CriterionTypeLoader {
    
    public static ArrayList<CriterionType> loadAll()
    {
        ArrayList<CriterionType> results = new ArrayList<CriterionType>();
        
        ArrayList<Integer> ids = Query.find("moe_criteriontype", null, null);
        
        if (ids != null)
        {
            //System.out.println(ids.size() + " criteriontype rows loaded");
        
            for (int id : ids)
            {
                results.add(new CriterionType(id));
            }
        }
        else
        {
            //System.out.println("no criteriontypes found!");
        }
                
        return results;
    }
    
    
}
