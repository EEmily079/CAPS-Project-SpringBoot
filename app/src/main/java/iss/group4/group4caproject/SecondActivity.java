package iss.group4.group4caproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    private Integer touchTime = 1;
    List<String> stringList = new ArrayList<>();
    ImageButton myImageButton;

    ImageButton myImageButton2;
    private String tempRoute;
    private String tempRoute2;
    private Integer score = 0;
    private static final long COUNTDOWN_DURATION = 3 * 60 * 1000; // 3 minutes in milliseconds
    private TextView countdownTextView;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        setupBtns();
        Intent intent = getIntent();
       String path1 = intent.getStringExtra("route1");
        String path2 = intent.getStringExtra("route2");
        String path3 = intent.getStringExtra("route3");
        String path4 = intent.getStringExtra("route4");
        String path5 = intent.getStringExtra("route5");
        String path6 = intent.getStringExtra("route6");
        List<String> imagePaths = new ArrayList<>();
        imagePaths.add(path1);
        imagePaths.add(path2);
        imagePaths.add(path3);
        imagePaths.add(path4);
        imagePaths.add(path5);
        imagePaths.add(path6);

        for (String imagePath : imagePaths) {
            stringList.add(imagePath);
            stringList.add(imagePath);
        }
        Collections.shuffle(stringList);
        countdownTextView = findViewById(R.id.countdownTextView);
        startCountdown();

    }
    private void startCountdown() {
        countDownTimer = new CountDownTimer(COUNTDOWN_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / (1000 * 60);
                long seconds = (millisUntilFinished / 1000) % 60;
                String timeRemaining = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                countdownTextView.setText(timeRemaining);
            }

            @Override
            public void onFinish() {
                finish();
            }
        };

        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    protected void setupBtns() {
        int[] ids = new int[12];
        for (int i = 0; i < 12; i++) {
            String imageName = "gameImage" + (i + 1);
            ids[i] = getResources().getIdentifier(imageName, "id", getPackageName());
        }

        for (int i = 0; i < ids.length; i++) {
            ImageButton btn = findViewById(ids[i]);
            if (btn != null) {
                btn.setOnClickListener(this);
            }

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (touchTime==1){
            ImageButton imageButton = findViewById(id);
            String tag = (String) imageButton.getTag();
            int position = Integer.parseInt(tag)-1;
            Bitmap bitmap = BitmapFactory.decodeFile(stringList.get(position));
            imageButton.setImageBitmap(bitmap);
            tempRoute = stringList.get(position);
            myImageButton = imageButton;
            imageButton.setEnabled(false);
            touchTime = touchTime+1;
        }
       else if (touchTime==2){
            touchTime = 1;
            ImageButton imageButton = findViewById(id);
            String tag = (String) imageButton.getTag();
            int position = Integer.parseInt(tag)-1;

            Bitmap bitmap = BitmapFactory.decodeFile(stringList.get(position));
            imageButton.setImageBitmap(bitmap);
            tempRoute2 = stringList.get(position);
            myImageButton2 = imageButton;
            imageButton.setEnabled(false);

            if (tempRoute .equals(tempRoute2)) {
                score =score+1;
                TextView textView = findViewById(R.id.scoreTextView);
                textView.setText(score+" of 6 matches");
                if (score == 6)
                {
                    Toast msg = Toast.makeText(this,"Congratulations you have won a game!",Toast.LENGTH_LONG);
                    msg.show();
                    finish();
                }
            }
            else
            {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myImageButton.setImageResource(R.drawable.deafultimage);
                        myImageButton.setEnabled(true);
                        imageButton.setImageResource(R.drawable.deafultimage);
                        imageButton.setEnabled(true);
                    }
                }, 1000);
            }
        }

}}


