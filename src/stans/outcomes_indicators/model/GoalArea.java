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
public class GoalArea extends Alignable {
	private ArrayList<Outcome> outcomes;

	public GoalArea() {
		super();
		outcomes = new ArrayList<Outcome>();
	}
	public GoalArea( Integer db_id ) {
		super( db_id );
		outcomes = new ArrayList<Outcome>();
	}
	public GoalArea( String id, String t ) {
		super( id, t );
		outcomes = new ArrayList<Outcome>();
	}
	
	public ArrayList<Outcome> getOutcomes() {
		if( outcomes.isEmpty() ) {
			for( Integer outID : Query.find( "clp_sog", "parent_sog_pk1=?", new ArrayList<String>( Arrays.asList( this.getDbId().toString() ) ) ) ) {
				outcomes.add( new Outcome( outID ) );
			}
		}
		Collections.sort( outcomes, new Alignable.AlignableComparator() );
		return outcomes;
	}
}
