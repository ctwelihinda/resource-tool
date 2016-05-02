/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.model;

/**
 *
 * @author peter
 */
public final class SubjectCode {
	private String shortCode;
	private Integer integerCode;
	private String fullName;
	
	private SubjectCode( String sc, String fn, Integer ic ) {
		this.shortCode		= sc;
		this.fullName		= fn;
		this.integerCode	= ic;
	}
	
	public String getShortCode() {
		return this.shortCode;
	}
	public String getFullName() {
		return this.fullName;
	}
	public Integer getIntegerCode() {
		return this.integerCode;
	}
	
	public static final SubjectCode ART = new SubjectCode( "ae", "Arts Education", 1 );
	public static final SubjectCode CAR = new SubjectCode( "ce", "Career Education", 2 );
	public static final SubjectCode ELA = new SubjectCode( "ela", "English Language Arts", 3 );
	public static final SubjectCode HLT = new SubjectCode( "he", "Health Education", 4  );
	public static final SubjectCode MAT = new SubjectCode( "m", "Mathematics", 5 );
	public static final SubjectCode PHY = new SubjectCode( "pe", "Physical Education", 6 );
	public static final SubjectCode SCI = new SubjectCode( "s", "Science", 7 );
	public static final SubjectCode SOC = new SubjectCode( "ss", "Social Studies", 8 );
	public static final SubjectCode PAA = new SubjectCode( "paa", "Practical and Applied Arts", 9 );
	public static final SubjectCode EAL = new SubjectCode( "eal", "EAL", 10 );
	public static final SubjectCode LNG = new SubjectCode( "lang", "Languages", 11 );
	
	public boolean equals( SubjectCode other ) {
		if(
			( this.shortCode.equals( other.getShortCode()) ) &&
			( this.fullName.equals( other.getFullName()) )
		) {
			return true;
		} else {
			return false;
		}
	}
	
	public static SubjectCode getCodeFromString( String s ) {
		if( s.toUpperCase().equals( "ART" ) )	{ return ART; }
		if( s.toUpperCase().equals( "CAR" ) )	{ return CAR; }
		if( s.toUpperCase().equals( "ELA" ) )	{ return ELA; }
		if( s.toUpperCase().equals( "HLT" ) )	{ return HLT; }
		if( s.toUpperCase().equals( "MAT" ) )	{ return MAT; }
		if( s.toUpperCase().equals( "PHY" ) )	{ return PHY; }
		if( s.toUpperCase().equals( "SCI" ) )	{ return SCI; }
		if( s.toUpperCase().equals( "SOC" ) )	{ return SOC; }
		if( s.toUpperCase().equals( "PAA" ) )	{ return PAA; }
		if( s.toUpperCase().equals( "EAL" ) )	{ return EAL; }
		if( s.toUpperCase().equals( "LNG" ) )	{ return LNG; }
		
		return new SubjectCode( "", "", 0 );
	}
	public static SubjectCode getCodeFromInteger( Integer i ) {
		if( i == 1 )	{ return ART; }
		if( i == 2 )	{ return CAR; }
		if( i == 3 )	{ return ELA; }
		if( i == 4 )	{ return HLT; }
		if( i == 5 )	{ return MAT; }
		if( i == 6 )	{ return PHY; }
		if( i == 7 )	{ return SCI; }
		if( i == 8 )	{ return SOC; }
		if( i == 9 )	{ return PAA; }
		if( i == 10 )	{ return EAL; }
		if( i == 11 )	{ return LNG; }
		
		return new SubjectCode( "", "", 0 );
	}
}
