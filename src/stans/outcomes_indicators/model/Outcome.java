/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.outcomes_indicators.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import stans.curriculum_goals_query.Query;

/**
 *
 * @author peter
 */
public class Outcome extends Alignable {
	private ArrayList<Indicator> indicators;

	public Outcome() {
		super();
		indicators = new ArrayList<Indicator>();
	}
	public Outcome( Integer db_id ) {
		super( db_id );
		indicators = new ArrayList<Indicator>();
	}
	public Outcome( String id, String t ) {
		super( id, t );
		indicators = new ArrayList<Indicator>();
	}
	
	public ArrayList<Indicator> getIndicators() {
		if( indicators.isEmpty() ) {
			for( Integer indID : Query.find( "clp_sog", "parent_sog_pk1=?", new ArrayList<String>( Arrays.asList( this.getDbId().toString() ) ) ) ) {
				indicators.add( new Indicator( indID ) );
			}
		}
		Collections.sort( indicators, new AlignableComparator() );
		return indicators;
	}
}
