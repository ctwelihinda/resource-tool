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
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
Resource this_resource = (Resource) request.getAttribute("this_resource");
Integer this_resource_id = this_resource.getDBID();
ArrayList<Recommendation> recommendations = (ArrayList<Recommendation>) request.getAttribute("recommendations");
Integer rec_status = (Integer) request.getAttribute("rec_status");
String user_name = (String) request.getAttribute("user_name");
String curr_date = (String) request.getAttribute("curr_date");
Boolean is_rover = (Boolean) request.getAttribute("is_rover");
Boolean is_core = (Boolean) request.getAttribute("is_core");
TaggerPermissionsManager permissions = (TaggerPermissionsManager) request.getAttribute("permissions");
Flag flag = (Flag) request.getAttribute("flag");
String role_text = (String) request.getAttribute("role_text");
String page_title = (String) request.getAttribute("page_title");
String page_subtitle = (String) request.getAttribute("page_subtitle");
String page_edition = (String) request.getAttribute("page_edition");
String page_image = (String) request.getAttribute("page_image");

String title = (String) request.getAttribute("title");
String subtitle = (String) request.getAttribute("subtitle");
String edition = (String) request.getAttribute("edition");
String quick_desc = (String) request.getAttribute("quick_desc");
String quick_image_path = (String) request.getAttribute("quick_image_path");
String quick_info = (String) request.getAttribute("quick_info");

ArrayList<Integer> child_ids = this_resource.getChildIDs();
Resource parent = this_resource.getParent();
ArrayList<ResourceRelationship> relationships = this_resource.getResourceRelationships();

HashMap< String,ArrayList<Tag> > tags = (HashMap< String,ArrayList<Tag> >) request.getAttribute("tags");
HashMap< String,ArrayList<Tag> > tag_options = (HashMap< String,ArrayList<Tag> >) request.getAttribute("tag_options");
HashMap< String,ArrayList<String> > tag_option_names = (HashMap< String,ArrayList<String> >) request.getAttribute("tag_option_names");

////////////////////////////////////////////////////////////////////////////////////////////////////////
// BEGIN HTML
                                                       
    %>
    <!DOCTYPE html>
    <html>

        
        
<%

////////////////////////////////////////////////////////////////////////////////////////////////////////
// HEADER
%>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title><%=this_resource.getRNumber()%></title>

            <script src="javascript/jquery-latest.min.js"></script>
            <script src="javascript/jquery.ui.core.min.js"></script>
            <script src="javascript/jquery.ui.widget.min.js"></script>
            <script src="javascript/jquery.ui.position.min.js"></script>
            <script src="javascript/jquery.ui.menu.min.js"></script>
            <script src="javascript/jquery.ui.autocomplete.min.js"></script>
            <script src="javascript/tagger_scripts.js"></script>
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
                                { tag_id: tag_id, resource_id: <%=this_resource_id%> }
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

        
        
        
        
        
        
        
        
<%
////////////////////////////////////////////////////////////////////////////////////////////////////////
// BODY
%>
        
        <body>
            <div id="wrapper">
                
                <div id="top_area">
                    
                    <div class="top_new_link"><a target="_blank" href="CreateResource">New Resource</a></div>

                    <div id="right_top_box"><%
                        if (this_resource.getFinalRecommendation() == 1)
                        { %><div id="rec_msg_pos">This resource is LIVE!</div><% }
                        if (rec_status == 0)
                        { %><div id="rec_msg_none">There is no recommendation information for this resource</div><% }
                        if (rec_status == 1)
                        { %><div id="rec_msg_pos">Recommended by <%=recommendations.size()%> evaluator(s)</div><% }
                        if (rec_status == 2)
                        { %><div id="rec_msg_neg">NOT Recommended by <%=recommendations.size()%> evaluator(s)</div><% }
                        if (rec_status == 3)
                        { %><div id="rec_msg_both">Recommended by some evaluators but not recommended by others</div><% }
                        if (this_resource.isOutOfPrint())
                        { %><div id="rec_msg_neg">This resource is OUT OF PRINT</div><% }
                        if (flag != null)
                        {
                            %>
                            <div class="has_flag_div" id="has_flag_top">
                                <span class="flagged_notice">This resource has been flagged for follow-up</span>
                                <% if (permissions.getPermissionLevel("flag") >= TaggerPermissionsManager.READ_WRITE_DELETE) { %>
                                    <span class="flag_remove_box" id="<%=flag.getDBID()%>">REMOVE FLAG</span><br/>
                                <% } %>
                                <span class="form_text">Reason: 
                                <%
                                    Integer reason_code = flag.getReasonCode();
                                    String reason_text = "No reason given";
                                    if (reason_code != null)
                                    {
                                        switch (reason_code)
                                        {
                                            case 0: reason_text = "Recommended for other subject(s)";
                                                    break;
                                            case 1: reason_text = "Recommended for other grade(s)";
                                                    break;
                                            case 2: reason_text = "Recommended for other grade(s) and subject(s)";
                                                    break;
                                        }
                                    }
                                %>
                                <%=reason_text%>
                                </span><br/>
                                <span class="form_text">Comments: <%=flag.getComments()%></span><br/>
                            </div>
                            <%
                        }
                    %></div><%

                    %>
                    <%
                        String r_number_color = "brown";
                        if (is_rover)
                        { r_number_color = "#006837"; }
                    %>
                    <div id="left_top_box">
                        <% if (is_rover) { %><img id="ltb_rover_img" src="https://bblearndev.merlin.mb.ca/bbcswebdav/users/peter.broda/Home_Boy_Bear_smaller.png" /><% } %>
                        <div id="ltb_r_number" style="color: <%=r_number_color%>"><%=this_resource.getRNumber()%><%=role_text%></div>
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
                            <% if ((subtitle != null) && (!subtitle.equals(""))) { %> <div id="ctb_subtitle"><%=page_subtitle%></div> <% } %>
                            <% if ((edition != null) && (!edition.equals(""))) { %> <div id="ctb_edition"><%=page_edition%></div> <% } %>
                            <% if (page_image != null) { %><div id="ctb_image"><image src="<%=page_image%>"/></div><% } %>
                        </div>
                    </div>


                    <div style="clear:both;"></div>
                </div> <!--end top_area-->

<%
////////////////////////////////////////////////////////////////////////////////////////////////////////
// BODY CONTENT
%>
                
    <!-- ################ PARENT/CHILD ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">Relationships to Other Resources</div>
                    <div class="horizontal_form_wrapper">
                        <%
                            if (child_ids.size() > 0)
                            {
                                %>
                                    <div class="tagger_section_list_header">This resource has the following child resources:</div>
                                    <div id="children_container">

                                        <table class="resource_table" id="children_table">
                                            <thead>
                                                <tr>
                                                    <td>TYPE</td>
                                                    <td>R NUMBER</td>
                                                    <td>CHILD TITLE</td>
                                                    <td>CREATOR</td>
                                                    <td>DATE</td>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <% for (Integer child_id : child_ids)
                                                {
                                                    Resource r = ResourceLoader.loadByDBID(child_id);
                                                    String created_at_date = new SimpleDateFormat("MMM d, yyyy").format(r.getCreatedAt()); %>
                                                    <tr>
                                                        <td class="res_table_title">CHILD</td>
                                                        <td class="res_table_r"><a target="_blank" href="EditResource?resource_number=<%=r.getRNumber()%>"><%=r.getRNumber()%></a></td>
                                                        <td class="res_table_title"><a target="_blank" href="EditResource?resource_number=<%=r.getRNumber()%>"><%=r.getTitleAndSubtitle()%></a></td>
                                                        <td class="tag_table_user"><%=r.getCreatedBy()%></td>
                                                        <td class="tag_table_date"><%=created_at_date%></td>
                                                    </tr>
                                                <% } %>
                                            </tbody>
                                        </table>
                                    </div>
                                <%
                            }
                            if (parent != null)
                            {
                                %>
                                    <div class="tagger_section_list_header">This resource is a child of:</div>
                                    <div class="tags_container" id="parents_container">
                                        <table class="resource_table" id="parent_table">
                                            <thead>
                                                <tr>
                                                    <td>TYPE</td>
                                                    <td>R NUMBER</td>
                                                    <td>TITLE</td>
                                                    <td>ACTION</td>
                                                    <td>CREATOR</td>
                                                    <td>DATE</td>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <% String created_at_date = new SimpleDateFormat("MMM d, yyyy").format(parent.getCreatedAt()); %>
                                                <tr id="parent_row">
                                                    <td class="res_table_title">PARENT</td>
                                                    <td class="res_table_r"><a target="_blank" href="EditResource?resource_number=<%=parent.getRNumber()%>"><%=parent.getRNumber()%></a></td>
                                                    <td class="res_table_title"><a target="_blank" href="EditResource?resource_number=<%=parent.getRNumber()%>"><%=parent.getTitle()%></a></td>
                                                    <td class="res_table_edit">
                                                        <% if (permissions.getPermissionLevel("parent") >= TaggerPermissionsManager.READ_WRITE_DELETE) { %>
                                                            <span class="tag_remove_box" id="remove_parent">REMOVE PARENT</span>
                                                        <% } %>
                                                    </td>
                                                    <td class="tag_table_user"><%=parent.getCreatedBy()%></td>
                                                    <td class="tag_table_date"><%=created_at_date%></td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                <%
                            }
                            if ((parent == null) && (child_ids.size() == 0))
                            {
                                %><div class="tagger_section_list_header">This resource is a child of:</div><%
                            }
                        %>
                        <% if (permissions.getPermissionLevel("parent") >= TaggerPermissionsManager.READ_WRITE_DELETE) 
                        { %>
                            <div class="form_wrapper">
                                <form name="add_parent" id="add_parent_id">
                                    Enter the parent's R number here: <input type="text" name="parent_rnumber"/>
                                    <input type="hidden" name="child_id" value="<%=this_resource_id%>"/>
                                    <button type="button" id="add_parent_submit">ADD PARENT</button>
                                </form>
                                
                            </div>
                        <% } %>
                        
                        <!-- NICOLE: Put the form_wrapper and the wrapper around the text and button -->
                        <div class="tagger_section_instructions">
                            Should this resource be hidden and only its parent shown?
                        </div>

                        <% if (this_resource.hideIfChild()) { %>
                            <span class="longtext_header">This resource will NOT be shown, as long as it has a parent</span>
                            <% if (permissions.getPermissionLevel("parent") >= TaggerPermissionsManager.READ_WRITE)
                            {    
                            %>
                                
                                <div class="form_wrapper">
                                    <form name="hide_if_child" id="hide_if_child_id">
                                        <input type="hidden" name="resource_id" value="<%=this_resource_id%>" />
                                        <input type="hidden" name="hic_value" value="0" />
                                        <button type="button" id="hide_if_child_submit">Show It</button>
                                    </form>
                                </div>
                            <% } %>
                        <% } else { %>

                            
                                <div class="form_wrapper">
                                    <form name="hide_if_child" id="hide_if_child_id">
                            <span class="longtext_header">This resource WILL be shown, no matter what</span>
                            <% if (permissions.getPermissionLevel("parent") >= TaggerPermissionsManager.READ_WRITE)
                            {    
                            %>
                                    
                                        <input type="hidden" name="resource_id" value="<%=this_resource_id%>" />
                                        <input type="hidden" name="hic_value" value="1" />
                                        <button type="button" id="hide_if_child_submit">Hide It</button>
                                    </form>
                                </div>
                                    
                                
                            <% } %>
                        <% } %>

                    </div> <!--end of parent section-->
                    <div class="horizontal_form_wrapper">
                        <%
                            if (relationships.size() > 0)
                            {
                                %>
                                    <div class="tagger_section_list_header">This resource has the following relationships:</div>
                                    <div class="tags_container" id="other_resources_container">
                                        <table class="resource_table" id="parent_table">
                                            <thead>
                                                <tr>
                                                    <td>R NUMBER</td>
                                                    <td>TITLE</td>
                                                    <td>OTHER RESOURCE TYPE</td>
                                                    <td>THIS RESOURCE TYPE</td>
                                                    <td>ACTIONS</td>
                                                    <td>CREATOR</td>
                                                    <td>DATE</td>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            <%
                                                for (ResourceRelationship relationship : relationships)
                                                {
                                                    Resource other = ResourceLoader.loadByDBID(relationship.other_id);
                                                    %>
                                                    <% String created_at_date = new SimpleDateFormat("MMM d, yyyy").format(other.getCreatedAt()); %>
                                                    <tr id="<%=relationship.db_id%>">
                                                        <td class="res_table_r"><%=other.getRNumber()%></td>
                                                        <td class="res_table_title"><%=other.getTitle()%></td>
                                                        <td class="res_table_r"><%=relationship.other_type%></td>
                                                        <td class="res_table_r"><%=relationship.this_type%></td>
                                                        <td class="res_table_edit">
                                                            <span class="res_edit_box"><a target="_blank" href="EditResource?resource_number=<%=other.getRNumber()%>">EDIT</a></span>
                                                            <% if (permissions.getPermissionLevel("other_resources") >= TaggerPermissionsManager.READ_WRITE_DELETE) { %>
                                                                <span class="tag_remove_box remove_other_resource" id="<%=relationship.db_id%>">REMOVE RELATIONSHIP</span>
                                                            <% } %>
                                                        </td>
                                                        <td class="tag_table_user"><%=other.getCreatedBy()%></td>
                                                        <td class="tag_table_date"><%=created_at_date%></td>
                                                    </tr>
                                            <% } %>
                                            </tbody>
                                        </table>
                                    </div>
                                <%
                            }
                        %>
                        <% if (permissions.getPermissionLevel("other_resources") >= TaggerPermissionsManager.READ_WRITE_DELETE) 
                        { %>
                            <div class="form_wrapper">
                                <form name="add_other_resource" id="add_other_resource_id">
                                    Enter R number for the other resource here: <input type="text" name="resource_2_rnumber"/> <button type="button" id="add_other_resource_submit">ADD RELATIONSHIP</button><br/>
                                    <div class="horizontal_form_wrapper">
                                        <span class="form_text">Type for THIS resource:</span><br/>
                                        <select id="resource_1_type_select" name="resource_1_type">
                                            <option value="Selection">Selection</option>
                                            <option value="Source">Source</option>
                                        </select>
                                    </div>
                                    <!-- NICOLE: moved the button around for Add relationship -->
                                    
                                    <div class="horizontal_form_wrapper">
                                        <span class="form_text">Type for THE OTHER resource:</span><br/>
                                        <select id="resource_2_type_select" name="resource_2_type">
                                            <option value="Selection">Selection</option>
                                            <option value="Source">Source</option>
                                        </select>
                                    </div>
                                    <input type="hidden" name="resource_1_id" value="<%=this_resource_id%>"/>
                                    
                                </form>
                            </div>
                        <% } %>
                    </div> <!--end of other_resource section-->
					<div style="clear: both"></div>
                </div>

    <!-- ################ QUICK INFO ####################### -->
                <% if (permissions.getPermissionLevel("quick_info") >= TaggerPermissionsManager.READ_ONLY) { %>
                    <div class="tagger_section">
                        <div id="quick_info_list">
                            <div class="big_tag_row">Quick Info: <%=quick_info%></div>
                        </div>
                    </div>
                <% } %>
                    
    <!-- ################ TITLE ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">Title</div>
                    <div class="tagger_section_instructions">
                        Use this section to add the title(s). Multiple titles can be added if needed.
                        You can also add subtitles and series information.
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
                        <div id="subtitle_row" class="big_tag_row">
                            <span class="big_tag_name">Subtitle</span>
                            <span class="big_tag_value" id="subtitle_value"><%=subtitle%></span>
                            <% if (permissions.getPermissionLevel("title") >= TaggerPermissionsManager.READ_WRITE_DELETE) { %>
                                <span class="text_edit_box" id="subtitle_edit_box">EDIT</span>
                                <span class="text_remove_box" id="subtitle_remove_box">DELETE</span>
                            <% } %>
                        </div>
                        <div id="edition_row" class="big_tag_row">
                            <span class="big_tag_name">Edition</span>
                            <span class="big_tag_value" id="edition_value"><%=edition%></span>
                            <% if (permissions.getPermissionLevel("title") >= TaggerPermissionsManager.READ_WRITE_DELETE) { %>
                                <span class="text_edit_box" id="edition_edit_box">EDIT</span>
                                <span class="text_remove_box" id="edition_remove_box">DELETE</span>
                            <% } %>
                        </div>
                        <% request.setAttribute("table_list", tags.get("title")); %>
                        <% request.setAttribute("permissions", permissions); %>
                        <jsp:include page="partials/generate_tag_table.jsp">
                            <jsp:param name="table_id" value="title_tags" />
                            <jsp:param name="perm_name" value="title" />
                            <jsp:param name="res_id" value="<%=this_resource_id%>" />
                        </jsp:include>
                    </div>
                    <% if (permissions.getPermissionLevel("title") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        <div class="form_wrapper">
                            <form name="add_title_tag" id="add_title_tag_id">
                                <select id="title_type_select" name="tag_type">
                                    <%
                                        for (String current : tag_option_names.get("title"))
                                        {
                                            %><option value="<%=current%>"><%=current%></option><%
                                        }
                                    %>
                                </select>
                                <input type="text" name="tag_value"/>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_title_tag_submit">ADD TAG</button>
                            </form>
                            
                        </div>
                    <% } %>
                </div>


    <!-- ################ PUBLISHER/DISTRIBUTOR ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">Publisher and Distributor</div>
                    <div class="tagger_section_instructions">
                        Add the publisher and/or distributor name here.
                    </div>
                    <div id="pubdist_tags_container" class="tags_container">
                        <div class="right_info_box"></div>                
                        <% request.setAttribute("table_list", tags.get("pubdist")); %>
                        <% request.setAttribute("permissions", permissions); %>
                        <jsp:include page="partials/generate_tag_table.jsp">
                            <jsp:param name="table_id" value="pubdist_tags" />
                            <jsp:param name="perm_name" value="pubdist" />
                            <jsp:param name="res_id" value="<%=this_resource_id%>" />
                        </jsp:include>
                    </div>
                    <div style="clear: both"></div>

                    <% if (permissions.getPermissionLevel("pubdist") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        <div class="horizontal_form_wrapper">
                            <form name="add_publisher_tag" id="add_publisher_tag_id">
                                <span class="form_text">Publisher:</span><br/>
                                <input type="text" name="tag_value" id="auto_sugg_pub"/>
                                <input type="hidden" name="tag_type" value="Publisher"/>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_publisher_tag_submit">ADD PUBLISHER</button>
                            </form>
                            
                        </div>
                        <div class="horizontal_form_wrapper">
                            <form name="add_distributor_tag" id="add_distributor_tag_id">
                                <span class="form_text">Distributor:</span><br/>
                                <input type="text" name="tag_value" id="auto_sugg_distro"/>
                                <input type="hidden" name="tag_type" value="Distributor"/>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_distributor_tag_submit">ADD DISTRIBUTOR</button>
                            </form>
                            
                        </div>
                        <div style="clear: both"></div>
                    <% } %>
                </div>



    <!-- ################ DATES ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">Dates</div>
                    <div class="tagger_section_instructions">
                        This section is for dates pertaining to this resource. 
                        Please enter dates in one of the following formats: YYYY-MM-DD, YYYY-MM, or YYYY.
                        You must enter 2 digits for the month and date, so use leading zeros if needed (e.g. 01 for January).
                    </div>
                    <div id="date_tags_container" class="tags_container">
                        <% request.setAttribute("table_list", tags.get("date")); %>
                        <% request.setAttribute("permissions", permissions); %>
                        <jsp:include page="partials/generate_tag_table.jsp">
                            <jsp:param name="table_id" value="date_tags" />
                            <jsp:param name="perm_name" value="dates" />
                            <jsp:param name="res_id" value="<%=this_resource_id%>" />
                        </jsp:include>
                    </div>                    
                    <% if (permissions.getPermissionLevel("dates") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        <div class="form_wrapper">
                            <form name="add_date_tag" id="add_date_tag_id">
                                <select name="tag_type">
                                    <%
                                        for (String current : tag_option_names.get("date"))
                                        {
                                            %><option value="<%=current%>"><%=current%></option><%
                                        }
                                    %>
                                </select>
                                <input type="text" name="tag_value" id="date_input_field"/>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_date_tag_submit">ADD TAG</button>
                            </form>
                            
                        </div>
                    <% } %>
                </div>
    <!-- ################ TRACKING INFO ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">Tracking Information</div>
                    <div class="tagger_section_instructions">
                        This is section is for adding information regarding the physical location of a Resource.
                    </div>
                    <div id="tracking_info_tags_container" class="tags_container">
                        <% request.setAttribute("table_list", tags.get("tracking_info")); %>
                        <% request.setAttribute("permissions", permissions); %>
                        <jsp:include page="partials/generate_tag_table.jsp">
                            <jsp:param name="table_id" value="tracking_info_tags" />
                            <jsp:param name="perm_name" value="tracking_info" />
                            <jsp:param name="res_id" value="<%=this_resource_id%>" />
                        </jsp:include>
                    </div>
                    <% if (permissions.getPermissionLevel("tracking_info") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        <div class="form_wrapper">
                            <form name="add_tracking_info_tag" id="add_tracking_info_tag_id">
                            	<select name="tag_type">
                                    <%
                                        for (String current : tag_option_names.get("tracking_info"))
                                        {
                                            %><option value="<%=current%>"><%=current%></option><%
                                        }
                                    %>
                                </select>
                                <input type="text" name="tag_value"/>
                                <!--  <input type="hidden" name="tag_type" value="Tracking Info"/> -->
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_tracking_info_tag_submit">ADD TAG</button>
                            </form>
                            
                        </div>
                    <% } %>
                    
                </div>


    <!-- ################ OTHER DATA ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">Other Data</div>
                    <div class="tagger_section_instructions">
                        This section is for other types of data not found in other sections. 
                        Use the drop-down menu to select the field for which you would like to enter information, and use the text box to enter it.
                        Add the new tag by pressing the "ADD TAG" button.
                    </div>
                    <div class="tagger_section_instructions">
                        NOTE: You can save both a URL and hyperlink text in the External URL tag. Use a double colon (::) to separate them, with the text first, like this: Watch::http://rover.edonline.sk.ca... Or you can just enter a URL and nothing else; either will work.
                    </div>
                    <div id="other_tags_container" class="tags_container">
                        <div class="big_tag_row">Quick Image Path: <%=quick_image_path%></div>
                        <% request.setAttribute("table_list", tags.get("other")); %>
                        <% request.setAttribute("permissions", permissions); %>
                        <jsp:include page="partials/generate_tag_table.jsp">
                            <jsp:param name="table_id" value="other_tags" />
                            <jsp:param name="perm_name" value="other" />
                            <jsp:param name="res_id" value="<%=this_resource_id%>" />
                        </jsp:include>
                    </div>                    
                    <% if (permissions.getPermissionLevel("other") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        <div class="form_wrapper">
                            <form name="add_other_tag" id="add_other_tag_id">
                                <select id="other_type_select" name="tag_type">
                                    <%
                                        for (String current : tag_option_names.get("other"))
                                        {
                                            %><option value="<%=current%>"><%=current%></option><%
                                        }
                                    %>
                                </select>
                                <input type="text" id="other_data_input" name="tag_value"/>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_other_tag_submit">ADD TAG</button>
                            </form>
                            
                        </div>
                    <% } %>
                </div>


    <!-- ################ OUT OF PRINT ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">Out of Print</div>
                    <div class="tagger_section_instructions">
                        This section is just for adding the "Out of Print" designation
                    </div>
                    
                    <% if (this_resource.isOutOfPrint()) { %>
                    <div class="form_wrapper">
                        <form name="add_outofprint_tag" id="add_outofprint_tag_id">
                        <span class="longtext_header">This resource is OUT OF PRINT</span>
                        <% if (permissions.getPermissionLevel("outofprint") >= TaggerPermissionsManager.READ_WRITE)
                        {    
                        %>
                            
                                
                                    <input type="hidden" name="resource_id" value="<%=this_resource_id%>" />
                                    <input type="hidden" name="oop_value" value="0" />
                                    <button type="button" id="add_outofprint_submit">It's No Longer Out of Print</button>
                                </form>
                                
                            </div>
                        <% } %>
                    <% } else { %>
                    <div class="form_wrapper">
                        <form name="add_outofprint_tag" id="add_outofprint_tag_id">
                        <span class="longtext_header">This resource is NOT out of print</span>
                        <% if (permissions.getPermissionLevel("outofprint") >= TaggerPermissionsManager.READ_WRITE)
                        {    
                        %>
                            
                                
                                    <input type="hidden" name="resource_id" value="<%=this_resource_id%>" />
                                    <input type="hidden" name="oop_value" value="1" />
                                    <button type="button" id="add_outofprint_submit">Actually, It's Out of Print</button>
                                </form>
                                
                            </div>
                        <% } %>
                    <% } %>
                    
                </div>
					
					
					
					
					
    <!-- ################ BORROW FROM STF ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">Borrow from STF Link</div>
                    <div class="tagger_section_instructions">
                        This is section is for adding a link to the STF library.
                    </div>
                    <div id="stf_borrow_tags_container" class="tags_container">
                        <% request.setAttribute("table_list", tags.get("stf_borrow")); %>
                        <% request.setAttribute("permissions", permissions); %>
                        <jsp:include page="partials/generate_tag_table.jsp">
                            <jsp:param name="table_id" value="stf_borrow_tags" />
                            <jsp:param name="perm_name" value="stf_borrow" />
                            <jsp:param name="res_id" value="<%=this_resource_id%>" />
                        </jsp:include>
                    </div>
                    <% if (permissions.getPermissionLevel("stf_borrow") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        <div class="form_wrapper">
                            <form name="add_stf_borrow_tag" id="add_stf_borrow_tag_id">
                                <input type="text" name="tag_value"/>
                                <input type="hidden" name="tag_type" value="STF Link"/>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_stf_borrow_tag_submit">ADD TAG</button>
                            </form>
                            
                        </div>
                    <% } %>
                    
                </div>



    <!-- ################ PEOPLE ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">People</div>
                    <div class="tagger_section_instructions">
                        Add the names of any people involved in creating this resource. They can be authors, editors, translators, etc. 
                        Use the drop-down list to select a category and enter the name in the text box, one person at a time.
                        Press the "add tag" button to add it.
                    </div>
                    <div id="people_tags_container" class="tags_container">
                        <% request.setAttribute("table_list", tags.get("people")); %>
                        <% request.setAttribute("permissions", permissions); %>
                        <jsp:include page="partials/generate_tag_table.jsp">
                            <jsp:param name="table_id" value="people_tags" />
                            <jsp:param name="perm_name" value="people" />
                            <jsp:param name="res_id" value="<%=this_resource_id%>" />
                        </jsp:include>
                    </div>
                    <% if (permissions.getPermissionLevel("people") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        <div class="form_wrapper">
                            <form name="add_people_tag" id="add_people_tag_id">
                                <select name="tag_type">
                                    <%
                                        for (String current : tag_option_names.get("people"))
                                        {
                                            %><option value="<%=current%>"><%=current%></option><%
                                        }
                                    %>
                                </select>
                                <input type="text" name="tag_value"/>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_people_tag_submit">ADD TAG</button>
                            </form>
                            
                        </div>
                    <% } %>
                </div>


    <!-- ################ MEDIA AND FORMATS ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">Media, Formats, and Languages</div>
                    <div class="tagger_section_instructions">
                        Here you can add information about the medium and format of the resource. 
                        Add any that apply, even if that means, say, adding several different medium tags.
                    </div>
                    <div id="mediaformat_tags_container" class="tags_container">
                        <%
                            ArrayList<Tag> all_medium_format_tags = new ArrayList<Tag>();
                            all_medium_format_tags.addAll(tags.get("medium"));
                            all_medium_format_tags.addAll(tags.get("format"));
                            all_medium_format_tags.addAll(tags.get("language"));
                            all_medium_format_tags.addAll(tags.get("genre"));
                        %>
                        <% request.setAttribute("table_list", all_medium_format_tags); %>
                        <% request.setAttribute("permissions", permissions); %>
                        <jsp:include page="partials/generate_tag_table.jsp">
                            <jsp:param name="table_id" value="mediaformat_tags" />
                            <jsp:param name="perm_name" value="formats" />
                            <jsp:param name="res_id" value="<%=this_resource_id%>" />
                        </jsp:include>
                    </div>
                    <% 
                    if (permissions.getPermissionLevel("formats") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        <div class="horizontal_form_wrapper">
                            <form name="add_medium_tag" id="add_medium_tag_id">
                                <span class="form_text">Medium:</span><br/>
                                <select name="tag_id">
                                    <%
                                        for (Tag current : TagLoader.loadByTypeName("Medium"))
                                        {
                                            %><option value="<%=Integer.toString(current.getDBID())%>"><%=current.getValue()%></option><%
                                        }
                                    %>
                                </select>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_medium_tag_submit">ADD TAG</button>
                            </form>
                                                
                        </div>
                        <div class="horizontal_form_wrapper">
                            <form name="create_medium_tag" id="create_medium_tag_id">
                                <span class="form_text">Create and add your own media type:</span><br/>
                                <input type="text" name="tag_value">
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>                        
                                <input type="hidden" name="tag_type" value="Medium"/>
                                <button type="button" id="create_medium_tag_submit">CREATE AND ADD TAG</button>
                            </form>
                            
                        </div>
                        <div class="horizontal_form_wrapper">
                            <form name="add_format_tag" id="add_format_tag_id">
                                <span class="form_text">Formats (preset):</span><br/>
                                <select name="tag_id">
                                    <%
                                        for (Tag current : tag_options.get("format"))
                                        {
                                            %><option value="<%=Integer.toString(current.getDBID())%>"><%=current.getValue()%></option><%
                                        }
                                    %>
                                </select>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_format_tag_submit">ADD TAG</button>
                            </form>
                            
                        </div>
                        <div class="horizontal_form_wrapper">
                            <form name="add_format_tag_w_text" id="add_format_tag_w_text_id">
                                <span class="form_text">Other format info:</span><br/>
                                <select name="tag_type">
                                    <%
                                        for (String current : tag_option_names.get("format"))
                                        {
                                            %><option value="<%=current%>"><%=current%></option><%
                                        }
                                    %>
                                </select>
                                <input type="text" name="tag_value"/>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_format_tag_w_text_submit">ADD TAG</button>
                            </form>
                            
                        </div>
                        <div class="horizontal_form_wrapper">
                            <form name="add_language_tag" id="add_language_tag_id">
                                <span class="form_text">Languages:</span><br/>
                                <select name="tag_id">
                                    <%
                                        for (Tag current : tag_options.get("language"))
                                        {
                                            %><option value="<%=Integer.toString(current.getDBID())%>"><%=current.getValue()%></option><%
                                        }
                                    %>
                                </select>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_language_tag_submit">ADD TAG</button>
                            </form>
                            
                        </div>
                        <div class="horizontal_form_wrapper">
                            <form name="add_genre_tag" id="add_genre_tag_id">
                                <span class="form_text">Genres:</span><br/>
                                <select name="tag_id">
                                    <%
                                        for (Tag current : tag_options.get("genre"))
                                        {
                                            %><option value="<%=Integer.toString(current.getDBID())%>"><%=current.getValue()%></option><%
                                        }
                                    %>
                                </select>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_genre_tag_submit">ADD TAG</button>
                            </form>
                            
                        </div>
                        <div style="clear: both"></div>
                    <% } %>
                </div>



    <!-- ################ GENERAL CONTENT CATEGORIES ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">General Content Categories</div>
                    <div class="tagger_section_instructions">
                        This section is for tags describing the content as a whole, in terms of the topics and themes it covers and to which areas of Ministry focus (Canadian content, FNMI content, Bullying, etc.) it pertains.
                        You can also categorize this resource as "Core" or "Additional" here.
                        More specific tags can be added in the "Grades and Subjects" section below.
                    </div>
                    <div id="content_tags_container" class="tags_container">
                        <% request.setAttribute("table_list", tags.get("content")); %>
                        <% request.setAttribute("permissions", permissions); %>
                        <jsp:include page="partials/generate_tag_table.jsp">
                            <jsp:param name="table_id" value="content_tags" />
                            <jsp:param name="perm_name" value="content" />
                            <jsp:param name="res_id" value="<%=this_resource_id%>" />
                        </jsp:include>
                    </div>
                    <% 
                    if (permissions.getPermissionLevel("content") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        <div class="form_wrapper">
                            <form name="add_content_tag" id="add_content_tag_id">
                                <span class="form_text">Content Categories:</span><br/>
                                <select name="tag_id">
                                    <%
                                        for (Tag current : tag_options.get("content"))
                                        {
                                            %><option value="<%=Integer.toString(current.getDBID())%>"><%=current.getType()%>: <%=current.getValue()%></option><%
                                        }
                                    %>
                                </select>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                <button type="button" id="add_content_tag_submit">ADD TAG</button>
                            </form>
                            
                        </div>
                    <% } %>
                </div>


    <!-- ################ GRADES AND SUBJECTS ####################### -->                    
                <div class="tagger_section">
                    <div class="tagger_section_title">Subjects and Grades</div>
                    <div class="tagger_section_instructions">
                        All subject, grade level, outcome, strand, etc. tags must be added as part of a tag group. 
                        To create a new group, enter a name for the group (it can be anything, but something meaningful like "Science 1 tags" might be helpful for you) and click the "Add new group" button below.<br/><br/>
                        Tag groups are used to associate the resource with a particular area and level of study.
                        For example, if a resource applies to Math 5 (Shapes and Patterns module) and Science 6 (Crystal Structures module), you would use two groups: 
                        one containing the tags "Subject: Mathematics", "Level: 5", and "Module: Shapes and Patterns", and one containing the tags "Subject: Science", "Level: 6", and "Module: Crystal Structures".
                        If these six tags were not added to separate groups, there would be no way to know, say, to which level of the Science program the resource applies, or that "Crystal Structures" is a module in Science and not Math.<br/><br/>
                        You can attach any number of tags of any kind to a group, but the onus is on you to make sure that the tagging is accurate, meaningful, and logical.
                    </div>

                    <% 
                    if (permissions.getPermissionLevel("subjgrade") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        <form name="add_new_group" id="add_new_group_id">
                            Group Name: <input type="text" name="tag_name" />
                            <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                            <button type="button" id="add_new_group_button">ADD NEW CURRICULUM GROUP</button>
                        </form>
                        
                    <% } %>

                    <div id="subjectgrade_tags_container" class="tags_container">
                        <div id="group_list_container">
                        <%
                        for (Tag tg : tags.get("groups")) 
                        {
                        %>
                            <div class="group_container">
                                <div class="tag_group_header">Curriculum Name: <span class="tag_group_name"><%=tg.getValue()%></span></div>
                                <!-- NICOLE: I think the delete for the group should show up beside group name -->
                                <% 
                                if (permissions.getPermissionLevel("subjgrade") >= TaggerPermissionsManager.READ_WRITE_DELETE)
                                { %><span class="tag_remove_box" id="<%=tg.getDBID()%>">DELETE</span><% }
                                %>

                                <div class="tag_group_box" id="subjectgrade_tags_id_<%=tg.getDBID()%>">
                                    <% request.setAttribute("table_list", tg.getChildren(this_resource_id)); %>
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
                                        <form name="add_grade_tag" id="add_grade_tag_id_<%=tg.getDBID()%>">
                                            <span class="form_text">Grade/Level: </span><br/>
                                            <select name="tag_id">
                                                <%
                                                    for (Tag current : tag_options.get("grade"))
                                                    {
                                                        %><option value="<%=Integer.toString(current.getDBID())%>"><%=current.getValue()%></option><%
                                                    }
                                                %>
                                            </select>
                                            <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                            <input type="hidden" name="parent_id" value="<%=tg.getDBID()%>"/>
                                            <button type="button" class="add_grade_tag_to_group" id="add_grade_tag_to_group_<%=tg.getDBID()%>">ADD GRADE/LEVEL TO GROUP</button>
                                        </form>
                                        
                                   </div>
                                    <div class="horizontal_form_wrapper">
                                        <form name="add_subject_tag" id="add_subject_tag_id_<%=tg.getDBID()%>">
                                            <span class="form_text">Subject: </span><br/>
                                            <select name="tag_id">
                                                <%
                                                    for (Tag current : tag_options.get("subject"))
                                                    {
                                                        %><option value="<%=Integer.toString(current.getDBID())%>"><%=current.getValue()%></option><%
                                                    }
                                                %>
                                            </select>
                                            <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                            <input type="hidden" name="parent_id" value="<%=tg.getDBID()%>"/>
                                            <button type="button" class="add_subject_tag_to_group" id="add_subject_tag_to_group_<%=tg.getDBID()%>">ADD SUBJECT TO GROUP</button>
                                        </form>
                                        
                                   </div>
                                    <div class="horizontal_form_wrapper">
                                        <form name="add_other_curric_tag" id="add_other_curric_tag_id_<%=tg.getDBID()%>">
                                            <span class="form_text">Other tags: </span><br/>
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
                                    <div class="horizontal_form_wrapper">
                                        <span class="form_text">Outcome/strand/module etc. tags:<br/>(select a subject area and then click "Load Tags..." to populate the list)</span><br/>
                                        <form name="select_subject_for_outcomes_list" id="select_subject_for_outcomes_list_id_<%=tg.getDBID()%>">
                                            <select name="subject">
                                                <option value="generic">Generic</option>
                                                <option value="arts">Arts Education</option>
                                                <option value="ela">English Language Arts</option>
                                                <option value="career">Career Education</option>
                                                <option value="c_and_c">Construction and Carpentry</option>
                                                <option value="francaise_imm">Francaise (Imm.)</option>
                                                <option value="health">Health</option>
                                                <option value="history">History</option>
                                                <option value="law">Law</option>
                                                <option value="life_trans">Life Transitions</option>
                                                <option value="math">Mathematics</option>
                                                <option value="native_studies">Native Studies</option>
                                                <option value="phys_ed">Physical Education</option>
                                                <option value="paa">Practical &amp; Applied Arts</option>
                                                <option value="prek">Prekindergarten</option>
                                                <option value="prog_interdisc">Programme d'études interdisciplinaire</option>
                                                <option value="psych">Psychology</option>
                                                <option value="science">Science</option>
                                                <option value="sciences">Sciences (Fr.)</option>
                                                <option value="sciences_humaines_fran">Sciences Humaines (Fr.)</option>
                                                <option value="social">Social Studies</option>
                                                <option value="treaty">Treaty Education</option>
                                                <option value="treaty_fr">Étude sur les traités</option>
                                                <option value="welding">Welding</option>
                                                <option value="wellness">Wellness</option>
                                            </select>
                                            <button type="button" class="select_subject_for_outcomes_list_submit" id="select_subject_for_outcomes_list_submit_<%=tg.getDBID()%>">LOAD TAGS FOR SUBJECT</button>
                                        </form>
                                        

                                        <form name="add_outcome_tag" id="add_outcome_tag_id_<%=tg.getDBID()%>">
                                            <select name="tag_value" class="outcome_list" size="7">
                                            </select>
                                            <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                            <input type="hidden" name="parent_id" value="<%=tg.getDBID()%>"/>
                                            <!-- NICOLE: Text Box buttons should be displayed below text boxes -->
                                            <br />
                                            <button type="button" class="add_outcome_tag_to_group" id="add_outcome_tag_to_group_id_<%=tg.getDBID()%>">ADD TAG TO GROUP</button>
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



    <!-- ################ LONG TEXT ####################### -->
                <div class="tagger_section">
                    <div class="tagger_section_title">Text</div>
                    <div class="tagger_section_instructions">
                        Here you can add text to the resource. Use the drop-down to select the category. 
                        Text saved as "Annotation" or "Notes" will appear in the record for this resource on the Ministry's curriculum site and ROVER.
                        Text saved as "Comments" will not; comments are just for taggers and evaluators, and will appear on this page only. So, choose carefully.
                    </div>
                    <div id="quick_desc">
                        <div class="big_tag_row">Quick Description: <%=quick_desc%></div>
                    </div>
                    <%
                        for (ResourceText rt : ResourceTextLoader.loadByResourceID(this_resource_id))
                        {
                            %><div class="longtext_wrapper"><%
                                %><span class="longtext_header"><%=rt.getType()%> (by <%=rt.getCreatedBy()%>): </span><%
                                if (permissions.getPermissionLevel("text") >= TaggerPermissionsManager.READ_WRITE)
                                { %><span class="text_edit_box" id="<%=rt.getDBID()%>">EDIT</span><% } 
                                if (permissions.getPermissionLevel("text") >= TaggerPermissionsManager.READ_WRITE_DELETE)
                                { %><span class="text_remove_box" id="<%=rt.getDBID()%>">DELETE</span><% } 
                                %><div class="longtext_block"><%=rt.getText().replace("\n", "<br/><br/>") %></div><%
                            %></div><%
                        }
                    %>
                    <% 
                    if (permissions.getPermissionLevel("text") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        <div class="form_wrapper">
                            <form name="add_text" id="add_text_id">
                                <span class="form_text">Purpose of Text:</span>
                                <select id="text_type_select" name="text_type">
                                    <option value="Annotation">Annotation</option>
                                    <option value="Notes">Notes</option>
                                    <option value="Comments">Comments</option>
                                    <option value="Order Note">Order Note</option>
                                    <option value="Cover Permission">Cover Permission</option>
                                </select>
                                <br/>
                                <textarea name="resource_text" cols="120" rows="10" wrap="soft"></textarea>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>" />
                                <br />
                                <button type="button" id="add_text_submit">ADD TEXT</button>
                            </form>
                            
                        </div>
                    <% } %>
                </div>

    <!-- ################ RECOMMENDED ####################### -->                    
                <div class="tagger_section">
                    <div class="tagger_section_title">Recommendation</div>
                    <div class="tagger_section_instructions">
                        Is this resource recommended? You can fill this out at any time, so feel free to leave this blank for now and come back to it later.
                        Be careful, though! Recommendations cannot be removed once you submit them.
                    </div>
                    <%
                        for (Recommendation r : recommendations)
                        {
                            %><div class="longtext_wrapper"><%
                                %><span class="tagger_section_list_header">Recommendation by <%=r.getCreatedBy()%> on <%=String.format("%1$tb %1$te, %1$tY", r.getCreatedAt())%>:</span><br/><%
                                if (r.getRecommended() != 0)
                                {
                                    %><span class="longtext_header">This resource is recommended</span><%
                                }
                                else
                                {
                                    %><span class="longtext_header">This resource is NOT recommended</span><%                                
                                }
                                if (permissions.getPermissionLevel("rec") >= TaggerPermissionsManager.READ_WRITE_DELETE)
                                { %><span class="rec_remove_box" id="<%=r.getDBID()%>">DELETE</span><% } 
                                %><div class="longtext_block"><%=r.getComments()%></div><%
                            %></div><%
                        }
                    %>
                    <% 
                    if (permissions.getPermissionLevel("rec") >= TaggerPermissionsManager.READ_WRITE)
                    {    
                    %>
                        
                        <div class="horizontal_form_wrapper">
                            <form name="add_recommendation" id="add_recommendation_id">
                                <span class="form_text">Is this resource is recommended?</span>
                                <!--NICOLE: I took a break out here and changed the col row of comments -->
                                <span class="form_text">Yes </span><input type="radio" name="recommended" value="1">
                                <span class="form_text">No  </span><input type="radio" name="recommended" value="0">
                                <br/>
                                <span class="form_text">You may use this text box to explain the decision: </span>
                                <br/>
                                <textarea name="comments" cols="60" rows="7" wrap="soft"></textarea>
                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>" />
                                <br />
                                <button type="button" id="add_recommendation_submit">ADD RECOMMENDATION</button>
                            </form>
                            
                        </div>
                    
                        <div class="horizontal_form_wrapper">
                            <%
                                if (flag != null)
                                {
                                    %>
                                        <div class="has_flag_div">
                                            <span class="flagged_notice">This resource has been flagged for follow-up</span>
                                            <% if (permissions.getPermissionLevel("flag") >= TaggerPermissionsManager.READ_WRITE_DELETE)
                                            { %><span class="flag_remove_box" id="<%=flag.getDBID()%>">REMOVE FLAG</span><br/><% } %>
                                            <span class="form_text">Reason: 
                                            <%
                                                Integer reason_code = flag.getReasonCode();
                                                String reason_text = "No reason given";
                                                if (reason_code != null)
                                                {
                                                    switch (reason_code)
                                                    {
                                                        case 0: reason_text = "Recommended for other subject(s)";
                                                                break;
                                                        case 1: reason_text = "Recommended for other grade(s)";
                                                                break;
                                                        case 2: reason_text = "Recommended for other grade(s) and subject(s)";
                                                                break;
                                                    }
                                                }
                                            %>
                                            <%=reason_text%>
                                            </span><br/><br/>
                                            <span class="tagger_section_list_header">Comments: </span><br/>
                                            <% if (permissions.getPermissionLevel("flag") >= TaggerPermissionsManager.READ_WRITE)
                                            { %>
                                                <form name="update_flag_comments" id="update_flag_comments_id">
                                                    <textarea name="comments" cols="50" rows="4" wrap="soft"><%=flag.getComments()%></textarea><br/>
                                                    <input type="hidden" name="flag_id" value="<%=flag.getDBID()%>"/>
                                                    <br />
                                                    <button type="button" id="update_flag_comments_submit">UPDATE COMMENTS</button>
                                                </form>
                                                
                                            <% } %>
                                        </div>
                                    <%
                                }
                                else
                                {
                                    if (permissions.getPermissionLevel("flag") >= TaggerPermissionsManager.READ_WRITE)
                                    { %>
                                        <div id="add_flag_div">
                                            <form name="add_flag" id="add_flag_id">
                                                <span class="form_text">Reason: </span><br/>
                                                <select name="reason_code">
                                                    <option value="0">Recommended for other subject(s)</option>
                                                    <option value="1">Recommended for other grade(s)</option>
                                                    <option value="2">Recommended for other grade(s) and subject(s)</option>
                                                </select>
                                                <br/><br/>
                                                <span class="form_text">You can add comments here if you wish: </span>
                                                <br/>
                                                <textarea name="comments" cols="50" rows="4" wrap="soft"></textarea>
                                                <input type="hidden" name="resource_id" value="<%=this_resource_id%>"/>
                                            </form>
                                            <button type="button" id="add_flag_submit">ADD FLAG</button>
                                        </div>
                                        <% 
                                    } 
                                }
                            %>
                       </div>
                            
                        <div style="clear: both;"></div>
                    <% } %>
                    
                </div>

    <!-- ################ FINAL APPROVAL ####################### -->                    
                <div class="tagger_section">
                    <div class="tagger_section_title">Final Approval</div>
                    <div class="tagger_section_instructions">
                        Is this resource recommended and approved for listing on the curriculum site?
                    </div>
                    <%
                        Integer live_status = this_resource.getFinalRecommendation();
                    
                        switch (live_status) 
                        {
                            case 0: // not approved and not live
                                %><span class="longtext_header">This resource has not yet been approved and is not live</span><%
                                if (permissions.getPermissionLevel("approval") >= TaggerPermissionsManager.READ_WRITE_DELETE) // full admin can make it live
                                {
                                    %>
                                        <form name="add_final_recommendation" id="add_final_recommendation_id">
                                            <input type="hidden" name="resource_id" value="<%=this_resource_id%>" />
                                            <input type="hidden" name="rec_value" value="1" />
                                            <button type="button" id="add_final_recommendation_submit">APPROVE and ADD TO LISTINGS</button>
                                        </form>
                                        
                                    <%
                                }
                                else if (permissions.getPermissionLevel("approval") == TaggerPermissionsManager.READ_WRITE) // read_write can only recommend to full admin
                                {
                                    %><i>Click the button below to approve this resource for the curriculum lists. An email will be sent to SPDU; they will make this resource live and available to the sector</i><%
                                    %>
                                        <form name="add_final_recommendation" id="add_final_recommendation_id">
                                            <input type="hidden" name="resource_id" value="<%=this_resource_id%>" />
                                            <input type="hidden" name="rec_value" value="2" />
                                            <button type="button" id="add_final_recommendation_submit">APPROVE THIS RESOURCE (will notify SPDU)</button>
                                        </form>
                                    
                                        
                                    <%
                                }
                                break;
                                
                            case 1: // resource is approved and live
                                %><span class="longtext_header">Yes, this resource has been approved and is live!</span><%
                                if (permissions.getPermissionLevel("approval") >= TaggerPermissionsManager.READ_WRITE_DELETE)
                                {
                                    %>
                                    <form name="add_final_recommendation" id="add_final_recommendation_id">
                                        <input type="hidden" name="resource_id" value="<%=this_resource_id%>" />
                                        <input type="hidden" name="rec_value" value="0" />
                                        <button type="button" id="add_final_recommendation_submit">REMOVE FROM LISTINGS</button>
                                    </form>
                                    
                                    <%
                                }
                                break;
                                
                            case 2: // resource is approved but not yet live
                                %><span class="longtext_header">This resource has been approved, but is not yet live</span><%
                                if (permissions.getPermissionLevel("approval") >= TaggerPermissionsManager.READ_WRITE_DELETE)
                                {
                                    %>
                                    <form name="add_final_recommendation" id="add_final_recommendation_id">
                                        <input type="hidden" name="resource_id" value="<%=this_resource_id%>" />
                                        <input type="hidden" name="rec_value" value="1" />
                                        <button type="button" id="add_final_recommendation_submit">ADD TO LISTINGS</button>
                                    </form>
                                    
                                    <%
                                }
                                break;
                        }
                    %>
                </div>
            </div>    
        </body>
    </html>
