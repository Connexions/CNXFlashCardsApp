package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.CARDS_TABLE;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static org.cnx.flashcards.Constants.*;


public class DeckProvider extends ContentProvider {
	
	private CardDatabaseOpenHelper helper;
	public static final Uri CONTENT_URI = Uri.parse("content://org.cnx.flashcards.DeckProvider");
	
	
	@Override
	public boolean onCreate() {
		helper = new CardDatabaseOpenHelper(getContext());
		return true;
	}
	

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = helper.getWritableDatabase();
		
		db.insertOrThrow(DECKS_TABLE, null, values);
		return null;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
			
		SQLiteDatabase cardsdb = helper.getReadableDatabase();
		
		Cursor c = cardsdb.query(DECKS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
