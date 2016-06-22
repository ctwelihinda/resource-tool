<!DOCTYPE html>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="stans.resourcerecord.helpers.specialCharHelpers"%>
<%@page import="stans.resourcerecord.dao.FlagLoader"%>
<%@page import="stans.resourcerecord.dao.ResourceLoader"%>
<%@page import="stans.resourcerecord.model.Tag"%>
<%@page import="stans.resourcerecord.model.Resource"%>
<%@page import="stans.resourcerecord.model.Flag"%>
<%@page import="stans.resourcerecord.model.Recommendation"%>
<%@page import="java.util.ArrayList"%>
<%@page import="stans.db.Query"%>
<%@page import="stans.EasyCourse"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="stans.EasyUser"%>
<%@page import="blackboard.data.course.CourseMembership"%>
<%@page import="blackboard.data.user.User"%>
<%@page import="blackboard.persist.user.impl.UserDbLoaderImpl"%>
<%@page import="blackboard.platform.context.ContextManagerFactory"%>
<%@page import="java.util.Calendar" %>
<%@page import="blackboard.servlet.util.DatePickerUtil" %>

<%@ taglib uri="/bbNG" prefix="bbNG" %>
<%@ taglib uri="/bbUI" prefix="bbUI" %>

<%

    // if the resources attribute has not been set by the search
    // servlet, populate the list with the most recent resources
    if (request.getAttribute("searchResults") == null) {
        //ArrayList<Resource> searchResults = ResourceLoader.loadRecent(10);
        ArrayList<Resource> searchResults = new ArrayList<Resource>();
        request.setAttribute("searchResults", searchResults);
    }
    EasyUser me = new EasyUser(request);
	
    String[] dateTypes = { "Review Date", "Order Date", "Received Date", "Copyright Date", "Expiry Date", "Date Recorded"};

%>
<link rel="stylesheet" type="text/css" href="css/dashboard_styles.css" />
<!--<link rel="stylesheet" type="text/css" href="https://bbtest.edonline.sk.ca/bbcswebdav/library/Resource%20Tool/css/dashboard_styles.css" />-->
<link rel="stylesheet" type="text/css" href="css/popup_styles.css" />
<!--<link rel="stylesheet" type="text/css" href="https://bbtest.edonline.sk.ca/bbcswebdav/library/Resource%20Tool/css/popup_styles.css" />-->
<script src="javascript/jquery-latest.min.js"></script>

<bbNG:genericPage authentication="N">

    <bbNG:pageHeader>
        <bbNG:pageTitleBar title="Resource Dashboard">
        </bbNG:pageTitleBar>
    </bbNG:pageHeader>   

    <div style="padding:20px; border: solid thin black; background-color: #eee">


        <div style="float:left;">
            <br/>
            <b>Search by Resource Field</b><br/>
            <form action="SearchResources">
                <bbNG:searchBar

                    searchLabel=":"
                    selectName="fieldName"
                    textfieldName="fieldValue"
                    operatorName="operator"
                    renderBodyContentInLine="false">                
                    <bbNG:searchOption label="Title" value="title" />
                    <bbNG:searchOption label="R Number" value="entry_id" />
                    <bbNG:searchOption label="Database ID" value="resource_id" />
                    <bbNG:searchOption label="Created By" value="created_by" />  
                    <bbNG:searchOption label="Created At" value="created_at" />  
                    <bbNG:searchOption label="Parent Database ID" value="parent_id" />  
                </bbNG:searchBar>
                <input type="hidden" name="searchType" value="resource" />    
            </form>
        </div>
        <div style="float:left;">
            <br/>
            <b>Search by Resource Tag</b><br/>
            <form action="SearchResources">
                <bbNG:searchBar            
                    searchLabel=":"
                    selectName="tagTypeName"
                    textfieldName="tagTypeValue"
                    operatorName="operator"
                    renderBodyContentInLine="false">
                    <bbNG:searchOption label="Series Title" value="Series Title" />            
                    <bbNG:searchOption label="Publisher" value="Publisher" />            
                    <bbNG:searchOption label="Distributor" value="Distributor" />            
                    <bbNG:searchOption label="Author" value="Author" />            
                    <bbNG:searchOption label="Subject" value="Subject" />            
                    <bbNG:searchOption label="Grade" value="Grade" />            
                    <bbNG:searchOption label="Level" value="Level" />              
                    <bbNG:searchOption label="Expiry Date" value="Expiry Date" />              
                </bbNG:searchBar>
                <input type="hidden" name="searchType" value="tag" />    
            </form>
        </div>
        <div style="float:left;">
            <br/>
            <b>Search by a Resource Flag</b><br/>
            <form action="SearchResources">
                <bbNG:searchBar
                    searchLabel=":"
                    selectName="flagFieldName"
                    textfieldName="flagFieldValue"
                    operatorName="operator"
                    renderBodyContentInLine="false">                
                    <bbNG:searchOption label="Comments" value="comments" />
                    <bbNG:searchOption label="Reason Code" value="reason_code" />                
                    <bbNG:searchOption label="User" value="user_id" />  
                    <bbNG:searchOption label="Created At" value="created_at" />  
                </bbNG:searchBar>
                <input type="hidden" name="searchType" value="flag" />    
            </form>
        </div>
        <div style="float:left;">
            <br/>
            <b>Search by Resource Recommendation</b><br/>
            <form action="SearchResources">
                <bbNG:searchBar            
                    searchLabel=":"
                    selectName="recommendationFieldName"
                    textfieldName="recommendationFieldValue"
                    operatorName="operator"
                    renderBodyContentInLine="false">
                    <bbNG:searchOption label="Recommendation Code" value="recommended" />            
                    <bbNG:searchOption label="Comments" value="comments" />            
                    <bbNG:searchOption label="Created By" value="created_by" />            
                    <bbNG:searchOption label="Created At" value="created_at" />                            
                </bbNG:searchBar>
                <input type="hidden" name="searchType" value="recommendation" />    
            </form>
        </div>
        <div style="float:left;">
            <br/>
            <b>Search by Text Attached to a Resource</b><br/>
            <form action="SearchResources">
                <bbNG:searchBar            
                    searchLabel=":"
                    selectName="resourceTextFieldName"
                    textfieldName="resourceTextFieldValue"
                    operatorName="operator"
                    renderBodyContentInLine="false">
                    <bbNG:searchOption label="Type" value="type" />            
                    <bbNG:searchOption label="text" value="text" />            
                    <bbNG:searchOption label="Created By" value="created_by" />            
                    <bbNG:searchOption label="Created At" value="created_at" />                            
                </bbNG:searchBar>
                <input type="hidden" name="searchType" value="resourcetext" />    
            </form>
        </div>
        <br style="clear:both;"/> <br/>
        <div>
        <form action="SearchResources">
              <bbNG:dataCollection>
               	<bbNG:step title="Search By Date Range:">
                	<bbNG:dataElement>
                	<bbNG:selectElement name= "tagTypeName">
                	<%for(int i = 0; i < dateTypes.length; i++){ %>
                		<%if(i == 0){ %>
                		<bbNG:selectOptionElement value="<%= dateTypes[i] %>" optionLabel = "<%=dateTypes[i] %>" isSelected = "true"/>
                		<%} %>
                		<bbNG:selectOptionElement value="<%= dateTypes[i] %>" optionLabel = "<%=dateTypes[i] %>"/>

                	<%} %>
                	</bbNG:selectElement>
                	
                	

					<% Calendar date = Calendar.getInstance();

					
					
					Calendar endDate = Calendar.getInstance();
					
					
					%>
						<bbNG:dateRangePicker startDateTime = "<%=date%>" endDateTime = "<%=endDate%>"/>
                	</bbNG:dataElement>
                </bbNG:step>
               	<bbNG:stepSubmit title= "dateSubmit" showCancelButton= "false">
               		

               		<bbNG:stepSubmitButton label= "Search" primary = "true"
               />
               	</bbNG:stepSubmit>
            </bbNG:dataCollection>
            <!--  <br/>
                <b>Search by Date Range </b><br/>
      
            <form action="SearchResources">
                <bbNG:searchBar            
                    searchLabel=":"
                    selectName="startDateFieldName"
                    textfieldName="resourceTextFieldValue"
                    operatorName="operator"
                    operatorValue="Between"
                    
                    renderBodyContentInLine="false">
                    <bbNG:searchOption label="Review Date" value="Review Date" />            
                    <bbNG:searchOption label="Copyright Date" value="Copyright Date" />            
                    <bbNG:searchOption label="Order Date" value="Order Date" />            
                    <bbNG:searchOption label="Recieved Date" value="Recieved Date" />                            
                </bbNG:searchBar>


                <input type="hidden" name="searchType" value="dates" />    
            </form>
            -->
            <input type="hidden" name="searchType" value="date_range"/>
		</form>
        </div>


        <br style="clear:both;"/>
        <br/>
        <div style="width:100%;">
            <b>Common Searches</b><br/>
            <a class="genericButton" href="SearchResources?searchType=recent&limit=100">
                <img src="/images/ci/icons/refresh.png" style="margin-bottom:4px; margin-right:3px; height:10px;">
                Recent Resources
            </a>
            <a class="genericButton" href="SearchResources?searchType=resource&fieldName=created_by&operator=Equals&fieldValue=<%=me.blackboard.getUserName()%>">                
                <img src="/images/ci/icons/user_ti.gif" style="margin-bottom:4px;height:10px;">
                My Resources
            </a>            
            <a class="genericButton" href="SearchResources?searchType=test&operator=NotBlank">
                <img src="/images/ci/icons/grade-correct_u.gif" style="margin-bottom:4px; margin-right:3px; height:10px;">
                Resources With Surveys
            </a>            
            <a class="genericButton" href="SearchResources?searchType=test&operator=Blank">
                <img src="/images/ci/icons/grade-incorrect_u.gif" style="margin-bottom:4px; margin-right:3px; height:10px;">
                Resources Without Surveys                
            </a>                        
            <a class="genericButton" href="SearchResources?searchType=flag&flagFieldName=created_at&operator=NotBlank">
                <img src="/images/ci/icons/grade_warning.gif" style="margin-bottom:4px; margin-right:3px; height:10px;">
                Flagged Resources
            </a>
            <a class="genericButton" href="SearchResources?recommendationFieldName=recommended&operator=Equals&recommendationFieldValue=1&searchType=recommendation">
                <img src="/images/ci/icons/check.gif" style="margin-bottom:4px; margin-right:3px; height:10px;">
                Recommended Resources
            </a>
            
            <a class="genericButton" href="SearchResources?recommendationFieldName=recommended&operator=Equals&recommendationFieldValue=0&searchType=recommendation">
                <img src="/images/ci/icons/x.gif" style="margin-bottom:4px; margin-right:3px; height:10px;">
                Rejected Resources
            </a>
<!--                
            <a class="genericButton" href="SearchResources?fieldName=created_at&operator=NotBlank&searchType=resource">
                <img src="/images/ci/icons/globe_ti.gif" style="margin-bottom:4px; margin-right:3px; height:10px;">
                Show All
            </a>
-->
        </div>
    </div>

<!-- POPUP DIV -->
    <div id="resource_popup">
        <div id="popup_top_bar">
            <span id="close_popup">X</span>
        </div>
        <div id="resource_popup_content">

        </div>                
    </div>

    <br/>
    <br/>

    <bbNG:inventoryList emptyMsg="No Results Found" collection="${searchResults}" objectVar="r" className="Resource" description="List Description"  >



        <bbNG:listElement label="RNumber" name="rnumber" isRowHeader="true">
            <%=r.getRNumber()%>
        </bbNG:listElement>


        <bbNG:listElement label="Live?" name="live">
            <% int live_status = r.getFinalRecommendation(); %>
            <% if (live_status == 1) {%>
                <img src="/images/ci/icons/check.gif" style="margin-bottom:4px;height:10px;" />
            <% } %>
            <% if (live_status == 0) {%>
                <img src="/images/ci/icons/x.gif" style="margin-bottom:4px;height:10px;" />
            <% } %>
            <% if (live_status == 2) {%>
                <span>approved</span>
            <% } %>
        </bbNG:listElement>

        <bbNG:listElement label="Notes" name="flagged">
            <% if (r.getFlags().size() > 0) {%>
            <a class="csstooltip" href="#">
                <%=r.getFlags().size()%> <img src="/images/ci/icons/grade_warning.gif" style="margin-bottom:4px;height:10px;" />           
                <span>
                    <% for(Flag f : r.getFlags()){ %>
                    <b><%= f.getUsername() %>(<%=f.getReasonCode()%>):</b> <%= f.getComments() %><br/>
                    <% } %>
                </span>
            </a>
            <% }%>
        </bbNG:listElement>
        <bbNG:listElement label="&nbsp;" name="positive_recommendations">            
            <% 
                HashMap popup_notes = new HashMap();
                int num_recs = 0;
                for (Recommendation rec : r.getPositiveRecommendations()) 
                {
                    String comments = rec.getComments();                    
                    if (comments != null) // by Pat's request. She didn't want to keep seeing the useless mateval recs
                    {
                        num_recs++;
                        popup_notes.put(rec.getCreatedBy(), comments);
                    }
                }
                if (num_recs > 0)
                {
                    %>
                    <a class="csstooltip" href="#">
                        <b><%=num_recs%></b><img src="/images/ci/icons/check.gif" style="margin-bottom:4px;height:10px;" />
                        <span>
                            <% 
                                Iterator it = popup_notes.entrySet().iterator();
                                while (it.hasNext())
                                {
                                    Map.Entry pairs = (Map.Entry)it.next();
                                    %><b><%= pairs.getKey() %>:</b> <%= pairs.getValue() %><br/><%
                                    it.remove();
                                }
                            %>
                        </span>
                    </a>
                    <%
                }
            %>
        </bbNG:listElement>
        <bbNG:listElement label="&nbsp;" name="negative_recommendations">
            <% if (r.getNegativeRecommendations().size() > 0) {%>
            <a class="csstooltip" href="#">
                <b><%=r.getNegativeRecommendations().size()%></b><img src="/images/ci/icons/x.gif" style="margin-bottom:4px;height:10px;" />
               <span>
                    <% for(Recommendation rec : r.getNegativeRecommendations()){ %>
                    <b><%= rec.getCreatedBy() %>:</b> <%= rec.getComments() %><br/>
                    <% } %>
                </span>
            </a>
            <% }%>            
        </bbNG:listElement>

        <bbNG:listElement label="Title" name="title">
            <!--<div style="width:400px;"></div>-->
            <div class="result_title_field open_popup" id="<%=r.getDBID()%>"><%=r.getTitle()%></div>
            <div class="result_children">
                <%
                for (Integer child_id : r.getChildIDs())
                {
                    Resource child_resource = new Resource(child_id);
                    %><div class="open_popup" id="<%=child_resource.getDBID()%>"><%=child_resource.getTitle()%></div><%
                }
                %>
            </div>
        </bbNG:listElement>

        <bbNG:listElement label="Created By" name="createdby">
            <%=r.getCreatedBy()%>
        </bbNG:listElement>

        <bbNG:listElement label="Created At" name="createdat">
            <%=r.getCreatedAt()%>
        </bbNG:listElement>

        <bbNG:listElement label="Resource Options" name="resourceoptions">
            <a class="genericButton" target="_blank" href="EditResource?resource_number=<%=r.getRNumber()%>">Edit</a>                
        </bbNG:listElement>

        <bbNG:listElement label="Survey Options" name="surveyoptions">
            <% if (r.getTest() != null) {%>
            <a target="_blank" class="genericButton" href="<%=r.getTest().shortcuts.getEditPath()%>">Edit</a>
            <a target="_blank" class="genericButton" href="<%=r.getTest().shortcuts.getEditOptionsPath()%>">Options</a>
            <a target="_blank" class="genericButton" href="<%=r.getTest().shortcuts.getAdaptiveReleasePath()%>">A.R.</a>       
            <% } else { %>
            <a target="_blank" href="/webapps/assessment/do/authoring/viewAssessmentManager?assessmentType=Test&course_id=_314_1"><i>Create Survey</i></a>
            &nbsp;|&nbsp;
            <a target="_blank" href="/webapps/assessment/do/content/assessment?action=ADD&course_id=_314_1&content_id=_196749_1&assessmentType=Test"><i>Deploy Survey</i></a>
            <%}%>
        </bbNG:listElement>
        <bbNG:listElement label="Survey Results" name="surveyresults">
            <% if (r.getTest() != null) {%>
            <a target="_blank" class="genericButton" href="<%=r.getTest().shortcuts.getAggregatedResultsPath()%>">Statistics</a>
            <a target="_blank" class="genericButton" href="<%=r.getTest().shortcuts.getResultsPath()%>">Individual</a>            
            <% } else { %>
            <i>N/A</i>
            <%}%>
        </bbNG:listElement>

    </bbNG:inventoryList>
            <br/>
            <br/>
            
            
</bbNG:genericPage>

            
<script src="javascript/dashboard_filters.js"></script>        
<script src="javascript/dashboard_popup.js"></script>        

<script>
    //JS lol
    var option_elements = document.getElementsByTagName("option");
    for (var i = 0, max = option_elements.length; i < max; i++) {
        option_elements[i].setAttribute("requiresinput", "true");
    }
    document.getElementById("listContainer_datatable").removeAttribute("title"); // so my tooltips dont get covered up
    
</script>




