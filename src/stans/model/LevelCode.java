/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author peter
 */
public class LevelCode {
	private String shortCode;
	private String fullName;
	
	private LevelCode( String sc, String fn ) {
		this.shortCode	= sc;
		this.fullName	= fn;
	}
	
	public String getShortCode() {
		return this.shortCode;
	}
	public String getFullName() {
		return this.fullName;
	}
	
	public static final LevelCode UNKNOWN = new LevelCode( "NULL", "Null" );
	public static final LevelCode PREK = new LevelCode( "PREK", "Prekindergarten" );
	public static final LevelCode K = new LevelCode( "K", "Kindergarten" );
	public static final LevelCode GRADE1 = new LevelCode( "1", "Grade 1" );
	public static final LevelCode GRADE2 = new LevelCode( "2", "Grade 2" );
	public static final LevelCode GRADE3 = new LevelCode( "3", "Grade 3" );
	public static final LevelCode GRADE4 = new LevelCode( "4", "Grade 4" );
	public static final LevelCode GRADE5 = new LevelCode( "5", "Grade 5" );
	public static final LevelCode GRADE6 = new LevelCode( "6", "Grade 6" );
	public static final LevelCode GRADE7 = new LevelCode( "7", "Grade 7" );
	public static final LevelCode GRADE8 = new LevelCode( "8", "Grade 8" );
	public static final LevelCode GRADE9 = new LevelCode( "9", "Grade 9" );
	public static final LevelCode LEVEL10 = new LevelCode( "10", "Level 10" );
	public static final LevelCode LEVELA10 = new LevelCode( "A10", "Level A10" );
	public static final LevelCode LEVELB10 = new LevelCode( "B10", "Level B10" );
	public static final LevelCode LEVEL20 = new LevelCode( "20", "Level 20" );
	public static final LevelCode LEVELA20 = new LevelCode( "A20", "Level A20" );
	public static final LevelCode LEVELB20 = new LevelCode( "B20", "Level B20" );
	public static final LevelCode LEVEL30 = new LevelCode( "30", "Level 30" );
	public static final LevelCode LEVELA30 = new LevelCode( "A30", "Level A30" );
	public static final LevelCode LEVELB30 = new LevelCode( "B30", "Level B30" );
	
	public boolean equals( LevelCode other ) {
		if(
			( this.shortCode.equals( other.getShortCode()) ) &&
			( this.fullName.equals( other.getFullName()) )
		) {
			return true;
		} else {
			return false;
		}
	}
	
	public static LevelCode getCodeFromString( String s ) {
		if( s.toUpperCase().equals( "K" ) )		{ return K; }
		if( s.toUpperCase().equals( "PREK" ) )	{ return PREK; }
		if( s.toUpperCase().equals( "1" ) )		{ return GRADE1; }
		if( s.toUpperCase().equals( "2" ) )		{ return GRADE2; }
		if( s.toUpperCase().equals( "2" ) )		{ return GRADE3; }
		if( s.toUpperCase().equals( "4" ) )		{ return GRADE4; }
		if( s.toUpperCase().equals( "5" ) )		{ return GRADE5; }
		if( s.toUpperCase().equals( "6" ) )		{ return GRADE6; }
		if( s.toUpperCase().equals( "7" ) )		{ return GRADE7; }
		if( s.toUpperCase().equals( "8" ) )		{ return GRADE8; }
		if( s.toUpperCase().equals( "9" ) )		{ return GRADE9; }
		if( s.toUpperCase().equals( "10" ) )	{ return LEVEL10; }
		if( s.toUpperCase().equals( "A10" ) )	{ return LEVELA10; }
		if( s.toUpperCase().equals( "B10" ) )	{ return LEVELB10; }
		if( s.toUpperCase().equals( "20" ) )	{ return LEVEL20; }
		if( s.toUpperCase().equals( "A20" ) )	{ return LEVELA20; }
		if( s.toUpperCase().equals( "B20" ) )	{ return LEVELB20; }
		if( s.toUpperCase().equals( "30" ) )	{ return LEVEL30; }
		if( s.toUpperCase().equals( "A30" ) )	{ return LEVELA30; }
		if( s.toUpperCase().equals( "B30" ) )	{ return LEVELB30; }
		
		return UNKNOWN;
	}
	
	public static ArrayList<LevelCode> getAll() {
		return new ArrayList<LevelCode>( Arrays.asList(
			PREK,
			K,
			GRADE1,
			GRADE2,
			GRADE3,
			GRADE4,
			GRADE5,
			GRADE6,
			GRADE7,
			GRADE8,
			GRADE9,
			LEVEL10,
			LEVELA10,
			LEVELB10,
			LEVEL20,
			LEVELA20,
			LEVELB20,
			LEVEL30,
			LEVELA30,
			LEVELB30
		) );
	}
}
