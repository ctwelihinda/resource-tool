jQuery(document).ready(function() {
    var loading_html = "<div style=\"text-align: center;\"><b>LOADING, PLEASE WAIT...</b></div>";
    
    jQuery('body').on('click', '.open_popup', function() {
        var this_id = this.id;
        var language = jQuery('#display_language').text();
        if (language === null)
        { language = "en"; }
        
        jQuery('#resource_popup_content').html(loading_html);
        
        jQuery('#resource_popup').height(jQuery(window).height() * 4/5);
        jQuery('#resource_popup_content').height(jQuery('#resource_popup').height() - 80);

        jQuery('#print_popup a').attr("href", "https://bblearndev.merlin.mb.ca/webapps/moe-resource_tool_final-BBLEARN/ViewRecords?resource_ids=" + this_id + "&language=" + language);
        jQuery('#print_popup').show();

        jQuery('#resource_popup').show();
        jQuery('#resource_popup').css('z-index', 10001); /* because the megamenu has a z-index of 10000 */
        var this_url = location.protocol + "//" + location.hostname + "/GenerateResourcePopup";
        jQuery.ajax({
            //url: this_url,
            url: "https://bblearndev.merlin.mb.ca/webapps/moe-resource_tool_final-BBLEARN/GenerateResourcePopup",
            type: 'POST',
            data: {
                resource_id: this_id,
                language: language
            },
            success: function (data) {
                    jQuery('#resource_popup_content').html(data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                    jQuery('#resource_popup_content').html(jqXHR.responseText);
            }
        });
    });
    
    jQuery('#resource_popup').on('click', '#close_popup', function() {
       jQuery('#resource_popup').hide(); 
    });

});
