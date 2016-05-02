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
import stans.resourcerecord.model.Resource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.EasyMailer;
import stans.EasyUser;

/**
 *
 * @author peter
 */
public class AddFinalRecommendation extends HttpServlet {

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
        
                String resource_id = request.getParameter("resource_id");
                String rec_value = request.getParameter("rec_value");
                StringBuilder sb = new StringBuilder();
                PrintWriter out = response.getWriter();

                if ((ValidationHelpers.isPositiveInteger(resource_id)) && (ValidationHelpers.isWholeNumber(rec_value)))
                {
                    int res_id = Integer.parseInt(resource_id);
                    ResourcePersister.addFinalRecommendation(res_id, Integer.parseInt(rec_value));
                    
                    Resource r = new Resource(res_id);
                    String r_number = r.getRNumber();

                    String email_subject = "Resource Tool: " + r_number + " (" + Integer.toString(res_id) + ": "+ r.getTitle() + ")";
                    String partial_email_subject = "";
                    String email_to = "";
                    String email_cc = "";
                    
                    Integer rec_value_int = Integer.parseInt(rec_value);
                    
                    switch (rec_value_int)
                    {
                        case 0:
                            partial_email_subject = " has been made unlive";
                            email_to = "Chamath.Welihinda@gov.mb.ca";
                            email_cc = "";
                            break;
                        case 1:
                            partial_email_subject = " is now live";
                            email_to = "Chamath.Welihinda@gov.mb.ca";
                            email_cc = "";
                            break;
                        case 2:
                            partial_email_subject = " has been approved. Please proofread and make live.";
                            email_to = "Chamath.Welihinda@gov.mb.ca";
                            //email_cc = "peter.broda@gov.sk.ca";
                            break;
                    }
                    email_subject = email_subject + partial_email_subject;
                    
                    EasyMailer mailer = new EasyMailer();
                    mailer.setTo(email_to);
                    mailer.setSubject(email_subject);
                    mailer.setBody("Title: " + r.getTitle());
                    mailer.setBody("<br/><a target=\"_blank\" href=\"" + r.getRNumber() + "\">VIEW</a>");
                    if (!email_cc.equals(""))
                    { mailer.setCc(email_cc); }

                // Send it
                    try
                    {
                        System.out.println(email_subject);
                        mailer.send();
                    }
                    catch (MessagingException me)
                    {
                        System.out.println("AddFinalRecommendation: Mailer didn't work!");
                        me.printStackTrace(System.out);
                    }
                }
                else
                {
                    System.out.println("AddFinalRecommendation: invalid values");
                    System.out.println("resource_id: " + resource_id);
                    System.out.println("rec_value: " + rec_value);
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
