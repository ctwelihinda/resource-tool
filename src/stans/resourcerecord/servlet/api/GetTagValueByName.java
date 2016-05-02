/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.db.Query;
import stans.resourcerecord.dao.ResourceLoader;
import stans.resourcerecord.dao.TagLoader;
import stans.resourcerecord.dao.TagTypeLoader;
import stans.resourcerecord.model.Resource;
import stans.resourcerecord.model.Tag;
import stans.resourcerecord.model.TagType;

/**
 *
 * @author peter
 */
public class GetTagValueByName extends HttpServlet {

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
        try {
            String jsonp = request.getParameter("jsonp");
            String r_number = request.getParameter("r_number");
            String tag_name = request.getParameter("tag_name");
            
            boolean is_jsonp = false;
            if ((jsonp != null) && (jsonp.equals("true")))
            { is_jsonp = true; }
            
            if ((r_number != null) && (tag_name != null))
            {
                Resource r = ResourceLoader.loadByRNumber(r_number);
                TagType tt = TagTypeLoader.loadByType(tag_name);
                Integer tt_id = tt.getDBID();
                
                ArrayList<Tag> all_tags_for_resource = TagLoader.loadByResourceDBID(r.getDBID());
                
                Integer t_type = 0;
                int i=0;
                String tag_value = "";
                while ((t_type != tt_id) && (i < all_tags_for_resource.size()))
                {
                    t_type = all_tags_for_resource.get(i).getTypeID();
                    if (t_type == tt_id)
                    { tag_value = all_tags_for_resource.get(i).getValue(); }
                    else
                    { i++; }
                }
                
                String resource_id = Integer.toString(r.getDBID());
                if (is_jsonp) { out.println("jsonCallback("); }
                    out.print("{");
                        out.print("\"resource\":");
                        out.print("{");
                            out.print("\"resource_id\": \"" + resource_id + "\",");
                            out.print("\"tag_value\": \"" + tag_value + "\"");
                        out.print("}");
                    out.print("}");
                if (is_jsonp) { out.print(");"); }
            }
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
