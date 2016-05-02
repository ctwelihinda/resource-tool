<%-- 
    Document   : view_resource_simple
    Created on : 3-Feb-2014, 11:48:32 AM
    Author     : peter

	This is not used by anything; it is just a standalone debug page.
	This page lists all of the tags attached to a resource, with as little intermediate processing as possible
		-it should reflect exactly what is in the database
--%>

<%@page import="blackboard.platform.security.authentication.HttpAuthManager"%>
<%@page import="blackboard.platform.session.BbSessionManagerServiceFactory"%>
<%@page import="blackboard.platform.session.BbSession"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.HashMap"%>
<%@page import="stans.resourcerecord.model.*"%>
<%@page import="stans.resourcerecord.dao.*"%>
<%@page import="blackboard.data.user.User"%>
<%@page import="blackboard.platform.context.Context"%>
<%@page import="blackboard.platform.context.ContextManagerFactory"%>
<%@page import="blackboard.platform.context.ContextManager"%>
<%@page import="stans.db.Query"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    // check if user is logged in, and redirect to login page if not
    BbSession bbSession = BbSessionManagerServiceFactory.getInstance().getSession(request);
    if (! bbSession.isAuthenticated())
    {
        HttpAuthManager.sendLoginRedirect(request,response);
        return;
    }
	
	String RNumber = request.getParameter("resource_number");
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>View Resource <%=RNumber%></title>
        <link rel="stylesheet" type="text/css" href="css/tagger_styles.css" />
    </head>
    
    <body>
        <div id="wrapper">
            <div class="page_title">Viewing Resource <%=RNumber%></div>
            
            <div class="tagger_section">
				<%
					if( ( RNumber!=null )&&( !RNumber.equals("") ) ) {

						%>
						<div class="tagger_section_title">All Tags</div>
						<div class="tagger_section_list_header">This resource currently has the following tags:</div>
						<div id="tags_container">
						<div class="tag_list" id="mediaformat_tags">
						<%

						Resource this_resource = ResourceLoader.loadByRNumber(RNumber);
						int this_resource_id = this_resource.getDBID();

						ArrayList<Tag> all_curr_tags = TagLoader.loadByResourceDBID(this_resource_id);

						for (Tag t : all_curr_tags) 
						{
							%><div class="tag_row"><%
							%>(<%=t.getDBID()%>)<%
							%><%=t.getType()%>: <%=t.getValue()%><%
							%></div><%
							if (t.getType().equals("Tag Group"))
							{
								for (Tag c : t.getChildren(this_resource_id))
								{
									%>
									<div class="child_tag_row">
										**<%=c.getType()%>: <%=c.getValue()%>
									</div>
									<%
								}
							}
						}
						%>
						</div>
						</div>
						<%
					} else {
						%>
						<div class="tagger_section_title">Error</div>
						<div class="tagger_section_list_header">Invalid or missing R Number</div>
						<%
					}
				%>
            </div>
            
        </div>
    </body>
</html>


