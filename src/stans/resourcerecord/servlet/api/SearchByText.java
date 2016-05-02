/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.db.Query;
import stans.db.Enumerators;
import stans.resourcerecord.model.Resource;

/**
 *
 * @author peter
 */
public class SearchByText extends HttpServlet {

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
        
        String title_to_search = request.getParameter("title");
        String sa = request.getParameter("search_all");
        String rover_only = request.getParameter("rover_only");
        
        boolean search_all = false;
        if ((sa != null) && (sa.equals("true")))
        { search_all = true; }
        
        if (title_to_search == null)
        { title_to_search = "Les sous-marins"; }
        
        ArrayList<Integer> title_search_results = null;
        ArrayList<Integer> desc_search_results = null;
        if (rover_only != null)
        {
            String title_search_constraints = "lower(quick_title) LIKE ? AND lower(quick_info) LIKE ?";
            String desc_search_constraints = "lower(quick_description) LIKE ? AND lower(quick_info) LIKE ?";
            
            ArrayList<String> search_args = new ArrayList<String>();
            search_args.add("%" + title_to_search.toLowerCase() + "%");
            search_args.add("%rover%");
            
            title_search_results = Query.find("moe_resource", title_search_constraints, search_args);
            desc_search_results = Query.find("moe_resource", desc_search_constraints, search_args);
        }
        else
        { 
            title_search_results = Query.findWithOperator("moe_resource", "quick_title", title_to_search, Enumerators.BBComparisonOperator.CONTAINS); 
            desc_search_results = Query.findWithOperator("moe_resource", "quick_description", title_to_search, Enumerators.BBComparisonOperator.CONTAINS);
        }
        
        
        
        HashMap<Integer, Integer> result_ids = new HashMap<Integer, Integer>();
        for (Integer r_id : title_search_results)
        {
            result_ids.put(r_id, 1); // maybe actually increment later, but it's kind of inefficient in Java
        }
        for (Integer r_id : desc_search_results)
        {
            result_ids.put(r_id, 1);        
        }
        
        try {
            boolean first = true;
            out.println("{\"resources\":[");
            
            Iterator it = result_ids.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry<Integer, Integer> result_pairs = (Map.Entry<Integer, Integer>)it.next();
                
                Integer result_id = result_pairs.getKey();
                Resource this_res = new Resource(result_id);
                Integer is_live = this_res.getFinalRecommendation();
                
                if ((is_live == 1) || (search_all))
                {
                    if (!first)
                    { out.println(","); }
                    else
                    { first = false; }

                    String this_title = this_res.getTitle();
                    String this_desc = this_res.getQuickData("desc");
                    String this_pic = this_res.getQuickData("image");
                    String this_info = this_res.getQuickData("info");
                    String this_rnumber = this_res.getRNumber();

                    if (this_title != null)
                    { this_title = this_title.replace("\"", "\\\""); }
                    if (this_desc != null)
                    { this_desc = this_desc.replace("\"", "\\\"").replace("\n", "").replace("\r", "").replace(String.valueOf((char) 0x0a), ""); }

                    out.println("{\"db_id\":\""
                            + result_id
                            + "\", \"title\":\""
                            + this_title
                            + "\", \"description\":\""
                            + this_desc
                            + "\", \"rnumber\":\""
                            + this_rnumber
                            + "\", \"pic_url\":\""
                            + this_pic
                            + "\", \"info\":\""
                            + this_info
                            + "\"}");
                }
            }
            //out.println("{\"title\":\"json test 1\", \"description\":\"json test 1 desc\", \"pic_url\":\"https://scontent-a-sea.xx.fbcdn.net/hphotos-xpa1/v/t1.0-9/10336713_602434626531279_4459806218008741669_n.jpg?oh=787bff7a9625a0123dedd561e3d98ebb&oe=5504A5EF\", \"info\":\"info 1\"},");
            //out.println("{\"title\":\"json test 2\", \"description\":\"json test 2 desc\", \"pic_url\":\"http://newstalk650.com/sites/default/files/news-image/936810_10151605530572139_2133682416_n.jpg\", \"info\":\"info 2\"}");
            out.println("]}");
        } catch (Exception e) {
                System.out.println("QuickSearch servlet: Error\n");
                e.printStackTrace(System.out);
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
