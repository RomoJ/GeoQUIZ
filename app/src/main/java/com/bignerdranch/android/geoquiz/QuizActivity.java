package com.bignerdranch.android.geoquiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = QuizActivity.class.toString();

    private static final String KEY_INDEX = "index";
    private static final String KEY_QUESTION_ANSWERED = "question_answered";
    private static final String KEY_QUESTION_ANSWERED_CORRECTLY = "question_answered_correctly";

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private boolean[] mQuestionAnswered;

    private boolean[] mQuestionAnsweredCorrectly;

    private int mCurrentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mQuestionAnswered = savedInstanceState.getBooleanArray(KEY_QUESTION_ANSWERED);
            mQuestionAnsweredCorrectly = savedInstanceState.getBooleanArray(KEY_QUESTION_ANSWERED_CORRECTLY);
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        } else {
            mQuestionAnswered = new boolean[mQuestionBank.length];
            mQuestionAnsweredCorrectly = new boolean[mQuestionBank.length];
            mCurrentIndex = 0;
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementIndex();

                updateQuestion();

                configureButtons();
            }
        });


        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);

                configureButtons();
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);

                configureButtons();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementIndex();

                updateQuestion();

                configureButtons();
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementIndex();

                updateQuestion();

                configureButtons();
            }
        });

        updateQuestion();

        configureButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu called");
        getMenuInflater().inflate(R.menu.activity_quiz, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putBooleanArray(KEY_QUESTION_ANSWERED, mQuestionAnswered);
        savedInstanceState.putBooleanArray(KEY_QUESTION_ANSWERED_CORRECTLY, mQuestionAnsweredCorrectly);
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                resetQuizState();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void decrementIndex() {
        mCurrentIndex = (mCurrentIndex == 0) ? mQuestionBank.length - 1 : mCurrentIndex - 1;
    }

    private void incrementIndex() {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void configureButtons() {
        if (mQuestionAnswered[mCurrentIndex]) {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        } else {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        mQuestionAnswered[mCurrentIndex] = true;

        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (userPressedTrue == answerIsTrue) {
            mQuestionAnsweredCorrectly[mCurrentIndex] = true;
            messageResId = R.string.correct_toast;
        } else {
            mQuestionAnsweredCorrectly[mCurrentIndex] = false;
            messageResId = R.string.incorrect_toast;
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
                .show();

        if (isQuizCompleted()) {
            gradeQuiz();
        }
    }

    private void resetQuizState() {
        mCurrentIndex = 0;

        for (int i = 0; i < mQuestionAnswered.length; i++) {
            mQuestionAnswered[i] = false;
        }

        configureButtons();

        updateQuestion();

        Toast.makeText(this, R.string.exam_reset, Toast.LENGTH_SHORT).show();
    }

    private boolean isQuizCompleted() {
        boolean quizCompleted = true;

        for (boolean questionAnswered : mQuestionAnswered) {
            if (!questionAnswered) {
                quizCompleted = false;
                break;
            }
        }

        return quizCompleted;
    }

    private void gradeQuiz() {
        int questionsAnsweredCorrectly = 0;
        int totalQuestions = mQuestionBank.length;

        for (boolean questionAnsweredCorrectly : mQuestionAnsweredCorrectly) {
            if (questionAnsweredCorrectly) {
                questionsAnsweredCorrectly++;
            }
        }

        double score = (double) questionsAnsweredCorrectly / totalQuestions * 100;

        Toast.makeText(this, "You were " + Math.round(score) + "% correct", Toast.LENGTH_SHORT).show();
    }
}
