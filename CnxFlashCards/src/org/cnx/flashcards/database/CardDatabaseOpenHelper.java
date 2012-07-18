/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.flashcards.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import static org.cnx.flashcards.Constants.*;

public class CardDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;

    public CardDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CARDS_TABLE + " (" + 
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                DECK_ID + " INT, " + 
                TERM + " STRING, " + 
                MEANING + " TEXT NOT NULL);");

        db.execSQL("CREATE TABLE " + DECKS_TABLE + " (" + 
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                MODULE_ID + " STRING, " + 
                TITLE + " STRING, " + 
                AUTHOR + " STRING, " + 
                MODIFIED + " BOOLEAN, " + 
                ABSTRACT + " TEXT, " +
                HIGH_SCORE + " INT, " +
                NOTES + " TEXT);");
    }

    /**
     * Run when database is upgraded. Currently doesn't do anything clever, just
     * drops tables and creates new ones.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from v. " + oldVersion + " to v. "
                + newVersion + ".");

        db.execSQL("DROP TABLE IF EXISTS " + DECKS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CARDS_TABLE);

        onCreate(db);
    }
}
