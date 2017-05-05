package edu.niu.cs.z1754862.fitnessup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.support.v7.app.ActionBar;
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

public class SplashScreen extends AppCompatActivity {

    TextView titleTV;
    private final int SPLASH_DISPLAY_LENGTH = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Hide action bar
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }

        //Set typeface
        titleTV = (TextView) findViewById(R.id.titleView);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");
        titleTV.setTypeface(typeface);

        //Delay moving to home screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        }, SPLASH_DISPLAY_LENGTH);

        //Get shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //And check if pin is already there, meaning user has previously logged in and will not need to again
        if (preferences.contains("pin"))
        {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
        }

        //Otherwise, have them login
        else
        {
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Check if pin already exists, and send data usage
        if (preferences.contains("pin"))
        {
            new PerformAsyncTask().execute();
        }
    }

    private class PerformAsyncTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params)
        {
            //Get response from server
            String response = sendUsage();
            return response;
        }

        @Override
        protected void onPostExecute(String response)
        {
            //Display server confirmation to user in Toast
            Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, 0);
            toast.show();
        }
    }

    public String sendUsage()
    {

        //Form connection
        HttpURLConnection connection;
        String response = new String();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pin;
        String dt = DateFormat.getDateTimeInstance().format(new Date());

        if (preferences.contains("pin"))
        {
            //Get pin and set default
            pin = preferences.getString("pin", "def123");
        }

        else
        {
            pin = "def123";
        }

        try
        {
            //Connect to server
            URL url = new URL("http://students.cs.niu.edu/~exerciseapp/postusagedata.php");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);//set http method to post
            connection.setRequestMethod("POST");//set to post
            //Encode parameters to be sent
            String outParms = "pin=" + URLEncoder.encode(pin, "UTF-8") + "&dt=" + URLEncoder.encode(dt, "UTF-8");
            //Set final connection attributes and create PrintWriter to get response from server
            connection.setFixedLengthStreamingMode(outParms.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            PrintWriter writer = new PrintWriter(connection.getOutputStream());
            writer.print(outParms);
            writer.close();

            Scanner inStream = new Scanner(connection.getInputStream());

            //Form response from Scanner
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

        return response;
    }
}
