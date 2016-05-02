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
public class TypeAheadFunctions {
    public static String printOutAllTagTypeValues(String tagtype_in) 
    {
        ArrayList<String> tag_pk_in = new ArrayList();
        String val_publisher="source: [";

        ArrayList<String> tag_type_in = new ArrayList();
        ArrayList<Integer> tag_type_pk = new ArrayList();

        //get the pk1 for tag type of Publisher from moe_tagtype
        String str_tag_type_pk = "";
        tag_type_in = new ArrayList();
        tag_type_in.add(tagtype_in);
        tag_type_pk = Query.find("moe_tagtype", "type = ?", tag_type_in);
        for (int i = 0; i < tag_type_pk.size(); i++) 
        {
            str_tag_type_pk = tag_type_pk.get(i).toString();
        }

        tag_pk_in.add(str_tag_type_pk);

        //match the pk1 to fk of tagtype_id in moe_tag and get the pk1 for the table
        ArrayList<Integer> tag_pk = Query.find("moe_tag", "tagtype_id = ?", tag_pk_in);

        ArrayList<String> tag_val_al = new ArrayList();

        for (int i = 0; i < tag_pk.size(); i++) 
        {
            //get the value of the tag of  tagtype publisher
            String tag_value = Query.select("moe_tag", "value", tag_pk.get(i)).toString();
            tag_val_al.add(tag_value);
        }

        for (int j = 0; j < tag_val_al.size(); j++) 
        {
            //print out each tag value -> print out each publisher's name
            val_publisher += " \"" + tag_val_al.get(j) + "\", ";
        }

        if (!val_publisher.equals("")) 
        {
            val_publisher = val_publisher.substring(0, val_publisher.length() - 2);
        }

        val_publisher += "]";
        
        return val_publisher;
    }
    
}
