package com.example.belli.medicare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main extends AppCompatActivity {
    FirebaseAuth auth;
    String user_id;
    DatabaseReference current_user_db;
    ProgressDialog ProgressWait;

    Button BTNDoctor,BTNMStore,BTNDonor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BTNDoctor=findViewById(R.id.btnDoctor);
        BTNDonor=findViewById(R.id.btnDonor);
        BTNMStore=findViewById(R.id.btnMstore);

        ProgressWait = new ProgressDialog(this);
        ProgressWait.setMessage("Please Wait..!!");
        ProgressWait.show();
        ProgressWait.setCanceledOnTouchOutside(false);



        user_id= auth.getInstance().getUid();
        current_user_db = FirebaseDatabase.getInstance().getReference().child("Users");

        current_user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int DonorAmount= (int) dataSnapshot.child("Donor").getChildrenCount();
                if(dataSnapshot.child("Donor").hasChild(user_id)){
                    Intent intent = new Intent(Main.this,DonorActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(dataSnapshot.child("Doctor").hasChild(user_id)){
                    Intent intent = new Intent(Main.this,Doctor_Nurses_CSActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(dataSnapshot.child("Mstore").hasChild(user_id)){
                    Intent intent = new Intent(Main.this,MStoreActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    ProgressWait.dismiss();
                }
                BTNMStore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Main.this,MStoreActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                BTNDonor.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(DonorAmount<10){
                            Intent intent = new Intent(Main.this,DonorActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"Sorry..! Donor Room is Full",Toast.LENGTH_LONG).show();
                        }

                    }
                });
                BTNDoctor.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Main.this,Doctor_Nurses_CSActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
