package edu.niu.cs.z1754862.fitnessup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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

        //Set on-screen elements
        exSpin = (Spinner)findViewById(R.id.spinnerExercise);
        startBtn = (Button) findViewById(R.id.buttonStart);
        pauseBtn = (Button) findViewById(R.id.buttonPause);
        resetBtn = (Button) findViewById(R.id.buttonReset);
        saveBtn = (Button) findViewById(R.id.buttonSave);

        timeTV = (TextView) findViewById(R.id.textViewElapsedTime);

        //Complete typeface consistency
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");
        startBtn.setTypeface(typeface);
        pauseBtn.setTypeface(typeface);
        resetBtn.setTypeface(typeface);
        saveBtn.setTypeface(typeface);

        //adapter for exercises, populate with appropriate strings and set style
        ArrayAdapter<CharSequence> adapterEx =
                ArrayAdapter.createFromResource(this,R.array.exercises,
                        android.R.layout.simple_spinner_item);
        adapterEx.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exSpin.setAdapter(adapterEx);
        exSpin.setOnItemSelectedListener(this);

        //Begin timer upon clicking start button
        startBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(updateTimerThread, 0);
            }
        });

        //Pause timer
        pauseBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view)
            {
                timeSwapBuff += timeInMilliseconds;
                handler.removeCallbacks(updateTimerThread);
            }
        });

        //Reset timer to start time
        resetBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                handler.removeCallbacks(updateTimerThread);
                timeTV.setText(R.string.start_time);

                //Reset variables used for time calculation
                timeSwapBuff = 0L;
                timeInMilliseconds = 0L;
                updatedTime = 0L;
            }
        });

        //Send data to server upon clicking saveBtn
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PerformAsyncTask().execute();
            }
        });
    }

    //Create runnable to run the timer and create a smooth UI
    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            //Calculate time amounts
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            //Update textView
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
            //Get selected exercise to send to the server
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
            //Send data to server and get response
            String response = sendPin();
            return response;
        }

        @Override
        protected void onPostExecute(String response)
        {
            //Display response from server in Toast
            Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, 0);
            toast.show();

            //Return to home screen after completing exercise
            Intent returnToMain = new Intent(ExerciseActivity.this, MainActivity.class);
            startActivity(returnToMain);
        }
    }

    public String sendPin()
    {
        //Form connection
        HttpURLConnection connection;
        String response = new String();
        String currentDT = DateFormat.getDateTimeInstance().format(new Date());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Ensure user's pin and app version are saved in memory
        if (preferences.contains("pin") && preferences.contains("ver"))
        {
            try {
                //Get appropriate URL
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

                //Form response string from Scanner
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
