package stans.resourcerecord.servlet.tagCRUD;


	import blackboard.platform.security.authentication.HttpAuthManager;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerServiceFactory;
import stans.resourcerecord.dao.PubDistPersister;
import stans.resourcerecord.dao.ResourcePersister;
import stans.resourcerecord.helpers.TaggerPermissionsManager;
import stans.resourcerecord.model.PubDistRecord;
import stans.resourcerecord.model.Resource;
import stans.db.Query;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import stans.EasyUser;

	/**
	 *
	 * @author chamath
	 */
	public class CreatePubDist extends HttpServlet {

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
	            
	            boolean can_create = false;
	            if (curr_easyuser.shortcuts.hasRoleId("resource_tool_full_admin"))
	            { can_create = true; }
	            
	            if (!can_create)
	            {
	                HttpAuthManager.sendAccessDeniedRedirect(request, response);
	            }
	            else
	            {
	                String status_msg;
	                String dispatch_url;

	                PubDistRecord new_pub = PubDistPersister.createNew();

	                if (new_pub != null)
	                {
	                    int pk1 = new_pub.getDBID();
	                    //System.out.println("The pk1 of the new Resource is " + Integer.toString(pk1));
	                    status_msg = "New resource successfully created<br/>The DB ID is: " + pk1;
	                    dispatch_url = "/webapps/moe-resource_tool_final-BBLEARN/EditPubDist?publisher_id=" + pk1;

	                }
	                else
	                {
	                    dispatch_url = "/webapps/moe-resource_tool_final-BBLEARN/index.jsp?status=fail";
	                    //status_msg = "Unsuccessful resource creation";
	                }

	                //request.setAttribute("servlet_status", status_msg);

	                //RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(dispatch_url);
	                //dispatcher.forward(request, response) ;
	                response.sendRedirect(dispatch_url);
	            }
	        }
	        catch (Exception e)
	        {
	            System.out.println("CreateResource servlet: Unsuccessful resource creation");
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
