/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.helpers;

/**
 *
 * @author peter
 */
public class ValidationHelpers {
    
    public static boolean isPositiveInteger(String s)
    {
        Integer i;
        
        // is it an integer?
        try 
        {
            i = Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        
        // is it positive?
        if (i < 1)
        {
            return false;
        }
        
        // haven't returned false, so must be true        
        return true;
    }
    
    public static boolean isWholeNumber(String s)
    {
        Integer i;
        
        // is it an integer?
        try 
        {
            i = Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        
        // is it non-negative?
        if (i < 0)
        {
            return false;
        }
        
        // haven't returned false, so must be true        
        return true;
    }
}
