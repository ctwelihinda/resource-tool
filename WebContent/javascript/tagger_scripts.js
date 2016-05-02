function add_tag (new_or_existing, form_element, result_element) {
    
    var servlet_name = "AddTagToResource";
    if (new_or_existing == "new")
    { servlet_name = "CreateAndAddNewTagToResource"; }
    if (new_or_existing == "by value")
    { servlet_name = "AddTagToResourceByValue"; }
    var servlet_data = form_element.find("hidden, select, input").serialize().replace(/'/g, "''");

    $.getJSON(
        servlet_name,
        servlet_data,
        function (data) {
            $.each(data.tags, function(i, tag) {
                var curr_user = $(".current_user").text();
                var curr_date = $(".current_date").text();
                var html_to_add = "<tr id=\"" + tag.id + "\">";
                    html_to_add += "<td class=\"tag_table_type\">";
                        html_to_add += tag.type;
                    html_to_add += "</td>";
                    html_to_add += "<td class=\"tag_table_value\">";
                        html_to_add += tag.value;
                    html_to_add += "</td>";
                    html_to_add += "<td class=\"tag_table_delete\">";
                        html_to_add += "<span class=\"tag_remove_box\" id=\"";
                            html_to_add += tag.id;
                        html_to_add += "\" data-joinid=\"";
                            html_to_add += tag.join_id;
                        html_to_add += "\">DELETE</span>";
                    html_to_add += "</td>";
                    html_to_add += "<td class=\"tag_table_user\">";
                        html_to_add += curr_user;
                    html_to_add += "</td>";
                    html_to_add += "<td class=\"tag_table_date\">";
                        html_to_add += curr_date;
                    html_to_add += "</td>";
                html_to_add += "</tr>";
                result_element.append(html_to_add);
            });
        }
    );
}


$(document).ready(function() {

/////////////////////////////////////////
// subjects and grades
    $('#subjectgrade_tags_container').on('click', '.add_grade_tag_to_group', function() {
        var this_id = this.id.replace("add_grade_tag_to_group_", "");
        add_tag ("existing", $("#add_grade_tag_id_" + this_id), $("#subjectgrade_tags_list_" + this_id + " tbody"));
    });
    $('#subjectgrade_tags_container').on('click', '.add_subject_tag_to_group', function() {
        var this_id = this.id.replace("add_subject_tag_to_group_", "");
        add_tag ("existing", $("#add_subject_tag_id_" + this_id), $("#subjectgrade_tags_list_" + this_id + " tbody"));
    });
    $('#subjectgrade_tags_container').on('click', '.add_other_curric_tag_to_group', function() {
        var this_id = this.id.replace("add_other_curric_tag_to_group_", "");
        add_tag ("existing", $("#add_other_curric_tag_id_" + this_id), $("#subjectgrade_tags_list_" + this_id + " tbody"));
    });
    $('#subjectgrade_tags_container').on('click', '.add_outcome_tag_to_group', function() {
        var this_id = this.id.replace("add_outcome_tag_to_group_id_", "");
        add_tag ("by value", $("#add_outcome_tag_id_" + this_id), $("#subjectgrade_tags_list_" + this_id + " tbody"));
    });
    $('#subjectgrade_tags_container').on('click', '.select_subject_for_outcomes_list_submit', function() {
         var this_id = this.id.replace("select_subject_for_outcomes_list_submit_", "");
         $.post(
             "GetOutcomesList",
             $("#select_subject_for_outcomes_list_id_" + this_id).find("select").serialize().replace(/'/g, "''"),
             function (data) {
                 var matching = $("#add_outcome_tag_id_" + this_id + " select.outcome_list");
                 $(matching).html(data);
             }
         );
    });

/////////////////////////////////////////
// groups
    $('.tagger_section').on('click', '#add_new_group_button', function() {
         $.post(
             "CreateAndAddNewGroupToResource",
             $("#add_new_group_id").find("input").serialize().replace(/'/g, "''")
         ).done(function(data){
             window.location.reload(true);
         });
    });

/////////////////////////////////////////
// parent/child
    $('#add_parent_submit').on('click', function() {
         $.post(
             "AddParentResource",
             $("#add_parent_id").find("input").serialize(),
             function (data) {
                 window.location.reload(true);
             }
         );
    });

/////////////////////////////////////////
// other resources
    $('#add_other_resource_submit').on('click', function() {
         $.post(
             "AddResourceRelationship",
             $("#add_other_resource_id").find("select, input").serialize().replace(/'/g, "''"),
             function (data) {
                 window.location.reload(true);
             }
         );
    });

/////////////////////////////////////////
// titles
    $('#add_title_tag_submit').on('click', function() {
        add_tag ("new", $("#add_title_tag_id"), $("#title_tags tbody"));
    });
    $('#title_tags_container').on('click', "#title_edit_box", function() {
        var new_html = "<form name=\"title_form\" id=\"title_form_id\">";
        new_html += "<input type=\"text\" name=\"new_title\" value=\"" + $('#title_value').text() + "\" />";
        new_html += "<button type=\"button\" id=\"title_form_submit\">SAVE NEW TITLE</button>";
        new_html += "</form>";
        $('#title_row').html(new_html);
    });
    $('#title_tags_container').on('click', "#subtitle_edit_box", function() {
        var new_html = "<form name=\"subtitle_form\" id=\"subtitle_form_id\">";
        new_html += "<input type=\"text\" name=\"new_subtitle\" value=\"" + $('#subtitle_value').text() + "\" />";
        new_html += "<button type=\"button\" id=\"subtitle_form_submit\">SAVE NEW SUBTITLE</button>";
        new_html += "</form>";
        $('#subtitle_row').html(new_html);
    });
    $('#title_tags_container').on('click', "#edition_edit_box", function() {
        var new_html = "<form name=\"edition_form\" id=\"edition_form_id\">";
        new_html += "<input type=\"text\" name=\"new_edition\" value=\"" + $('#edition_value').text() + "\" />";
        new_html += "<button type=\"button\" id=\"edition_form_submit\">SAVE NEW EDITION</button>";
        new_html += "</form>";
        $('#edition_row').html(new_html);
    });
    $('#title_tags_container').on('click', "#title_remove_box", function() {
        var this_res_id = $("#ltb_db_id").text();
        $.post(
            "AddQuickData",
            {
                type: "title",
                value: "",
                resource_id: this_res_id
            },
            function (data) {
                if (data == "error")
                {
                    alert("ERROR: title not removed!");
                }
                else
                {
                    $('#title_value').text("");
                    $("#ctb_title").text("");
                }
            }
        );
    });
    $('#title_tags_container').on('click', "#subtitle_remove_box", function() {
        var this_res_id = $("#ltb_db_id").text();
        $.post(
            "AddQuickData",
            {
                type: "subtitle",
                value: "",
                resource_id: this_res_id
            },
            function (data) {
                if (data == "error")
                {
                    alert("ERROR: subtitle not removed!");
                }
                else
                {
                    $('#subtitle_value').text("");
                    $("#ctb_subtitle").text("");
                }
            }
        );
    });
    $('#title_tags_container').on('click', "#edition_remove_box", function() {
        var this_res_id = $("#ltb_db_id").text();
        $.post(
            "AddQuickData",
            {
                type: "edition",
                value: "",
                resource_id: this_res_id
            },
            function (data) {
                if (data == "error")
                {
                    alert("ERROR: edition not removed!");
                }
                else
                {
                    $('#edition_value').text("");
                    $("#ctb_edition").text("");
                }
            }
        );
    });
    $('#title_tags_container').on('click', '#title_form_submit', function(e) {
        e.preventDefault();
        var new_title = $("#title_form_id").find("input").val().replace(/'/g, "''");
        var this_res_id = $("#ltb_db_id").text();
        $.post(
            "AddQuickData",
            {
                type: "title",
                value: new_title,
                resource_id: this_res_id
            },
            function (data) {
                if (data == "error")
                {
                    alert("ERROR: title not added!");
                }
                else
                {
                    var new_html = "<span class=\"big_tag_name\">Title</span>";
                    new_html += "<span class=\"big_tag_value\" id=\"title_value\">" + new_title.replace(/''/g, "'") + "</span>";
                    new_html += "<span class=\"text_edit_box\" id=\"title_edit_box\">EDIT</span>";
                    new_html += "<span class=\"text_remove_box\" id=\"title_remove_box\">DELETE</span>";
                    $("#title_row").html(new_html);
                    $("#ctb_title").text(new_title.replace(/''/g, "'"));
                }
            }
        );
    });
    $('#title_tags_container').on('click', '#subtitle_form_submit', function(e) {
        e.preventDefault();
        var new_subtitle = $("#subtitle_form_id").find("input").val().replace(/'/g, "''");
        var this_res_id = $("#ltb_db_id").text();
        $.post(
            "AddQuickData",
            {
                type: "subtitle",
                value: new_subtitle,
                resource_id: this_res_id
            },
            function (data) {
                if (data == "error")
                {
                    alert("ERROR: subtitle not added!");
                }
                else
                {
                    var new_html = "<span class=\"big_tag_name\">Subtitle</span>";
                    new_html += "<span class=\"big_tag_value\" id=\"subtitle_value\">" + new_subtitle.replace(/''/g, "'") + "</span>";
                    new_html += "<span class=\"text_edit_box\" id=\"subtitle_edit_box\">EDIT</span>";
                    new_html += "<span class=\"text_remove_box\" id=\"subtitle_remove_box\">DELETE</span>";
                    $("#subtitle_row").html(new_html);
                    $("#ctb_subtitle").text(new_subtitle.replace(/''/g, "'"));
                }
            }
        );
    });
    $('#title_tags_container').on('click', '#edition_form_submit', function(e) {
        e.preventDefault();
        var new_edition = $("#edition_form_id").find("input").val().replace(/'/g, "''");
        var this_res_id = $("#ltb_db_id").text();
        $.post(
            "AddQuickData",
            {
                type: "edition",
                value: new_edition,
                resource_id: this_res_id
            },
            function (data) {
                if (data == "error")
                {
                    alert("ERROR: edition not added!");
                }
                else
                {
                    var new_html = "<span class=\"big_tag_name\">Edition</span>";
                    new_html += "<span class=\"big_tag_value\" id=\"edition_value\">" + new_edition.replace(/''/g, "'") + "</span>";
                    new_html += "<span class=\"text_edit_box\" id=\"edition_edit_box\">EDIT</span>";
                    new_html += "<span class=\"text_remove_box\" id=\"edition_remove_box\">DELETE</span>";
                    $("#edition_row").html(new_html);
                    $("#ctb_edition").text(new_edition.replace(/''/g, "'"));
                }
            }
        );
    });

/////////////////////////////////////////
// people
    $('#add_people_tag_submit').on('click', function() {
        add_tag ("new", $("#add_people_tag_id"), $("#people_tags tbody"));
    });
    
///////////////////////////////////////////////////
    //Tracking info
    $('#add_tracking_info_tag_submit').on('click', function() {
    	alert("Hello");
    	//alert($("#tracking_info_tags tbody"));
    	add_tag("new", $("#add_tracking_info_tag_id"), $("#tracking_info_tags tbody"));

    });

/////////////////////////////////////////
// stf borrowing
    $('#add_stf_borrow_tag_submit').on('click', function() {
        add_tag ("new", $("#add_stf_borrow_tag_id"), $("#stf_borrow_tags tbody"));
    });

/////////////////////////////////////////
// publisher/distributor
    $('#add_publisher_tag_submit').on('click', function() {
        add_tag ("new", $("#add_publisher_tag_id"), $("#pubdist_tags tbody"));
    });
    $('#add_distributor_tag_submit').on('click', function() {
        add_tag ("new", $("#add_distributor_tag_id"), $("#pubdist_tags tbody"));
    });
    $('#pubdist_tags tbody').on('mouseover', 'tr', function() {
         var $tag_id = $(this).attr('id');
         $.post(
             "GetTagInfo",
             { tag_id: $tag_id },
             function (data) {
                $('.right_info_box').html(data);                            
             }
         );
    });

/////////////////////////////////////////
// date tags
    $('#add_date_tag_submit').on('click', function() {
        var input_date = $("#date_input_field").val();
        var all_ok = true;
        var date_array = input_date.split(/-/);
        var year = "";
        var month = "";
        var day = "";
        if ((date_array.length > 3) || (date_array.length < 1))
        {
            all_ok = false;
        }
        if (date_array.length == 1)
        {
            year = input_date;
        }
        else if (date_array.length == 2)
        {
            year = date_array[0];
            month = date_array[1];
        }
        else if (date_array.length == 3)
        {
            year = date_array[0];
            month = date_array[1];
            day = date_array[2];
        }
        
        var regex_year = /^\d{4}$/;
        var regex_month = /^\d{2}$/;
        var regex_day = /^\d{2}$/;
        if (!regex_year.exec(year))
        {
            all_ok = false;
        }
        if (month != "")
        {
            if ((!regex_month.exec(month)) || (month > 12) || (month < 1))
            {
                all_ok = false;
            }
        }
        if (day != "")
        {
            if ((!regex_day.exec(day)) || (day > 31) || (day < 1))
            {
                all_ok = false;
            }
        }
        if (all_ok)
        {
            add_tag ("new", $("#add_date_tag_id"), $("#date_tags tbody"));
        }
        else
        {
            alert("Please enter a valid date. Follow the formatting rules in the instruction text.");
        }
    });




/////////////////////////////////////////
// streaming license tags
    $('#add_license_tag_submit').on('click', function() {
        add_tag ("existing", $("#add_license_tag_id"), $("#license_tags tbody"));
    });



/////////////////////////////////////////
// other tags
    $('#add_other_tag_submit').on('click', function() {
        add_tag ("new", $("#add_other_tag_id"), $("#other_tags tbody"));
        var tag_type = $("#other_type_select").val();
        if (tag_type == "Cover Image Path")
        {
            var new_quick_image = $("#add_other_tag_id").find("input").val().replace(/'/g, "''");
            var this_res_id = $("#add_other_tag_id").find("input:hidden").val();
            $.post(
                "AddQuickData",
                {
                    type: "image",
                    value: new_quick_image,
                    resource_id: this_res_id
                },
                function (data) {
                    $("div.big_tag_row:contains('Quick Image Path: ')").text("Quick Image Path: " + new_quick_image.replace(/''/g, "'"));
                }
            );
        }
    });

/////////////////////////////////////////
// out of print tags
    $('#add_outofprint_submit').on('click', function() {
        $.post(
            "SetOutOfPrint",
            $("#add_outofprint_tag_id").find("input").serialize().replace(/'/g, "''"),
            function (data) {
               window.location.reload(true);
            }
        );
    });

/////////////////////////////////////////
// hide if child
    $('#hide_if_child_submit').on('click', function() {
        $.post(
            "HideIfChild",
            $("#hide_if_child_id").find("input").serialize().replace(/'/g, "''"),
            function (data) {
               window.location.reload(true);
            }
        );
    });

/////////////////////////////////////////
// media formats
    $('#add_medium_tag_submit').on('click', function() {
        add_tag ("existing", $("#add_medium_tag_id"), $("#mediaformat_tags tbody"));
    });
    $('#create_medium_tag_submit').on('click', function() {
        add_tag ("new", $("#create_medium_tag_id"), $("#mediaformat_tags tbody"));
    });               
    $('#add_format_tag_submit').on('click', function() {
        add_tag ("existing", $("#add_format_tag_id"), $("#mediaformat_tags tbody"));
    });
    $('#add_language_tag_submit').on('click', function() {
        add_tag ("existing", $("#add_language_tag_id"), $("#mediaformat_tags tbody"));
    });
    $('#add_genre_tag_submit').on('click', function() {
        add_tag ("existing", $("#add_genre_tag_id"), $("#mediaformat_tags tbody"));
    });
    $('#add_format_tag_w_text_submit').on('click', function() {
        add_tag ("new", $("#add_format_tag_w_text_id"), $("#mediaformat_tags tbody"));
    });



/////////////////////////////////////////
// content
    $('#add_content_tag_submit').on('click', function() {
        add_tag ("existing", $("#add_content_tag_id"), $("#content_tags tbody"));
    });


/////////////////////////////////////////
// long text
    $('#add_text_submit').on('click', function() {
        $.post(
            "AddLongTextToResource",
            $("#add_text_id").find("select, textarea, input").serialize().replace(/'/g, "''"),
            function (data) {
                window.location.reload(true);
            }
        );
        var text_type = $("#text_type_select").val();
        if (text_type == "Annotation")
        {
            var new_quick_desc = $("#add_text_id").find("textarea").val().replace(/'/g, "''");
            var this_res_id = $("#add_text_id").find("input:hidden").val();
            $.post(
                "AddQuickData",
                {
                    type: "description",
                    value: new_quick_desc,
                    resource_id: this_res_id
                },
                function (data) {
                    $("div.big_tag_row:contains('Quick Description: ')").text("Quick Description: " + new_quick_desc.replace(/''/g, "'"));
                }
            );
        }
    });
    $('.longtext_wrapper').on('click', '.text_remove_box', function() {
         var $text_id = $(this).attr('id');
         var parent_element = $(this).parent();
         $.post(
             "DeleteTextFromResource",
             { text_id: $text_id }
         ).done(function() {
             $(parent_element).remove();
         });
    });
    $('.longtext_wrapper').on('click', '.text_edit_box', function() {
         var text_id = $(this).attr('id');
         var parent_element = $(this).parent();
         var textblock_element = parent_element.children('.longtext_block');
         var the_text = textblock_element.text();
         parent_element.empty();
         
         var edit_block_code = "<div class=\"form_wrapper\"><span class=\"longtext_header\">Edit Annotation: </span><form name=\"longtext_edit\"><textarea name=\"new_text\" cols=\"100\" rows=\"15\" wrap=\"soft\"></textarea><input type=\"hidden\" name=\"text_id\" value=\"" + text_id + "\"/></form><button class=\"edit_text_submit\" id=\"" + text_id + "\">Update Text</button>";

         parent_element.html(edit_block_code);
         parent_element.find('textarea').text(the_text);
    });
    $('.longtext_wrapper').on('click', '.edit_text_submit', function() {
        $.post(
            "ReplaceLongText",
            $(this).parent().find("input, textarea").serialize().replace(/'/g, "''"),
            function (data) {
                window.location.reload(true);
            }
        );
        //alert($(this).parent().find(".longtext_header").text());
        //alert($(this).parent().find(".longtext_header").text().indexOf("Edit Annotation"));
        if ($(this).parent().find(".longtext_header").text().indexOf("Edit Annotation") > 0)
        {
            var new_quick_desc = $(this).parent().find("textarea").val().replace(/'/g, "''");
            var this_res_id = $(this).parent().find("input:hidden").val();
            $.post(
                "AddQuickData",
                {
                    type: "description",
                    value: new_quick_desc,
                    resource_id: this_res_id
                },
                function (data) {
                    $("div.big_tag_row:contains('Quick Description: ')").text("Quick Description: " + new_quick_desc.replace(/''/g, "'"));
                }
            );
        }
    });
    
    


/////////////////////////////////////////
// recommendations
    $('#add_recommendation_submit').on('click', function() {
        $.post(
            "CreateAndAddNewRecommendation",
            $("#add_recommendation_id").find("textarea, input").serialize().replace(/'/g, "''"),
            function (data) {
                window.location.reload(true);
            }
        );
    });
    $('.longtext_wrapper').on('click', '.rec_remove_box', function() {
         var $rec_id = $(this).attr('id');
         var parent_element = $(this).parent();
         $.post(
             "DeleteRecFromResource",
             { rec_id: $rec_id }
         ).done(function() {
             $(parent_element).remove();
         });
    });
    $('#add_final_recommendation_submit').on('click', function() {
        $.post(
            "AddFinalRecommendation",
            $("#add_final_recommendation_id").find("input").serialize().replace(/'/g, "''"),
            function (data) {
                window.location.reload(true);
            }
        );
    });
    $('#remove_final_recommendation_submit').on('click', function() {
        $.post(
            "AddFinalRecommendation",
            $("#remove_final_recommendation_id").find("input").serialize().replace(/'/g, "''"),
            function (data) {
                window.location.reload(true);
            }
        );
    });
    
/////////////////////////////////////////
// flags
    $('#add_flag_submit').on('click', function() {
        $.post(
            "AddFlagToResource",
            $("#add_flag_id").find("select, textarea, input").serialize().replace(/'/g, "''"),
            function (data) {
                window.location.reload(true);
            }
        );
    });                   
    $('#update_flag_comments_submit').on('click', function() {
        $.post(
            "UpdateFlagComments",
            $("#update_flag_comments_id").find("textarea, input").serialize().replace(/'/g, "''"),
            function (data) {
                window.location.reload(true);
            }
        );
    });                   
    $('.has_flag_div').on('click', '.flag_remove_box', function() {
         var $flag_id = $(this).attr('id');
         $.post(
             "DeleteFlag",
             { flag_id: $flag_id }
         ).done(function() {
             window.location.reload(true);
         });
    });
    
});

