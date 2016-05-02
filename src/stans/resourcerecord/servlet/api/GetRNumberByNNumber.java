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
public class GetRNumberByNNumber extends HttpServlet {

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
            String n_number = request.getParameter("n_number");
            
            if (n_number != null)
            {
                Resource r = ResourceLoader.loadByRNumber(n_number);
                Tag t = TagLoader.loadByTagTypeAndTagValue(n_number, new TagType(201));
                ArrayList<Resource> matching_resources = ResourceLoader.loadByTag(t);
                
                String r_number = "unknown";
                
                if ((matching_resources != null) && (!matching_resources.isEmpty()))
                { r_number = matching_resources.get(0).getRNumber(); }
                
                out.print("{");
                    out.print("\"resource\":");
                    out.print("{");
                        out.print("\"r_number\": \"" + r_number + "\"");
                    out.print("}");
                out.print("}");
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
