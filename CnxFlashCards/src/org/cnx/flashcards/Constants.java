/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.flashcards;

public interface Constants {
    public static final String TAG = "CNXFlashCards";

    public static final String DATABASE_NAME = "flashcards.db";

    public static final String CARDS_TABLE = "cards";
    public static final String DECK_ID = "deck_id";
    public static final String TERM = "term";
    public static final String MEANING = "meaning";

    public static final String DECKS_TABLE = "decks";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String DATE = "date";
    public static final String ABSTRACT = "abstract";
    public static final String MODIFIED = "modified";
    public static final String NOTES = "notes";

    // public static final String TEST_ID = "m9006/2.22";
    public static final String TEST_ID = "testfile";
    
    public static int QUIZ_LAUNCH = 0;
    public static int STUDY_LAUNCH = 1;
    public static int SELF_TEST_LAUNCH = 2;
    
    public static final String SCORE = "score";
    
    // RESULT_FIRST_USER = 1, so need to start custom results above that
    public static int RESULT_INVALID_DECK = 2;
}
