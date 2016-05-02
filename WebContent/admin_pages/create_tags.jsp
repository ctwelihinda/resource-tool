<%-- 
    Document   : create_tags
    Created on : 21-Jan-2014, 6:58:41 AM
    Author     : peter
--%>

<%@page import="stans.EasyUser"%>
<%@page import="blackboard.platform.security.authentication.HttpAuthManager"%>
<%@page import="blackboard.platform.session.BbSessionManagerServiceFactory"%>
<%@page import="blackboard.platform.session.BbSession"%>
<%@page import="stans.resourcerecord.model.Tag"%>
<%@page import="stans.resourcerecord.dao.TagLoader"%>
<%@page import="stans.resourcerecord.dao.TagTypeLoader"%>
<%@page import="stans.resourcerecord.model.TagType"%>
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
    if (!curr_easyuser.shortcuts.hasRoleId("stf_resource_evaluation_administrator"))
    {
        HttpAuthManager.sendAccessDeniedRedirect(request, response);
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>

        <%
            if (request.getAttribute("servlet_status") != null)
            {
                %><div style="color: mediumvioletred; font-size: 1.2em;">${servlet_status}</div><%
            }           
        %>
            
        <h3>Create new tag</h3>
        <form name="create_tag" action="CreateTag" method="post">
            Value: <input type="text" name="tag_value">
            Type: 
            <select name="tag_type">
                <%
                    for (TagType current : TagTypeLoader.loadAll())
                    {
                        %><option value="<%=Integer.toString(current.getDBID())%>"><%=current.getType()%> - <%=current.getDescription()%></option><%
                    }
                %>
            </select>
            <input type="submit" value="Create Tag">
        </form>
            
            
        <h3>Create new tag type</h3>
        <form name="create_tagtype" action="CreateTagType" method="post">
            Name: <input type="text" name="tagtype_name">
            Description: 
            <textarea name="tagtype_desc" rows="5" cols="30"></textarea>            
            <input type="submit" value="Create Tag Type">
        </form>

    </body>
</html>
