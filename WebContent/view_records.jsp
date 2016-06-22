<%-- 
    Document   : view_records
    Created on : 9-Mar-2015, 11:34:21 AM
    Author     : peter

	This is for displaying resource records in a format that is printable from the browser. It is called by the ViewRecords servlet, which in turn is called via the dashboard_popup.js javascript file

--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> --%>
<%@ page import="stans.resourcerecord.helpers.LanguageHelpers"%>
<%@ page import="stans.resourcerecord.model.ResourceRecord"%>
<%@ page import="stans.resourcerecord.model.ResourceRecordCurriculumGroup" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>View Resource Records</title>
        
    <!-- CSS -->
        <link rel="stylesheet" type="text/css" href="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/css/resource_printing_styles.css" />
        <link rel="stylesheet" type="text/css" href="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/css/view_records_styles.css" />
    </head>
    

    <body>
        <div id="resource_print_link" onClick="javascript:window.print()">
            <div id="print_icon">
                <img src="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/images/print_icon.jpeg" />
            </div>
            <div id="print_text">
                PRINT
            </div>
        </div>
        <%         
            //String display_language = request.getAttribute("language");
            String display_language = String.valueOf(request.getAttribute("language"));
          ResourceRecord recs[] = (ResourceRecord [])request.getAttribute("resource_records");
          System.out.println(recs[0].getRnumber() +  " " + recs.length
        		  + " " + request.getAttribute("resource_records").toString());
          for(int i = 0; i< recs.length; i++ ){
        	  ResourceRecord r = recs[i];
          	
          
        %>
        
        

            <div class="resource_div">
                <div id="left_side_wrapper">
                    <div class="inner_resource_div">
                
                        <div class="resource_field" id="rnumber_field"> <%= r.getRnumber()%></div>
                        
                        <% if(!r.getCoverImagePath().equals("") ) { %>
                            <div class="resource_main_image"><img src=<%= r.getCoverImagePath() %>/></div>
                        <% } %>
                        
                        <% if(r.getOutOfPrint())  {%>
                            <div id="outofprint_field"><% out.println(LanguageHelpers.getTranslation("This resource is out of print", display_language, null));%></div>
                        <% } %>
                        
                        <div class="resource_field" id="title_field"><%= r.getTitle() %> </div>
                        <div class="resource_field" id="link_field"><a href="<%=r.getLinkString() %>" target="_blank"><%=r.getLinkString()%></a></div>
                        
                        <% if(r.getAuthor() != "") {%>
                            <div class="resource_field" id="people_field">
                                <!-- <span class="resource_field_title" id="people_field_title">Author(s): </span>  -->
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Author(s)", display_language, null));%>: <%=r.getAuthor() %>
                              </span>
                                
                            </div>
                        <% } %>
                        
                        <% if(r.getEditor() != "") {%>
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Editor(s)", display_language, null));%>: <%=r.getEditor() %>
                                </span>
                             
                            </div>
                        <% } %>
                        
                        <% if( r.getIllustrator() != "") {%>
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Illustrator(s)", display_language, null));%>: <%=r.getIllustrator() %>
                                </span>
                               
                            </div>
                        <% } %>
                        
                        <% if(r.getTranslator() != "") {%>
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Translator(s)", display_language, null));%>: <%=r.getTranslator()%>
                                </span>
                 
                            </div>
                        <% } %>

                        <% if(r.getProducer() != "") {%>
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Producer(s)", display_language, null));%>: <%=r.getProducer()%>
                                </span>
                                
                            </div>
                        <% } %>
                        
                        <% if( r.getNarrator() !=  ""){ %>
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Narrator(s)", display_language, null));%>: <%=r.getNarrator()%>
                                </span>
                                
                            </div>
                        <% } %>
                        
                        <% if( r.getComposer() != "") {%>
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Composer(s)", display_language, null));%>: <%=r.getComposer()%>
                                </span>
                                
                            </div>
                        <% } %>
                        
                        <% if( r.getMusician() != "") { %>
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Musician(s)", display_language, null));%>:  <%=r.getMusician()%>
                                </span>
                               
                            </div>
                        <% } %>
                        
                   		<% if( r.getCreator() != "") { %>
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Creator(s)", display_language, null));%>: <%=r.getCreator()%>
                                </span>
                                
                            </div>
                        <% } %>
                        
           				<% if( r.getDirector() != "") {%>
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Director(s)", display_language, null));%>: <%=r.getDirector()%>
                                </span>
                                
                            </div>
                        <% } %>
                        
                        <% if (r.getContent() != "") { %>
                            <div class="resource_field" id="content_field">
                                <span class="resource_field_title" id="content_field_title"><% out.println(LanguageHelpers.getTranslation("Content", display_language, null));%>: <%=r.getContent()%>
                                </span><br/>
                                
                            </div>
                        <% } %>
                        
                        <div class="resource_field" id="annotation_field">
                        <%=r.getAnnotation()%>
                        <% if( r.getNotes() != "") {%>
                            <span class="resource_field_title"><% out.println(LanguageHelpers.getTranslation("Note", display_language, null));%>: <%=r.getNotes()%>
                            </span><br/>
                            
                        <% } %>
                    	</div>                     

                            
                    </div>
                </div>
                
                
                <div id="right_side_wrapper">
                    <div class="inner_resource_div">
                    <% ResourceRecordCurriculumGroup[] g = r.getCurriculumGroups(); 
                    	for(int j = 0; j < g.length; j++) {%>
                    	
                            <div class="resource_table">
                                <table>
                                    <thead>
                                        <%
                                           if (display_language.equals("fr")) {
                                               out.println("<tr><th></th> <th>M</th> <th>1</th> <th>2</th> <th>3</th> <th>4</th> <th>5</th> <th>6</th> <th>7</th> <th>8</th> <th>9</th> <th>10</th> <th>11</th> <th>12</th> </tr>");
                                           }
                                           else {
                                               out.println("<tr><th></th> <th>K</th> <th>1</th> <th>2</th> <th>3</th> <th>4</th> <th>5</th> <th>6</th> <th>7</th> <th>8</th> <th>9</th> <th>10</th> <th>11</th> <th>12</th> </tr>");
                                           }
                                               
                                        %>
                                    </thead>
                                    <tbody>
                                        <tr>
                                        <% boolean[] h = g[j].getHasGradeList();
                                        	for(int k = 0; k < h.length; k++) {%>
                                       
                                                <% if(h[k]) { %>
                                          
                                                        <td class="grade_highlight">&#8730;</td>
                                                  <%} else { %>
                                               
                                                        <td class="grade_nohighlight">&nbsp;</td>
                                                  
                                               <%} %>
                                            <% } %>
                                        </tr>
                                    </tbody>
                                </table>
                                    
                                <div class="resource_field" id="subject_field">
                                    <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Subject(s)", display_language, null));%>: <%=g[j].getSubjects()%>
                                    </span>
                                    
                                </div>
                                <% if(g[j].getKeywords() != "") { %>
                                    <div class="resource_field" id="keywords_field">
                                        <span class="resource_field_title" id="keywords_field_title"><% out.println(LanguageHelpers.getTranslation("Keyword(s)", display_language, null));%>: <%=g[j].getKeywords() %>
                                        </span>
                                        
                                    </div> 
                                <%} %>
                            </div>                    	
                    	
                    	<%} //end Curriculum group for loop%>
                        
                            <% if((r.getPublisher()).equals(r.getDistributor())) {%>
                                <div class="resource_field" id="publisher_field">
                                    <span class="resource_field_title" id="publisher_field_title"><% out.println(LanguageHelpers.getTranslation("Publisher", display_language, null));%> / <% out.println(LanguageHelpers.getTranslation("Distributor", display_language, null));%>: <%= r.getPublisher()%>
                                    </span><br/>
                                    
                                </div>
                            <%} else { %>
                                <div class="resource_field" id="publisher_field">
                                    <span class="resource_field_title" id="publisher_field_title"><% out.println(LanguageHelpers.getTranslation("Publisher", display_language, null));%>: <%= r.getPublisher()%>
                                    </span><br/>
                                    
                                </div>
                                <div class="resource_field" id="publisher_field">
                                    <span class="resource_field_title" id="publisher_field_title"><% out.println(LanguageHelpers.getTranslation("Distributor", display_language, null));%>: <%= r.getDistributor()%></span><br/>
                                   
                                </div>
                            <%} %>
                            
                        <% if(r.getPrice() != ""){ %>
                        <div class="resource_field" id="price_field">
                            <span class="resource_field_title" id="price_field_title"><% out.println(LanguageHelpers.getTranslation("Price", display_language, null));%>:  <%=r.getPrice()%>
                            </span>
                        </div>
                        <% } %>
                        
                        
                        <% if(r.getMedium() != ""){ %>
                        <div class="resource_field" id="medium_field">
                            <span class="resource_field_title" id="medium_field_title"><% out.println(LanguageHelpers.getTranslation("Medium", display_language, null));%>: <%=r.getMedium()%>
                                
                            </span>

                        </div>
                        <% } %>

                        <% if(r.getFormat() != ""){ %>
                        <div class="resource_field" id="pagination_field">
                            <span class="resource_field_title" id="pagination_field_title"><% out.println(LanguageHelpers.getTranslation("Pagination", display_language, null));%> / <% out.println(LanguageHelpers.getTranslation("Format", display_language, null));%>: <%=r.getFormat()%></span>
                          
                        </div>
                        <% } %>

                        <% if(r.getIsbn() != ""){ %>
                        <div class="resource_field" id="pagination_field">
                            <span class="resource_field_title" id="pagination_field_title"><% out.println(LanguageHelpers.getTranslation("ISBN", display_language, null));%>: <%=r.getIsbn()%>
                            </span>
                        </div>
                        <% } %>

                        <% if(r.getCopyright() != ""){ %>
                        <div class="resource_field" id="pagination_field">
                            <span class="resource_field_title" id="pagination_field_title"><% out.println(LanguageHelpers.getTranslation("Copyright", display_language, null));%>: <%=r.getCopyright()%>
                            </span>
                        </div>
                        <% } %>

                        <% if(r.getExpiry() != ""){ %>
                        <div class="resource_field" id="pagination_field">
                            <span class="resource_field_title" id="pagination_field_title"><% out.println(LanguageHelpers.getTranslation("Expiry Date", display_language, null));%>: <%=r.getExpiry()%>
                            </span> 
                        </div>
                        <% } %>

                        <% if(r.getRoverId() != ""){ %>
                        <div class="resource_field" id="pagination_field">
                            <span class="resource_field_title" id="pagination_field_title"><% out.println(LanguageHelpers.getTranslation("ROVER ID", display_language, null));%>: <%=r.getRoverId()%>
                            </span>
                        </div>
                        <% } %>                           
                                          	
                 	</div>
                </div>
				<div style="clear:both"></div>
            </div>
            <% } //end for loop %>
       
    </body>
</html>
