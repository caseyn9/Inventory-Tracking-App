package sweng.aa03.inventorytracking.view;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sweng.aa03.inventorytracking.R;
import sweng.aa03.inventorytracking.controller.EquipmentAdapter;
import sweng.aa03.inventorytracking.model.Equipment;

public class EquipmentActivity extends AppCompatActivity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference equipmentRef = rootRef.child("Equipment");
    EquipmentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setNumColumns(3);
        ArrayList<Equipment> arrayOfEquipment = new ArrayList<>();
        adapter = new EquipmentAdapter(this, arrayOfEquipment);
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

        equipmentRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    ArrayList<Equipment> equipmentS = new ArrayList<Equipment>();
                    for (DataSnapshot equipmentSnapshot: dataSnapshot.getChildren()) {
                        Equipment equipment = equipmentSnapshot.getValue(Equipment.class);
                        equipment.addKey(equipmentSnapshot.getKey());
                        equipmentS.add(equipment);
                    }
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

    //Called when FAB in activity_equipment is clicked
    //Starts the ScanBarcodeActivity
    public  void scanNew(View view){
        Intent intent = new Intent(this, ScanBarcodeActivity.class);
        startActivityForResult(intent, 0);
    }

    //Called when ScanBarcodeActivity returns
    //Then calls NewEquipmentActivity with the barcode value as a String
    //Or sets status code to cancelled and calls addEquipmentActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) { //Check Not returning to class due to back button.
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcodeScanned = data.getParcelableExtra("BARCODE"); //extract Barcode
                    Intent intent = new Intent(getBaseContext(), NewEquipmentActivity.class);
                    if(barcodeScanned!=null) {
                        intent.putExtra("BARCODE", barcodeScanned.displayValue); //Gets latest barcode value to return
                    }
                        startActivity(intent);

                } else {
                    Toast.makeText(this,"No Barcode found.",Toast.LENGTH_LONG).show();
                }
            }
            //No barcode button pressed. Therefore must enter it manually.
            else if(resultCode==CommonStatusCodes.CANCELED){
                Toast.makeText(this, "Must enter barcode manually.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), NewEquipmentActivity.class);
                startActivity(intent);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
