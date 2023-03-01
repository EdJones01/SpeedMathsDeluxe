package speed.mathsdeluxe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getApplication().getSharedPreferences("speed.maths", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getInt("numberOfQuestions", 0) == 0) {
            editor.putInt("numberOfQuestions", 10);
            editor.putInt("minimumNumber", 1);
            editor.putInt("maximumNumber", 10);
            editor.commit();
        }
    }

    public void openGame(View view) {
        Intent openGameIntent = new Intent(getApplicationContext(), GameActivity.class);
        Gson gson = new Gson();
        openGameIntent.putExtra("game", gson.toJson(createGameFromSharedPreferences()));
        startActivity(openGameIntent);
    }

    Game createGameFromSharedPreferences() {

        return new Game(
                sharedPreferences.getInt("minimumNumber", 1),
                sharedPreferences.getInt("maximumNumber", 10),
                sharedPreferences.getInt("numberOfQuestions", 10)
        );
    }

    public void openSettings(View view) {
        Intent openSettingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(openSettingsIntent);
    }

    public void openHelp(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.help_layout, null);

        TextView textView = popupView.findViewById(R.id.text);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.help);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            textView.setText(new String(b));
        } catch (Exception e) {
            textView.setText("Error loading help menu.");
        }

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}