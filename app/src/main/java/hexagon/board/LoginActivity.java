package hexagon.board;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    // Current version of the app
    private static String VERSION = "0.1";

    // Var for intents
    private Intent mainIntent;
    private Intent goRegisterIntent;

    // Var for Firebase
    private FirebaseAuth mAuth;

    // Var for the app UI
    private EditText etUsername;
    private EditText etPassword;
    private Button bLogin;
    private Button bGoRegister;
    private TextView tvWelcome;
    private TextView tvNoAcc;
    private TextView tvCurVer;

    // Var for UI feedback
    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;

    // Var used for backend work
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        // Init all the var UI
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        bGoRegister = (Button) findViewById(R.id.bGoRegister);
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        tvNoAcc = (TextView) findViewById(R.id.tvNoAcc);
        progressDialog = new ProgressDialog(LoginActivity.this);

        // Init Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Set onClickListener for all the button
        bLogin.setOnClickListener(this);
        bGoRegister.setOnClickListener(this);

    }

    /**
     * When the user hit the login button, first get the user's input for
     * the Username and password, then Firebase Authentication will check
     * for the input; if match sign in, if wrong show Toast error to user.
     */

    private void login(){

        // Getting user's input from et and init it
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        // Check for user's input, whether it is validating any rules
        if(!validateForm(username, password)){
            return;
        }

        /*
            Show progress dialog to user to let user knows that the app is processing
         */

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        /*
            If pass valid's checkpoint Firebase will check for username and
            password, will pass true or false statement.
        */

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        /*
                            If pass true, change from login screen to main screen, dismiss
                            progressDialog.
                            If pass false, show user Toast error msg
                         */

                        if(task.isSuccessful()){
                            mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(mainIntent);
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                });

    }

    // Simple method use to switch to register screen
    private void goRegister(){
        goRegisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(goRegisterIntent);
    }

    /**
     * This method is used to validate user's account and password, check to
     * see if the input is validating any rules.
     * General rules:
     *  1. Not to be duplicate. (Firebase can auto-check)
     *  2. Use alphabet letter and numbers, no special characters. (Use Firebase's rule system)
     *  3. The etUsername and/or etPassword are not blank. (Implement here)
     */

    private boolean validateForm(String username, String password){

        // Init assuming that the inputs are valid
        boolean valid = true;

        // Use if condition to check for the general rules
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){

            // Create/Init builder to show error
            builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Please enter all fields")
                    .setNegativeButton("Retry", null)
                    .create()
                    .show();
            valid = false;
        }
        return valid;

    }

    // Override method for all the user's action
    @Override
    public void onClick(View v){
        int i =  v.getId();
        if(i == R.id.bLogin)
            login();
        if(i == R.id.bGoRegister)
            goRegister();

    }
}
