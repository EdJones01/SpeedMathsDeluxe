package speed.mathsdeluxe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.innovattic.rangeseekbar.RangeSeekBar;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    RangeSeekBar rangeBar;

    TextView questionsTextView;
    TextView numberRangeView;
    TextView highscoreView;

    ImageButton muteButton;

    SeekBar questionsBar;

    boolean changesMade = false;

    boolean mute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getApplication().getSharedPreferences("speed.mathsdeluxe", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        findViews();

        setupSeekbar();

        setupRangebar();

        if (!(sharedPreferences.getInt("numberOfQuestions", 0) == 0)) {
            load();
        } else {
            mute = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (changesMade)
            new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setTitle("Do you want to discard changes?")
                    .setPositiveButton("Discard", (dialog, which) -> {
                        super.onBackPressed();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        else
            super.onBackPressed();
    }

    void findViews() {
        questionsTextView = findViewById(R.id.number_of_questions_text);
        questionsBar = findViewById(R.id.questions_bar);
        rangeBar = findViewById(R.id.range_bar);
        numberRangeView = findViewById(R.id.number_range_text);
        highscoreView = findViewById(R.id.highscore_text);
        muteButton = findViewById(R.id.mute_button);
    }

    void load() {
        questionsBar.setProgress(sharedPreferences.getInt("numberOfQuestions", 0));
        rangeBar.setMinThumbValue(sharedPreferences.getInt("minimumNumber", 1));
        rangeBar.setMaxThumbValue(sharedPreferences.getInt("maximumNumber", 10));
        numberRangeView.setText("Numbers between " + rangeBar.getMinThumbValue() + " and " + rangeBar.getMaxThumbValue());
        mute = sharedPreferences.getBoolean("mute", false);
        refreshMuteButton();
        updateHighscoreText();
    }

    void updateHighscoreText() {
        String savecode = new Game(rangeBar.getMinThumbValue(), rangeBar.getMaxThumbValue(), questionsBar.getProgress()).getSavecode();
        Log.i("myLog1", savecode);
        float bestTime = Float.parseFloat(sharedPreferences.getString(savecode, "0.0"));
        if (bestTime != 0.0f) {
            highscoreView.setText("Best time for this configuration is " + bestTime + "s");
        } else {
            highscoreView.setText("No previous attempts for this configuration");
        }
    }

    void setupRangebar() {
        rangeBar.setSeekBarChangeListener(new RangeSeekBar.SeekBarChangeListener() {
            @Override
            public void onStartedSeeking() {
                changesMade = true;
            }

            @Override
            public void onStoppedSeeking() {
            }

            @Override
            public void onValueChanged(int i, int i1) {
                numberRangeView.setText("Numbers between " + i + " and " + i1);
                updateHighscoreText();

            }
        });
    }

    void setupSeekbar() {
        questionsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                changesMade = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                questionsTextView.setText("Number of questions: " + progress);
                updateHighscoreText();

            }
        });
    }

    void refreshMuteButton() {
        if(mute) {
            muteButton.setBackgroundColor(getColor(R.color.lightGrey));
            muteButton.setImageResource(R.drawable.mute);
        } else {
            muteButton.setBackgroundColor(getColor(R.color.black));
            muteButton.setImageResource(R.drawable.unmute);
        }
    }

    public void muteButtonAction(View view) {
        mute = !mute;
        refreshMuteButton();
        changesMade = true;
    }

    public void saveButtonAction(View view) {
        editor.putInt("numberOfQuestions", questionsBar.getProgress());
        editor.putInt("minimumNumber", rangeBar.getMinThumbValue());
        editor.putInt("maximumNumber", rangeBar.getMaxThumbValue());
        editor.putBoolean("mute", mute);
        editor.commit();
        finish();
    }
}