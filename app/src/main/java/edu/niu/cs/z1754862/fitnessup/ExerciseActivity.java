package edu.niu.cs.z1754862.fitnessup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.Scanner;

public class ExerciseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private Spinner exSpin;
    Button startBtn;
    Button pauseBtn;
    Button resetBtn;
    Button saveBtn;
    TextView timeTV;
    long startTime = 0L;

    private Handler handler = new Handler();

    int selectedExercise;

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
        saveBtn = (Button) findViewById(R.id.buttonSave);

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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PerformAsyncTask().execute();
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
            selectedExercise = exSpin.getSelectedItemPosition();
        }//end exercise spinner
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }

    private class PerformAsyncTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params)
        {
            String response = sendPin();
            return response;
        }

        @Override
        protected void onPostExecute(String response)
        {
            Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, 0);
            toast.show();

            Intent returnToMain = new Intent(ExerciseActivity.this, MainActivity.class);
            startActivity(returnToMain);
        }
    }

    public String sendPin()
    {
        HttpURLConnection connection;
        String response = new String();
        String currentDT = DateFormat.getDateTimeInstance().format(new Date());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (preferences.contains("pin") && preferences.contains("ver"))
        {
            try {
                URL url = new URL("http://students.cs.niu.edu/~exerciseapp/postdata.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);//set http method to post
                connection.setRequestMethod("POST");//set to post
                //Encode parameters to be sent
                String outParms = "pin=" + URLEncoder.encode(preferences.getString("pin", "def123"), "UTF-8") +
                        "&min=" + URLEncoder.encode(Long.toString(updatedTime), "UTF-8") +
                        "&act=" + URLEncoder.encode("1", "UTF-8") +
                        "&name=" + URLEncoder.encode(Integer.toString(selectedExercise)) +
                        "&dt=" + URLEncoder.encode(currentDT, "UTF-8") +
                        "&ver=" + URLEncoder.encode(preferences.getString("ver", "UTF-8"));

                //Set final connection attributes and create PrintWriter to get response from server
                connection.setFixedLengthStreamingMode(outParms.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                PrintWriter writer = new PrintWriter(connection.getOutputStream());
                writer.print(outParms);
                writer.close();

                Scanner inStream = new Scanner(connection.getInputStream());

                while (inStream.hasNextLine())
                {
                    response += (inStream.nextLine());
                }
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return response;
    }
}
