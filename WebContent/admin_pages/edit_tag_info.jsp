<%-- 
    Document   : edit_tag_info
    Created on : 11-Jun-2015, 1:26:02 PM
    Author     : peter
--%>

<%@page import="stans.resourcerecord.model.Tag"%>
<%@page import="stans.resourcerecord.helpers.ValidationHelpers"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Edit Tag Info</title>
        
        <link rel="stylesheet" type="text/css" href="../css/tagger_styles.css" />
        <script src="../javascript/jquery-latest.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                $('#pubdist_info_form').submit(function(event) {
                    event.preventDefault();
                    
                    $.post(
                       // "https://www.edonline.sk.ca/webapps/moe-resource_tool_final-BBLEARN/AddInfoToTag",
                       "https://bblearndev.merlin.mb.ca/webapps/moe-resource_tool_final-BBLEARN/AddInfoToTag",
                        $('#pubdist_info_form').find("textarea, input").serialize().replace(/'/g, "''"),
                        function (data) {
                            $('.status_message').html('<p>' + data + '</p>');
                        }
                    );

                });
            });
        </script>
    </head>
    <body>
        <div class="status_message"></div>
        <%
            String tag_id = request.getParameter("tag_id");

            if ((tag_id != null) && (ValidationHelpers.isPositiveInteger(tag_id)))
            {
                Tag t = new Tag(Integer.parseInt(tag_id));
                %>
                    <h3><%=t.getType()%>: <%=t.getValue()%></h3>
                    
                    <form id="pubdist_info_form" method="POST">
                        <textarea name="tag_info" rows="25" cols="50"><%=t.getInfo()%></textarea>
                        <input type="hidden" name="tag_id" value="<%=tag_id%>" />
                        <input type="submit" value="SAVE" />
                    </form>
                <%
            }
            else
            {
                %><h3>No tag identified</h3><%
            }
        %>

    </body>
</html>
