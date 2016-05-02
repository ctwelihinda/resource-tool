/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.tagging;

import blackboard.platform.security.authentication.HttpAuthManager;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.EasyUser;
import stans.resourcerecord.dao.JoinPersister;
import stans.resourcerecord.dao.ResourceLoader;
import stans.resourcerecord.dao.ResourcePersister;
import stans.resourcerecord.helpers.TaggerPermissionsManager;
import stans.resourcerecord.helpers.ValidationHelpers;
import stans.resourcerecord.model.Resource;

/**
 *
 * @author peter
 */
public class AddResourceRelationship extends HttpServlet {

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
    // ######### AUTHENTICATION ###############
        // check if user is logged in, and redirect to login page if not
        BbSession bbSession = BbSessionManagerServiceFactory.getInstance().getSession(request);
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        
        if (!bbSession.isAuthenticated())
        {
            HttpAuthManager.sendLoginRedirect(request,response);
            return;
        }
        // also check if the user has the correct role(s) to be doing this, and give them a message if not
        EasyUser curr_easyuser = new EasyUser(request);
        try
        {
            boolean can_edit = false;
            for (String role_name : TaggerPermissionsManager.getAllAllowedRoles())
            {
                if (curr_easyuser.shortcuts.hasRoleId(role_name))
                { can_edit = true; }
            }
            
            if (!can_edit)
            {
                HttpAuthManager.sendAccessDeniedRedirect(request, response);
            }
            else
            {

                String resource_1_id = request.getParameter("resource_1_id");
                String resource_1_type = request.getParameter("resource_1_type");
                String resource_2_id = request.getParameter("resource_2_id");
                String resource_2_type = request.getParameter("resource_2_type");
                String resource_2_rnumber = request.getParameter("resource_2_rnumber");
                if (!resource_2_rnumber.startsWith("R"))
                { resource_2_rnumber = "R" + resource_2_rnumber; }

                if ((resource_2_id == null) && (resource_2_rnumber != null))
                {
                    resource_2_id = Integer.toString(ResourceLoader.loadByRNumber(resource_2_rnumber).getDBID());
                }    
                
                System.out.println("Resource 1 ID: " + resource_1_id);
                System.out.println("Resource 2 ID: " + resource_2_id);
                System.out.println("Resource 1 type: " + resource_1_type);
                System.out.println("Resource 2 type: " + resource_2_type);
                System.out.println("Resource 2 R Number: " + resource_2_rnumber);

                if (
                        (resource_1_id != null) &&
                        (resource_2_id != null) &&
                        (ValidationHelpers.isPositiveInteger(resource_1_id)) &&
                        (ValidationHelpers.isPositiveInteger(resource_2_id))
                )
                {
                    Integer result_id = JoinPersister.addResourceRelation(Integer.parseInt(resource_1_id), resource_1_type, Integer.parseInt(resource_2_id), resource_2_type);

                    sb.append("success");
                }
                else
                {
                    sb.append("error");
                }

                out.println(sb);
                out.close();
            }
        }
        catch (Exception e)
        {
        
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
