<%-- 
    Document   : delete_tags
    Created on : 20-Nov-2015, 3:10:19 PM
    Author     : peter
--%>

<%@page import="stans.resourcerecord.model.TagType"%>
<%@page import="stans.resourcerecord.dao.TagTypeLoader"%>
<%@page import="stans.resourcerecord.model.Tag"%>
<%@page import="stans.EasyUser"%>
<%@page import="blackboard.platform.security.authentication.HttpAuthManager"%>
<%@page import="blackboard.platform.session.BbSessionManagerServiceFactory"%>
<%@page import="blackboard.platform.session.BbSession"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

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
    try
    {
        if (!curr_easyuser.shortcuts.hasRoleId("stf_resource_evaluation_administrator"))
        {
            HttpAuthManager.sendAccessDeniedRedirect(request, response);
        }
    }
    catch (Exception e)
    {

    }
%>

<%
    String page_mode; // whether we're editing, finding tags, etc.
    
    String db_id = request.getParameter("db_id");
    
    if (db_id != null)
    {
        page_mode = "edit";
    }
    else
    {
        page_mode = "find";
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tag Editor</title>
        
        <script src="../javascript/jquery-latest.min.js"></script>        
        <script>
            $(document).ready(function() {
                $('#update_tag_submit').on('click', function() {
                    $.post(
                        "UpdateTag",
                        $("#update_tag_id").find("select, input").serialize().replace(/'/g, "''"),
                        function (data) {
                            if (data == "error")
                            {
                                alert("ERROR: tag not updated!");
                            }
                            else
                            {
                                window.location.reload(true);
                            }
                        }
                    );
                });
            });
        </script>        
    </head>
    <body>
        
        <%
        if (page_mode.equals("edit"))
        {
            Tag t = new Tag(Integer.parseInt(db_id));

            %>
            <form name="update_tag" id="update_tag_id">
                Value: <input type="text" name="tag_value" value="<%=t.getValue()%>"/><br/>
                Type:                     
                <select name="tagtype_id" form="add_date_tag">
                    <%
                        String selected = "";
                        for (TagType tt : TagTypeLoader.loadAll())
                        {
                            if (tt.getType().equals(t.getType()))
                            { selected = "selected "; }
                            %><option <%=selected%>value="<%=tt.getDBID()%>"><%=tt.getType()%></option><%
                            selected = "";
                        }
                    %>
                </select>
                <input type="hidden" name="tag_id" value="<%=t.getDBID()%>"/>
            </form>
            <button id="update_tag_submit">DELETE</button>
            <br/><br/>
            <%
        }
        %>
        
        <form action="edit_tags.jsp" name="find_tag" method="get">
            DB ID: <input type="text" name="db_id"/><br/>
            <input type="submit" value="Find"/>
        </form>
    </body>
</html>
