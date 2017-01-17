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
import sweng.aa03.inventorytracking.controller.ProjectsAdapter;
import sweng.aa03.inventorytracking.model.Project;

public class ProjectsActivity extends AppCompatActivity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference projectRef = rootRef.child("Project");
    ProjectsAdapter adapter;
    ArrayList<Project> projects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setNumColumns(3);
        ArrayList<Project> arrayOfProjects = new ArrayList<>();
        adapter = new ProjectsAdapter(this, arrayOfProjects);
        gridView.setAdapter(adapter);
        ActionBar ab  = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Project clicked = projects.get(position);
                Intent intent = new Intent(ProjectsActivity.this, ViewIndividualProjectActivity.class);
                intent.putExtra("PROJECT_KEY", clicked.getKey());
                startActivity(intent);
            }
        });
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

        projectRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    projects = new ArrayList<Project>();
                    for (DataSnapshot projectSnapshot: dataSnapshot.getChildren()) {
                        Project project = projectSnapshot.getValue(Project.class);
                        project.addKey(projectSnapshot.getKey());
                        projects.add(project);
                    }
                    adapter.clear();
                    adapter.addAll(projects);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void newProject(View view){
        Intent intent = new Intent(this,NewProjectActivity.class);
        startActivity(intent);
    }

}
