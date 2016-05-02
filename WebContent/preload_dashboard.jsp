<%-- 
    Document   : preload_dashboard
    Created on : 19-Nov-2015, 2:27:07 PM
    Author     : peter
--%>

<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Preload testing</title>
    </head>
    <body>
		<%
			ArrayList<String> subjectList = ( ArrayList<String> )request.getAttribute( "subjectList" );
			ArrayList<String> levelList = ( ArrayList<String> )request.getAttribute( "levelList" );
			ArrayList<String> programList = ( ArrayList<String> )request.getAttribute( "programList" );
		%>
		<h3>Subjects</h3>
			<ul>
				<%
					for( String subject : subjectList ) {
						%><li><%= subject %></li><%
					}
				%>
			</ul>
		<h3>Levels</h3>
			<ul>
				<%
					for( String level : levelList ) {
						%><li><%= level %></li><%
					}
				%>
			</ul>
		<h3>Programs</h3>
			<ul>
				<%
					for( String program : programList ) {
						%><li><%= program %></li><%
					}
				%>
			</ul>
    </body>
</html>
