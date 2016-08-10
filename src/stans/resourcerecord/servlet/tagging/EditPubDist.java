package stans.resourcerecord.servlet.tagging;


import blackboard.platform.security.authentication.HttpAuthManager;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerServiceFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import stans.EasyUser;
import stans.db.Query;
import stans.resourcerecord.dao.*;
import stans.resourcerecord.helpers.TaggerPermissionsManager;
import stans.resourcerecord.model.*;


/**
 *
 * @author peter
 */
public class EditPubDist extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        
        
        
////////////////////////////////////////////////////////////////////////////////////////////////////////
// AUTHENTICATION

    boolean logged_in = false;
    BbSession bbSession = BbSessionManagerServiceFactory.getInstance().getSession(request);
    if( !bbSession.isAuthenticated() ) {
        HttpAuthManager.sendLoginRedirect( request,response );
        return;
    } else {
        logged_in = true;
    }

    
    


////////////////////////////////////////////////////////////////////////////////////////////////////////
// BASIC INFO
                     
    EasyUser curr_user = new EasyUser(request);
    TaggerPermissionsManager permissions = new TaggerPermissionsManager(curr_user);
       
    String RId = request.getParameter("publisher_id");
    
    PubDistRecord this_resource = null;
	
    if( ( RId == null ) ) {
		this_resource = new PubDistRecord( Integer.parseInt( RId ) );
	} else {
		this_resource = PubDistLoader.loadByDBID(Integer.parseInt(RId));
	}
    
    int this_resource_id = this_resource.getDBID();
    System.out.println(this_resource_id + " after Basic Info");
            

////////////////////////////////////////////////////////////////////////////////////////////////////////
// TAGS
    
    HashMap< String,ArrayList<Tag> > tags = new HashMap< String,ArrayList<Tag> >();
    HashMap< String,ArrayList<Tag> > tag_options = new HashMap< String,ArrayList<Tag> >();
    HashMap< String,ArrayList<String> > tag_option_names = new HashMap< String,ArrayList<String> >();
    

    tag_option_names.put(
        "phone",
        new ArrayList<String>(Arrays.asList(
            "Primary Phone",
            "Other Phone",
            "Fax"

        ))
    );
    tag_options.put(
    		"contacts",
    		TagLoader.loadByTypeName("Representative")
    );

    tag_option_names.put(
        "address",
        new ArrayList<String>(Arrays.asList(

        	"Street Address",
        	"Postal Code",
        	"Zip Code",
        	"City",
        	"Province",
        	"State",
        	"Country"

        ))
    );
    
    tag_option_names.put(
    		"country",
    		new ArrayList<String>(Arrays.asList(
    				"Canada",
    				"USA",
    				"UK"
    				))
    		);
    tag_option_names.put(
        "digital",
        new ArrayList<String>(Arrays.asList(
            "Email",
            "Website"
        ))
    );

    
    tag_options.put(
        "subject",
        TagLoader.loadByTypeName("Subject")
    );
    tag_options.put(
            "other_curriculum",
            TagLoader.loadByTypeName("Country")
        );
    //tag_options.get("other_curriculum").addAll(TagLoader.loadByTypeName("Resource List Classification"));
    tag_options.put(
    		"status",
    		TagLoader.loadByTypeName("Active")
    );

    
    tags.put(   "phone",		new ArrayList<Tag>());
    tags.put(   "contacts",		new ArrayList<Tag>());
    tags.put(   "address",		new ArrayList<Tag>());
    tags.put(   "digital",		new ArrayList<Tag>());
    tags.put(   "other",		new ArrayList<Tag>());
    tags.put(	"groups", 		new ArrayList<Tag>());
    tags.put(   "status",		new ArrayList<Tag>());
    tags.put("PubContact", 		new ArrayList<Tag>());

    for (Tag t : this_resource.getRootTags())
    {
    	System.out.println(t.getType() + " " + t.getValue() + " " + t.getJoinID() +  " " + t.getChildren(this_resource_id).toString() );
        if (tag_option_names.get("phone").contains(t.getType()))           { tags.get("phone").add(t); }
        else if (tag_option_names.get("digital").contains(t.getType()))		{ tags.get("digital").add(t);}
        else if (tag_option_names.get("address").contains(t.getType()))		{ tags.get("address").add(t);}
        else if (t.getType().equals("Active"))								{ tags.get("status").add(t); }
        else if(t.getType().equals("PubContact"))							{ tags.get("PubContact").add(t);}
        else if (t.getType().equals("Tag Group"))                           { tags.get("groups").add(t); System.out.println( "TAG GROUP:  " + 
        t.getChildren(this_resource_id).toString()); }
        else if (tag_option_names.get("other").contains(t.getType()))       { tags.get("other").add(t); }
        else                                                                { tags.get("misc").add(t); }
    }




    
    
                      
////////////////////////////////////////////////////////////////////////////////////////////////////////
// QUICK INFO

        String title = (String) Query.select("moe_publisher", "name", this_resource_id);
		System.out.println( "EditPublisher:" + title );

        String quick_image_path = (String) Query.select("moe_publisher", "quick_pic", this_resource_id);

        String page_image = quick_image_path;

        String page_title = title;

        if (title == null)
        {
            title = "<i>not available</i>";
            page_title = "<i>not available</i>";
        }
       
        if (quick_image_path == null)
        { quick_image_path = "<i>not available</i>"; }
        
    
    
    
    
    



////////////////////////////////////////////////////////////////////////////////////////////////////////
// USER AND DATE

        String role_text = "";
        if (curr_user.blackboard.getBatchUid().equals("peter.broda"))
        {
            ArrayList<Integer> roles = new ArrayList<Integer>();
            String role = request.getParameter("role");
            if (role != null)
            {
                roles.add(Integer.parseInt(role));
                permissions.addRoles(roles);
                role_text = " as role " + role;
            }
        }

        Calendar curr_cal = Calendar.getInstance();
        SimpleDateFormat date_format = new SimpleDateFormat("MMM d, yyyy");
        String curr_date = date_format.format(curr_cal.getTime());

        
        
        
        
    
////////////////////////////////////////////////////////////////////////////////////////////////////////
// FORWARD TO JSP
        request.setAttribute("tags", tags);
        request.setAttribute("tag_options", tag_options);
        request.setAttribute("tag_option_names", tag_option_names);
        
        request.setAttribute("this_resource", this_resource);

        request.setAttribute("user_name", curr_user.blackboard.getUserName());
        request.setAttribute("curr_date", curr_date);

        request.setAttribute("permissions", permissions);


        request.setAttribute("role_text", role_text);
        request.setAttribute("page_title", page_title);

        request.setAttribute("page_image", page_image);
        request.setAttribute("title", title);

        request.setAttribute("quick_image_path", quick_image_path);


        String next_jsp = "/pubdisttagger.jsp";
        if (!logged_in)
        { next_jsp = "/getout.jsp"; }
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(next_jsp);
        dispatcher.forward(request,response);
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
