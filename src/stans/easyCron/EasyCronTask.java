package stans.easyCron;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskExecutor;
import javax.servlet.ServletContext;

// this class will only function if the easycrontaskcontextlistner is registered with your web.xml file
public class EasyCronTask {

    public Task task;    
    public ShortcutMethods shortcuts;
    private static Scheduler scheduler;

    public EasyCronTask(ServletContext context) {
        //System.out.println("requestattribute: " + context.getAttribute("cron4j.scheduler"));
        scheduler = (Scheduler) context.getAttribute("cron4j.scheduler");
        task = new Task() {
            @Override
            public void execute(TaskExecutionContext tec) throws RuntimeException {
                //System.out.println("task running");
            }
        };        
        shortcuts = new ShortcutMethods();
    }

    public EasyCronTask(Scheduler s) {
        scheduler = s;
        task = new Task() {
            @Override
            public void execute(TaskExecutionContext tec) throws RuntimeException {
                //System.out.println("task running");
            }
        };
        shortcuts = new ShortcutMethods();
    }

    public static void killAll(ServletContext context){
        scheduler = (Scheduler) context.getAttribute("cron4j.scheduler");
         TaskExecutor[] executingTasks = scheduler.getExecutingTasks();
            for(TaskExecutor et : executingTasks){
                et.stop();
            }
    }
    
    public class ShortcutMethods {

        public void setTaskToRun(Task t) {
            task = t;
        }

        public void setCronExpression(String cronExpression) {      
            //System.out.println("cronexpression: " + cronExpression);
            //System.out.println("task: ");
            //System.out.println(task.toString());
            scheduler.schedule(cronExpression, task);
        }
       
        public void start() {
            scheduler.launch(task);
        }
        
        public void resume() {
            getTaskExecutor().resume();
        }

        public void pause() {
            getTaskExecutor().pause();
        }

        public void kill() {
            scheduler.deschedule(task.toString());
        }
        
        public TaskExecutor getTaskExecutor(){
            TaskExecutor[] executingTasks = scheduler.getExecutingTasks();
            for(TaskExecutor et : executingTasks){
                if(et.getTask() == task){
                    return et;
                }
            }
            return null;
        }
        
    }
}
