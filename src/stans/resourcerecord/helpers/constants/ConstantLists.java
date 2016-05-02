/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.helpers.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author peter
 */
public class ConstantLists {
    public static final List<String> allGradesList = Arrays.asList(
        "PreK",
        "K",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "20",
        "30");
    
    //Added by Nicole - Done like this to prevent breaking of any other instances
        public static final List<String> allGradesListFrench = Arrays.asList(
        "M",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "20",
        "30");
    //End of Add
    
    public static final HashMap<String, String> iconImageMap = new HashMap<String, String>();
    static {
        iconImageMap.put("rovervideo", "rover_icon.jpeg");
        iconImageMap.put("free", "free_icon.jpeg");
        iconImageMap.put("canadiancontent", "can_icon.jpeg");
        iconImageMap.put("saskatchewancontent", "sk_icon.png");
        iconImageMap.put("fnmicontent", "fnmi_icon.png");
    }
    
    public static final HashMap<String, String> mediumformatShortlist = new HashMap<String, String>();
    static {
        mediumformatShortlist.put("Big Book", "Book");
        mediumformatShortlist.put("Binder", "Book");
        mediumformatShortlist.put("Binder + CD", "Book");
        mediumformatShortlist.put("Binder + CD-ROM", "Book");
        mediumformatShortlist.put("Binder + USB", "Book");
        mediumformatShortlist.put("Board Book", "Book");
        mediumformatShortlist.put("Book + DVD", "Book");
        mediumformatShortlist.put("Book and Student eText Access Card", "Book");
        mediumformatShortlist.put("Graphic Novel", "Book");
        mediumformatShortlist.put("Journal", "Book");
        mediumformatShortlist.put("PDF", "Book");

        mediumformatShortlist.put("Blu-ray disc", "DVD/Video/CD");
        mediumformatShortlist.put("CD", "DVD/Video/CD");
        mediumformatShortlist.put("CD-ROM", "DVD/Video/CD");
        mediumformatShortlist.put("Cassette", "DVD/Video/CD");
        mediumformatShortlist.put("DVD", "DVD/Video/CD");
        mediumformatShortlist.put("DVD-ROM", "DVD/Video/CD");
        mediumformatShortlist.put("Disk", "DVD/Video/CD");
        mediumformatShortlist.put("ROVER Video", "DVD/Video/CD");
        mediumformatShortlist.put("ROVER Series", "DVD/Video/CD");
        mediumformatShortlist.put("Streaming Video", "DVD/Video/CD");

        mediumformatShortlist.put("Multimedia Kit", "Kit");
        mediumformatShortlist.put("Transparencies", "Kit");

        mediumformatShortlist.put("Online Resource", "Website");

        mediumformatShortlist.put("Apparatus", "Other");
        mediumformatShortlist.put("Game", "Other");
        mediumformatShortlist.put("Manipulative", "Other");
        mediumformatShortlist.put("Map", "Other");
        mediumformatShortlist.put("Pictures", "Other");
        mediumformatShortlist.put("Poster", "Other");
        mediumformatShortlist.put("Record", "Other");
        mediumformatShortlist.put("Rug", "Other");
        mediumformatShortlist.put("Set", "Other");
        mediumformatShortlist.put("Software", "Other");
        mediumformatShortlist.put("Toy", "Other");
        mediumformatShortlist.put("Video Game", "Other");
    }
}
