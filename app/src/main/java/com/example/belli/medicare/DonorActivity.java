package com.example.belli.medicare;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.belli.medicare.R;

import java.util.HashMap;
import java.util.Map;

public class DonorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference DonerDatabase;
    FirebaseAuth auth;
    DatabaseReference Donordatabase,Donatedatabase;
    Button ChooseAMS,ChooseLMS,ChooseBMS,BTNdonate;

    ImageView DODrawerimage;
    EditText ETdonatemedicine,ETdonateqty;
    TextView DODrawername;
    String user_id,DonerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user_id=auth.getInstance().getUid();
        Donordatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Donor")
                .child(user_id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        DODrawerimage=hView.findViewById(R.id.drdrawerimage);
        DODrawername=hView.findViewById(R.id.drdrawername);

        DonorProfile fragment= new DonorProfile();
        android.support.v4.app.FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.DOActivityContainer,fragment);
        fragmentTransaction.commit();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.doner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.DOlogout) {
            Intent intent=new Intent(this,Login.class);
            startActivity(intent);
            auth.getInstance().signOut();
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

    if (id == R.id.Dforum) {
        Forum fragment= new Forum();
        android.support.v4.app.FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.DOActivityContainer,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }else if (id == R.id.donate) {
            final Dialog dialog=new Dialog(this, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.choose_ms_stock);

            ChooseAMS=dialog.findViewById(R.id.chooseAMS);
            ChooseLMS=dialog.findViewById(R.id.chooseLMS);
            ChooseBMS=dialog.findViewById(R.id.chooseBMS);

            Donatedatabase=FirebaseDatabase.getInstance().getReference().child("Stock");

            ChooseAMS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog Donatedialog=new Dialog(DonorActivity.this, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
                    Donatedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    Donatedialog.setContentView(R.layout.donate);

                    BTNdonate=Donatedialog.findViewById(R.id.btndonate);
                    ETdonatemedicine=Donatedialog.findViewById(R.id.etdonatemedicine);
                    ETdonateqty=Donatedialog.findViewById(R.id.etdonateqty);

                    BTNdonate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(ETdonatemedicine.getText().toString().isEmpty()){
                                ETdonatemedicine.setError("Please enter Medicine name");
                            }else if (ETdonateqty.getText().toString().isEmpty()) {
                                ETdonateqty.setError("Please enter Quantity");
                            }else{
                                Map stock = new HashMap();
                                stock.put(ETdonatemedicine.getText().toString(), ETdonateqty.getText().toString());
                                Donatedatabase.child("1").updateChildren(stock);
                                ETdonateqty.setText("");
                                ETdonatemedicine.setText("");
                                Toast.makeText(getApplicationContext(),"Donated Succesfully",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    Donatedialog.show();
                }
            });
            ChooseLMS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog Donatedialog=new Dialog(DonorActivity.this, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
                    Donatedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    Donatedialog.setContentView(R.layout.donate);

                    BTNdonate=Donatedialog.findViewById(R.id.btndonate);
                    ETdonatemedicine=Donatedialog.findViewById(R.id.etdonatemedicine);
                    ETdonateqty=Donatedialog.findViewById(R.id.etdonateqty);

                    BTNdonate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(ETdonatemedicine.getText().toString().isEmpty()){
                                ETdonatemedicine.setError("Please enter Medicine name");
                            }else if (ETdonateqty.getText().toString().isEmpty()) {
                                ETdonateqty.setError("Please enter Quantity");
                            }else{
                                Map stock = new HashMap();
                                stock.put(ETdonatemedicine.getText().toString(), ETdonateqty.getText().toString());
                                Donatedatabase.child("2").updateChildren(stock);
                                ETdonateqty.setText("");
                                ETdonatemedicine.setText("");
                                Toast.makeText(getApplicationContext(),"Donated Succesfully",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    Donatedialog.show();
                }
            });
            ChooseBMS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog Donatedialog=new Dialog(DonorActivity.this, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
                    Donatedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    Donatedialog.setContentView(R.layout.donate);

                    BTNdonate=Donatedialog.findViewById(R.id.btndonate);
                    ETdonatemedicine=Donatedialog.findViewById(R.id.etdonatemedicine);
                    ETdonateqty=Donatedialog.findViewById(R.id.etdonateqty);

                    BTNdonate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(ETdonatemedicine.getText().toString().isEmpty()){
                                ETdonatemedicine.setError("Please enter Medicine name");
                            }else if (ETdonateqty.getText().toString().isEmpty()) {
                                ETdonateqty.setError("Please enter Quantity");
                            }else{
                                Map stock = new HashMap();
                                stock.put(ETdonatemedicine.getText().toString(), ETdonateqty.getText().toString());
                                Donatedatabase.child("3").updateChildren(stock);
                                ETdonateqty.setText("");
                                ETdonatemedicine.setText("");
                                Toast.makeText(getApplicationContext(),"Donated Succesfully",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    Donatedialog.show();
                }
            });
            dialog.show();
        } else if (id == R.id.requests) {
            DonorRequests fragment = new DonorRequests();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.DOActivityContainer, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
