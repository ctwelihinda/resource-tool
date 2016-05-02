<%-- 
    Document   : bulk_check_if_live
    Created on : 21-Oct-2014, 11:41:16 AM
    Author     : peter
--%>
<%@page import="stans.resourcerecord.model.Tag"%>
<%@page import="stans.resourcerecord.dao.ResourceLoader"%>
<%@page import="stans.resourcerecord.model.Resource"%>
<%@page import="java.util.ArrayList"%>
<%@page import="blackboard.platform.security.authentication.HttpAuthManager"%>
<%@page import="blackboard.platform.session.BbSessionManagerServiceFactory"%>
<%@page import="blackboard.platform.session.BbSession"%>


<%
// ######### AUTHENTICATION ###############
    // check if user is logged in, and redirect to login page if not
    BbSession bbSession = BbSessionManagerServiceFactory.getInstance().getSession(request);
    if (! bbSession.isAuthenticated())
    {
        HttpAuthManager.sendLoginRedirect(request,response);
        return;
    }
%>   

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        
    </head>
    <body>
        
        <form name="find_subject" action="bulk_find_subject.jsp" method="post">
            Enter R Numbers or DB IDs (comma separated): <input type="text" name="resource_ids" />
            <input type="submit" value="FIND SUBJECTS" class="submit_button" />
        </form>
<%    
    String resource_id_string = request.getParameter("resource_ids");
    if (resource_id_string != null)
    {
        for (String rnumber : resource_id_string.split(","))
        {
            Resource r = ResourceLoader.loadByRNumber(rnumber);
            
            %><p><%=r.getTitle()%></p><%
            %><p><%=rnumber%></p><%
            
            %><ul><%
            for (Tag t : r.getTags())
            {
                if (t.getType().equals("Subject"))
                {
                    %><li><%=t.getValue()%></li><%
                }
            }
            %></ul><%
        }
    }
    
%>
        
    </body>
</html>
