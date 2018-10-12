package com.tricheer.launcherk218.utility;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper {

	private static final String TAG = "" + "";

	private final static String DATABASE_NAME = "Database.db";
	private final static int DB_VERSION = 2;

	private final static String TABLE_NAME = "apps_data";
	private final static String TABLE_ID = "_id";
	public final static String TABLE_CLASS_NAME = "class_name";
	private Context mContext = null;

	private SQLHelper mDatabaseHelper = null;
	private SQLiteDatabase mSQLiteDatabase = null;

	private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + TABLE_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + TABLE_CLASS_NAME + " TEXT)";

	private static class SQLHelper extends SQLiteOpenHelper {
		private static SQLHelper instantce;
		SQLHelper(Context context) {
			super(context, DATABASE_NAME, null, DB_VERSION);
		}
		public static synchronized  SQLHelper getHelper(Context context){
			if(instantce == null)
				instantce = new SQLHelper(context);
			return instantce;
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(CREATE_TABLE);
			Log.d(TAG, "create table is " + CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}

	public DatabaseHelper(Context context) {
		mContext = context;
	}

	public void openDatabase() {
		/*mDatabaseHelper = new SQLHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();*/
		mSQLiteDatabase = SQLHelper.getHelper(mContext).getWritableDatabase();
	}

	public void insertData(String name) {
		String insert = " INSERT INTO " + TABLE_NAME + " (" + TABLE_CLASS_NAME + " ) values(" + name + ");";
		mSQLiteDatabase.execSQL(insert);
		Log.d(TAG, "inert data sql = " + insert);
	}

	public void insertData(ContentValues values, boolean isopen) {
		// ContentValues value = new ContentValues();
		// value.put("name", "");
		Log.e(TAG, "Transaction -->  beginTransaction");
		mSQLiteDatabase.beginTransaction();
		mSQLiteDatabase.insert(TABLE_NAME, null, values);
		if (isopen) {
			mSQLiteDatabase.endTransaction();
			
			Log.e(TAG, "Transaction-->  endTransaction");
		}
	}

	public ArrayList<String> getData() {
		ArrayList<String> allApps = new ArrayList<String>();

		try {
			Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						int index = cursor.getColumnIndex(TABLE_CLASS_NAME);
						String name = cursor.getString(index);
						allApps.add(name);
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
		} catch (SQLiteException e) {
			Log.d(TAG, "query exception is " + e.toString());
			// return null;
		}

		return allApps;
	}

	public void close() {
		mDatabaseHelper.close();
	}

	public void deleteAll() {
		mSQLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
	}

	public void clear() {
		mSQLiteDatabase.delete(TABLE_NAME, null, null);
	}
}
