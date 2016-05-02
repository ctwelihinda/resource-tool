<%-- 
    Document   : view_records
    Created on : 9-Mar-2015, 11:34:21 AM
    Author     : peter

	This is for displaying resource records in a format that is printable from the browser. It is called by the ViewRecords servlet, which in turn is called via the dashboard_popup.js javascript file

--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>--%>
<%@ page import="stans.resourcerecord.helpers.LanguageHelpers"%>
<%@ page import="stans.resourcerecord.model.ResourceRecord"%>
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
          System.out.println(recs[0].getRnumber());
          
        %>
        
        <c:forEach items="${resource_records}" var="r">

            <div class="resource_div">
                <div id="left_side_wrapper">
                    <div class="inner_resource_div">
                
                        <div class="resource_field" id="rnumber_field"><c:out value="${r.rnumber}" /></div>
                        
                        <c:if test="${r.coverImagePath != ''}">
                            <div class="resource_main_image"><img src="${r.coverImagePath}"/></div>
                        </c:if>
                        
                        <c:if test="${r.outOfPrint}">
                            <div id="outofprint_field"><% out.println(LanguageHelpers.getTranslation("This resource is out of print", display_language, null));%></div>
                        </c:if>
                        
                        <div class="resource_field" id="title_field"><c:out value="${r.title}" /></div>
                        <div class="resource_field" id="link_field"><a href="${r.linkString}" target="_blank"><c:out value="${r.linkString}"/></a></div>

                        <c:if test="${r.author != ''}">
                            <div class="resource_field" id="people_field">
                                <!-- <span class="resource_field_title" id="people_field_title">Author(s): </span>  -->
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Author(s)", display_language, null));%>: </span>
                                <c:out value="${r.author}" />
                            </div>
                        </c:if>
                        <c:if test="${r.editor != ''}">
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Editor(s)", display_language, null));%>: </span>
                                <c:out value="${r.editor}" />
                            </div>
                        </c:if>
                                                <c:if test="${r.illustrator != ''}">
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Illustrator(s)", display_language, null));%>: </span>
                                <c:out value="${r.illustrator}" />
                            </div>
                        </c:if>
                        <c:if test="${r.translator != ''}">
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Translator(s)", display_language, null));%>: </span>
                                <c:out value="${r.translator}" />
                            </div>
                        </c:if>
                        <c:if test="${r.producer != ''}">
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Producer(s)", display_language, null));%>: </span>
                                <c:out value="${r.producer}" />
                            </div>
                        </c:if>
                        <c:if test="${r.narrator != ''}">
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Narrator(s)", display_language, null));%>: </span>
                                <c:out value="${r.narrator}" />
                            </div>
                        </c:if>
                        <c:if test="${r.composer != ''}">
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Composer(s)", display_language, null));%>: </span>
                                <c:out value="${r.composer}" />
                            </div>
                        </c:if>
                        <c:if test="${r.musician != ''}">
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Musician(s)", display_language, null));%>: </span>
                                <c:out value="${r.musician}" />
                            </div>
                        </c:if>
                        <c:if test="${r.creator != ''}">
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Creator(s)", display_language, null));%>: </span>
                                <c:out value="${r.creator}" />
                            </div>
                        </c:if>
                        <c:if test="${r.director != ''}">
                            <div class="resource_field" id="people_field">
                                <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Director(s)", display_language, null));%>: </span>
                                <c:out value="${r.director}" />
                            </div>
                        </c:if>

                        <c:if test="${r.content != ''}">
                            <div class="resource_field" id="content_field">
                                <span class="resource_field_title" id="content_field_title"><% out.println(LanguageHelpers.getTranslation("Content", display_language, null));%>: </span><br/>
                                <c:out escapeXml="false" value="${r.content}" />
                            </div>
                        </c:if>
                            
                    <div class="resource_field" id="annotation_field">
                        <c:out escapeXml="false" value="${r.annotation}" />
                        <c:if test="${r.notes != ''}">
                            <span class="resource_field_title"><% out.println(LanguageHelpers.getTranslation("Note", display_language, null));%>: </span><br/>
                            <c:out escapeXml="false" value="${r.notes}" />
                        </c:if>
                    </div>
                            
                    </div>
                </div>
                    
                <div id="right_side_wrapper">
                    <div class="inner_resource_div">
                        <c:forEach items="${r.curriculumGroups}" var="g">
                            <div class="resource_table">
                                <table>
                                    <thead>
                                        <%
                                           if (display_language.equals("fr")) {
                                               out.println("<tr><th></th> <th>M</th> <th>1</th> <th>2</th> <th>3</th> <th>4</th> <th>5</th> <th>6</th> <th>7</th> <th>8</th> <th>9</th> <th>10</th> <th>20</th> <th>30</th> </tr>");
                                           }
                                           else {
                                               out.println("<tr><th>PK</th> <th>K</th> <th>1</th> <th>2</th> <th>3</th> <th>4</th> <th>5</th> <th>6</th> <th>7</th> <th>8</th> <th>9</th> <th>10</th> <th>20</th> <th>30</th> </tr>");
                                           }
                                               
                                        %>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <c:forEach items="${g.hasGradeList}" var="h">
                                                <c:choose>
                                                    <c:when test="${h}">
                                                        <td class="grade_highlight">&#8730;</td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td class="grade_nohighlight">&nbsp;</td>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </tr>
                                    </tbody>
                                </table>
                                    
                                <div class="resource_field" id="subject_field">
                                    <span class="resource_field_title" id="people_field_title"><% out.println(LanguageHelpers.getTranslation("Subject(s)", display_language, null));%>: </span>
                                    <c:out value="${g.subjects}" />
                                </div>
                                <c:if test="${g.keywords != ''}">
                                    <div class="resource_field" id="keywords_field">
                                        <span class="resource_field_title" id="keywords_field_title"><% out.println(LanguageHelpers.getTranslation("Keyword(s)", display_language, null));%>: </span>
                                        <c:out value="${g.keywords}" />
                                    </div> 
                                </c:if>
                            </div>
                        </c:forEach>
                        
                        
                        <c:choose>
                            <c:when test="${r.publisher} == ${r.distributor}">
                                <div class="resource_field" id="publisher_field">
                                    <span class="resource_field_title" id="publisher_field_title"><% out.println(LanguageHelpers.getTranslation("Publisher", display_language, null));%> / <% out.println(LanguageHelpers.getTranslation("Distributor", display_language, null));%>: </span><br/>
                                    <c:out escapeXml="false" value="${r.publisher}" />
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="resource_field" id="publisher_field">
                                    <span class="resource_field_title" id="publisher_field_title"><% out.println(LanguageHelpers.getTranslation("Publisher", display_language, null));%>: </span><br/>
                                    <c:out escapeXml="false" value="${r.publisher}" />
                                </div>
                                <div class="resource_field" id="publisher_field">
                                    <span class="resource_field_title" id="publisher_field_title"><% out.println(LanguageHelpers.getTranslation("Distributor", display_language, null));%>: </span><br/>
                                    <c:out escapeXml="false" value="${r.distributor}" />
                                </div>
                            </c:otherwise>
                        </c:choose>
                    
                        <div class="resource_field" id="price_field">
                            <span class="resource_field_title" id="price_field_title"><% out.println(LanguageHelpers.getTranslation("Price", display_language, null));%>: </span>
                            <c:out value="${r.price}" />
                        </div>
                        <div class="resource_field" id="medium_field">
                            <span class="resource_field_title" id="medium_field_title"><% out.println(LanguageHelpers.getTranslation("Medium", display_language, null));%>: </span>
                            <c:out value="${r.medium}" />
                        </div>
                        <div class="resource_field" id="pagination_field">
                            <span class="resource_field_title" id="pagination_field_title"><% out.println(LanguageHelpers.getTranslation("Pagination", display_language, null));%> / <% out.println(LanguageHelpers.getTranslation("Format", display_language, null));%>: </span>
                            <c:out escapeXml="false" value="${r.format}" />
                        </div>
                        <div class="resource_field" id="pagination_field">
                            <span class="resource_field_title" id="pagination_field_title"><% out.println(LanguageHelpers.getTranslation("ISBN", display_language, null));%>: </span>
                            <c:out escapeXml="false" value="${r.isbn}" />
                        </div>
                        <div class="resource_field" id="pagination_field">
                            <span class="resource_field_title" id="pagination_field_title"><% out.println(LanguageHelpers.getTranslation("Copyright", display_language, null));%>: </span>
                            <c:out escapeXml="false" value="${r.copyright}" />
                        </div>
                        <div class="resource_field" id="pagination_field">
                            <span class="resource_field_title" id="pagination_field_title"><% out.println(LanguageHelpers.getTranslation("Expiry Date", display_language, null));%>: </span>
                            <c:out escapeXml="false" value="${r.expiry}" />
                        </div>
                        <div class="resource_field" id="pagination_field">
                            <span class="resource_field_title" id="pagination_field_title"><% out.println(LanguageHelpers.getTranslation("ROVER ID", display_language, null));%>: </span>
                            <c:out escapeXml="false" value="${r.roverId}" />
                        </div>
                    </div>
                </div>
                <div style="clear:both"></div>
            </div>
        </c:forEach>
    </body>
</html>
