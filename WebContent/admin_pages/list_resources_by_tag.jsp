<%@page import="stans.resourcerecord.model.Resource"%>
<%@page import="java.util.ArrayList"%>
<%@page import="stans.resourcerecord.helpers.ValidationHelpers"%>
<%@page import="stans.resourcerecord.model.Tag"%>
<%@page import="stans.resourcerecord.dao.TagLoader"%>
<%@page import="stans.resourcerecord.model.TagType"%>
<%@page import="stans.resourcerecord.dao.TagTypeLoader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%
String tag_id = request.getParameter("tag_id");
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Resources by Tag</title>
    </head>
    <body>
        <form action="list_resources_by_tag.jsp" method="get" name="set_tag_id">
            Tag DB ID: <input type="text" name="tag_id" />
            <input type="submit" value="Get Resources"/>
        </form>
        
        <%
            if ((tag_id != null) && (ValidationHelpers.isPositiveInteger(tag_id)))
            {
                Tag t = new Tag(Integer.parseInt(tag_id));
                ArrayList<Integer> resource_ids = t.getResourceIDs();
                %>
                <ul>
                    <%
                    for (Integer this_id : resource_ids)
                    {
                        Resource r = new Resource(this_id);
                        String title_style = "color: #C26666;";
                        if (r.getFinalRecommendation() == 1)
                        { title_style = "color: #009900; font-weight: bold;"; }
                        if (r.getFinalRecommendation() == 2)
                        { title_style = "color: #336600;"; }
                        %><li><a target="_blank" href="https://bblearndev.merlin.mb.ca/webapps/moe-resource_tool_final-BBLEARN/EditResource?resource_number=<%=r.getRNumber()%>"><%=r.getRNumber()%></a> - <span style="<%=title_style%>"><%=r.getTitle()%></span></li><%
                    }
                    %>
                </ul>
                <%
            }
        %>               
    </body>
</html>
