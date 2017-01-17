package sweng.aa03.inventorytracking.view;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sweng.aa03.inventorytracking.R;
import sweng.aa03.inventorytracking.controller.DataBaseHelper;
import sweng.aa03.inventorytracking.model.Individual;
import sweng.aa03.inventorytracking.model.Project;

import static sweng.aa03.inventorytracking.R.string.project;

public class NewProjectActivity extends AppCompatActivity {

    protected ArrayList<String> individualsString;

    EditText nameEditText;
    EditText descriptionEditText;
    Button dateButton;

    protected String leader;
    protected ArrayList<String> members;

    ArrayAdapter<String> leaderAdapter;
    MultiSelectionSpinner teamSpinner;
    Spinner leaderDropdown;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference individualRef = rootRef.child("Individual");

    Calendar calendar = Calendar.getInstance();

    boolean dateSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);
        individualsString  = new ArrayList<String>();
        members = new ArrayList<String>();
        nameEditText = (EditText) findViewById(R.id.projectName);
        descriptionEditText = (EditText) findViewById(R.id.projectDescription);
        dateButton = (Button) findViewById(R.id.date);
        dateSet = false;
        teamSpinner=(MultiSelectionSpinner)findViewById(R.id.team_members_spinner);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(
                        NewProjectActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        TextView tv = (TextView) findViewById(R.id.date_result);
                        monthOfYear++;
                        tv.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                        dateSet = true;
                    }
                },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        leaderDropdown = (Spinner) findViewById(R.id.leaderSpinner);
        leaderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, individualsString);
        leaderDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                leader = individualsString.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(NewProjectActivity.this, "Nothing Selected",
                        Toast.LENGTH_SHORT).show();
            }
        });
        leaderDropdown.setAdapter(leaderAdapter);
        ActionBar ab  = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private AdapterView.OnItemSelectedListener OnCatSpinnerCL = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
            ((TextView) parent.getChildAt(0)).setTextSize(5);
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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


    public void createProject(View v) {
        //create project button pressed, check all field are filled and submit to database;
        if (allFieldsAnswered()) {
            Date date = calendar.getTime();
            long time = date.getTime();
            members = teamSpinner.getSelectedStrings(); //get all selected checkboxes
            Project project = new Project(
                    nameEditText.getText().toString(),
                    descriptionEditText.getText().toString(),
                    leader,
                    time,
                    members);
            DataBaseHelper.newProject(rootRef, project);
            finish();
        } else {
            Toast.makeText(NewProjectActivity.this, "Some Fields Left Empty.",
                    Toast.LENGTH_SHORT).show();
        }


    }

    private boolean allFieldsAnswered() {
        return (!nameEditText.getText().toString().isEmpty()) && (!leader.isEmpty()) && dateSet;
    }

    @Override
    protected void onStart() {
        super.onStart();
        individualRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> strings = new ArrayList<String>();
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot individualSnapshot: dataSnapshot.getChildren()) {
                        Individual individual = individualSnapshot.getValue(Individual.class);
                        individual.addKey(individualSnapshot.getKey());
                        individualsString.add(individual.name);
                        strings.add(individual.name);
                    }

                }
                leaderAdapter.clear();
                leaderAdapter.addAll(strings);
                leaderAdapter.notifyDataSetChanged();
                //set multiselection spinner of all team member names
                teamSpinner.setItems(individualsString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
