/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.resourcerecord.dao.ResourceLoader;
import stans.resourcerecord.dao.ResourceTextLoader;
import stans.resourcerecord.dao.TagLoader;
import stans.resourcerecord.helpers.ValidationHelpers;
import stans.resourcerecord.model.Resource;
import stans.resourcerecord.model.ResourceText;
import stans.resourcerecord.model.Tag;

/**
 *
 * @author peter
 */
public class GetResource extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String resource_id_string = request.getParameter( "resource_id" );
            String r_number_string = request.getParameter( "r_number" );

			String length = "";
			String expiry_date = "";
			String image = "";
			String desc = "";
			String producer = "";
			String copyright = "";
            
			Resource r = null;
			
		/*
		 *	LOAD RESOURCE
		 *	-------------
		 */
            if( ( resource_id_string!=null )&&( ValidationHelpers.isPositiveInteger( resource_id_string ) ) ) {
                Integer resource_id = Integer.parseInt( resource_id_string );
                r = new Resource( resource_id );
			} else if( r_number_string!=null ) {
				r = ResourceLoader.loadByRNumber( r_number_string );
			}
			

		/*
		 *	GET IMAGE
		 *	---------
		 */
			image = r.getQuickData( "image" );
			image = ( image==null ) ? "" : image;

			
			
		/*
		 *	GET ANNOTATION
		 *	--------------
		 */
			for( ResourceText rt : ResourceTextLoader.loadByResourceID( r.getDBID() ) ) {
				if( rt.getType().equals( "Annotation" ) ) {
					desc = rt.getText();
				}
			}
			desc = ( desc==null ) ? "" : desc;
			
			
		/*
		 *	GET UPDATED_AT TIMESTAMP
		 *	------------------------
		 */
			Timestamp updatedAt = ( r.getUpdatedAt()==null ) ? r.getCreatedAt() : r.getUpdatedAt();
			if( updatedAt==null ) {
				updatedAt = new Timestamp( 0 );
			}
			String updated_at = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( updatedAt ) ).toString();
			
			


			StringBuilder taxonPathSb = new StringBuilder();
			StringBuilder initiativeSb = new StringBuilder();
			for( Tag g : TagLoader.loadByResourceDBID( r.getDBID() ) ) {
		/*
		 *	GET EXPIRY
		 *	----------
		 */
				if( g.getType().equals("Expiry Date") ) {
					expiry_date = g.getValue();
				}
				if( g.getType().equals("Streaming License") ) {
					expiry_date = g.getValue();
				}
		/*
		 *	GET PUBLISHER/PRODUCER
		 *	----------------------
		 */
				if( g.getType().equals("Publisher") ) {
					producer = g.getValue();
				}
		/*
		 *	GET COPYRIGHT DATE
		 *	------------------
		 */
				if( g.getType().equals("Copyright Date") ) {
					copyright = g.getValue();
				}
		/*
		 *	GET COPYRIGHT DATE
		 *	------------------
		 */
				if( ( g.getType().equals("Content Classification") )||( g.getType().equals("Topic") ) ) {
					
					String thisInit;
					if( g.getValue().equals( "Canadian Content" ) ) {
						thisInit = "Canada";
					} else if( g.getValue().equals( "Saskatchewan Content" ) ) {
						thisInit = "Saskatchewan";
					} else if( g.getValue().equals( "FNMI Content" ) ) {
						thisInit = "FNMI";
					} else if( g.getValue().equals( "WNCP Content" ) ) {
						thisInit = "WNCP";
					} else {
						thisInit = g.getValue();
					}
					
					if( initiativeSb.length()>0 ) {
						initiativeSb.append( "," );
					}
					initiativeSb.append( "\"" );
					initiativeSb.append( thisInit );
					initiativeSb.append( "\"" );
				}
		/*
		 *	GET LENGTH
		 *	----------
		 */
				if( ( g.getType().equals("Length") )||( g.getType().equals("Running Time") ) ) {
					length = g.getValue();
					
					if( length.contains( "min" ) ) {
						length = length.replaceAll( "\\s*min.*", "" );
						length = length.replaceAll( "[^\\d]", "" );
						try {
							length = Integer.toString( Integer.parseInt( length ) * 60 );
						} catch( NumberFormatException e ) {
							length = "";
						}
					}
					
					if( length.contains( ":" ) ) {
						Integer tempLength = 0;
						if( length.indexOf( ":" )!=length.lastIndexOf( ":" ) ) { // i.e. there are several colons
							int pos=0;
							for( String timeVal : length.split( ":" ) ) {
								switch( pos ) {
									case 0:
										tempLength = Integer.parseInt( timeVal ) * 3600;
										break;
									case 1:
										tempLength += Integer.parseInt( timeVal ) * 60;
										break;
									case 2:
										tempLength += Integer.parseInt( timeVal );
										break;
								}
								pos++;
							}
						} else {
							int pos=0;
							for( String timeVal : length.split( ":" ) ) {
								switch( pos ) {
									case 0:
										tempLength = Integer.parseInt( timeVal ) * 60;
										break;
									case 1:
										tempLength += Integer.parseInt( timeVal );
										break;
								}
								pos++;
							}
						}
						length = tempLength.toString();
					}
				}
		/*
		 *	GET PROGRAM/SUBJECT/GRADE/OUTCOMES
		 *	----------------------------------
		 */
				if( g.getType().equals("Tag Group") ) {
					ArrayList<String> subjects = new ArrayList<String>();
					ArrayList<String> levels = new ArrayList<String>();
					ArrayList<String> outcomes = new ArrayList<String>();
					ArrayList<String> programs = new ArrayList<String>();

					for( Tag t : g.getChildren( r.getDBID() ) ) {
						String type = t.getType();
						if( type.equals( "Subject") ) {
							subjects.add( t.getValue() );
						} else if(
							( type.equals( "Level") ) ||
							( type.equals( "Grade") )
						) {
							levels.add( t.getValue() );
						} else if (
							( type.equals("Unit") ) ||
							( type.equals("Module") ) ||
							( type.equals("Goal") ) ||
							( type.equals("Outcome") ) ||
							( type.equals("Domain") ) ||
							( type.equals("Strand") ) ||
							( type.equals("Indicator") ) ||
							( type.equals("Language Cuing System") ) ||
							( type.equals("Suggested Use") ) ||
							( type.equals("Contexte") )
						) {
							outcomes.add( t.getValue() );
						} else if ( type.equals("Program") ) {
							programs.add( t.getValue() );
						}
					}

					if( subjects.isEmpty() ) {
						subjects.add( "" );
					}
					if( levels.isEmpty() ) {
						levels.add( "" );
					}
					if( outcomes.isEmpty() ) {
						outcomes.add( "" );
					}
					if( programs.isEmpty() ) {
						programs.add( "" );
					}

					for( String p : programs ) {
						for( String s : subjects ) {
							for( String l : levels ) {
								for( String o : outcomes ) {
									if( taxonPathSb.length()>0 ) {
										taxonPathSb.append( "," );
									}
									taxonPathSb.append( "\"" );
									taxonPathSb.append( p );
									taxonPathSb.append( "::" );
									taxonPathSb.append( s );
									taxonPathSb.append( "::" );
									taxonPathSb.append( l );
									taxonPathSb.append( "::" );
									taxonPathSb.append( o );
									taxonPathSb.append( "\"" );
								}
							}
						}
					}
				}
			}

				out.println(	"{" );
				out.println(		"\"resource\":" );
				out.println(		"{" );
				out.println(			"\"title\":\"" + r.getTitleAndSubtitle().replace( "\"", "\\\"" ) + "\"," );
				//out.println(			"\"description\":\"" + desc.replace( "\"", "\\\"" ).replace( "\'", "\\\'" ).replace( "\n", "<br/>" ) + "\"" );
				out.println(			"\"description\":\"" + desc.replace( "\n", "" ).replace( "\r", "" ).replace( "\"", "\\\"" ).replace( "\'", "\\\'" ) + "\"" );
			if( !expiry_date.equals("") ) {
				out.println(			",");
				out.println(			"\"expiry\":\"" + expiry_date + "\"");
			}
			if( !updated_at.equals("") ) {
				out.println(			",");
				out.println(			"\"updated_at\":\"" + updated_at + "\"");
			}
			if( !length.equals("") ) {
				out.println(			",");
				out.println(			"\"length\":\"" + length + "\"");
			}
			if( !producer.equals("") ) {
				out.println(			",");
				out.println(			"\"producer\":\"" + producer + "\"");
			}
			if( !copyright.equals("") ) {
				out.println(			",");
				out.println(			"\"copyright_date\":\"" + copyright + "\"");
			}
			if( initiativeSb.length()>0 ) {
				out.println(			",");
				out.println(			"\"initiatives\":[" + initiativeSb.toString() + "]");
			}
			if( !image.equals("") ) {
				out.println(			",");
				out.println(			"\"image\":\"" + image.replace( " ", "%20" ) + "\"");
			}
			if( taxonPathSb.length()>0 ) {
				out.println(			",");
				out.println(			"\"taxon_paths\":[" + taxonPathSb.toString() + "]");
			}
				out.println(		"}");
				out.println(	"}");

					
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
