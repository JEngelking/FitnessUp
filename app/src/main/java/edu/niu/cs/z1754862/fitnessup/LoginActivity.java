package edu.niu.cs.z1754862.fitnessup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity
{
    TextView pinExplain;
    EditText pinET;

    public static final String PIN_MATCH = "[a-zA-Z]{3}\\d{3}";
    public static final String APP_VERSION = "8";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar bar = getSupportActionBar();
        bar.hide();

        SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Toast.makeText(getApplicationContext(), "PIN added to preferences", Toast.LENGTH_SHORT).show();
            }
        };

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(spChanged);

        pinET = (EditText) findViewById(R.id.pinEditText);

        pinExplain = (TextView) findViewById(R.id.pinExplainTextView);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");
        pinExplain.setTypeface(typeface);
        pinET.setTypeface(typeface);

        pinET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (pinET.getText().toString().length() == 6)
                {
                    if (pinET.getText().toString().matches(PIN_MATCH))
                    {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("pin", pinET.getText().toString());
                        editor.putString("ver", APP_VERSION);
                        editor.commit();

                        new PerformAsyncTask().execute();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    else
                    {
                        pinET.setText("");
                        Toast toast = Toast.makeText(getApplicationContext(), "Please enter PIN as three letters followed by three digits", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, 0);
                        toast.show();
                    }
                }
            }
        });
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
        }
    }

    public String sendPin()
    {
        HttpURLConnection connection;
        String response = new String();

        try
        {
            URL url = new URL("http://students.cs.niu.edu/~exerciseapp/postcheckvalid.php");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);//set http method to post
            connection.setRequestMethod("POST");//set to post
            //Encode parameters to be sent
            String outParms = "pin=" + URLEncoder.encode(pinET.getText().toString(), "UTF-8") + "&ver=" + URLEncoder.encode(APP_VERSION, "UTF-8");
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

        return response;
    }
}
