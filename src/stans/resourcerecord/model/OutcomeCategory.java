/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.model;

/**
 *
 * @author peter
 */
public class OutcomeCategory {
    
    public String category_id;
    public String name;
    
    public OutcomeCategory ()
    {
        name = "";
        category_id = "";
    }
    
    public OutcomeCategory (String n, String id)
    {
        name = n;
        category_id = id;
    }
}
