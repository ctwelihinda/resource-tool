/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import stans.resourcerecord.dao.ResourceTextLoader;
import stans.resourcerecord.helpers.ValidationHelpers;

/**
 *
 * @author peter
 */
public class ResourceRecord {

    private String r_number;
    private String title;
    private String publisher;
    private String distributor;
    private String author;
    private String editor;
    private String illustrator;
    private String translator;
    private String producer;
    private String narrator;
    private String composer;
    private String musician;
    private String creator;
    private String director;
    private String content;
    private String price;
    private String page_count;
    private String isbn;
    private String format;
    private String copyright;
    private String expiry;
    private String year_recommended;
    private String rover_id;
    private String medium;
    private String running_time;
    private String cover_image_path;
    private String link_string;
    private String notes;
    private String annotation;
    private Boolean out_of_print;
    private ArrayList<ResourceRecordCurriculumGroup> curriculum_groups;
    
    
    
    
    
    
/////////////////////////////////////////////////////
//                                                 //
//      *~*~*~*~*~*~ CONSTRUCTOR  ~*~*~*~*~*~*     //
//                                                 //
/////////////////////////////////////////////////////
    
    public ResourceRecord (Resource r)
    {
        r_number = r.getRNumber();
        title = r.getTitle();
        publisher = "";
        distributor = "";
        author = "";
        editor = "";
        illustrator = "";
        translator = "";
        producer = "";
        narrator = "";
        composer = "";
        musician = "";
        creator = "";
        director = "";
        content = "";
        price = "";
        page_count = "";
        isbn = "";
        format = "";
        copyright = "";
        expiry = "";
        year_recommended = "";
        rover_id = "";
        medium = "";
        running_time = "";
        cover_image_path = "";
        link_string= "";
        notes = "";
        annotation = "";
        curriculum_groups = new ArrayList<ResourceRecordCurriculumGroup>();

        for (Tag t : r.getRootTags())
        {
            if (t.getType().equals("Tag Group"))
            { curriculum_groups.add(new ResourceRecordCurriculumGroup(t, r.getDBID())); }

            
            if (t.getType().equals("Cover Image Path"))
            { cover_image_path = t.getValue(); }

            
            if (t.getType().equals("Publisher"))
            { 
                publisher = t.getValue(); 
                String info = t.getInfo();
                if ((info != null) && (!info.equals("")))
                {
                    publisher = publisher + "<br/>";
                    publisher = publisher + info.replace("\n", "<br/>").replace("http://rover.edonline.sk.ca", "<a target=\"_blank\" href=\"http://rover.edonline.sk.ca\">http://rover.edonline.sk.ca</a>");
                }
            }
            
            
            if (t.getType().equals("Distributor"))
            { 
                distributor = t.getValue(); 
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
            )
            {
                if (!content.equals(""))
                { content = content + ", "; }
                content = content + t.getValue().replace("Core", "Core Resource").replace("Additional", "Additional Resource");
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
                link_string = link_string + "http://rover.edonline.sk.ca/goToVideo.htm?entry=" + t.getValue();
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
                        link_text = "DOWNLOAD";
                    } else if (link_URL.contains("connect.edonline")) {
                        link_text = "WATCH";
                    } else {
                        link_text = "VISIT";
                    }
                }

                if (!link_string.equals("")) {
                    link_string = link_string + " - ";
                }
                link_string = link_string + link_URL;
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

        //if (cover_image_path.equals(""))    { cover_image_path = "http://rover.edonline.sk.ca/assets/screenshot_missing.png"; }
        
        int rec_year = 0;
        for (Recommendation rc : r.getRecommendations())
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

        if (price.equals("")) 
        {
            if (medium.contains("ROVER"))
            { price = "Free"; } // make sure ROVER videos are listed as "free"
        }
        if (format.equals(""))
        {
            if (page_count.equals(""))
            { format = ""; }
            else
            { format = page_count; }
        }
        else
        {
            if (!page_count.equals(""))
            { format = page_count + " (" + format + ")"; }
        }

        for (ResourceText rt : ResourceTextLoader.loadByResourceID(r.getDBID()))
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
        }
        
        out_of_print = r.isOutOfPrint();
    }
    
    
    
    
    
/////////////////////////////////////////////////////
//                                                 //
//       *~*~*~*~*~*~ ACCESSORS  ~*~*~*~*~*~*      //
//                                                 //
/////////////////////////////////////////////////////
    
    public String getRnumber ()
    {
        return r_number;
    }
    
    public String getTitle ()
    {
        return title;
    }
    
    public String getPublisher ()
    {
        return publisher;
    }

    public String getDistributor ()
    {
        return distributor;
    }

    public String getAuthor ()
    {
        return author;
    }

    public String getEditor ()
    {
        return editor;
    }

    public String getIllustrator ()
    {
        return illustrator;
    }

    public String getTranslator ()
    {
        return translator;
    }

    public String getProducer ()
    {
        return producer;
    }

    public String getNarrator ()
    {
        return narrator;
    }

    public String getComposer ()
    {
        return composer;
    }

    public String getMusician ()
    {
        return musician;
    }

    public String getCreator ()
    {
        return creator;
    }

    public String getDirector ()
    {
        return director;
    }

    public String getContent ()
    {
        return content;
    }

    public String getPrice ()
    {
        return price;
    }

    public String getPageCount ()
    {
        return page_count;
    }

    public String getIsbn ()
    {
        return isbn;
    }

    public String getFormat ()
    {
        return format;
    }

    public String getCopyright ()
    {
        return copyright;
    }

    public String getExpiry ()
    {
        return expiry;
    }

    public String getYearRecommended ()
    {
        return year_recommended;
    }

    public String getRoverId ()
    {
        return rover_id;
    }

    public String getMedium ()
    {
        return medium;
    }

    public String getRunningTime ()
    {
        return running_time;
    }

    public String getCoverImagePath ()
    {
        return cover_image_path;
    }

    public String getLinkString ()
    {
        return link_string;
    }
    public String getNotes ()
    {
        return notes;
    }

    public String getAnnotation ()
    {
        return annotation;
    }

    public ResourceRecordCurriculumGroup[] getCurriculumGroups () 
    {
        return curriculum_groups.toArray(new ResourceRecordCurriculumGroup[curriculum_groups.size()]);
    }
    
    public Boolean getOutOfPrint ()
    {
        return out_of_print;
    }
    
}
