package hexagon.board;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AlertInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private Intent commentIntent;

    private TextView tvSubjectArea;
    private TextView tvContentArea;
    private TextView tvLocationArea;
    private Button bComment;
    private ListView lvCommentArea;

    private ArrayList<String> commentList;

    private FirebaseUser mUser;
    private DatabaseReference mFirebaseDatabase;

    private String alertUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_info);

        tvSubjectArea = (TextView) findViewById(R.id.tvSubjectArea);
        tvContentArea = (TextView) findViewById(R.id.tvContentArea);
        tvLocationArea = (TextView) findViewById(R.id.tvLocationArea);
        bComment = (Button) findViewById(R.id.bComment);
        lvCommentArea = (ListView) findViewById(R.id.lvCommentArea);

        alertUid = getIntent().getStringExtra("alertUid");

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("alerts").child(alertUid);

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvSubjectArea.setText(dataSnapshot.child("subject").getValue().toString());
                tvContentArea.setText(dataSnapshot.child("content").getValue().toString());
                tvLocationArea.setText(dataSnapshot.child("location").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getCommentList();

        bComment.setOnClickListener(this);

    }

    private void getCommentList(){
        commentList = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("comments").child(alertUid);

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot value : dataSnapshot.getChildren()) {
                    String data = value.getValue().toString();
                    commentList.add(data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, commentList);

        lvCommentArea.setAdapter(adapter);

    }


    private void comment(){
        commentIntent = new Intent(AlertInfoActivity.this, CommentActivity.class);
        commentIntent.putExtra("alertUid", alertUid);
        AlertInfoActivity.this.startActivity(commentIntent);
    }

    // Override method for all the user's action
    @Override
    public void onClick(View v){
        int i =  v.getId();
        if(i == R.id.bComment)
            comment();
    }

}
