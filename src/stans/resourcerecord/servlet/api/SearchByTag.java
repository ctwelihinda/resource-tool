/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.db.Query;
import stans.db.Enumerators;
import stans.resourcerecord.dao.ResourceLoader;
import stans.resourcerecord.dao.TagLoader;
import stans.resourcerecord.dao.TagTypeLoader;
import stans.resourcerecord.helpers.ValidationHelpers;
import stans.resourcerecord.model.Resource;
import stans.resourcerecord.model.Tag;
import stans.resourcerecord.model.TagType;

/**
 *
 * @author peter
 */
public class SearchByTag extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String tag_type = request.getParameter("type");
        String tag_value = request.getParameter("value");
        String tag_id = request.getParameter("id");
        String sa = request.getParameter("search_all");
        String rover_only = request.getParameter("rover_only");
        
        boolean search_all = false;
        if ((sa != null) && (sa.equals("true")))
        { search_all = true; }
        
        if (tag_type == null)
        { tag_type = "Cover Image Path"; }
        if (tag_value == null)
        { tag_value = "http://newstalk650.com/sites/default/files/news-image/936810_10151605530572139_2133682416_n.jpg"; }
        
        Tag t;
        if ((tag_id != null) && (ValidationHelpers.isPositiveInteger(tag_id)))
        { t = new Tag(Integer.parseInt(tag_id)); }
        else
        {
            TagType tt = TagTypeLoader.loadByType(tag_type);
            t = TagLoader.loadByTagTypeAndTagValue(tag_value, tt);
        }
        
        ArrayList<Resource> results = ResourceLoader.loadByTag(t);
        
        try {
            boolean first = true;
            out.println("{\"resources\":[");
            
            for (Resource this_res : results)
            {
                Integer is_live = this_res.getFinalRecommendation();
                
                if ((is_live == 1) || (search_all))
                {
                    if (!first)
                    { out.println(","); }
                    else
                    { first = false; }

                    String this_title = this_res.getTitle();
                    String this_desc = this_res.getQuickData("desc");
                    String this_pic = this_res.getQuickData("image");
                    String this_info = this_res.getQuickData("info");
                    String this_rnumber = this_res.getRNumber();

                    if (this_title != null)
                    { this_title = this_title.replace("\"", "\\\""); }
                    if (this_desc != null)
                    { this_desc = this_desc.replace("\"", "\\\"").replace("\n", "").replace("\r", "").replace(String.valueOf((char) 0x0a), ""); }

                    out.println("{\"db_id\":\""
                            + Integer.toString(this_res.getDBID())
                            + "\", \"title\":\""
                            + this_title
                            + "\", \"description\":\""
                            + this_desc
                            + "\", \"rnumber\":\""
                            + this_rnumber
                            + "\", \"pic_url\":\""
                            + this_pic
                            + "\", \"info\":\""
                            + this_info
                            + "\"}");
                }
            }
            //out.println("{\"title\":\"json test 1\", \"description\":\"json test 1 desc\", \"pic_url\":\"https://scontent-a-sea.xx.fbcdn.net/hphotos-xpa1/v/t1.0-9/10336713_602434626531279_4459806218008741669_n.jpg?oh=787bff7a9625a0123dedd561e3d98ebb&oe=5504A5EF\", \"info\":\"info 1\"},");
            //out.println("{\"title\":\"json test 2\", \"description\":\"json test 2 desc\", \"pic_url\":\"http://newstalk650.com/sites/default/files/news-image/936810_10151605530572139_2133682416_n.jpg\", \"info\":\"info 2\"}");
            out.println("]}");
        } catch (Exception e) {
                System.out.println("QuickSearch servlet: Error\n");
                e.printStackTrace(System.out);
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
