/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.model;

import stans.resourcerecord.helpers.ValidationHelpers;

/**
 *
 * @author peter
 */
public class ResourceRecordCurriculumGroup {

    private boolean[] has_grade = new boolean[14];
    private boolean a10 = false;
    private boolean b10 = false;
    private boolean a30 = false;
    private boolean b30 = false;

    private String subjects = "";
    private String keywords = "";

    public ResourceRecordCurriculumGroup (Tag g, Integer r_id)
    {
        for (Tag this_child : g.getChildren(r_id))
        {
            
    //////////////////    
    // SUBJECT
            if (this_child.getType().equals("Subject"))
            {
                if (!subjects.equals(""))
                { subjects = subjects + ", "; }
                subjects = subjects + this_child.getValue();
            }

            
    //////////////////    
    // UNIT/STRAND/OUTCOME/ETC
            if (
                (this_child.getType().equals("Unit")) ||
                (this_child.getType().equals("Module")) ||
                (this_child.getType().equals("Goal")) ||
                (this_child.getType().equals("Outcome")) ||
                (this_child.getType().equals("Domain")) ||
                (this_child.getType().equals("Program")) ||
                (this_child.getType().equals("Strand")) ||
                (this_child.getType().equals("Indicator")) ||
                (this_child.getType().equals("Language Cuing System")) ||
                (this_child.getType().equals("Suggested Use"))
            )
            {
                if (!keywords.equals(""))
                { keywords = keywords + ", "; }
                keywords = keywords + this_child.getValue();
            }


            
            
    //////////////////    
    // GRADE/LEVEL
            if ((this_child.getType().equals("Grade")) || (this_child.getType().equals("Level")))
            {
                
                String full_value = this_child.getValue();
                //Nicole: Added check for PreK & K
                if (this_child.getValue().equals("Prekindergarten"))
                        { has_grade[0] = true; }
                else if (this_child.getValue().equals("Kindergarten"))
                        { has_grade[1] = true; }
                
                String just_number = full_value.replace("[ABC]", "");
                if (just_number.equals("10"))
                {
                    if (full_value.equals("A10"))   { a10 = true; }
                    if (full_value.equals("B10"))   { b10 = true; }
                    if (full_value.equals("10"))   { a10 = true; b10 = true; }
                }
                if (just_number.equals("20"))
                { just_number = "11"; }
                if (just_number.equals("30"))
                { 
                    just_number = "12"; 
                    if (full_value.equals("A30"))   { a30 = true; }
                    if (full_value.equals("B30"))   { b30 = true; }
                    if (full_value.equals("30"))   { a30 = true; b30 = true; }
                }

                if ((ValidationHelpers.isPositiveInteger(just_number)) && (Integer.parseInt(just_number) < 13))
                {
                    has_grade[Integer.parseInt(just_number) + 1] = true; // add one to accomodate prek (prek = 0, k = 1, grade 1 = 2, etc.)
                }
            }
        } // end of children loop
    }
    
    public boolean isA10 ()
    {
        return a10;
    }
    public boolean isB10 ()
    {
        return b10;
    }
    public boolean isA30 ()
    {
        return a30;
    }
    public boolean isB30 ()
    {
        return b30;
    }
    
    public boolean[] getHasGradeList ()
    {
        return has_grade;
    }
    
    public String getSubjects ()
    {
        return subjects;
    }
    public String getKeywords ()
    {
        return keywords;
    }
}
