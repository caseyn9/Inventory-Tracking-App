package sweng.aa03.inventorytracking.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sweng.aa03.inventorytracking.R;
import sweng.aa03.inventorytracking.controller.IndividualsAdapter;
import sweng.aa03.inventorytracking.model.Individual;

public class IndividualsActivity extends AppCompatActivity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference individualRef = rootRef.child("Individual");
    IndividualsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individuals);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setNumColumns(3);
        final ArrayList<Individual> arrayOfIndividuals = new ArrayList<>();
        adapter = new IndividualsAdapter(this, arrayOfIndividuals);
        gridView.setAdapter(adapter);
        ActionBar ab  = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //ActionBar functionality
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        individualRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    ArrayList<Individual> individuals= new ArrayList<Individual>();
                    for (DataSnapshot individualSnapshot: dataSnapshot.getChildren()) {
                        Individual individual = individualSnapshot.getValue(Individual.class);
                        individual.addKey(individualSnapshot.getKey());
                        individuals.add(individual);
                    }
                    adapter.clear();
                    adapter.addAll(individuals);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Calls newIndividualActivity on button click
    public void newIndividual(View view) {
        Intent intent = new Intent(this, NewIndividualActivity.class);
        startActivity(intent);
    }
}
