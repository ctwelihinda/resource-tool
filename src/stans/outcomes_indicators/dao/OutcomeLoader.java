/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.outcomes_indicators.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import stans.db.Query;
import stans.model.LevelCode;
import stans.model.SubjectCode;
import stans.outcomes_indicators.model.Alignable;
import stans.outcomes_indicators.model.GoalArea;
import stans.outcomes_indicators.model.Outcome;

/**
 *
 * @author peter
 */
public class OutcomeLoader {
	public static ArrayList<GoalArea> loadGoalAreasByGradeAndSubject( SubjectCode sc, LevelCode lc ) {
		ArrayList<GoalArea> returnList = new ArrayList<GoalArea>();

		System.out.println( "SubjectCode full name:" + sc.getFullName() );
		System.out.println( "SubjectCode short code:" + sc.getShortCode() );
		System.out.println( "LevelCode full name:" + lc.getFullName() );
		System.out.println( "LevelCode short code:" + lc.getShortCode() );
		
		String middle = "";
		String suffix = "_0";
		boolean addZero = true;
		if(
			( sc.equals( SubjectCode.ART ) ) ||
			( sc.equals( SubjectCode.MAT ) ) ||
			( sc.equals( SubjectCode.ELA ) )
		) {
			addZero = false;
		}

		if( addZero ) {
			if( !lc.equals( LevelCode.K ) ) {
				middle = "0";
			}
		}
		middle += lc.getShortCode();

		String batchUID = sc.getShortCode() + "_" + middle + suffix;

		if( sc.equals( SubjectCode.ART ) ) {
			/*if( lc.equals( LevelCode.K ) )			{ batchUID = "ae__0"; }
			if( lc.equals( LevelCode.GRADE1 ) )		{ batchUID = "C0888B11-3DE3-A483-80E2-4B48CD28DBC8"; }
			if( lc.equals( LevelCode.GRADE2 ) )		{ batchUID = "D0888B11-3DE3-A483-80E2-4B48CD28DBC8"; }
			if( lc.equals( LevelCode.GRADE3 ) )		{ batchUID = "E0888B11-3DE3-A483-80E2-4B48CD28DBC8"; }
			if( lc.equals( LevelCode.GRADE4 ) )		{ batchUID = "F0888B11-3DE3-A483-80E2-4B48CD28DBC8"; }
			if( lc.equals( LevelCode.GRADE5 ) )		{ batchUID = "G0888B11-3DE3-A483-80E2-4B48CD28DBC8"; }
			if( lc.equals( LevelCode.GRADE6 ) )		{ batchUID = "H0888B11-3DE3-A483-80E2-4B48CD28DBC8"; }
			if( lc.equals( LevelCode.GRADE7 ) )		{ batchUID = "H0888B11-3DE3-A483-80E2-4B48CD28DBC8"; }
			if( lc.equals( LevelCode.GRADE8 ) )		{ batchUID = "H0888B11-3DE3-A483-80E2-4B48CD28DBC8"; }
			if( lc.equals( LevelCode.GRADE9 ) )		{ batchUID = "H0888B11-3DE3-A483-80E2-4B48CD28DBC8"; }*/
			if( lc.equals( LevelCode.K ) )			{ batchUID = "ae__0"; }
			if( lc.equals( LevelCode.GRADE1 ) )		{ batchUID = "ART_1"; }
			if( lc.equals( LevelCode.GRADE2 ) )		{ batchUID = "ART_2"; }
			if( lc.equals( LevelCode.GRADE3 ) )		{ batchUID = "ART_3"; }
			if( lc.equals( LevelCode.GRADE4 ) )		{ batchUID = "ART_4"; }
			if( lc.equals( LevelCode.GRADE5 ) )		{ batchUID = "ART_5"; }
			if( lc.equals( LevelCode.GRADE6 ) )		{ batchUID = "ART_6"; }
			if( lc.equals( LevelCode.GRADE7 ) )		{ batchUID = "ART_7"; }
			if( lc.equals( LevelCode.GRADE8 ) )		{ batchUID = "ART_8"; }
			if( lc.equals( LevelCode.GRADE9 ) )		{ batchUID = "ART_9"; }
		}

		System.out.println( "batchUID:" + batchUID );
		
		ArrayList<String> lArgs = new ArrayList<String>( Arrays.asList( batchUID ) );
		for( Integer lID : Query.find( "lrn_stds_sub_doc", "batch_uid=?", lArgs ) ) {
			System.out.println( "Found lrn_stds_sub_doc:" + lID.toString() );
			ArrayList<String> gArgs = new ArrayList<String>( Arrays.asList( lID.toString() ) );
			for( Integer gID : Query.find( "clp_sog", "lrn_stds_sub_doc_pk1=? AND parent_sog_pk1 IS NULL", gArgs ) ) {
				System.out.println( "Found clp_sog:" + gID.toString() );
				returnList.add( new GoalArea( gID ) );
			}
		}
		
		Collections.sort( returnList, new Alignable.AlignableComparator() );
		
		return returnList;
	}
}
