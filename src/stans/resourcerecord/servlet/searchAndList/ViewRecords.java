/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.searchAndList;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import stans.resourcerecord.dao.ResourceLoader;
import stans.resourcerecord.helpers.ValidationHelpers;
import stans.resourcerecord.model.Resource;
import stans.resourcerecord.model.ResourceRecord;

/**
 *
 * @author peter
 */
public class ViewRecords extends HttpServlet {

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
        
        String r_id_param = request.getParameter("resource_ids");
        String language = request.getParameter("language");
        String r_number = request.getParameter("record_number");
        
        if ((language == null) || (language.equals("")))
        { language = "en"; }
        
        ResourceRecord[] resource_records = new ResourceRecord[0];
               
        if (r_id_param != null)
        {
            String[] id_params = r_id_param.split("-");
            System.out.println(r_id_param);
            System.out.println(id_params[0]);
            resource_records = new ResourceRecord[id_params.length];

            for (int i=0; i<id_params.length; i++)
            {
                if (ValidationHelpers.isPositiveInteger(id_params[i]))    { 
                		resource_records[i] = new ResourceRecord(new Resource(Integer.parseInt(id_params[i])));
                		//resource_records[i] = new ResourceRecord(ResourceLoader.loadByRNumber(id_params[i]));
                	}
            }

        } else if(r_number != null) 
        {
        	String[] params = r_number.split("-");
        	resource_records = new ResourceRecord[params.length];
        	
        	for(int i= 0; i < params.length; i++){
        		resource_records[i] = new ResourceRecord(ResourceLoader.loadByRNumber(params[i]));
        	}
        }
        
        request.setAttribute("language", language);
        request.setAttribute("resource_records", resource_records);
        System.out.println("Inside ViewRecords.java, rr = " + resource_records[0].getRnumber() );
        RequestDispatcher dispatcher = request.getRequestDispatcher("/view_records.jsp");
        dispatcher.forward(request, response);
        
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
