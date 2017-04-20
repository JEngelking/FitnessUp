package edu.niu.cs.z1754862.fitnessup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WorkoutActivity extends AppCompatActivity
{
    private LinearLayout gallery;
    private TextView workoutDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_activity);

        gallery = (LinearLayout)findViewById(R.id.gallery);
        workoutDesc = (TextView)findViewById(R.id.workoutDescription);

        Intent intent = getIntent();

        int selectedWorkout = intent.getIntExtra("selectedWorkout", 0);

        fillGallery(selectedWorkout);
    }

    private void fillGallery(int selectedWorkout)
    {
        ImageView imageView;
        int wew = selectedWorkout;

        for (int count = 0; count < WorkoutInfo.coreDrill.length; count++)
        {
            imageView = new ImageView(this);

           // Workout workout = new Workout(selectedWorkout, WorkoutInfo.coreDrill[count]);

            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), WorkoutInfo.coreDrill[count], null));

            gallery.addView(imageView);
        }

        workoutDesc.setText(WorkoutInfo.description[wew]);

    }
}
