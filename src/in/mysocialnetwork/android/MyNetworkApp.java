package in.mysocialnetwork.android;

import android.app.Application;
import android.content.Context;

import com.facebook.SessionDefaultAudience;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.utils.Logger;

public class MyNetworkApp extends Application {
    private static final String APP_ID = "401336146648895";
    private static final String APP_NAMESPACE = "mynetworkinsights";
    
   
    private static Context context;
    
    public static Context getAppContext() {
        return MyNetworkApp.context;
    }
    
    @Override
    public void onCreate() {
		super.onCreate();
	
		// set log to true
		Logger.DEBUG_WITH_STACKTRACE = true;
	
		// initialize facebook configuration
		Permission[] permissions = new Permission[] { 
			Permission.BASIC_INFO, 
			Permission.FRIENDS_ACTIVITIES,
			Permission.FRIENDS_ABOUT_ME,
			Permission.FRIENDS_EVENTS,
			Permission.PUBLISH_STREAM,
			Permission.FRIENDS_BIRTHDAY,
			Permission.FRIENDS_EDUCATION_HISTORY,
			Permission.FRIENDS_HOMETOWN,
			Permission.FRIENDS_INTERESTS,
			Permission.FRIENDS_LOCATION,
			Permission.FRIENDS_RELATIONSHIPS,
			Permission.FRIENDS_WORK_HISTORY,
			Permission.FRIENDS_LIKES,
			Permission.FRIENDS_RELIGION_POLITICS
		};

		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
			.setAppId(APP_ID)
			.setNamespace(APP_NAMESPACE)
			.setPermissions(permissions)
			.setDefaultAudience(SessionDefaultAudience.FRIENDS)
			.setAskForAllPermissionsAtOnce(false)
			.build();
	
		SimpleFacebook.setConfiguration(configuration);
    }
}
