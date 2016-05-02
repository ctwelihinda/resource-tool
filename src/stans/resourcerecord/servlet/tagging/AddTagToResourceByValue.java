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
import stans.resourcerecord.dao.TagTypeLoader;
import stans.resourcerecord.helpers.TaggerPermissionsManager;
import stans.resourcerecord.model.Tag;
import stans.resourcerecord.model.TagType;
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
public class AddTagToResourceByValue extends HttpServlet {

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

                String resource_id = request.getParameter("resource_id");
                String parent_id = request.getParameter("parent_id");
                String tag_value = request.getParameter("tag_value");
                String tag_type;

                if (tag_value.contains("---"))
                {
                    tag_type = tag_value.split("---")[0];
                    tag_value = tag_value.split("---")[1];
                }
                else
                {
                    tag_type = request.getParameter("tag_type");
                }
                Integer type_id = TagTypeLoader.loadByType(tag_type).getDBID();
                Integer tag_id = 0;
                ArrayList<String> args = new ArrayList<String>();
                args.add(tag_value); 
                args.add(Integer.toString(type_id));
                ArrayList<Integer> search_results = Query.find("moe_tag", "value = ? AND tagtype_id = ?", args);
                if (resource_id != null)
                {
                    Tag t = null;
					Integer joinID = 0;
					
                    if (!search_results.isEmpty())
                    {
                        tag_id = search_results.get(0);

                        //boolean result = false;
                        if (parent_id == null)
                        { joinID = JoinPersister.addResourceTagJoin(Integer.parseInt(resource_id), tag_id); }
                        else
                        { joinID = JoinPersister.addResourceTagJoin(Integer.parseInt(resource_id), tag_id, Integer.parseInt(parent_id)); }

                        t = new Tag(tag_id);
                    }
                    else if (search_results.isEmpty())
                    {
                        t = TagPersister.createNew(tag_value, type_id);
                        tag_id = t.getDBID();

                        //boolean result = false;
                        if (parent_id == null)
                        { joinID = JoinPersister.addResourceTagJoin(Integer.parseInt(resource_id), tag_id); }
                        else
                        { joinID = JoinPersister.addResourceTagJoin(Integer.parseInt(resource_id), tag_id, Integer.parseInt(parent_id)); }
                    }

                    if (t != null)
                    {

                        sb.append("{\"tags\":[");
                            sb.append("{\"id\":\"");
                            sb.append(Integer.toString(t.getDBID()));
                            sb.append("\", \"join_id\":\"");
                            sb.append(Integer.toString( joinID ) );
                            sb.append("\", \"type\":\"");
                            sb.append(t.getType());
                            sb.append("\", \"value\":\"");
                            sb.append(t.getValue());
                            sb.append("\"}");
                        sb.append("]}");
                        /*
                        sb.append("<div class=\"tag_row\">");
                        sb.append(t.getType());
                        sb.append(": ");
                        sb.append(t.getValue());
                        sb.append("<span class=\"tag_remove_box\" id=\"");
                        sb.append(Integer.toString(t.getDBID()));
                        sb.append("\">DELETE</span>");
                        sb.append("</div>");
                        */
                    }
                    else
                    {
                        sb.append("error");
                    }
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
