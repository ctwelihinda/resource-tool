/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.helpers;

import stans.resourcerecord.dao.JoinPersister;
import stans.resourcerecord.dao.TagLoader;
import stans.resourcerecord.dao.TagPersister;
import stans.resourcerecord.dao.TagTypeLoader;
import stans.resourcerecord.model.Tag;
import stans.db.Query;
import java.util.ArrayList;

/**
 *
 * @author peter
 */
public class ImportHelpers {
    
    public static int importJazzPerformer (Integer resource_id, String name, String gender, ArrayList<String> instruments, ArrayList<String> styles, String musician_type, boolean is_fnmi, boolean is_can, boolean is_sask)
    {
    ////////////////////////////////////
    // get DB IDs for types and tags
        Integer performer_type_id = TagTypeLoader.loadByType("Musician/Musical Group").getDBID();        
        Integer instrument_type_id = TagTypeLoader.loadByType("Musical Role").getDBID();
        Integer style_type_id = TagTypeLoader.loadByType("Musical Genre/Style").getDBID();

    // gender tags
        Integer male_id = 0;
        Integer female_id = 0;
        Integer other_id = 0;
        for (Tag gt : TagLoader.loadByTypeID(TagTypeLoader.loadByType("Gender").getDBID()))
        {
            if (gt.getValue().equals("Male"))
            { male_id = gt.getDBID(); }
            if (gt.getValue().equals("Female"))
            { female_id = gt.getDBID(); }
            if (gt.getValue().equals("Other"))
            { other_id = gt.getDBID(); }
        }        
    // nationality tags
        Integer sk_id = 0;
        Integer can_id = 0;
        for (Tag gt : TagLoader.loadByTypeID(TagTypeLoader.loadByType("Nationality").getDBID()))
        {
            if (gt.getValue().equals("Saskatchewanian"))
            { sk_id = gt.getDBID(); }
            if (gt.getValue().equals("Canadian"))
            { can_id = gt.getDBID(); }
        }        
    // fnmi tag
        Integer fnmi_id = 0;
        for (Tag gt : TagLoader.loadByTypeID(TagTypeLoader.loadByType("Ethnicity").getDBID()))
        {
            if (gt.getValue().equals("FNMI"))
            { fnmi_id = gt.getDBID(); }
        }
        
    ////////////////////////////////////////
    // find or create Musician/Musical Group tag
        name = name.replace("'", "''");
        
        Integer parent_id;
        ArrayList<String> tagfind_args = new ArrayList<String>();
        tagfind_args.add(Integer.toString(performer_type_id));
        tagfind_args.add(name);
        ArrayList<Integer> find_ids = Query.find("moe_tag", "tagtype_id = ? AND value = ?", tagfind_args); // does this tag already exist?
        if (find_ids.size() > 0)
        { parent_id = find_ids.get(0); }
        else
        {
            parent_id = TagPersister.createNew(name, performer_type_id).getDBID();
        }
    // add Musician/Musical Group tag to Jazz Performer List resource (if not already attached)
        find_ids.clear();
        tagfind_args.clear();
        tagfind_args.add(Integer.toString(resource_id));
        tagfind_args.add(Integer.toString(parent_id));
        find_ids = Query.find("moe_resource_tag", "resource_id = ? AND tag_id = ?", tagfind_args); // does this tag already exist?
        if (find_ids.isEmpty())
        { JoinPersister.addResourceTagJoin(resource_id, parent_id); }
        

        
    //////////////////////////////
    // handle special tags
        if (musician_type.equals("Musical Group"))
        {
            instruments.add("Musical Group");
        }        
        if (is_can)
        {
            find_ids.clear();
            tagfind_args.clear();
            tagfind_args.add(Integer.toString(resource_id));
            tagfind_args.add(Integer.toString(can_id));
            tagfind_args.add(Integer.toString(parent_id));
            find_ids = Query.find("moe_resource_tag", "resource_id = ? AND tag_id = ? AND parent_id = ?", tagfind_args); // does this tag already exist?
            if (find_ids.isEmpty())
            { JoinPersister.addResourceTagJoin(resource_id, can_id, parent_id); }
        }
        if (is_sask)
        {
            find_ids.clear();
            tagfind_args.clear();
            tagfind_args.add(Integer.toString(resource_id));
            tagfind_args.add(Integer.toString(sk_id));
            tagfind_args.add(Integer.toString(parent_id));
            find_ids = Query.find("moe_resource_tag", "resource_id = ? AND tag_id = ? AND parent_id = ?", tagfind_args); // does this tag already exist?
            if (find_ids.isEmpty())
            { JoinPersister.addResourceTagJoin(resource_id, sk_id, parent_id); }
        }
        if (is_fnmi)
        {
            find_ids.clear();
            tagfind_args.clear();
            tagfind_args.add(Integer.toString(resource_id));
            tagfind_args.add(Integer.toString(fnmi_id));
            tagfind_args.add(Integer.toString(parent_id));
            find_ids = Query.find("moe_resource_tag", "resource_id = ? AND tag_id = ? AND parent_id = ?", tagfind_args); // does this tag already exist?
            if (find_ids.isEmpty())
            { JoinPersister.addResourceTagJoin(resource_id, fnmi_id, parent_id); }
        }
        if (gender != null)
        {
            if (gender.equals("Male"))
            {
                find_ids.clear();
                tagfind_args.clear();
                tagfind_args.add(Integer.toString(resource_id));
                tagfind_args.add(Integer.toString(male_id));
                tagfind_args.add(Integer.toString(parent_id));
                find_ids = Query.find("moe_resource_tag", "resource_id = ? AND tag_id = ? AND parent_id = ?", tagfind_args); // does this tag already exist?
                if (find_ids.isEmpty())
                { JoinPersister.addResourceTagJoin(resource_id, male_id, parent_id); }
            }
            if (gender.equals("Female"))
            {
                find_ids.clear();
                tagfind_args.clear();
                tagfind_args.add(Integer.toString(resource_id));
                tagfind_args.add(Integer.toString(female_id));
                tagfind_args.add(Integer.toString(parent_id));
                find_ids = Query.find("moe_resource_tag", "resource_id = ? AND tag_id = ? AND parent_id = ?", tagfind_args); // does this tag already exist?
                if (find_ids.isEmpty())
                { JoinPersister.addResourceTagJoin(resource_id, female_id, parent_id); }
            }
            if (gender.equals("Other"))
            {
                find_ids.clear();
                tagfind_args.clear();
                tagfind_args.add(Integer.toString(resource_id));
                tagfind_args.add(Integer.toString(other_id));
                tagfind_args.add(Integer.toString(parent_id));
                find_ids = Query.find("moe_resource_tag", "resource_id = ? AND tag_id = ? AND parent_id = ?", tagfind_args); // does this tag already exist?
                if (find_ids.isEmpty())
                { JoinPersister.addResourceTagJoin(resource_id, other_id, parent_id); }
            }
        }
    // add Musical Role tags (instruments)
        for (String inst : instruments)
        {
            ArrayList<String> query_args = new ArrayList<String>();
            query_args.add(Integer.toString(instrument_type_id));
            query_args.add(inst);
            
            ArrayList<Integer> existing_tags = Query.find("moe_tag", "tagtype_id = ? AND value = ?", query_args); // does this tag already exist?
            Integer inst_tag_id;
            if (existing_tags.size() > 0)
            { inst_tag_id = existing_tags.get(0); }
            else
            { inst_tag_id = TagPersister.createNew(inst, instrument_type_id).getDBID(); }

            find_ids.clear();
            tagfind_args.clear();
            tagfind_args.add(Integer.toString(resource_id));
            tagfind_args.add(Integer.toString(inst_tag_id));
            tagfind_args.add(Integer.toString(parent_id));
            find_ids = Query.find("moe_resource_tag", "resource_id = ? AND tag_id = ? AND parent_id = ?", tagfind_args); // does this tag already exist?
            if (find_ids.isEmpty())
            { JoinPersister.addResourceTagJoin(resource_id, inst_tag_id, parent_id); }
        }
    // add Musical Genre/Style tags
        for (String style : styles)
        {
            ArrayList<String> query_args = new ArrayList<String>();
            query_args.add(Integer.toString(style_type_id));
            query_args.add(style);
            
            ArrayList<Integer> existing_tags = Query.find("moe_tag", "tagtype_id = ? AND value = ?", query_args); // does this tag already exist?
            Integer style_tag_id;
            if (existing_tags.size() > 0)
            { style_tag_id = existing_tags.get(0); }
            else
            { style_tag_id = TagPersister.createNew(style, style_type_id).getDBID(); }

            find_ids.clear();
            tagfind_args.clear();
            tagfind_args.add(Integer.toString(resource_id));
            tagfind_args.add(Integer.toString(style_tag_id));
            tagfind_args.add(Integer.toString(parent_id));
            find_ids = Query.find("moe_resource_tag", "resource_id = ? AND tag_id = ? AND parent_id = ?", tagfind_args); // does this tag already exist?
            if (find_ids.isEmpty())
            { JoinPersister.addResourceTagJoin(resource_id, style_tag_id, parent_id); }
        }

        
        return 0;
    }
}
