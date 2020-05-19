package hexagon.board;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    // Current version of the app
    private static String VERSION = "0.1";

    // Var for intents
    private Intent loginIntent;

    // Var for Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;

    // Var for the app UI
    private EditText etFullname;
    private EditText etUsername;
    private EditText etPassword;
    private Button bRegister;
    private Button bBackLogin;
    private TextView tvSignup;
    private TextView tvbackLogin;

    // POJO for user to add into Firebase
    private UserPOJO user;

    // Var for UI feedback
    private ProgressDialog progressDialog;
    private AlertDialog.Builder builder;

    // Var used for backend work
    private String fullname;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Init all the var UI
        etFullname = (EditText) findViewById(R.id.etFullname);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bRegister = (Button) findViewById(R.id.bRegister);
        bBackLogin = (Button) findViewById(R.id.bBackLogin);
        tvSignup = (TextView) findViewById(R.id.tvSignup);
        tvbackLogin = (TextView) findViewById(R.id.tvBackLogin);
        progressDialog = new ProgressDialog(RegisterActivity.this);

        // Init Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // Set onClickListener for all the button
        bRegister.setOnClickListener(this);
        bBackLogin.setOnClickListener(this);

    }

    /**
     * This method is used to register the user when the user hit register
     * button. First get the user's input, Firebase will check if the
     * username is already in the database
     */

    public void register(){

        // Getting user's input from et and init it
        fullname = etFullname.getText().toString();
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        // Check for user's input, whether it is validating any rules
        if(!validateForm(fullname, username, password)){
            return;
        }

        /*
            Show progress dialog to user to let user knows that the app is processing
         */

        progressDialog.setMessage("Registering...");
        progressDialog.show();

        /*
            If pass valid's checkpoint Firebase will try to register the user's information.
                1. If Firebase Authentication step is successful, Firebase will return the
                user, then we can use that information to save the user's POJO to Firebase
                2. If fail, make toast.
        */

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            /*
                                Creating another method that in charge of saving the user into the
                                Firebase and switch screen for the user
                            */
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(RegisterActivity.this, "Sign up failed",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

    }

    private void onAuthSuccess(FirebaseUser mUser){

        //Save user into Firebase using UserPOJO
        saveUser(mUser.getUid(), fullname, username, password);

        //Switch screen, go back to Login Activity when done
        backLogin();
        progressDialog.dismiss();

        //Simple toast to give user feedback
        Toast.makeText(RegisterActivity.this, "Register Successful", Toast.LENGTH_SHORT);

    }

    private void saveUser(String uid, String fullname, String username, String password){
        user = new UserPOJO(uid, fullname, username, password);
        mFirebaseDatabase.child("users").child(uid).setValue(user);
    }


    /**
     * This method is used to validate user's input, check to see if the input
     * is validating any rules.
     * General rules:
     *  1. Not to be duplicate. (Firebase can auto-check)
     *  2. Use alphabet letter and numbers, no special characters. (Use Firebase's rule system)
     *  3. The fields cannot be blank. (Implement here)
     */

    private boolean validateForm(String fullname, String username, String password){

        // Init assuming that the inputs are valid
        boolean valid = true;

        // Use if condition to check for the general rules
        if(TextUtils.isEmpty(fullname) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){

            // Create/Init builder to show error
            builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("Please enter all fields")
                    .setNegativeButton("Retry", null)
                    .create()
                    .show();
            valid = false;
        }
        return valid;

    }

    // Simple method to switch back to Login screen
    private void backLogin() {
        loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        RegisterActivity.this.startActivity(loginIntent);
    }

    // Override method for all the user's action
    @Override
    public void onClick(View v){
        int i =  v.getId();
        if(i == R.id.bBackLogin)
            backLogin();
        if(i == R.id.bRegister)
            register();
    }

}
