package com.example.quizzapplication;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String RESULT_SCORE = "RESULT_SCORE";

    private static final String BUNDLE_STATE_SCORE = "BUNDLE_STATE_SCORE";
    private static final String BUNDLE_STATE_QUESTION_COUNT = "BUNDLE_STATE_QUESTION_COUNT";
    private static final String BUNDLE_STATE_QUESTION_BANK = "BUNDLE_STATE_QUESTION_BANK";

    private static final int INITIAL_QUESTION_COUNT = 4;

    private TextView mQuestionTextView;
    private Button mAnswerButton1;
    private Button mAnswerButton2;
    private Button mAnswerButton3;
    private Button mAnswerButton4;

    private int mScore;
    private int mRemainingQuestionCount;
    private QuestionBank mQuestionBank;

    private boolean mEnableTouchEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mEnableTouchEvents = true;

        mQuestionTextView = (TextView) findViewById(R.id.game_activity_textview_question);
        mAnswerButton1 = (Button) findViewById(R.id.game_activity_button_1);
        mAnswerButton2 = (Button) findViewById(R.id.game_activity_button_2);
        mAnswerButton3 = (Button) findViewById(R.id.game_activity_button_3);
        mAnswerButton4 = (Button) findViewById(R.id.game_activity_button_4);

        mAnswerButton1.setOnClickListener(this);
        mAnswerButton2.setOnClickListener(this);
        mAnswerButton3.setOnClickListener(this);
        mAnswerButton4.setOnClickListener(this);

        if (savedInstanceState != null) {
            mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
            mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION_COUNT);
            mQuestionBank = (QuestionBank) savedInstanceState.getSerializable(BUNDLE_STATE_QUESTION_BANK);
        } else {
            mScore = 0;
            mRemainingQuestionCount = INITIAL_QUESTION_COUNT;
            mQuestionBank = generateQuestionBank();
        }

        displayQuestion(mQuestionBank.getCurrentQuestion());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(BUNDLE_STATE_SCORE, mScore);
        outState.putInt(BUNDLE_STATE_QUESTION_COUNT, mRemainingQuestionCount);
        outState.putSerializable(BUNDLE_STATE_QUESTION_BANK, mQuestionBank);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mEnableTouchEvents && super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        int index;

        if (v == mAnswerButton1) {
            index = 0;
        } else if (v == mAnswerButton2) {
            index = 1;
        } else if (v == mAnswerButton3) {
            index = 2;
        } else if (v == mAnswerButton4) {
            index = 3;
        } else {
            throw new IllegalStateException("Unknown clicked view : " + v);
        }

        if (index == mQuestionBank.getCurrentQuestion().getAnswerIndex()) {
            Toast.makeText(this, "Bonne réponse !", Toast.LENGTH_SHORT).show();
            mScore++;
        } else {
            Toast.makeText(this, "Mauvaise réponse!", Toast.LENGTH_SHORT).show();
        }

        mEnableTouchEvents = false;

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mEnableTouchEvents = true;

                mRemainingQuestionCount--;

                if (mRemainingQuestionCount <= 0) {
                    endGame();
                } else {
                    displayQuestion(mQuestionBank.getNextQuestion());
                }
            }
        }, 2_000);
    }

    private void displayQuestion(final Question question) {
        mQuestionTextView.setText(question.getQuestion());
        mAnswerButton1.setText(question.getChoiceList().get(0));
        mAnswerButton2.setText(question.getChoiceList().get(1));
        mAnswerButton3.setText(question.getChoiceList().get(2));
        mAnswerButton4.setText(question.getChoiceList().get(3));
    }


    private void endGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Partie terminée !")
                .setMessage("Votre score est : " + mScore)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(RESULT_SCORE, mScore);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                })
                .create()
                .show();
    }

    private QuestionBank generateQuestionBank() {
        Question question1 = new Question(
                "Quel animal n'est pas carnivore",
                Arrays.asList(
                        "Le lion",
                        "Le crocodile",
                        "Le lapin",
                        "Le bernard-lermitte"
                ),
                2
        );

        Question question2 = new Question(
                "En quel année l'homme a été sur la lune ?",
                Arrays.asList(
                        "1958",
                        "1962",
                        "1967",
                        "1969"
                ),
                3
        );

        Question question3 = new Question(
                "Quel animal est le plus gros",
                Arrays.asList(
                        "Le dauphin",
                        "La baleine",
                        "La girafe",
                        "Le dindon"
                ),
                1
        );

        Question question4 = new Question(
                "Qui a peint Mona Lisa?",
                Arrays.asList(
                        "Michelangelo",
                        "Leonardo Da Vinci",
                        "Raphael",
                        "Carravagio"
                ),
                1
        );

        Question question5 = new Question(
                "De quel pays Brasilia est la capitale?",
                Arrays.asList(
                        "Brésil",
                        "Cambodge",
                        "Pérou",
                        "Normandie"
                ),
                0
        );

        return new QuestionBank(Arrays.asList(question1, question2, question3, question4, question5));
    }
}