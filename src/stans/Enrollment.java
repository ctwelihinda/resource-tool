package stans;

public class Enrollment
{
  private String firstname;
  private String lastname;
  private String school;
  private String division;
  private String tsn;
  private String sid;
  
  public String getFirstname()
  {
    return firstname;
  }
  
  public void setFirstname(String firstname)
  {
    this.firstname = firstname;
  }
  
  public String getLastname()
  {
    return lastname;
  }
  
  public void setLastname(String lastname)
  {
    this.lastname = lastname;
  }
  
  public String getSchool()
  {
    return school;
  }
  
  public void setSchool(String school)
  {
    this.school = school;
  }
  
  public String getDivision()
  {
    return division;
  }
  
  public void setDivision(String division)
  {
    this.division = division;
  }
  
  public String getTsn()
  {
    if (tsn == null) {
      return "";
    }
    return tsn;
  }
  
  public void setTsn(String tsn)
  {
    this.tsn = tsn;
  }
  
  public String getSid()
  {
    if (sid == null) {
      return "";
    }
    return sid;
  }
  
  public void setSid(String sid)
  {
    this.sid = sid;
  }
  
  public String getRole()
  {
    if ((tsn != null) && (!tsn.equals(""))) {
      return "Teacher/Leader";
    }
    if ((sid != null) && (!sid.equals(""))) {
      return "Student/Participant";
    }
    return "";
  }
}