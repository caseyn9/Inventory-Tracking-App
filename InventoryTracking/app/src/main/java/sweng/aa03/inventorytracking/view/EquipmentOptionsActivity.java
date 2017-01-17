package sweng.aa03.inventorytracking.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sweng.aa03.inventorytracking.R;
import sweng.aa03.inventorytracking.controller.EquipmentOptionsAdapter;
import sweng.aa03.inventorytracking.model.Equipment;

public class EquipmentOptionsActivity extends AppCompatActivity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference equipmentRef = rootRef.child("Equipment");
    EquipmentOptionsAdapter adapter;
    String barcode;
    TextView nameView;
    TextView barcodeView;
    TextView amountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_options);

        nameView = (TextView) findViewById(R.id.equipmentName);
        barcodeView = (TextView) findViewById(R.id.equipmentBarcode);
        amountView = (TextView) findViewById(R.id.equipmentAmount);

        barcode = getIntent().getStringExtra("BARCODE");
        barcodeView.setText(barcode);

        ListView listView = (ListView) findViewById(R.id.availableListView);

        ArrayList<Equipment> arrayOfEquipment = new ArrayList<>();
        adapter = new EquipmentOptionsAdapter(this, arrayOfEquipment);
        listView.setAdapter(adapter);
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
        equipmentRef.orderByChild("barcode").equalTo(barcode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    ArrayList<Equipment> equipmentS = new ArrayList<Equipment>();
                    for (DataSnapshot equipmentSnapshot: dataSnapshot.getChildren()) {
                        Equipment equipment = equipmentSnapshot.getValue(Equipment.class);
                        equipment.addKey(equipmentSnapshot.getKey());
                        equipmentS.add(equipment);
                    }
                    if (equipmentS.size()>=1) {
                        nameView.setText(equipmentS.get(0).name);
                    }
                    amountView.setText("No of items in database: " + equipmentS.size());
                    adapter.clear();
                    adapter.addAll(equipmentS);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
