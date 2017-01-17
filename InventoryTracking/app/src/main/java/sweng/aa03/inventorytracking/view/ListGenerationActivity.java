package sweng.aa03.inventorytracking.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sweng.aa03.inventorytracking.R;
import sweng.aa03.inventorytracking.model.Equipment;
import sweng.aa03.inventorytracking.model.Individual;
import sweng.aa03.inventorytracking.model.Project;

public class ListGenerationActivity extends AppCompatActivity {
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    Calendar calendar = Calendar.getInstance();
    boolean dateSet;
    ArrayAdapter<String> projectAdapter;
    ArrayAdapter<String> individualAdapter;
    int spinnerPos;
    String projectOrIndividualSelection;

    ArrayList<Project> projectArrayList;
    ArrayList<Equipment> damagedEquipmentArrayList;
    ArrayList<Equipment> allEquipment;
    ArrayList<Equipment> equipmentToBeReclaimed;

    private ListView listView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_generation);
        projectAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item);
        individualAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item);
        spinnerPos = 0;
        projectOrIndividualSelection = "";

        allEquipment = new ArrayList<>();
        projectArrayList = new ArrayList<>();
        damagedEquipmentArrayList = new ArrayList<>();
        equipmentToBeReclaimed = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list_view);

        Button dateButton = (Button) findViewById(R.id.date);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(
                        ListGenerationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                TextView tv = (TextView) findViewById(R.id.date_result);
                                monthOfYear ++;
                                String date = dayOfMonth + "/" + monthOfYear + "/" + year;
                                tv.setText(date);
                                dateSet = true;
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }



        });

        Button generateButton = (Button) findViewById(R.id.generate_list);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat formatter = new SimpleDateFormat();
                List<String> listStrings = new ArrayList<String>();
                ArrayAdapter<String> genAdapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,listStrings);
                String string = "";
                switch(spinnerPos){
                    case 0:       //equipment attached to an individual to be returned by date
                        for (int i = 0; i< allEquipment.size(); i++){
                            if(allEquipment.get(i).individualID!=null) {
                                if (allEquipment.get(i).individualID.equals(projectOrIndividualSelection)) {
                                    string += allEquipment.get(i).name + " : " +
                                            allEquipment.get(i).barcode + "\n";
                                    long date = allEquipment.get(i).returnBy;
                                    if(date!=0){
                                        string += "Return By: " + formatter.format(date);
                                        if(date <= calendar.getTimeInMillis()){
                                            string += " - OVERDUE BY SET DATE";
                                        }
                                    }
                                    else string += "No return date set";
                                }
                            }
                            if(string.length()>0){
                                listStrings.add(string);
                                string="";
                            }

                        }
                        break;
                    case 1:      //equipment attached to particular project to be returned by date
                        for (int i = 0; i< allEquipment.size(); i++){
                            if(allEquipment.get(i).projectID!=null) {
                                if (allEquipment.get(i).projectID.equals(projectOrIndividualSelection)) {
                                    string += allEquipment.get(i).name + " : " +
                                            allEquipment.get(i).barcode + "\n";
                                    long date = allEquipment.get(i).returnBy;
                                    if(date!=0){
                                        string += "Return By: " + formatter.format(date);
                                        if(allEquipment.get(i).returnBy <= calendar.getTimeInMillis()){
                                            string += " - OVERDUE BY SET DATE";
                                        }
                                    }
                                    else string += "No return date set";

                                }
                            }
                            if(string.length()>0){
                                listStrings.add(string);
                                string="";
                            }
                        }
                        break;
                    case 2:     //equipment to be reclaimed by certain date
                        equipmentToBeReclaimed.clear();
                        for (int i = 0; i< allEquipment.size(); i++) {
                            Equipment equip = allEquipment.get(i);
                            if(!(equip.individualID==null && equip.projectID==null)
                                    && equip.returnBy <= calendar.getTimeInMillis()) {
                                equipmentToBeReclaimed.add(equip);
                            }

                        }
                        for(int i = 0; i<equipmentToBeReclaimed.size();i++){
                            string += equipmentToBeReclaimed.get(i).name + " : " +
                                    equipmentToBeReclaimed.get(i).barcode + "\n";
                            if(string.length()>0){
                                listStrings.add(string);
                                string="";
                            }

                        }
                        break;
                    case 3: //equipment damaged
                        for(int i = 0; i<damagedEquipmentArrayList.size();i++){
                            string += damagedEquipmentArrayList.get(i).name + " : " +
                                    damagedEquipmentArrayList.get(i).barcode + "\n";
                            if(string.length()>0){
                                listStrings.add(string);
                                string="";
                            }

                        }
                        break;
                }
                listView.setAdapter(genAdapter);
                listView.setVisibility(View.VISIBLE);
            }



        });


        final Spinner spinner = (Spinner) findViewById(R.id.generate_condition_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.generate_list_options,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                final Spinner projectSpinner = (Spinner) findViewById(R.id.individual_or_project_spinner);
                TextView dateText = (TextView) findViewById(R.id.date_layout_text);
                LinearLayout dateLayout = (LinearLayout) findViewById(R.id.date_layout);
                listView.setVisibility(View.GONE);
                switch (position) {
                    case 0: //equipment attached to an individual to be returned by date
                        spinnerPos = 0;
                        dateText.setVisibility(View.VISIBLE);
                        dateLayout.setVisibility(View.VISIBLE);
                        projectSpinner.setAdapter(individualAdapter);
                        projectSpinner.setVisibility(View.VISIBLE);
                        break;
                    case 1: //equipment attached to particular project to be returned by date
                        spinnerPos = 1;
                        dateText.setVisibility(View.VISIBLE);
                        dateLayout.setVisibility(View.VISIBLE);
                        projectSpinner.setAdapter(projectAdapter);
                        projectSpinner.setVisibility(View.VISIBLE);
                        break;
                    case 2: //equipment to be reclaimed by certain date
                        spinnerPos = 2;
                        dateText.setVisibility(View.VISIBLE);
                        dateLayout.setVisibility(View.VISIBLE);
                        projectSpinner.setVisibility(View.GONE);
                        break;
                    case 3: //equipment damaged
                        spinnerPos = 3;
                        dateText.setVisibility(View.GONE);
                        dateLayout.setVisibility(View.GONE);
                        projectSpinner.setVisibility(View.GONE);
                        break;
                }

                projectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int secondPosition, long id) {
                        switch (position) {
                            case 0: //equipment attached to an individual to be returned by date
                                projectOrIndividualSelection = projectSpinner.getSelectedItem().toString();

                                break;
                            case 1: //equipment attached to particular project to be returned by date
                                Project project = projectArrayList.get(secondPosition);
                                projectOrIndividualSelection = project.getKey();
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // sometimes you need nothing here
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });

    }

    protected void onStart() {
        super.onStart();

        DatabaseReference individualRef = rootRef.child("Individual");
        DatabaseReference projectRef = rootRef.child("Project");
        DatabaseReference equipmentRef = rootRef.child("Equipment");


        individualRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    individualAdapter.clear();
                    for (DataSnapshot individualSnapshot: dataSnapshot.getChildren()) {
                        Individual individual = individualSnapshot.getValue(Individual.class);
                        individualAdapter.add(individual.name);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        projectRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    projectArrayList.clear();
                    projectAdapter.clear();
                    for (DataSnapshot projectSnapshot: dataSnapshot.getChildren()) {
                        Project project = projectSnapshot.getValue(Project.class);
                        project.addKey(projectSnapshot.getKey());
                        projectArrayList.add(project);
                        projectAdapter.add(project.name);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        equipmentRef.orderByChild("damaged").equalTo(true).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    damagedEquipmentArrayList.clear();
                    for (DataSnapshot projectSnapshot: dataSnapshot.getChildren()) {
                        Equipment equipment = projectSnapshot.getValue(Equipment.class);
                        equipment.addKey(projectSnapshot.getKey());
                        damagedEquipmentArrayList.add(equipment);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        equipmentRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    allEquipment.clear();
                    for (DataSnapshot projectSnapshot: dataSnapshot.getChildren()) {
                        Equipment equipment = projectSnapshot.getValue(Equipment.class);
                        equipment.addKey(projectSnapshot.getKey());
                        allEquipment.add(equipment);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
