package sweng.aa03.inventorytracking.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sweng.aa03.inventorytracking.R;
import sweng.aa03.inventorytracking.controller.DataBaseHelper;
import sweng.aa03.inventorytracking.model.Individual;
/**
 * Created by nicky on 06-Dec-16.
 */

public class NewIndividualActivity extends AppCompatActivity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference individualRef = rootRef.child("Individual");
    ArrayList<Individual> allIndividuals;
    EditText editName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_individual);
        allIndividuals = new ArrayList<>();
        editName = (EditText) findViewById(R.id.individualName);
    }

    protected void onStart() {
        super.onStart();
        individualRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allIndividuals.clear();
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot individualSnapshot: dataSnapshot.getChildren()) {
                        Individual individual = individualSnapshot.getValue(Individual.class);
                        allIndividuals.add(individual);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void createIndividual(View v) {
        if (hasName()) {
            String name = editName.getText().toString();
            if(nameUnique(name)) {
                Individual individual = new Individual(name);
                DataBaseHelper.newIndividual(rootRef, individual);
                finish();
            }
            else{
                Toast.makeText(NewIndividualActivity.this, "Name already taken",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
             Toast.makeText(NewIndividualActivity.this, "No name given",
                 Toast.LENGTH_SHORT).show();
        }
    }

    public boolean hasName() {
        return !editName.getText().toString().isEmpty();

    }

    public boolean nameUnique(String name){
        for(int i = 0; i<allIndividuals.size(); i++){
            if (allIndividuals.get(i).name.equals(name)){
                return false;
            }
        }
        return true;
    }
}