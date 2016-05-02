/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import stans.EasyUser;

/**
 *
 * @author peter
 */
public class TaggerPermissionsManager {
    
    // constants for permissions - need to be in the same order as the role names below
    public final static Integer STF_ADMIN = 0;
    public final static Integer CURRIC_TAGGER = 1;
    public final static Integer FULL_ADMIN = 2;
    
    public final static Integer NONE = 0;
    public final static Integer READ_ONLY = 1;
    public final static Integer READ_WRITE = 2;
    public final static Integer READ_WRITE_DELETE = 3;
    
    private boolean is_superadmin;
    
    // permissions
    private ArrayList<Integer> roles = new ArrayList<Integer>();
    
    // these need to be in the same order as the constants above
    private static List<String> all_role_names = Arrays.asList(
        "stf_resource_evaluation_administrator",
        "curriculum_resource_tagger",
        "resource_tool_full_admin"
    );
    private ArrayList<HashMap> permission_defs = new ArrayList<HashMap>();
    private ArrayList<String> permission_names = new ArrayList<String>();
    
    public TaggerPermissionsManager (EasyUser curr_user)
    {
        try 
        {
            if ((curr_user.blackboard.getBatchUid().equals("peter.broda")) || (curr_user.blackboard.getBatchUid().equals("chamath.welihinda")))
            { is_superadmin = true; }
            else
            { is_superadmin = false; }
            
            int role_counter = 0;
            for (String role_name : all_role_names)
            {
                if (curr_user.shortcuts.hasRoleId(role_name))
                {
                    roles.add(role_counter);
                    //System.out.println("has role: " + role_name);
                }
                role_counter++;
            }
        }
        catch (Exception e)
        {}

        permission_names.add("parent");
        permission_names.add("other_resources");
        permission_names.add("title");
        permission_names.add("pubdist");
        permission_names.add("dates");
        permission_names.add("other");
        permission_names.add("licenses");
        permission_names.add("outofprint");
        permission_names.add("people");
        permission_names.add("stf_borrow");
        permission_names.add("tracking_info");
        permission_names.add("formats");
        permission_names.add("content");
        permission_names.add("subjgrade");
        permission_names.add("text");
        permission_names.add("rec");
        permission_names.add("flag");
        permission_names.add("approval");
        permission_names.add("push_to_rover");
        permission_names.add("quick_info");
    
        for (Integer i=0; i<=FULL_ADMIN; i++)
        { 
            HashMap curr_hash = new HashMap();

            // set access for all permission sections
            for (String perm_name : permission_names)
            {
                if ((i==FULL_ADMIN) || (i==STF_ADMIN))
                { curr_hash.put(perm_name, READ_WRITE_DELETE); }
                else
                { curr_hash.put(perm_name, READ_ONLY); }
            }

            // permissions for ONLY full admins
            if (i!=FULL_ADMIN)
            { 
                curr_hash.put("push_to_rover", NONE); 
            }
            
            permission_defs.add(curr_hash); 
        }
        // special cases
        permission_defs.get(STF_ADMIN).put("approval", READ_ONLY);
        permission_defs.get(STF_ADMIN).put("rec", READ_WRITE);
        
        permission_defs.get(CURRIC_TAGGER).put("content", READ_WRITE_DELETE);
        permission_defs.get(CURRIC_TAGGER).put("subjgrade", READ_WRITE_DELETE);
        permission_defs.get(CURRIC_TAGGER).put("text", READ_WRITE_DELETE);
        permission_defs.get(CURRIC_TAGGER).put("rec", READ_WRITE);
        permission_defs.get(CURRIC_TAGGER).put("approval", READ_WRITE);
    }
    
    public Integer getPermissionLevel (String section)
    {
        Integer perm_level = NONE;
        
        if (roles.contains(FULL_ADMIN))
        { perm_level = READ_WRITE_DELETE; }
        else
        {
            for (Integer role : roles)
            {
                Integer this_perm_level = (Integer)permission_defs.get(role).get(section);
                perm_level = Math.max(this_perm_level, perm_level);
            }
        }
        
        return perm_level;
    }
 
    public void addRoles (ArrayList<Integer> new_roles)
    {
        if (is_superadmin)
        {
            roles.clear();
            for (Integer new_role : new_roles)
            { roles.add(new Integer(new_role)); }
        }
    }
    
    public static List<String> getAllAllowedRoles ()
    {
        return all_role_names;
    }
}
