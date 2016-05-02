package stans;

import blackboard.data.course.Course;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManagerFactory;
import blackboard.platform.institutionalhierarchy.service.Node;
import blackboard.platform.institutionalhierarchy.service.NodeAssociationManager;
import blackboard.platform.institutionalhierarchy.service.NodeManager;
import blackboard.platform.institutionalhierarchy.service.NodeManagerFactory;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author jwatson
 */
public class EasyData {

    public ShortcutMethods shortcuts;
    public Context blackboard;
    public EasyData(Context bbContext) {
        blackboard = bbContext;
        shortcuts = new ShortcutMethods();
    }
    public EasyData(HttpServletRequest request) {
        ContextManagerFactory.getInstance().setContext(request);
        blackboard = ContextManagerFactory.getInstance().getContext();
        shortcuts = new ShortcutMethods();
    }

    public class ShortcutMethods {

        private NodeManager nm;
        private NodeAssociationManager am;
        public ShortcutMethods() {
            nm = NodeManagerFactory.getHierarchyManager();
            am = NodeManagerFactory.getAssociationManager();
        }
        public EasyUser getCurrentUser() {
            return new EasyUser(blackboard);
        }
        public List<EasyUser> getUsers() {

            return null;
        }
        public List<Node> getAllInstitutionalHierarchyNodes() throws PersistenceException {
            if (nm.isInstitutionalHierarchyAvailable()) {
                return nm.loadAllChildren(nm.loadRootNode().getNodeId());
            } else {
                return null;
            }
        }
        public List<Node> getAllSchools() throws PersistenceException {
            if (nm.isInstitutionalHierarchyAvailable()) {
                List<Node> divisions = getAllDivisions();
                List<Node> schools = new ArrayList<Node>();

                for (Node d : divisions) {
                    schools.addAll(nm.loadImmediateChildren(d.getNodeId()));
                }
                return schools;
            } else {
                return null;
            }

        }
        public List<Node> getAllDivisions() throws PersistenceException {
            if (nm.isInstitutionalHierarchyAvailable()) {
                Node root_node = nm.loadRootNode();
                Node divs_node = nm.loadImmediateChildren(root_node.getNodeId()).get(0);

                return nm.loadImmediateChildren(divs_node.getNodeId());
            } else {
                return null;
            }
        }
        public Node getParentNode(Node node) throws PersistenceException {
            if (nm.isInstitutionalHierarchyAvailable()) {
                Id parent_id = node.getParentId();
                if (parent_id != null) {
                    return nm.loadNodeById(parent_id);
                }
            }
            return null;

        }
        public List<EasyCourse> getAllCoursesAndOrganizations() throws KeyNotFoundException, PersistenceException {
            List<Course> coursesAndOrgs = CourseDbLoader.Default.getInstance().loadAllCourses();
            List<EasyCourse> easy_courses_and_orgs = new ArrayList<EasyCourse>();
            for (Course c : coursesAndOrgs) {
                easy_courses_and_orgs.add(new EasyCourse(c));
            }
            return easy_courses_and_orgs;
        }
        public List<EasyCourse> getAllCourses() throws KeyNotFoundException, PersistenceException {
            List<EasyCourse> courses = new ArrayList<EasyCourse>();
            for (EasyCourse c : getAllCoursesAndOrganizations()) {
                if (!c.blackboard.isOrganization()) {
                    courses.add(c);
                }
            }
            return courses;
        }
        public List<EasyCourse> getAllOrganizations() throws KeyNotFoundException, PersistenceException {
            List<EasyCourse> organizations = new ArrayList<EasyCourse>();
            for (EasyCourse o : getAllCoursesAndOrganizations()) {
                if (o.blackboard.isOrganization()) {
                    organizations.add(o);
                }
            }
            return organizations;
        }
    }
}
