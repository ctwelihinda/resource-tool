<%@page import="stans.resourcerecord.helpers.TaggerPermissionsManager"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="stans.resourcerecord.model.Tag"%>
<%@page import="java.util.ArrayList"%>
<%
ArrayList<Tag> table_list = (ArrayList<Tag>) request.getAttribute("table_list");
TaggerPermissionsManager permissions = (TaggerPermissionsManager) request.getAttribute("permissions");
String table_id = request.getParameter("table_id");
String perm_name = request.getParameter("perm_name");
Integer res_id = Integer.parseInt(request.getParameter("res_id"));

%>

<table class="tag_table" id="<%=table_id%>">
    <thead>
        <tr>
            <td>TYPE</td>
            <td>VALUE</td>
            <td>ACTION</td>
            <td>CREATOR</td>
            <td>DATE</td>
        </tr>
    </thead>
    <tbody>
    <% for (Tag t : table_list) 
    {
            String tag_value = t.getValue();
            if ((t.getType().equals("External URL")) || (t.getType().equals("Cover Image Path")))
            {
                tag_value = "<a target=\"_blank\" href=\"" + tag_value + "\">" + tag_value + "</a>" ;
            }
            if ((t.getType().equals("Publisher")) || (t.getType().equals("Distributor")))
            {
                tag_value = "<a target=\"_blank\" href=\"admin_pages/edit_tag_info.jsp?tag_id=" + Integer.toString(t.getDBID()) + "\">" + tag_value + "</a>" ;
            }
        %>
        <tr id="<%=Integer.toString(t.getDBID())%>">
            <td class="tag_table_type"><%=t.getType()%></td>
            <td class="tag_table_value"><%=tag_value%></td>
            <td class="tag_table_delete">
                <% 
                if (permissions.getPermissionLevel(perm_name) >= TaggerPermissionsManager.READ_WRITE_DELETE)
                { 
                    %><span class="tag_remove_box" id="<%=t.getDBID()%>">DELETE</span><% 
                }
                %>
            <td class="tag_table_user"><%=t.getJoinCreatedBy(res_id)%></td>
            <td class="tag_table_date"><%=String.format("%1$tb %1$te, %1$tY", t.getJoinCreatedAt(res_id))%></td>
        </tr>
    <% } %>
    </tbody>
</table>

