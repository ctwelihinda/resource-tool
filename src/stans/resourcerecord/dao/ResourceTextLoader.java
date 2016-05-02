/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import stans.db.Enumerators.BBComparisonOperator;
import stans.resourcerecord.model.ResourceText;
import stans.db.Query;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author peter
 */
public class ResourceTextLoader {

    public static ArrayList<ResourceText> loadByResourceID(int resource_id) {
        ArrayList<ResourceText> rtn = new ArrayList<ResourceText>();

        ArrayList<String> args = new ArrayList<String>();
        args.add(Integer.toString(resource_id));
        ArrayList<Integer> text_ids = Query.find("moe_resource_text", "resource_id = ?", args);

        for (int text_id : text_ids) {
            rtn.add(new ResourceText(text_id));
        }

        return rtn;
    }

    public static ArrayList<ResourceText> loadByCreatedByUsernameAndOperator(String username, BBComparisonOperator operator) {
        ArrayList<ResourceText> result_resourceTexts = new ArrayList<ResourceText>();
        for (int id : Query.findWithOperator("users", "user_id", username, operator)) {
            result_resourceTexts.addAll(loadByCreatedBy(id));
        }
        return result_resourceTexts;
    }

    public static ArrayList<ResourceText> loadByFieldNameAndFieldValueAndOperator(String dbFieldName, String fieldValue, BBComparisonOperator operator) {
        ArrayList<ResourceText> result_resourceTexts = new ArrayList<ResourceText>();
        for (int id : Query.findWithOperator("moe_resource_text", dbFieldName, fieldValue, operator)) {
            result_resourceTexts.add(new ResourceText(id));
        }
        return result_resourceTexts;
    }

    public static ArrayList<ResourceText> loadByFieldNameAndFieldValueAndFieldTypeAndOperator(String dbFieldName, String fieldValue, String fieldType, BBComparisonOperator operator) {
        ArrayList<ResourceText> result_resourceTexts = new ArrayList<ResourceText>();
        for (int id : Query.findWithOperator("moe_resource_text", dbFieldName, fieldValue, operator)) {
            String type = (String)Query.select("moe_resource_text", "type", id);
            if ((type != null) && (type.equals(fieldType)))
            { result_resourceTexts.add(new ResourceText(id)); }
        }
        return result_resourceTexts;
    }
    
    private static ArrayList<ResourceText> loadByCreatedBy(int user_id) {
        ArrayList<ResourceText> result_resourceTexts = new ArrayList<ResourceText>();
        ArrayList<String> args = new ArrayList<String>();
        args.add(Integer.toString(user_id));
        ArrayList<Integer> result_ids = Query.find("moe_resource_text", "created_by = ?", args);
        for (int id : result_ids) {
            result_resourceTexts.add(new ResourceText(id));
        }
        return result_resourceTexts;
    }

}
