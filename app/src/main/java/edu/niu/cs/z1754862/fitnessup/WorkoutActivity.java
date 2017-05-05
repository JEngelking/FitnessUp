package edu.niu.cs.z1754862.fitnessup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.Scanner;

public class WorkoutActivity extends AppCompatActivity
{
    private LinearLayout gallery;
    private TextView workoutDesc;
    private Button startBtn, finishBtn;
    private ProgressBar workoutPB;

    int selectedWorkout;

    MyCountDownTimer countDownTimer;
    long timeRemaining;
    long timeExercised;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_activity);

        //Connect on-screen elements
        gallery = (LinearLayout)findViewById(R.id.gallery);
        workoutDesc = (TextView)findViewById(R.id.workoutDescription);
        startBtn = (Button) findViewById(R.id.workoutStartButton);
        finishBtn = (Button) findViewById(R.id.finishButton);
        workoutPB = (ProgressBar) findViewById(R.id.workoutProgress);

        //Apply new typeface to on-screen elements
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");
        workoutDesc.setTypeface(typeface);
        startBtn.setTypeface(typeface);
        finishBtn.setTypeface(typeface);

        Intent intent = getIntent();

        //Get selected from previous choose screen
        selectedWorkout = intent.getIntExtra("selectedWorkout", 0);

        //Fill gallery full of images to assist with current workout
        fillGallery(selectedWorkout);

        //Complete background process upon clicking finish button
        finishBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new PerformAsyncTask().execute();
            }
        });
    }

    //Pass current view into function to be used as onClick for the start button
    //Switches text and begins a countdown timer starting at a half an hour
    public void beginWorkoutClick(View v)
    {
        if (startBtn.getText().equals("Start"))
        {
            //Tick every second
            countDownTimer = new MyCountDownTimer(1800000, 1000);
            startBtn.setText("Pause");
            countDownTimer.start();
        }

        else if (startBtn.getText().equals("Pause"))
        {
            //Switch text appropriately
            startBtn.setText("Resume");
            //Pause timer
            countDownTimer.cancel();
        }

        else if (startBtn.getText().equals("Resume"))
        {
            startBtn.setText("Pause");
            //Begin timer again once un-paused
            countDownTimer.onResume();
        }
    }

    //Custom timer class coded to include extra methods onResume and onFinish
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

            //Update total time exercised
            timeExercised = (1800000 - timeRemaining)/6000;

            //Update progressBar
            workoutPB.setMax(1800000);
            workoutPB.setProgress(workoutPB.getMax() - progress);
        }

        public void onResume()
        {
            //If timer is paused and then started, restart and create a new timer with a time reflective of how long the current workout has gone on for
            countDownTimer = new MyCountDownTimer(timeRemaining, 1000);
            countDownTimer.start();
        }

        @Override
        public void onFinish()
        {

        }
    }

    //Pass selectedWorkout variable gotten from being passed from previous activity into function to fill the horizontalScrollView
    private void fillGallery(int selectedWorkout)
    {
        ImageView imageView;
        int wew = selectedWorkout;

        if (wew == 3)
        {
            for (int count = 0; count < WorkoutInfo.coreDrill.length; count++)
            {
                //Dynamically create new imageView
                imageView = new ImageView(this);

                //set drawable resource equal to image pointed to in the coreDrill array in the WorkoutInfo class, using current index in for loop
                imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), WorkoutInfo.coreDrill[count], null));

                //Add this image to HorizontalScrollView
                gallery.addView(imageView);
            }

            //Set appropriate workout description in the textView
            workoutDesc.setText(WorkoutInfo.description[wew]);
        }

        else
        {
            imageView = new ImageView(this);

            //If selectedWorkout does not have images associated with it, load stock picture
            imageView.setImageResource(R.drawable.stock);

            gallery.addView(imageView);

            workoutDesc.setText(WorkoutInfo.description[wew]);
        }
    }

    private class PerformAsyncTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params)
        {
            //Send usage to servers
            String response = sendPin();
            return response;
        }

        @Override
        protected void onPostExecute(String response)
        {
            //Display response from server, retrieved from sendPin
            Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, 0);
            toast.show();

            //Return to the home screen after completion
            Intent returnToMain = new Intent(WorkoutActivity.this, MainActivity.class);
            startActivity(returnToMain);
        }
    }

    public String sendPin()
    {
        //Form connection and get current date to send to server
        HttpURLConnection connection;
        String response = new String();
        String currentDT = DateFormat.getDateTimeInstance().format(new Date());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //As long as preferences contains user's pin and app version
        if (preferences.contains("pin") && preferences.contains("ver"))
        {
            try
            {
                //Set proper URL
                URL url = new URL("http://students.cs.niu.edu/~exerciseapp/postdata.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);//set http method to post
                connection.setRequestMethod("POST");//set to post
                //Encode parameters to be sent
                String outParms = "pin=" + URLEncoder.encode(preferences.getString("pin", "def123"), "UTF-8") +
                        "&min=" + URLEncoder.encode(Long.toString(timeExercised), "UTF-8") +
                        "&act=" + URLEncoder.encode("2", "UTF-8") +
                        "&name=" + URLEncoder.encode(Integer.toString(selectedWorkout)) +
                        "&dt=" + URLEncoder.encode(currentDT, "UTF-8") +
                        "&ver=" + URLEncoder.encode(preferences.getString("ver", "UTF-8"));

                //Set final connection attributes and create PrintWriter to get response from server
                connection.setFixedLengthStreamingMode(outParms.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                PrintWriter writer = new PrintWriter(connection.getOutputStream());
                writer.print(outParms);
                writer.close();

                Scanner inStream = new Scanner(connection.getInputStream());

                //Create response string from Scanner
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
