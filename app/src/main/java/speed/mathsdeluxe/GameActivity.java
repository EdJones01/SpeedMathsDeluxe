package speed.mathsdeluxe;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.digitalink.DigitalInkRecognition;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions;
import com.google.mlkit.vision.digitalink.RecognitionCandidate;

import java.util.LinkedList;
import java.util.List;

public class GameActivity extends Activity {
    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    private float timeTaken = 0;

    private Thread timerThread;

    private static GameActivity instance;

    private DrawingView drawingView;

    private TextView currentQuestionView;
    private TextView nextQuestionView;
    private TextView timerView;
    private TextView countdownView;

    private int countdown = 4;

    private int numberOfQuestions;
    private String savecode;

    LinkedList<Equation> questions;

    Game game;

    private Button clearButton;

    private DigitalInkRecognizer recognizer;

    boolean soundMuted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sharedPreferences = getApplication().getSharedPreferences("speed.mathsdeluxe", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        instance = this;

        soundMuted = sharedPreferences.getBoolean("mute", false);

        findViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            game = extras.getParcelable("game");
        }

        Gson gson = new Gson();
        game = gson.fromJson(getIntent().getStringExtra("game"), Game.class);
        numberOfQuestions = game.getNumberOfQuestions();
        questions = game.getQuestions();
        savecode = game.getSavecode();

        nextQuestionView.setText(getNextQuestion().toString());
        currentQuestionView.setText(getCurrentQuestion().toString());

        setupInk();

        createTimer();

        countdownView.setText(countdown + "");
        playSound(R.raw.countdown);

        Log.i("myLog1", savecode);

        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                countdown -= 1;
                countdownView.setText(countdown + "");
                countdownAnimation(countdownView);
            }

            public void onFinish() {
                countdownView.setVisibility(View.GONE);
                clearButton.setVisibility(View.VISIBLE);
                drawingView.clear();
                timerThread.start();
            }
        }.start();
    }

    float getBestSavedTime() {
        try {
            return Float.parseFloat(sharedPreferences.getString(savecode, ""));
        } catch (Exception e) {
            return 0;
        }
    }

    Equation getCurrentQuestion() {
        try {
            return questions.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    Equation getNextQuestion() {
        try {
            return questions.get(1);
        } catch (Exception e) {
            return null;
        }
    }

    void finishGame() {
        float savedBest = getBestSavedTime();
        String message = "";

        if(timeTaken < savedBest) {
            message = "\nYou beat your highscore of " + getBestSavedTime();
        } else if (savedBest != 0.0f) {
            message = "\nYour previous highscore is " + getBestSavedTime();
        }
        if (savedBest == 0.0f || timeTaken < savedBest) {
            editor.putString(savecode, getTimeTaken());
            editor.commit();
        }

        showWinDialog(message);
    }

    public void changeQuestion() {
        questions.pop();
        if (!(getCurrentQuestion() == null)) {
            currentQuestionView.setText(getCurrentQuestion().toString());
            greyToBlackAnimation(currentQuestionView);
        } else {
            currentQuestionView.setText("");
            timerThread.interrupt();
            finishGame();
        }
        if (!(getNextQuestion() == null)) {
            nextQuestionView.setText(getNextQuestion().toString());
        } else {
            nextQuestionView.setText("");
        }
        moveQuestionAnimation(currentQuestionView);
        moveQuestionAnimation(nextQuestionView);
        growAnimation(currentQuestionView);
    }

    void createTimer() {
        timerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!timerThread.isInterrupted()) {
                        Thread.sleep(10);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeTaken += 0.01;
                                timerView.setText(getTimeTaken() + "s");
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
    }

    private String getTimeTaken() {
        return String.format("%.2f", timeTaken);
    }

    void moveQuestionAnimation(TextView view) {
        int distance;
        if (view == currentQuestionView)
            distance = (int) (view.getX() - nextQuestionView.getX());
        else
            distance = (int) (view.getX() + 300);

        ObjectAnimator moveLeft = ObjectAnimator.ofFloat(view, "translationX", 0, -distance);
        moveLeft.setDuration(0);
        moveLeft.start();

        ObjectAnimator moveRight = ObjectAnimator.ofFloat(view, "translationX", -distance, 0);
        moveRight.setDuration(500);
        moveRight.setInterpolator(new AccelerateInterpolator());
        moveRight.start();

    }

    private void showWinDialog(String message) {
        new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("You Win!")
                .setMessage("You got " + numberOfQuestions + " quesions right in " + getTimeTaken() + "s." + message)
                .setIcon(0)
                .setPositiveButton("Return to main menu.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    void greyToBlackAnimation(TextView view) {
        ObjectAnimator colorAnim = ObjectAnimator.ofInt(view, "textColor", getColor(R.color.lightGrey), Color.BLACK);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(300);
        colorAnim.start();
    }

    void countdownAnimation(TextView view) {
        ValueAnimator animator = new ValueAnimator();

        animator.setFloatValues(0.3f, 1f);

        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                view.setScaleX(value);
                view.setScaleY(value);
            }
        });

        animator.start();
    }

    void growAnimation(TextView view) {
        ValueAnimator animator = new ValueAnimator();

        animator.setFloatValues(nextQuestionView.getScaleX(), 1f);

        animator.setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                view.setScaleX(value);
                view.setScaleY(value);
            }
        });

        animator.start();
    }

    public void clearDrawingView(View view) {
        drawingView.clear();
    }

    private void setupInk() {
        DigitalInkRecognitionModelIdentifier modelIdentifier = null;
        try {
            modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US");
        } catch (MlKitException e) {
            e.printStackTrace();
        }

        DigitalInkRecognitionModel model = DigitalInkRecognitionModel.builder(modelIdentifier).build();
        RemoteModelManager remoteModelManager = RemoteModelManager.getInstance();

        remoteModelManager.download(model, new DownloadConditions.Builder().build());

        recognizer = DigitalInkRecognition.getClient(DigitalInkRecognizerOptions.builder(model).build());
    }

    public static GameActivity getInstance() {
        return instance;
    }

    private void playSound(int id) {
        if(!soundMuted) {
            MediaPlayer mediaPlayer = MediaPlayer.create(GameActivity.this, id);
            mediaPlayer.start();
        }
    }

    public void checkAnswer() {
        recognizer.recognize(drawingView.build())
                .addOnSuccessListener(result -> {
                    for(String answer : getCurrentQuestion().getSimplifiedFractionalAnswerStrings()) {
                        if (parseCandidates(result.getCandidates()).contains(answer)) {
                            playSound(R.raw.ding);
                            changeQuestion();
                            drawingView.clear();
                            break;
                        }
                    }
                });
    }

    LinkedList<String> parseCandidates(List<RecognitionCandidate> candidates) {
        LinkedList<String> list = new LinkedList<>();
        for (int i = 0; i < candidates.size(); i++) {
                list.add(candidates.get(i).getText());
        }
        return list;
    }

    void findViews() {
        currentQuestionView = findViewById(R.id.current_question_view);
        nextQuestionView = findViewById(R.id.next_question_view);
        timerView = findViewById(R.id.timer_view);
        drawingView = findViewById(R.id.drawing_view);
        countdownView = findViewById(R.id.countdown_text);
        clearButton = findViewById(R.id.clear_button);
    }
}