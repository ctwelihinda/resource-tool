$(document).ready(function() {

    $('body').on('click', '.get_old_info', function() {
        var this_type;
        if ($(this).attr("id").indexOf("pub") > -1)
        { this_type = "pub"; }
        else
        { this_type = "dist"; }
        
        var select_form;
        if (this_type == "pub")
        { select_form = "#select_pub_form"; }
        else
        { select_form = "#select_dist_form"; }
        
        var this_id = $(select_form).find("select").serialize().replace(/tag\_id\=/, "");
        
        
        $.post(
             "https://bblearndev.merlin.mb.ca/webapps/moe-resource_tool_final-BBLEARN/GetTagInfo",
             $(select_form).find("select, input").serialize().replace(/'/g, "''"),
             function (data) {
                 if (data.indexOf("error") != -1)
                 {
                     alert("ERROR: tag not created!");
                 }
                 else
                 {
                     var new_data = data.replace(/\<br\/\>/g, "\n");
                     
                     var this_title = $("option[value='" + this_id + "']").text();
                     var text_title = "";
                        if (this_type == "pub")
                        { text_title = "Publisher: "; }
                        else
                        { text_title = "Distributor: "; }
                        text_title = text_title + this_title;
                     
                     var textarea = $("textarea#update_info_textarea");
                     var input = $("input#tag_id_input")
                     $(textarea).html(new_data);
                     $(input).attr("value", this_id);
                     $('#textarea_title').html(text_title);
                 }
             }
         );
    });

    $('body').on('click', '#update_info', function() {
         $.post(
             "https://bblearndev.merlin.mb.ca/webapps/moe-resource_tool_final-BBLEARN/AddInfoToTag",
             $('#edit_info_form').find("textarea, input").serialize().replace(/'/g, "''"),
             function (data) {
                 $('.status_message').html('<p>' + data + '</p>');
             }
         );
    });
    
    
});

