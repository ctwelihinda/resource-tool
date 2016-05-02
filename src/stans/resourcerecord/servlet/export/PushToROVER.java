/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.export;

import blackboard.cms.filesystem.CSContext;
import stans.XMLReader;
import stans.resourcerecord.dao.ResourceLoader;
import stans.resourcerecord.dao.ResourceTextLoader;
import stans.resourcerecord.helpers.ValidationHelpers;
import stans.resourcerecord.model.Recommendation;
import stans.resourcerecord.model.Resource;
import stans.resourcerecord.model.ResourceText;
import stans.resourcerecord.model.Tag;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xythos.common.api.XythosException;

/**
 *
 * @author peter
 */
public class PushToROVER extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String resource_ids = request.getParameter("resource_ids");

        boolean uses_R_numbers;
        if (resource_ids.contains("R"))
        { uses_R_numbers = true; }
        else
        { uses_R_numbers = false; }
        
        String[] resource_id_list = resource_ids.split(",");

        String base_path = "/library/ROVER Videos/New Video Exports";
        String file_name = resource_ids.substring(0, Math.min(resource_ids.length()-1, 95)).replace(",", "_") + ".rb";
        StringBuilder entire_file_sb = new StringBuilder();
        
        entire_file_sb.append("# encoding: utf-8\n\n");

        for (String resource_id : resource_id_list)
        {
            Resource this_res;
            
            if (uses_R_numbers)
            { this_res = ResourceLoader.loadByRNumber(resource_id); }
            else
            { this_res = new Resource(Integer.parseInt(resource_id)); }
            
            
            ArrayList<Tag> this_res_tags = this_res.getTags();

            String title = "";
            String description = "";
            String series_name = "";
            String duration = "";
            String entry = this_res.getRNumber();
            String language = "eng-ca";
            String video_filetype = "video/x-flv";
            String video_filename = this_res.getRNumber() + ".flv";
            String copyright = "";
            String expiry = "";
            String producer = "";
            String start_date = "";
            String image_filename = "";
            String image_filetype = "";
            ArrayList<String> initiative_ids = new ArrayList<String>();
            ArrayList<String> taxon_paths = new ArrayList<String>();
            ArrayList<Tag> tag_groups = new ArrayList<Tag>();

            for (Tag t : this_res_tags)
            {
                if (t.getType().equals("Title"))
                {
                    title = t.getValue() + title;
                }
                if (t.getType().equals("Subtitle"))
                {
                    title = title + ": " + t.getValue();
                }
                if (t.getType().equals("Series Title"))
                {
                    series_name = t.getValue();
                }
                if (t.getType().equals("Publisher"))
                {
                    producer = t.getValue();
                }
                if (t.getType().equals("Copyright Date"))
                {
                    copyright = t.getValue();
                }
                if (t.getType().equals("Expiry Date"))
                {
                    expiry = t.getValue();
                }
                if (t.getType().equals("Year Recommended"))
                {
                    start_date = t.getValue();
                }
                if (t.getType().equals("Cover Image Path"))
                {
                    image_filename = t.getValue().substring(t.getValue().lastIndexOf('/') + 1);
                    image_filetype = "image/" + image_filename.substring(image_filename.lastIndexOf('.') + 1).toLowerCase();
                }
                if ((t.getType().equals("Running Time")) || (t.getType().equals("Length")))
                {
                    String minutes_only = t.getValue().replaceAll("^([\\d]+)[^\\d].*$", "$1");
                    
                    Integer num_seconds = 0;

                    try 
                    {
                        num_seconds = Integer.parseInt(minutes_only) * 60;
                    }                    
                    catch (NumberFormatException e)
                    {
                        System.out.println("PushToROVER servlet: Error converting duration!!");
                        System.out.println("Value before: ");
                        System.out.println(t.getValue());
                        System.out.println("Value after: ");
                        System.out.println(minutes_only);
                    }
                    duration = Integer.toString(num_seconds);
                }
                if (t.getType().equals("Language"))
                {
                    language = t.getValue();
                    if (language.equals("English"))
                    { language = "eng-ca"; }
                    if (language.equals("French"))
                    { language = "fra-ca"; }
                    if (language.equals("Cree"))
                    { language = "cre"; }
                }
                if ((t.getType().equals("Content Classification")) || (t.getType().equals("Topic")))
                {
                    String content_classification = t.getValue();

                    if (content_classification.equals("Saskatchewan Content"))
                    { initiative_ids.add("1"); }
                    if (content_classification.equals("Canadian Content"))
                    { initiative_ids.add("2"); }
                    if (content_classification.equals("FNMI Content"))
                    { initiative_ids.add("3"); }
                    if (content_classification.equals("Bullying"))
                    { initiative_ids.add("4"); }
                    if (content_classification.equals("Inquiry"))
                    { initiative_ids.add("5"); }
                    if (content_classification.equals("WNCP Content"))
                    { initiative_ids.add("6"); }
                }
                if (t.getType().equals("Tag Group"))
                { 
                    tag_groups.add(t); 
                }
            }

            for (ResourceText rt : ResourceTextLoader.loadByResourceID(this_res.getDBID()))
            {
                if (rt.getType().equals("Annotation"))
                {
                    description = rt.getText().replace("\n", "").replace("\"", "\\\"") + description;
                }
                if (rt.getType().equals("Notes"))
                {
                    description = description + " " + rt.getText().replace("\n", "").replace("\"", "\\\"");
                }                        
            }

            for (Tag group : tag_groups)
            {
                ArrayList<Tag> all_subjects = new ArrayList<Tag>();
                ArrayList<Tag> all_levels = new ArrayList<Tag>();
                ArrayList<Tag> all_programs = new ArrayList<Tag>();
                ArrayList<Tag> all_outcomes = new ArrayList<Tag>();
                for (Tag t : group.getChildren(this_res.getDBID()))
                {
                    if (t.getType().equals("Subject"))
                    { all_subjects.add(t); }
                    if (t.getType().equals("Program"))
                    { all_programs.add(t); }
                    if ((t.getType().equals("Level")) || (t.getType().equals("Grade")))
                    { all_levels.add(t); }
                    if ((t.getType().equals("Outcome")) || (t.getType().equals("Goal")) || (t.getType().equals("Strand")) || (t.getType().equals("Unit")) || (t.getType().equals("Module")))
                    { all_outcomes.add(t); }
                }

                for (Tag subj : all_subjects)
                {
                    if (!all_levels.isEmpty()) // have to check, so that we can still add just the subject if no levels are listed (see else block below)
                    {
                        for (Tag level : all_levels)
                        {
                            if (!all_outcomes.isEmpty())
                            {
                                for (Tag outcome : all_outcomes)
                                {
                                    taxon_paths.add(subj.getValue() + "::" + level.getType() + " " + level.getValue() + "::" + outcome.getValue());
                                }
                            }
                            else
                            { taxon_paths.add(subj.getValue() + "::" + level.getType() + " " + level.getValue()); }
                        }
                    }
                    else
                    { taxon_paths.add(subj.getValue()); }
                }
            }


/* ================================================
 * =========== GENERATE FILE CONTENTS =============
 * ================================================
 */

            entire_file_sb.append("\n#################\n");
            entire_file_sb.append("#    ");
            entire_file_sb.append(this_res.getRNumber());
            entire_file_sb.append("    #\n");
            entire_file_sb.append("#################\n\n");
            
            entire_file_sb.append("r = Resource.new\n");
                entire_file_sb.append("r.title = \"");
                    entire_file_sb.append(title);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("r.description = \"");
                    entire_file_sb.append(description);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("r.entry = \"");
                    entire_file_sb.append(entry);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("r.language = \"");
                    entire_file_sb.append(language);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("r.screenshot_file_name = \"");
                    entire_file_sb.append(image_filename);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("r.screenshot_content_type = \"");
                    entire_file_sb.append(image_filetype);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("r.active = true\n");
                entire_file_sb.append("r.deleted = false\n");
            entire_file_sb.append("r.save\n\n");

            entire_file_sb.append("new_res_id = r.id\n");

            entire_file_sb.append("rf = ResourceFile.new\n");
                entire_file_sb.append("rf.duration = \"");
                    entire_file_sb.append(duration);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("rf.file_type = \"");
                    entire_file_sb.append(video_filetype);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("rf.filename = \"");
                    entire_file_sb.append(video_filename);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("rf.resource_id = new_res_id\n");
            entire_file_sb.append("rf.save\n\n");

            entire_file_sb.append("rr = Rights.new\n");
                entire_file_sb.append("rr.copyright = \"");
                    entire_file_sb.append(copyright);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("rr.expiry = \"");
                    entire_file_sb.append(expiry);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("rr.producer = \"");
                    entire_file_sb.append(producer);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("rr.start_date = \"");
                    entire_file_sb.append(start_date);
                entire_file_sb.append("\"\n");

                entire_file_sb.append("rr.perpetuity = false\n");
                
                entire_file_sb.append("rr.searchable = false\n");
                entire_file_sb.append("rr.requires_easy_proxy_authentication = true\n");
                entire_file_sb.append("rr.easy_proxy_group = \"curriculum_consultant_preview\"\n");

                entire_file_sb.append("rr.resource_id = new_res_id\n");
            entire_file_sb.append("rr.save\n\n");

            for (String i_id : initiative_ids)
            {
                entire_file_sb.append("il = InitiativeListing.new\n");

                entire_file_sb.append("il.initiative_id = ");
                    entire_file_sb.append(i_id);
                entire_file_sb.append("\n");

                entire_file_sb.append("il.resource_id = new_res_id\n");

                entire_file_sb.append("il.save\n\n");
            }

            entire_file_sb.append("dupes = Hash.new\n");
            
            for (String tp : taxon_paths)
            {
                
                boolean first = true;
                for (String t : tp.split("::"))
                {
                    if (first)
                    {
                        entire_file_sb.append("t = Taxon.where(\"name = '");
                        entire_file_sb.append(t);
                        entire_file_sb.append("'\")[0]\n");                        
                        entire_file_sb.append("parent_id = 0\n");
                        entire_file_sb.append("unless t.nil?\n");
                        entire_file_sb.append(" parent_id = t.id\n");
                        entire_file_sb.append("end\n");
                        first = false;
                    }
                    else
                    {
                        entire_file_sb.append("new_t = Taxon.where(\"parent_id = ? AND name LIKE '%");
                        entire_file_sb.append(t);
                        entire_file_sb.append("%'\", parent_id)[0]\n\n");
                        
                        entire_file_sb.append("unless new_t.nil?\n");
                        entire_file_sb.append(" t = new_t\n");
                        entire_file_sb.append("end\n\n");
                    }
                }
                entire_file_sb.append("if (!t.nil?) && (!dupes.has_key?(t.id))\n");
                entire_file_sb.append(" dupes[t.id] = 1\n");
                entire_file_sb.append(" tp = TaxonPath.new\n");
                entire_file_sb.append(" tp.resource_id = new_res_id\n");
                entire_file_sb.append(" tp.taxon_id = t.id\n");
                entire_file_sb.append(" tp.save\n");
                entire_file_sb.append("end\n\n");
            }

        }

/* ====================================
 * =========== WRITE FILE =============
 * ====================================
 */

        CSContext cs_context = null;
        ByteArrayInputStream file_to_write_is = null;

        try {
            cs_context = CSContext.getContext();

            file_to_write_is = new ByteArrayInputStream(entire_file_sb.toString().getBytes());

            cs_context.isSuperUser(true);
            cs_context.createFile(base_path, file_name, file_to_write_is, true);

            System.out.println("Wrote file " + file_name + " to the folder " + base_path);

        } catch (Exception e) {
            //StringWriter stringWriter = new StringWriter();
            //String stackTrace = "";
            //e.printStackTrace(new PrintWriter(stringWriter));
            //stackTrace = stringWriter.toString();
            //System.out.println(stackTrace);
            System.out.println("Error writing ROVER export file " + file_name + " to " + base_path + ": " + e.getMessage());
            cs_context.rollback();
        } finally {
            if (cs_context != null) {
                try {
					cs_context.commit();
				} catch (XythosException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                file_to_write_is.close();
            }
        }

/* ==================================================
* =========== GENERATE AND DISPLAY URL =============
* ==================================================
*/
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            //out.println("<html>");
            //out.println("<head>");
            //out.println("<title>Preview Resource</title>");            
            //out.println("</head>");
            //out.println("<body>");
            out.println("<p><b>Here's the import script: </b><p>");
            out.println("<p>");
            out.println(entire_file_sb.toString());
            out.println("</p>");
            out.println("<hr/>");

            //out.println("</body>");
            //out.println("</html>");
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
