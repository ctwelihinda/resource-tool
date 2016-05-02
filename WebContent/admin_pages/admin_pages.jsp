<%-- 
    Document   : admin_pages
    Created on : 24-Oct-2014, 10:24:48 AM
    Author     : peter
--%>
<%@page import="stans.EasyUser"%>
<%@page import="blackboard.platform.session.BbSessionManagerServiceFactory"%>
<%@page import="blackboard.platform.security.authentication.HttpAuthManager"%>
<%@page import="blackboard.platform.session.BbSession"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

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

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Resource Admin Pages</title>
        
        <style type="text/css">
            body {
                font-family: Optima, Segoe, "Segoe UI", Candara, Calibri, Arial, sans-serif;
                padding: 10px;
            }
            a {
                font-weight: bold;
                color: #777;
                text-decoration: none;
            }
            a:hover {
                font-weight: bold;
                color: #AAA;
            }
            #link_list {
                margin: 35px 0px;
                padding: 15px;
                border: 1px solid #CCC;
            }
        </style>
    </head>
    <body>
        <h2>Resource Tool Admin Pages</h2>
        <div id="link_list">
            <p><a href="edit_tags.jsp">Edit tags/types</a></p>
            <p><a href="create_tags.jsp">Create tags/types</a></p>
            <p><a href="list_tags.jsp">List all tags by type</a></p>
            <p><a href="add_tag_info.jsp">Add/edit pub/dist info</a></p>
            <p><a href="list_resources_by_tag.jsp">List all resources by tag</a></p>
            <p><a href="bulk_check_if_live.jsp">Batch check if live</a></p>
        </div>
    </body>
</html>
