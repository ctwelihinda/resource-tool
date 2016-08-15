/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class EditResource extends HttpServlet {

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
       
    String RNumber = request.getParameter("resource_number");
    String RId = request.getParameter("resource_id");
    
	Resource this_resource = null;
	
    if( ( RNumber==null )&&( RId!=null ) ) {
		this_resource = new Resource( Integer.parseInt( RId ) );
	} else {
		this_resource = ResourceLoader.loadByRNumber(RNumber);
	}
    
    int this_resource_id = this_resource.getDBID();

    // flags
    Flag flag = null;
    ArrayList<Flag> flags = FlagLoader.loadAllByResourceDBID(this_resource_id);
    if (flags.size() > 0)
    {
        flag = new Flag(flags.get(0).getDBID());
    }
    
    Boolean is_rover = this_resource.isROVER();
    Boolean is_core = this_resource.isCore();
    

            

////////////////////////////////////////////////////////////////////////////////////////////////////////    
// RECOMMENDATIONS
    
    ArrayList<Recommendation> all_recs = this_resource.getRecommendations();
    int rec_status = 0; 
    int rec_total = 0;
    for (Recommendation r : all_recs)
    { rec_total += r.getRecommended(); }
    // rec_status values:
    // --------------------
    // 0 = no recs on record
    // 1 = recommended
    // 2 = not recommended
    // 3 = both recommended and not
    if ((rec_total == all_recs.size()) && (all_recs.size() > 0)) // all recs were positive
    { rec_status = 1; }
    if ((rec_total > 0) && (rec_total < all_recs.size())) // some were positive, some negative
    { rec_status = 3; }
    if ((rec_total == 0) && (all_recs.size() > 0)) // all were negative, since there are recs but none with a value other than 0
    { rec_status = 2; }

    
    
    
   
    
    
    
    
    
    
    
////////////////////////////////////////////////////////////////////////////////////////////////////////
// TAGS
    
    HashMap< String,ArrayList<Tag> > tags = new HashMap< String,ArrayList<Tag> >();
    HashMap< String,ArrayList<Tag> > tag_options = new HashMap< String,ArrayList<Tag> >();
    HashMap< String,ArrayList<String> > tag_option_names = new HashMap< String,ArrayList<String> >();
    
    tag_option_names.put(
        "title",
        new ArrayList<String>(Arrays.asList(
//            "Title",
//            "Subtitle",
//            "Edition",
            "Series Title",
            "Series Number"
        ))
    );
    tag_option_names.put(
        "date",
        new ArrayList<String>(Arrays.asList(
            "Copyright Date",
            "Expiry Date",
            "Order Date",
            "Received Date",
            "Year Recommended",
            "Date Recorded",
            "Review Date"
        ))
    );
    tag_option_names.put(
    		"sendRec",
    		
    		new ArrayList<String>(Arrays.asList(
    			
    			"Received From",
    			"Sent To"
    		))
    );

    tag_option_names.put(
        "other",
        new ArrayList<String>(Arrays.asList(
            "ISBN",
            "ISSN",
            "Serial Number",
            "Order Number",
            "Price",
            "ROVER ID",
            "ROVER Series ID",
            "Cover Image Path",
            "External URL",
            "Teachers Guide Link"
        ))
    );
    tag_option_names.put(
        "people",
        new ArrayList<String>(Arrays.asList(
            "Author",
            "Editor",
            "Producer",
            "Narrator",
            "Illustrator",
            "Translator",
            "Composer",
            "Musician",
            "Designer",
            "Creator",
            "Mixer",
            "Engineer",
            "Director",
            "Associate Producer",
            "Evaluator"
        ))
    );
    tag_option_names.put(
        "format",
        new ArrayList<String>(Arrays.asList(
            "Number of Pages",
            "Running Time",
            "Number of Tracks",
            "Length",
            "Region Code",
            "Number of Copies"
        ))
    );
    tag_option_names.put(
        "content",
        new ArrayList<String>(Arrays.asList(
            "Topic",
            "Content Classification",
            "Resource List Classification"
        ))
    );
    tag_option_names.put(
            "status",
            new ArrayList<String>(Arrays.asList(
                "Intent Recieved",
                "Requested",
                "Recieved",
                "Returned",
                "Recommended for Approval",
                "Denied Approval",
                "Ineligle"
                
            ))
        );

    
    TagType grade_type = TagTypeLoader.loadByType("Grade");
    TagType level_type = TagTypeLoader.loadByType("Level");
    tag_options.put(
        "grade",
        new ArrayList<Tag>(Arrays.asList(
           //TagLoader.loadByTagTypeAndTagValue("Prekindergarten", level_type),
            TagLoader.loadByTagTypeAndTagValue("Kindergarten", level_type),
            TagLoader.loadByTagTypeAndTagValue("Maternelle", level_type),
            TagLoader.loadByTagTypeAndTagValue("1", grade_type),
            TagLoader.loadByTagTypeAndTagValue("2", grade_type),
            TagLoader.loadByTagTypeAndTagValue("3", grade_type),
            TagLoader.loadByTagTypeAndTagValue("4", grade_type),
            TagLoader.loadByTagTypeAndTagValue("5", grade_type),
            TagLoader.loadByTagTypeAndTagValue("6", grade_type),
            TagLoader.loadByTagTypeAndTagValue("7", grade_type),
            TagLoader.loadByTagTypeAndTagValue("8", grade_type),
            TagLoader.loadByTagTypeAndTagValue("9", grade_type),
            TagLoader.loadByTagTypeAndTagValue("10", grade_type),
            TagLoader.loadByTagTypeAndTagValue("11", grade_type),
            TagLoader.loadByTagTypeAndTagValue("12", grade_type)
            /*TagLoader.loadByTagTypeAndTagValue("10", level_type),
            TagLoader.loadByTagTypeAndTagValue("A10", level_type),
            TagLoader.loadByTagTypeAndTagValue("B10", level_type),
            TagLoader.loadByTagTypeAndTagValue("20", level_type),
            TagLoader.loadByTagTypeAndTagValue("A20", level_type),
            TagLoader.loadByTagTypeAndTagValue("B20", level_type),
            TagLoader.loadByTagTypeAndTagValue("30", level_type),
            TagLoader.loadByTagTypeAndTagValue("A30", level_type),
            TagLoader.loadByTagTypeAndTagValue("B30", level_type),
            TagLoader.loadByTagTypeAndTagValue("C30", level_type),
            TagLoader.loadByTagTypeAndTagValue("Example Grade", grade_type)*/
        ))
    );
    tag_options.put(
        "subject",
        TagLoader.loadByTypeName("Subject")
    );
    tag_options.put(
        "other_curriculum",
        TagLoader.loadByTypeName("Program")
    );
    tag_options.get("other_curriculum").addAll(TagLoader.loadByTypeName("Resource List Classification"));
    tag_options.put(
        "content",
        new ArrayList<Tag>()
    );
    for (String content_type_name : tag_option_names.get("content"))
    { tag_options.get("content").addAll(TagLoader.loadByTypeName(content_type_name)); }
    tag_options.put(
        "format",
        TagLoader.loadByTypeName("Format")
    );
    tag_options.put(
    		"status",
    		TagLoader.loadByTypeName("Status")
    );
    tag_options.put(
        "language",
        TagLoader.loadByTypeName("Language")
    );
    tag_options.put(
        "genre",
        TagLoader.loadByTypeName("Genre")
    );
    tag_options.put(
        "license",
        TagLoader.loadByTypeName("Streaming License")
    );

    
    tags.put(   "title",		new ArrayList<Tag>());
    tags.put(   "pubdist",		new ArrayList<Tag>());
    tags.put(   "date",			new ArrayList<Tag>());
    tags.put(   "other",		new ArrayList<Tag>());
    tags.put(   "tracking_info",new ArrayList<Tag>());
    tags.put(   "sendRec",		new ArrayList<Tag>());
    tags.put(   "status",		new ArrayList<Tag>());
    tags.put(   "stf_borrow",   new ArrayList<Tag>());
    tags.put(   "people",		new ArrayList<Tag>());
    tags.put(   "medium",		new ArrayList<Tag>());
    tags.put(   "format",		new ArrayList<Tag>());
    tags.put(   "genre",		new ArrayList<Tag>());
    tags.put(   "language",		new ArrayList<Tag>());
    tags.put(   "content",		new ArrayList<Tag>());
    tags.put(   "misc",			new ArrayList<Tag>());
    tags.put(   "licenses",		new ArrayList<Tag>());
    tags.put(   "groups",		new ArrayList<Tag>());

    for (Tag t : this_resource.getRootTags())
    {
    
        if (tag_option_names.get("people").contains(t.getType()))           { tags.get("people").add(t); }
        else if (t.getType().equals("Medium"))                              { tags.get("medium").add(t); }
        else if (t.getType().equals("Language"))                            { tags.get("language").add(t); }
        else if (t.getType().equals("Genre"))                               { tags.get("genre").add(t); }
        else if (t.getType().equals("STF Link"))                            { tags.get("stf_borrow").add(t); }
        else if (t.getType().equals("Streaming License"))					{ tags.get("licenses").add(t); }
        else if (t.getType().equals("Tag Group"))                           { tags.get("groups").add(t);
        //	for (Tag tag : t.getChildren(this_resource_id)){
        //		System.out.println(tag.getType() + " " + tag.getValue() + " " + tag.getJoinID() );
        //	}
        }
        else if (tag_option_names.get("sendRec").contains(t.getType()))		{ tags.get("sendRec").add(t);}
        else if (t.getType().equals("Status"))								{ tags.get("status").add(t); }
      
        else if (tag_option_names.get("other").contains(t.getType()))       { tags.get("other").add(t); }
        else if (tag_option_names.get("date").contains(t.getType()))        { tags.get("date").add(t); }
        //else if	(tag_option_names.get("tracking_info").contains(t.getType())){tags.get("tracking_info").add(t);}
        else if (tag_option_names.get("title").contains(t.getType()))       { tags.get("title").add(t); }
        else if (tag_option_names.get("content").contains(t.getType()))     { tags.get("content").add(t); }
        else if (
            (t.getType().equals("Publisher")) ||
            (t.getType().equals("Distributor"))
        )                                                                   { tags.get("pubdist").add(t); }
        else if (
            (t.getType().equals("Format")) || 
            (tag_option_names.get("format").contains(t.getType()))
        )                                                                   { tags.get("format").add(t); }
        else                                                                { tags.get("misc").add(t); }
    }




    
    
                      
////////////////////////////////////////////////////////////////////////////////////////////////////////
// QUICK INFO

        String title = (String) Query.select("moe_resource", "title", this_resource_id);
		System.out.println( "EditResource:" + title );
        String subtitle = (String) Query.select("moe_resource", "subtitle", this_resource_id);
        String edition = (String) Query.select("moe_resource", "edition", this_resource_id);
        String quick_image_path = (String) Query.select("moe_resource", "quick_pic", this_resource_id);
        String quick_desc = (String) Query.select("moe_resource", "quick_description", this_resource_id);
        String quick_info = (String) Query.select("moe_resource", "quick_info", this_resource_id);

        String page_image = quick_image_path;

        String page_title = title;
        String page_subtitle = subtitle;
        String page_edition = edition;
        if (title == null)
        {
            title = "<i>not available</i>";
            page_title = "<i>not available</i>";
        }
        if (subtitle == null)
        {
            subtitle = "";
            page_subtitle = "";
        }
        if (edition == null)
        {
            edition = "";
            page_edition = "";
        }
        if (quick_image_path == null)
        { quick_image_path = "<i>not available</i>"; }
        if (quick_desc == null)
        { quick_desc = "<i>not available</i>"; }
        if (quick_info == null)
        { quick_info = "<i>not available</i>"; }

    
    
    
    
    



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
        request.setAttribute("recommendations", all_recs);
        request.setAttribute("rec_status", rec_status);
        request.setAttribute("user_name", curr_user.blackboard.getUserName());
        request.setAttribute("curr_date", curr_date);
        request.setAttribute("is_rover", is_rover);
        request.setAttribute("is_core", is_core);
        request.setAttribute("permissions", permissions);
        request.setAttribute("flag", flag);

        request.setAttribute("role_text", role_text);
        request.setAttribute("page_title", page_title);
        request.setAttribute("page_subtitle", page_subtitle);
        request.setAttribute("page_edition", page_edition);
        request.setAttribute("page_image", page_image);
        request.setAttribute("title", title);
        request.setAttribute("subtitle", subtitle);
        request.setAttribute("edition", edition);
        request.setAttribute("quick_image_path", quick_image_path);
        request.setAttribute("quick_desc", quick_desc);
        request.setAttribute("quick_info", quick_info);

        String next_jsp = "/tagger.jsp";
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
