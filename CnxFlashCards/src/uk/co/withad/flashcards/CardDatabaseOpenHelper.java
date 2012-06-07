package uk.co.withad.flashcards;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import static uk.co.withad.flashcards.Constants.*;


public class CardDatabaseOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	public CardDatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DEFAULT_TABLE_NAME + " (" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + TERM + " STRING, " 
				+ MEANING + " TEXT NOT NULL);");
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
