package stans.resourcerecord.servlet.searchAndList;

import stans.resourcerecord.dao.ResourceTextLoader;
import stans.resourcerecord.helpers.ValidationHelpers;
import stans.resourcerecord.model.Recommendation;
import stans.resourcerecord.model.Resource;
import stans.resourcerecord.model.ResourceText;
import stans.resourcerecord.model.Tag;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.resourcerecord.helpers.LanguageHelpers;

/**
 *
 * @author peter
 */
public class GenerateResourcePopup extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
    // initialize output
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
    // initialize resource vars
        String r_id_param = request.getParameter("resource_id");
        String display_language = request.getParameter("language");
        if ((display_language == null) || (!display_language.equals("fr")))
        { display_language = "en"; }
        
        String rover_URL;
        if (display_language.equals("fr"))  { rover_URL = "http://revel.edonline.sk.ca"; }
        else                                { rover_URL = "http://rover.edonline.sk.ca"; }
        
        Integer resource_id;
        if (ValidationHelpers.isPositiveInteger(r_id_param))
        { resource_id = Integer.parseInt(r_id_param); }
        else
        { resource_id = 0; }
        
    // let's do it
        if (resource_id == 0)
        { out.println("Error: Resource ID is invalid"); }
        else
        {
            Resource the_resource = new Resource(resource_id);
            
            try {
                StringBuilder output_sb = new StringBuilder();
                StringBuilder subj_grade_sb = new StringBuilder();
                ArrayList<Tag> this_res_tags = the_resource.getRootTags();
                
				ArrayList<String> contentTagNames = new ArrayList<String>();
				
                String publisher = "";
                String distributor = "";
                String author = "";
                String editor = "";
                String illustrator = "";
                String translator = "";
                String producer = "";
                String narrator = "";
                String composer = "";
                String musician = "";
                String creator = "";
                String director = "";
                String price = "";
                String page_count = "";
                String isbn = "";
                String format = "";
                String copyright = "";
                String expiry = "";
                String year_recommended = "";
                String rover_id = "";
                String medium = "";
                String running_time = "";
                String cover_image_path = "";
                String link_string = "";
                String teachers_guide_link = "";
                
                
                for (Tag t : this_res_tags)
                {
                    // SUBJECT / GRADES
                    if (t.getType().equals("Tag Group"))
                    {
                        ArrayList<Tag> children = t.getChildren(resource_id);
                        
                        boolean[] has_grade = new boolean[14];
                        boolean a10 = false;
                        boolean b10 = false;
                        boolean a30 = false;
                        boolean b30 = false;

                        String subjects = "";
                        String keywords = "";

                        for (Tag this_child : children)
                        {
                            if (this_child.getType().equals("Subject"))
                            {
                                if (!subjects.equals(""))
                                { subjects = subjects + ", "; }
                                subjects = subjects + this_child.getValue();
                            }
                            if (
                                    (this_child.getType().equals("Unit")) ||
                                    (this_child.getType().equals("Module")) ||
                                    (this_child.getType().equals("Goal")) ||
                                    (this_child.getType().equals("Outcome")) ||
                                    (this_child.getType().equals("Domain")) ||
                                    (this_child.getType().equals("Program")) ||
                                    (this_child.getType().equals("Strand")) ||
                                    (this_child.getType().equals("Indicator")) ||
                                    (this_child.getType().equals("Language Cuing System")) ||
                                    (this_child.getType().equals("Suggested Use")) ||
                                    (this_child.getType().equals("Contexte"))
                            )
                            {
                                if (!keywords.equals(""))
                                { keywords = keywords + ", "; }
                                keywords = keywords + this_child.getValue();
                            }
                            if ((this_child.getType().equals("Grade")) || (this_child.getType().equals("Level")))
                            {
                                if (this_child.getValue().equals("Prekindergarten"))
                                { has_grade[0] = true; }
                                else if( ( this_child.getValue().equals("Kindergarten") )||( this_child.getValue().equals("Maternelle") ) )
                                { has_grade[1] = true; }
                                else
                                {
                                    String full_value = this_child.getValue();
                                    String just_number = full_value.replaceAll("[ABC]", "");
                                    if (just_number.equals("10"))
                                    {
                                        if (full_value.equals("A10"))   { a10 = true; }
                                        if (full_value.equals("B10"))   { b10 = true; }
                                        if (full_value.equals("10"))   { a10 = true; b10 = true; }
                                    }
                                    if (just_number.equals("20"))
                                    { just_number = "11"; }
                                    if (just_number.equals("30"))
                                    { 
                                        just_number = "12"; 
                                        if (full_value.equals("A30"))   { a30 = true; }
                                        if (full_value.equals("B30"))   { b30 = true; }
                                        if (full_value.equals("30"))   { a30 = true; b30 = true; }
                                    }

                                    if ((ValidationHelpers.isPositiveInteger(just_number)) && (Integer.parseInt(just_number) < 13))
                                    {
                                        has_grade[Integer.parseInt(just_number) + 1] = true; // add one to accomodate prek (prek = 0, k = 1, grade 1 = 2, etc.)
                                    }
                                }
                            }

                        }

                        subj_grade_sb.append("<div class=\"resource_table\">");
                            subj_grade_sb.append("<table>");
                            subj_grade_sb.append("<thead>");
                            //Nicole: French Insert
                            if( display_language.equals("fr") ) {
								subj_grade_sb.append("<tr> <th>M</th> <th>1</th> <th>2</th> <th>3</th> <th>4</th> <th>5</th> <th>6</th> <th>7</th> <th>8</th> <th>9</th> <th>10</th> <th>20</th> <th>30</th> </tr>");
							} else {
								subj_grade_sb.append("<tr><th>PK</th> <th>K</th> <th>1</th> <th>2</th> <th>3</th> <th>4</th> <th>5</th> <th>6</th> <th>7</th> <th>8</th> <th>9</th> <th>10</th> <th>11</th> <th>12</th> </tr>");
							}
                            subj_grade_sb.append("</thead>");
                            subj_grade_sb.append("<tbody>");
                            subj_grade_sb.append("<tr>");
                            int grade_counter = 0;
                            for( boolean b : has_grade )
                            {
                                if ((subjects.contains("English Language Arts")) && ((grade_counter == 11) || (grade_counter == 13)))
                                {
                                    if (b)
                                    { 
                                        subj_grade_sb.append("<td>");
                                            if (grade_counter == 11)
                                            {
                                                if (b10)    { subj_grade_sb.append("<div class=\"grade_highlight subgrade_r\">B</div>"); }
                                                else        { subj_grade_sb.append("<div class=\"grade_nohighlight subgrade_r\">B</div>"); }
                                                if (a10)    { subj_grade_sb.append("<div class=\"grade_highlight subgrade_l\">A</div>"); }
                                                else        { subj_grade_sb.append("<div class=\"grade_nohighlight subgrade_l\">A</div>"); }
                                            }
                                            if (grade_counter == 13)
                                            {
                                                if (b30)    { subj_grade_sb.append("<div class=\"grade_highlight subgrade_r\">B</div>"); }
                                                else        { subj_grade_sb.append("<div class=\"grade_nohighlight subgrade_r\">B</div>"); }
                                                if (a30)    { subj_grade_sb.append("<div class=\"grade_highlight subgrade_l\">A</div>"); }
                                                else        { subj_grade_sb.append("<div class=\"grade_nohighlight subgrade_l\">A</div>"); }
                                            }
                                        subj_grade_sb.append("</td>"); 
                                    }
                                    else
                                    { 
                                        subj_grade_sb.append("<td>");
                                            subj_grade_sb.append("<div class=\"grade_nohighlight subgrade_r\">B</div>");
                                            subj_grade_sb.append("<div class=\"grade_nohighlight subgrade_l\">A</div>");
                                        subj_grade_sb.append("</td>"); 
                                    }
                                }
                                else
                                {         
									if(
										( display_language.equals("en") ) ||
										( ( display_language.equals("fr") )&&( grade_counter!=0 ) ) // skip 0 for fr, since there's no prek in fr
									) {
										if( b )
										{ subj_grade_sb.append("<td class=\"grade_highlight\">&nbsp;</td>"); }
										else
										{ subj_grade_sb.append("<td class=\"grade_nohighlight\">&nbsp;</td>"); }
									}
                                }
                                
                                grade_counter++;
                            }
                            subj_grade_sb.append("</tr>\n");
                            subj_grade_sb.append("</tbody>\n");
                            subj_grade_sb.append("</table>\n");
                        
                            subj_grade_sb.append("<div class=\"resource_field\" id=\"subject_field\">\n");
                            subj_grade_sb.append("<span class=\"resource_field_title\" id=\"subject_field_title\">");
                                subj_grade_sb.append(LanguageHelpers.getTranslation("Subject(s)", display_language, null));
                            subj_grade_sb.append(": </span>\n");
                            subj_grade_sb.append(subjects);
                            subj_grade_sb.append("</div>\n");
                            if (!keywords.equals(""))
                            {
                                subj_grade_sb.append("<div class=\"resource_field\" id=\"keywords_field\">\n");
                                subj_grade_sb.append("<span class=\"resource_field_title\" id=\"keywords_field_title\">");
                                    subj_grade_sb.append(LanguageHelpers.getTranslation("Keyword(s)", display_language, null));
                                subj_grade_sb.append(": </span>\n");
                                subj_grade_sb.append(keywords);
                                subj_grade_sb.append("</div>\n");
                            }
                        subj_grade_sb.append("</div>\n");
                    } // end of GRADES / SUBJECTS
                    
                    
                    if (t.getType().equals("Cover Image Path"))
                    { cover_image_path = t.getValue(); }
                    
                    if (t.getType().equals("Publisher"))
                    { 
                        publisher = t.getValue().replace( "& ", "&amp; " );
                        String info = t.getInfo();
                        if ((info != null) && (!info.equals("")))
                        {
                            publisher = publisher + "<br/>";
                            publisher = publisher + info.replace("\n", "<br/>").replace("http://rover.edonline.sk.ca", "<a target=\"_blank\" href=\"http://rover.edonline.sk.ca\">http://rover.edonline.sk.ca</a>");
                        }
                    }
                    if (t.getType().equals("Distributor"))
                    { 
                        distributor = t.getValue().replace( "& ", "&amp; " );
                        String info = t.getInfo();
                        if ((info != null) && (!info.equals("")))
                        {
                            distributor = distributor + "<br/>";
                            distributor = distributor + info.replace("\n", "<br/>").replace("http://rover.edonline.sk.ca", "<a target=\"_blank\"  href=\"http://rover.edonline.sk.ca\">http://rover.edonline.sk.ca</a>");
                        }
                    }
                    
                    if (t.getType().equals("Author"))
                    {
                        if (!author.equals(""))
                        { author = author + " "; }
                        author = author + t.getValue();
                    }
                    if (t.getType().equals("Editor"))
                    {
                        if (!editor.equals(""))
                        { editor = editor + " "; }
                        editor = editor + t.getValue();
                    }
                    if (t.getType().equals("Illustrator"))
                    {
                        if (!illustrator.equals(""))
                        { illustrator = illustrator + " "; }
                        illustrator = illustrator + t.getValue();
                    }
                    if (t.getType().equals("Translator"))
                    {
                        if (!translator.equals(""))
                        { translator = translator + " "; }
                        translator = translator + t.getValue();
                    }
                    if (t.getType().equals("Producer"))
                    {
                        if (!producer.equals(""))
                        { producer = producer + " "; }
                        producer = producer + t.getValue();
                    }
                    if (t.getType().equals("Narrator"))
                    {
                        if (!narrator.equals(""))
                        { narrator = narrator + " "; }
                        narrator = narrator + t.getValue();
                    }
                    if (t.getType().equals("Composer"))
                    {
                        if (!composer.equals(""))
                        { composer = composer + " "; }
                        composer = composer + t.getValue();
                    }
                    if (t.getType().equals("Musician"))
                    {
                        if (!musician.equals(""))
                        { musician = musician + " "; }
                        musician = musician + t.getValue();
                    }
                    if (t.getType().equals("Creator"))
                    {
                        if (!creator.equals(""))
                        { creator = creator + " "; }
                        creator = creator + t.getValue();
                    }
                    if (t.getType().equals("Director"))
                    {	
                        if (!director.equals(""))
                        { director = director + " "; } 
                        director = director + t.getValue();
                    }

                    if (
						(t.getType().equals("Topic")) ||
						(t.getType().equals("Content Classification")) ||
						(t.getType().equals("Resource List Classification"))
                    ) {
                        contentTagNames.add( t.getValue().replace("Core", "Core Resource").replace("Additional", "Additional Resource") );
                    }

                    if (t.getType().equals("Price"))
                    { 
                        price = t.getValue(); 
                        if ((!price.contains("$")) && (!price.contains("ree")))
                        { price = "$" + price; }
                    }
                    if (t.getType().equals("ISBN"))
                    { isbn = t.getValue(); }
                    if (t.getType().equals("Number of Pages"))
                    { page_count = t.getValue(); }
                    if (t.getType().equals("Format"))
                    { 
                        if (!format.equals(""))
                        {
                            format = format + ", ";
                        }
                        format = format + t.getValue(); 
                    }
                    if (t.getType().equals("Copyright Date"))
                    { copyright = t.getValue(); }
                    if (t.getType().equals("Expiry Date"))
                    { 
                        expiry = t.getValue().replace(" 00:00:00.0", "");
                        if (expiry.equals("0000"))
                        { expiry = "<i>Rights in Perpetuity</i>"; }
                    }
                    if (t.getType().equals("Year Recommended"))
                    { 
                        if (!year_recommended.equals(""))
                        {
                            year_recommended = year_recommended + ", ";
                        }
                        year_recommended = year_recommended + t.getValue();
                    }
                    
                    if (t.getType().equals("ROVER ID")) {
                        if (!link_string.equals("")) {
                            link_string = link_string + " - ";
                        }
                        link_string = link_string + "<a target=\"_blank\" href=\"" + rover_URL + "/goToVideo.htm?entry=" + t.getValue() + "\">" + LanguageHelpers.getTranslation("WATCH", display_language, null) + "</a>";
                        rover_id = t.getValue();
                    }
                    if (t.getType().equals("ROVER Series ID")) {
                        if (!link_string.equals("")) {
                            link_string = link_string + " - ";
                        }
                        link_string = link_string + "<a target=\"_blank\" href=\"" + rover_URL + "/en/rover/resources?q%5Bseries_id_eq%5D=" + t.getValue() + "\">" + LanguageHelpers.getTranslation("VIEW ALL", display_language, null) + "</a>";
                        rover_id = t.getValue();
                    }

                    if (t.getType().equals("External URL")) {
                        String link_text = "";
                        String link_URL = "";
                        String link_value = t.getValue();
                        
                        if (link_value.contains("::"))
                        {
                            link_text = link_value.split("::")[0];
                            link_URL = link_value.split("::")[1];
                        }
                        else
                        {
                            link_URL = link_value;
                            if (link_URL.contains("www.edonline")) {
                                link_text = LanguageHelpers.getTranslation("DOWNLOAD", display_language, null);
                            } else if (link_URL.contains("connect.edonline")) {
                                link_text = LanguageHelpers.getTranslation("WATCH", display_language, null);
                            } else {
                                link_text = LanguageHelpers.getTranslation("VISIT", display_language, null);
                            }
                        }

                        if (!link_string.equals("")) {
                            link_string = link_string + " - ";
                        }
                        link_string = link_string + "<a target=\"_blank\" href=\"" + link_URL + "\">" + LanguageHelpers.getTranslation(link_text, display_language, null) + "</a>";
                    }
                    if (t.getType().equals("Teachers Guide Link"))
                    {
                        teachers_guide_link = "<a target=\"_blank\" href=\"" + t.getValue() + "\">" + "TEACHER'S GUIDE</a>";
                    }

                    if ((t.getType().equals("Length")) || (t.getType().equals("Running Time")))
                    { running_time = t.getValue(); }
                    if (t.getType().equals("Medium"))
                    { 
                        if (!medium.equals(""))
                        {
                            medium = medium + ", ";
                        }
                        medium = medium + t.getValue(); 
                    }
                }
                
                if ((medium.contains("ROVER Video")) && (!link_string.contains("rover")))
                {
                    if (!link_string.equals("")) {
                        link_string = link_string + " - ";
                    }
                    link_string = link_string + "<a target=\"_blank\" href=\"" + rover_URL + "/goToVideo.htm?entry=" + the_resource.getRNumber() + "\">" + LanguageHelpers.getTranslation("WATCH", display_language, null) + "</a>";
                }
///////////////////////////////
// END OF BIG ALL TAGS LOOP
                
                
                int rec_year = 0;
                for (Recommendation rc : the_resource.getRecommendations())
                {
                    if (rc.getRecommended() == 1)
                    {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date(rc.getCreatedAt().getTime()));
                        if (cal.get(Calendar.YEAR) > rec_year) // may be multiple recs; get most recent
                        { rec_year = cal.get(Calendar.YEAR); }
                    }
                }
                if (rec_year > 1000) // 1000 is the default year we used for MatEval imports where none was given
                {
                    if (!year_recommended.equals(""))
                    {
                        year_recommended = year_recommended + ", ";
                    }
                    year_recommended = Integer.toString(rec_year);
                }
                
                if (author.equals(""))
                { 
                    //author = "<span class=\"not_available\">No authors listed</span>"; 
                }
                if (publisher.equals(""))
                { publisher = "<span class=\"not_available\">Not Available</span>"; }
                if (distributor.equals(""))
                { distributor = "<span class=\"not_available\">Not Available</span>"; }

                if (price.equals("")) 
                {
                    if (medium.contains("ROVER"))
                    { price = "Free"; } // make sure ROVER videos are listed as "free"
                    else
                    { price = "<span class=\"not_available\">Not Available</span>"; }
                }
                if (format.equals(""))
                {
                    if (page_count.equals(""))
                    { format = "<span class=\"not_available\">Not Available</span>"; }
                    else
                    { format = page_count; }
                }
                else
                {
                    if (!page_count.equals(""))
                    { format = page_count + " (" + format + ")"; }
                }

                output_sb.append("<div class=\"resource_div\">\n");                
                output_sb.append("<div id=\"left_side_wrapper\">\n");
                output_sb.append("<div class=\"inner_resource_div\">\n");
                
                // COVER IMAGE
                    if (cover_image_path.equals(""))
                    { cover_image_path = "http://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum Website/New Resource Search/images/bison.png"; }
                    output_sb.append("<div class=\"resource_main_image\">\n");
                    output_sb.append("<img src=\"");
                    output_sb.append(cover_image_path);
                    output_sb.append("\" />\n");
                    output_sb.append("</div>\n");

                // OUT OF PRINT
                    if (the_resource.isOutOfPrint())
                    {
                        output_sb.append("<div id=\"outofprint_field\">**");
                        output_sb.append(LanguageHelpers.getTranslation("This resource is out of print", display_language, null));
                        output_sb.append("**</div>\n");
                    }
                    
                // TITLE
                    output_sb.append("<div class=\"resource_field\" id=\"title_field\">\n");
                    output_sb.append(the_resource.getTitleAndSubtitleAndEdition());
                    output_sb.append("</div>\n");
                    
                // LINKS
                    if (!link_string.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"link_field\">\n");
                        output_sb.append(link_string);
                        output_sb.append("</div>\n");
                    }
                    if (!teachers_guide_link.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"link_field\">\n");
                        output_sb.append(teachers_guide_link);
                        output_sb.append("</div>\n");
                    }
                    
                    
                // PEOPLE
                    if (!author.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"people_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"people_field_title\">");
                            output_sb.append(LanguageHelpers.getTranslation("Author", display_language, null));
                        output_sb.append("(s): </span><br/>\n");
                        output_sb.append(author);
                        output_sb.append("\n</div>\n");
                    }
                    if (!editor.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"people_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"people_field_title\">");
                            output_sb.append(LanguageHelpers.getTranslation("Editor", display_language, null));
                        output_sb.append("(s): </span><br/>\n");
                        output_sb.append(editor);
                        output_sb.append("\n</div>\n");
                    }
                    if (!illustrator.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"people_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"people_field_title\">Illustrator(s): </span><br/>\n");
                        output_sb.append(illustrator);
                        output_sb.append("\n</div>\n");
                    }
                    if (!translator.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"people_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"people_field_title\">Translator(s): </span><br/>\n");
                        output_sb.append(translator);
                        output_sb.append("\n</div>\n");
                    }
                    if (!producer.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"people_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"people_field_title\">Producer(s): </span><br/>\n");
                        output_sb.append(producer);
                        output_sb.append("\n</div>\n");
                    }
                    if (!narrator.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"people_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"people_field_title\">Narrator(s): </span><br/>\n");
                        output_sb.append(narrator);
                        output_sb.append("\n</div>\n");
                    }
                    if (!composer.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"people_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"people_field_title\">Composer(s): </span><br/>\n");
                        output_sb.append(composer);
                        output_sb.append("\n</div>\n");
                    }
                    if (!musician.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"people_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"people_field_title\">Musician(s): </span><br/>\n");
                        output_sb.append(musician);
                        output_sb.append("\n</div>\n");
                    }
                    if (!creator.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"people_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"people_field_title\">Creator(s): </span><br/>\n");
                        output_sb.append(creator);
                        output_sb.append("\n</div>\n");
                    }
                    if (!director.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"people_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"people_field_title\">Director(s): </span><br/>\n");
                        output_sb.append(director);
                        output_sb.append("\n</div>\n");
                    }

                // CONTENT
                    if( !contentTagNames.isEmpty() ) {
                        output_sb.append("<div class=\"resource_field\" id=\"content_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"content_field_title\">");
                            output_sb.append(LanguageHelpers.getTranslation("Content", display_language, null));
                        output_sb.append(": </span><br/>\n");
						boolean firstContent = true;
						for( String c : contentTagNames ) {
							if( firstContent ) {
								firstContent = false;
							} else {
								output_sb.append( ", " );
							}
							output_sb.append(LanguageHelpers.getTranslation( c, display_language, null) );
						}
                        output_sb.append("\n");
                        output_sb.append("</div>\n");
                    }
                    
                // ANNOTATION AND NOTES
                    output_sb.append("<div class=\"resource_field\" id=\"annotation_field\">\n");
                    String notes = "";
                    String annotation = "";
                    String cover_permission = "";
                    for (ResourceText rt : ResourceTextLoader.loadByResourceID(the_resource.getDBID()))
                    {
                        if (rt.getType().equals("Annotation"))
                        {
                            annotation = rt.getText().replace("\n", "<br/><br/>");
                            annotation = annotation + "<br/><br/>";
                        }
                        if (rt.getType().equals("Notes"))
                        {
                            notes = rt.getText().replace("\n", "<br/><br/>"); // want to have notes after the annotation, so save it here and append it after the loop
                            notes = notes + "<br/><br/>";
                        }                        
                        if (rt.getType().equals("Cover Permission"))
                        {
                            cover_permission = rt.getText().replace("\n", "<br/><br/>");
                        }                        
                    }
                    output_sb.append(annotation);
                    if (!notes.equals(""))
                    {
                        output_sb.append("<span class=\"resource_field_title\">");
						output_sb.append(LanguageHelpers.getTranslation("Note", display_language, null));
						output_sb.append("</span><br/>");
                        output_sb.append(notes);
                    }
                    output_sb.append("</div>\n");

                // COVER PERMISSION
                    if (!cover_permission.equals(""))
                    {
                        output_sb.append("<div class=\"cover_permission\">\n");
                            output_sb.append(cover_permission.replace("\n", "<br/><br/>"));
                        output_sb.append("</div>\n");
                    }

                // END OF LEFT SIDE
                output_sb.append("</div>\n");
                output_sb.append("</div>\n");
                    
                    
                
                output_sb.append("<div id=\"right_side_wrapper\">");
                output_sb.append("<div class=\"inner_resource_div\">");

                // SUBJECT, GRADES, KEYWORDS
                    output_sb.append(subj_grade_sb.toString());

                // PUBLISHER, PRODUCER, AND DISTRIBUTOR
                    if (publisher.equals(distributor))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"publisher_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"publisher_field_title\">");
                            output_sb.append(LanguageHelpers.getTranslation("Publisher", display_language, null));
                            output_sb.append(" / ");
                            output_sb.append(LanguageHelpers.getTranslation("Distributor", display_language, null));
                        output_sb.append(": </span><br/>\n");
                        output_sb.append(publisher);
                        output_sb.append("</div>\n");                    
                    }
                    else
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"publisher_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"publisher_field_title\">");
                            output_sb.append(LanguageHelpers.getTranslation("Publisher", display_language, null));
                        output_sb.append(": </span><br/>\n");
                        output_sb.append(publisher);
                        output_sb.append("</div>\n");

                        output_sb.append("<div class=\"resource_field\" id=\"distributor_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"distributor_field_title\">");
                            output_sb.append(LanguageHelpers.getTranslation("Distributor", display_language, null));
                        output_sb.append(": </span><br/>\n");
                        output_sb.append(distributor);
                        output_sb.append("</div>\n");
                    }
                    
                    
                // PRICE
                    output_sb.append("<div class=\"resource_field\" id=\"price_field\">\n");
                    output_sb.append("<span class=\"resource_field_title\" id=\"price_field_title\">");
                    output_sb.append(LanguageHelpers.getTranslation("Price", display_language, null));
                    output_sb.append(": </span>\n");
                    output_sb.append(price);
                    output_sb.append("</div>\n");

                // MEDIUM
                    output_sb.append("<div class=\"resource_field\" id=\"medium_field\">\n");
                    output_sb.append("<span class=\"resource_field_title\" id=\"medium_field_title\">");
                        output_sb.append(LanguageHelpers.getTranslation("Medium", display_language, null));
                    output_sb.append(": </span>\n");
                    output_sb.append(medium);
                    output_sb.append("</div>\n");

                    
                // PAGINATION
                    output_sb.append("<div class=\"resource_field\" id=\"pagination_field\">\n");
                    output_sb.append("<span class=\"resource_field_title\" id=\"pagination_field_title\">");
                        output_sb.append(LanguageHelpers.getTranslation("Pagination / Format", display_language, null));
                    output_sb.append(": </span>\n");
                    output_sb.append(format);
                    output_sb.append("</div>\n");

                    
                // LENGTH / RUNNING TIME
                    if (
                            (medium.contains("DVD")) ||
                            (medium.contains("CD")) ||
                            (medium.contains("Casette")) ||
                            (medium.contains("Reel")) ||
                            (medium.contains("Video")) ||
                            (medium.contains("Record")) ||
                            (format.contains("Webinar")) ||
                            (!running_time.equals(""))
                    )
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"length_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"length_field_title\">");
                            output_sb.append(LanguageHelpers.getTranslation("Length / Running Time", display_language, null));
                        output_sb.append(": </span>\n");
                        output_sb.append(running_time);
                        output_sb.append("</div>\n");
                    }

                // ISBN
                    if (
                            (medium.contains("Book")) ||
                            (format.contains("Book")) ||
                            (medium.contains("Casette"))
                    )
                    {
                        if (isbn.equals(""))
                        { isbn = "<span class=\"not_available\">Not Available</span>"; }
                        output_sb.append("<div class=\"resource_field\" id=\"isbn_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"isbn_field_title\">ISBN: </span>\n");
                        output_sb.append(isbn);
                        output_sb.append("</div>\n");
                    }
                    
                // COPYRIGHT DATE
                    if (copyright.equals(""))
                    { copyright = "<span class=\"not_available\">Not Available</span>"; }
                    output_sb.append("<div class=\"resource_field\" id=\"copyright_field\">\n");
                    output_sb.append("<span class=\"resource_field_title\" id=\"copyright_field_title\">");
                        output_sb.append(LanguageHelpers.getTranslation("Copyright", display_language, null));
                    output_sb.append(": </span>\n");
                    output_sb.append(copyright);
                    output_sb.append("</div>\n");

                // EXPIRY DATE
                    if ((!expiry.equals("")) || (medium.contains("ROVER")))
                    {
                        if (expiry.equals(""))
                        { expiry = "<span class=\"not_available\">Not Available</span>"; }
                        output_sb.append("<div class=\"resource_field\" id=\"expiry_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"expiry_field_title\">");
                            output_sb.append(LanguageHelpers.getTranslation("Expiry Date", display_language, null));
                        output_sb.append(": </span>\n");
                        output_sb.append(expiry);
                        output_sb.append("</div>\n");
                    }

                // YEAR RECOMMENDED
                    if (year_recommended.equals(""))
                    { year_recommended = "<span class=\"not_available\">Not Available</span>"; }
                    output_sb.append("<div class=\"resource_field\" id=\"year_recommended_field\">\n");
                    output_sb.append("<span class=\"resource_field_title\" id=\"year_recommended_field_title\">");
                        output_sb.append(LanguageHelpers.getTranslation("Year Recommended", display_language, null));
                    output_sb.append(": </span>\n");
                    output_sb.append(year_recommended);
                    output_sb.append("</div>\n");

                // ROVER ID
                    if (!rover_id.equals(""))
                    {
                        output_sb.append("<div class=\"resource_field\" id=\"rover_id_field\">\n");
                        output_sb.append("<span class=\"resource_field_title\" id=\"rover_id_field_title\">");
                            output_sb.append(LanguageHelpers.getTranslation("ROVER ID", display_language, null));
                        output_sb.append(": </span>\n");
                        output_sb.append(rover_id);
                        output_sb.append("</div>\n");
                    }

                // END OF RIGHT SIDE
                output_sb.append("</div>\n");
                output_sb.append("</div>\n");

                
    // PRINT IT!!!
                out.println(output_sb.toString());
            
            } finally {            
                out.close();
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
