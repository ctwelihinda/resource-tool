/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.tagging;

import blackboard.data.user.User;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManager;
import blackboard.platform.context.ContextManagerFactory;
import blackboard.platform.security.authentication.HttpAuthManager;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerServiceFactory;
import stans.resourcerecord.dao.FlagLoader;
import stans.resourcerecord.dao.FlagPersister;
import stans.resourcerecord.helpers.TaggerPermissionsManager;
import stans.resourcerecord.model.Flag;
import stans.db.Query;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.EasyUser;

/**
 *
 * @author peter
 */
public class UpdateFlagComments extends HttpServlet {

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
        if (! bbSession.isAuthenticated())
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
                StringBuilder sb = new StringBuilder();

                String flag_id = request.getParameter("flag_id");
                String comments = "'" + request.getParameter("comments") + "'";


                Flag the_flag = new Flag(Integer.parseInt(flag_id));

                if (the_flag != null)
                {
                    FlagPersister.updateComments(Integer.parseInt(flag_id), comments);
                }
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
