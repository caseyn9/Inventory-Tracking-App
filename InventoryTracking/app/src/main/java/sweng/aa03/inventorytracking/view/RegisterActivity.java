package sweng.aa03.inventorytracking.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sweng.aa03.inventorytracking.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Register";
    private EditText usernameField;
    private EditText passwordField;
    private EditText passwordSameField;

    public ProgressDialog mProgressDialog;

    private FirebaseAuth auth;

    private FirebaseAuth.AuthStateListener authListener;

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

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Views
        usernameField = (EditText) findViewById(R.id.username_id);
        passwordField = (EditText) findViewById(R.id.password_id);
        passwordSameField = (EditText) findViewById(R.id.password_same_id);

        //Buttons
        findViewById(R.id.register_button_id).setOnClickListener(this);

        //Start Auth
        auth = FirebaseAuth.getInstance();

        //Start auth_state_listener
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //move forward to next activity
                    Toast.makeText(RegisterActivity.this, R.string.signed_in,
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(intent );
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.register_button_id:
                register(
                        usernameField.getText().toString(),
                        passwordField.getText().toString()
                );
                break;
        }
    }

    private void register(String username, String password) {
        Log.d(TAG, "createAccount:" + username);
        if (!validateForm()) {
            return;
        }
        showProgressDialog(getString(R.string.registering));
        //create regex to check if valid email, if not then add email ending to username
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        boolean isEmail = matcher.matches();
        if(!isEmail){
            username = username.concat("@inventory.com");
        }
        auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.registration_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(usernameField.getText().toString())) {
            usernameField.setError(getString(R.string.required));
            valid = false;
        } else {
            usernameField.setError(null);
        }

        String password = passwordField.getText().toString();
        if(password.length()<6){
            passwordField.setError("Password must have 6 or more characters");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordField.setError(getString(R.string.required));
            valid = false;
        } else {
            passwordField.setError(null);
        }
        String passwordSame = passwordSameField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError(getString(R.string.required));
            valid = false;
        } else {
            passwordField.setError(null);
        }

        if (!password.equals(passwordSame)) {
            passwordSameField.setError(getString(R.string.password_match));
            valid = false;
        } else {
            passwordSameField.setError(null);
        }
        return valid;
    }
}
