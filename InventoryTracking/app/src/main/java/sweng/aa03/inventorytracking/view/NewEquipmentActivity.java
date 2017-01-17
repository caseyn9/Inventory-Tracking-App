package sweng.aa03.inventorytracking.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sweng.aa03.inventorytracking.R;
import sweng.aa03.inventorytracking.controller.DataBaseHelper;
import sweng.aa03.inventorytracking.model.Equipment;

public class NewEquipmentActivity extends Activity {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    EditText barcodeInput, nameInput, amountInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_new_equipment);
        barcodeInput = (EditText) findViewById(R.id.barcodeInput);
        nameInput = (EditText) findViewById(R.id.nameInput);
        amountInput = (EditText) findViewById(R.id.amountInput);
        String barcode = getIntent().getStringExtra("BARCODE");
        if(barcode!=null){
            barcodeInput.setText(barcode);
            barcodeInput.setEnabled(false);
        }
        amountInput.setText("1");
    }
    //When all values entered and add button pressed
    //Validate all fields then add to DB
    //Returns to equipment page
    public void addOnClick(View view){
        String barcode = barcodeInput.getText().toString();
        String name = nameInput.getText().toString();
        int amount = Integer.parseInt(amountInput.getText().toString());
        if(!barcode.isEmpty()&&!name.isEmpty()&&amount>0){
            for (int i = 0; i<amount ; i++) {
                DataBaseHelper.newEquipment(rootRef, new Equipment(name, barcode, null, null));
            }
            Toast.makeText(this, "Equipment successfully added", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else{
            Toast.makeText(this,"Input Error, try again", Toast.LENGTH_SHORT).show();
        }
    }
}
