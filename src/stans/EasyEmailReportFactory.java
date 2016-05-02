package stans;

import java.util.ArrayList;
import java.util.List;

public class EasyEmailReportFactory
{
  public static String problemReportPrimary = "#8C001A";
  public static String problemReportSecondary = "#C24641";
  public static String accountRequestPrimary = "#4863A0";
  public static String accountRequestSecondary = "#98AFC7";
  public static String courseRequestPrimary = "#299052";
  public static String courseRequestSecondary = "#8BB381";
  public static String enrollmentRequestPrimary = "#299052";
  public static String enrollmentRequestSecondary = "#8BB381";
  public static String liveEventRegistrationPrimary = "#A9A9A9";
  public static String liveEventRegistrationSecondary = "#808080";
  public static String adobeConnectRegistrationPrimary = "#6B47B2";
  public static String adobeConnectRegistrationSecondary = "#B5A3D8";
    public static String tempAccountPrimary = "#EB9100";
    public static String tempAccountSecondary = "#F59B00";
  private static EasyEmailReport report = new EasyEmailReport();
  private static String helpDesk = "NetworkServices@gov.sk.ca";
  
  private static EasyEmailReport.Format emailFormat;
  
  public static EasyEmailReport createProblemReport(String problemSubject, String problemDescription, String name, String email, String bbLogin, String school, String division, String ip)
  {
    emailFormat = EasyEmailReport.Format.PROBLEM_REPORT;
    
    report.setTo(helpDesk);
    report.setReplyTo(email);
    report.setFrom("ReportProblem@mail.edonline.sk.ca");
    
    report.setReceipt(CreateProblemReportReceipt(email));
    
    report.setSubject("Problem Report From: " + name);
    report.setReportTitle("Sector Technologies and Network Services: <br/><u>Problem Report</u>");
    report.addCommonReportData(name, bbLogin, email, school, division, ip);
    report.addReportHr();
    report.addReportTextData("Problem Subject:", problemSubject);
    report.addReportTextData("Problem Description:", problemDescription);
    report.addReportSpacer();
    
    report.setReportColorScheme("white", problemReportPrimary, "black", "black", problemReportSecondary, "black", "black", problemReportSecondary, "black");
    return report;
  }
  
  private static EasyEmailReport CreateProblemReportReceipt(String email)
  {
    EasyEmailReport problemReportReceipt = new EasyEmailReport();
    
    problemReportReceipt.setTo(email);
    problemReportReceipt.setReplyTo(helpDesk);
    problemReportReceipt.setFrom("ReportProblem@mail.edonline.sk.ca");
    
    problemReportReceipt.setSubject("RE: EDonline Problem Report");
    problemReportReceipt.setReportTitle("You have submitted a problem to Network Services");
    problemReportReceipt.addReportTextData("", "The following problem report has been sent to the Network Services Support Desk on your behalf. You will be contacted by a member of the support desk who will assist you with this problem within the next 24 hours.");
    problemReportReceipt.addReportTextData("", "If you are not contacted in this time please contact the support desk directly at <a style='color:white;' href='mailto:NetworkServices@gov.sk.ca'>NetworkServices@gov.sk.ca</a> or by phone at: <span style='color:white'><u>1-866-933-8333</u></span>");
    problemReportReceipt.addReportSpacer();
    
    problemReportReceipt.setReportColorScheme("white", problemReportPrimary, "black", "white", problemReportSecondary, "black", "white", problemReportSecondary, "black");
    return problemReportReceipt;
  }
  
  public static EasyEmailReport createAccountRequest(String requestedRoles, String specialRequests, String name, String email, String school, String division, String ip, String number)
  {
    emailFormat = EasyEmailReport.Format.ACCOUNT_REQUEST;
    
    report.setTo(helpDesk);
    report.setReplyTo(email);
    report.setFrom("RequestAccount@mail.edonline.sk.ca");
    
    report.setReceipt(CreateAccountRequestReceipt(email));
    
    report.setSubject("Account Request From: " + name);
    report.setReportTitle("Sector Technologies and Network Services: <br/><u>Account Request</u>");
    report.addCommonReportData(name, "N/A", email, school, division, ip, number);
    report.addReportHr();
    report.addReportTextData("Requested Role(s):", requestedRoles);
    report.addReportTextData("Notes/Special Requests:", specialRequests);
    report.addReportSpacer();
    
    report.setReportColorScheme("white", accountRequestPrimary, "black", "black", accountRequestSecondary, "black", "black", accountRequestSecondary, "black");
    return report;
  }
  
  private static EasyEmailReport CreateAccountRequestReceipt(String email)
  {
    EasyEmailReport accountRequestReceipt = new EasyEmailReport();
    
    accountRequestReceipt.setTo(email);
    accountRequestReceipt.setReplyTo(helpDesk);
    accountRequestReceipt.setFrom("RequestAccount@mail.edonline.sk.ca");
    
    accountRequestReceipt.setSubject("RE: EDonline Account Request");
    accountRequestReceipt.setReportTitle("You have requested an account from Network Services");
    accountRequestReceipt.addReportTextData("", "The following account request has been sent to the Network Services Support Desk on your behalf. You will be contacted by a member of the support desk who will assist you with this problem within the next 24 hours.");
    accountRequestReceipt.addReportTextData("", "If you are not contacted in this time please contact the support desk directly at <a style='color:white;' href='mailto:NetworkServices@gov.sk.ca'>NetworkServices@gov.sk.ca</a> or by phone at: <span style='color:white'><u>1-866-933-8333</u></span>");
    accountRequestReceipt.addReportSpacer();
    
    accountRequestReceipt.setReportColorScheme("white", accountRequestPrimary, "black", "black", accountRequestSecondary, "black", "black", accountRequestSecondary, "black");
    return accountRequestReceipt;
  }
  
  public static EasyEmailReport createCourseRequest(String courseName, String gradeLevels, String courseOrOrg, Enrollment[] enrollments, String specialRequests, String name, String email, String bbLogin, String school, String division, String ip)
  {
    emailFormat = EasyEmailReport.Format.ACCOUNT_REQUEST;
    
    report.setTo(helpDesk);
    report.setReplyTo(email);
    report.setFrom("RequestCourse@mail.edonline.sk.ca");
    
    report.setReceipt(CreateCourseRequestReceipt(email));
    
    report.setSubject("Course Request From: " + name);
    report.setReportTitle("Sector Technologies and Network Services: <br/> <u>Course/Org. Request</u>");
    report.addCommonReportData(name, bbLogin, email, school, division, ip);
    
    report.addReportHr();
    
    String[] course_info_labels = { "Course or Organization", "Course/Org Name", "Grade level(s)" };
    String[] course_info_values = { courseOrOrg, courseName, gradeLevels };
    report.addReportListData("Course/Org Info:", course_info_labels, course_info_values);
    
    String[] enrollment_columns = { "Name", "SID/TCN", "Role" };
    ArrayList<String[]> enrollments_array = new ArrayList();
    for (int i = 0; i < enrollments.length; i++)
    {
      Enrollment e = enrollments[i];
      if (((e.getFirstname() != null) && (!e.getFirstname().equals("null")) && (!e.getFirstname().equals(""))) || ((e.getLastname() != null) && (!e.getLastname().equals("null")) && (!e.getLastname().equals(""))))
      {
        String[] enrollment_array = new String[3];
        enrollment_array[0] = (e.getFirstname() + " " + e.getLastname());
        enrollment_array[1] = (e.getSid() + e.getTsn());
        enrollment_array[2] = e.getRole();
        enrollments_array.add(enrollment_array);
      }
    }
    report.addReportTabularData("Enrollments", enrollment_columns, (String[][])enrollments_array.toArray(new String[enrollments_array.size()][3]));
    

    report.addReportTextData("Notes/Special Requests:", specialRequests);
    report.addReportSpacer();
    
    report.setReportColorScheme("white", courseRequestPrimary, "black", "black", courseRequestSecondary, "black", "black", courseRequestSecondary, "black");
    return report;
  }
  
  private static EasyEmailReport CreateCourseRequestReceipt(String email)
  {
    EasyEmailReport courseRequestReceipt = new EasyEmailReport();
    
    courseRequestReceipt.setTo(email);
    courseRequestReceipt.setReplyTo(helpDesk);
    courseRequestReceipt.setFrom("RequestCourse@mail.edonline.sk.ca");
    
    courseRequestReceipt.setSubject("RE: EDonline Course Request");
    courseRequestReceipt.setReportTitle("You have requested a course or organization from Network Services");
    courseRequestReceipt.addReportTextData("", "The following course request has been sent to the Network Services Support Desk on your behalf. You will be contacted by a member of the support desk who will assist you with this problem within the next 24 hours.");
    courseRequestReceipt.addReportTextData("", "If you are not contacted in this time please contact the support desk directly at <a style='color:white;' href='mailto:NetworkServices@gov.sk.ca'>NetworkServices@gov.sk.ca</a> or by phone at: <span style='color:white'><u>1-866-933-8333</u></span>");
    courseRequestReceipt.addReportSpacer();
    
    courseRequestReceipt.setReportColorScheme("white", courseRequestPrimary, "black", "white", courseRequestSecondary, "black", "white", courseRequestSecondary, "black");
    return courseRequestReceipt;
  }
  
  public static EasyEmailReport createEnrollmentRequest(String courseName, Enrollment[] enrollments, String specialRequests, String name, String email, String bbLogin, String school, String division, String ip)
  {
    emailFormat = EasyEmailReport.Format.ENROLLMENT_REQUEST;
    
    report.setTo(helpDesk);
    report.setReplyTo(email);
    report.setFrom("RequestEnrollments@mail.edonline.sk.ca");
    
    report.setReceipt(CreateEnrollmentRequestReceipt(email));
    
    report.setSubject("Enrollment Request From: " + name);
    report.setReportTitle("Sector Technologies and Network Services: <br/> <u>Enrollment Request</u>");
    report.addCommonReportData(name, bbLogin, email, school, division, ip);
    
    report.addReportHr();
    
    String[] course_info_labels = { "Course/Org Name" };
    String[] course_info_values = { courseName };
    
    report.addReportListData("Course/Org Info:", course_info_labels, course_info_values);
    
    String[] enrollment_columns = { "Name", "SID/TCN", "Role" };
    ArrayList<String[]> enrollments_array = new ArrayList();
    for (int i = 0; i < enrollments.length; i++)
    {
      Enrollment e = enrollments[i];
      if (((e.getFirstname() != null) && (!e.getFirstname().equals("null")) && (!e.getFirstname().equals(""))) || ((e.getLastname() != null) && (!e.getLastname().equals("null")) && (!e.getLastname().equals(""))))
      {
        String[] enrollment_array = new String[3];
        enrollment_array[0] = (e.getFirstname() + " " + e.getLastname());
        enrollment_array[1] = (e.getSid() + e.getTsn());
        enrollment_array[2] = e.getRole();
        enrollments_array.add(enrollment_array);
      }
    }
    report.addReportTabularData("Enrollments", enrollment_columns, (String[][])enrollments_array.toArray(new String[enrollments_array.size()][3]));
    

    report.addReportTextData("Notes/Special Requests:", specialRequests);
    
    report.addReportSpacer();
    
    report.setReportColorScheme("white", enrollmentRequestPrimary, "black", "black", enrollmentRequestSecondary, "black", "black", enrollmentRequestSecondary, "black");
    
    return report;
  }
  
  private static EasyEmailReport CreateEnrollmentRequestReceipt(String email)
  {
    EasyEmailReport enrollmentRequestReceipt = new EasyEmailReport();
    
    enrollmentRequestReceipt.setTo(email);
    enrollmentRequestReceipt.setReplyTo(helpDesk);
    enrollmentRequestReceipt.setFrom("RequestEnrollments@mail.edonline.sk.ca");
    
    enrollmentRequestReceipt.setSubject("RE: EDonline Enrollment Request");
    enrollmentRequestReceipt.setReportTitle("You have requested course/org. enrollments from Network Services");
    enrollmentRequestReceipt.addReportTextData("", "The following enrollment request has been sent to the Network Services Support Desk on your behalf. You will be contacted by a member of the support desk who will assist you with this problem within the next 24 hours.");
    enrollmentRequestReceipt.addReportTextData("", "If you are not contacted in this time please contact the support desk directly at <a style='color:white;' href='mailto:NetworkServices@gov.sk.ca'>NetworkServices@gov.sk.ca</a> or by phone at: <span style='color:white'><u>1-866-933-8333</u></span>");
    enrollmentRequestReceipt.addReportSpacer();
    
    enrollmentRequestReceipt.setReportColorScheme("white", enrollmentRequestPrimary, "black", "white", enrollmentRequestSecondary, "black", "white", enrollmentRequestSecondary, "black");
    return enrollmentRequestReceipt;
  }
  
  public static EasyEmailReport createLiveEventRegistration(String[] liveEventIds, List<String> liveEventNames, List<String> liveEventStudents, List<String> liveEventClassrooms, String name, String title, String email, String bbLogin, String school, String location, String division, String ip)
  {
    emailFormat = EasyEmailReport.Format.LIVE_EVENT_REGISTRATION;
    
    report.setTo(helpDesk);
    report.setReplyTo(email);
    report.setFrom("LiveEventRegistration@mail.edonline.sk.ca");
    
    report.setReceipt(CreateLiveEventRegistrationReceipt(email));
    
    report.setSubject("LIVE Event Registration From: " + school + "-" + location + "-" + division);
    report.setReportTitle("Sector Technologies and Network Services: <br/><u>LIVE Event Registration</u>");
    report.addCommonReportData(name, bbLogin, email, school, division, ip);
    report.addReportSpacer();
    report.addReportListData("Additional Info", new String[] { "Location" }, new String[] { location });
    report.addReportHr();
    for (int i = 0; i < liveEventIds.length; i++) {
      report.addReportListData("Event " + (i + 1), new String[] { "Event ID", "Event Name", "Estimated Students", "Classrooms" }, new String[] { liveEventIds[i], (String)liveEventNames.get(i), (String)liveEventStudents.get(i), (String)liveEventClassrooms.get(i) });
    }
    report.addReportSpacer();
    
    report.setReportColorScheme("white", liveEventRegistrationPrimary, "black", "black", liveEventRegistrationSecondary, "black", "black", liveEventRegistrationSecondary, "black");
    return report;
  }
  
  private static EasyEmailReport CreateLiveEventRegistrationReceipt(String email)
  {
    EasyEmailReport liveEventRegistrationReceipt = new EasyEmailReport();
    
    liveEventRegistrationReceipt.setTo(email);
    liveEventRegistrationReceipt.setReplyTo(helpDesk);
    liveEventRegistrationReceipt.setFrom("LiveEventRegistration@mail.edonline.sk.ca");
    
    liveEventRegistrationReceipt.setSubject("RE: Live Event Registration");
    liveEventRegistrationReceipt.setReportTitle("You have registered for a Live Event");
    liveEventRegistrationReceipt.addReportTextData("", "The following LIVE Event Registration has been sent to the Network Services Support Desk on your behalf. If any of the information below is incorrect, or you'd like to cancel your registration please contact the support desk at <a style='color:white;' href='mailto:NetworkServices@gov.sk.ca'>NetworkServices@gov.sk.ca</a> or by phone at: <span style='color:white'><u>1-866-933-8333</u></span>\" ");
    
    liveEventRegistrationReceipt.addReportSpacer();
    
    liveEventRegistrationReceipt.setReportColorScheme("white", liveEventRegistrationPrimary, "black", "white", liveEventRegistrationSecondary, "black", "white", liveEventRegistrationSecondary, "black");
    return liveEventRegistrationReceipt;
  }
  
  //----------------Start Nicole
  public static EasyEmailReport createAdobeConnectRegistration(String event_name, String name, String email, String school, String division, String ip)
  {
    emailFormat = EasyEmailReport.Format.ADOBE_CONNECT_REGISTRATION;
    
    report.setTo(helpDesk);
    report.setReplyTo(email);
    report.setFrom("AdobeConnectRegistration@mail.edonline.sk.ca");
    
    report.setReceipt(CreateAdobeConnectRegistrationReceipt(email));
    
    report.setSubject("Adobe Connect Registration: " + event_name );
    report.setReportTitle("Sector Technologies and Network Services: <br/><u>Adobe Connect Registration</u>");    
    report.addCommonReportData(name, "N/A", email, school, division, ip);
    report.addReportListData("Additional Info", new String[] { "Event" }, new String[] { event_name });
    report.addReportSpacer();
    //report.addReportListData("Additional Info", new String[] { "Location" }, new String[] { location });
    report.addReportHr();
    report.addReportSpacer();
    
    report.setReportColorScheme("white", adobeConnectRegistrationPrimary, "black", "black", adobeConnectRegistrationSecondary, "black", "black", adobeConnectRegistrationSecondary, "black");
    return report;
  }
  
  private static EasyEmailReport CreateAdobeConnectRegistrationReceipt(String email)
  {
    EasyEmailReport adobeConnectRegistrationReceipt = new EasyEmailReport();
    
    adobeConnectRegistrationReceipt.setTo(email);
    adobeConnectRegistrationReceipt.setReplyTo(helpDesk);
    adobeConnectRegistrationReceipt.setFrom("AdobeConnectRegistration@mail.edonline.sk.ca");
    
    adobeConnectRegistrationReceipt.setSubject("RE: Adobe Connect Registration");
    adobeConnectRegistrationReceipt.setReportTitle("You have registered for a Adobe Connect Session");
    adobeConnectRegistrationReceipt.addReportTextData("", "The following Adobe Connect Registration has been sent to the Network Services Support Desk on your behalf. If any of the information below is incorrect, or you'd like to cancel your registration please contact the support desk at <a style='color:white;' href='mailto:NetworkServices@gov.sk.ca'>NetworkServices@gov.sk.ca</a> or by phone at: <span style='color:white'><u>1-866-933-8333</u></span>\" ");
    
    adobeConnectRegistrationReceipt.addReportSpacer();
    
    adobeConnectRegistrationReceipt.setReportColorScheme("white", adobeConnectRegistrationPrimary, "black", "white", adobeConnectRegistrationSecondary, "black", "white", adobeConnectRegistrationSecondary, "black");
    return adobeConnectRegistrationReceipt;
  }
  //----------------End Nicole
    public static EasyEmailReport createTemporaryAccountCredentialsEmail(String fname, String lname, String uname, String email, String password, String disable_expiry, String delete_expiry) {
        report.emailFormat = EasyEmailReport.Format.TEMP_ACCOUNT;

        report.setTo(email);
        report.setReplyTo(helpDesk);
        report.setFrom("TemporaryAccount@mail.edonline.sk.ca");

        report.setSubject("Temporary Blackboard Access");
        report.setReportTitle("Sector Technologies and Network Services: <br/> <u>Temporary Account</u>");

        report.addReportTextData("Instructions:", "A temporary account has been created for you allowing you access to content on Blackboard. Click the link at the bottom of this email, enter your credentials (displayed below) when prompted and press the \"Login\" button");        
        report.addReportSpacer();
        report.addReportTextData("Please Keep the Following In Mind:", " <ul>\n"
                + "        <li style='list-style:square; margin-left:20px; color:white;'>After 24 hours your account will be disabled; if you require access beyond this 24 hours please contact <a href='mailto:NetworkServices@gov.sk.ca'>NetworkServices@gov.sk.ca</a></li>\n"
                + "        <li style='list-style:square; margin-left:20px; color:white;'>After one week your account will be deleted along with any content or course work you have created in Blackboard with this account</li>\n"                
                + "    </ul>");
        report.addReportSpacer();

        String[] user_info_row_names = {"Username", "Password", "Account will be disabled", "Account will be deleted"};
        String[] user_info_data = {uname, password, disable_expiry, delete_expiry};
        report.addReportListData("Account Info:", user_info_row_names, user_info_data);
        report.addReportHr();

        report.addReportTextData("Click here for Access:", "<a style='font-weight:bold; color:white;' target='_blank' href='http://www.edonline.sk.ca/webapps/login?action=default_login'>http://www.edonline.sk.ca/webapps/login?action=default_login</a>");

        report.addReportSpacer();

        report.setReportColorScheme("white", tempAccountPrimary, "black", "black", tempAccountSecondary, "black", "black", tempAccountSecondary, "black");
        return report;
    }
}