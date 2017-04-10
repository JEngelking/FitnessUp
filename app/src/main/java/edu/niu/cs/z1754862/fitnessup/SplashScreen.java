package edu.niu.cs.z1754862.fitnessup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v7.app.ActionBar;

public class SplashScreen extends AppCompatActivity {

    TextView titleTV;
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ActionBar bar = getSupportActionBar();
        bar.hide();

        titleTV = (TextView) findViewById(R.id.titleView);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");
        titleTV.setTypeface(typeface);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        }, SPLASH_DISPLAY_LENGTH);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.contains("pin"))
        {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
        }

        else
        {
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private static String getDefaultSharedPreferencesName(Context context)
    {
        return context.getPackageName() + "_preferences";
    }
}
