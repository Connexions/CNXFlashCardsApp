package org.cnx.flashcards;

import java.util.ArrayList;
import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import static org.cnx.flashcards.Constants.*;

public class QuizCardActivity extends CardActivity {

    RadioGroup answersGroup;
    ArrayList<RadioButton> answerButtons;
    int rightButton = -1;
    Random rand;
    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.quizmode);
        rand = new Random();

        // Get UI elements
        answersGroup = (RadioGroup) findViewById(R.id.answersRadioGroup);
        answerButtons = new ArrayList<RadioButton>();
        answerButtons.add((RadioButton) findViewById(R.id.answer1));
        answerButtons.add((RadioButton) findViewById(R.id.answer2));
        answerButtons.add((RadioButton) findViewById(R.id.answer3));

        super.onCreate(savedInstanceState);
    }

    /*
     * Display the right answer and two wrong ones 
     * TODO: Generalise for more wrong answers
     */
    @Override
    void setMeaningText() {
        String rightAnswer = definitions.get(currentCard)[1];
        rightButton = rand.nextInt(3);

        int wrongAnswer1 = -1;
        int wrongAnswer2 = -1;
        
        // Valid check should prevent this from hanging.
        while ((wrongAnswer1 = rand.nextInt(definitions.size())) == currentCard
                || (wrongAnswer2 = rand.nextInt(definitions.size())) == currentCard
                || wrongAnswer1 == wrongAnswer2) {
        }

        answerButtons.get(rightButton).setText(rightAnswer);
        answerButtons.get((rightButton + 1) % 3).setText(
                definitions.get(wrongAnswer1)[1]);
        answerButtons.get((rightButton + 2) % 3).setText(
                definitions.get(wrongAnswer2)[1]);
    }
    

    @Override
    protected void nextCard() {
        if(answerButtons.get(rightButton).getId() == answersGroup.getCheckedRadioButtonId()) {
            score++;
            Log.d(TAG, "Right answer! Score is now " + score);
        }
        else {
            Log.d(TAG, "Wrong answer!");
        }
        
        answersGroup.clearCheck();
        super.nextCard();
    }
    

    @Override
    boolean checkIfValidDeck() {
        boolean valid = definitions.size() >= 3;
     
        return valid;
    }

}
