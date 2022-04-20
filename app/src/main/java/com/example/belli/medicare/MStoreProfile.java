package com.example.belli.medicare;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.belli.medicare.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MStoreProfile extends Fragment {
    FirebaseAuth auth;
    Button msBTNeditdetailok,msBTNeditdetail;
    EditText msETusername,msETemail,msETdesignation,msETaddress,msETphone,msETlicenceno,msETeducation;
    DatabaseReference current_user_db;
    String user_id;
    Spinner msSPms;
    ImageView msIMGprofile;
    Button msBTNselectprofile;
    StorageReference storageRef;
    FirebaseStorage storage;
    TextView TVpoints;
    ArrayAdapter<CharSequence> adapter;
    int PostPoints,AnsPoints;
    private static final int RESULT_LOAD_IMG = 101;


    public MStoreProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_mstore_profile,container,false);

        user_id= auth.getInstance().getUid();

        current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Mstore").child(user_id);

        msBTNselectprofile=rootView.findViewById(R.id.MSbtnselectprofile);
        msBTNeditdetail= rootView.findViewById(R.id.MSbtneditinfo);
        msBTNeditdetailok=rootView.findViewById(R.id.MSbtnsaveinfo);
        msETusername=rootView.findViewById(R.id.MSetusername);
        msETemail=rootView.findViewById(R.id.MSetemail);
        msETphone=rootView.findViewById(R.id.MSetPhone);
        msETaddress=rootView.findViewById(R.id.MSetAddress);
        msETdesignation=rootView.findViewById(R.id.MSetDesignation);
        msETlicenceno=rootView.findViewById(R.id.MSetLicenseNo);
        msETeducation=rootView.findViewById(R.id.MSeteducation);
        msIMGprofile=rootView.findViewById(R.id.MSimgprofile);
        msSPms=rootView.findViewById(R.id.MSspms);
        TVpoints=rootView.findViewById(R.id.mforumpoints);

        adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.Mstore, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        msSPms.setAdapter(adapter);
        msSPms.setSelection(0);
        msSPms.setEnabled(false);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://medicare-9ed76.appspot.com/images/").child(user_id+".jpg");


        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                msIMGprofile.setImageBitmap(bitmap);
            }
        });
//////////////////////////////////FORUM CODE//////////////////////////////////////////////////////////////////
        FirebaseDatabase.getInstance().getReference().child("Forum").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PostPoints=0;
                AnsPoints=0;
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    if(child.child("UID").getValue().toString().equals(user_id)){
                        if(child.child("Points").exists()) {
                            PostPoints = PostPoints + Integer.parseInt(child.child("Points").getValue().toString());
                        }
                    }
                    for(DataSnapshot childAns:child.child("Answers").getChildren()){
                        if(childAns.child("UID").getValue().toString().equals(user_id)){
                            if(childAns.child("Points").exists()){
                                AnsPoints = AnsPoints + Integer.parseInt(childAns.child("Points").getValue().toString());
                            }
                        }
                    }
                }
                TVpoints.setText(String.valueOf(PostPoints+AnsPoints));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
/////////////////////////////PROFILE BASIC INFO////////////////////////////////////////////////////////////
        current_user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    msETusername.setText(dataSnapshot.child("Username").getValue(String.class));
                    msETphone.setText(dataSnapshot.child("Phone").getValue(String.class));
                    msETaddress.setText(dataSnapshot.child("Address").getValue(String.class));
                    msETdesignation.setText(dataSnapshot.child("Designation").getValue(String.class));
                    msETemail.setText(dataSnapshot.child("Email").getValue(String.class));
                    msETlicenceno.setText(dataSnapshot.child("LicenceNo").getValue(String.class));
                    msETeducation.setText(dataSnapshot.child("Education").getValue(String.class));

                    String s = dataSnapshot.child("Mstore").getValue().toString();
                    int m = Integer.parseInt(s);
                    msSPms.setSelection(m);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        msBTNeditdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msETemail.setEnabled(true);
                msBTNselectprofile.setVisibility(View.VISIBLE);
                msETlicenceno.setEnabled(true);
                msETusername.setEnabled(true);
                msETaddress.setEnabled(true);
                msSPms.setEnabled(true);
                msETdesignation.setEnabled(true);
                msETphone.setEnabled(true);
                msETeducation.setEnabled(true);
                msBTNeditdetail.setVisibility(View.INVISIBLE);
                msBTNeditdetailok.setVisibility(View.VISIBLE);
            }
        });
        msBTNselectprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMG);

            }
        });
        msBTNeditdetailok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int s=msSPms.getSelectedItemPosition();

                if(msETusername.getText().toString().isEmpty()){
                    msETusername.setError("Please Enter Username");
                }
                else if(msETlicenceno.getText().toString().isEmpty()){
                    msETlicenceno.setError("Please Enter Licence No");
                }
                else if(!msETemail.getText().toString().contains("@") || !msETemail.getText().toString().contains(".com")){
                    msETemail.setError("Please Enter Valid Email");
                }
                else if(msETphone.length()<10||msETphone.length()>13){
                    msETphone.setError("Please Enter Valid Phone");
                }
                else if(msETdesignation.getText().toString().isEmpty()){
                    msETdesignation.setError("Please Enter Designation");
                }
                else if(msETaddress.getText().toString().isEmpty()){
                    msETaddress.setError("Please Enter Address");
                }
                else if(msETeducation.getText().toString().isEmpty()){
                    msETeducation.setError("Please Enter Education");
                }
                else if(s==0){
                    TextView errorText = (TextView)msSPms.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Please Select Hospital");
                }
                else {
                    String Email = msETemail.getText().toString();
                    String Phone = msETphone.getText().toString();
                    String Username = msETusername.getText().toString();
                    String Designation = msETdesignation.getText().toString();
                    String Address = msETaddress.getText().toString();
                    String LicenceNo = msETlicenceno.getText().toString();
                    String Education = msETeducation.getText().toString();
                    String Mstore=String.valueOf(s);
////////////////////////////////store image in a firebase/////////////////////////////////////////
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    // Create a storage reference from our app root refrence
                    StorageReference storageRef = storage.getReference();
                    // Create a reference to "mountains.jpg"
                    StorageReference mountainsRef = storageRef.child("images/" + user_id + ".jpg");
                    // Get the data from an ImageView as bytes
                    msIMGprofile.setDrawingCacheEnabled(true);
                    msIMGprofile.buildDrawingCache();
                    Bitmap bitmap = msIMGprofile.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getActivity(), "Something went wrong with upload", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getActivity(), "Succesfuly updated", Toast.LENGTH_LONG).show();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();////faltu
                        }
                    });
/////////////////////////Database men value dalny k ley...........................
                    Map details=new HashMap();
                    details.put("Username",Username);
                    details.put("Email",Email);
                    details.put("Phone",Phone);
                    details.put("Designation",Designation);
                    details.put("Address",Address);
                    details.put("LicenceNo",LicenceNo);
                    details.put("Education",Education);
                    details.put("Mstore", Mstore);
                    current_user_db.updateChildren(details);

                    msETemail.setEnabled(false);
                    msETlicenceno.setEnabled(false);
                    msETusername.setEnabled(false);
                    msSPms.setEnabled(false);
                    msETaddress.setEnabled(false);
                    msETdesignation.setEnabled(false);
                    msETphone.setEnabled(false);
                    msETeducation.setEnabled(false);
                    msBTNselectprofile.setVisibility(View.INVISIBLE);
                    msBTNeditdetail.setVisibility(View.VISIBLE);
                    msBTNeditdetailok.setVisibility(View.INVISIBLE);
                }
            }
        });
        return rootView;
    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                msIMGprofile.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

}
