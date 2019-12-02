package tk.kituthegreat.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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

import tk.kituthegreat.trivia.controller.AppController;
import tk.kituthegreat.trivia.data.AnswerListAsyncResponse;
import tk.kituthegreat.trivia.data.QuestionBank;
import tk.kituthegreat.trivia.model.Question;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextview;
    private TextView questionCounterTextview;
    private Button trueButton;
    private Button falseButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private AnimationSound animationSound;

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
        animationSound = new AnimationSound(this, R.raw.cashreg);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);


        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTextview.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextview.setText(currentQuestionIndex + " /" + questionArrayList.size());
                //Log.d("Main", "onCreate: " + questionArrayList);
            }
        });



    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.prev_btn:
                currentQuestionIndex = (((currentQuestionIndex - 1) % questionList.size()) + questionList.size()) % questionList.size();
                updateQuestion();
                animationSound.startsound();
                break;
            case R.id.next_btn:
                currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
                updateQuestion();
                animationSound.startsound();
                break;
            case R.id.true_btn:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_btn:
                checkAnswer(false);
                updateQuestion();
                break;
        }

    }

    private void checkAnswer(boolean b) {
        boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        if(b == answer){
            // True answer
            //translateAnimation();
            fadeView();
            Toast.makeText(this,"Correct!", Toast.LENGTH_SHORT).show();

        }else {
            // False answer
            shakeAnimation();
            Toast.makeText(this,"Wrong Answer!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateQuestion(){
        questionTextview.setText(questionList.get(currentQuestionIndex).getAnswer());
        questionCounterTextview.setText(currentQuestionIndex + " /" + questionList.size());
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
