/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.quizcards;

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
    public static final String HIGH_SCORE = "high_score";
    public static final String MODULE_ID = "module_id";
    
    public static final String ID_TYPE = "id_type";
    
    public static final String NEW_DECK = "new_deck";
    
    public static int QUIZ_LAUNCH = 0;
    public static int STUDY_LAUNCH = 1;
    public static int SELF_TEST_LAUNCH = 2;
    public static int EDIT_LAUNCH = 3;
    
    public static final String SCORE = "score";
    
    // RESULT_FIRST_USER = 1, so need to start custom results above that
    public static int RESULT_INVALID_DECK = 2;
    public static int RESULT_DECK_DELETED = 3;
    
    public static final String SEARCH_TERM = "search_term";
}
