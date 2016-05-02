/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.helpers;

import stans.db.Query;
import java.util.ArrayList;

/**
 *
 * @author peter
 */
public class RNumberManager {

    public static String getNext()
    {
        String rtn = "";
        ArrayList<Integer> results = Query.find("moe_rnumber_incrementer", null, null);
        
        if (results.size() > 0)
        {
            Integer next_number = (Integer) Query.select("moe_rnumber_incrementer", "next_number", results.get(0));
            Query.update("moe_rnumber_incrementer", "next_number", results.get(0), Integer.toString(next_number + 1));
            rtn = "R" + Integer.toString(next_number);
        }
        else
        {
            rtn = "error";
        }
        
        return rtn;
    }
    
}
