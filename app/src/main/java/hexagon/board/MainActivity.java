package hexagon.board;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Intent createAlertIntent;
    private Intent alertHeader;
    private Intent showMapIntent;

    private Button bAdd;
    private Button bShowMap;
    private ListView lvAlertList;
    private ArrayList<String> alertList;

    private DatabaseReference mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bAdd = (Button) findViewById(R.id.bCreateNewAlert);
        bShowMap = (Button) findViewById(R.id.bShowMap);
        lvAlertList = (ListView) findViewById(R.id.lvAlertList);

        getAlertList();

        bAdd.setOnClickListener(this);
        bShowMap.setOnClickListener(this);

    }

    private void getAlertList(){

        alertList = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("alerts");
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot value : dataSnapshot.getChildren())
                    alertList.add(value.getKey().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, alertList);
        lvAlertList.setAdapter(adapter);

        lvAlertList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                int itemPosition = position;
                String itemValue = (String) lvAlertList.getItemAtPosition(position);
                alertHeader = new Intent(MainActivity.this, AlertInfoActivity.class);
                alertHeader.putExtra("alertUid", itemValue);
                MainActivity.this.startActivity(alertHeader);
            }


        });
    }

    private void addAlert(){
        createAlertIntent = new Intent(MainActivity.this, AddAlertActivity.class);
        MainActivity.this.startActivity(createAlertIntent);
    }

    private void showMap(){
        showMapIntent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(showMapIntent);
    }

    // Override method for all the user's action
    @Override
    public void onClick(View v){
        int i =  v.getId();
        if(i == R.id.bCreateNewAlert)
            addAlert();
        if(i == R.id.bShowMap)
            showMap();
    }
}
