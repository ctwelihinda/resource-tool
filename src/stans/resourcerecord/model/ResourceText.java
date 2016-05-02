/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.model;

import stans.db.Query;
import java.sql.Timestamp;

/**
 *
 * @author peter
 */
public class ResourceText {

    private int db_id;
    private Integer resource_id;
    private String created_by;
    private Timestamp created_at;
    private String text;
    private String type;

    public ResourceText(int id) {

        db_id = id;

        resource_id = null;
        created_by = null;
        created_at = null;
        text = null;
        type = null;
    }

    public int getDBID() {
        return db_id;
    }

    public String getCreatedBy() {
        if (created_by == null) {
            String q_result = (String) Query.select("moe_resource_text", "created_by", db_id);
            if (q_result != null) {
                int user_pk1 = Integer.parseInt(q_result);
                created_by = (String) Query.select("users", "user_id", user_pk1);
            }
        }
        return created_by;
    }

    public Timestamp getCreatedAt() {
        if (created_at == null) {
            created_at = ((Timestamp) Query.select("moe_resource_text", "created_at", db_id));
        }
        return created_at;
    }

    public String getText() {
        if (text == null) {
            text = (String) Query.select("moe_resource_text", "text", db_id);
        }
        if (text == null)
        {
            text = "No text available";
        }
        return text;
    }

    public String getType() {
        if (type == null) {
            type = (String) Query.select("moe_resource_text", "type", db_id);
        }
        if (type == null)
        {
            type = "Unknown type";
        }
        return type;
    }

    public int getResourceID() {
        if (resource_id == null) {
            resource_id = (Integer) Query.select("moe_resource_text", "resource_id", db_id);
        }
        return resource_id;
    }
}
