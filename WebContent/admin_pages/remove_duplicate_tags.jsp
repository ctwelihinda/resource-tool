<%-- 
    Document   : remove_duplicate_tags
    Created on : 9-Jun-2015, 4:00:46 PM
    Author     : peter
--%>
<%@page import="stans.db.Query"%>
<%@page import="java.util.Arrays"%>
<%@page import="stans.db.Enumerators.BBComparisonOperator"%>
<%@page import="stans.resourcerecord.model.Resource"%>
<%@page import="java.util.ArrayList"%>
<%@page import="stans.resourcerecord.helpers.ValidationHelpers"%>
<%@page import="stans.resourcerecord.model.Tag"%>
<%@page import="stans.resourcerecord.dao.TagLoader"%>
<%@page import="stans.resourcerecord.model.TagType"%>
<%@page import="stans.resourcerecord.dao.TagTypeLoader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Remove Dupe Tags</title>
    </head>
    <body>
        <div id="results_div">
            <%
                String master_tag_id = request.getParameter("master_tag");
                if (master_tag_id != null)
                {
                    Tag master_tag = new Tag(Integer.parseInt(master_tag_id));
                    ArrayList<Tag> tags_to_delete = TagLoader.loadByTagTypeNameAndTagValueAndOperator(master_tag.getType(), master_tag.getValue(), BBComparisonOperator.EQUALS);

                    %><h3><%=master_tag.getType()%>: <%=master_tag.getValue()%></h3><%
                    
                    for (Tag old_tag : tags_to_delete)
                    {
                        if (old_tag.getDBID() != Integer.parseInt(master_tag_id))
                        {
                            ArrayList<String> args = new ArrayList<String>(Arrays.asList(Integer.toString(old_tag.getDBID())));
                            ArrayList<Integer> join_ids = Query.find("moe_resource_tag", "tag_id = ?", args);

                            %><ul><%
                                for (Integer this_join_id : join_ids)
                                {
                                    Resource r = new Resource((Integer)Query.select("moe_resource_tag", "resource_id", this_join_id));
                                    %><li>Resource <%=r.getRNumber()%> (<%=r.getTitle()%> -- <%=r.getDBID()%>) updated</li><%
                                    Query.update("moe_resource_tag", "tag_id", this_join_id, Integer.toString(master_tag.getDBID()));
                                }
                            %></ul><%

                            Query.delete("moe_tag", "pk1 = ?", args);
                            %><p><b>Tag <%=old_tag.getDBID()%> deleted</b></p><%
                        }
                    }
                }

            %>
        </div>
        
        <form action="remove_duplicate_tags.jsp" method="get">
            ID of Tag to <b>Keep</b>: <input type="text" name="master_tag"/>
            <input type="submit" value="Add this tag to all pertinent resources and delete dupes"/>
        </form>
    </body>
</html>
