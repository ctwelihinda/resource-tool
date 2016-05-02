/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.resourcerecord.dao.ResourceLoader;
import stans.resourcerecord.model.Resource;

/**
 *
 * @author peter
 */
public class GetUpdatedAtTimestamp extends HttpServlet {

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
            String r_number = request.getParameter("r_number");
            String jsonp = request.getParameter("jsonp");
            
            boolean is_jsonp = false;
            if ((jsonp != null) && (jsonp.equals("true")))
            { is_jsonp = true; }
            
            if (r_number != null)
            {
                Resource r = ResourceLoader.loadByRNumber(r_number);
				if( r!=null ) {
					String resource_id = Integer.toString(r.getDBID());
					Timestamp updatedAt = ( r.getUpdatedAt()==null ) ? r.getCreatedAt() : r.getUpdatedAt();
					if( updatedAt==null ) {
						updatedAt = new Timestamp( 0 );
					}
					String updated_at = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( updatedAt ) ).toString();
					if (is_jsonp) { out.println("jsonCallback("); }
						out.println("{");
							out.println("\"timestamps\":");
							out.println("{");
								out.println("\"resource_id\": \"" + resource_id + "\",");
								out.println("\"updated_at\": \"" + updated_at + "\"");
							out.println("}");
						out.println("}");
					if (is_jsonp) { out.println(");"); }
				}
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
