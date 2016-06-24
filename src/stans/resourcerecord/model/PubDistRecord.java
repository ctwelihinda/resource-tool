/*
 * This is just for importing the publisher/distributor information
 * 
 */
package stans.resourcerecord.model;
import java.sql.Timestamp;
import java.util.*;

import stans.resourcerecord.dao.TagLoader;
import stans.resourcerecord.helpers.TagComparator;

/**
 *
 * @author peter
 */
public class PubDistRecord {
    private int db_id;
    private String created_by;
    private Timestamp created_at;
    private Timestamp updated_at;
    private String name;
    private String address;
    private ArrayList<String> contacts;
    private String phone;
    private String email;
    private String website;
    private String notes;
    
    public PubDistRecord()
    {
 
        name = "";
        address = "";
        contacts = new ArrayList<String>();
        phone = "";
        email = "";
        website = "";
        notes = "";
    }
    
    public PubDistRecord(int id)
    {
    	db_id = id;
        name = "";
        address = "";
        contacts = new ArrayList<String>();
        phone = "";
        email = "";
        website = "";
        notes = "";
    }
    public PubDistRecord(int db_id, String pName, String pAddress, String pPhone, String pEmail, String pWebsite, String pNotes){
    	this.db_id = db_id;
        name = pName;
        address = pAddress;
        contacts = new ArrayList<String>();
        phone = pPhone;
        email = pEmail;
        website = pWebsite;
        notes = pNotes;
    }
    
    
    
    public int getDBID(){
    	return db_id;
    }
    
    
    public String getName(){
    	return name;
    }
    public String getAddress(){
    	return address;
    }
    public ArrayList<String> getContacts(){
    	return contacts;
    }
    public String getPhone(){
    	return phone;
    }
    public String getEmail(){
    	return email;
    }
    public String getWebsite(){
    	return website;
    }
    public String getNotes(){
    	return notes;
    }
    public void addContact(String contact){
    	contacts.add(contact);
    }
    
    public void setName(String name){
    	this.name = name;
    }
    public void setAddress(String address){
    	this.address = address;
    }
    public void setContacts(ArrayList<String> contacts){
    	this.contacts = contacts;
    }
    public void setPhone(String phone){
    	this.phone = phone;
    }
    public void setEmail(String email){
    	this.email = email;
    }
    public void setWebsite(String website){
    	this.website = website;
    }
    public void setNotes(String notes){
    	this.notes = notes;
    }
    
    public ArrayList<Tag> getRootTags() {
        ArrayList<Tag> tags = TagLoader.loadRootOnlyByPublisherDBID(db_id);
        Collections.sort(tags, new TagComparator());

        return tags;
    }
    

    
}
