package sweng.aa03.inventorytracking.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sweng.aa03.inventorytracking.R;

public class HomeActivity extends AppCompatActivity {

    private static final int SCAN_BARCODE_REQUEST = 1;
    private static final int ENTER_BARCODE_MANUALLY= 2;
    private static final String TAG = "HomeActivity";

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkPermissionsGranted();
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    //user signed out
                    finish();
                }
            }
        };
        ActionBar ab  = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    //Checks if permission is granted and requests it if not.
    //Can only check permissions at runtime in sdk 23+
    public void checkPermissionsGranted(){
        if(Build.VERSION.SDK_INT>=23) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.CAMERA}, 0);
            }
        }
        else{
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.CAMERA}, 0);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //ActionBar functionality
        switch (item.getItemId()) {
            case android.R.id.home:
                auth.signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickScan(View view) {
        Intent intent = new Intent(this, ScanBarcodeActivity.class);
        startActivityForResult(intent, SCAN_BARCODE_REQUEST);
    }

    public void onClickProjects(View view){
        Intent intent = new Intent(this, ProjectsActivity.class);
        startActivity(intent);
    }

    public void onClickEquipment(View view){
        Intent intent = new Intent(this, EquipmentActivity.class);
        startActivity(intent);
    }
    public void onClickIndividual(View view){
        Intent intent = new Intent(this, IndividualsActivity.class);
        startActivity(intent);
    }

    public void onClickManage(View view){
        Intent intent = new Intent(this,ListGenerationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_BARCODE_REQUEST) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcodeScanned = data.getParcelableExtra("BARCODE"); //extract Barcode
                    if(barcodeScanned!=null) {
                        Intent intent = new Intent(getBaseContext(), EquipmentOptionsActivity.class);
                        intent.putExtra("BARCODE", barcodeScanned.displayValue); //Gets latest barcode value to return
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "No Barcode found.", Toast.LENGTH_LONG).show();
                }

            }
            //No barcode button pressed. Therefore must enter it manually.
            else if(resultCode==CommonStatusCodes.CANCELED){
                Toast.makeText(this, "Must enter barcode manually.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), ManualBarcodeActivity.class);
                startActivityForResult(intent,ENTER_BARCODE_MANUALLY);
            }
        }
        else if(requestCode == ENTER_BARCODE_MANUALLY){
            Toast.makeText(this, "Barcode entered", Toast.LENGTH_LONG).show();
            if(resultCode == RESULT_OK){
                String barcode = data.getStringExtra("BARCODE"); //extract Barcode
                Toast.makeText(this, barcode + " barcode", Toast.LENGTH_LONG).show();
                if(barcode != null){
                    Intent intent = new Intent(getBaseContext(), EquipmentOptionsActivity.class);
                    intent.putExtra("BARCODE", barcode); //Gets latest barcode value to return
                    startActivity(intent);
                }
                else{
                    Toast.makeText(this, "No Barcode found.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            auth.signOut();
        }
        return super.onKeyDown(keyCode, event);
    }
}
