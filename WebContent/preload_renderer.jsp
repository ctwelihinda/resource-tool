<%@page import="stans.resourcerecord.helpers.constants.ConstantLists"%>
<%@page import="stans.resourcerecord.helpers.LanguageHelpers"%>
<%@page import="java.util.Properties"%>
<%@page import="java.io.InputStream"%>
<%@page import="blackboard.platform.security.authentication.HttpAuthManager"%>
<%@page import="blackboard.platform.session.BbSessionManagerServiceFactory"%>
<%@page import="blackboard.platform.session.BbSession"%>
<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="org.w3c.dom.NodeList"%>
<%@page import="stans.XMLReader"%>
<%@page import="stans.resourcerecord.model.CurriculumSearchResult"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.io.ByteArrayInputStream"%>
<%@page import="blackboard.cms.filesystem.CSFile"%>
<%@page import="blackboard.cms.filesystem.CSContext"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.StringWriter"%>
<%@page import="com.xythos.storageServer.api.FileSystemDirectory"%>
<%@page import="com.xythos.storageServer.api.FileSystemEntry"%>
<%@page import="com.xythos.storageServer.api.FileSystem"%>
<%@page import="com.xythos.common.api.VirtualServer"%>
<%@page import="blackboard.cms.xythos.XythosContextUtil"%>
<%@page import="stans.db.Enumerators.BBComparisonOperator"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="stans.resourcerecord.helpers.specialCharHelpers"%>
<%@page import="stans.resourcerecord.dao.FlagLoader"%>
<%@page import="stans.resourcerecord.dao.TagLoader"%>
<%@page import="stans.resourcerecord.dao.TagTypeLoader"%>
<%@page import="stans.resourcerecord.dao.ResourceLoader"%>
<%@page import="stans.resourcerecord.dao.ResourceTextLoader"%>
<%@page import="stans.resourcerecord.model.Tag"%>
<%@page import="stans.resourcerecord.model.Resource"%>
<%@page import="stans.resourcerecord.model.ResourceText"%>
<%@page import="stans.resourcerecord.model.Flag"%>
<%@page import="stans.resourcerecord.model.Recommendation"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Collections"%>
<%@page import="stans.db.Query"%>
<%@page import="stans.EasyCourse"%>
<%@page import="java.util.List"%>
<%@page import="stans.EasyUser"%>
<%@page import="blackboard.data.course.CourseMembership"%>
<%@page import="blackboard.data.user.User"%>
<%@page import="blackboard.persist.user.impl.UserDbLoaderImpl"%>
<%@page import="blackboard.platform.context.ContextManagerFactory"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

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

                         ////////////////////
//~*~*~*~*~*~*~*~*~*~*~  // INITIALIZATION //  ~*~*~*~*~*~*~*~*~*~*~
                         ////////////////////

    ArrayList<Resource> search_result_resources = (ArrayList<Resource>) request.getAttribute("searchResults");
    if (search_result_resources == null) {
        search_result_resources = new ArrayList<Resource>();
    }
    
    ArrayList<CurriculumSearchResult> search_results = new ArrayList<CurriculumSearchResult>();
    
	String cacheFileName = ( String )request.getAttribute( "cacheFileName" );
	
    String subjects_string = "";
    String grades_string = "";
    String display_language = "en";
    String curricName = ( String )request.getAttribute( "curricName" );
	if( curricName==null ) {
		curricName = "unknown";
	}
	
    ArrayList<String> short_subject_list = new ArrayList<String>();
    
    String subject = request.getParameter("subject");
    String level = request.getParameter("level");
    String resource_or_support = request.getParameter("resource_or_support");
    if (resource_or_support == null)
    { resource_or_support = ""; }
    String fran_or_imm = request.getParameter("fran_or_imm");
    if (fran_or_imm == null)
    { fran_or_imm = ""; }
    
    if (
            (fran_or_imm.equals("fran")) ||
            (fran_or_imm.equals("imm")) ||
            (fran_or_imm.equals("both"))
    )
    { display_language = "fr"; }
    
    
    if( subject != null ) {
        if (!subjects_string.equals(""))    
        { subjects_string = subjects_string + ", "; }
        subjects_string = subjects_string + subject;

        if ((subject.contains("Social")) || (subject.contains("History")) || (subject.contains("Psych")) || (subject.contains("Law")) || (subject.contains("Native")))
        { 
            short_subject_list.add("social"); 
            short_subject_list.add("psych"); 
            short_subject_list.add("history"); 
            short_subject_list.add("law"); 
            short_subject_list.add("native_studies"); 
        }
        if (subject.contains("English"))
        { short_subject_list.add("ela"); }
        if (subject.contains("Arts Ed"))
        { short_subject_list.add("arts"); }
        if (subject.contains("Math"))
        { short_subject_list.add("math"); }
        if (subject.contains("Career"))
        { short_subject_list.add("career"); }
        if (subject.contains("Health"))
        { short_subject_list.add("health"); }
        if (subject.contains("Practical"))
        { short_subject_list.add("paa"); }
        if (subject.contains("Carpentry"))
        { short_subject_list.add("c_and_c"); }
        if (subject.contains("Welding"))
        { short_subject_list.add("welding"); }
        if (subject.contains("Treaty"))
        { short_subject_list.add("treaty"); }
        if (subject.contains("Science")) {
	        if (subject.contains("Sciences")) {
				short_subject_list.add("sciences");
			} else {
				short_subject_list.add("science");
			}
		}
        if ((subject.contains("Wellness")) || (subject.contains("Physical E")))
        { 
            short_subject_list.add("phys_ed"); 
            short_subject_list.add("wellness"); 
        }
		
		if( subject.equals("Kindergarten") ) {
			System.out.println( "adding kindergarten to short_subject_list" );
			short_subject_list.add("kindergarten");
		}
		if( subject.equals("Prekindergarten") ) {
			System.out.println( "adding prekindergarten to short_subject_list" );
			short_subject_list.add("prekindergarten");
		}
    } else {
		subject = "";
	}
    
    if( level != null ) {
        if (!grades_string.equals(""))    
        { grades_string = grades_string + ", "; }
        grades_string = grades_string + level;
		
		if( level.equals("Kindergarten") ) {
			System.out.println( "adding kindergarten to short_subject_list" );
			short_subject_list.add("kindergarten");
		}
		if( level.equals("Prekindergarten") ) {
			System.out.println( "adding prekindergarten to short_subject_list" );
			short_subject_list.add("prekindergarten");
		}
    } else {
		level = "";
		System.out.println( "level is null" );
	}
    
    if (subjects_string.equals(""))     { subjects_string = ""; }
    if (grades_string.equals(""))       { grades_string = "";   }
    if (grades_string.endsWith(", "))   { grades_string = grades_string.replaceAll(", $", ""); }
    String join_string = "";
    if (!subjects_string.equals(""))
    { join_string = " "; }




                         //////////////////////////
//~*~*~*~*~*~*~*~*~*~*~  // CONTENT SYSTEM STUFF //  ~*~*~*~*~*~*~*~*~*~*~
                         //////////////////////////
    
// Cached list locations
    String cache_base_path = "/library/Curriculum Website/New Resource Search/Cached Lists";
    
    String fran_imm_string = "";
    if (fran_or_imm.equals("fran"))
    { fran_imm_string = "f"; }
    if (fran_or_imm.equals("imm"))
    { fran_imm_string = "i"; }
    if (fran_or_imm.equals("both"))
    { fran_imm_string = "if"; }

    //String cacheFileName = subjects_string.toLowerCase().replaceAll(" ", "").replaceAll(",", "").replaceAll("'", "").replaceAll("’", "") + fran_imm_string + grades_string.replaceAll(" ", "").replaceAll(",", "").replaceAll("Kindergarten", "k").replaceAll("Maternelle", "M").replaceAll(",", "") + ".html";
    
    if ((resource_or_support != null) && (resource_or_support.equals("support")))
    {
        cacheFileName = "support_" + cacheFileName;
    }
	cacheFileName = cacheFileName.toLowerCase();

// Open outcome list    
    String outcomelist_filename = "outcomes_list_en.xml";
    String querymanifest_filename = "query_manifest.xml";
    ByteArrayOutputStream outcomelist_os = new ByteArrayOutputStream();
    ByteArrayOutputStream querymanifest_os = new ByteArrayOutputStream();
    
    CSContext open_file_context = null;
    try {
        open_file_context = CSContext.getContext();
        CSFile outcomelist_file = (CSFile)open_file_context.findEntry("/library/Curriculum Website/New Resource Search/Outcomes Lists/" + outcomelist_filename);
        outcomelist_file.getFileContent(outcomelist_os);
        CSFile querymanifest_file = (CSFile)open_file_context.findEntry("/library/Curriculum Website/New Resource Search/Query Manifest/" + querymanifest_filename);
        querymanifest_file.getFileContent(querymanifest_os);
    } catch (Exception e) {
        open_file_context.rollback();
    } finally {
        if (open_file_context != null) {
            open_file_context.commit();
        }
    }

    ByteArrayInputStream outcomelist_is = new ByteArrayInputStream(outcomelist_os.toByteArray());
    ByteArrayInputStream querymanifest_is = new ByteArrayInputStream(querymanifest_os.toByteArray());
    XMLReader outcomelist_xml = new XMLReader();
    XMLReader querymanifest_xml = new XMLReader();
    outcomelist_xml.setStream(outcomelist_is);
    querymanifest_xml.setStream(querymanifest_is);

    ArrayList<String> area_filters = new ArrayList<String>();
    HashMap<String,Boolean> allowed_area_tags = new HashMap();
    HashMap allowed_subject_tags = new HashMap();
    for (String short_subj : short_subject_list)
    {
		//System.out.println( "short_subj is " + short_subj );
		//System.out.println( "Using \"" + subject.toLowerCase().replaceAll(" ", "") + level.toLowerCase() + "\" for query manifest path" );
        String partial_xpath_query = "queries/query[@name='" + subject.toLowerCase().replaceAll(" ", "") + level.toLowerCase() + "']/";
		
		//System.out.println( "Searching query manifest for \"" + partial_xpath_query + "filters/filter\"" );
		//System.out.println( "Searching query manifest for \"" + partial_xpath_query + "other_subjects/other_subject\"" );
        NodeList filter_nodelist = querymanifest_xml.getNodelist(partial_xpath_query + "filters/filter");
        NodeList subject_nodelist = querymanifest_xml.getNodelist(partial_xpath_query + "other_subjects/other_subject");
        
		if( subject_nodelist != null ) {
			//System.out.println( "subject_nodelist size is " + Integer.toString( subject_nodelist.getLength() ) );
		} else {
			//System.out.println( "subject_nodelist is null" );
		}
        if ((subject_nodelist != null) && (subject_nodelist.getLength() > 0)) // if a list of filters is set in query_manifest
        {
            for (int i=0; i<subject_nodelist.getLength(); i++) // allow just the subjects that are listed
            {
                allowed_subject_tags.put(subject_nodelist.item(i).getTextContent(), true);
				//System.out.println("allowed_subject_tag: " + subject_nodelist.item(i).getTextContent());
            }
        }
        
		if( filter_nodelist != null ) {
			//System.out.println( "filter_nodelist size is " + Integer.toString( filter_nodelist.getLength() ) );
		} else {
			//System.out.println( "filter_nodelist is null" );
		}
        if ((filter_nodelist != null) && (filter_nodelist.getLength() > 0)) // if a list of filters is set in query_manifest
        {
            for (int i=0; i<filter_nodelist.getLength(); i++) // allow just the filters that are listed
            {
				String thisFilter = filter_nodelist.item(i).getTextContent();
				if( !area_filters.contains( thisFilter ) ) {
					area_filters.add( thisFilter ); // do this here so we get them in desired order
				}
                allowed_area_tags.put( thisFilter, false ); // set to false initially, then to true if we find a resource that uses this tag
            }
            
/*			System.out.println( "area_filters: ");
			for( String listFilter : area_filters ) {
				System.out.println( listFilter );
			}
*/			
        // however, we still want to allow numerical tags (i.e. outcomes)
            String partial_xpath_outcome = "subjects/subject[@name='" + short_subj + "']/";
            NodeList outcome_nodelist = outcomelist_xml.getNodelist(partial_xpath_outcome + "outcomes/outcome");
            for (int i=0; i<outcome_nodelist.getLength(); i++)
            {
                Pattern outcome_grade_pattern = Pattern.compile(".*" + level + "\\..*");
                Pattern outcome_number_pattern = Pattern.compile(".*\\d+\\..*");

                Matcher outcome_grade_matcher = outcome_grade_pattern.matcher(outcome_nodelist.item(i).getTextContent());
                Matcher outcome_number_matcher = outcome_number_pattern.matcher(outcome_nodelist.item(i).getTextContent());

                if (outcome_number_matcher.matches()) // if it's of the form *[some number(s)].*
                {
                    if (outcome_grade_matcher.matches()) { // ...and the number is the one we want
                        allowed_area_tags.put(outcome_nodelist.item(i).getTextContent(), false); // set to false initially, then to true if we find a resource that uses this tag
                    }
                }
            }
        }
        else // if there are no filters listed in query_manifest
        {
            String partial_xpath_outcome = "subjects/subject[@name='" + short_subj + "']/";
            NodeList outcome_nodelist = outcomelist_xml.getNodelist(partial_xpath_outcome + "outcomes/outcome");
            for (int i=0; i<outcome_nodelist.getLength(); i++)
            {
                Pattern outcome_grade_pattern = Pattern.compile(".*" + level + "\\..*");
                Pattern outcome_number_pattern = Pattern.compile(".*\\d+\\..*");

                Matcher outcome_grade_matcher = outcome_grade_pattern.matcher(outcome_nodelist.item(i).getTextContent());
                Matcher outcome_number_matcher = outcome_number_pattern.matcher(outcome_nodelist.item(i).getTextContent());

                if (outcome_number_matcher.matches()) // if it's of the form *[some number(s)].*
                {
                    if (outcome_grade_matcher.matches()) // ...and the number is the one we want
                    {
                        allowed_area_tags.put(outcome_nodelist.item(i).getTextContent(), false); // set to false initially, then to true if we find a resource that uses this tag
                    }
                }
                else // any other tag is fine, as long as it isn't of the number form
                {
                    allowed_area_tags.put(outcome_nodelist.item(i).getTextContent(), false); // set to false initially, then to true if we find a resource that uses this tag
                }

            }
        }
    }


                         /////////////
//~*~*~*~*~*~*~*~*~*~*~  // FILTERS //  ~*~*~*~*~*~*~*~*~*~*~
                         /////////////

    String filter_position = request.getParameter("filter_position");
    if (filter_position == null)
    { filter_position = "side"; }
    
    ArrayList<String> content_filters = new ArrayList<String>();
    ArrayList<String> subject_filters = new ArrayList<String>();
    ArrayList<String> grade_filters = new ArrayList<String>();
    ArrayList<String> language_filters = new ArrayList<String>();
    HashMap<String, Boolean> mediumformat_filters = new HashMap<String, Boolean>();

    HashMap<String, Boolean> allowed_tagtypes = new HashMap<String, Boolean>();
    allowed_tagtypes.put("Topic", true);
    allowed_tagtypes.put("Content Classification", true);
    allowed_tagtypes.put("Resource List Classification", true);
    allowed_tagtypes.put("Medium", true);
    allowed_tagtypes.put("Format", true);
    allowed_tagtypes.put("Language", true);
    allowed_tagtypes.put("Program", true);
    allowed_tagtypes.put("Outcome", true);
    allowed_tagtypes.put("Domain", true);
    allowed_tagtypes.put("Strand", true);
    allowed_tagtypes.put("Unit", true);
    allowed_tagtypes.put("Module", true);
    allowed_tagtypes.put("Goal", true);
    allowed_tagtypes.put("Subject", true);
    allowed_tagtypes.put("Grade", true);
    allowed_tagtypes.put("Level", true);

    ArrayList<Tag> searchFilters = ( ArrayList<Tag> )request.getAttribute("searchFilters");
    if( searchFilters==null ) {
    } else {
        for (Tag t : searchFilters) {
            if(
				(t.getType().equals("Topic")) ||
				(t.getType().equals("Content Classification")) //||
				//(t.getType().equals("Resource List Classification"))
            ) {
				content_filters.add(t.getValue());
			}
            
            if( (t.getType().equals("Medium") )||( t.getType().equals("Format")) ) {
                String t_value = t.getValue();
                String short_filter = ConstantLists.mediumformatShortlist.get(t_value);
                if( short_filter!=null ) {
                    mediumformat_filters.put(short_filter, true);
                } else {
                    mediumformat_filters.put(t_value, true);
                }
            }
            if (t.getType().equals("Language")) {
                language_filters.add(t.getValue());
            }
            if( ( t.getType().equals("Subject") )||( t.getType().equals("Program") ) ) {
                if( allowed_subject_tags.containsKey(t.getValue()) ) {
                    subject_filters.add(t.getValue());
                }
            }
            if(
				(t.getType().equals("Grade")) ||
				(t.getType().equals("Level"))
			) {
                grade_filters.add(t.getValue());
            }
            if(
				(t.getType().equals("Unit")) ||
				(t.getType().equals("Strand")) ||
				(t.getType().equals("Outcome")) ||
				(t.getType().equals("Domain")) ||
				(t.getType().equals("Module")) ||
				(t.getType().equals("Goal"))
            ) { 
                if( allowed_area_tags.containsKey(t.getValue()) ) {
                    //System.out.println("Area tag allowed: " + t.getValue());
					allowed_area_tags.put( t.getValue(), true );
                    //area_filters.add(t.getValue()); 
                } else {
                    //System.out.println("Area tag NOT allowed: " + t.getValue());
                }
                
            }
        }
		
		// if area_filters hasn't been filled, then fill it based on our allowed_tags hash
		// if it has been filled, it's because we wanted it in a certain order, so just remove entries that don't belong
		if( ( area_filters!=null )&&( !area_filters.isEmpty() ) ) {
			/*ArrayList<Integer> filterIndexesToRemove = new ArrayList<Integer>();
			for( int i=0; i<area_filters.size(); i++ ) {
				if( !allowed_area_tags.get( area_filters.get( i ) ) ) {
					filterIndexesToRemove.add( i );
				}
			}
			for( Integer r : filterIndexesToRemove ) {
				area_filters.remove( r );
			}*/
		} else {
			Iterator it = allowed_area_tags.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String,Boolean> pair = ( Map.Entry<String,Boolean> )it.next();
				if( pair.getValue() ) {
					area_filters.add( pair.getKey() );
				}
				it.remove(); // avoids a ConcurrentModificationException
			}
	        Collections.sort(area_filters);
		}
        //Collections.sort(grade_filters);
    }
    ArrayList<String> other_filters = new ArrayList<String>();
    other_filters.add("Free");
    

       
%>
 <!DOCTYPE HTML>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><%= curricName %></title>
        <link rel="stylesheet" type="text/css" href="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/css/popup_styles.css" />
        <link rel="stylesheet" type="text/css" href="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/css/resource_printing_styles.css" />
        <link rel="stylesheet" type="text/css" href="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/css/curriculum_list_styles.css" />
        <link rel="stylesheet" type="text/css" href="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/css/curriculum_list_sidefilters.css" />
        <script src="javascript/jquery-latest.min.js"></script>

        <script src="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/javascript/dashboard_filters.js"></script>
        <script src="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/javascript/dashboard_popup.js"></script>
        <script src="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/javascript/resource_list_scripts.js"></script>
        <script src="https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/javascript/sidefilter.js"></script>
    </head>

    <body>
        <div id="resource_search_form_wrapper">
            <form action="SearchResources" method="POST" name="get_search_params" id="resource_search_form">
                <div class="resource_search_formblock" id="resource_search_textblock">
                    Subject: <input type="text" name="subject" /><br/>
                    Level or Grade: <input type="text" name="level" /><br/>
                </div>
                <div class="resource_search_formblock" id="resource_search_radioblock">
                    <input type="radio" name="resource_or_support" value="both" checked="checked" /> Resource and Support<br/>
                    <input type="radio" name="resource_or_support" value="resource" /> Resources Only<br/>
                    <input type="radio" name="resource_or_support" value="support" /> Support Materials Only<br/>
                </div>
                <div class="resource_search_formblock" id="resource_search_radioblock">
                    <input type="radio" name="fran_or_imm" value="none" checked="checked" />N/A<br/>
                    <input type="radio" name="fran_or_imm" value="fran" /> Fransaskois Only<br/>
                    <input type="radio" name="fran_or_imm" value="imm" /> Immersion Only<br/>
                    <input type="radio" name="fran_or_imm" value="both" /> Immersion and Fransaskois<br/>
                </div>
                <input type="hidden" name="searchType" value="CURRICULUM_RESOURCE" />
                <input type="hidden" name="is_public" value="true" />    
                <input type="hidden" name="filter_position" value="<%=filter_position%>" />
                <div class="resource_search_formblock">
                    <input type="submit" value="Load resources and write to cache" />
                </div>
                <div style="clear:both;"></div>
            </form>
        </div>

        <%
            ArrayList<CurriculumSearchResult> core_resources = new ArrayList<CurriculumSearchResult>();
            ArrayList<CurriculumSearchResult> addl_resources = new ArrayList<CurriculumSearchResult>();
            ArrayList<CurriculumSearchResult> oum_resources = new ArrayList<CurriculumSearchResult>();
            Integer list_count = search_result_resources.size();
            for (Resource r : search_result_resources) {
                // add filterable tag values to class names
                
                CurriculumSearchResult this_result = new CurriculumSearchResult(r, allowed_tagtypes, ConstantLists.mediumformatShortlist, display_language);
                this_result.show_this_resource = true;
                
                if ((r.hideIfChild()) && (r.getParent() != null))
                {
                    this_result.show_this_resource = false;
                    list_count--; // don't include unshown resources in count
                }
                
                if (this_result.show_this_resource)
                {
                    if (this_result.is_core)
                    { core_resources.add(this_result); }
                    else if (this_result.is_support)
                    { oum_resources.add(this_result); }
                    else
                    { addl_resources.add(this_result); }
                }
            }
            Collections.sort(core_resources);
            Collections.sort(addl_resources);
            Collections.sort(oum_resources);

            search_results.addAll(core_resources);
            search_results.addAll(addl_resources);
            search_results.addAll(oum_resources);

    
            StringBuilder html_output_sb = new StringBuilder();


                              
                         ////////////////
//~*~*~*~*~*~*~*~*~*~*~  // POP-UP DIV //  ~*~*~*~*~*~*~*~*~*~*~
                         ////////////////
            
            html_output_sb.append("<div id=\"resource_popup\">");
            html_output_sb.append("<div id=\"popup_top_bar\">");
                html_output_sb.append("<span id=\"close_popup\" class=\"popup_top_button\">X</span>");
                html_output_sb.append("<span id=\"print_popup\" class=\"popup_top_button\"><a target=\"_blank\" href=\"#\">Click for a Printable Version</a></span>");
            html_output_sb.append("</div>");
            html_output_sb.append("<div id=\"resource_popup_content\"></div>");
            html_output_sb.append("</div>\n");

            
                         ////////////////////
//~*~*~*~*~*~*~*~*~*~*~  // LANGUAGE VALUE //  ~*~*~*~*~*~*~*~*~*~*~
                         ////////////////////
            
                              
            html_output_sb.append("<div id=\"display_language\" style=\"display:none\">");
            html_output_sb.append(display_language);
            html_output_sb.append("</div>");


                         /////////////////
//~*~*~*~*~*~*~*~*~*~*~  // PAGE HEADER //  ~*~*~*~*~*~*~*~*~*~*~
                         /////////////////
            
            html_output_sb.append("<div id=\"rl_wrapper\">");

                html_output_sb.append("<div id=\"rl_page_header\">\n");
                    html_output_sb.append("<div class=\"rl_page_title\">"); 
                        if (resource_or_support.equals("support"))
                        { html_output_sb.append(LanguageHelpers.getTranslation("Other Useful Materials for", display_language, null)); }
                        else
                        { html_output_sb.append(LanguageHelpers.getTranslation("Resources for", display_language, null)); }
                        html_output_sb.append(" "); 
                        html_output_sb.append(curricName);
                    html_output_sb.append("</div>\n");
                    html_output_sb.append("<div class=\"rl_page_subtitle\">");
                        html_output_sb.append(list_count);
                        html_output_sb.append(" ");
                        html_output_sb.append(LanguageHelpers.getTranslation("resources found", display_language, null));
                    html_output_sb.append("</div>\n");

                    html_output_sb.append("<div class=\"rl_page_link\">");
                    html_output_sb.append("<a id=\"select_resources_for_printing\" href=\"#\">");
                        html_output_sb.append(LanguageHelpers.getTranslation("Print/View Multiple Resource Records", display_language, null));
                    html_output_sb.append("</a>");
                    html_output_sb.append("</div>");
                html_output_sb.append("</div>\n\n");

                                  
                         //////////////////////////
//~*~*~*~*~*~*~*~*~*~*~  // RENDER SEARCH RESULTS //  ~*~*~*~*~*~*~*~*~*~*~
                         //////////////////////////

                html_output_sb.append("<div id=\"results_list_wrapper\">");

                    html_output_sb.append("<div id=\"results_list\">");

                        if ( !resource_or_support.equals( "support" ) ) { 
                            html_output_sb.append("<div class=\"partial_results_list\" id=\"core_results_list\">");
                                html_output_sb.append("<div class=\"list_header_area\" id=\"core_header_area\">");
                                    html_output_sb.append("<span class=\"list_description_link\" id=\"core_description_link\">");
                                        html_output_sb.append(LanguageHelpers.getTranslation("What is a core resource", display_language, null));
                                        html_output_sb.append("?");
                                    html_output_sb.append("</span>");
                                    html_output_sb.append("<span class=\"list_header\" id=\"core_list_header\">");
                                        html_output_sb.append(LanguageHelpers.getTranslation("Core Resources", display_language, null));
                                    html_output_sb.append("</span>");
                                    html_output_sb.append("<div class=\"list_description_popup\" id=\"core_description_popup\">");
                                        if (display_language.equals("fr"))
                                        {
                                            html_output_sb.append("Une ressource clé complète de façon importante et fondamentale  un programme d’études. Elle peut être une ressource à l’intention de l’élève qui aborde ou englobe plusieurs des résultats d'apprentissage (par ex. une ressource intégrée, une série, ou un manuel de l’élève), une ressource qui exploite en profondeur un ou quelques résultats d'apprentissage ou une ressource didactique pour les éducateurs.");
                                        }
                                        else
                                        {
                                            html_output_sb.append("A core resource is a foundational or key resource that complements a curriculum in an especially effective way. It may be a comprehensive resource that broadly addresses several of the student learning outcomes (e.g., a major integrated resource, series, textbook), an in-depth student resource that addresses at least one student learning outcome or a professional resource for educators.");
                                        }
                                    html_output_sb.append("</div>");
                                html_output_sb.append("</div>");

                                for ( CurriculumSearchResult this_result : core_resources ) {
                                    html_output_sb.append( this_result.generateHTML( display_language ) );
                                }
                            html_output_sb.append("</div>"); // end of core_results_list


                            html_output_sb.append("<div class=\"partial_results_list\" id=\"addl_results_list\">");
                                html_output_sb.append("<div class=\"list_header_area\" id=\"addl_header_area\">");
                                    html_output_sb.append("<span class=\"list_description_link\" id=\"addl_description_link\">");
                                        html_output_sb.append(LanguageHelpers.getTranslation("What are additional resources", display_language, null));
                                        html_output_sb.append("?");
                                    html_output_sb.append("</span>");
                                    html_output_sb.append("<span class=\"list_header\" id=\"addl_list_header\">");
                                        html_output_sb.append(LanguageHelpers.getTranslation("Additional Resources", display_language, null));
                                    html_output_sb.append("</span>");
                                    html_output_sb.append("<div class=\"list_description_popup\" id=\"addl_description_popup\">");
                                        if (display_language.equals("fr")) {
                                            html_output_sb.append("Une ressource supplémentaire complète également le programme d’études de façon efficace et est de la même variété que les ressources clé. Toutefois, lorsque les fonds sont limités, les éducateurs sont prévenus de commencer par l'achat de ressources clé.");
                                        } else {
                                            html_output_sb.append("Additional resources also complement the curriculum in an effective way and are of the same variety as core resources. However, where funds are limited, educators are advised to start by purchasing core resources.");
                                        }
                                    html_output_sb.append("</div>");
                                html_output_sb.append("</div>");

                                for ( CurriculumSearchResult this_result : addl_resources ) {
                                    html_output_sb.append( this_result.generateHTML( display_language ) );
                                }
                            html_output_sb.append("</div>"); // end of addl_results_list
                        }      

                        html_output_sb.append("<div class=\"partial_results_list\" id=\"oum_results_list\">");
                            html_output_sb.append("<div class=\"list_header_area\" id=\"oum_header_area\">");
                                html_output_sb.append("<span class=\"list_description_link\" id=\"oum_description_link\">");
                                    html_output_sb.append(LanguageHelpers.getTranslation("What are other useful materials", display_language, null));
                                    html_output_sb.append("?");
                                html_output_sb.append("</span>");
                                html_output_sb.append("<span class=\"list_header\" id=\"oum_list_header\">");
                                    html_output_sb.append(LanguageHelpers.getTranslation("Other Useful Materials", display_language, null));
                                html_output_sb.append("</span>");
                                html_output_sb.append("<div class=\"list_description_popup\" id=\"oum_description_popup\">");
                                    if (display_language.equals("fr"))
                                    {
                                        html_output_sb.append("Un matériel annexe consiste de documents, de plans de cours ou de leçons utiles pour soutenir un programme d'études qui, par habitude, n’est pas sujet au même processus d'évaluation formel exigé pour les ressources clé et supplémentaires.");
                                    }
                                    else
                                    {
                                        html_output_sb.append("Other useful materials are documents, lesson plans or classroom activities that teachers may find useful in supporting the curriculum, but that have not have not typically undergone the same formal evaluation process that is required for core and additional resources.");
                                    }
                                html_output_sb.append("</div>");
                            html_output_sb.append("</div>");

                            for ( CurriculumSearchResult this_result : oum_resources ) {
                                html_output_sb.append( this_result.generateHTML( display_language ) );
                            }
                        html_output_sb.append("</div>");
                    html_output_sb.append("</div>"); // end of results_list
                html_output_sb.append("</div>"); // end of results_list_wrapper

            
                         /////////////////////////////
//~*~*~*~*~*~*~*~*~*~*~  // RENDER FILTER DASHBOARD //  ~*~*~*~*~*~*~*~*~*~*~
                         /////////////////////////////
                html_output_sb.append("<div id=\"sidefilter_scroller_anchor\"></div>");
                html_output_sb.append("<div id=\"sidefilter_wrapper\">");            
                    html_output_sb.append("<div id=\"sidefilter_panel\">");
                        int filter_count = 0;
                        if (!resource_or_support.equals("support"))
                        {
                            html_output_sb.append("<div class=\"rl_filter_header\">");
                                html_output_sb.append(LanguageHelpers.getTranslation("Filter by Resource List", display_language, null));
                            html_output_sb.append("</div>");
                            html_output_sb.append("<div class=\"rl_filter_list\">");
                                html_output_sb.append("<div class=\"rl_filter_vertical\">");
                                    html_output_sb.append("<span class=\"rl_filter_bullet\">&#8226;</span>");
                                    html_output_sb.append("<span class=\"rl_filter list_filter\" id=\"core_only\">");
                                        html_output_sb.append(LanguageHelpers.getTranslation("Core Only", display_language, null));
                                    html_output_sb.append("</span>");
                                html_output_sb.append("</div>");
                                html_output_sb.append("<div class=\"rl_filter_vertical\">");
                                    html_output_sb.append("<span class=\"rl_filter_bullet\">&#8226;</span>");
                                    html_output_sb.append("<span class=\"rl_filter list_filter\" id=\"addl_only\">");
                                        html_output_sb.append(LanguageHelpers.getTranslation("Additional Only", display_language, null));
                                    html_output_sb.append("</span>");
                                html_output_sb.append("</div>");
                                html_output_sb.append("<div class=\"rl_filter_vertical\">");
                                    html_output_sb.append("<span class=\"rl_filter_bullet\">&#8226;</span>");
                                    html_output_sb.append("<span class=\"rl_filter list_filter\" id=\"oum_only\">");
                                        html_output_sb.append(LanguageHelpers.getTranslation("Other Useful Materials Only", display_language, null));
                                    html_output_sb.append("</span>");
                                html_output_sb.append("</div>");
                            html_output_sb.append("</div>");
                        }
                        html_output_sb.append("<div class=\"rl_filter_header\">");
                            html_output_sb.append(LanguageHelpers.getTranslation("Filter by Content", display_language, null));
                        html_output_sb.append("</div>");
                        html_output_sb.append("<div class=\"rl_filter_list\">");
                            filter_count = 0;
                            for (String f : content_filters) {
                                if (filter_count > 0) {
                                    html_output_sb.append("&nbsp;&#8226;&nbsp;");
                                }
                                html_output_sb.append("<span class=\"rl_filter filterable_object fname_f_");
                                html_output_sb.append(f.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", ""));
                                html_output_sb.append("\">");
                                    html_output_sb.append(LanguageHelpers.getTranslation(f, display_language, null));
                                html_output_sb.append("</span>");
                                filter_count++;
                            }
                        html_output_sb.append("</div>");
                        
                        if (!subject_filters.isEmpty())
                        {
							java.util.Collections.sort( subject_filters );
                            html_output_sb.append("<div class=\"rl_filter_header\">");
                                html_output_sb.append(LanguageHelpers.getTranslation("Filter by Subject", display_language, null));
                            html_output_sb.append("</div>");
                            html_output_sb.append("<div class=\"rl_filter_list\">");
                                filter_count = 0;
                                for (String f : subject_filters) {
                                    if (filter_count > 0) {
                                        html_output_sb.append("&nbsp;&#8226;&nbsp;");
                                    }
                                    html_output_sb.append("<span class=\"rl_filter filterable_object fname_f_");
                                    html_output_sb.append(f.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", ""));
                                    html_output_sb.append("\">");
                                        html_output_sb.append(LanguageHelpers.getTranslation(f, display_language, null));
                                    html_output_sb.append("</span>");
                                    filter_count++;
                                }
                            html_output_sb.append("</div>");
                        }
                        
                        html_output_sb.append("<div class=\"rl_filter_header\">");
                            html_output_sb.append(LanguageHelpers.getTranslation("Filter by Outcome/Unit/Strand", display_language, null));
                        html_output_sb.append("</div>");
                        html_output_sb.append("<div class=\"rl_filter_list\">");
                            filter_count = 0;
                            for (String f : area_filters) {
                                html_output_sb.append("<div class=\"rl_filter_vertical\">");
                                    html_output_sb.append("<span class=\"rl_filter_bullet\">&#8226;</span>");
                                    html_output_sb.append("<span class=\"rl_filter filterable_object fname_f_");
                                    html_output_sb.append(f.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", ""));
                                    html_output_sb.append("\">");
                                        html_output_sb.append(LanguageHelpers.getTranslation(f, display_language, null));
                                    html_output_sb.append("</span>");
                                html_output_sb.append("</div>");
                                filter_count++;
                            }
                        html_output_sb.append("</div>");
                        html_output_sb.append("<div class=\"rl_filter_header\">");
                            html_output_sb.append(LanguageHelpers.getTranslation("Filter by Language", display_language, null));
                        html_output_sb.append("</div>");
                        html_output_sb.append("<div class=\"rl_filter_list\">");
                            filter_count = 0;
                            for (String f : language_filters) {
                                if (filter_count > 0) {
                                    html_output_sb.append("&nbsp;&#8226;&nbsp;");
                                }
                                //html_output_sb.append("<span class=\"rl_filter\" id=\"f_");
                                html_output_sb.append("<span class=\"rl_filter filterable_object fname_f_");
                                html_output_sb.append(f.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", ""));
                                html_output_sb.append("\">");
                                    html_output_sb.append(LanguageHelpers.getTranslation(f, display_language, null));
                                html_output_sb.append("</span>");
                                filter_count++;
                            }
                        html_output_sb.append("</div>");
                        html_output_sb.append("<div class=\"rl_filter_header\">");
                            html_output_sb.append(LanguageHelpers.getTranslation("Filter by Medium and Format", display_language, null));
                        html_output_sb.append("</div>");
                        html_output_sb.append("<div class=\"rl_filter_list\">");
                            filter_count = 0;
                            Iterator it = mediumformat_filters.entrySet().iterator();
                            while (it.hasNext())
                            {
                                Map.Entry<String, String> f = (Map.Entry<String, String>)it.next();
                                
                            //for (String f : mediumformat_filters) {
                                if (filter_count > 0) {
                                    html_output_sb.append("&nbsp;&#8226;&nbsp;");
                                }
                                html_output_sb.append("<span class=\"rl_filter filterable_object fname_f_");
                                //html_output_sb.append("<span class=\"rl_filter\" id=\"f_");
                                html_output_sb.append(f.getKey().toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", ""));
                                html_output_sb.append("\">");
                                    html_output_sb.append(LanguageHelpers.getTranslation(f.getKey(), display_language, null));
                                html_output_sb.append("</span>");
                                filter_count++;
                            }
                        html_output_sb.append("</div>");
                        html_output_sb.append("<div class=\"rl_filter_header\">");
                            html_output_sb.append(LanguageHelpers.getTranslation("Other Filters", display_language, null));
                        html_output_sb.append("</div>");
                        html_output_sb.append("<div class=\"rl_filter_list\">");
                            filter_count = 0;
                            for (String f : other_filters) {
                                if (filter_count > 0) {
                                    html_output_sb.append("&nbsp;&#8226;&nbsp;");
                                }
                                html_output_sb.append("<span class=\"rl_filter filterable_object fname_f_");
                                //html_output_sb.append("<span class=\"rl_filter\" id=\"f_");
                                html_output_sb.append(f.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", ""));
                                html_output_sb.append("\">");
                                    html_output_sb.append(LanguageHelpers.getTranslation(f, display_language, null));
                                html_output_sb.append("</span>");
                                filter_count++;
                            }
                        html_output_sb.append("</div>");
                    html_output_sb.append("</div>\n");
                html_output_sb.append("</div>\n");
            
            
                         /////////////////////////////////////////////
//~*~*~*~*~*~*~*~*~*~*~  // RENDER ACTIVE FILTER DASHBOARD (BOTTOM) //  ~*~*~*~*~*~*~*~*~*~*~
                         /////////////////////////////////////////////
            
            html_output_sb.append("<div id=\"rl_dashboard_wrapper\">");
            
                html_output_sb.append("<div id=\"rl_dashboard_visible\">");
                    html_output_sb.append("<div class=\"rl_dashboard_filter_header\">ACTIVE FILTERS</div>");
                    html_output_sb.append("<div id=\"rl_dashboard_quick_filter_list\"></div>");
                html_output_sb.append("</div>");
            html_output_sb.append("</div>"); // end of dashboard wrapper
        html_output_sb.append("</div>");

    html_output_sb.append("</div>\n");//end of wrapper

        %>    
        <div id="cached_resource_list_wrapper">
            <%=html_output_sb.toString()%>
        </div>

        <%
            
                         //////////////////////
//~*~*~*~*~*~*~*~*~*~*~  // WRITE CACHE FILE //  ~*~*~*~*~*~*~*~*~*~*~
                         //////////////////////
        
        
            if (!curricName.equals("")) {
                CSContext cs_context = null;
                ByteArrayInputStream file_to_write_is = null;

                try {
                    cs_context = CSContext.getContext();

                    file_to_write_is = new ByteArrayInputStream(html_output_sb.toString().getBytes());

                    cs_context.isSuperUser(true);
                    cs_context.createFile(cache_base_path, cacheFileName, file_to_write_is, true);

                    System.out.println("Wrote file " + cacheFileName + " to the folder " + cache_base_path);

                } catch (Exception e) {
                    System.out.println("Error writing cache file " + cacheFileName + " to " + cache_base_path + ": " + e.getMessage());
                    cs_context.rollback();
                } finally {
                    if (cs_context != null) {
                        cs_context.commit();
                        file_to_write_is.close();
                    }
                }
            }
        %>

        <!--<script type="text/javascript">
            update_filter_totals();
        </script>-->
    </body>

</html>
