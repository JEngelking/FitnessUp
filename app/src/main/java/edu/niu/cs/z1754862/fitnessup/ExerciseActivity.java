package edu.niu.cs.z1754862.fitnessup;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ExerciseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private Spinner exSpin;
    Button startBtn;
    Button pauseBtn;
    Button resetBtn;
    TextView timeTV;
    long startTime = 0L;

    private Handler handler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_activity);
        exSpin = (Spinner)findViewById(R.id.spinnerExercise);
        startBtn = (Button) findViewById(R.id.buttonStart);
        pauseBtn = (Button) findViewById(R.id.buttonPause);
        resetBtn = (Button) findViewById(R.id.buttonReset);

        timeTV = (TextView) findViewById(R.id.textViewElapsedTime);

        //adapter for exercises
        ArrayAdapter<CharSequence> adapterEx =
                ArrayAdapter.createFromResource(this,R.array.exercises,
                        android.R.layout.simple_spinner_item);
        adapterEx.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exSpin.setAdapter(adapterEx);
        exSpin.setOnItemSelectedListener(this);

        startBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(updateTimerThread, 0);
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view)
            {
                timeSwapBuff += timeInMilliseconds;
                handler.removeCallbacks(updateTimerThread);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                handler.removeCallbacks(updateTimerThread);
                timeTV.setText(R.string.start_time);

                timeSwapBuff = 0L;
                timeInMilliseconds = 0L;
                updatedTime = 0L;
            }
        });
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timeTV.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            handler.postDelayed(this, 0);
        }

    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
    {
        int spinType = adapterView.getId();

        if (spinType == R.id.spinnerExercise)
        {

        }//end exercise spinner
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }
}
