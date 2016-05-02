        
function filter(obj)
{
        if(obj){
                if (jQuery(obj).css("font-weight") == "bold" || jQuery(obj).css("font-weight") > 400){
                        jQuery(obj).css("font-weight", "normal");
                        jQuery(obj).css("font-style", "normal");
                }else{
                        jQuery(obj).css("font-weight", "bold");
                        jQuery(obj).css("font-style", "italic");
                }
        }
        var x = jQuery("span.rl_filter");  //get all the filter objects
        var z = new Array();  //to be an array of active filters
        var j=0;
        var parse_dot;

        // gs defined in view4
        if (jQuery("#1.1.1.1").length){
                z[j++] = jQuery("#"+gs+'.'+gs+'.'+gs+'.'+o).html().toLowerCase();
        }

        jQuery("span.rl_filter").each(function(i){
                parse_dot = jQuery(this).attr("id").replace(/\./,"_");
                if (jQuery(this).css("font-weight") == "bold" || jQuery(this).css("font-weight") > 400){
                        z[j++]= parse_dot.toLowerCase();
                }
        });

        x = jQuery(".search_result");  //find all the search items
        var count=0,notShown=0;
        x.each(function(){
                for(var j=0;j<z.length;j++){
                        if (jQuery(this).hasClass(z[j])){
                                count++;
                        }

                }

                if(count==j){
                        jQuery(this).show();
                }else {
                        jQuery(this).hide();
                        notShown++;
                }
                count=0;
        });


        jQuery(".search_result").each(function(){
                for(var j=0;j<z.length;j++) {
                        if (jQuery(this).hasClass(z[j])){
                                count++;
                        }
                }
        });



        if(x.length!=0) {
                if(notShown==x.length) //if all the search items are hidden
                        jQuery('#searchMessage').show();
                else //if at least one search item is shown
                        jQuery('#searchMessage').hide();
        }


        update_filter_totals();

}            

function update_filter_totals()
{
    jQuery(document).ready(function() {
        jQuery(".rl_filter").each(function(){
             parse_dot = jQuery(this).attr("id").replace(/\./,"_");
                jQuery(this).html(jQuery(this).html().replace(/\(\d*\)/,"") + " (" + jQuery("div." + parse_dot.toLowerCase() + ".search_result").length + ")");
        });
    });
}