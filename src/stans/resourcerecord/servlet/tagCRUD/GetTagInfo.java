/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.tagCRUD;

import blackboard.platform.security.authentication.HttpAuthManager;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerServiceFactory;
import stans.resourcerecord.helpers.TaggerPermissionsManager;
import stans.resourcerecord.model.Tag;
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
public class GetTagInfo extends HttpServlet {

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
        }
        catch (Exception e)
        {
        
        }
        
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        
        String tag_id = request.getParameter("tag_id");

        Tag this_tag = new Tag(Integer.parseInt(tag_id));
        
        if (this_tag != null)
        {
            String this_info = this_tag.getInfo();
            if ((this_info == null) || (this_info.equals("")))
            {
                sb.append("No information available");
            }
            else
            {
                sb.append(this_info.replace("\n", "<br/>"));
            }
        }
        else
        {
            sb.append("error");
        }
        
        out.println(sb);
        out.close();
        
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
