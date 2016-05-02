/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stans.helpers;

import blackboard.data.user.User;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManager;
import blackboard.platform.context.ContextManagerFactory;
import java.util.ArrayList;
import stans.db.Query;

/**
 *
 * @author peter
 */
public class UserHelpers {
    
    public static String getCurrentUsername () {
        ContextManager contextManager = ContextManagerFactory.getInstance();
        Context ctx = contextManager.getContext();

        User user = ctx.getUser();
        String username = user.getUserName();
        
        return username;
    }
    public static Integer getCurrentUserId () {
		Integer id = -1;
		
        ArrayList<String> user_args = new ArrayList<String>();
        user_args.add( UserHelpers.getCurrentUsername() );
        ArrayList<Integer> user_results = Query.find( "users", "user_id = ?", user_args );
		
		if( user_results.size() > 0 ) {
			id = user_results.get( 0 );
		}
		
		return id;
    }
}
