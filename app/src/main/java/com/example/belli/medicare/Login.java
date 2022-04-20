package com.example.belli.medicare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.example.belli.medicare.R;

public class Login extends AppCompatActivity {

    private Spinner spinner;
    private EditText editText;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        spinner = findViewById (R.id.spinnerCountries);
        spinner.setAdapter (new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Country.countryNames));
        editText = findViewById (R.id.editTextPhone);
        findViewById (R.id.buttonContinue).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                String code = Country.countryAreaCodes[spinner.getSelectedItemPosition ()];
                String number=editText.getText ().toString ().trim ();
                if (number.isEmpty ()|| number.length ()< 10){
                    editText.setError (" Valid Number is Required ");
                    editText.requestFocus ();
                    return;
                }
                String phoneNumber ="+" + code+number;

                Intent intent = new Intent(Login.this,Verify.class);
                intent.putExtra("phonenumber", phoneNumber);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart ();
        if (FirebaseAuth.getInstance ().getCurrentUser () != null){
            Intent intent = new Intent(this, Main.class);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);///faltu
            startActivity(intent);
        }
    }
}
