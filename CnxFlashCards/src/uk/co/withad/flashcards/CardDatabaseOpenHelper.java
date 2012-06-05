package uk.co.withad.flashcards;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class CardDatabaseOpenHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "flashcards.db";
	private static final String CARD_TABLE_NAME = "cards";
	private static final String TERM = "term";
	private static final String MEANING = "meaning";
	private static final int DATABASE_VERSION = 1;

	public CardDatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE" + CARD_TABLE_NAME + " (" + BaseColumns._ID
				+ "INTEGER PRIMARY KEY AUTOINCREMENT, " + TERM + " STRING" 
				+ MEANING + " TEXT NOT NULL");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + CARD_TABLE_NAME);
		onCreate(db);

	}

}
