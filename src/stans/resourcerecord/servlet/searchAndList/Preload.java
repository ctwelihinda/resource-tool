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
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xythos.common.api.XythosException;

import stans.XSLRenderer;

/**
 *
 * @author peter
 */
public class Preload extends HttpServlet {

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

		String debugString = request.getParameter( "debug" );
		Boolean debug = false;
		if( ( debugString!=null )&&( debugString.equals("true") ) ) {
			debug = true;
		}
		
	// Open outcome list    
		String xsl_filename = "curriculum_manifest_menu.xsl";
		String manifest_filename = "curric_manifest.xml";
		ByteArrayOutputStream manifest_os = new ByteArrayOutputStream();

		CSContext open_file_context = null;
		try {
			open_file_context = CSContext.getContext();
			CSFile manifest_file = ( CSFile )open_file_context.findEntry( "/library/Curriculum Website/New Resource Search/Curriculum Manifest/" + manifest_filename );
			manifest_file.getFileContent( manifest_os );
		} catch( Exception e ) {
			open_file_context.rollback();
		} finally {
			if( open_file_context != null ) {
				try {
					open_file_context.commit();
				} catch (XythosException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		ByteArrayInputStream manifest_is = new ByteArrayInputStream( manifest_os.toByteArray() );
		
		StringBuilder outputHTML = new StringBuilder();
		outputHTML.append( "<html>" );
			outputHTML.append( "<head>" );
				outputHTML.append( "<title>Preload Dashboard</title>" );
				outputHTML.append( "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://www.bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/css/preload_dashboard_styles.css\" />" );
				outputHTML.append( "<script src=\"javascript/jquery-latest.min.js\"></script>" );
				outputHTML.append( "<script src=\"https://www.bblearndev.merlin.mb.ca/bbcswebdav/library/Curriculum%20Website/New%20Resource%20Search/javascript/preload_dashboard_scripts.js\"></script>" );
			outputHTML.append( "</head>" );
			outputHTML.append( "<body>" );
				outputHTML.append( "<form action=\"GeneratePreload\" method=\"POST\">" );
		if( debug ) {
					outputHTML.append( "<input type=\"hidden\" name=\"debug\" value=\"true\" />" );
		}
					outputHTML.append( XSLRenderer.transformXMLFromStream( request, manifest_is, xsl_filename, null ) );
				outputHTML.append( "</form>" );
			outputHTML.append( "</body>" );
		outputHTML.append( "</html>" );

		try {
			out.print( outputHTML.toString() );
        } finally {            
            out.close();
        }
		//request.getRequestDispatcher( "/preload_dashboard.jsp" ).forward( request, response );
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
