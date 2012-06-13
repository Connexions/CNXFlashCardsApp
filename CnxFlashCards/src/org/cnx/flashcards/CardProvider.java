package org.cnx.flashcards;

import static org.cnx.flashcards.Constants.CARDS_TABLE;
import static org.cnx.flashcards.Constants.TAG;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class CardProvider extends ContentProvider {

	private CardDatabaseOpenHelper helper;
	public static final Uri CONTENT_URI = Uri.parse("content://org.cnx.flashcards.CardProvider");
	
	
	@Override
	public boolean onCreate() {
		helper = new CardDatabaseOpenHelper(getContext());
		return true;
	}
	
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/** Inserts values into the cards table.
	 * TODO: Modify to return the right URI, handle duplicates.
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase cardsdb = helper.getWritableDatabase();
		
		cardsdb.insertOrThrow(CARDS_TABLE, null, values);
		return null;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {
		
		SQLiteDatabase db = helper.getReadableDatabase();
		
		Cursor c = db.query(CARDS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
