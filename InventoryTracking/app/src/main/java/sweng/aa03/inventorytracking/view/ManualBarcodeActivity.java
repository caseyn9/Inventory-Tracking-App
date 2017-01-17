package sweng.aa03.inventorytracking.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

import sweng.aa03.inventorytracking.R;

public class ManualBarcodeActivity extends AppCompatActivity {

    EditText barcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_barcode);
        barcode =  (EditText) findViewById(R.id.barcode);
    }

    //Button to confirm barcode number
    public void onClickConfirm(View v){
        String result = barcode.getText().toString();
        if(result != null){
            Intent intent = new Intent();
            intent.putExtra("BARCODE",result); //Gets latest barcode value to return
            setResult(Activity.RESULT_OK,intent);
           // Toast.makeText(this, "finishing", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
