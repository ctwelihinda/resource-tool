/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.outcomes_indicators.servlet;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.outcomes_indicators.dao.OutcomeLoader;
import stans.model.LevelCode;
import stans.model.SubjectCode;
import stans.outcomes_indicators.model.GoalArea;

/**
 *
 * @author peter
 */
public class curriculumDisplayBySubjectAndLevel extends HttpServlet {

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

		String level = request.getParameter( "level" );
		String subj = request.getParameter( "subj" );
		
		ArrayList<GoalArea> goalAreas = new ArrayList<GoalArea>();
		
		if( ( level!=null )&&( subj!=null ) ) {
			SubjectCode sc = SubjectCode.getCodeFromString( subj );
			LevelCode lc = LevelCode.getCodeFromString( level );
			goalAreas = OutcomeLoader.loadGoalAreasByGradeAndSubject( sc, lc );
		}
		
		request.setAttribute( "goalAreas", goalAreas );
		RequestDispatcher dispatcher = request.getRequestDispatcher( "/index.jsp" );
        dispatcher.forward( request, response );
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
