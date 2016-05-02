<%-- 
    Document   : analytics
    Created on : 24-Mar-2015, 5:00:17 PM
    Author     : peter
--%>

<%@page import="stans.EasyUser"%>
<%@page import="java.util.ArrayList"%>
<%@page import="stans.db.Query"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<% 
    Integer total_res_count = Query.getRowCount("moe_resource"); 

    ArrayList<String> user_args = new ArrayList<String>();
    user_args.add("englundpat");
    ArrayList<Integer> user_results = Query.find("users", "user_id = ?", user_args);
    Integer pat_user_pk = 0;
    if ((user_results != null) && (!user_results.isEmpty()))
    {
        pat_user_pk = user_results.get(0);
    }            

    //Integer pat_user_pk1 = pat_user.blackboard;
    ArrayList<String> pat_created_res_query = Query.freefind("SELECT COUNT(*) FROM moe_resource WHERE created_by = " + Integer.toString(pat_user_pk));
    Integer num_pat_created_res = 0;
    if ((pat_created_res_query != null) && (!pat_created_res_query.isEmpty()))
    {
        num_pat_created_res = Integer.parseInt(pat_created_res_query.get(0));
    }
    
    ArrayList<String> num_rec_query = Query.freefind("SELECT COUNT(*) FROM moe_resource WHERE recommendation = 1 OR recommendation = 2");
    Integer num_rec_res = 0;
    if ((num_rec_query != null) && (!num_rec_query.isEmpty()))
    {
        num_rec_res = Integer.parseInt(num_rec_query.get(0));
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Resource Tool Analytics</title>
        <link rel="stylesheet" type="text/css" href="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Resource%20Tool/css/analytics_styles.css" />
    </head>
    <body>
        <div class="stat_row">
            <span class="stat_title">Total Number of Resources</span>
            <span class="stat_value"><%=total_res_count%></span>
        </div>
        <div class="stat_row">
            <span class="stat_title">Number of Recommended Resources</span>
            <span class="stat_value"><%=num_rec_res%></span>
        </div>
        <div class="stat_row">
            <span class="stat_title">Number of Resources Created by Pat</span>
            <span class="stat_value"><%=num_pat_created_res%></span>
        </div>
    </body>
</html>
