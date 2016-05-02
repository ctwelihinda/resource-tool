/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.forms;

/**
 *
 * @author peter
 */
public class CheckboxItem {

    private String db_type_name;
    private String text;
    
    public CheckboxItem () 
    {
        db_type_name = "";
        text = "";
    }
    
    public CheckboxItem (String db)
    {
        db_type_name = db;
    }
    
    public CheckboxItem (String db, String t) 
    {
        db_type_name = db;
        text = t;
    }
    
    public String getText ()
    {
        return text;
    }
    public void setText (String t)
    {
        text = t;
    }
    
    public String getDBTypeName ()
    {
        return db_type_name;
    }
    public void setDBTypeName (String db)
    {
        db_type_name = db;
    }
}
