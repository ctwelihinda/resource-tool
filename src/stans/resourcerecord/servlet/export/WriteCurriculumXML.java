/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.export;

import blackboard.cms.filesystem.CSContext;
import stans.XMLReader;
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
public class WriteCurriculumXML extends HttpServlet {

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
        
        String resource_id = request.getParameter("resource_id");
        
        String get_protocol = request.getScheme(); // should be https
        String get_domain = request.getServerName();
        String base_URL = get_protocol + "://" + get_domain;

        XMLReader manifestdoc = new XMLReader();  
        manifestdoc.setRootPath(getServletContext().getRealPath("/"));
        manifestdoc.setDoc("curric_manifest.xml");
        ArrayList<String> class_ids = new ArrayList<String>();
        ArrayList<Tag> tag_groups = new ArrayList<Tag>();
        
        if (ValidationHelpers.isPositiveInteger(resource_id))
        {
            Resource this_res = new Resource(Integer.parseInt(resource_id));
            ArrayList<Tag> this_res_tags = this_res.getTags();
            
            ArrayList<Integer> to_remove = new ArrayList<Integer>();
            
            String base_path = "/library/Curriculum Website/New Resource Previews";
            String file_name = this_res.getRNumber() + ".xml";

            String subject = "";
            
/* ================================================
 * =========== GENERATE FILE CONTENTS =============
 * ================================================
 */
            StringBuilder entire_file_sb = new StringBuilder();
             
            entire_file_sb.append("<?xml version=\"1.0\" ?>\n<resource>\n<content>\n");

            
                // TITLE
                    entire_file_sb.append("<title>");
                    entire_file_sb.append(this_res.getTitle());
                    entire_file_sb.append("</title>\n");

                // AUTHORS
                    entire_file_sb.append("<authors>");
                    int count = 0;
                    for (Tag t : this_res_tags)
                    {                        
                        if (t.getType().equals("Author"))
                        {
                            if (count > 0)
                            {
                                entire_file_sb.append(", ");
                            }
                            entire_file_sb.append(t.getValue());
                            count++;
                        }                    
                    }
                    entire_file_sb.append("</authors>\n");
                    
                // EDITORS
                    entire_file_sb.append("<editors>");
                    count = 0;
                    for (Tag t : this_res_tags)
                    {
                        if (t.getType().equals("Editor"))
                        {
                            if (count > 0)
                            {
                                entire_file_sb.append(", ");
                            }
                            entire_file_sb.append(t.getValue());
                            count++;
                        }                    
                    }
                    entire_file_sb.append("</editors>\n");

                    
                // ANNOTATION AND NOTES
                    entire_file_sb.append("<annotation><br/>");
                    String notes = "";
                    for (ResourceText rt : ResourceTextLoader.loadByResourceID(this_res.getDBID()))
                    {
                        if (rt.getType().equals("Annotation"))
                        {
                            entire_file_sb.append(rt.getText().replace("\n", "<br/><br/>"));
                        }
                        if (rt.getType().equals("Notes"))
                        {
                            notes = rt.getText().replace("\n", "<br/><br/>"); // want to have notes after the annotation, so save it here and append it after the loop
                        }                        
                    }
                    if (!notes.equals(""))
                    {
                        entire_file_sb.append("<br/><br/>NOTE:<br/>");
                        entire_file_sb.append(notes);
                    }
                    entire_file_sb.append("<br/></annotation>\n");

                    
                // GRADES
                    entire_file_sb.append("<grades>\n");
                    for (Tag t : this_res_tags)
                    {
                        if ((t.getType().equals("Grade")) || (t.getType().equals("Level")))
                        {
                            String this_level = t.getValue().replaceAll("[ABC]", "");
                            if (this_level.equals("20"))
                            { this_level = "11"; }
                            if (this_level.equals("30"))
                            { this_level = "12"; }
                            
                            entire_file_sb.append("<grade>");
                            entire_file_sb.append(this_level);
                            entire_file_sb.append("</grade>\n");
                        }
                        
                        // get groups while we're here...
                        if (t.getType().equals("Tag Group"))
                        { tag_groups.add(t); }
                    }
                    entire_file_sb.append("</grades>\n");
                    
                // SUBJECTS AND CLASSIFICATION IDS
                    for (Tag t : this_res_tags)
                    {
                        if (t.getType().equals("Subject"))
                        {
                            subject = t.getValue(); // just keep it simple for now
                        }
                    }
                    
                // SUGGESTED USES
                    entire_file_sb.append("<suggested_use><br/>");
                    entire_file_sb.append("</suggested_use>\n");

                    
                // PUBLISHER, PRODUCER, AND DISTRIBUTOR
                    entire_file_sb.append("<publisher>");
                    count = 0;
                    for (Tag t : this_res_tags)
                    {
                        if (t.getType().equals("Publisher"))
                        {
                            if (count > 0)
                            {
                                entire_file_sb.append("<br/><br/>");
                            }
                            entire_file_sb.append(t.getValue());
                            entire_file_sb.append("<br/>");
                            if (t.getInfo() != null)
                            {
                                entire_file_sb.append(t.getInfo().replace("\n", "<br/>"));
                            }
                            count++;
                        }
                    }
                    entire_file_sb.append("</publisher>\n");
                    entire_file_sb.append("<producer>");
                    count = 0;
                    for (Tag t : this_res_tags)
                    {
                        if (t.getType().equals("Producer"))
                        {
                            if (count > 0)
                            {
                                entire_file_sb.append(", ");
                            }
                            entire_file_sb.append(t.getValue());
                            count++;
                        }                    
                    }
                    entire_file_sb.append("</producer>\n");
                    entire_file_sb.append("<distributor>");
                    count = 0;
                    for (Tag t : this_res_tags)
                    {
                        if (t.getType().equals("Distributor"))
                        {
                            if (count > 0)
                            {
                                entire_file_sb.append("<br/><br/>");
                            }
                            entire_file_sb.append(t.getValue());
                            entire_file_sb.append("<br/>");
                            if (t.getInfo() != null)
                            {
                                entire_file_sb.append(t.getInfo().replace("\n", "<br/>"));
                            }
                            count++;
                        }
                    }
                    entire_file_sb.append("</distributor>\n");

                    
                // PRICE
                    entire_file_sb.append("<price>");
                    for (Tag t : this_res_tags)
                    {
                        if (t.getType().equals("Price"))
                        {
                            entire_file_sb.append(t.getValue());
                        }
                    }
                    entire_file_sb.append("</price>\n");

                // PAGINATION
                    entire_file_sb.append("<pagination>");
                    for (Tag t : this_res_tags)
                    {
                        if (t.getType().equals("Number of Pages"))
                        {
                            entire_file_sb.append(t.getValue());
                        }                    
                    }
                    entire_file_sb.append("</pagination>\n");

                // ISBN
                    entire_file_sb.append("<isbn>");
                    for (Tag t : this_res_tags)
                    {
                        if (t.getType().equals("ISBN"))
                        {
                            entire_file_sb.append(t.getValue());
                        }                    
                    }
                    entire_file_sb.append("</isbn>\n");

                // COPYRIGHT DATE
                    entire_file_sb.append("<copyright>");
                    for (Tag t : this_res_tags)
                    {
                        if (t.getType().equals("Copyright Date"))
                        {
                            entire_file_sb.append(t.getValue());
                        }                    
                    }
                    entire_file_sb.append("</copyright>\n");

                // YEAR RECOMMENDED
                    entire_file_sb.append("<year_recommended>");
                    int rec_year = 0;
                    for (Recommendation rc : this_res.getRecommendations())
                    {
                        if (rc.getRecommended() == 1)
                        {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(new Date(rc.getCreatedAt().getTime()));
                            rec_year = cal.get(Calendar.YEAR);
                        }
                    }
                    if (rec_year != 0)
                    {
                        entire_file_sb.append(rec_year);
                    }
                    entire_file_sb.append("</year_recommended>\n");

                    
            ArrayList<Integer> child_ids = this_res.getChildIDs();
            if (!child_ids.isEmpty())
            {
                entire_file_sb.append("<related_resource_table>\n");
                entire_file_sb.append("<tbody>\n");
                entire_file_sb.append("<tr>\n");
                        entire_file_sb.append("<td><p><b>Title</b></p></td>\n");
                        entire_file_sb.append("<td><p><b>Link</b></p></td>\n");
                entire_file_sb.append("</tr>\n");
            }
            for (Integer c_id : this_res.getChildIDs())
            {
                Resource child = new Resource(c_id);
                
                entire_file_sb.append("<tr>\n");
                    entire_file_sb.append("<td><p>");
                    entire_file_sb.append(child.getTitle());
                    entire_file_sb.append("</p></td>\n");
                    entire_file_sb.append("<td><p><a href=\"https://www.edonline.sk.ca/webapps/moe-curriculum-BBLEARN/index.jsp?view=resource_detail&amp;resource_file=/library/Curriculum%20Website/");
                    entire_file_sb.append(subject);
                    entire_file_sb.append("/Resources/Additional/");
                    entire_file_sb.append(child.getRNumber());
                    entire_file_sb.append(".xml\">View</a></p></td>\n");
                entire_file_sb.append("</tr>\n");
                
            }
            if (!child_ids.isEmpty())
            {
                entire_file_sb.append("</tbody>\n");
                entire_file_sb.append("</related_resource_table>\n");
            }
                    
                    
            entire_file_sb.append("</content>\n</resource>");

            
            
            
/* ================================================
 * =========== GET CLASSIFICATION IDS =============
 * ================================================
 */
        //String classificationID = manifestdoc.getNodeValue("subjects/subject[@name='" + subj + "']/levels/level[@id='" + level + "']/res_class_id");
            for (Tag group : tag_groups)
            {
                ArrayList<String> all_subjects = new ArrayList<String>();
                ArrayList<String> all_levels = new ArrayList<String>();
                ArrayList<String> all_programs = new ArrayList<String>();
                for (Tag t : group.getChildren(this_res.getDBID()))
                {
                    if (t.getType().equals("Subject"))
                    { all_subjects.add(t.getValue()); }
                    if (t.getType().equals("Program"))
                    { all_programs.add(t.getValue()); }
                    if ((t.getType().equals("Level")) || (t.getType().equals("Grade")))
                    { all_levels.add(t.getValue()); }
                }
                
                if (all_programs.size() > 0)
                {
                    int initial_size = all_subjects.size();
                    for (int i=0; i<initial_size; i++)
                    {
                        //all_subjects.set(i, all_subjects.get(i) + program_modifier);
                    }
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
                System.out.println("Error writing resource preview file " + file_name + " to " + base_path + ": " + e.getMessage());
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
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Preview Resource</title>");            
                out.println("</head>");
                out.println("<body>");
                out.println("<p><b>Here's the link: </b><p>");
                out.println("<a target=\"_blank\" href=\"");
                out.println(base_URL);
                out.println("/webapps/moe-curriculum-BBLEARN/index.jsp?view=resource_detail&resource_file=/library/Curriculum%20Website/New%20Resource%20Previews/" + file_name + "&dark_colour=d86558&light_colour=f7e0de\">Click here</a>");
                
                if ((get_domain.contains("bbtest")) || (get_domain.contains("bbdev")))
                {
                    out.println("<p>And here are all of the tags: </p>");
                    out.println("<ul>");
                    for (Tag t : this_res_tags)
                    {
                        out.println("<li>");
                        out.println(t.getType());
                        out.println(" - ");
                        out.println(t.getValue());
                        out.println("</li>");
                    }
                    out.println("</ul>");                
                }
                
                out.println("</body>");
                out.println("</html>");
            } finally {            
                out.close();
            }
            
            
        }
        else
        {
            // TODO: handle the cases where value of resource_id is invalid
            System.out.println("Error: Resource ID is invalid");
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
