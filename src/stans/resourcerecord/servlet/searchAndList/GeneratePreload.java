/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.searchAndList;

import blackboard.cms.filesystem.CSContext;
import blackboard.cms.filesystem.CSFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.xythos.common.api.XythosException;

import stans.XMLReader;
import stans.db.Enumerators;
import stans.resourcerecord.dao.ResourceLoader;
import stans.resourcerecord.dao.TagLoader;
import stans.resourcerecord.dao.TagTypeLoader;
import stans.resourcerecord.model.Resource;
import stans.resourcerecord.model.Tag;

/**
 *
 * @author peter
 */
public class GeneratePreload extends HttpServlet {

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
	
		
		Boolean debug = false;
		String debugString = request.getParameter( "debug" );
		if( ( debugString!=null )&&( debugString.equals("true") ) ) {
			debug = true;
		}
		
		String curricName = "unknown";
		String queryName = "unknown";
        ArrayList<Resource> results = new ArrayList<Resource>();
		ArrayList<Tag> searchFilters = new ArrayList<Tag>();
		LinkedHashSet<Integer> hf = new LinkedHashSet<Integer>(); // for eliminating dupes
		ArrayList<String> allowed_tagtypes = new ArrayList<String>();
        HashMap<String, Boolean> rtn_dupes = new HashMap<String, Boolean>();
        ArrayList<String> levelList = new ArrayList<String>();
        ArrayList<String> subjectList = new ArrayList<String>();
        ArrayList<String> programList = new ArrayList<String>();
		
		String searchCode = request.getParameter( "search_code" );
		String separator = request.getParameter( "separator" );
		
		Boolean kindergartenSearch = false;
		Boolean prekindergartenSearch = false;
		
        String subject = "";
        String level = "";
		if( searchCode!=null ) {
			if( separator==null ) {
				separator = "--";
			}
			String[] subj_and_level = searchCode.split( separator );
			if( subj_and_level.length>=2 ) {
				subject = subj_and_level[0];
				level = subj_and_level[1];
				if( debug ) {
					System.out.println( "search_code:" + searchCode );
					System.out.println( "subject:" + subject );
					System.out.println( "level:" + level );
				}
			} else {
				if( debug ) { System.out.println( "Invalid search_code provided:" + searchCode ); }
			}
		}
		
		allowed_tagtypes.add("Topic");
		allowed_tagtypes.add("Content Classification");
		allowed_tagtypes.add("Resource List Classification");
		allowed_tagtypes.add("Medium");
		allowed_tagtypes.add("Format");
		allowed_tagtypes.add("Language");
		allowed_tagtypes.add("Program");
		allowed_tagtypes.add("Outcome");
		allowed_tagtypes.add("Domain");
		allowed_tagtypes.add("Strand");
		allowed_tagtypes.add("Module");
		allowed_tagtypes.add("Unit");
		allowed_tagtypes.add("Goal");
		allowed_tagtypes.add("Subject");
		allowed_tagtypes.add("Grade");
		allowed_tagtypes.add("Level");
		
		CSContext ctxCS = null;
		ByteArrayOutputStream manifestOutput = new ByteArrayOutputStream();
		try {
			ctxCS = CSContext.getContext();
			CSFile manifestFile = ( CSFile )ctxCS.findEntry( "/library/Curriculum Website/New Resource Search/Curriculum Manifest/curric_manifest.xml" );
			manifestFile.getFileContent( manifestOutput );
		} catch( Exception e ) {
			ctxCS.rollback();
		} finally {
			if (ctxCS != null) {
				try {
					ctxCS.commit();
				} catch (XythosException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		ByteArrayInputStream manifestInput = new ByteArrayInputStream( manifestOutput.toByteArray() );
		XMLReader manifestdoc = new XMLReader();  
		manifestdoc.setStream( manifestInput );
		
	// Dig into the content system
		try {
			String partialXPath = "subjects/subject[@name='" + subject + "']/levels/level[@id='" + level + "']/";
            if (manifestdoc.nodeExists(partialXPath + "curric_name")){
                curricName = manifestdoc.getNodeValue( partialXPath + "curric_name" );
            }
            if (manifestdoc.nodeExists(partialXPath + "res_class_id")){
                queryName = manifestdoc.getNodeValue( partialXPath + "res_class_id" ).replaceAll( "\\s","" );// + ".html";
				if( debug ) { System.out.println( "queryName:" + queryName ); }
            }
            if (manifestdoc.nodeExists(partialXPath + "search_subjects/search_subject")){
				if( debug ) { System.out.println( "Looking at search_subjects" ); }
                NodeList nl = manifestdoc.getNodelist( partialXPath + "search_subjects/search_subject" );
				for (int i = 0; i < nl.getLength(); i++) {
					Node temp = nl.item(i);
					if( debug ) { System.out.println( Integer.toString(i) + ":node value:" + temp.getTextContent() ); }
					subjectList.add( temp.getTextContent().replaceAll( "^\\s+", "" ).replaceAll( "\\s+$", "" ) );
					if( debug ) { System.out.println( "Add to subjectList:" + subjectList.get( subjectList.size()-1 ) ); }
				}
            }
            if (manifestdoc.nodeExists(partialXPath + "search_programs/search_program")){
				if( debug ) { System.out.println( "Looking at search_programs" ); }
                NodeList nl = manifestdoc.getNodelist( partialXPath + "search_programs/search_program" );
				for (int i = 0; i < nl.getLength(); i++) {
					Node temp = nl.item(i);
					if( debug ) { System.out.println( Integer.toString(i) + ":node value:" + temp.getTextContent() ); }
					programList.add( temp.getTextContent().replaceAll( "^\\s+", "" ).replaceAll( "\\s+$", "" ) );
					if( debug ) { System.out.println( "Add to programList:" + programList.get( programList.size()-1 ) ); }
				}
            }
            if( manifestdoc.nodeExists(partialXPath + "search_levels/search_level") ){
				if( debug ) { System.out.println( "Looking at search_levels" ); }
                NodeList nl = manifestdoc.getNodelist( partialXPath + "search_levels/search_level" );
				for (int i = 0; i < nl.getLength(); i++) {
					Node temp = nl.item(i);
					if( debug ) { System.out.println( Integer.toString(i) + ":node value:" + temp.getTextContent() ); }
					levelList.add( temp.getTextContent().replaceAll( "^\\s+", "" ).replaceAll( "\\s+$", "" ) );
					if( debug ) { System.out.println( "Add to levelList:" + levelList.get( levelList.size()-1 ) ); }
				}
            }
			
		} catch( Exception e ) {
		
		}
		
		
		if( searchCode.equals("kindergarten"+separator+"k") ) {
			//kindergartenSearch = true;
			//level = "Kindergarten";
		}
		if( searchCode.equals("prekindergarten"+separator+"prek") ) {
			//prekindergartenSearch = true;
			//level = "Prekindergarten";
		}
		
        String resourceOrSupport = request.getParameter("resource_or_support");
        if ((resourceOrSupport == null) || (resourceOrSupport.isEmpty())) {
			resourceOrSupport = "both";
		}
        
        String franOrImm = "";
		for( String p : programList ) {
			if( p.equals( "Immersion") ) {
				if( !franOrImm.equals("") ) {
					franOrImm = "both";
				} else {
					franOrImm = "imm";
				}
			}
			if( p.equals( "Fransaskois") ) {
				if( !franOrImm.equals("") ) {
					franOrImm = "both";
				} else {
					franOrImm = "fran";
				}
			}
		}

		
		if( levelList.isEmpty() ) {
			levelList.add( "" );
		}
		if( subjectList.isEmpty() ) {
			subjectList.add( "" );
		}
		
		prekindergartenSearch = false;
		kindergartenSearch = false;
		if( ( kindergartenSearch )||( prekindergartenSearch ) ) {
			/*
			Tag subjectTag = TagLoader.loadByTagTypeAndTagValue( level, TagTypeLoader.loadByType( "Subject" ) );
			Tag levelTag = TagLoader.loadByTagTypeAndTagValue( level, TagTypeLoader.loadByType( "Level" ) );
			for (Resource r : ResourceLoader.loadByTag(subjectTag) )
			{
				if (!rtn_dupes.containsKey(r.getRNumber()))
				{
					results.add(r);
					rtn_dupes.put(r.getRNumber(), Boolean.TRUE);
				}
			}
			for (Resource r : ResourceLoader.loadByTag(levelTag) )
			{
				if (!rtn_dupes.containsKey(r.getRNumber()))
				{
					results.add(r);
					rtn_dupes.put(r.getRNumber(), Boolean.TRUE);
				}
			}*/
		} else {			
			for (String this_level : levelList) {
				for (String this_subject : subjectList) {

					if( debug ) {
						System.out.println( "this_level: " + this_level );
						System.out.println( "this_subject: " + this_subject );
					}
				
					ArrayList<Tag> tags_to_search = new ArrayList<Tag>();

					if( !this_subject.equals("") )	{ tags_to_search.addAll(TagLoader.loadByTagTypeNameAndTagValueAndOperator("Subject", this_subject, Enumerators.BBComparisonOperator.EQUALS)); }
					if( !this_level.equals("") )	{ tags_to_search.addAll(TagLoader.loadByTagTypeNameAndTagValueAndOperator("Level", this_level, Enumerators.BBComparisonOperator.EQUALS)); }
					if( !this_level.equals("") )	{ tags_to_search.addAll(TagLoader.loadByTagTypeNameAndTagValueAndOperator("Grade", this_level, Enumerators.BBComparisonOperator.EQUALS)); }

					if (franOrImm.equals("fran"))
					{ tags_to_search.addAll(TagLoader.loadByTagTypeNameAndTagValueAndOperator("Program", "Fransaskois", Enumerators.BBComparisonOperator.EQUALS)); }
					if (franOrImm.equals("imm"))
					{ tags_to_search.addAll(TagLoader.loadByTagTypeNameAndTagValueAndOperator("Program", "Immersion", Enumerators.BBComparisonOperator.EQUALS)); }

					// if we just want one or the other, iterate over the results and check each one and only add it to results if it has support material tag (or not)
					// otherwise, just return the whole results list
					if( !resourceOrSupport.equals("both") ) {
						for( Resource r : ResourceLoader.loadByTagGroupContent(tags_to_search) ) {
							if( r.isSupportMaterial() ) {
								if( resourceOrSupport.equals("support") ) {
									if( !rtn_dupes.containsKey(r.getRNumber()) ) {
										if (r.getFinalRecommendation() == 1) {
											results.add(r);
											rtn_dupes.put(r.getRNumber(), Boolean.TRUE);
										}
									}
								}
							} else {
								if (resourceOrSupport.equals("resource")) {
									if( !rtn_dupes.containsKey(r.getRNumber()) ) {
										if (r.getFinalRecommendation() == 1) {
											results.add(r);
											rtn_dupes.put(r.getRNumber(), Boolean.TRUE);
										}
									}
								}
							}
						}
					} else {
						if( debug ) {
							System.out.println( "tags_to_search:" );
							System.out.println( "---------------" );
							for( Tag t : tags_to_search ) {
								System.out.println( "  *" + t.getType() + ":" + t.getValue() );
							}
						}
						for( Resource r : ResourceLoader.loadByTagGroupContent( tags_to_search ) ) {
							if( !rtn_dupes.containsKey(r.getRNumber()) ) {
								if (r.getFinalRecommendation() == 1) {
									results.add(r);
									rtn_dupes.put(r.getRNumber(), Boolean.TRUE);
								}
							}
						}
					}
				}
	        }
        }
		
		
	// filters
		for( Resource r : results ) {
			for( Tag t: r.getTags() ) {
				if( allowed_tagtypes.contains( t.getType() ) ) { 
					if( !hf.contains(t.getDBID()) ) {
						hf.add(t.getDBID());
						searchFilters.add(t);
					} 
				}
			}
		}
		
		request.setAttribute( "franOrImm", franOrImm );
		request.setAttribute( "resourceOrSupport", resourceOrSupport );
		request.setAttribute( "searchFilters", searchFilters );
		request.setAttribute( "curricName", curricName );
		request.setAttribute( "subjectList", subjectList );
		request.setAttribute( "levelList", levelList );
		request.setAttribute( "programList", programList );
		request.setAttribute( "searchResults", results );
		request.setAttribute( "queryName", queryName );
		request.setAttribute( "debug", debug );
		
		request.getRequestDispatcher("/preload_renderer.jsp").forward(request, response);
		
		//request.getRequestDispatcher("/preload_dashboard.jsp").forward(request, response);
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
