<%-- 
    Document   : resource_summary
    Created on : 29-Jun-2015, 11:01:59 AM
    Author     : peter
--%>

<%@page import="java.sql.Timestamp"%>
<%@page import="stans.db.Query"%>
<%@page import="stans.resourcerecord.model.Resource"%>
<%@page import="stans.resourcerecord.dao.ResourceLoader"%>
<%@page import="blackboard.platform.security.authentication.HttpAuthManager"%>
<%@page import="stans.EasyUser"%>
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

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Resource Summary</title>
    </head>
    <body>
        <form action="resource_summary.jsp" name="find_resource" method="get">
            R Number: <input type="text" name="r_number"/><br/>
            <input type="submit" value="Show"/>
        </form>

        <%
            String r_number = request.getParameter("r_number");
            if (r_number != null)
            {
                Resource r = ResourceLoader.loadByRNumber(r_number);
              
                int db_id = r.getDBID();
        
                %><ul><%
                System.out.println("before first tag");	%><li>pk1: <%=(String)Query.select("moe_resource", "pk1", db_id)%></li><% 
                    %><li>entry_id: <%=(String)Query.select("moe_resource", "entry_id", db_id)%></li><%
                    %><li>created_by: <%=(String)Query.select("moe_resource", "created_by", db_id)%></li><%
                    %><li>created_at: <%=((Timestamp)Query.select("moe_resource", "created_at", db_id)).toString()%></li><%
                    %><li>go_live_date: <%=(String)Query.select("moe_resource", "go_live_date", db_id)%></li><%
                    %><li>parent_id: <%=(Integer)Query.select("moe_resource", "parent_id", db_id)%></li><%
                    %><li>recommendation: <%=(Integer)Query.select("moe_resource", "recommendation", db_id)%></li><%
                    %><li>out_of_print: <%=(Integer)Query.select("moe_resource", "out_of_print", db_id)%></li><%
                    %><li>dont_show_if_child: <%=(Integer)Query.select("moe_resource", "dont_show_if_child", db_id)%></li><%
                    %><li>is_core: <%=(Integer)Query.select("moe_resource", "is_core", db_id)%></li><%
                    %><li>is_rover: <%=(Integer)Query.select("moe_resource", "is_rover", db_id)%></li><%
                    %><li>quick_title: <%=(String)Query.select("moe_resource", "quick_title", db_id)%></li><%
                    %><li>quick_description: <%=(String)Query.select("moe_resource", "quick_description", db_id)%></li><%
                    %><li>quick_info: <%=(String)Query.select("moe_resource", "quick_info", db_id)%></li><%
                    %><li>quick_pic: <%=(String)Query.select("moe_resource", "quick_pic", db_id)%></li><%
                %></ul><%
            }
        %>
    </body>
</html>
