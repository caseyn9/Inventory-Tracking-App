package sweng.aa03.inventorytracking.controller;

import com.google.firebase.database.DatabaseReference;

import sweng.aa03.inventorytracking.model.Equipment;
import sweng.aa03.inventorytracking.model.Individual;
import sweng.aa03.inventorytracking.model.Project;

/**
 * Created by eganl on 29/11/2016.
 */

public class DataBaseHelper {

    public static void newProject(DatabaseReference database, Project project) {
        String key = database.child("Project").push().getKey();
        database.child("Project").child(key).setValue(project);
    }

    public static void newIndividual(DatabaseReference database, Individual person) {
        String key = database.child("Individual").push().getKey();
        database.child("Individual").child(key).setValue(person);
    }

    public static void newEquipment(DatabaseReference database, Equipment thing) {
        String key = database.child("Equipment").push().getKey();
        database.child("Equipment").child(key).setValue(thing);
    }

}
