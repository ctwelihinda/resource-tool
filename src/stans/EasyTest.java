package stans;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import blackboard.base.BbList;
import blackboard.data.content.Content;
import blackboard.data.gradebook.Lineitem;
import blackboard.data.gradebook.impl.OutcomeDefinition;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.persist.content.ContentDbLoader;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.course.impl.CourseDbLoaderImpl;
import blackboard.persist.gradebook.LineitemDbLoader;
import blackboard.persist.gradebook.impl.OutcomeDefinitionDbLoader;
//import blackboard.platform.integration.extension.as.ASIntegrationExtension;

public class EasyTest {

    public OutcomeDefinition blackboard;
    public ShortcutMethods shortcuts;

    public EasyTest(OutcomeDefinition blackboardOutcomeDefinition) throws PersistenceException {
        blackboard = blackboardOutcomeDefinition;
        shortcuts = new ShortcutMethods();
    }

    public EasyTest(Lineitem blackboardLineitem) {
        blackboard = lineItemToOutcomeDefinition(blackboardLineitem);
        shortcuts = new ShortcutMethods();
    }
    
    public EasyTest(Content blackboardContent) throws PersistenceException {
        blackboard = contentToOutcomeDefinition(blackboardContent);
        shortcuts = new ShortcutMethods();
    }

    public EasyTest(Id courseId, String testName) throws PersistenceException {
        blackboard = lineItemToOutcomeDefinition(courseIdAndTestNameToLineitem(courseId, testName));
        shortcuts = new ShortcutMethods();
    }
    
    public EasyTest(EasyCourse course, String testName) throws PersistenceException {
        blackboard = lineItemToOutcomeDefinition(courseIdAndTestNameToLineitem(course.blackboard.getId(), testName));
        shortcuts = new ShortcutMethods();
    }
    public EasyTest(Id courseId, int rNumber) throws PersistenceException {
        blackboard = lineItemToOutcomeDefinition(courseIdAndTestNameToLineitem(courseId, rNumberToTestName(rNumber)));
        shortcuts = new ShortcutMethods();
    }
    
    public EasyTest(EasyCourse course, int rNumber) throws PersistenceException {
        blackboard = lineItemToOutcomeDefinition(courseIdAndTestNameToLineitem(course.blackboard.getId(), rNumberToTestName(rNumber)));
        shortcuts = new ShortcutMethods();
    }

    
    //helpers
    public static OutcomeDefinition lineItemToOutcomeDefinition(Lineitem li) {
        return li.getOutcomeDefinition();
    }

    public static OutcomeDefinition contentToOutcomeDefinition(Content blackboardContent) throws PersistenceException {        
        OutcomeDefinitionDbLoader oddl = OutcomeDefinitionDbLoader.Default.getInstance();
        OutcomeDefinition outcomeDefinition = oddl.loadByContentId(blackboardContent.getId());
        return outcomeDefinition;
    }

    public static Lineitem courseIdAndTestNameToLineitem(Id courseId, String testName) throws PersistenceException {
        BbList<Lineitem> lidl = LineitemDbLoader.Default.getInstance().loadByCourseIdAndLineitemName(courseId, testName);
        Lineitem lineitem = lidl.get(0);
        return lineitem;
    }
    
    public static String rNumberToTestName(int rNumber){
         return "R"+String.format("%06d", rNumber);
    }
    
    public class ShortcutMethods {

        private final String outcome_definition_id_string; //test
        private final String course_id_string;
        private final String content_id_string;
        private final Id outcome_definition_id;
        private final Id course_id;
        private final Id content_id;

        public ShortcutMethods() {
            outcome_definition_id = blackboard.getId();
            course_id = blackboard.getCourseId();
            content_id = blackboard.getContentId();
            outcome_definition_id_string = blackboard.getId().getExternalString();
            course_id_string = blackboard.getCourseId().getExternalString();
            content_id_string = blackboard.getContentId().getExternalString();            
        }

        public String getEditPath() {
            return "/webapps/assessment/do/content/assessment?action=MODIFY&course_id=" + course_id_string + "&content_id=" + content_id_string + "&assessmentType=Test&method=modifyAssessment";
        }

        public String getEditOptionsPath() {
            return "/webapps/assessment/do/content/assessment?action=MODIFY&course_id=" + course_id_string + "&content_id=" + content_id_string + "&assessmentType=Test&method=modifyOptions";
        }

        public String getAdaptiveReleasePath() {
            return "/webapps/blackboard/execute/manageQuickRule?course_id=" + course_id_string + "&content_id=" + content_id_string;
        }

        public String getResultsPath() {
            return "/webapps/assessment/do/viewAttempt?outcome_definition_id=" + outcome_definition_id_string + "&course_id=" + course_id_string;
        }

        public String getAggregatedResultsPath() {
            return "/webapps/assessment/stats/stats.jsp?outcome_definition_id=" + outcome_definition_id_string + "&course_id=" + course_id_string;
        }       
        
        public OutcomeDefinition getOutcomeDefinition() {
            return blackboard;
        }

        public EasyCourse getCourse() throws PersistenceException {
            CourseDbLoader cdbl = CourseDbLoaderImpl.Default.getInstance();
            return new EasyCourse(cdbl.loadById(blackboard.getCourseId()));
        }

        //this is not the content of the test! (those are called outcomes... atleast I think they are) it is the content item that contains the test
        public Content getContent() throws PersistenceException {
            ContentDbLoader cdl = ContentDbLoader.Default.getInstance();
            return cdl.loadById(blackboard.getContentId());
        }

        public String getOutcomeDefinitionIdString() {
            return outcome_definition_id_string;
        }

        public String getCourseIdString() {
            return course_id_string;
        }

        public String getContentIdString() {
            return outcome_definition_id_string;
        }

        public Id getOutcomeDefinitionId() {
            return outcome_definition_id;
        }

        public Id getCourseId() {
            return course_id;
        }

        public Id getContentId() {
            return content_id;
        }

    }
}
