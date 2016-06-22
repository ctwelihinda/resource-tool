/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.model;
import stans.EasyUser;

import com.xythos.common.api.XythosException;
import java.io.IOException;
import stans.resourcerecord.dao.ResourceTextLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List; //Added Nicole
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import stans.resourcerecord.helpers.LanguageHelpers;
import stans.resourcerecord.helpers.constants.ConstantLists;


public class CurriculumSearchResult implements Comparable<CurriculumSearchResult> {
    
    public boolean show_this_resource;
    public boolean is_core;
    public boolean is_support;
    public boolean is_outofprint;
    
    private String default_image_path = "http://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum Website/New Resource Search/images/bison.png ";
    private String rover_URL;
    private String watch_text;
    
    private String default_title;
    private String title_separator;
    
    public Integer db_id;
    public String r_number;
    public String title;
    public String link_string;
    public String image_path;
    public String price;
    public String HTML_class;
    public String STFLink;
	
    private String display_language;
    
    public String this_annotation = "";
    
    //public ArrayList<Integer> child_ids;
    public ArrayList<Resource> children;
    public ArrayList<String> medium_formats;
    public ArrayList<String> languages;
    public ArrayList<String> topics;
    public ArrayList<String> subjects;
    public HashMap<String, Boolean> subjects_hash;
    public HashMap<String, Boolean> grade_hash = new HashMap<String, Boolean>();
    public HashMap<String, Boolean> icon_hash = new HashMap<String, Boolean>();
    
    public CurriculumSearchResult() {
        this(new Resource(), new HashMap<String, Boolean>(), new HashMap<String, String>(), "en");
    }
    public CurriculumSearchResult(Resource r) {
        this(r, new HashMap<String, Boolean>(), new HashMap<String, String>(), "en");
    }
    
    public CurriculumSearchResult(Resource r, HashMap<String, Boolean> allowed_tagtypes, HashMap<String, String> mediumformat_shortlist, String lang) {
        if( lang == null ) {
        display_language = "en"; }
        else
        { display_language = lang; }
        
        if( display_language.equals("fr"))  { rover_URL = "http://revel.edonline.sk.ca"; }
        else                                { rover_URL = "http://rover.edonline.sk.ca"; }
        try {
            watch_text = LanguageHelpers.getTranslation("WATCH", display_language, null);
        } catch (IOException ex) {
            watch_text = "WATCH";
            Logger.getLogger(CurriculumSearchResult.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        show_this_resource = true;
        default_title = "*no title available*";
        title_separator = " / ";

        subjects = new ArrayList<String>();
        subjects_hash = new HashMap<String, Boolean>();
        medium_formats = new ArrayList<String>();
        languages = new ArrayList<String>();
        topics = new ArrayList<String>();
        //child_ids = r.getChildIDs();
        children = r.getChildren();
		Collections.sort( children );
    
        db_id = r.getDBID();
        r_number = r.getRNumber();
        
        title = r.getTitleAndSubtitleAndEdition();
        link_string = "";
        image_path = "";
        price = "";
        is_core = false;
        is_support = false;
        HTML_class = "";
        is_outofprint = r.isOutOfPrint();
		STFLink = "";
        
        ArrayList<Tag> curr_res_tags = r.getTags();
        for (Tag t : curr_res_tags) {
            if( (allowed_tagtypes.get(t.getType()) != null) && (allowed_tagtypes.get(t.getType()))) // if this is a tag that we care about
            {
                String t_value = t.getValue();
                if( (t.getType().equals("Medium")) || (t.getType().equals("Format"))) 
                {
                    String short_mf = mediumformat_shortlist.get(t.getValue());
                    if( short_mf != null ) {
                    t_value = short_mf; }
                }
                HTML_class = HTML_class + "f_" + t_value.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", "") + " ";
            }

            if( (t.getType().equals("Medium")) || (t.getType().equals("Format"))) {
                medium_formats.add(t.getValue());
                if( (t.getValue().equals("ROVER Video")) || (t.getValue().equals("ROVER Series")) ) {
                
                    icon_hash.put("free", true);
                    icon_hash.put("rovervideo", true); 
                }
            }
            
            if( t.getType().equals("Language") ) {
				languages.add(t.getValue());
            }

            if( t.getType().equals("Subject") ) {
                subjects_hash.put(t.getValue(), true);
            }

            if( (t.getType().equals("Level")) || (t.getType().equals("Grade")) ) {
				if( t.getValue().equals("Prekindergarten") ) {
					grade_hash.put("PreK", true);
				}
                if(
					( t.getValue().equals("Kindergarten") && display_language.equals("fr")) ||
					( t.getValue().equals("Maternelle") )
				) {
					grade_hash.put("M", true);
				} else if( t.getValue().equals("Kindergarten") ) {
					grade_hash.put("K", true);
				}
                if( t.getValue().equals("1") )		{ grade_hash.put("1", true); }
                if( t.getValue().equals("2") )		{ grade_hash.put("2", true); }
                if( t.getValue().equals("3") )		{ grade_hash.put("3", true); }
                if( t.getValue().equals("4") )		{ grade_hash.put("4", true); }
                if( t.getValue().equals("5") )		{ grade_hash.put("5", true); }
                if( t.getValue().equals("6") )		{ grade_hash.put("6", true); }
                if( t.getValue().equals("7") )		{ grade_hash.put("7", true); }
                if( t.getValue().equals("8") )		{ grade_hash.put("8", true); }
                if( t.getValue().equals("9") )		{ grade_hash.put("9", true); }
                if( t.getValue().contains("10") )	{ grade_hash.put("10", true); }
                if( t.getValue().contains("20") )	{ grade_hash.put("20", true); }
                if( t.getValue().contains("30") )	{ grade_hash.put("30", true); }
            }

			if( t.getType().equals( "STF Link" ) ) {
				STFLink = t.getValue();
				if( !STFLink.startsWith("http") ) {
					STFLink = "http://" + STFLink;
				}
			}
            if( t.getType().equals( "Teachers Guide Link" ) ) {
                if( !link_string.equals("")) {
                    link_string = link_string + " - ";
                }
                link_string = link_string + "<a target=\"_blank\" href=\"" + t.getValue() + "\">" + "TEACHER'S GUIDE</a>";
            }
            if( t.getType().equals("ROVER ID") ) {
                if( !link_string.equals("")) {
                    link_string = link_string + " - ";
                }
                link_string = link_string + "<a target=\"_blank\" href=\"" + rover_URL + "/goToVideo.htm?entry=" + t.getValue() + "\">" + watch_text + "</a>";
                icon_hash.put("free", true);
                icon_hash.put("rovervideo", true);
            }
            if( t.getType().equals("ROVER Series ID")) {
                if( !link_string.equals("")) {
                    link_string = link_string + " - ";
                }
                try {
                    link_string = link_string + "<a target=\"_blank\" href=\"" + rover_URL + "/en/rover/resources?q%5Bseries_id_eq%5D=" + t.getValue() + "\">" + LanguageHelpers.getTranslation("VIEW ALL", display_language, null) + "</a>";
                } catch (IOException ex) {
                    link_string = link_string + "<a target=\"_blank\" href=\"" + rover_URL + "/en/rover/resources?q%5Bseries_id_eq%5D=" + t.getValue() + "\">VIEW ALL</a>";
                    Logger.getLogger(CurriculumSearchResult.class.getName()).log(Level.SEVERE, null, ex);
                }
                icon_hash.put("free", true);
                icon_hash.put("rovervideo", true);
            }

            if( t.getType().equals("Cover Image Path")) {
                image_path = t.getValue();

                if( !image_path.contains("http") ) {
                                    image_path = "http://" + image_path;
                }
            }

            if( t.getType().equals("Resource List Classification")) {
                if( t.getValue().equals("Core") ) {
                                    is_core = true;
                }
                if( t.getValue().equals("Support Material") ) {
                                    is_support = true;
                }
            }
            
            if( (t.getType().equals("Content Classification")) || (t.getType().equals("Topic"))) {
                if( t.getValue().equals("Canadian Content") ) {
                icon_hash.put("canadiancontent", true); }
                if( t.getValue().equals("FNMI Content") ) {
                icon_hash.put("fnmicontent", true); }
                if( t.getValue().equals("Saskatchewan Content") ) {
                icon_hash.put("saskatchewancontent", true); }
                if( t.getType().equals("Topic") ) {
                topics.add(t.getValue()); }
            }

            if( t.getType().equals("External URL")) {
                String link_text = "";
                String link_URL = "";
                String link_value = t.getValue();

                if( link_value.contains("::") ) {
                                    link_text = link_value.split("::")[0];
                    link_URL = link_value.split("::")[1];
                }
                else
                {
                    link_URL = link_value;
                    if( link_URL.contains("www.edonline")) {
                        link_text = "DOWNLOAD";
                    } else if( link_URL.contains("connect.edonline")) {
                        link_text = "WATCH";
                    } else {
                        link_text = "VISIT";
                    }
                }

                if( !link_string.equals("")) {
                    link_string = link_string + " - ";
                }
                try {
                    link_string = link_string + "<a target=\"_blank\" href=\"" + link_URL + "\">" + LanguageHelpers.getTranslation(link_text, display_language, null) + "</a>";
                } catch (IOException ex) {
                    link_string = link_string + "<a target=\"_blank\" href=\"" + link_URL + "\">" + link_text + "</a>";
                    Logger.getLogger(CurriculumSearchResult.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if( t.getType().equals("Price") ) {
                            price = t.getValue();
                if( price.contains("ree") ) {
                icon_hash.put("free", true); }
            }
        } // end of tag loop
        
        // the newer ROVER additions use the R number instead of N or V number, so often the record doesn't include this in a ROVER ID tag
        // therefore, a link to the ROVER video will not have been added to link_string in the all-tags loop above
        if( 
                (medium_formats.contains("ROVER Video")) &&
                (!link_string.contains(watch_text))
         ) {
                    if( !link_string.equals("")) {
                link_string = link_string + " - ";
            }
            link_string = link_string + "<a target=\"_blank\" href=\"" + rover_URL + "/goToVideo.htm?entry=" + r_number + "\">" + watch_text + "</a>";
        }
        
        if( (image_path.equals("")) || (image_path.equals("http://rover.edonline.sk.ca/screenshots/original/missing.png")) ) {
        image_path = default_image_path; }

        if( icon_hash.containsKey("free") ) {
        HTML_class = HTML_class + "f_free "; }
        
        if( icon_hash.containsKey("rovervideo") ) {
                    HTML_class = HTML_class + "f_video ";
            HTML_class = HTML_class + "f_rovervideo ";
            if( price.equals(""))               { price = "Free"; }
            else if( !price.contains("ree"))    { price = price + " (free to the SK education sector)"; }
        }

        if( is_core ) {
                    HTML_class = HTML_class + "f_core ";
        }
        else
        {
            HTML_class = HTML_class + "f_addl ";
        }
        
        Iterator subj_it = subjects_hash.entrySet().iterator();
        while (subj_it.hasNext() ) {
                    Map.Entry<String, Boolean> other_subject = (Map.Entry<String, Boolean>)subj_it.next();
            subjects.add(other_subject.getKey());
        }
        
        for (ResourceText rt : ResourceTextLoader.loadByResourceID(db_id)) {
            if( rt.getType().equals("Annotation")) {
                if( this_annotation.equals("")) {
                    this_annotation = rt.getText();
                } else {
                    this_annotation = this_annotation + " - " + rt.getText();
                }
            }
        }
        if( this_annotation.length() > 500) {
            this_annotation = this_annotation.substring(0, 500);
            this_annotation = this_annotation + "...";
        }
        if( this_annotation.equals("")) {
            this_annotation = "<i>No annotation found</i>";
        }

        
    }

    @Override
    public int compareTo(CurriculumSearchResult other ) {
            return this.get_sortable_name().compareTo(other.get_sortable_name());
    }
    

    public String get_sortable_name() {

        String sortable_name = title.toLowerCase();

        //drop special characters that will be problomatic
        sortable_name = sortable_name.replaceAll("[,-:;. ]", "");

        //drop typical title starting words
        sortable_name = sortable_name.replaceFirst("^[Tt]he ", "");
        sortable_name = sortable_name.replaceFirst("^[Tt]hat ", "");
        sortable_name = sortable_name.replaceFirst("^[Aa] ", "");
        sortable_name = sortable_name.replaceFirst("^[Aa]n ", "");

        return sortable_name;
    }
    
    public void setDefaultTitle(String dt ) {
    default_title = dt; }
    public void setTitleSeparator(String ts ) {
    title_separator = ts; }
    
    
    public StringBuilder generateHTML( String lang ) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append( "<div class=\"search_result " );
        sb.append( this.HTML_class );
        sb.append( "\">\n" );
        //System.out.println("trying to render html");
///////////////////////////////////////////////////////////////////
// TOP SECTION
            sb.append( "<div class=\"result_top_section\">" );

                sb.append( "<div class=\"result_image_frame\">" );
                    sb.append( "<img class=\"result_image\" src=\"" );
                    sb.append( this.image_path );
                    sb.append( "\"/>" );
                sb.append( "</div>\n" );

                sb.append( "<div class=\"result_id_field\">" );
             /*   if(true){
                	sb.append("<a href=\"https://bblearndev.merlin.mb.ca/webapps/moe-resource_tool_final-BBLEARN/EditResource?resource_number=");
                	sb.append( this.r_number );
                	sb.append("\">");
                	sb.append( this.r_number );
                	sb.append("</a>");
                } else{ */
                	sb.append( this.r_number );
              //  }
                sb.append( "</div>\n" );

                sb.append( "<div class=\"result_title_block\">" );
                    String outofprint_class = "";
                    if( this.is_outofprint ) {
                                            sb.append( "<div class=\"result_outofprint_field\">" );
                            sb.append( LanguageHelpers.getTranslation("This resource is out of print", lang, null) );
                        sb.append( "</div>" );
                        outofprint_class = " out_of_print";
                    }
                    sb.append( "<div class=\"result_title_field open_popup" );
                    sb.append( outofprint_class );
                    sb.append( "\" id=\"" );
                    sb.append( this.db_id );
                    sb.append( "\">" );
                        sb.append( this.title );
                    sb.append( "</div>\n" );

                    if( !this.link_string.equals("")) {
                        sb.append( "<div class=\"result_link_field\">" );
                        sb.append( LanguageHelpers.getTranslation(this.link_string, lang, null) );
                        sb.append( "</div>\n" );
                    } else {
                    }
                sb.append( "</div>\n" );


                sb.append( "<div class=\"result_annotation_field" );
                sb.append( outofprint_class );
                sb.append( "\">" );
                    sb.append( this.this_annotation );
                sb.append( "</div>\n" );

                //if( this.is_core)
                //{ sb.append( "<div class=\"result_list_classification_field\">CORE</div>" ); }

                sb.append( "<div style=\"clear:both\"></div>\n" );
            sb.append( "</div>" ); // end of top_section

///////////////////////////////////////////////////////////////////
// MIDDLE SECTION
                if( !this.children.isEmpty() ) {
					sb.append( "<div class=\"result_middle_section\">" );
                        sb.append( "<div class=\"result_children_section\">" );
                            //sb.append( LanguageHelpers.getTranslation("This resource contains the following", lang, null) );
                            sb.append( LanguageHelpers.getTranslation("Related resources", lang, null) );
                            sb.append( ":" );
                            sb.append( "<div class=\"result_children\">" );
                            for( Resource child : this.children ) {
                                if( child.getFinalRecommendation() == 1) {
                                    sb.append( "<div class=\"open_popup\" id=\"" );
                                    sb.append( child.getDBID() );
                                    sb.append( "\">" );
                                    sb.append( child.getTitleAndSubtitle() );
                                    sb.append( "</div>\n" );
                                }
                            }
                            sb.append( "</div>\n" );
                        sb.append( "</div>\n" );
                    sb.append( "</div>\n" );
                }

///////////////////////////////////////////////////////////////////
// BOTTOM SECTION
                sb.append( "<div class=\"result_bottom_section\">" );

                sb.append( "<div class=\"result_bottom_left_section\">" );

                    sb.append( "<div class=\"result_section result_grade_section\">" );
                    sb.append( "<div class=\"result_grade_list\">" );
                    //Nicole Start
                    List<String> gradesList;
                    if( display_language.equals("fr")) {
                        gradesList = ConstantLists.allGradesListFrench;
                    }
                    else {
                        gradesList = ConstantLists.allGradesList;
                    }
                    //Nicole End
                        //for (String this_grade : ConstantLists.allGradesList)
                        for (String this_grade : gradesList ) {
								sb.append( "<span class=\"result_grade " );
                                if( (this.grade_hash.get(this_grade) != null) && (this.grade_hash.get(this_grade) == true) ) {
                                
                                    sb.append( "has_grade filterable_object fname_f_" ); 
                                    sb.append( this_grade.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", "") ); 
                                }
                                else
                                { sb.append( "no_grade" ); }
                            sb.append( "\">" );
                            sb.append( this_grade );
                            sb.append( "</span>" );
                        }
                    sb.append( "</div>" );
                    sb.append( "</div>\n" );


                    if( !this.subjects.isEmpty()) {
                        boolean first_subj = true;
                        for ( String other_subject : this.subjects  ) {
                                                    if( first_subj ) {
                                sb.append( "<div class=\"result_section result_subject_section\">" );
                                sb.append( "<span class=\"result_section_header\">" );
                                sb.append( LanguageHelpers.getTranslation("Other Subjects", lang, null) );
                                sb.append( ": </span>" );
                                first_subj = false;
                            }
                            sb.append( "<span class=\"result_field result_subject_field filterable_object fname_f_" );
                            sb.append( other_subject.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", "") );
                            sb.append( "\">" );
                            sb.append( other_subject );
                            sb.append( "</span>\n" );
                        }
                        if( !first_subj ) {
                        sb.append( "</div>\n" ); }
                    }


                    if( !this.medium_formats.isEmpty()) {
                        sb.append( "<div class=\"result_section result_medium_section\">" );
                        sb.append( "<span class=\"result_section_header\">" );
                        sb.append( LanguageHelpers.getTranslation("Media and Formats", lang, null) );
                        sb.append( ": </span>" );
                        for (String medium : this.medium_formats ) {
                                                    sb.append( "<span class=\"result_field result_medium_field filterable_object fname_f_" );

                            String short_mf = ConstantLists.mediumformatShortlist.get(medium );
                            String medium_class = "";
                            if( short_mf != null ) {
                            medium_class = short_mf; }
                            else
                            { medium_class = medium; }

                            sb.append( medium_class.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", "") );
                            sb.append( "\">" );
                            sb.append( medium );
                            sb.append( "</span>\n" );
                        }
                        sb.append( "</div>\n" );
                    } //else {
    //                    sb.append( "<div class=\"result_nolink_field\">No formats listed</div>\n" );
      //              }

                    if( !this.languages.isEmpty()) {
                        sb.append( "<div class=\"result_section result_language_section\">" );
                        sb.append( "<span class=\"result_section_header\">" );
                        sb.append( LanguageHelpers.getTranslation("Language", lang, null) );
                        sb.append( ": </span>" );
                        for (String lng : this.languages ) {
                                                    sb.append( "<span class=\"result_field result_language_field filterable_object fname_f_" );
                            sb.append( lng.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", "") );
                            sb.append( "\">" );
                                sb.append( LanguageHelpers.getTranslation(lng, lang, null) );
                            sb.append( "</span>\n" );
                        }
                        sb.append( "</div>\n" );
                    }

                    if( !this.topics.isEmpty()) {
                        sb.append( "<div class=\"result_section result_topic_section\">" );
                        sb.append( "<span class=\"result_section_header\">Topics: </span>" );
                        for (String topic : this.topics ) {
                                                    sb.append( "<span class=\"result_field result_topic_field filterable_object fname_f_" );
                            sb.append( topic.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", "") );
                            sb.append( "\">" );
                                sb.append( LanguageHelpers.getTranslation(topic, lang, null) );
                            sb.append( "</span>\n" );
                        }
                        sb.append( "</div>\n" );
                    }

                    if( !this.price.equals("") ) {
                                            if( this.price.matches("^[0-9.].*$") ) {
                        this.price = "$" + this.price; }
                        if( this.price.equals("$0.00") ) {
                        this.price = "Free"; }

                        sb.append( "<div class=\"result_section result_price_section\">" );
                        if( this.price.contains("ree") ) {
                        sb.append( "<span class=\"result_field result_price_field filterable_object fname_f_free\">" ); }
                        else
                        { sb.append( "<span class=\"result_price_field\">" ); }
                        sb.append( LanguageHelpers.getTranslation(this.price, lang, null) );
                        sb.append( "</span>\n" );
                        sb.append( "</div>\n" );
                    } //else {
                        //sb.append( "<div class=\"result_nolink_field\">No price listed</div>\n" );
                    //}
                sb.append( "</div>\n" ); // end of bottom_left_section

///////////////////////////////////////////////////////////////////
// ICON SECTION

                sb.append( "<div class=\"result_icon_section\">" );
                    Iterator it = this.icon_hash.entrySet().iterator();
                    while (it.hasNext() ) {
						Map.Entry<String, Integer> this_set = (Map.Entry<String,Integer>)it.next();
                        String filter_name = this_set.getKey();
                        sb.append( "<div class=\"result_icon_wrapper filterable_object fname_f_" );
                            sb.append( filter_name );
                            sb.append( "\">" );
                            //sb.append( "<figure class=\"icon_tint\">" );
                            sb.append( "<figure>" );
                            sb.append( "<img class=\"result_icon\" alt=\"" );
                            sb.append( filter_name );
                            sb.append( "\" src=\"https://bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/images/" );
                            sb.append( ConstantLists.iconImageMap.get(this_set.getKey()) );
                            sb.append( "\" />" );
                            sb.append( "</figure>\n" );
                        sb.append( "</div>\n" );
                    }
					if( !STFLink.equals("") ) {
						sb.append( "<div class=\"borrow_icon_row\">" );
							sb.append( "<div class=\"result_icon_wrapper borrow_icon\">" );
								sb.append( "<figure>" );
									sb.append( "<a target=\"_blank\" href=\"" );
									sb.append( STFLink );
									sb.append( "\">" );
										if( display_language.equals( "en" ) ) {
											sb.append( "<img class=\"borrow_img\" alt=\"borrow_from_stf\" src=\"https://www.edonline.sk.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/images/borrow_button_img.jpg\" />" );
										} else {
											sb.append( "<img class=\"borrow_img\" alt=\"borrow_from_stf\" src=\"https://www.edonline.sk.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/images/borrow_button_img_fr.jpg\" />" );
										}
									sb.append( "</a>" );
								sb.append( "</figure>\n" );
							sb.append( "</div>\n" );
						sb.append( "</div>\n" );
					}
                sb.append( "</div>\n" );

                sb.append( "<div style=\"clear:both\"></div>\n" );

            sb.append( "</div>\n" ); // end of bottom_section
            sb.append( "</div>\n" ); // end of search_result
        return sb;
    }
}
