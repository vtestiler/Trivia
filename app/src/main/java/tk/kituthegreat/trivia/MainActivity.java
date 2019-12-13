package tk.kituthegreat.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tk.kituthegreat.trivia.data.AnswerListAsyncResponse;
import tk.kituthegreat.trivia.data.QuestionBank;
import tk.kituthegreat.trivia.model.Question;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String PREFS_ID = "trivia_prefs";

    private TextView questionTextview;
    private TextView questionCounterTextview;
    private TextView scoreTextView;
    private Button trueButton;
    private Button falseButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private int currentQuestionIndex = 0;
    private int numberOfRightAnswers = 0;
    private float score = 0;
    private List<Question> questionList;
    private AnimationSound next_prev_button_sound;
    private AnimationSound lose_sound;
    private AnimationSound win_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextButton = findViewById(R.id.next_btn);
        prevButton = findViewById(R.id.prev_btn);
        trueButton = findViewById(R.id.true_btn);
        falseButton = findViewById(R.id.false_btn);
        questionTextview = findViewById(R.id.question_textview);
        questionCounterTextview = findViewById(R.id.counter_text);
        scoreTextView = findViewById(R.id.score_value);
        next_prev_button_sound = new AnimationSound(this, R.raw.next_prev);
        lose_sound = new AnimationSound(this, R.raw.lose);
        win_sound = new AnimationSound(this, R.raw.win);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);


        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @SuppressLint("SetTextI18n")
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTextview.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextview.setText(currentQuestionIndex + " /" + questionArrayList.size());
                updateScore();
                //Log.d("Main", "onCreate: " + questionArrayList);
            }
        });

        //Get data back from SP
        SharedPreferences getShareData = getSharedPreferences(PREFS_ID, MODE_PRIVATE);
        numberOfRightAnswers = getShareData.getInt("right_answers", 0);
        currentQuestionIndex = getShareData.getInt("index", 0);
        Log.d("From SP", "currentQuestionIndex from SP: " + currentQuestionIndex);
        Log.d("From SP", "numberOfRightAnswers from SP: "+ numberOfRightAnswers);
        //updateUI();



    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.prev_btn:
                currentQuestionIndex = (((currentQuestionIndex - 1) % questionList.size()) + questionList.size()) % questionList.size();
                updateUI();
                next_prev_button_sound.startsound();
                break;
            case R.id.next_btn:
                currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
                updateUI();
                next_prev_button_sound.startsound();
                break;
            case R.id.true_btn:
                checkAnswer(true);
                updateUI();
                win_sound.startsound();
                break;
            case R.id.false_btn:
                checkAnswer(false);
                updateUI();
                lose_sound.startsound();
                break;
        }

    }

    private void checkAnswer(boolean b) {
        boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        if(b == answer){
            // True answer
            //translateAnimation();
            if(numberOfRightAnswers <= questionList.size()){
                numberOfRightAnswers++;
                //Log.d("Score", "Number in checkAnswer = " + numberOfRightAnswers);
            }

            fadeView();
            Toast.makeText(this,"Correct!", Toast.LENGTH_SHORT).show();

        }else {
            // False answer
            shakeAnimation();
            Toast.makeText(this,"Wrong Answer!", Toast.LENGTH_SHORT).show();
        }
        //currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(){
        questionTextview.setText(questionList.get(currentQuestionIndex).getAnswer());
        questionCounterTextview.setText(currentQuestionIndex + " /" + questionList.size());
        updateScore();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("right_answers", numberOfRightAnswers);
        editor.putInt("index", currentQuestionIndex);

        editor.apply();  //saving to disk
    }
    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void updateScore(){
        score = (float)numberOfRightAnswers / (float)questionList.size() * 100.0f;
        int scored = (int)Math.round(score);
        //score = (int) Math.ceil((double) 6 / 900) ;
        Log.d("Score", "Number in updateUI = " + numberOfRightAnswers);
        Log.d("Score", "questionList.size() = " + questionList.size());
        Log.d("Score", "Score = " + score);
        scoreTextView.setText(scored +"%");
    }

    private void translateAnimation(){
        Animation translate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.translate_animation);
        CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(translate);
    }

    private void fadeView(){
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



    }
}
