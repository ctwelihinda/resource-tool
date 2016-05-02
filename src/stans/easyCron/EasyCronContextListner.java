package stans.easyCron;

import blackboard.data.user.User;
import blackboard.persist.SearchOperator;
import blackboard.persist.user.UserDbLoader;
import blackboard.persist.user.UserSearch;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import stans.EasyUser;

/**
 * This listener starts a scheduler bounded to the web application: the
 * scheduler is started when the application is started, and the scheduler is
 * stopped when the application is destroyed. The scheduler uses a custom
 * TaskCollector to retrieve, once a minute, its job list. Moreover the
 * scheduler is registered on the application context, in a attribute named
 * according to the value of the {@link Constants#SCHEDULER} constant.
 */
public class EasyCronContextListner implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        Scheduler scheduler = new Scheduler();

        //Run any startup crons
        //System.out.println("---REGISTERING STARTUP CRONS---");

        //start the scheduler
        //System.out.println("Starting the scheduler");
        try {
            scheduler.start();
            context.setAttribute("cron4j.scheduler", scheduler);
            EasyCronTask.killAll(context);
            //System.out.println("success");

        } catch (Exception e) {
            //System.out.println("error starting scheduler:");
            //System.out.println(e.toString());
            for (StackTraceElement st : e.getStackTrace()) {
                //System.out.println(st.toString());
            }
        }

//----------STARTUP CRONS HERE
        //--Disable  userss flagged for disablement, and delete
        // users flagged for deletion. when a user is disabled
        // it is flagged for deletion at a later date
        //System.out.println("scheduling task: DisableTemporaryAccountsAfter24Hours");
        try {
            EasyCronTask disableTemporaryAccounts = new EasyCronTask(scheduler);
            disableTemporaryAccounts.shortcuts.setTaskToRun(getDeleteAndDisableUserTask());
            disableTemporaryAccounts.shortcuts.setCronExpression("0 * * * *"); //every hour
            disableTemporaryAccounts.shortcuts.start();
            //System.out.println("success");

        } catch (Exception e) {
            //System.out.println("error registering task:");
            //System.out.println(e.toString());
            for (StackTraceElement st : e.getStackTrace()) {
                //System.out.println(st.toString());
            }
        }       

//----------END STARTUP CRONS     
        //System.out.println("---STARTUP CRONS REGISTERED---");
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        EasyCronTask.killAll(context);
        Scheduler scheduler = (Scheduler) context.getAttribute("cron4j.scheduler");
        context.removeAttribute("cron4j.scheduler");
        scheduler.stop();
    }

    // TASKS
    public Task getDeleteAndDisableUserTask() {
        Task task = new Task() {

            @Override
            public void execute(TaskExecutionContext tec) throws RuntimeException {
                try {
                    ////System.out.println("---STARTING EXPIRED TEMPORARY ACCOUNT DISABLE SCRIPT---");

                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DATE, 7);
                    Date a_week_from_now = c.getTime();
                    
                    Date now = Calendar.getInstance().getTime();
                    
                    
                    
                    //Search for users who might be expired
                    UserSearch us = new UserSearch();
                    us.setNameParameter(UserSearch.SearchKey.UserName, SearchOperator.Contains, "bbtemp");
                    List<User> users = UserDbLoader.Default.getInstance().loadByUserSearch(us);

                    ////System.out.println("Found " + users.size() + " temporary accounts");

                    //iterate through the users
                    for (User u : users) {
                        try {
                            EasyUser user = new EasyUser(u.getUserName());
                            Date expiry_date = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.ENGLISH).parse(user.blackboard.getStudentId().split(": ")[1]);
                            if (expiry_date.before(now)) {
                                ////System.out.println("EXPIRED: " + user.blackboard.getUserName() + " " + user.blackboard.getStudentId());                                
                                try {
                                    if (u.getStudentId().toUpperCase().contains("DISABLEAT")) {
                                        ////System.out.print("disabling...");
                                        user.blackboard.setIsAvailable(false);
                                        user.blackboard.setStudentId("DeleteAt: " + new SimpleDateFormat("MM/dd/yyyy h:mm a").format(a_week_from_now));
                                        user.shortcuts.save();
                                    } else if (u.getStudentId().toUpperCase().contains("DELETEAT")) {
                                        ////System.out.print("deleting...");
                                        user.shortcuts.delete();
                                    }
                                    ////System.out.println("success");
                                } catch (Exception e) {
                                    ////System.out.println("error");
                                    ////System.out.println(e.toString());
                                    for (StackTraceElement st : e.getStackTrace()) {
                                        ////System.out.println(st.toString());
                                    }
                                }
                                //show unexpired user
                            } else {
                                ////System.out.println("UNEXPIRED: " + u.getUserName() + " " + u.getStudentId());
                            }
                        } catch (Exception e) {
                            ////System.out.println("An Error Occured while looking up or disabling/deleting a perticular user");
                            ////System.out.println(e.toString());
                            for (StackTraceElement st : e.getStackTrace()) {
                                ////System.out.println(st.toString());
                            }
                        }

                    }

                } catch (Exception e) {
                    ////System.out.println("An Error Occured while disabling/deleting expired temporary accounts");
                    ////System.out.println(e.toString());
                    for (StackTraceElement st : e.getStackTrace()) {
                        ////System.out.println(st.toString());
                    }
                }
                ////System.out.println("---EXPIRED TEMPORARY ACCOUNT DISABLE SCRIPT COMPLETE---");
            }
        };
        return task;
    }
   

}
