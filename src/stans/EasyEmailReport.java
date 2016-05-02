package stans;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Stack;

/**
 *
 * @author jwatson
 */
public class EasyEmailReport {
    private String report_from;
    private String report_to;
    private String report_replyTo;
    private String report_cc;
    public String report_width;
    public boolean contains_ministry_logo = false;
    public String user_number;

    
    //Nicole - Added ADOBECONNECT_REGISTRATION
    public enum Format {
        PROBLEM_REPORT, ACCOUNT_REQUEST, COURSE_REQUEST, ENROLLMENT_REQUEST, TEMP_ACCOUNT, CUSTOM, RAW, LIVE_EVENT_REGISTRATION, ADOBE_CONNECT_REGISTRATION
    }
    public Format emailFormat;
    public Stack<String> reportContent = new Stack<String>();    
    private EasyEmailReport reportReceipt = null;
    
    private String bodyTextColor;
    private String bodyBackgroundColor;
    private String bodyBorderColor;
    private String tableTextColor;
    private String tableBackgroundColor;
    private String tableBorderColor;
    private String listTextColor;
    private String listBackgroundColor;
    private String listBorderColor;
    private String report_body;
    private String report_subject;
    private String user_name;
    private String user_bbLogin;
    private String user_school;
    private String user_division;
    private String user_ip;
    private String user_email;

    public EasyEmailReport() {
        emailFormat = Format.CUSTOM;
        report_width = "500px";
    }
    
    //report format methods (basically receipes for different reports)
    //custom format methods / helper methods used by the receipes above
    public void setReportColorScheme(String bodyTextColor, String bodyBackgroundColor, String bodyBorderColor, String tableTextColor, String tableBackgroundColor, String tableBorderColor, String listTextColor, String listBackgroundColor, String listBorderColor) {
        this.bodyTextColor = bodyTextColor;
        this.bodyBackgroundColor = bodyBackgroundColor;
        this.bodyBorderColor = bodyBorderColor;

        this.tableTextColor = tableTextColor;
        this.tableBackgroundColor = tableBackgroundColor;
        this.tableBorderColor = tableBorderColor;

        this.listTextColor = listTextColor;
        this.listBackgroundColor = listBackgroundColor;
        this.listBorderColor = listBorderColor;
    }
    public void setReportTitle(String title) {
        //random cid for logo:
        // ministry_email_icon_6745231zxc
        contains_ministry_logo = true;
        reportContent.push("<tr><td width='20'><br/></td><td><br/></td><td width='20'><br/></td></tr><tr><td width='20'></td><td><table cellpadding='0' cellspacing='0'  border='0'><tr><td><h3 style='color:white;'>" + title + "</h3></td><td width='20'></td><td style='background-color:white; padding-right:10px;'><img style='width:200px;' src='cid:ministry_email_icon_6745231zxc'/></td></tr></table></td><td width='20'></td></tr>");
    }
    public void setReportDescription(String description) {
        addReportTextData("Report Description", description);
    }
    public void addReportListData(String sectionTitle, String[] rowNames, String[] listData) {
        reportContent.push(wrapInTr("<h3>"+sectionTitle+"</h3>"));
        String finishedListData = "<table style=' /**ListStyle**/ width:100%;'>";
        for (int i = 0; i < rowNames.length; i++) {
            finishedListData += "<tr><td></td><td><b>" + rowNames[i] + ":</b></td>";
            if (listData[i] != null) {
                finishedListData += "<td>" + listData[i] + "</td><td></td></tr>";
            } else {
                finishedListData += "</tr>";
            }
        }
        finishedListData += "</table>";
        reportContent.push(wrapInTr(finishedListData));
    }
    public void addReportTabularData(String sectionTitle, String[] columnNames, String[][] tableData) {
        reportContent.push(wrapInTr(sectionTitle));
        String finishedTabularData = "<table style=' /**TableStyle**/ width:100%;'>";

        if (columnNames != null) {
            finishedTabularData += "<tr><td></td>";
            for (int i = 0; i < columnNames.length; i++) {
                finishedTabularData += "<td><b>" + columnNames[i] + "</b></td>";
            }
            finishedTabularData += "<td></td></tr>";
        }
        if (tableData != null) {
            for (int i = 0; i < tableData.length; i++) {
                finishedTabularData += "<tr><td></td>";
                for (int j = 0; j < tableData[i].length; j++) {
                    finishedTabularData += "<td>" + tableData[i][j] + "</td>";
                }
                finishedTabularData += "<td></td></tr>";
            }
        }
        finishedTabularData += "</table>";
        reportContent.push(wrapInTr(finishedTabularData));
    }
    public void addReportTextData(String sectionTitle, String data) {
        reportContent.push(wrapInTr("<h3>" + sectionTitle + "</h3>" + data));
    }
    public void addReportSpacer() {
        reportContent.push("<tr><td height='20'><br/></td></tr>");
    }
    public void addReportHr() {
        reportContent.push(wrapInTr("<hr/>"));
    }
    
    //sets the user data common to most reports and builds the list for it (all of the above use it)
    public void addCommonReportData(String n, String bbl, String bbemail, String s, String d, String i) {
        user_name = n;
        user_bbLogin = bbl;
        user_school = s;
        user_division = d;
        user_ip = i;
        user_email = bbemail;

        
        
        String[] user_info_row_names = {"Name", "BB Username", "Email", "School", "Division", "IP", "Timestamp"};
        String[] user_info_data = {user_name, user_bbLogin, "<a style='color:white ' href='mailto:"+user_email+"'>"+user_email+"</a>", user_school, user_division, user_ip, getTimestamp()};
        addReportListData("User Info:", user_info_row_names, user_info_data);
    }
    void addCommonReportData(String n, String bbl, String bbemail, String s, String d, String i, String number) {
         user_name = n;
        user_bbLogin = bbl;
        user_school = s;
        user_division = d;
        user_ip = i;
        user_email = bbemail;
        user_number = number;

        
        
        String[] user_info_row_names = {"Name", "BB Username", "Email", "School", "Division", "IP", "SID/TSN", "Timestamp"};
        String[] user_info_data = {user_name, user_bbLogin, "<a style='color:white ' href='mailto:"+user_email+"'>"+user_email+"</a>", user_school, user_division, user_ip, user_number, getTimestamp()};
        addReportListData("User Info:", user_info_row_names, user_info_data);
    }
          
    public String getTimestamp(){
        Date now = new Date();
        String timestamp = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(now);
        return timestamp;
    }
    
    // this is the text added to to the top of the email when a copy is sent to the original user
    //it is passed in as a report for simplicity and code reuse
    public void setReceipt(EasyEmailReport receipt){       
        reportReceipt = receipt;         
    }       
    public EasyEmailReport getReceipt(){
        return reportReceipt;
    }        
    
    public void setSubject(String subject) {
        report_subject = subject;
    }        
    public void setTo(String to) {
        report_to = to;
    }
    public void setCc(String cc) {
        report_cc = cc;
    }
    public void setReplyTo(String replyTo) {
        report_replyTo = replyTo;
    }
    public void setFrom(String from) {
        report_from = from;
    }
   
    public String getBody() {
        compileReport();
        return report_body;
    }
    
    public String getSubject(){
        return report_subject;
    }    
    public String getTo(){
        return report_to;
    }
    public String getCc(){
        return report_cc;
    }
    public String getReplyTo(){
        return report_replyTo;
    }
    public String getFrom(){
        return report_from;        
    }
   

    

    //helpers
    private String wrapInTr(String text) {
        return "<tr><td width='20'></td><td><br/>" + text + "</td><td width='20'></td></tr>";
    }
    private void compileReport() {
        String finishedReport = "";
        if (reportContent.size() > 0) {
            finishedReport += "<table cellpadding='0' cellspacing='0'  border='0' style=' /**BodyStyle**/ width:"+ report_width +";'>";
            Collections.reverse(reportContent);
            while (reportContent.size() > 0) {
                finishedReport += reportContent.pop();
            }
            finishedReport += "</table><br/>";

        } else {
            finishedReport = "";
        }

        finishedReport = applyColorScheme(finishedReport);

        report_body = finishedReport;

    }
    private String applyColorScheme(String content) {

        String bodyStyle = "background-color:" + bodyBackgroundColor + "; color:" + bodyTextColor + "; border: solid thick " + bodyBorderColor + "; ";
        String tableStyle = "background-color:" + tableBackgroundColor + "; color:" + tableTextColor + "; border: solid thin " + tableBorderColor + "; ";
        String listStyle = "background-color:" + listBackgroundColor + "; color:" + listTextColor + "; border: solid thin " + listBorderColor + "; ";

        content = content.replaceAll("(?s)\\/\\*\\*BodyStyle\\*\\*/", bodyStyle);
        content = content.replaceAll("(?s)\\/\\*\\*TableStyle\\*\\*/", tableStyle);
        content = content.replaceAll("(?s)\\/\\*\\*ListStyle\\*\\*/", listStyle);

        return content;
    }
}
