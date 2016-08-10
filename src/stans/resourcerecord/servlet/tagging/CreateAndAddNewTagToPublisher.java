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

/**
 *
 * @author peter
 */
public class CreateAndAddNewTagToPublisher extends HttpServlet {

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
        	System.out.println("Inside Create Tag");
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

                String resource_id = request.getParameter("publisher_id");
                String tag_value = request.getParameter("tag_value");
                String tag_type = request.getParameter("tag_type");
                String parent_id = request.getParameter("parent_id");

				System.out.println( "publisher_id = " + resource_id );
				System.out.println( "tag_value = " + tag_value );
				System.out.println( "tag_type = " + tag_type );
				
                ArrayList<String> tagtype_args = new ArrayList<String>();
                ArrayList<String> tagfind_args = new ArrayList<String>();
                tagtype_args.add(tag_type);

                ArrayList<Integer> tagtype_ids = Query.find("moe_tagtype", "type = ?", tagtype_args);

                if (tagtype_ids.size() > 0)
                {
                    tagfind_args.add(Integer.toString(tagtype_ids.get(0)));
                    tagfind_args.add(tag_value);

                    ArrayList<Integer> existing_tags = Query.find("moe_tag", "tagtype_id = ? AND value = ?", tagfind_args); // does this tag already exist?

                    Tag new_tag;
                    if (existing_tags.size() > 0) // if it exists, just create a tag object with the id
                    {
                        new_tag = new Tag(existing_tags.get(0));
                    }
                    else // otherwise add the new tag tso the db
                    {
                        new_tag = TagPersister.createNew(tag_value, tagtype_ids.get(0));
                    }
                    Integer joinID;
                    if(parent_id == null){
                    joinID = JoinPersister.addPublisherTagJoin(Integer.parseInt(resource_id), new_tag.getDBID());
                    } else { joinID = JoinPersister.addPublisherTagJoin(Integer.parseInt(resource_id), new_tag.getDBID(), Integer.parseInt(parent_id));}

                    

                    sb.append("{\"tags\":[");
                        sb.append("{\"id\":\"");
                        sb.append(Integer.toString(new_tag.getDBID()));
						sb.append("\", \"join_id\":\"");
						sb.append(Integer.toString( joinID ) );
                        sb.append("\", \"type\":\"");
                        sb.append(new_tag.getType());
                        sb.append("\", \"value\":\"");
                        sb.append(new_tag.getValue());
                        sb.append("\"}");
                    sb.append("]}");
                    
                    /*
                    sb.append("<li class=\"tag_row\" id=\"");
                    sb.append(Integer.toString(new_tag.getDBID()));
                    sb.append("\">");
                    sb.append(new_tag.getType());
                    sb.append(": ");
                    sb.append(new_tag.getValue());
                    sb.append("<span class=\"tag_remove_box\" id=\"");
                    sb.append(Integer.toString(new_tag.getDBID()));
                    sb.append("\">DELETE</span>");
                    sb.append("</li>");
                    */
                    out.println(sb);

                }
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
