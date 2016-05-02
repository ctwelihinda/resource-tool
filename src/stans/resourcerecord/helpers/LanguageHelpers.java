/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.helpers;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author peter
 */
public class LanguageHelpers {
    
    public static String getTranslation(String original_phrase, String new_language, String context) throws IOException
    {
        if (new_language.equals("fr"))
        {
            String prop_filename = "default_fr.properties";
            Properties translations = new Properties();
            translations.load(LanguageHelpers.class.getClassLoader().getResourceAsStream("../" + prop_filename));
            return translations.getProperty(original_phrase.replace(" ", "_"), original_phrase);
        }
        
        return original_phrase;
    }
}
