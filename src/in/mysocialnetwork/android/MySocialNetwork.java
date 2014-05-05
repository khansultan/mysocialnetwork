package in.mysocialnetwork.android;


import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Education;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.entities.Profile.Properties;
import com.sromku.simple.fb.entities.Work;
import com.sromku.simple.fb.listeners.OnFriendsListener;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

import in.mysocialnetwork.dbhandler.CreateTables;
import in.mysocialnetwork.helpers.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MySocialNetwork extends Activity {

	protected static final String TAG = MySocialNetwork.class.getName();
	private SimpleFacebook mSimpleFacebook;

	private ProgressDialog mProgress;
	private Button mButtonLogin;
	private Button mButtonLogout;
	private TextView mTextStatus;
	
	private Button mButtonGetFWork;
	private Button mButtonGetFHome;
	
	private Button mBPersonal;
	private Button mBHomeTwon;
	private Button mBSunSign;
	private Button mBEducation;
	private Button mBWork;
	private Button mBMore;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// test local language
		Utils.updateLanguage(getApplicationContext(), "en");
		Utils.printHashKey(getApplicationContext());

		setContentView(R.layout.activity_my_social_network);
		initUI();
		
		// 1. Login example
		loginExample();

		// 2. Logout example
		logoutExample();
				
		fetchFriendData();
	}
	
	/**
	 * Login example.
	 */
	private void loginExample() {
		mButtonLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mSimpleFacebook.login(mOnLoginListener);
			}
		});
	}

	/**
	 * Logout example
	 */
	private void logoutExample() {
		mButtonLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mSimpleFacebook.logout(mOnLogoutListener);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		setUIState();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
	}
	
	private void fetchFriendData() {
		// listener for friends request
		final OnFriendsListener onFriendsListener = new OnFriendsListener() {

			@Override
			public void onFail(String reason) {
				hideDialog();
				// insure that you are logged in before getting the friends
				Log.w(TAG, reason);
			}

			@Override
			public void onException(Throwable throwable) {
				hideDialog();
				Log.e(TAG, "Bad thing happened", throwable);
			}

			@Override
			public void onThinking() {
				showDialog();
				// show progress bar or something to the user while fetching
				// profile
				Log.i(TAG, "Thinking...");
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onComplete(List<Profile> friends) {
				loggedInUIState();
				hideDialog();
				mButtonGetFWork.setEnabled(false);
				CreateTables createTable = new CreateTables(MySocialNetwork.this, "MSN.db", null, 1);
				createTable.createTables();
				new processWork().execute(friends);
				
				for (Profile profile : friends) 
				{ 
					String hometown = profile.getHometown();
					try{
						JSONObject jobj = new JSONObject(hometown);	  
						hometown = jobj.getString("name");
			         
			        }catch(JSONException e1){
			            Log.d("UPDATE TRAFIC RETURN JSON ERROR -->" , e1.toString());
			        }catch (ParseException e1){
			            Log.d("UPDATE TRAFIC PARSE RETURN DATA ERROR -->" , e1.toString());
			        }
					

					FBFriend fbuser = new FBFriend();

					fbuser.setBday(profile.getBirthday());
					fbuser.setFb_id(profile.getId());
					fbuser.setFb_name(profile.getName());
					fbuser.setGender(profile.getGender());
					fbuser.setHometown(hometown);
					fbuser.setLocation(profile.getLocation().getName());
					fbuser.setPolitics(profile.getPolitical());
					fbuser.setRelationship(profile.getRelationshipStatus());
					fbuser.setReligion(profile.getReligion());
					
					createTable.addUser(fbuser);				
					
				}
				

			}

		};

		// set button
		mButtonGetFWork.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//mSimpleFacebook.getFriends(onFriendsListener);
				 // create properties
				 Properties properties = new Properties.Builder().
						 add(Properties.ID).
						 add(Properties.NAME).
						 add(Properties.BIRTHDAY).
						 add(Properties.HOMETOWN).
						 add(Properties.LOCATION).
						 add(Properties.AGE_RANGE).
						 add(Properties.DEVICES).
						 add(Properties.EDUCATION).
						 add(Properties.FAVORITE_ATHLETES).
						 add(Properties.FAVORITE_TEAMS).
						 add(Properties.GENDER).
						 add(Properties.LANGUAGE).
						 add(Properties.POLITICAL).
						 add(Properties.RELATIONSHIP_STATUS).
						 add(Properties.RELIGION).
						 add(Properties.WORK).build();
				 
				mSimpleFacebook.getFriends(properties, onFriendsListener);
				
			}
		});

			
		
	}
	
	// Login listener
	private OnLoginListener mOnLoginListener = new OnLoginListener() {

		@Override
		public void onFail(String reason) {
			mTextStatus.setText(reason);
			Log.w(TAG, "Failed to login");
		}

		@Override
		public void onException(Throwable throwable) {
			mTextStatus.setText("Exception: " + throwable.getMessage());
			Log.e(TAG, "Bad thing happened", throwable);
		}

		@Override
		public void onThinking() {
			// show progress bar or something to the user while login is
			// happening
			mTextStatus.setText("Processing...");
		}

		@Override
		public void onLogin() {
			// change the state of the button or do whatever you want
			mTextStatus.setText("Logged in");
			loggedInUIState();
			toast("You are logged in");
		}

		@Override
		public void onNotAcceptingPermissions(Permission.Type type) {
			toast(String.format("You didn't accept %s permissions", type.name()));
		}
	};

	// Logout listener
	private OnLogoutListener mOnLogoutListener = new OnLogoutListener() {

		@Override
		public void onFail(String reason) {
			mTextStatus.setText(reason);
			Log.w(TAG, "Failed to login");
		}

		@Override
		public void onException(Throwable throwable) {
			mTextStatus.setText("Exception: " + throwable.getMessage());
			Log.e(TAG, "Bad thing happened", throwable);
		}

		@Override
		public void onThinking() {
			// show progress bar or something to the user while login is
			// happening
			mTextStatus.setText("Thinking...");
		}

		@Override
		public void onLogout() {
			// change the state of the button or do whatever you want
			mTextStatus.setText("Logged out");
			loggedOutUIState();
			toast("You are logged out");
		}

	};
	
	private void initUI() {
		mButtonLogin = (Button) findViewById(R.id.button_login);
		mButtonLogout = (Button) findViewById(R.id.button_logout);
		mTextStatus = (TextView) findViewById(R.id.text_status);
		mButtonGetFWork = (Button) findViewById(R.id.button_get_friend_data);
		mButtonGetFHome = (Button) findViewById(R.id.button_get_home);
		mBPersonal = (Button) findViewById(R.id.personal);
		mBHomeTwon = (Button) findViewById(R.id.button_get_home);
		mBSunSign = (Button) findViewById(R.id.sunsign);
		mBEducation = (Button) findViewById(R.id.education);
		mBWork = (Button) findViewById(R.id.work);
		mBMore = (Button) findViewById(R.id.more);
	}
	
	private void setUIState() {
		if (mSimpleFacebook.isLogin()) {
			loggedInUIState();
		}
		else {
			loggedOutUIState();
		}
	}
	
	private void toast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	private void loggedInUIState() {
		mButtonLogin.setEnabled(false);
		mButtonLogout.setEnabled(true);
		mButtonGetFWork.setEnabled(true);
		mButtonGetFHome.setEnabled(true);
		mBPersonal.setEnabled(true);
		mBHomeTwon.setEnabled(true);
		mBSunSign.setEnabled(true);
		mBEducation.setEnabled(true);
		mBWork.setEnabled(true);
		mBMore.setEnabled(true);
		mTextStatus.setText("Logged in");
	}
	
	private void loggedOutUIState() {
		mButtonLogin.setEnabled(true);
		mButtonLogout.setEnabled(false);
		mButtonGetFWork.setEnabled(false);
		mButtonGetFHome.setEnabled(false);
		mBPersonal.setEnabled(false);
		mBHomeTwon.setEnabled(false);
		mBSunSign.setEnabled(false);
		mBEducation.setEnabled(false);
		mBWork.setEnabled(false);
		mBMore.setEnabled(false);
		mTextStatus.setText("Logged out");
	}
	
	private void disableButtons(){
		mBPersonal.setEnabled(false);
		mBHomeTwon.setEnabled(false);
		mBSunSign.setEnabled(false);
		mBEducation.setEnabled(false);
		mBWork.setEnabled(false);
		mBMore.setEnabled(false);
	}
	
	
	public void showHomeTown(View v){
		CreateTables ct = new CreateTables(MySocialNetwork.this, "MSN.db", null, 1);
		if(!ct.isTableExists("fb_user")){return;}
		HashMap<String, Integer> hm = ct.getGroupedData("hometown", "fb_user");
		String result = "";
		for (HashMap.Entry<String, Integer> entry : hm.entrySet())
		{   
			if(!entry.getKey().equalsIgnoreCase("null")){  
				String city = entry.getKey();
				String cities[] = city.split(",");
				city = cities[0];
				result = result + "<b>" + entry.getValue() + "</b>"+ 
						"<i> of your friends are from: </i><b>" + 
						city + "</b><br/><br/>";
				city = null;
				
			}
			
		}	
		
		HashMap<String, Integer> hm1 = ct.getGroupedData("location",  "fb_user");
		//toast("Location Results: " + hm1.size());
		result = result + "------------------------<br/><br/>";
		for (HashMap.Entry<String, Integer> entry1 : hm1.entrySet())
		{    
			if(entry1.getKey() == null) {} else{
				String city = entry1.getKey();
				String cities[] = city.split(",");
				city = cities[0];
				result = result + "<b>" + entry1.getValue() + "</b>"+ 
						"<i> of your friends currently live in:  </i><b>" + 
						city + "</b><br/><br/>";
				city = null;
			}
			
		}
		
		// this is just to show the results
		@SuppressWarnings("unchecked")
		AlertDialog dialog = Utils.buildProfileResultDialog(MySocialNetwork.this, 
				new Pair<String, String>("", result));
		dialog.setTitle("HomeTown/Current Location");
		dialog.show();
		TextView msgTxt = (TextView) dialog.findViewById(android.R.id.message);
		msgTxt.setTextSize((float) 13.0);
	}

	public void showPolitics(View v){
		CreateTables ct = new CreateTables(MySocialNetwork.this, "MSN.db", null, 1);
		if(!ct.isTableExists("fb_user")){return;}
		HashMap<String, Integer> hm = ct.getGroupedData("political" , "fb_user");
		String result = "";
		for (HashMap.Entry<String, Integer> entry : hm.entrySet())
		{   
			if(!entry.getKey().equalsIgnoreCase("null")){  
				String city = entry.getKey();
				String cities[] = city.split(",");
				city = cities[0];
				result = result + "<b>" + entry.getValue() + "</b>"+ 
						"<i> of your friends : </i><b>" + 
						city + "</b><br/><br/>";
				city = null;
				
			}
			
		}	
		
		HashMap<String, Integer> hm1 = ct.getGroupedData("religion" , "fb_user");
		//toast("Location Results: " + hm1.size());
		result = result + "------------------------<br/><br/>";
		for (HashMap.Entry<String, Integer> entry1 : hm1.entrySet())
		{    
			if(!entry1.getKey().equalsIgnoreCase("null")){
				String city = entry1.getKey();
				String cities[] = city.split(",");
				city = cities[0];
				result = result + "<b>" + entry1.getValue() + "</b>"+ 
						"<i> of your friends :  </i><b>" + 
						city + "</b><br/><br/>";
				city = null;
			}
			
		}
		
		// this is just to show the results
		@SuppressWarnings("unchecked")
		AlertDialog dialog = Utils.buildProfileResultDialog(MySocialNetwork.this, 
				new Pair<String, String>("", result));
		dialog.setTitle("Political & Religious Beliefs");
		dialog.show();
		TextView msgTxt = (TextView) dialog.findViewById(android.R.id.message);
		msgTxt.setTextSize((float) 13.0);
	}
	
	
	public void showPersonalInfo(View v){
		CreateTables ct = new CreateTables(MySocialNetwork.this, "MSN.db", null, 1);
		if(!ct.isTableExists("fb_user")){return;}
		HashMap<String, Integer> hm = ct.getGroupedData("gender" , "fb_user");
		String result = "";
		for (HashMap.Entry<String, Integer> entry : hm.entrySet())
		{   
			if(!entry.getKey().equalsIgnoreCase("null")){  
				//toast(entry.getValue() + " of your friends are from " + entry.getKey());
				result = result + "<b>" + entry.getValue() + "</b>"+ 
						" <i>of your friends are:  </i><b>" + 
						entry.getKey() + "</b><br/><br/>";
				
			}
			
		}
		
		HashMap<String, Integer> hm1 = ct.getGroupedData("relationship", "fb_user");
		result = result + "------------------------<br/><br/>";
		for (HashMap.Entry<String, Integer> entry1 : hm1.entrySet())
		{   
			if(!entry1.getKey().equalsIgnoreCase("null")){  
				result = result + "<b>" + entry1.getValue() + "</b>"+ 
						"<i> of your friends are:  </i><b>" + 
						entry1.getKey() + "</b><br/><br/>";
				
			}
			
		}
		
		// this is just to show the results
		@SuppressWarnings("unchecked")
		AlertDialog dialog = Utils.buildProfileResultDialog(MySocialNetwork.this, 
				new Pair<String, String>("", result));
		dialog.setTitle("Personal Info");
		dialog.show();
		TextView msgTxt = (TextView) dialog.findViewById(android.R.id.message);
		msgTxt.setTextSize((float) 13.0);
	}
	
	
	public void showWork(View v){
		CreateTables ct = new CreateTables(MySocialNetwork.this, "MSN_1.db", null, 1);
		if(!ct.isTableExists("fb_work")){return;}
		HashMap<String, Integer> hm = ct.getGroupedData("work_name", "fb_work");
		String result = "";
		for (HashMap.Entry<String, Integer> entry : hm.entrySet())
		{   
			if(!entry.getKey().equalsIgnoreCase("null")){  
				//toast(entry.getValue() + " of your friends are from " + entry.getKey());
				result = result + "<b>" + entry.getValue() + "</b>"+ 
						" <i>of your friends:  </i><b>" + 
						entry.getKey() + "</b><br/><br/>";
				
			}
			
		}
		
		// this is just to show the results
		@SuppressWarnings("unchecked")
		AlertDialog dialog = Utils.buildProfileResultDialog(MySocialNetwork.this, 
				new Pair<String, String>("", result));
		dialog.setTitle("Work");
		dialog.show();
		TextView msgTxt = (TextView) dialog.findViewById(android.R.id.message);
		msgTxt.setTextSize((float) 13.0);
	}
	
	public void showEducation(View v){
		CreateTables ct = new CreateTables(MySocialNetwork.this, "MSN_1.db", null, 1);
		if(!ct.isTableExists("fb_work")){return;}
		HashMap<String, Integer> hm = ct.getGroupedData("degree_name", "fb_degree");
		String result = "";
		for (HashMap.Entry<String, Integer> entry : hm.entrySet())
		{   
			if(entry.getKey() == null) {} else{ 
				//toast(entry.getValue() + " of your friends are from " + entry.getKey());
				result = result + "<b>" + entry.getValue() + "</b>"+ 
						" <i>of your friends are:  </i><b>" + 
						entry.getKey() + "</b><br/><br/>";
				
			}
			
		}
		
		// this is just to show the results
		@SuppressWarnings("unchecked")
		AlertDialog dialog = Utils.buildProfileResultDialog(MySocialNetwork.this, 
				new Pair<String, String>("", result));
		dialog.setTitle("Education");
		dialog.show();
		TextView msgTxt = (TextView) dialog.findViewById(android.R.id.message);
		msgTxt.setTextSize((float) 13.0);
	}
	
	
	
	
	
	public void showSunSigns(View v){
		CreateTables ct = new CreateTables(MySocialNetwork.this, "MSN.db", null, 1);
		if(!ct.isTableExists("fb_user")){return;}
		HashMap<String, Integer> hm = ct.getSunSigns();
		String result = "";
		for (HashMap.Entry<String, Integer> entry : hm.entrySet())
		{   

			result = result + "<b>" + entry.getValue() + "</b>"+ 
					" <i> of your friends have zodiac sign: </i> <b>" + 
					entry.getKey() + "</b><br/><br/>";				
			
		}
		
		// this is just to show the results
		@SuppressWarnings("unchecked")
		AlertDialog dialog = Utils.buildProfileResultDialog(MySocialNetwork.this, 
				new Pair<String, String>("", result));
		dialog.setTitle("Zodiac Signs");
		dialog.show();
		TextView msgTxt = (TextView) dialog.findViewById(android.R.id.message);
		msgTxt.setTextSize((float) 13.0);
	}
	
	private void showDialog() {
		mProgress = ProgressDialog.show(this, "Fetching data from facebook", "This can take some time...", true);
	}

	private void hideDialog() {
		if (mProgress != null) {
			mProgress.hide();
		}
	}

	class processWork extends AsyncTask<List<Profile>, String, String>{
		ProgressDialog about  = new ProgressDialog(MySocialNetwork.this);
		
		protected void onPreExecute(){ 
		   about.setMessage("Processing education, work...");
		   about.setCancelable(false);
		   about.setCanceledOnTouchOutside(false);
		   about.show();
		   super.onPreExecute();
		}

		@Override
		protected String doInBackground(List<Profile>... friends) { 
			CreateTables createTable = new CreateTables(MySocialNetwork.this, "MSN_1.db", null, 1);
			createTable.createExtra();
			for (Profile profile : friends[0]) 
			{	
				List<Work> works = profile.getWork();
				for (Work work:  works){
					String work_name = work.getEmployer().getName();
					String work_loc = work.getLocation().getName();

					ContentValues values = new ContentValues();
			        values.put("fb_id", profile.getId());
			        values.put("fb_name", profile.getName());
			        values.put("work_name", work_name);
			        values.put("work_loc", work_loc);
			        createTable.addData(values, "fb_work");
				}
			     
			    List<Education> educations = profile.getEducation();
				for (Education education:  educations){
					String degree_name = education.getDegree();
					String degree_school = education.getSchool();
					
					ContentValues values1 = new ContentValues();
			        values1.put("fb_id", profile.getId());
			        values1.put("fb_name", profile.getName());
			        values1.put("degree_name", degree_name);
			        values1.put("degree_school", degree_school);
			        createTable.addData(values1, "fb_degree");   
				}			
			}
	    	return "done";
		}
		
		@Override
		protected void onPostExecute(String result){
			about.dismiss();
			super.onPostExecute(result);
		   
		}
		
	}
	
	



}
