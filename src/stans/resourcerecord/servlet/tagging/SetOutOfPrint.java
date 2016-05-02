/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.tagging;

import blackboard.platform.security.authentication.HttpAuthManager;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerServiceFactory;
import stans.resourcerecord.dao.ResourcePersister;
import stans.resourcerecord.helpers.TaggerPermissionsManager;
import stans.resourcerecord.helpers.ValidationHelpers;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.EasyUser;

/**
 *
 * @author peter
 */
public class SetOutOfPrint extends HttpServlet {

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
        StringBuilder sb = new StringBuilder();
        PrintWriter out = response.getWriter();
        
    // ######### AUTHENTICATION ###############
        // check if user is logged in, and redirect to login page if not
        BbSession bbSession = BbSessionManagerServiceFactory.getInstance().getSession(request);
        if (! bbSession.isAuthenticated())
        {
            sb.append("SetOutOfPrint: ERROR - user is not authenticated\n");
            out.println(sb);
            System.out.println(sb);
            out.close();
            HttpAuthManager.sendLoginRedirect(request,response);
            return;
        }
        // also check if the user has the correct role(s) to be doing this, and give them a message if not
        EasyUser curr_easyuser = new EasyUser(request);
        TaggerPermissionsManager permissions = new TaggerPermissionsManager(curr_easyuser);
        try
        {
            if (permissions.getPermissionLevel("outofprint") >= TaggerPermissionsManager.READ_WRITE)
            {
        
                String resource_id = request.getParameter("resource_id");
                String oop_value = request.getParameter("oop_value");

                if ((ValidationHelpers.isPositiveInteger(resource_id)) && (ValidationHelpers.isWholeNumber(oop_value)))
                {
                    if (ResourcePersister.setOutOfPrint(Integer.parseInt(resource_id), Integer.parseInt(oop_value)))
                    {
                        sb.append("SetOutOfPrint: resource ");
                        sb.append(resource_id);
                        sb.append(" now has an OOP value of ");
                        sb.append(oop_value);
                    }
                    else
                    {
                        sb.append("SetOutOfPrint: ERROR - ResourcePersister.setOutOfPrint failed\n");
                        sb.append("--resource_id: ");
                        sb.append(resource_id);
                        sb.append("\n");
                        sb.append("--oop_value: ");
                        sb.append(oop_value);
                        sb.append("\n");
                    }
                }
                else
                {
                    sb.append("SetOutOfPrint: ERROR - numeric validation error\n");
                    sb.append("--resource_id: ");
                    sb.append(resource_id);
                    sb.append("\n");
                    sb.append("--oop_value: ");
                    sb.append(oop_value);
                    sb.append("\n");
                }

            }
            else
            {
                sb.append("SetOutOfPrint: ERROR - user does not have the proper roles\n");
                HttpAuthManager.sendAccessDeniedRedirect(request, response);
            }
        }
        catch (Exception e)
        {
        
        }
        finally
        {
            System.out.println(sb);
            out.println(sb);
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
