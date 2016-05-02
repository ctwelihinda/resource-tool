<%-- 
    Document   : bulk_push_to_rover
    Created on : 23-Sep-2014, 11:30:27 AM
    Author     : peter
--%>

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
        
        <form name="push_to_rover" action="PushToROVER" method="post">
            Enter R Numbers or DB IDs (comma separated): <input type="text" name="resource_ids" />
            <input type="submit" value="PUSH TO ROVER" class="submit_button" />
        </form>
        
    </body>
</html>
