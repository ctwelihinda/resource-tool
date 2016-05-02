/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.tagCRUD;

import blackboard.platform.security.authentication.HttpAuthManager;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerServiceFactory;
import stans.resourcerecord.model.Recommendation;
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
public class DeleteRecord extends HttpServlet {

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
            if (!curr_easyuser.shortcuts.hasRoleId("stf_resource_evaluation_administrator"))
            {
                HttpAuthManager.sendAccessDeniedRedirect(request, response);
            }
            else
            {    
        /*        
        ArrayList<String> args = new ArrayList<String>();
        args.add("1");
        if (Query.delete("moe_resource", "pk1 = ?", args) > 0)
        {
            System.out.println("Resource number 1 deleted");
        }
*/
/*        
        ArrayList<String> args = new ArrayList<String>();
        args.add("'Vocal Jazz'");
        if (Query.delete("moe_tag", "value = ?", args) > 0)
        {
            //System.out.println("Resource number 1 deleted");
            System.out.println("Tag deleted");
        }
*/
/*        ArrayList<String> args1 = new ArrayList<String>();
        ArrayList<String> args2 = new ArrayList<String>();
        args1.add("R100099");
        args2.add("R100103");
        ArrayList<Integer> ids1 = Query.find("moe_resource", "entry_id = ?", args1);
        ArrayList<Integer> ids2 = Query.find("moe_resource", "entry_id = ?", args2);
        Query.update("moe_resource", "parent_id", ids1.get(0), "0");
        Query.update("moe_resource", "parent_id", ids2.get(0), "0");
                ArrayList<String> del_args = new ArrayList<String>();
                ArrayList<String> find_args = new ArrayList<String>();
                del_args.add("43877");
                del_args.add("55621");
                find_args.add("55621");

                ArrayList<Integer> find_ids = Query.find("moe_recommendation", "resource_id = ?", find_args);
                for (Integer id : find_ids)
                {
                    Recommendation r = new Recommendation(id);

                    System.out.println("---------");
                    System.out.println(r.getDBID());
                    System.out.println(r.getComments());
                    System.out.println(r.getCreatedAt());
                    System.out.println(r.getRecommended());
                    System.out.println(r.getResourceID());
                    System.out.println(r.getCreatedBy());

                }
*/

//                int del_success = Query.delete("moe_recommendation", "pk1 = ? AND resource_id = ?", del_args);

//                System.out.println("Deleted " + del_success + " recs");
                
//////////////////////////////////////////////
// DELETE MULTIPLE TAGS                
                ArrayList<Integer> pk1s = new ArrayList<Integer>();
                pk1s.add(4255);
                
                System.out.println("The following tags will be deleted:");
                for (Integer this_pk1 : pk1s)
                {
                    ArrayList<String> del_args = new ArrayList<String>();
                    del_args.add(Integer.toString(this_pk1));
                    //Query.delete("moe_tag", "pk1 = ?", del_args);
                    //System.out.println((String)Query.select("moe_tag", "value", this_pk1));
                }
                
////////////////////////////////////////////////
// ADD INFO TO QUICK_* COLUMNS
/*                Query.update("moe_resource", "quick_title", 2, "'Quick Title Test 1'");
                Query.update("moe_resource", "quick_title", 3, "'Quick Title Test 2'");
                Query.update("moe_resource", "quick_description", 2, "'This is the quick description'");
                Query.update("moe_resource", "quick_description", 3, "'This is another quick description of resource R100001'");
                Query.update("moe_resource", "quick_pic", 2, "'https://scontent-a-sea.xx.fbcdn.net/hphotos-xpa1/v/t1.0-9/10336713_602434626531279_4459806218008741669_n.jpg?oh=787bff7a9625a0123dedd561e3d98ebb&oe=5504A5EF'");
                Query.update("moe_resource", "quick_pic", 3, "'http://newstalk650.com/sites/default/files/news-image/936810_10151605530572139_2133682416_n.jpg'");
                Query.update("moe_resource", "quick_info", 2, "'PAA5/7/8/9/10::SCIENCE5'");
                Query.update("moe_resource", "quick_info", 3, "'SS5/6::ELA5::ARTS5'");
 */               
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
