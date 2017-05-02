package edu.niu.cs.z1754862.fitnessup;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WorkoutActivity extends AppCompatActivity
{
    private LinearLayout gallery;
    private TextView workoutDesc;
    private Button startBtn, finishBtn;
    private ProgressBar workoutPB;

    MyCountDownTimer countDownTimer;
    long timeRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_activity);

        gallery = (LinearLayout)findViewById(R.id.gallery);
        workoutDesc = (TextView)findViewById(R.id.workoutDescription);
        startBtn = (Button) findViewById(R.id.workoutStartButton);
        finishBtn = (Button) findViewById(R.id.finishButton);
        workoutPB = (ProgressBar) findViewById(R.id.workoutProgress);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");
        workoutDesc.setTypeface(typeface);

        Intent intent = getIntent();

        int selectedWorkout = intent.getIntExtra("selectedWorkout", 0);

        fillGallery(selectedWorkout);
    }

    public void beginWorkoutClick(View v)
    {
        if (startBtn.getText().equals("Start"))
        {
            countDownTimer = new MyCountDownTimer(1800000, 1000);
            startBtn.setText("Pause");
            countDownTimer.start();
        }

        else if (startBtn.getText().equals("Pause"))
        {
            startBtn.setText("Resume");
            countDownTimer.cancel();
        }

        else if (startBtn.getText().equals("Resume"))
        {
            startBtn.setText("Pause");
            countDownTimer.onResume();
        }
    }

    public class MyCountDownTimer extends CountDownTimer
    {

        public MyCountDownTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            timeRemaining = millisUntilFinished;

            int progress = (int) (millisUntilFinished / 1000);

            workoutPB.setMax(2500000);
            workoutPB.setProgress(workoutPB.getMax() - progress);
        }

        public void onResume()
        {
            countDownTimer = new MyCountDownTimer(timeRemaining, 1000);
            countDownTimer.start();
        }

        @Override
        public void onFinish()
        {

        }
    }

    private void fillGallery(int selectedWorkout)
    {
        ImageView imageView;
        int wew = selectedWorkout;

        if (wew == 3)
        {
            for (int count = 0; count < WorkoutInfo.coreDrill.length; count++)
            {
                imageView = new ImageView(this);

                imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), WorkoutInfo.coreDrill[count], null));

                gallery.addView(imageView);
            }

            workoutDesc.setText(WorkoutInfo.description[wew]);
        }

        else
        {
            imageView = new ImageView(this);

            imageView.setImageResource(R.drawable.stock);

            gallery.addView(imageView);

            workoutDesc.setText(WorkoutInfo.description[wew]);
        }
    }
}
