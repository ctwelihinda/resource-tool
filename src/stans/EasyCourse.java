package stans;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import blackboard.data.course.Course;
import blackboard.data.course.Course.Enrollment;
import blackboard.data.user.User;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.user.UserDbLoader;
import blackboard.platform.context.ContextManagerFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

public class EasyCourse {

    public Course blackboard;
    public ShortcutMethods shortcuts;

    public EasyCourse(Course blackboardCourse) {
        blackboard = blackboardCourse;
        shortcuts = new ShortcutMethods();

    }

    public EasyCourse(String courseId) throws PersistenceException {
        blackboard = CourseDbLoader.Default.getInstance().loadByCourseId(courseId);
        shortcuts = new ShortcutMethods();
    }

    public EasyCourse(HttpServletRequest request) throws PersistenceException {
        ContextManagerFactory.getInstance().setContext(request);
        blackboard = ContextManagerFactory.getInstance().getContext().getCourse();
        shortcuts = new ShortcutMethods();
    }

    public class ShortcutMethods {

        public ShortcutMethods() {
            //todo set the private fields
        }

        public int getEnrolledUserCount() throws KeyNotFoundException, PersistenceException {
            List<EasyUser> users = getEnrolledUsers();
            if (users == null) {
                return 0;
            } else {
                return users.size();
            }
        }

        public int getEnrolledStudentCount() throws KeyNotFoundException, PersistenceException {
            List<EasyUser> students = getEnrolledStudents();
            if (students == null) {
                return 0;
            } else {
                return students.size();
            }
        }

        public int getEnrolledTeacherCount() throws KeyNotFoundException, PersistenceException {
            List<EasyUser> teachers = getEnrolledTeachers();
            if (teachers == null) {
                return 0;
            } else {
                return teachers.size();
            }
        }

        public List<EasyUser> getEnrolledUsers() throws KeyNotFoundException, PersistenceException {
            List<User> users = UserDbLoader.Default.getInstance().loadByCourseId(blackboard.getId());
            if (users != null) {
                List<EasyUser> easy_users = new ArrayList<EasyUser>();
                for (User u : users) {
                    easy_users.add(new EasyUser(u));
                }
                return easy_users;
            } else {
                return null;
            }
        }

        public List<EasyUser> getEnrolledStudents() throws KeyNotFoundException, PersistenceException {
            List<EasyUser> easy_users = getEnrolledUsers();
            if (easy_users != null) {
                List<EasyUser> students = new ArrayList<EasyUser>();
                for (EasyUser u : easy_users) {
                    if (u.shortcuts.isStudent()) {
                        students.add(u);
                    }
                }
                return students;
            }
            return null;

        }

        public List<EasyUser> getEnrolledTeachers() throws KeyNotFoundException, PersistenceException {
            List<EasyUser> easy_users = getEnrolledUsers();
            if (easy_users != null) {
                List<EasyUser> teachers = new ArrayList<EasyUser>();
                for (EasyUser u : easy_users) {
                    if (u.shortcuts.isTeacher()) {
                        teachers.add(u);
                    }
                }
                return teachers;
            }
            return null;
        }

        public String getCourseIdForURLCreation() {
            return blackboard.getId().getExternalString();
        }

        public boolean isCurrentlyAvailableForSelfEnrollment() {
            Calendar c = Calendar.getInstance();
            Date now = c.getTime();

            Date start_date = null;
            Date end_date = null;

            try {
                start_date = blackboard.getEnrollmentStartDate().getTime();
                end_date = blackboard.getEnrollmentEndDate().getTime();
            } catch (Exception e) {
            }

            if (blackboard.getEnrollmentType() != Enrollment.SELF_ENROLLMENT) {
                return false;
            }
            if (start_date != null && now.before(start_date)) {
                return false;
            }
            if (end_date != null && now.after(end_date)) {
                return false;
            }
            return true;
        }
        
        public boolean containsUser(EasyUser u) throws PersistenceException{
            for(EasyUser course_user : getEnrolledUsers()){
                if(course_user.blackboard.getUserName().equals(u.blackboard.getUserName())){
                    return true;
                } 
            }
            return false;
        }
    }
}
