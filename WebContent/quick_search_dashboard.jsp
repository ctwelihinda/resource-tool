<%-- 
    Document   : quick_search_dashboard
    Created on : 2-Dec-2014, 10:11:34 AM
    Author     : peter
--%>

<%@page import="stans.EasyUser"%>
<%@page import="blackboard.platform.security.authentication.HttpAuthManager"%>
<%@page import="blackboard.platform.session.BbSessionManagerServiceFactory"%>
<%@page import="blackboard.platform.session.BbSession"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%

                         ////////////////////
//~*~*~*~*~*~*~*~*~*~*~  // AUTHENTICATION //  ~*~*~*~*~*~*~*~*~*~*~
                         ////////////////////

    // check if user is logged in, and redirect to login page if not
    BbSession bbSession = BbSessionManagerServiceFactory.getInstance().getSession(request);
    if (! bbSession.isAuthenticated())
    {
        HttpAuthManager.sendLoginRedirect(request,response);
        return;
    }
    // also check if the user has the correct role(s) to be doing this, and give them a message if not
    try
    {
        EasyUser curr_easyuser = new EasyUser(request);
        if (!curr_easyuser.shortcuts.hasRoleId("resource_tool_full_admin"))
        { HttpAuthManager.sendAccessDeniedRedirect(request, response); }
    }
    catch (Exception e)
    {}


%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">


        <title>Quick Search</title>
        
        <link rel="stylesheet" type="text/css" href="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/css/popup_styles.css" />
        <link rel="stylesheet" type="text/css" href="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/css/curriculum_list_styles.css" />
        
        <script src="javascript/jquery-latest.min.js"></script>
        <script src="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/javascript/quick_search_scripts.js"></script>
        <script src="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/javascript/dashboard_filters.js"></script>
        <script src="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/javascript/dashboard_popup.js"></script>
        <script src="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/javascript/resource_list_scripts.js"></script>
    </head>
    <body>
        <div id="cached_resource_list_wrapper">

    <!-- POP-UP DIV -->
            <div id="resource_popup">
                <div id="popup_top_bar">
                    <span id="close_popup">X</span>
                </div>
                <div id="resource_popup_content"></div>
            </div>

    <!-- FORM -->
            <form name="quick_search_form" id="quick_search_form">
                Title: <input type="text" id="search_text_id" name="title" />
                <input type="checkbox" id="search_rover_only_id" name="rover_only" value="yes" /> ROVER only
                <input type="checkbox" id="search_live_only_id" name="live_only" value="yes" /> show live resources only
                <input type="submit" value="SEARCH" />
            </form>


    <!-- PAGE HEADER -->
            <div id="rl_wrapper">

                <div id="rl_page_header">
                    <div class="rl_page_title">No search performed</div>
                    <div class="rl_page_subtitle">0 results found</div>
                </div>

    <!-- RENDER SEARCH RESULTS -->
                <div id="results_list_wrapper">

                    <div id="results_list">
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
