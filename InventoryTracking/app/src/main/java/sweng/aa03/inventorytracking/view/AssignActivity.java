package sweng.aa03.inventorytracking.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import sweng.aa03.inventorytracking.R;
import sweng.aa03.inventorytracking.model.Equipment;
import sweng.aa03.inventorytracking.model.Individual;
import sweng.aa03.inventorytracking.model.Project;

public class AssignActivity extends AppCompatActivity  {


    String barcode;
    String projectNameSelected,indivSelectedName,indivSelectedKey;
    Project projectSelected;
    TextView barcodeText;
    TextView projectNameText;
    RadioButton projectRButton;
    RadioButton indivRButton;
    Button assignButton;
    Spinner indivSpinner;
    Spinner projectSpinner;
    ArrayAdapter<String> individualsAdapter;
    ArrayAdapter<String> projectsAdapter;
    ArrayList<String> projectsList;
    ArrayList<Project> projects;
    ArrayList<String> indivList;

    Calendar calendar = Calendar.getInstance();
    Equipment equipmentToAssign;
    boolean dateSet=false;
    //To keep track of which object to update in DB
    boolean assignProject;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference projectsRef = rootRef.child("Project");
    DatabaseReference indivRef = rootRef.child("Individual");
    DatabaseReference equipmentRef = rootRef.child("Equipment");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        barcodeText = (TextView) findViewById(R.id.barcodeInput);
        projectNameText = (TextView) findViewById(R.id.projectNameText);
        projectRButton= (RadioButton) findViewById(R.id.projectRButton);
        indivRButton = (RadioButton) findViewById(R.id.indivRButton);
        indivSpinner = (Spinner) findViewById(R.id.indivSpinner);
        projectSpinner = (Spinner) findViewById(R.id.projectSpinner);
        assignButton = (Button) findViewById(R.id.assignButton);

        //Use Key to get Equipment object to be assigned
        equipmentToAssign=null;
        barcode= getIntent().getStringExtra("KEY");
        assignProject=true;  //Project is default
        projects = new ArrayList<Project>();
        setupSpinners();
        setDropdownProject();
        setDropdownProjectIndiv();

        Button dateButton = (Button) findViewById(R.id.date_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(
                        AssignActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                TextView tv = (TextView) findViewById(R.id.date_result);
                                monthOfYear++;
                                tv.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                                dateSet=true;
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    //Individual radio button selected.
    public void individualPressed(View view){
        projectRButton.setChecked(false);
        projectSpinner.setVisibility(View.INVISIBLE);
        projectNameText.setVisibility(View.INVISIBLE);
        assignProject=false;
        setDropdownIndiv();
    }
    //project radio button selected.
    public void projectPressed(View view){
        indivRButton.setChecked(false);
        assignProject=true;
        projectSpinner.setVisibility(View.VISIBLE);
        projectNameText.setVisibility(View.VISIBLE);
        setDropdownProjectIndiv();
    }

    //Setup Spinners
    public void setupSpinners(){
        //Setup Projects spinner and adapter
        projectsList = new ArrayList<String>();
        projectsAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,projectsList);
        projectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                projectNameSelected = projectsList.get(position);
                projectSelected=projects.get(position);
               // Toast.makeText(AssignActivity.this, projectNameSelected, Toast.LENGTH_SHORT).show();
                setDropdownProjectIndiv();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(AssignActivity.this, "Nothing Selected",
                        Toast.LENGTH_SHORT).show();
            }
        });
        projectSpinner.setAdapter(projectsAdapter);

        //Setup individual spinner and adapter
        indivList = new ArrayList<String>();
        individualsAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, indivList);
        indivSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                indivSelectedName = indivList.get(position);
                getIndividualKey();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(AssignActivity.this, "Nothing Selected",
                        Toast.LENGTH_SHORT).show();
            }
        });
        indivSpinner.setAdapter(individualsAdapter);
    }

    //Method to set dropdown if project button checked
    //Populated with all projects
    void setDropdownProject(){
        projectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> allProjects = new ArrayList<String>();
                if(dataSnapshot.getValue()!=null){
                    for(DataSnapshot projectSnapshot: dataSnapshot.getChildren()){
                        Project p = projectSnapshot.getValue(Project.class);
                        p.addKey(projectSnapshot.getKey());
                        allProjects.add(p.name);
                        projectsList.add(p.name);
                        projects.add(p);

                    }
                }
                projectsAdapter.clear();
                projectsAdapter.addAll(allProjects);
                projectsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Method to set dropdown when project is selected
    //Populated with individuals in the selected project
    void setDropdownProjectIndiv() {
        ArrayList<String> projectIndivs = new ArrayList<String>();
        if (projectSelected != null) {
            projectIndivs = (ArrayList<String>) projectSelected.memberNames.clone();
            projectIndivs.add(projectSelected.leaderName);
            individualsAdapter.clear();
            individualsAdapter.addAll(projectIndivs);
            individualsAdapter.notifyDataSetChanged();
            //Set first individual in list as selected by default
            indivSelectedName=projectIndivs.get(0);
            getIndividualKey();
        }

    }
    //Method to set dropdown if individual button checked
    //Populates individuals spinner with all individuals
    void setDropdownIndiv(){
       indivRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               ArrayList<String> allIndiv = new ArrayList<String>();
               if(dataSnapshot.getValue()!=null){
                   for(DataSnapshot indivSnapshot: dataSnapshot.getChildren()){
                       Individual indiv = indivSnapshot.getValue(Individual.class);
                       indiv.addKey(indivSnapshot.getKey());
                       allIndiv.add(indiv.name);
                       indivList.add(indiv.name);
                   }
               }
               individualsAdapter.clear();
               individualsAdapter.addAll(allIndiv);
               individualsAdapter.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

    }

    //Called when assign button is pressed in activity_assign
    void assignClicked(View view){
        if(checkValidEntries()){
            //projectId null if just assigning to individual
            String pKey = (assignProject)?projectSelected.getKey():null;
            equipmentToAssign.projectID= pKey;
            Date date = calendar.getTime();
            long time = date.getTime();
            equipmentToAssign.individualID=indivSelectedName;
            equipmentToAssign.returnBy=time;
            equipmentRef.child(barcode).setValue(equipmentToAssign);

            Toast.makeText(AssignActivity.this, "Equipment successfully assigned", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(AssignActivity.this, "Invalid entries, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getIndividualKey(){

        indivRef.orderByChild("name").equalTo(indivSelectedName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    Individual indivToAssign;
                    for(DataSnapshot indivSnapshot: dataSnapshot.getChildren()){
                        indivToAssign = indivSnapshot.getValue(Individual.class);
                        indivToAssign.addKey(indivSnapshot.getKey());
                        indivSelectedKey=indivSnapshot.getKey();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onStart() {
        super.onStart();
        equipmentRef.child(barcode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                   Equipment equipment;
                    equipment= dataSnapshot.getValue(Equipment.class);
                    equipment.addKey(dataSnapshot.getKey());
                    equipmentToAssign=equipment;
                    barcodeText.setText(equipmentToAssign.name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public boolean checkValidEntries(){
        if(!projectNameSelected.isEmpty()&&!indivSelectedName.isEmpty()&&dateSet){
            return true;
        }
        return false;
    }
}
