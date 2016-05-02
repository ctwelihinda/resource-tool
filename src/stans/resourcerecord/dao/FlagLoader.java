/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.dao;

import stans.db.Enumerators.BBComparisonOperator;
import stans.resourcerecord.model.Flag;
import stans.db.Query;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author peter
 */
public class FlagLoader {

    public static ArrayList<Flag> loadAllByResourceDBID(int resource_id) {
        ArrayList<Flag> results = new ArrayList<Flag>();
        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> join_ids;

        join_args.add(Integer.toString(resource_id));
        join_ids = Query.find("moe_resource_flag", "resource_id = ?", join_args);

        for (int join_id : join_ids) {
            results.add(new Flag(join_id));
        }

        return results;
    }

    public static ArrayList<Flag> loadAllByUserDBID(int user_id) {
        ArrayList<Flag> results = new ArrayList<Flag>();
        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> join_ids;

        join_args.add(Integer.toString(user_id));
        join_ids = Query.find("moe_resource_flag", "user_id = ?", join_args);

        for (int join_id : join_ids) {
            results.add(new Flag(join_id));
        }

        return results;
    }

    public static ArrayList<Flag> loadByResourceAndUser(int user_id, int resource_id) {
        ArrayList<Flag> results = new ArrayList<Flag>();
        ArrayList<String> join_args = new ArrayList<String>();
        ArrayList<Integer> join_ids;

        join_args.add(Integer.toString(user_id));
        join_args.add(Integer.toString(resource_id));
        join_ids = Query.find("moe_resource_flag", "user_id = ? AND resource_id", join_args);

        for (int join_id : join_ids) {
            results.add(new Flag(join_id));
        }

        return results;
    }

    public static ArrayList<Flag> loadByUserIdUsernameAndOperator(String username, BBComparisonOperator operator) {
        ArrayList<Flag> result_flags = new ArrayList<Flag>();
        for (int id : Query.findWithOperator("users", "user_id", username, operator)) {
            result_flags.addAll(loadByUserId(id));
        }
        return result_flags;
    }

    public static ArrayList<Flag> loadByFieldNameAndFieldValueAndOperator(String dbFieldName, String fieldValue, BBComparisonOperator operator) {
        ArrayList<Flag> result_flags = new ArrayList<Flag>();
        for (int id : Query.findWithOperator("moe_resource_flag", dbFieldName, fieldValue, operator)) {
            result_flags.add(new Flag(id));
        }
        return result_flags;
    }

    private static ArrayList<Flag> loadByUserId(int user_id) {
        ArrayList<Flag> result_flags = new ArrayList<Flag>();
        ArrayList<String> args = new ArrayList<String>();
        args.add(Integer.toString(user_id));
        ArrayList<Integer> result_ids = Query.find("moe_resource_flag", "user_id = ?", args);
        for (int id : result_ids) {
            result_flags.add(new Flag(id));
        }
        return result_flags;
    }

}
