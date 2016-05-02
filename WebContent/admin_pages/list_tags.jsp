<%-- 
    Document   : list_tags
    Created on : 27-Mar-2014, 10:24:09 AM
    Author     : peter
--%>

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
String tag_type_id = request.getParameter("tag_type_id");
boolean show_resources = false;
String show_resources_text = request.getParameter("show_resources_too");
if ((show_resources_text != null) && (show_resources_text.equals("show")))
{
    show_resources = true;
}
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tag List</title>
    </head>
    <body>
        <form action="list_tags.jsp" method="get" name="set_type">
            Which type?
            <select name="tag_type_id">
                <%
                    for (TagType tt : TagTypeLoader.loadAll())
                    {
                        %><option value="<%=tt.getDBID()%>"><%=tt.getType()%> - <%=tt.getDBID()%></option><%
                    }
                %>
            </select>
            <br/>
            Show resources as well? <input type="checkbox" name="show_resources_too" value="show"/><br/>
            <input type="submit" value="Get Tags"/>
        </form>
        
        <ul>
        <%
            if ((tag_type_id != null) && (ValidationHelpers.isPositiveInteger(tag_type_id)))
            {
                for (Tag t : TagLoader.loadByTypeID(Integer.parseInt(tag_type_id)))
                {
                    %><li><%
                        %><%=t.getValue()%> (<%=t.getDBID()%>) <a target="_blank" href="https://www.edonline.sk.ca/webapps/moe-resource_tool_final-BBLEARN/admin_pages/edit_tags.jsp?db_id=<%=t.getDBID()%>">Edit This Tag</a> - <a target="_blank" href="https://www.edonline.sk.ca/webapps/moe-resource_tool_final-BBLEARN/admin_pages/list_resources_by_tag.jsp?tag_id=<%=t.getDBID()%>">List Resources (warning: may crash if this is a very common tag)</a><%

                        String this_info = t.getInfo();
                        if ((this_info != null) && (!this_info.equals("")))
                        {
                            this_info = this_info.replace("\n", "<br/>");
                            %><br/><%=this_info%><br/><%
                        }
                        if (show_resources)
                        {
                            ArrayList<Integer> resource_ids = t.getResourceIDs();
                            %>
                            <ul>
                                <%
                                for (Integer this_id : resource_ids)
                                {
                                    Resource r = new Resource(this_id);
                                    %><li><%=r.getRNumber()%></li><%
                                }
                                %>
                            </ul>
                            <%
                        }
                    %></li><%
                }
            }
            else
            {
                %><li><i>No tags to list</i></li><%
            }
        %>
        </ul>
    </body>
</html>
