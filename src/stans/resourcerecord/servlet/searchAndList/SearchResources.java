/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.searchAndList;

import blackboard.persist.PersistenceException;
import stans.resourcerecord.dao.CacheLoader;
import stans.db.Enumerators;
import stans.db.Enumerators.BBComparisonOperator;
import stans.resourcerecord.dao.FlagLoader;
import stans.resourcerecord.dao.RecommendationLoader;
import stans.resourcerecord.dao.ResourceLoader;
import stans.resourcerecord.dao.ResourceTextLoader;
import stans.resourcerecord.dao.TagLoader;
import stans.resourcerecord.dao.TagTypeLoader;
import stans.resourcerecord.helpers.ValidationHelpers;
import stans.resourcerecord.model.Resource;
import stans.resourcerecord.model.Tag;
import stans.resourcerecord.model.TagType;
import stans.db.Query;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jwatson
 */
public class SearchResources extends HttpServlet {

    public enum SearchType {

        TAG, RESOURCE, SURVEY, CURRICULUM_RESOURCE, RECENT, TEST, FLAG, RECOMMENDATION, RESOURCETEXT, ADVANCED
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, PersistenceException {

        SearchType searchType = SearchType.valueOf(request.getParameter("searchType").toUpperCase());
        ArrayList<Resource> searchResults = new ArrayList<Resource>();

        
        switch (searchType) {
            case TAG:
                searchResults = tagSearch(request);
                break;
            case RESOURCE:
                searchResults = resourceSearch(request);
                break;
            case SURVEY:
                searchResults = resourceSearch(request);
                break;
            case CURRICULUM_RESOURCE:
                searchResults = curriculumResourceSearch(request);
                break;
            case FLAG:
                searchResults = flagSearch(request);
                break;
            case RECOMMENDATION:
                searchResults = recommendationSearch(request);
                break;
            case RESOURCETEXT:
                searchResults = resourceTextSearch(request);
                break;
            case RECENT:
                searchResults = recentSearch(request);
                break;
            case TEST:
                searchResults = testSearch(request);
                break;
            case ADVANCED:
                // take in any number of params from any of the above 
                // searches, and/or them together (based on further params)
                // and return the results
                // todo
                break;
        }

        //make sure results are unique
        /*LinkedHashSet<Resource> hs = new LinkedHashSet<Resource>();
        hs.addAll(searchResults);
        searchResults.clear();
        searchResults.addAll(hs);*/

        String is_public = request.getParameter("is_public");
        if (is_public == null)
        { is_public = "false"; }
        
    // FILTERS
        if (is_public.equals("true")) 
        {
            ArrayList<Tag> searchFilters = new ArrayList<Tag>();
            LinkedHashSet<Integer> hf = new LinkedHashSet<Integer>(); // for eliminating dupes
            ArrayList<String> allowed_tagtypes = new ArrayList<String>();
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
            
            ArrayList<Resource> temp_searchResults = new ArrayList<Resource>();
            //System.out.println("Search of type " + searchType.toString() + " returned the following " + searchResults.size() + "results: ");
            for (Resource r : searchResults)
            {
                //System.out.println("-" + r.getRNumber());
                //if ((r.getParent() == null) && (r.getFinalRecommendation() == 1))
                if (r.getFinalRecommendation() == 1)
                {
                    temp_searchResults.add(r);
                    
                    for (Tag t: r.getTags())
                    {
                        if (allowed_tagtypes.contains(t.getType()))
                        { 
                            if (!hf.contains(t.getDBID()))
                            {
                                hf.add(t.getDBID());
                                searchFilters.add(t);
                            } 
                        }
                    }
                }
            }
            
            searchResults.clear();
            searchResults.addAll(temp_searchResults);

            request.setAttribute("searchFilters", searchFilters);
        }
        
        request.setAttribute("searchResults", searchResults);
        
        if (searchType == SearchType.CURRICULUM_RESOURCE)
        {
            request.getRequestDispatcher("/curriculum_resource_list.jsp").forward(request, response);
            return;
        }
        if (searchType == SearchType.SURVEY)
        {
            request.getRequestDispatcher("/survey_dashboard.jsp").forward(request, response);
            return;
        }
        if (is_public.equals("true"))
        {
            request.getRequestDispatcher("/resource_dashboard_public.jsp").forward(request, response);
            return;
        }
        else
        {
            request.getRequestDispatcher("/resource_dashboard.jsp").forward(request, response);
            return;
        }

    }

    private ArrayList<Resource> tagSearch(HttpServletRequest request) {
        String tagTypeName = request.getParameter("tagTypeName");
        String tagTypeValue = request.getParameter("tagTypeValue");
        String operator = request.getParameter("operator");
        ArrayList<Integer> cached_list = new ArrayList<Integer>();
        
        if (tagTypeName.equals("varies")) // for where we want to use multiple tag types with a select-based search
        {
            tagTypeName = tagTypeValue.split("::")[0];
            tagTypeValue = tagTypeValue.split("::")[1];
        }
        
        if (tagTypeName.equals("Subject")) // load from cache, if available
        {
            cached_list = CacheLoader.getCachedListByName("subject=" + tagTypeValue);            
        }
        
        if (cached_list.size() > 0) // load from cache returned results
        {
            System.out.println("Loading from cache");
            
            ArrayList<Resource> cached_resources = new ArrayList<Resource>();
            
            for (Integer res_id : cached_list)
            {
                cached_resources.add(new Resource(res_id));
            }
            
            return cached_resources;
        }
        else
        {
            System.out.println("Performing Tag Search");
            System.out.println("tagTypeName:  " + tagTypeName);
            System.out.println("operator:     " + operator);
            System.out.println("tagTypeValue: " + tagTypeValue);
            System.out.println("");

            // tags are a little different then the searches below, in the case of a tag we only ever care about
            // the type/value pair
            return ResourceLoader.loadByTags(TagLoader.loadByTagTypeNameAndTagValueAndOperator(tagTypeName, tagTypeValue, Enumerators.BBComparisonOperator.valueOf(operator.toUpperCase())));
        }
    }

    private ArrayList<Resource> resourceSearch(HttpServletRequest request) throws PersistenceException {
        String fieldName = request.getParameter("fieldName");
        String fieldValue = request.getParameter("fieldValue");
        String operator = request.getParameter("operator");
        System.out.println("Performing Resource Search");
        System.out.println("fieldName:  " + fieldName);
        System.out.println("operator:     " + operator);
        System.out.println("fieldValue: " + fieldValue);
        System.out.println("");

        // Add R to beginning of RNumber if it's not there
        if ((fieldName.equals("entry_id")) && (!fieldValue.startsWith("R")))
        { fieldValue = "R" + fieldValue; }
        
        //created_by usernames arent stored in the resource table (its just a FK), they need a special call
        if (fieldName.equals("created_by")) {
            return ResourceLoader.loadByCreatedByUsernameAndOperator(fieldValue, Enumerators.BBComparisonOperator.valueOf(operator.toUpperCase()));
        } else if ((fieldName.equals("entry_id")) && (operator.equals("equals"))) { // to speed up R Number find
            ArrayList<Resource> rtn = new ArrayList<Resource>();
            rtn.add(ResourceLoader.loadByRNumber(fieldValue));
            return rtn;
        } else {
            return ResourceLoader.loadByFieldNameAndFieldValueAndOperator(fieldName, fieldValue, Enumerators.BBComparisonOperator.valueOf(operator.toUpperCase()));
        }
    }

    private ArrayList<Resource> curriculumResourceSearch(HttpServletRequest request) throws PersistenceException {
        ArrayList<String> level_list = new ArrayList<String>();
        String subject = request.getParameter("subject");
        String level = request.getParameter("level");
		Boolean KindergartenSearch = false;
		Boolean PrekindergartenSearch = false;
		
		if(
			( ( level!=null )&&( level.equals("Kindergarten") ) ) ||
			( ( subject!=null )&&( subject.equals("Kindergarten") ) )
		) {
			level = "Kindergarten"; // just in case subject==kindergarten but level is empty
			if( ( subject==null )||( subject.equals("") ) ) {
				KindergartenSearch = true; // if subject is blank but level==kindergarten, then search separately in both level and subject field for kindergarten
			}
		}
		if(
			( ( level!=null )&&( level.equals("Prekindergarten") ) ) ||
			( ( subject!=null )&&( subject.equals("Prekindergarten") ) )
		) {
			level = "Prekindergarten";
			PrekindergartenSearch = true;
		}
		
        if (level != null)
        {
            level_list.add(level);
        
            if ((level.equals("30")) && (subject.toUpperCase().equals("ENGLISH LANGUAGE ARTS")))
            {
                level_list.add("A30");
                level_list.add("B30");
            }
            if (level.equals("10, 20, 30"))
            {
                level_list.remove("10, 20, 30");
                level_list.add("10");
                level_list.add("20");
                level_list.add("30");
            }
        }
        
        String resource_or_support = request.getParameter("resource_or_support");
        if ((resource_or_support == null) || (resource_or_support.isEmpty()))
        { resource_or_support = "both"; }
        
        String fran_or_imm = request.getParameter("fran_or_imm");
        if ((fran_or_imm == null) || (fran_or_imm.isEmpty()))
        { fran_or_imm = "both"; }
        
        ArrayList<Resource> rtn_list = new ArrayList<Resource>();
        HashMap<String, Boolean> rtn_dupes = new HashMap<String, Boolean>();
        
        for (String this_level : level_list)
        {
			if( ( KindergartenSearch )||( PrekindergartenSearch ) ) {
				Tag subjectTag = TagLoader.loadByTagTypeAndTagValue( level, TagTypeLoader.loadByType( "Subject" ) );
				Tag levelTag = TagLoader.loadByTagTypeAndTagValue( level, TagTypeLoader.loadByType( "Level" ) );
				for (Resource r : ResourceLoader.loadByTag(subjectTag) )
				{
					if (!rtn_dupes.containsKey(r.getRNumber()))
					{
						rtn_list.add(r);
						rtn_dupes.put(r.getRNumber(), Boolean.TRUE);
					}
				}
				for (Resource r : ResourceLoader.loadByTag(levelTag) )
				{
					if (!rtn_dupes.containsKey(r.getRNumber()))
					{
						rtn_list.add(r);
						rtn_dupes.put(r.getRNumber(), Boolean.TRUE);
					}
				}
			} else {
				ArrayList<Tag> tags_to_search = new ArrayList<Tag>();

				if (
						(!this_level.equals("Prekindergarten")) &&
						((subject != null) && (!subject.equals("")))
				)
				{ tags_to_search.addAll(TagLoader.loadByTagTypeNameAndTagValueAndOperator("Subject", subject, BBComparisonOperator.EQUALS)); }
				tags_to_search.addAll(TagLoader.loadByTagTypeNameAndTagValueAndOperator("Level", this_level, BBComparisonOperator.EQUALS));
				tags_to_search.addAll(TagLoader.loadByTagTypeNameAndTagValueAndOperator("Grade", this_level, BBComparisonOperator.EQUALS));

				if (fran_or_imm.equals("fran"))
				{ tags_to_search.addAll(TagLoader.loadByTagTypeNameAndTagValueAndOperator("Program", "Fransaskois", BBComparisonOperator.EQUALS)); }
				if (fran_or_imm.equals("imm"))
				{ tags_to_search.addAll(TagLoader.loadByTagTypeNameAndTagValueAndOperator("Program", "Immersion", BBComparisonOperator.EQUALS)); }
				// if we just want one or the other, iterate over the results and check each one and only add it to rtn_list if it has support material tag (or not)
				// otherwise, just return the whole results list
				if (!resource_or_support.equals("both"))
				{ 
					for (Resource r : ResourceLoader.loadByTagGroupContent(tags_to_search))
					{
						if (r.isSupportMaterial())
						{
							if (resource_or_support.equals("support"))
							{
								if (!rtn_dupes.containsKey(r.getRNumber()))
								{
									rtn_list.add(r);
									rtn_dupes.put(r.getRNumber(), Boolean.TRUE);
								}
							}
						}
						else
						{
							if (resource_or_support.equals("resource"))
							{
								if (!rtn_dupes.containsKey(r.getRNumber()))
								{
									rtn_list.add(r);
									rtn_dupes.put(r.getRNumber(), Boolean.TRUE);
								}
							}
						}
					}
				}
				else
				{
					for (Resource r : ResourceLoader.loadByTagGroupContent(tags_to_search))
					{
						if (!rtn_dupes.containsKey(r.getRNumber()))
						{
							rtn_list.add(r);
							rtn_dupes.put(r.getRNumber(), Boolean.TRUE);
						}
					}
				}
	        }
        }
        
        return rtn_list;
    }
    
    private ArrayList<Resource> flagSearch(HttpServletRequest request) throws PersistenceException {
        String flagFieldName = request.getParameter("flagFieldName");
        String flagFieldValue = request.getParameter("flagFieldValue");
        String operator = request.getParameter("operator");
        System.out.println("Performing Flag Search");
        System.out.println("flagFieldName:  " + flagFieldName);
        System.out.println("operator:     " + operator);
        System.out.println("flagFieldValue: " + flagFieldValue);
        System.out.println("");
        //created_by usernames arent stored in the resource table (its just a FK), they need a special call
        if (flagFieldName.equals("user_id")) {
            return ResourceLoader.loadByFlags(FlagLoader.loadByUserIdUsernameAndOperator(flagFieldValue, Enumerators.BBComparisonOperator.valueOf(operator.toUpperCase())));
        } else {
            return ResourceLoader.loadByFlags(FlagLoader.loadByFieldNameAndFieldValueAndOperator(flagFieldName, flagFieldValue, Enumerators.BBComparisonOperator.valueOf(operator.toUpperCase())));
        }
    }

    private ArrayList<Resource> recommendationSearch(HttpServletRequest request) {
        String recommendationFieldName = request.getParameter("recommendationFieldName");
        String recommendationFieldValue = request.getParameter("recommendationFieldValue");
        String operator = request.getParameter("operator");
        System.out.println("Performing Recommendation Search");
        System.out.println("recommendationFieldName:  " + recommendationFieldName);
        System.out.println("operator:     " + operator);
        System.out.println("recommendationFieldValue: " + recommendationFieldValue);
        System.out.println("");
        //created_by usernames arent stored in the resource table (its just a FK), they need a special call
        if (recommendationFieldName.equals("created_by")) {
            return ResourceLoader.loadByRecommendations(RecommendationLoader.loadByCreatedByUsernameAndOperator(recommendationFieldValue, Enumerators.BBComparisonOperator.valueOf(operator.toUpperCase())));
        } else {
            return ResourceLoader.loadByRecommendations(RecommendationLoader.loadByFieldNameAndFieldValueAndOperator(recommendationFieldName, recommendationFieldValue, Enumerators.BBComparisonOperator.valueOf(operator.toUpperCase())));
        }
    }

    private ArrayList<Resource> resourceTextSearch(HttpServletRequest request) {
        String resourceTextFieldName = request.getParameter("resourceTextFieldName");
        String resourceTextFieldValue = request.getParameter("resourceTextFieldValue");
        String resourceTextFieldType = request.getParameter("resourceTextFieldType"); // annotation, order notes, etc.
        String operator = request.getParameter("operator");
        System.out.println("Performing ResourceText Search");
        System.out.println("resourceTextFieldName:  " + resourceTextFieldName);
        System.out.println("operator:     " + operator);
        System.out.println("resourceTextFieldValue: " + resourceTextFieldValue);
        System.out.println("");
        //created_by usernames arent stored in the resource table (its just a FK), they need a special call
        if (resourceTextFieldName.equals("created_by")) {
            return ResourceLoader.loadByResourceTexts(ResourceTextLoader.loadByCreatedByUsernameAndOperator(resourceTextFieldValue, Enumerators.BBComparisonOperator.valueOf(operator.toUpperCase())));
        } else {
            if (resourceTextFieldType != null)
            {
                return ResourceLoader.loadByResourceTexts(
                        ResourceTextLoader.loadByFieldNameAndFieldValueAndFieldTypeAndOperator(resourceTextFieldName, resourceTextFieldValue, resourceTextFieldType, Enumerators.BBComparisonOperator.valueOf(operator.toUpperCase())));
            }
            else
            {
                return ResourceLoader.loadByResourceTexts(ResourceTextLoader.loadByFieldNameAndFieldValueAndOperator(resourceTextFieldName, resourceTextFieldValue, Enumerators.BBComparisonOperator.valueOf(operator.toUpperCase())));
            }                
        }
    }

    private ArrayList<Resource> recentSearch(HttpServletRequest request) {
        String limit_string = request.getParameter("limit");
        int limit = 10;
        if (ValidationHelpers.isPositiveInteger(limit_string))
        { limit = Integer.parseInt(limit_string); }
        if (limit > 50)
        { limit = 50; }
        System.out.println("Performing Recent Search");
        System.out.println("Limit:  " + limit);
        System.out.println("");

        return ResourceLoader.loadRecent(limit);
    }

    private ArrayList<Resource> testSearch(HttpServletRequest request) throws PersistenceException {
        String operator = request.getParameter("operator");
        Enumerators.BBComparisonOperator bbOperator = Enumerators.BBComparisonOperator.valueOf(operator.toUpperCase());
        System.out.println("Performing Test Search");
        System.out.println("Operator:  " + operator);
        System.out.println("");

        // in the furture it'd be nice to be able to search by question text, description, instructions, etc
        // this would require a large testloader api 
        switch (bbOperator) {
            case NOTBLANK:
                return ResourceLoader.loadWithTests();
            case BLANK:
                return ResourceLoader.loadWithoutTests();
        }

        return null;
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (PersistenceException ex) {
            Logger.getLogger(SearchResources.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (PersistenceException ex) {
            Logger.getLogger(SearchResources.class.getName()).log(Level.SEVERE, null, ex);
        }
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
