/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.helpers.views;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.List; //Added Nicole
import stans.resourcerecord.helpers.LanguageHelpers;
import stans.resourcerecord.helpers.constants.ConstantLists;
import stans.resourcerecord.model.CurriculumSearchResult;
import stans.resourcerecord.model.Resource;

/**
 *
 * @author peter
 */
public class ResourceViews {
    public static StringBuilder generateSearchResultHTML( CurriculumSearchResult result, String lang ) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append( "<div class=\"search_result " );
        sb.append( result.HTML_class );
        sb.append( "\">\n" );

///////////////////////////////////////////////////////////////////
// TOP SECTION
            sb.append( "<div class=\"result_top_section\">" );

                sb.append( "<div class=\"result_image_frame\">" );
                    sb.append( "<img class=\"result_image\" src=\"" );
                    sb.append( result.image_path );
                    sb.append( "\"/>" );
                sb.append( "</div>\n" );

                sb.append( "<div class=\"result_id_field\">" );
                sb.append( result.r_number );
                sb.append( "</div>\n" );

                sb.append( "<div class=\"result_title_block\">" );
                    String outofprint_class = "";
                    if (result.is_outofprint)
                    {
                        sb.append( "<div class=\"result_outofprint_field\">" );
                            sb.append( LanguageHelpers.getTranslation("This resource is out of print", lang, null) );
                        sb.append( "</div>" );
                        outofprint_class = " out_of_print";
                    }
                    sb.append( "<div class=\"result_title_field open_popup" );
                    sb.append( outofprint_class );
                    sb.append( "\" id=\"" );
                    sb.append( result.db_id );
                    sb.append( "\">" );
                        sb.append( result.title );
                    sb.append( "</div>\n" );

                    if (!result.link_string.equals("")) {
                        sb.append( "<div class=\"result_link_field\">" );
                        sb.append( LanguageHelpers.getTranslation(result.link_string, lang, null) );
                        sb.append( "</div>\n" );
                    } else {
                    }
                sb.append( "</div>\n" );


                sb.append( "<div class=\"result_annotation_field" );
                sb.append( outofprint_class );
                sb.append( "\">" );
                    sb.append( result.this_annotation );
                sb.append( "</div>\n" );

                //if (result.is_core)
                //{ sb.append( "<div class=\"result_list_classification_field\">CORE</div>" ); }

                sb.append( "<div style=\"clear:both\"></div>\n" );
            sb.append( "</div>" ); // end of top_section

///////////////////////////////////////////////////////////////////
// MIDDLE SECTION
                if (!result.children.isEmpty())
                {
                    sb.append( "<div class=\"result_middle_section\">" );
                        sb.append( "<div class=\"result_children_section\">" );
                            sb.append( LanguageHelpers.getTranslation("This resource contains the following", lang, null) );
                            sb.append( ":" );
                            sb.append( "<div class=\"result_children\">" );
                            for( Resource child : result.children ) {
                                if( child.getFinalRecommendation()==1 ) {
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
                    if (lang.equals("fr")) {
                        gradesList = ConstantLists.allGradesListFrench;
                    }
                    else {
                        gradesList = ConstantLists.allGradesList;
                    }
                    //Nicole End
                        //for (String this_grade : ConstantLists.allGradesList)
                        for (String this_grade : gradesList)
                        {
                            sb.append( "<span class=\"result_grade " );
                                if ((result.grade_hash.get(this_grade) != null) && (result.grade_hash.get(this_grade) == true))
                                { 
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


                    if (!result.subjects.isEmpty()) {
                        boolean first_subj = true;
                        for ( String other_subject : result.subjects )
                        {
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
                        if (!first_subj)
                        { sb.append( "</div>\n" ); }
                    }


                    if (!result.medium_formats.isEmpty()) {
                        sb.append( "<div class=\"result_section result_medium_section\">" );
                        sb.append( "<span class=\"result_section_header\">" );
                        sb.append( LanguageHelpers.getTranslation("Media and Formats", lang, null) );
                        sb.append( ": </span>" );
                        for (String medium : result.medium_formats)
                        {
                            sb.append( "<span class=\"result_field result_medium_field filterable_object fname_f_" );

                            String short_mf = ConstantLists.mediumformatShortlist.get(medium );
                            String medium_class = "";
                            if (short_mf != null)
                            { medium_class = short_mf; }
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

                    if (!result.languages.isEmpty()) {
                        sb.append( "<div class=\"result_section result_language_section\">" );
                        sb.append( "<span class=\"result_section_header\">" );
                        sb.append( LanguageHelpers.getTranslation("Language", lang, null) );
                        sb.append( ": </span>" );
                        for (String lng : result.languages)
                        {
                            sb.append( "<span class=\"result_field result_language_field filterable_object fname_f_" );
                            sb.append( lng.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", "") );
                            sb.append( "\">" );
                                sb.append( LanguageHelpers.getTranslation(lng, lang, null) );
                            sb.append( "</span>\n" );
                        }
                        sb.append( "</div>\n" );
                    }

                    if (!result.topics.isEmpty()) {
                        sb.append( "<div class=\"result_section result_topic_section\">" );
                        sb.append( "<span class=\"result_section_header\">Topics: </span>" );
                        for (String topic : result.topics)
                        {
                            sb.append( "<span class=\"result_field result_topic_field filterable_object fname_f_" );
                            sb.append( topic.toLowerCase().replaceAll(" ", "_").replaceAll("[^a-z0-9]", "") );
                            sb.append( "\">" );
                                sb.append( LanguageHelpers.getTranslation(topic, lang, null) );
                            sb.append( "</span>\n" );
                        }
                        sb.append( "</div>\n" );
                    }

                    if (!result.price.equals(""))
                    {
                        if (result.price.matches("^[0-9.].*$"))
                        { result.price = "$" + result.price; }
                        if (result.price.equals("$0.00"))
                        { result.price = "Free"; }

                        sb.append( "<div class=\"result_section result_price_section\">" );
                        if (result.price.contains("ree"))
                        { sb.append( "<span class=\"result_field result_price_field filterable_object fname_f_free\">" ); }
                        else
                        { sb.append( "<span class=\"result_price_field\">" ); }
                        sb.append( LanguageHelpers.getTranslation(result.price, lang, null) );
                        sb.append( "</span>\n" );
                        sb.append( "</div>\n" );
                    } //else {
                        //sb.append( "<div class=\"result_nolink_field\">No price listed</div>\n" );
                    //}
                sb.append( "</div>\n" ); // end of bottom_left_section

///////////////////////////////////////////////////////////////////
// ICON SECTION

                sb.append( "<div class=\"result_icon_section\">" );
                    Iterator it = result.icon_hash.entrySet().iterator();
                    while (it.hasNext())
                    {
                        Map.Entry<String, Integer> this_set = (Map.Entry<String,Integer>)it.next();
                        String filter_name = this_set.getKey();
                        sb.append( "<div class=\"result_icon_wrapper filterable_object fname_f_" );
                            sb.append( filter_name );
                            sb.append( "\">" );
                            //sb.append( "<figure class=\"icon_tint\">" );
                            sb.append( "<figure>" );
                            sb.append( "<img class=\"result_icon\" alt=\"" );
                            sb.append( filter_name );
                            sb.append( "\" src=\"https://www.edonline.sk.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/images/" );
                            sb.append( ConstantLists.iconImageMap.get(this_set.getKey()) );
                            sb.append( "\" />" );
                            sb.append( "</figure>\n" );
                        sb.append( "</div>\n" );
                    }
                sb.append( "</div>\n" );

                sb.append( "<div style=\"clear:both\"></div>\n" );

            sb.append( "</div>\n" ); // end of bottom_section
            sb.append( "</div>\n" ); // end of search_result
        return sb;
    }
}
