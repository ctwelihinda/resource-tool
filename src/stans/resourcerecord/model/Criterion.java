package stans.resourcerecord.model;

import stans.db.Query;
import java.sql.Timestamp;

public class Criterion {

    private int db_id;
    
    private String value_code;
    private String value_text;
    private String type;
    
    public Criterion (int id)
    {
        db_id = id;
        value_code = null;
        value_text = null;
        type = null;
    }
    
    
    public int getDBID ()
    {
        return db_id;
    }
    
    public void setValueCode (String v)
    {
        value_code = v;
    }
    public String getValueCode ()
    {
        if (value_code == null)
        {
            value_code = (String) Query.select("moe_criterion", "value_code", db_id);
        }
        return value_code;
    }

        
    public void setValueText (String v)
    {
        value_text = v;
    }
    public String getValueText ()
    {
        if (value_text == null)
        {
            value_text = (String) Query.select("moe_criterion", "value_text", db_id);
        }
        return value_text;
    }

    public String getTypeName ()
    {
        if (type == null)
        {
            int crittype_pk1 = (Integer) Query.select("moe_criterion", "criteriontype_id", db_id);
            type = (String) Query.select("moe_criteriontype", "name", crittype_pk1);
        }
        return type;
    }
}
