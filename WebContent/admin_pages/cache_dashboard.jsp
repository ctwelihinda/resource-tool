<%-- 
    Document   : cache_dashboard
    Created on : 5-May-2014, 12:22:15 PM
    Author     : peter
--%>

<%@page import="stans.db.Query"%>
<%@page import="java.util.ArrayList"%>
<%@page import="stans.resourcerecord.dao.TagLoader"%>
<%@page import="stans.resourcerecord.model.Tag"%>
<%@page import="stans.resourcerecord.dao.TagTypeLoader"%>
<%@page import="stans.resourcerecord.dao.CachePersister"%>
<%@page import="stans.EasyUser"%>
<%@page import="blackboard.platform.security.authentication.HttpAuthManager"%>
<%@page import="blackboard.platform.session.BbSessionManagerServiceFactory"%>
<%@page import="blackboard.platform.session.BbSession"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%
        		
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
    }
    catch (Exception e)
    {

    }

%>

<%
    String type = request.getParameter("type");
    if (type != null)
    {
        if (type.equals("publisher"))
        {
            CachePersister.saveTagListByType(TagTypeLoader.loadByType("Publisher").getDBID());
            System.out.println("Publisher list cached!");
        }
        if (type.equals("distributor"))
        {
            CachePersister.saveTagListByType(TagTypeLoader.loadByType("Distributor").getDBID());
            System.out.println("Distributor list cached!");
        }
        if (type.equals("jazz_performers"))
        {
            CachePersister.saveTagListByType(TagTypeLoader.loadByType("Musician/Musical Group").getDBID());
            System.out.println("Musician/Musical Group list cached!");
        }
        if (type.equals("jazz_performers_by_role"))
        {
            Tag role = new Tag(Integer.parseInt(request.getParameter("musical_role")));
            StringBuilder sb = new StringBuilder();

            if (role != null)
            {
                String query_name = "musician_where_role=" + role.getValue();
                ArrayList<Integer> query_results = new ArrayList<Integer>();
                Integer resource_id = 2571;
                //Integer resource_id = 261;
                

                ArrayList<String> search_args = new ArrayList<String>();
                search_args.add(Integer.toString(resource_id));
                search_args.add(Integer.toString(role.getDBID()));

                ArrayList<Integer> join_search_ids = Query.find("moe_resource_tag", "resource_id = ? AND tag_id = ?", search_args);

                
                for (Integer join_id : join_search_ids)
                {
                    Integer tag_id = (Integer)Query.select("moe_resource_tag", "parent_id", join_id);
                    query_results.add(tag_id);
                }
                
                CachePersister.saveNamedQuery(query_name, query_results);
                System.out.println("Musician/Musical Group list by role -");
                System.out.println(role.getValue());
                System.out.println("-cached!");
            }
        }
        if (type.equals("resources_by_subject"))
        {
            Tag subject = new Tag(Integer.parseInt(request.getParameter("subject_id")));
            StringBuilder sb = new StringBuilder();

            if (subject != null)
            {
                String query_name = "subject=" + subject.getValue();
                ArrayList<Integer> query_results = new ArrayList<Integer>();

                ArrayList<String> search_args = new ArrayList<String>();
                search_args.add(Integer.toString(subject.getDBID()));

                ArrayList<Integer> join_search_ids = Query.find("moe_resource_tag", "tag_id = ?", search_args);

                
                for (Integer join_id : join_search_ids)
                {
                    Integer resource_id = (Integer)Query.select("moe_resource_tag", "resource_id", join_id);
                    query_results.add(resource_id);
                }
                
                CachePersister.saveNamedQuery(query_name, query_results);
                System.out.println("Resource list by subject -");
                System.out.println(subject.getValue());
                System.out.println("-cached!");
            }
        }
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Cache Dashboard</title>
    </head>
    <body>
        Create/Update Publisher Cache List
        <form action="cache_dashboard.jsp" name="update_pub_cache" method="get">
            <input type="hidden" name="type" value="publisher" />
            <input type="submit"/>
        </form>
        <hr/>
        Create/Update Distributor Cache List
        <form action="cache_dashboard.jsp" name="update_dist_cache" method="get">
            <input type="hidden" name="type" value="distributor" />
            <input type="submit"/>
        </form>
        <hr/>
        Create/Update Master Musician Cache List
        <form action="cache_dashboard.jsp" name="update_jazz_cache" method="get">
            <input type="hidden" name="type" value="jazz_performers" />
            <input type="submit"/>
        </form>
        <hr/>
        Create/Update Musician Cache List by Role
        <form action="cache_dashboard.jsp" name="update_jazz_cache_by_role" method="get">
            <select name="musical_role">
                <%
                for (Tag t : TagLoader.loadByTypeID(TagTypeLoader.loadByType("Musical Role").getDBID()))
                {
                    %><option value="<%=t.getDBID()%>"><%=t.getValue()%></option><%
                }
                %>
            </select>
            <input type="hidden" name="type" value="jazz_performers_by_role" />
            <input type="submit"/>
        </form>
        Create/Update Resource/Tag Join Cache List by Subject
        <form action="cache_dashboard.jsp" name="update_by_subject" method="get">
            <select name="subject_id">
                <%
                for (Tag t : TagLoader.loadByTypeID(TagTypeLoader.loadByType("Subject").getDBID()))
                {
                    %><option value="<%=t.getDBID()%>"><%=t.getValue()%></option><%
                }
                %>
            </select>
            <input type="hidden" name="type" value="resources_by_subject" />
            <input type="submit"/>
        </form>
        
        <p>Currently have the following caches:</p>
        <ul>
            <%
            for (Integer cache_id : Query.find("moe_cached_list", null, null))
            {
                String cache_name = (String) Query.select("moe_cached_list", "query_name", cache_id);
                %><li><%=cache_name%></li><%
            }
            %>
        </ul>
    </body>
</html>
