/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.helpers;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author peter
 */
public class Enumerators {
	public enum Language {
		EN	(0),
		FR	(1),
		UK	(2);
		private final int value;
		
		private Language( int v ) {
			this.value = v;
		}
		public int getValue() {
			return this.value;
		}
		public String getName() {
			if ( this==FR ) {
				return "French";
			} else if ( this==UK ) {
				return "Ukrainian";
			} else {
				return "English";
			}
		}
		public static Language getByValue( int v ) {
			for( Language s : values() ) {
				if( s.getValue()==v ) {
					return s;
				}
			}
			return EN;
		}
		public static ArrayList<Language> getAll() {
			return new ArrayList<Language>( Arrays.asList( values() ) );
		}
	}
}
