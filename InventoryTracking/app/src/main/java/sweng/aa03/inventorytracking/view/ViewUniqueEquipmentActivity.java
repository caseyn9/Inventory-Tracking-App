package sweng.aa03.inventorytracking.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sweng.aa03.inventorytracking.R;
import sweng.aa03.inventorytracking.model.Equipment;
import sweng.aa03.inventorytracking.model.Individual;

public class ViewUniqueEquipmentActivity extends AppCompatActivity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference equipmentRef;

    TextView equipmentName;
    TextView attached;
    TextView returnBy;
    TextView damaged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_unique_equipment);

        //GET CLICKED EQUIPMENT
        String equipmentKey;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                equipmentKey= null;
            } else {
                equipmentKey= extras.getString("INDIVIDUAL_KEY");
            }
        } else {
            equipmentKey= (String) savedInstanceState.getSerializable("INDIVIDUAL_KEY");
        }

        equipmentRef = rootRef.child("Individual/"+equipmentKey);

        equipmentName = (TextView) findViewById(R.id.individualName);
//        attached= (TextView) findViewById(R.id.attached);
//        returnBy = (TextView) findViewById(R.id.returnBy);
//        damaged = (TextView) findViewById(R.id.damaged);

    }

    protected void onStart() {
        super.onStart();

        equipmentRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    Equipment equipment = dataSnapshot.getValue(Equipment.class);

                    //TODO: SEARCH FUNCTIONS.
//                    String returnDate = findReturnDate(equipemt);
//                    String user = findUserOf(equipment);

                    //Views
                    equipmentName.setText("Name: " + equipment.name);
//                    attached.setText("Being used by: " + user);
//                    returnBy.setText("To be returned by: " + returnDate);
                    String damagedString = equipment.damaged ? "yes" : "no";
                    damaged.setText("Damaged: " + damagedString);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
