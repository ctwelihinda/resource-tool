<%-- 
    Document   : add_tag_info
    Created on : 29-Jan-2014, 11:50:37 AM
    Author     : peter
--%>

<%@page import="stans.resourcerecord.dao.CacheLoader"%>
<%@page import="stans.EasyUser"%>
<%@page import="blackboard.platform.security.authentication.HttpAuthManager"%>
<%@page import="blackboard.platform.session.BbSessionManagerServiceFactory"%>
<%@page import="blackboard.platform.session.BbSession"%>
<%@page import="stans.resourcerecord.model.Tag"%>
<%@page import="stans.resourcerecord.dao.TagLoader"%>
<%@page import="stans.resourcerecord.dao.TagTypeLoader"%>
<%@page import="stans.resourcerecord.model.TagType"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

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
    if (!curr_easyuser.shortcuts.hasRoleId("stf_resource_evaluation_administrator"))
    {
        HttpAuthManager.sendAccessDeniedRedirect(request, response);
    }
%>


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="../css/tagger_styles.css" />
        <script src="../javascript/jquery-latest.min.js"></script>
        <script src="../javascript/edit_pubdist_info.js"></script>
        <title>Add Tag Info</title>
    </head>
    <body>
        
        <div class="status_message"></div>

        <div style="float: right; width: 45%;">
            <div class="tagger_section_title" id="textarea_title"></div>
            <form name="edit_publisher_info" id="edit_info_form">
                <textarea id="update_info_textarea" name="tag_info" rows="25" cols="50"></textarea>
                <input type="hidden" name="tag_id" id="tag_id_input" />
            </form>
            <button id="update_info">EDIT INFO</button>
        </div>

        <div style="width: 45%;">
            <div class="tagger_section">
                <div class="tagger_section_title">Publisher Info</div>
                <div class="form_wrapper">
                    <form name="select_pub" id="select_pub_form">
                        <select name="tag_id" id="publisher_list" class="company_list" size="7">
                        <%
                            for (Tag t : TagLoader.loadByTypeID(TagTypeLoader.loadByType("Publisher").getDBID()))
                            //for (Tag t : CacheLoader.getCachedTags(TagTypeLoader.loadByType("Publisher")))
                            {
                                %><option value="<%=t.getDBID()%>"><%=t.getValue()%></option><%
                            }
                        %>
                        </select>
                    </form>
                </div>
                <button class="get_old_info" id="get_old_info_pub">EDIT INFO</button>
            </div>

            <div class="tagger_section">
                <div class="tagger_section_title">Distributor Info</div>
                <div class="form_wrapper">
                    <form name="select_dist" id="select_dist_form">
                        <select name="tag_id" id="distributor_list" class="company_list" size="7">
                        <%
                            for (Tag t : TagLoader.loadByTypeID(TagTypeLoader.loadByType("Distributor").getDBID()))
                            //for (Tag t : CacheLoader.getCachedTags(TagTypeLoader.loadByType("Distributor")))
                            {
                                %><option value="<%=t.getDBID()%>"><%=t.getValue()%></option><%
                            }
                        %>
                        </select>
                    </form>
                </div>
                <button class="get_old_info" id="get_old_info_dist">EDIT INFO</button>
            </div>
        </div>

    </body>
</html>
