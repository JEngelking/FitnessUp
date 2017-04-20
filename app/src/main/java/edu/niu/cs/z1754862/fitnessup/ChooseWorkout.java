package edu.niu.cs.z1754862.fitnessup;

import android.content.Intent;
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
    private Spinner exSpin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_workout);
        exSpin = (Spinner) findViewById(R.id.spinnerExercise);

        //adapter for exercises
        ArrayAdapter<CharSequence> adapterEx =
                ArrayAdapter.createFromResource(this, R.array.workouttitles,
                        android.R.layout.simple_spinner_item);
        adapterEx.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exSpin.setAdapter(adapterEx);
        exSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selection = parent.getItemAtPosition(position).toString();

                Intent intent = new Intent (ChooseWorkout.this, WorkoutActivity.class);
                intent.putExtra("selectedWorkout", selection);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
