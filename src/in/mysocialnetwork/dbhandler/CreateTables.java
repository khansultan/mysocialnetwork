package in.mysocialnetwork.dbhandler;
import in.mysocialnetwork.helpers.FBFriend;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class CreateTables  extends SQLiteOpenHelper {

	//SQLiteDatabase db; 
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "MSN.db";
	private static final String DATABASE_NAME1 = "MSN_1.db";
	private static final String TABLE_USERS = "fb_user";
	private static final String TABLE_LANG = "fb_lang";
	private static final String TABLE_WORK = "fb_work";
	private static final String TABLE_DEGREE = "fb_degree";
	private static final String TABLE_TEAM = "fb_team";
	private static final String TABLE_ATHLETE = "fb_athlete";
	
	public static final String COLUMN_USERID = "_id";
	public static final String COLUMN_FB_ID = "fb_id";
	public static final String COLUMN_FB_NAME = "fb_name";
	public static final String COLUMN_AGE_RANGE = "age_range";
	public static final String COLUMN_BDAY = "bday";
	public static final String COLUMN_HOME = "hometown"; 
	public static final String COLUMN_LOCATION = "location"; 
	public static final String COLUMN_GENDER = "gender"; 
	public static final String COLUMN_RELIGION = "religion";
	public static final String COLUMN_POLITICAL = "political";
	public static final String COLUMN_RELATIONSHIP = "relationship";
	
	public static final String COLUMN_FB_LANG = "language";
	public static final String COLUMN_FB_LANGID = "langid";
	
	public static final String COLUMN_FB_WORK = "work_name";
	public static final String COLUMN_FB_WORKLOC = "work_loc";
	
	public static final String COLUMN_FB_DEGREESCHOOL = "degree_school";
	public static final String COLUMN_FB_DEGREENAME = "degree_name";
	
	public static final String COLUMN_FB_TEAM = "fb_team";
	public static final String COLUMN_FB_ATHLETE = "fb_athlete";

		
	public CreateTables(Context context, String name, 
			CursorFactory factory, int version) {
		super(context, name, factory, DATABASE_VERSION);

	}
		
	@Override
	public void onCreate(SQLiteDatabase db) {
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLS);
		String CREATE_USER_TABLE = "CREATE TABLE " +
				TABLE_USERS + "("
	             + COLUMN_USERID + " INTEGER PRIMARY KEY," + 
	             COLUMN_FB_ID + " TEXT," + 
	             COLUMN_FB_NAME + " TEXT," + 
	             COLUMN_AGE_RANGE + " TEXT," +
	             COLUMN_BDAY + " TEXT," +
	             COLUMN_HOME + " TEXT," + 
	             COLUMN_LOCATION + " TEXT," + 
	             COLUMN_GENDER + " TEXT," + 
	             COLUMN_RELIGION + " TEXT," + 	 
	             COLUMN_RELATIONSHIP + " TEXT," + 
	             COLUMN_POLITICAL + " TEXT" + ")";
		
	    try{
        	db.execSQL(CREATE_USER_TABLE);
        }
        catch(SQLException e){
        	Log.d("USER TABLE CREATION", e.toString());
        }

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
	    onCreate(db);

	}
	

	public void createExtra() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORK);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEGREE);
		
		String CREATE_WORK_TABLE = "CREATE TABLE " +
				TABLE_WORK + "(" +
	             COLUMN_FB_ID + " TEXT," + 
	             COLUMN_FB_NAME + " TEXT," + 
	             COLUMN_FB_WORKLOC + " TEXT," + 
	             COLUMN_FB_WORK + " TEXT" + ")";
		
		String CREATE_DEGREE_TABLE = "CREATE TABLE " +
				TABLE_DEGREE + "(" +
	             COLUMN_FB_ID + " TEXT," + 
	             COLUMN_FB_NAME + " TEXT," + 
	             COLUMN_FB_DEGREESCHOOL + " TEXT," + 
	             COLUMN_FB_DEGREENAME + " TEXT" + ")";
		
	    try{
        	db.execSQL(CREATE_WORK_TABLE);
        	db.execSQL(CREATE_DEGREE_TABLE);
        }
        catch(SQLException e){
        	Log.d("USER TABLE CREATION", e.toString());
        }

	}

		

	public void createTables()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		onUpgrade(db,0,0);
		
	}
		
	public boolean addUser(FBFriend userObject){
		ContentValues values = new ContentValues();
        values.put(COLUMN_FB_ID, userObject.getFb_id());
        values.put(COLUMN_FB_NAME, userObject.getFb_name());
        values.put(COLUMN_AGE_RANGE, userObject.getAge_range());
        values.put(COLUMN_BDAY, userObject.getBday());
        values.put(COLUMN_HOME, userObject.getHometown());
        values.put(COLUMN_LOCATION, userObject.getLocation());
        values.put(COLUMN_GENDER, userObject.getGender());
        values.put(COLUMN_RELIGION, userObject.getReligion());
        values.put(COLUMN_RELATIONSHIP, userObject.getRelationship());
        values.put(COLUMN_POLITICAL, userObject.getPolitics());

        SQLiteDatabase db = this.getWritableDatabase();
        
        try{
        	db.insertOrThrow(TABLE_USERS, null, values);
        }
        catch(SQLException e){
        	Log.d("USER Insert ", e.toString());
        	return false;
        }
        
        db.close();
        return true;

	}
	

	
	public boolean addData(ContentValues values, String table_name){

        SQLiteDatabase db = this.getWritableDatabase();
        
        try{
        	db.insertOrThrow(table_name, null, values);
        }
        catch(SQLException e){
        	Log.d("USER Insert " + table_name, e.toString());
        	return false;
        }
        
        db.close();
        return true;

	}
	
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Integer> getSunSigns() {
		int Aquarius =0 , Pisces =0, Aries =0, Taurus =0, Gemini =0, Cancer =0, Leo=0;
		int Virgo=0, Libra=0, Scorpion=0, Sagittarius=0, Capricorn = 0;
		String query = "select " + COLUMN_BDAY + " from " + TABLE_USERS ;
		SQLiteDatabase db = this.getWritableDatabase();	
		Cursor cursor = db.rawQuery(query, null);	
		HashMap<String, Integer> home_map = new HashMap<String, Integer>();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) 
		{
			String bday = cursor.getString(cursor.getColumnIndex(COLUMN_BDAY));
			
			if(!bday.equalsIgnoreCase("null")){
				String bdays[] = bday.split("/");
				int month = Integer.parseInt(bdays[0]);
				int day = Integer.parseInt(bdays[1]);
				String sign = getSign(month, day);
				
				if(sign.equalsIgnoreCase("Aquarius")){
					Aquarius++;
					home_map.put("Aquarius",Aquarius);
				}
				else if(sign.equalsIgnoreCase("Pisces")){
					Pisces++;
					home_map.put("Pisces",Pisces);
				}
				else if(sign.equalsIgnoreCase("Aries")){
					Aries++;	
					home_map.put("Aries",Aries);
				}
				else if(sign.equalsIgnoreCase("Taurus")){
					Taurus++;	
					home_map.put("Taurus",Taurus);
				}
				else if(sign.equalsIgnoreCase("Gemini")){
					Gemini++;	
					home_map.put("Gemini",Gemini);
				}
				else if(sign.equalsIgnoreCase("Cancer")){
					Cancer++;	
					home_map.put("Cancer",Cancer);
				}
				else if(sign.equalsIgnoreCase("Leo")){
					Leo++;	
					home_map.put("Leo",Leo);
				}
				else if(sign.equalsIgnoreCase("Virgo")){
					Virgo++;
					home_map.put("Virgo",Virgo);
				}
				else if(sign.equalsIgnoreCase("Libra")){
					Libra++;
					home_map.put("Libra",Libra);
				}
				else if(sign.equalsIgnoreCase("Scorpion")){
					Scorpion++;	
					home_map.put("Scorpion",Scorpion);
				}
				else if(sign.equalsIgnoreCase("Sagittarius")){
					Sagittarius++;	
					home_map.put("Sagittarius",Sagittarius);
				}
				else if(sign.equalsIgnoreCase("Capricorn")){
					Capricorn++;	
					home_map.put("Capricorn",Capricorn);
				}						
			}
			cursor.moveToNext();			
		} 
		cursor.close();
        db.close();        
        return (HashMap<String, Integer>) sortByValue(home_map);
	}
	

	
	@SuppressWarnings("unchecked")
	public HashMap<String, Integer> getGroupedData(String COLUMN_SELECTION, String TABLE) {
		String query = "select count(*) AS cnt, " + COLUMN_SELECTION + " from " + TABLE + 
				" group by " + COLUMN_SELECTION + " order by cnt DESC LIMIT 5";
		SQLiteDatabase db = this.getWritableDatabase();	
		Cursor cursor = db.rawQuery(query, null);	
		HashMap<String, Integer> home_map = new HashMap<String, Integer>();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) 
		{
			home_map.put(cursor.getString(cursor.getColumnIndex(COLUMN_SELECTION)), 
					cursor.getInt(cursor.getColumnIndex("cnt")));
			cursor.moveToNext();
			
		} 
		cursor.close();
        db.close();
        
		return (HashMap<String, Integer>) sortByValue(home_map);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static Map sortByValue(Map map) {
	     List list = new LinkedList(map.entrySet());
	     Collections.sort(list, new Comparator() {
	          public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o2)).getValue())
	              .compareTo(((Map.Entry) (o1)).getValue());
	          }
	     });

	    Map result = new LinkedHashMap();
	    for (Iterator it = list.iterator(); it.hasNext();) {
	        Map.Entry entry = (Map.Entry)it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	} 
	
	public String getSign(int month, int day){
	     
		if((month==1 && day>20)||(month==2 && day<20)){
	          return "Aquarius";
	     }else if((month==2 && day>18 )||(month==3 && day<21)){
	          return "Pisces";
	     }else if((month==3 && day>20)||(month==4 && day<21)){
	          return "Aries";
	     }else if((month==4 && day>20)||(month==5 && day<22)){
	          return "Taurus";
	     }else if((month==5 && day>21)||(month==6 && day<22)){
	          return "Gemini";
	     }else if((month==6 && day>21)||(month==7 && day<24)){
	          return "Cancer";
	     }else if((month==7 && day>23)||(month==8 && day<24)){
	          return "Leo";
	     }else if((month==8 && day>23)||(month==9 && day<24)){
	          return "Virgo";
	     }else if((month==9 && day>23)||(month==10 && day<24)){
	          return "Libra";
	     }else if((month==10 && day>23)||(month==11 && day<23)){
	          return "Scorpion";
	     }else if((month==11 && day>22)||(month==12 && day<23)){
	          return "Sagittarius";
	     }else if((month==12 && day>22)||(month==1 && day<21)){
	          return "Capricorn";
	     }
	     else return "";
	}
	
	public boolean isTableExists(String table){
		String query = "SELECT name FROM sqlite_master WHERE type ='table' AND name = '" + table + "'";
		
		SQLiteDatabase db = this.getWritableDatabase();	
		Cursor cursor = db.rawQuery(query, null);			
		if (!cursor.moveToFirst())
	    {
			cursor.close();
			db.close();
	        return false;
	    }
		cursor.close();
		db.close();
		return true;
	}
		
	
		
		

}



