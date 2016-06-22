<%-- 
    Document   : list_changes
    Created on : 29-Jun-2015, 3:53:35 PM
    Author     : peter
--%>

<%@page import="stans.resourcerecord.model.ResourceText"%>
<%@page import="stans.resourcerecord.model.Tag"%>
<%@page import="stans.resourcerecord.model.Resource"%>
<%@page import="stans.resourcerecord.helpers.ValidationHelpers"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="stans.db.Query"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>List Changes</title>
    </head>
    <body>
        <form action="list_changes.jsp" method="GET">
            For the last how many hours? <input type="text" name="delta_hours" />
            <input type="submit" value="List Changes"/>
        </form>
        
        <%
            String delta_hours_string = request.getParameter("delta_hours");
            if ((delta_hours_string != null) && (ValidationHelpers.isPositiveInteger(delta_hours_string)))
            {
                Integer delta_hours = Integer.parseInt(delta_hours_string);
                long delta_ms = (long)delta_hours * 3600000;

                final int TITLE_ID = 21;
                final int EXPIRY_ID = 101;
                final int ENTRY_ID = 201;
                final int COPYRIGHT_ID = 27;
                final int PUBLISHER_ID = 25;
                
                
                Timestamp curr_time = Query.getCurrentTime();
                Timestamp since_time = new Timestamp(curr_time.getTime() - delta_ms);

               // String tag_constraints = "created_at >= TO_TIMESTAMP('" + since_time.toString() + "','YYYY-MM-DD HH24:MI:SS.FF6')";
                String tag_constraints = "created_at >= CAST(CAST('" + since_time.toString() + "' as varchar(max)) as datetime)";
                //String text_constraints = "created_at >= TO_TIMESTAMP('" + since_time.toString() + "','YYYY-MM-DD HH24:MI:SS.FF6')";
                String text_constraints = "created_at >= CAST(CAST('" + since_time.toString() + "' as varchar(max)) as datetime)";
                //String ua_constraints = "updated_at >= TO_TIMESTAMP('" + since_time.toString() + "','YYYY-MM-DD HH24:MI:SS.FF6')";
    			String ua_constraints = "updated_at >= CAST(CAST('" + since_time.toString() + "' as varchar(max)) as datetime)";
                %><table><%
                for (Integer join_id : Query.find("moe_resource_tag", tag_constraints, null))
                {
                    Integer resource_id = (Integer)Query.select("moe_resource_tag", "resource_id", join_id);
                    Resource r = new Resource(resource_id);
                    if (r.isROVER())
                    {
                        Integer tag_id = (Integer)Query.select("moe_resource_tag", "tag_id", join_id);
                        Tag t = new Tag(tag_id);
                        %><tr><%
                            %><td><%=((Timestamp)Query.select("moe_resource_tag", "created_at", join_id)).toString()%></td><%
                            %><td><%=Integer.toString(resource_id)%></td><%
                            %><td><%=r.getRNumber()%></td><%
                            %><td><%=Integer.toString(tag_id)%></td><%
                            %><td><%=t.getValue()%></td><%
                            %><td><%=t.getType()%></td><%
                        %></tr><%
                    }
                }
                %><tr><td colspan="6">resource_text</td></tr><%
                for (Integer text_id : Query.find("moe_resource_text", text_constraints, null))
                {
                    Integer resource_id = (Integer)Query.select("moe_resource_text", "resource_id", text_id);
                    Resource r = new Resource(resource_id);
                    if (r.isROVER())
                    {
                        ResourceText rt = new ResourceText(text_id);
                        %><tr><%
                            %><td><%=((Timestamp)Query.select("moe_resource_text", "created_at", text_id)).toString()%></td><%
                            %><td><%=Integer.toString(resource_id)%></td><%
                            %><td><%=r.getRNumber()%></td><%
                            %><td><%=Integer.toString(text_id)%></td><%
                            %><td><%=rt.getType()%></td><%
                            %><td><%=rt.getText()%></td><%
                        %></tr><%
                    }
                }
                %><tr><td colspan="6">updated_at field</td></tr><%
                for (Integer resource_id : Query.find("moe_resource", ua_constraints, null))
                {
                    Resource r = new Resource(resource_id);
                    if (r.isROVER())
                    {
                        %><tr><%
                            %><td><%=((Timestamp)Query.select("moe_resource", "updated_at", resource_id)).toString()%></td><%
                            %><td><%=Integer.toString(resource_id)%></td><%
                            %><td><%=r.getRNumber()%></td><%
                            %><td>N/A</td><%
                            %><td>N/A</td><%
                            %><td>N/A</td><%
                        %></tr><%
                    }
                }
                %></table><%
            }
        %>

    </body>
</html>
