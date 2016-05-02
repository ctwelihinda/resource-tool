/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.resourcerecord.servlet.searchAndList;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import stans.db.Query;
import stans.resourcerecord.model.Resource;

/**
 *
 * @author peter
 */
public class QuickFind extends HttpServlet {

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
        
        String field_name = request.getParameter("field_name");
        String field_value = request.getParameter("field_value");
        
        if ((field_name.equals("entry_id")) && !(field_value.startsWith("R")))
        { field_value = "R" + field_value; }
        
        try {
            
            ArrayList<String> args = new ArrayList<String>();
            args.add(field_value);
            String constraints = "";
            if (field_name.equals("created_by"))
            { constraints = "created_by = ?"; }
            else if (field_name.equals("entry_id"))
            { constraints = "entry_id = ?"; }
            else
            { constraints = "entry_id = ?"; }
            
            ArrayList<Integer> result_ids = Query.find("moe_resource", constraints, args);
            if (!result_ids.isEmpty())
            {
                Resource this_res = new Resource(result_ids.get(0));

                String this_title = this_res.getTitle();
                String this_desc = this_res.getQuickData("desc");
                String this_pic = this_res.getQuickData("image");
                String this_info = this_res.getQuickData("info");
                String this_rnumber = this_res.getRNumber();
                String created_by = this_res.getCreatedByFullName();
                String created_at = new SimpleDateFormat("EEE, MMM d, yyyy").format(this_res.getCreatedAt());

                String sur_edit_path = "#";
                String sur_options_path = "#";
                String sur_ar_path = "#";
                String sur_aggres_path = "#";
                String sur_indres_path = "#";
                
                String parent_id = "";
                String parent_title = "";
                
                String live_status = Integer.toString(this_res.getFinalRecommendation());
                
                Resource parent = this_res.getParent();
                if (parent != null)
                {
                    parent_id = Integer.toString(parent.getDBID());
                    parent_title = parent.getTitle();
                }
                
                if (this_res.getTest() != null)
                {
                    sur_edit_path = this_res.getTest().shortcuts.getEditPath();
                    sur_options_path = this_res.getTest().shortcuts.getEditOptionsPath();
                    sur_ar_path = this_res.getTest().shortcuts.getAdaptiveReleasePath();
                    sur_aggres_path = this_res.getTest().shortcuts.getAggregatedResultsPath();
                    sur_indres_path = this_res.getTest().shortcuts.getResultsPath();
                }

                if (this_title != null)
                { this_title = this_title.replace("\"", "\\\""); }
                if (this_desc != null)
                { this_desc = this_desc.replace("\"", "\\\"").replace("\n", "").replace("\r", "").replace(String.valueOf((char) 0x0a), ""); }
                
                out.println("{\"resources\":[");
                    out.println("{\"db_id\":\""
                        + result_ids.get(0)
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
                        + "\", \"created_by\":\""
                        + created_by
                        + "\", \"created_at\":\""
                        + created_at
                        + "\", \"sur_edit_path\":\""
                        + sur_edit_path
                        + "\", \"sur_options_path\":\""
                        + sur_options_path
                        + "\", \"sur_ar_path\":\""
                        + sur_ar_path
                        + "\", \"sur_aggres_path\":\""
                        + sur_aggres_path
                        + "\", \"sur_indres_path\":\""
                        + sur_indres_path
                        + "\", \"parent_id\":\""
                        + parent_id
                        + "\", \"parent_title\":\""
                        + parent_title
                        + "\", \"live_status\":\""
                        + live_status
                    + "\"}");
                out.println("]}");
            }
            else
            {
                out.println("{\"resources\":[]}");
            }
            
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
