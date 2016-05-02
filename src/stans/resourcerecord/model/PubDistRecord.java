/*
 * This is just for importing the publisher/distributor information
 * 
 */
package stans.resourcerecord.model;

/**
 *
 * @author peter
 */
public class PubDistRecord {
   
    public String name;
    public String address;
    public String contact;
    public String phone;
    public String email;
    public String website;
    public String notes;
    
    public PubDistRecord()
    {
        name = "";
        address = "";
        contact = "";
        phone = "";
        email = "";
        website = "";
        notes = "";
    }
}
