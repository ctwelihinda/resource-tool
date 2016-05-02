/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.export;

import stans.resourcerecord.dao.ResourceLoader;
import stans.resourcerecord.model.Resource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author peter
 */
public class CheckIfLive extends HttpServlet {

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
        ArrayList<Resource> live_list = new ArrayList<Resource>();
        ArrayList<Resource> dead_list = new ArrayList<Resource>();
        ArrayList<Resource> approved_list = new ArrayList<Resource>();
        ArrayList<Resource> uncategorized_list = new ArrayList<Resource>();
        
        for (String resource_id : resource_id_list)
        {
            Resource this_res;
            
            if (uses_R_numbers)
            { this_res = ResourceLoader.loadByRNumber(resource_id); }
            else
            { this_res = new Resource(Integer.parseInt(resource_id)); }
            
            if (this_res.getFinalRecommendation() == 0)
            {
                dead_list.add(this_res);
            }
            else if (this_res.getFinalRecommendation() == 1)
            {
                live_list.add(this_res);
            }
            else if (this_res.getFinalRecommendation() == 2)
            {
                approved_list.add(this_res);
            }
            else
            {
                uncategorized_list.add(this_res);
            }
            
        }

/* ==================================================
* =========== GENERATE AND PRINT PAGE ===============
* ===================================================
*/
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Live Listing</title>");            
            out.println("</head>");
            out.println("<body>");
            
            out.println("<p><b>These videos are live:</b><p>");
            out.println("<ul>");
            for (Resource r : live_list)
            {
                out.println("<li>");
                out.println(r.getRNumber());
                out.println(" - ");
                out.println(r.getTitle());
                out.println("</li>");
            }
            out.println("</ul>");
            out.println("<p><b>...these are approved but NOT live:</b><p>");
            out.println("<ul>");
            for (Resource r : approved_list)
            {
                out.println("<li>");
                out.println(r.getRNumber());
                out.println(" - ");
                out.println(r.getTitle());
                out.println("</li>");
            }
            out.println("</ul>");
            out.println("<p><b>...these are NOT approved or live:</b><p>");
            out.println("<ul>");
            for (Resource r : dead_list)
            {
                out.println("<li>");
                out.println(r.getRNumber());
                out.println(" - ");
                out.println(r.getTitle());
                out.println("</li>");
            }
            out.println("</ul>");
            out.println("<p><b>...and these are uncategorized:</b><p>");
            out.println("<ul>");
            for (Resource r : uncategorized_list)
            {
                out.println("<li>");
                out.println(r.getRNumber());
                out.println(" - ");
                out.println(r.getTitle());
                out.println("</li>");
            }
            out.println("</ul>");

            out.println("</body>");
            out.println("</html>");
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
