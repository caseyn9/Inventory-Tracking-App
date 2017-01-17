package sweng.aa03.inventorytracking.view;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Date;
import sweng.aa03.inventorytracking.R;
import sweng.aa03.inventorytracking.model.Individual;
import sweng.aa03.inventorytracking.model.Project;

public class ViewIndividualProjectActivity extends AppCompatActivity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference projectRef;
    DatabaseReference individualRef = rootRef.child("Individual");
    Project project;
    ArrayList<String> memberNames;
    ArrayList<String> selectedMemberNames;
    protected ArrayList<String> currentMembers;
    private TextView projectName;
    private TextView description;
    private TextView leaderName;
    private TextView endDate;
    private TextView members;
    private TextView addMembers;
    MultiSelectionSpinner teamSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_individual_project);
        //Views
        projectName = (TextView) findViewById(R.id.projectName);
        description = (TextView) findViewById(R.id.description);
        leaderName = (TextView) findViewById(R.id.leaderName);
        endDate = (TextView) findViewById(R.id.endDate);
        members = (TextView) findViewById(R.id.members);
        addMembers = (TextView) findViewById(R.id.members_tv);
        addMembers.setText("Add members to project:");
        teamSpinner=(MultiSelectionSpinner)findViewById(R.id.members_spinner);
        memberNames = new ArrayList<>();
        currentMembers = new ArrayList<>();
        selectedMemberNames = new ArrayList<>();

        String projectKey;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                projectKey= null;
            } else {
                projectKey= extras.getString("PROJECT_KEY");
            }
        } else {
            projectKey= (String) savedInstanceState.getSerializable("PROJECT_KEY");
        }

        projectRef = rootRef.child("Project/"+projectKey);
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

    public void addMembers(View view){
        selectedMemberNames = teamSpinner.getSelectedStrings();
        for(int i = 0; i<currentMembers.size(); i++){
            if(selectedMemberNames.contains(currentMembers.get(i))){
                selectedMemberNames.remove(currentMembers.get(i));
            }
            selectedMemberNames.add(currentMembers.get(i));
        }
        project.memberNames = selectedMemberNames;
        projectRef.setValue(project);

    }

    @Override
    protected void onStart() {
        super.onStart();
        individualRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                memberNames.clear();
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot individualSnapshot: dataSnapshot.getChildren()) {
                        Individual individual = individualSnapshot.getValue(Individual.class);
                        memberNames.add(individual.name);
                    }

                }
                teamSpinner.setItems(memberNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        projectRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    project = dataSnapshot.getValue(Project.class);
                    currentMembers = project.memberNames;
                    String memberString = "";
                    for(int i = 0; i<project.memberNames.size(); i++){
                        memberString+=project.memberNames.get(i) + "\n";
                    }


                    Date date = new Date();
                    date.setTime(project.endTime);

                    //Views
                    projectName.setText("Project Name: " + project.name);
                    description.setText("Description: " + project.description);
                    leaderName.setText("Leader Name: " + project.leaderName);
                    endDate.setText("End Date: " + date);
                    members.setText("Members: " + memberString);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
