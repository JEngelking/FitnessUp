package edu.niu.cs.z1754862.fitnessup;

/**
 * Created by Josh on 4/12/2017.
 */

public class Workout
{
    private String workoutDescription;
    private int workoutID;

    public Workout (String newDescription, int newID)
    {
        workoutDescription = newDescription;
        workoutID = newID;
    }

    public int getWorkoutID ()
    {
        return workoutID;
    }

    public void setWorkoutID(int newID)
    {
        workoutID = newID;
    }

    public String getWorkoutDescription() {
        return workoutDescription;
    }

    public void setWorkoutDescription(String newDescription){
        workoutDescription = newDescription;
    }
}
