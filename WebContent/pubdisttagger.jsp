<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="stans.resourcerecord.helpers.TaggerPermissionsManager"%>
<%@page import="stans.EasyUser"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.HashMap"%>
<%@page import="stans.resourcerecord.model.*"%>
<%@page import="stans.resourcerecord.dao.*"%>
<%@page import="stans.db.Query"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<% 


PubDistRecord this_resource = (PubDistRecord)request.getAttribute("this_resource");
Integer this_resource_id = this_resource.getDBID();
String user_name = (String) request.getAttribute("user_name");
String curr_date = (String) request.getAttribute("curr_date");
TaggerPermissionsManager permissions = (TaggerPermissionsManager) request.getAttribute("permissions");
String role_text = (String) request.getAttribute("role_text");
String page_title = (String) request.getAttribute("page_title");
String page_image = (String) request.getAttribute("page_image");

String title = (String) request.getAttribute("title");
String quick_desc = (String) request.getAttribute("quick_desc");
String quick_image_path = (String) request.getAttribute("quick_image_path");
String quick_info = (String) request.getAttribute("quick_info");


HashMap< String,ArrayList<Tag> > tags = (HashMap< String,ArrayList<Tag> >) request.getAttribute("tags");
HashMap< String,ArrayList<Tag> > tag_options = (HashMap< String,ArrayList<Tag> >) request.getAttribute("tag_options");
HashMap< String,ArrayList<String> > tag_option_names = (HashMap< String,ArrayList<String> >) request.getAttribute("tag_option_names");

////////////////////////////////////////////////////////////////////////////////////////////////////////
// BEGIN HTML
                                                       
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
			<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title><%=title%></title>

            <script src="javascript/jquery-latest.min.js"></script>
            <script src="javascript/jquery.ui.core.min.js"></script>
            <script src="javascript/jquery.ui.widget.min.js"></script>
            <script src="javascript/jquery.ui.position.min.js"></script>
            <script src="javascript/jquery.ui.menu.min.js"></script>
            <script src="javascript/jquery.ui.autocomplete.min.js"></script>
           <!--  <script src="javascript/tagger_scripts.js?"></script> -->
           	<script src="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/javascript/pub_dist_scripts.js"></script>
           	<script type="text/javascript">
                $(document).ready(function() {
                   /////////////////////////////////////////
                   // "remove" buttons --- need to have this in this file because it references this_resource_id

                   $('.tags_container').on('click', '.tag_remove_box', function() {
                        var tag_id = $(this).attr('id');
                        var tag_class = $(this).attr('class');
                        
                        if (tag_id == "remove_parent")
                        {
                            var parent_element = $(this).parent();
                            $.post(
                                "RemoveParentResource",
                                { child_id: <%=this_resource_id%> }
                            ).done(function() {
                                $("tr#parent_row").remove();
                            });
                        }
                        else if (tag_class.toString().indexOf("remove_other_resource") > -1)
                        {
                            $.post(
                                "RemoveResourceRelationship",
                                { relationship_id: tag_id }
                            ).done(function() {
                                $("tr#" + tag_id).remove();
                            });
                        }
                        else
                        {
                            var parent_element = $(this).parent();
                            $.post(
                                "RemoveTagsFromResource",
                                { tag_id: tag_id, publisher_id: <%=this_resource_id%> }
                            ).done(function() {
                                $(parent_element).remove();
                                $("tr#" + tag_id).remove();
                            });
                        }
                   });
                });
            </script>
      		<link rel="stylesheet" href="css/jquery-ui.min.css">
            <link rel="stylesheet" type="text/css" href="css/tagger_styles.css" />
</head>
<body>
 <div id="wrapper">
                
                <div id="top_area">
                    
                    <div class="top_new_link"><a target="_blank" href="CreatePublisher">New Publisher</a></div>

                  <%

                    %>
                    <%
                        String r_number_color = "brown";
    					 r_number_color = "#006837";
    				
                    %>
                    <div id="left_top_box">
                
                        <div id="ltb_r_number" style="color: <%=r_number_color%>"><%=this_resource.getName()%><%=role_text%></div>
                        <div id="ltb_created_by">Created by <%=this_resource.getCreatedBy()%></div>
                        <div id="ltb_db_id"><%=this_resource_id%></div>
                        <div style="clear:both"></div>
                    </div>
                    <!-- these next two are display:none, but are in the DOM for the javascript to grab -->
                    <div class="current_user"><%=user_name%></div>
                    <div class="current_date"><%=curr_date%></div>

                    <div id="center_top_box">
                        <div id="ctb_title_area">
                            <div id="ctb_title"><%=page_title%></div>
                            <% if (page_image != null) { %><div id="ctb_image"><image src="<%=page_image%>"/></div><% } %>
                        </div>
                    </div>


                    <div style="clear:both;"></div>
                </div> <!--end top_area-->
		
		
		<div class="tagger_section">
                    <div class="tagger_section_title">General</div>
                    <div class="tagger_section_instructions">
                        Use this section to add the general information of the publisher/distributor.
                    </div>
                    <div id="title_tags_container" class="tags_container">
                        <div id="title_row" class="big_tag_row">
                            <span class="big_tag_name">Title</span>
                            <span class="big_tag_value" id="title_value"><%=title%></span>
                            <% if (permissions.getPermissionLevel("title") >= TaggerPermissionsManager.READ_WRITE_DELETE) { %>
                                <span class="text_edit_box" id="title_edit_box">EDIT</span>
                                <span class="text_remove_box" id="title_remove_box">DELETE</span>
                            <% } %>
                        </div>
                        
                        
                    </div>
        </div>
        
       
                 
                 
                 <!-- ################ ADDRESSES ####################### -->                    
                <div class="tagger_section">
                    <div class="tagger_section_title">Addresses</div>
                    <div class="tagger_section_instructions">
                        To create a new address group, enter a name for the group (it can be anything, but something meaningful like "Head Office" might be helpful for you) and click the "Add new location" button below.<br/><br/><br/><br/>
                        You can attach any number of tags of any kind to a group, but the onus is on you to make sure that the tagging is accurate, meaningful, and logical.
                    </div>

                    <% 
                    if (permissions.getPermissionLevel("subjgrade") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        <form name="add_new_group" id="add_new_group_id">
                            Location Name: <input type="text" name="tag_name" />
                            <input type="hidden" name="publisher_id" value="<%=this_resource_id%>"/>
                            <button type="button" id="add_new_group_button">ADD NEW LOCATION</button>
                        </form>
                        
                    <% } %>

                    <div id="subjectgrade_tags_container" class="tags_container">
                        <div id="group_list_container">
                        <%
                        for (Tag tg : tags.get("groups")) 
                        {
                        %>
                            <div class="group_container">
                                <div class="tag_group_header">Location Name: <span class="tag_group_name"><%=tg.getValue()%></span></div>
                                <!-- NICOLE: I think the delete for the group should show up beside group name -->
                                <% 
                                if (permissions.getPermissionLevel("subjgrade") >= TaggerPermissionsManager.READ_WRITE_DELETE)
                                { %><span class="tag_remove_box" id="<%=tg.getDBID()%>">DELETE</span><% }
                                %>

                                <div class="tag_group_box" id="subjectgrade_tags_id_<%=tg.getDBID()%>">
                                    <% request.setAttribute("table_list", tg.getPubChildren(this_resource_id)); %>
                                    <% request.setAttribute("permissions", permissions); %>
                                    <% String this_table_id = "subjectgrade_tags_list_" + Integer.toString(tg.getDBID()); %>
                                    <jsp:include page="partials/generate_tag_table.jsp">
                                        <jsp:param name="table_id" value="<%=this_table_id%>" />
                                        <jsp:param name="perm_name" value="subjgrade" />
                                        <jsp:param name="res_id" value="<%=this_resource_id%>" />
                                    </jsp:include>
                                </div>

                                <% 
                                if (permissions.getPermissionLevel("subjgrade") >= TaggerPermissionsManager.READ_WRITE)
                                {    
                                %>
                                    
                                    <div class="horizontal_form_wrapper">
                                        <form name="add_subject_tag" id="add_subject_tag_id_<%=tg.getDBID()%>">
                                            <span class="form_text">Address </span><br/>
                                            <select name="tag_type">
                                                <%
                                                    for (String current : tag_option_names.get("address"))
                                                    {
                                                      %><option value="<%=current%>"><%=current%></option><%
                                                    }
                                                %>
                                            </select>
                                            <input type="hidden" name="publisher_id" value="<%=this_resource_id%>"/>
                                            <input type="hidden" name="parent_id" value="<%=tg.getDBID()%>"/>
                                            <input type = "text" name = "tag_value"/>
                                            <button type="button" class="add_subject_tag_to_group" id="add_subject_tag_to_group_<%=tg.getDBID()%>">ADD STREET INFO TO LOCATION</button>
                                        </form>
                                        
                                   </div>
                                    <div class="horizontal_form_wrapper">
                                        <form name="add_other_curric_tag" id="add_other_curric_tag_id_<%=tg.getDBID()%>">
                                            <span class="form_text">Country </span><br/>
                                            <select name="tag_id">
                                                <%
                                                    for (Tag current : tag_options.get("other_curriculum"))
                                                    {
                                                        %><option value="<%=Integer.toString(current.getDBID())%>"><%=current.getType()%>: <%=current.getValue()%></option><%
                                                    }
                                                %>
                                            </select>
                                            <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                            <input type="hidden" name="parent_id" value="<%=tg.getDBID()%>"/>
                                            <button type="button" class="add_other_curric_tag_to_group" id="add_other_curric_tag_to_group_<%=tg.getDBID()%>">ADD TAG TO GROUP</button>
                                        </form>
                                        
                                   </div>
                                    
                                   <div style="clear: both"></div>
                                <% } %>
                            </div>
                        <% 
                        } 
                        %>
                        </div>
                    </div>
                </div>


                 
             <!-- ################ CONTACT INFO ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">Contact Information</div>
                    <div class="tagger_section_instructions">
                        This is section is for adding information regarding the contact information of a Publisher (Phone, Fax, Website, Email).
                    </div>
                    <div id="contact_tags_container" class="tags_container">
                       <%
                            ArrayList<Tag> all_contact_tags = new ArrayList<Tag>();
                            all_contact_tags.addAll(tags.get("digital"));
                        	all_contact_tags.addAll(tags.get("phone"));
                           // all_medium_format_tags.addAll(tags.get("language"));
                            //all_medium_format_tags.addAll(tags.get("genre"));
                        %>
                        <% request.setAttribute("table_list", all_contact_tags); %>
                        <% request.setAttribute("permissions", permissions); %>

                        <% request.setAttribute("permissions", permissions); %>
                        <jsp:include page="partials/generate_tag_table.jsp">
                            <jsp:param name="table_id" value="contact_info_tags" />
                            <jsp:param name="perm_name" value="contact_info" />
                            <jsp:param name="res_id" value="<%=this_resource_id%>" />
                        </jsp:include>
                    </div>
                    
                    <% if (permissions.getPermissionLevel("people") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
						<div class="horizontal_form_wrapper">
                            <form name="add_contact_tag" id="add_contact_tag_id">
                                <span class="form_text">Status:</span><br/>
                                <select name="tag_type">
                                    <%
                                        for (String current : tag_option_names.get("phone"))
                                        {
                                            %><option value="<%=current%>"><%=current%></option><%
                                        }
                                    	for (String current : tag_option_names.get("digital"))
                                    	{
                                    		%><option value="<%=current%>"><%=current%></option><%
                                    	}
                                    	
                                    %>
                                </select>
                                <input type="text" name="tag_value"/>
                                <input type="hidden" name="publisher_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_contact_tag_submit">ADD TAG</button>
                            </form>
                            
                        </div>
                    <% } %>
                    <div style="clear: both"></div>
                 </div>
</div>

</body>
</html>