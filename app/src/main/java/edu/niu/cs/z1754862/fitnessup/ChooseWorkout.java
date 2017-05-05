package edu.niu.cs.z1754862.fitnessup;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ChooseWorkout extends AppCompatActivity
{
    private Spinner wkoutSpin;
    private Button chooseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_workout);
        wkoutSpin = (Spinner) findViewById(R.id.spinnerWorkout);
        chooseBtn = (Button) findViewById(R.id.chooseButton);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/GeosansLight.ttf");
        chooseBtn.setTypeface(typeface);

        //adapter for exercises, populate with appropriate strings and specify style
        ArrayAdapter<CharSequence> adapterEx =
                ArrayAdapter.createFromResource(this, R.array.workouttitles,
                        android.R.layout.simple_spinner_item);
        adapterEx.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wkoutSpin.setAdapter(adapterEx);

        //Upon clicking the submit button, get the selected item's index, and pass this value to the WorkoutActivity which will load the specific workout information
        chooseBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int selection = wkoutSpin.getSelectedItemPosition();
                Intent intent = new Intent (ChooseWorkout.this, WorkoutActivity.class);
                intent.putExtra("selectedWorkout", selection);
                startActivity(intent);
            }
        });
    }
}
