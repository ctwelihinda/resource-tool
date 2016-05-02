/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.helpers;

/**
 *
 * @author peter
 */
public class specialCharHelpers {
    
    public static String frenchHTML(String s) {
        if (s.length() == 0) {
            return s;
        }
        
        s = s.replace("à", "&agrave;");
        s = s.replace("À", "&Agrave;");
        
        s = s.replace("è", "&egrave;");
        s = s.replace("È", "&Egrave;");
        
        s = s.replace("ù", "&ugrave;");
        s = s.replace("Ù", "&Ugrave;");
        
        s = s.replace("é", "&eacute;");
        s = s.replace("É", "&Eacute;");
        
        s = s.replace("ê", "&ecirc;");
        s = s.replace("Ê", "&Ecirc;");
        
        s = s.replace("â", "&acirc;");
        s = s.replace("Â", "&Acirc;");
        
        s = s.replace("î", "&icirc;");
        s = s.replace("Î", "&Icirc;");
        
        s = s.replace("ô", "&ocirc;");
        s = s.replace("Ô", "&Ocirc;");
        
        s = s.replace("û", "&ucirc;");
        s = s.replace("Û", "&Ucirc;");
        
        s = s.replace("ë", "&euml;");
        s = s.replace("Ë", "&Euml;");
        
        s = s.replace("ä", "&auml;");
        s = s.replace("Ä", "&Auml;");
        
        s = s.replace("ï", "&iuml;");
        s = s.replace("Ï", "&Iuml;");
        
        s = s.replace("ü", "&uuml;");
        s = s.replace("Ü", "&Uuml;");
        
        s = s.replace("œ", "&oelig;");
        s = s.replace("œ", "&Oelig;");
        
        s = s.replace("ç", "&ccedil;");
        s = s.replace("Ç", "&Ccedil;");
        
        s = s.replace("'", "\\'");
        
        return s;
    }
    
    public static String capitalize(String s) {
        if (s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
    
}
