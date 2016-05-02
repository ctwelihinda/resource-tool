/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.model;

/**
 *
 * @author peter
 */
public class ResourceRelationship {
    
    public Integer db_id;
    public Integer this_id;
    public Integer other_id;
    public String this_type;
    public String other_type;
    
    public ResourceRelationship ()
    {
        db_id = null;
        this_id = null;
        other_id = null;
        this_type = null;
        other_type = null;
    }
    public ResourceRelationship (Integer db_id, Integer t_id, String t_type, Integer o_id, String o_type)
    {
        db_id = db_id;
        this_id = t_id;
        other_id = o_id;
        this_type = t_type;
        other_type = o_type;
    }
}
