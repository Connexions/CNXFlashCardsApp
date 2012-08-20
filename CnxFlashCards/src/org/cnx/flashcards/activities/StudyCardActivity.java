/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.flashcards.activities;

import org.cnx.flashcards.R;
import org.cnx.flashcards.R.id;
import org.cnx.flashcards.R.layout;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import static org.cnx.flashcards.Constants.*;

public class StudyCardActivity extends CardActivity {

    private TextView meaningText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.cards);

        meaningText = (TextView) findViewById(R.id.meaningText);
        meaningText.setTextColor(Color.WHITE);
        meaningText.setOnTouchListener(this);
        meaningText.setClickable(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    void setMeaningText() {
        meaningText.setText(definitions.get(currentCard)[1]);
    }

    @Override
    boolean checkIfValidDeck() {
        return definitions.size() > 0;
    }

}
