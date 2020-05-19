package hexagon.board;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    Intent alertInfoIntent;

    private FirebaseUser mUser;
    private DatabaseReference mFirebaseDatabase;

    private EditText etCommentArea;
    private Button bSubmitComment;
    private Button bDiscardComment;

    private AlertDialog.Builder builder;

    private String comment;
    private String alertUid;

    private CommentPOJO commentPOJO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        etCommentArea = (EditText) findViewById(R.id.etCommentArea);
        bSubmitComment = (Button) findViewById(R.id.bSubmitComment);
        bDiscardComment = (Button) findViewById(R.id.bDiscardComment);

        alertUid = getIntent().getStringExtra("alertUid");
        System.out.println("alertUid: " + alertUid);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        bSubmitComment.setOnClickListener(this);
        bDiscardComment.setOnClickListener(this);

    }

    private void submitComment(){

        comment = etCommentArea.getText().toString();

        // Check for user's input, whether it is validating any rules
        if(!validateForm(comment)){
            return;
        }

        saveNewComment(mUser.getUid(), comment);

    }

    private void saveNewComment(String uid, String comment){

        commentPOJO = new CommentPOJO(uid, comment);
        mFirebaseDatabase.child("comments").child(alertUid).push().setValue(commentPOJO);
        backAlertInfo();

    }


    private void backAlertInfo(){
        alertInfoIntent = new Intent(CommentActivity.this, AlertInfoActivity.class);
        alertInfoIntent.putExtra("alertUid", alertUid);
        CommentActivity.this.startActivity(alertInfoIntent);
    }

    private boolean validateForm(String comment){
        // Init assuming that the inputs are valid
        boolean valid = true;

        // Use if condition to check for the general rules
        if(TextUtils.isEmpty(comment)){

            // Create/Init builder to show error
            builder = new AlertDialog.Builder(CommentActivity.this);
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
        if(i == R.id.bSubmitComment)
            submitComment();
        if(i == R.id.bDiscardComment)
            backAlertInfo();

    }
}
