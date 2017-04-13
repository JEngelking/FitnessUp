package edu.niu.cs.z1754862.fitnessup;

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

        fillGallery();
    }

    private void fillGallery()
    {
        ImageView imageView;

        for (int count = 0; count < WorkoutInfo.description.length; count++)
        {
            imageView = new ImageButton(this);

            Workout workout = new Workout(WorkoutInfo.description[count], WorkoutInfo.id[count]);

            imageView.setContentDescription(workout.getWorkoutDescription());

            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), workout.getWorkoutID(), null));

            workoutDesc.setText(imageView.getContentDescription());

            gallery.addView(imageView);
        }
    }
}
