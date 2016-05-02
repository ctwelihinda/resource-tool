/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.outcomes_indicators.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import stans.db.Query;

/**
 *
 * @author peter
 */
public abstract class Alignable {
	private String identifier;
	private String text;
	private Integer dbID;
	
	public Alignable() {
		this( "", "", 0 );
	}
	public Alignable( String id, String t ) {
		this( id, t, 0 );
	}
	public Alignable( int db_id ) {
		this( "", "", db_id );
	}
	private Alignable( String id, String t, int db_id ) {
		identifier = id;
		text = t;
		dbID = db_id;
	}
	
	public String getIdentifier() {
		if( identifier.equals("") ) {
			for( Integer identifierID : Query.find( "lrn_standard", "clp_sog_pk1=?", new ArrayList<String>(Arrays.asList(dbID.toString()) ) ) ) {
				identifier = ( String )Query.select( "lrn_standard", "doc_num", identifierID );
				identifier = ( identifier==null ) ? "" : identifier;
			}
		}
		return identifier;
	}
	public String getText() {
		if( text.equals("") ) {
			text = ( String )Query.select( "clp_sog", "description", dbID );
			text = ( text==null ) ? "" : text;
		}
		return text;
	}
	public Integer getDbId() {
		return dbID;
	}
	
	public void setIdentifier( String id ) {
		identifier = id;
	}
	public void setText( String t ) {
		text = t;
	}
	public void setDbId( Integer id ) {
		dbID = id;
	}
	
	public static Comparator<Alignable> getDefaultComparator() {
		return new AlignableComparator();
	}

	public static class AlignableComparator implements Comparator<Alignable> {
		@Override
		public int compare(Alignable a1, Alignable a2) {
			String a1SortText = a1.getIdentifier();
			String a2SortText = a2.getIdentifier();
			if( a1SortText.equals("") ) {
				a1SortText = a1.getText();
			}
			if( a2SortText.equals("") ) {
				a2SortText = a2.getText();
			}
			return a1SortText.compareTo( a2SortText );
		}
	}

}
