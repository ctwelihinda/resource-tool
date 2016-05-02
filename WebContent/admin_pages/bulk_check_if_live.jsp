<%-- 
    Document   : bulk_check_if_live
    Created on : 21-Oct-2014, 11:41:16 AM
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
        
        <form name="push_to_rover" action="CheckIfLive" method="post">
            Enter R Numbers or DB IDs (comma separated): <input type="text" name="resource_ids" />
            <input type="submit" value="ARE THEY LIVE?" class="submit_button" />
        </form>
        
    </body>
</html>
