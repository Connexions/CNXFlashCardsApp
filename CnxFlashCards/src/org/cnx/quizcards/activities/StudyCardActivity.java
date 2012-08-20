/**
 * Copyright (c) 2012 Rice University
 *
 * This software is subject to the provisions of the GNU Lesser General
 * Public License Version 2.1 (LGPL).  See LICENSE.txt for details.
 */

package org.cnx.quizcards.activities;

import org.cnx.quizcards.R;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

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
