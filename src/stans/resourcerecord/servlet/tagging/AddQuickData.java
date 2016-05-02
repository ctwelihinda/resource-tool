/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.tagging;

import blackboard.platform.security.authentication.HttpAuthManager;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerServiceFactory;
import stans.resourcerecord.dao.JoinPersister;
import stans.resourcerecord.dao.TagPersister;
import stans.resourcerecord.helpers.TaggerPermissionsManager;
import stans.resourcerecord.model.Tag;
import stans.db.Query;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.EasyUser;
import stans.resourcerecord.dao.ResourcePersister;

/**
 *
 * @author peter
 */
public class AddQuickData extends HttpServlet {

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

                PrintWriter out = response.getWriter();
                StringBuilder sb = new StringBuilder();

                String type = request.getParameter("type");
                String value = request.getParameter("value");
                String resource_id = request.getParameter("resource_id");

                //System.out.println("AddQuickData: type=" + type);
                //System.out.println("AddQuickData: value=" + value);
                //System.out.println("AddQuickData: resource_id=" + resource_id);
                
                if ((type != null) && (value != null) && (resource_id != null))
                {
                    if (type.equals("title"))
                    {
						System.out.println( "AddQuickData:" + value );
                        ResourcePersister.setTitle(Integer.parseInt(resource_id), value);
                    }
                    if (type.equals("subtitle"))
                    {
                        ResourcePersister.setSubtitle(Integer.parseInt(resource_id), value);
                    }
                    if (type.equals("edition"))
                    {
                        ResourcePersister.setEdition(Integer.parseInt(resource_id), value);
                    }
                    if (type.equals("image"))
                    {
                        String new_image_path = "'" + value + "'";
                        //System.out.println("AddQuickData: new_image_path=" + new_image_path);
                        Query.update("moe_resource", "quick_pic", Integer.parseInt(resource_id), new_image_path);
                    }
                    if (type.equals("description"))
                    {
                        if (value.length() >= 508)
                        { value = value.substring(0, 508) + "..."; }
                        String new_desc = "'" + value + "'";
                        //System.out.println("AddQuickData: new_desc=" + new_desc);
                        Query.update("moe_resource", "quick_description", Integer.parseInt(resource_id), new_desc);
                    }
                }
                
                
                out.close();            
            }
        }
        catch (Exception e)
        {
            System.out.print(e.getStackTrace());
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
