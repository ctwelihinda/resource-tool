/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.tagging;

import blackboard.platform.security.authentication.HttpAuthManager;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerServiceFactory;
import stans.resourcerecord.dao.JoinPersister;
import stans.resourcerecord.helpers.TaggerPermissionsManager;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.EasyUser;
import stans.db.Query;
import stans.resourcerecord.helpers.ValidationHelpers;
import stans.resourcerecord.model.Resource;
import stans.resourcerecord.model.Tag;

/**
 *
 * @author peter
 */
public class RemoveTagsFromResource extends HttpServlet {

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
                String tag_id_string = request.getParameter("tag_id");
                String join_id_string = request.getParameter("join_id");
                String resource_id_string = request.getParameter("resource_id");

                if (
					( tag_id_string!=null ) &&
					( ValidationHelpers.isPositiveInteger( tag_id_string ) ) &&
					( resource_id_string!=null ) &&
					( ValidationHelpers.isPositiveInteger( resource_id_string ) )
                ) {
                    Integer tag_id = Integer.parseInt( tag_id_string );
                    Integer resource_id = Integer.parseInt( resource_id_string );

                    final int CORE_ID = 4220;
                    final int ROVER_ID = 117368;
                    final int SUPPORT_ID = 117372;

                    switch (tag_id)
                    {
                        case CORE_ID:
                            Query.update("moe_resource", "is_core", resource_id, "0");
                            break;
                        case ROVER_ID:
                            Query.update("moe_resource", "is_rover", resource_id, "0");
                            break;
                        case SUPPORT_ID:
                            Query.update("moe_resource", "is_core", resource_id, "0");
                            break;
                    }
                    
					
					if(
						( join_id_string!=null ) &&
						( ValidationHelpers.isPositiveInteger( join_id_string ) )
					) {
						JoinPersister.removeResourceTagJoin( Integer.parseInt( join_id_string ), new Resource( resource_id ) );
					} else {
						JoinPersister.removeResourceTagJoin(resource_id, tag_id);
					}

                    Tag t = new Tag(tag_id);
                    Resource r = new Resource(resource_id);

                    if (t.getType().equals("Title"))
                    {
                        //String quick_title = "'" + r.getQuickData("title").replace(t.getValue(), "") + "'";
                        //Query.update("moe_resource", "quick_title", resource_id, quick_title);
                    }
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
