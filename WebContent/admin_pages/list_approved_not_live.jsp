<%-- 
    Document   : list_approved_not_live
    Created on : 17-Jun-2015, 4:49:26 PM
    Author     : peter
--%>

<%@page import="stans.resourcerecord.model.Resource"%>
<%@page import="java.util.ArrayList"%>
<%@page import="stans.db.Query"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
ArrayList<String> args = new ArrayList<String>();
args.add("2");
ArrayList<Integer> ids = Query.find("moe_resource", "recommendation = ?", args);
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Approved but NOT Live</title>
    </head>
    <body>
        <h3>The following resources are approved but not live:</h3>
        
        <ul>
        <%
            for (Integer id : ids)
            {
                Resource r = new Resource(id);
                %><li><%=r.getRNumber()%>: <%=r.getTitle()%></li><%
            }
        %>
        </ul>
    </body>
</html>
