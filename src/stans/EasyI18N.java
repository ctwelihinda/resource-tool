package stans;
/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

import blackboard.admin.data.IAdminObject;
import blackboard.admin.data.datasource.DataSource;
import blackboard.admin.data.role.PortalRoleMembership;
import blackboard.admin.persist.datasource.DataSourceLoader;
import blackboard.admin.persist.datasource.DataSourcePersister;
import blackboard.admin.persist.role.PortalRoleMembershipPersister;
import blackboard.admin.persist.role.impl.PortalRoleMembershipDbPersister;
import blackboard.data.ValidationException;
import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership;
import blackboard.data.role.PortalRole;
import blackboard.data.user.User;
import blackboard.data.user.UserRole;
import blackboard.db.ConstraintViolationException;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.role.PortalRoleDbLoader;
import blackboard.persist.role.PortalRoleDbPersister;
import blackboard.persist.user.UserDbPersister;
import blackboard.persist.user.UserRoleDbPersister;
import blackboard.persist.user.impl.UserDbLoaderImpl;
import blackboard.platform.LicenseUtil;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManagerFactory;
import blackboard.platform.institutionalhierarchy.service.Node;
import blackboard.platform.institutionalhierarchy.service.NodeAssociationManager;
import blackboard.platform.institutionalhierarchy.service.NodeManager;
import blackboard.platform.institutionalhierarchy.service.NodeManagerFactory;
import blackboard.platform.institutionalhierarchy.service.ObjectType;
import blackboard.platform.role.PortalRoleManager;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class EasyI18N {

    public User blackboard; //change to person? everything should still work because person extends user
    public ShortcutMethods shortcuts;

    //TODO: static methods to check for user existance and load users
    public EasyI18N() {
        blackboard = new User();
        shortcuts = new ShortcutMethods();

    }

    public EasyI18N(User blackboarduser) {
        blackboard = blackboarduser;
        shortcuts = new ShortcutMethods();

    }

    public EasyI18N(HttpServletRequest request) {
        ContextManagerFactory.getInstance().setContext(request);
        blackboard = ContextManagerFactory.getInstance().getContext().getUser();
        shortcuts = new ShortcutMethods();

    }

    public EasyI18N(Context bbContext) {
        blackboard = bbContext.getUser();
        shortcuts = new ShortcutMethods();

    }

    public EasyI18N(String username) throws KeyNotFoundException, PersistenceException {
        blackboard = UserDbLoaderImpl.Default.getInstance().loadByUserName(username);
        shortcuts = new ShortcutMethods();
    }

    public boolean exists() {
        return blackboard != null;
    }
    public boolean isGuest() {
        return !exists() || blackboard.getUserName().toLowerCase().equals("guest");
    }

    public class ShortcutMethods {

        private NodeManager nm;
        private NodeAssociationManager am;

        public ShortcutMethods() {

            nm = NodeManagerFactory.getHierarchyManager();
            am = NodeManagerFactory.getAssociationManager();

        }
		
        /*
         * Schools and Divisions
         */
        public Node getSchool() throws PersistenceException {
            List<Node> institutional_hierarchy_nodes = getInstitutionalHierarchyNodes();
            if (institutional_hierarchy_nodes != null && institutional_hierarchy_nodes.size() > 0) {
                Node school = institutional_hierarchy_nodes.get(0);
                return school;
            } else {
                return null;
            }
        }

        public Node getDivision() throws PersistenceException {
            List<Node> institutional_hierarchy_nodes = getInstitutionalHierarchyNodes();
            if (institutional_hierarchy_nodes != null && institutional_hierarchy_nodes.size() > 0) {
                Node division = nm.loadNodeById(institutional_hierarchy_nodes.get(0).getParentId());
                return division;
            } else {
                return null;
            }
        }

        public String getSchoolName() throws PersistenceException {
            Node school = getSchool();
            if (school != null && school.getName() != null) {
                return school.getName();
            } else {
                return "";
            }
        }

        public String getDivisionName() throws PersistenceException {
            Node division = getDivision();
            if (division != null && division.getName() != null) {
                return division.getName();
            } else {
                return "";
            }
        }
        /*
         * Convenience Methods
         */

        public String getFullName() {
            return blackboard.getGivenName() + " " + blackboard.getFamilyName();
        }
        /*
         * Course and Organizations
         */

        public List<Node> getInstitutionalHierarchyNodes() throws PersistenceException {
            if (nm.isInstitutionalHierarchyAvailable()) {
                List<Node> institutional_hierarchy_nodes = am.loadAssociatedNodes(blackboard.getId(), ObjectType.User);
                return institutional_hierarchy_nodes;
            } else {
                return null;
            }
        }

        public List<EasyCourse> getCoursesAndOrganizations(CourseMembership.Role role) throws KeyNotFoundException, PersistenceException {
            List<EasyCourse> coursesAndOrgs = new ArrayList<EasyCourse>();
            List<Course> bbCoursesAndOrgs = CourseDbLoader.Default.getInstance().loadByUserIdAndCourseMembershipRole(blackboard.getId(), role);
            for (Course c : bbCoursesAndOrgs) {
                coursesAndOrgs.add(new EasyCourse(c));
            }
            return coursesAndOrgs;
        }

        public List<EasyCourse> getCourses(CourseMembership.Role role) throws PersistenceException {
            List<EasyCourse> courses = new ArrayList<EasyCourse>();
            for (EasyCourse c : getCoursesAndOrganizations(role)) {
                if (!c.blackboard.isOrganization()) {
                    courses.add(c);
                }
            }
            return courses;

        }

        public List<EasyCourse> getOrganizations(CourseMembership.Role role) throws PersistenceException {
            List<EasyCourse> organizations = new ArrayList<EasyCourse>();
            for (EasyCourse o : getCoursesAndOrganizations(role)) {
                if (o.blackboard.isOrganization()) {
                    organizations.add(o);
                }
            }
            return organizations;

        }
        /*
         *
         * Institution Role Methods
         *
         */

        public List<PortalRole> getRoles() throws KeyNotFoundException, PersistenceException {
            List<PortalRole> user_roles = PortalRoleDbLoader.Default.getInstance().loadAllByUserId(blackboard.getId());
            return user_roles;
        }

        public boolean hasRoleName(String roleName) throws KeyNotFoundException, PersistenceException {
            List<PortalRole> roles = getRoles();
            if (roles != null && roles.size() > 0) {
                for (PortalRole r : roles) {
                    if (r.getRoleName().toLowerCase().equals(roleName)) {
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }
        }

        public boolean hasRoleId(String roleId) throws KeyNotFoundException, PersistenceException {
            List<PortalRole> roles = getRoles();
            if (roles != null && roles.size() > 0) {
                for (PortalRole r : roles) {
                    if (r.getRoleID().toLowerCase().equals(roleId)) {
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }
        }

        public boolean isTeacher() throws KeyNotFoundException, PersistenceException {
            if (hasRoleName("teacher")) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isStudent() throws KeyNotFoundException, PersistenceException {
            if (hasRoleName("student")) {
                return true;
            } else {
                return false;
            }
        }

        // setters
        public void setDatasource(String datasourceKeyName, String datasourceKeyDescription) throws PersistenceException, ValidationException {
            // check if there is a dsk by the same name, if so create a user under that DSK
            // otherwise create a new DS by the name of this B2 and assign the user that DS
            DataSource datasource = null;
            List<DataSource> all = DataSourceLoader.Default.getInstance().loadAll();

            for (DataSource ds : all) {
                if (ds.getBatchUid().equals(datasourceKeyName.toUpperCase())) {
                    datasource = ds;
                }
            }
            if (datasource == null) {
                datasource = new DataSource();
                datasource.setBatchUid(datasourceKeyName.toUpperCase());
                datasource.setDescription(datasourceKeyDescription);
                datasource.setRowStatus(IAdminObject.RowStatus.ENABLED);
                DataSourcePersister.Default.getInstance().create(datasource);
            }

            blackboard.setDataSourceId(datasource.getDataSourceId());
        }

        public void setandHashPassword(String password) {
            blackboard.setPassword(LicenseUtil.getHashValue(password));
        }

        
        
        //use before saving
        public void setPrimaryInstitutionRole(String role_id) throws PersistenceException, ValidationException {
             PortalRole portalRole;            

            try {
                portalRole = PortalRoleDbLoader.Default.getInstance().loadByRoleId(role_id);
            } catch (Exception e) {

                //if the role doesnt exist create it
                portalRole = new PortalRole();
                portalRole.setRoleID(role_id);
                portalRole.setRoleName(role_id);
                
                PortalRoleDbPersister persister = PortalRoleDbPersister.Default.getInstance();
                persister.persist(portalRole);
            }
            blackboard.setPortalRole(portalRole);
            

        }
        
        //use after saving
        public void addSecondaryInstitutionRole(String role_id) throws PersistenceException, ValidationException, ConstraintViolationException {

            PortalRole portalRole;
            UserRole userRole = new UserRole();

            try {
                portalRole = PortalRoleDbLoader.Default.getInstance().loadByRoleId(role_id);
            } catch (Exception e) {

                //if the role doesnt exist create it
                portalRole = new PortalRole();
                portalRole.setRoleID(role_id);
                portalRole.setRoleName(role_id);
                
                PortalRoleDbPersister persister = PortalRoleDbPersister.Default.getInstance();
                persister.persist(portalRole);
            }

            
            userRole.setUser(blackboard);            
            userRole.setPortalRoleId(portalRole.getId());

            UserRoleDbPersister.Default.getInstance().persist(userRole);

        }
        
        

        //persister
        public void save() throws ValidationException, PersistenceException {

            if (blackboard.getId() == null || blackboard.getId() == Id.UNSET_ID) {
                blackboard.setId(Id.newId(User.DATA_TYPE));
                blackboard.setBatchUid(blackboard.getUserName());
            }
//            System.out.println("");
//            System.out.println("");
//            System.out.println("SAVING USER");
//            System.out.println("id: " + blackboard.getId());
//            System.out.println("buid: " + blackboard.getBatchUid());
//            System.out.println("raw uid: " + blackboard.getRawUuid());
//            System.out.println("dsid: " + blackboard.getDataSourceId());
//            System.out.println("username: " + blackboard.getUserName());
//            System.out.println("name: " + getFullName());
//            System.out.println("SID: " + blackboard.getStudentId());
//            System.out.println("");
//            System.out.println("");

            UserDbPersister.Default.getInstance().persist(blackboard);
        }

        public void delete() throws PersistenceException {
            UserDbPersister.Default.getInstance().deleteById(blackboard.getId());
        }

        public void disable() {

        }

        

    }

}
