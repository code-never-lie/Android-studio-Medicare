package com.example.belli.medicare;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class Doctor_NCSProfile extends Fragment {
    FirebaseAuth auth;
    ArrayAdapter<CharSequence> adapter;
    Button dcBTNeditdetailok,dcBTNeditdetail;
    EditText dcETusername,dcETemail,dcETcnic,dcETaddress,dcETphone,dcETpost,dcETqualification;
    Spinner dcSPhsptl;
    ImageView dcIMGprofile;
    Button dcBTNselectprofile;
    StorageReference storageRef;
    FirebaseStorage storage;
    DatabaseReference current_user_db;
    String user_id;
    TextView TVpoints;
    private static final int RESULT_LOAD_IMG = 101;


    public Doctor_NCSProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_doctor_ncs_profile,container,false);

        user_id= auth.getInstance().getUid();
        current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Doctor").child(user_id);


        dcBTNselectprofile=rootView.findViewById(R.id.DCbtnselectprofile);
        dcBTNeditdetail= rootView.findViewById(R.id.DCbtneditinfo);
        dcBTNeditdetailok=rootView.findViewById(R.id.DCbtnsaveinfo);
        dcETusername=rootView.findViewById(R.id.DCetusername);
        dcETemail=rootView.findViewById(R.id.DCetemail);
        dcETphone=rootView.findViewById(R.id.DCetphone);
        dcETaddress=rootView.findViewById(R.id.DCetaddress);
        dcETcnic=rootView.findViewById(R.id.DCetcnic);
        dcETpost=rootView.findViewById(R.id.DCetpost);
        dcETqualification=rootView.findViewById(R.id.DCetqualification);
        dcSPhsptl=rootView.findViewById(R.id.DCsphsptl);
        dcIMGprofile=rootView.findViewById(R.id.DCimgprofile);
        TVpoints=rootView.findViewById(R.id.dncsforumpoints);


        dcSPhsptl.setEnabled(false);
        adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.hsptl, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dcSPhsptl.setAdapter(adapter);
        dcSPhsptl.setSelection(0);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://medicare-9ed76.appspot.com/images/").child(user_id+".jpg");


        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                dcIMGprofile.setImageBitmap(bitmap);
            }
        });
///////////////////////Forum Points//////////////////////////////////
        FirebaseDatabase.getInstance().getReference().child("ForumPoints").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user_id).exists()){
                    TVpoints.setText(dataSnapshot.child(user_id).getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /////////////BASIC PROFILE INFO//////////////////////////
        current_user_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    dcETusername.setText(dataSnapshot.child("Username").getValue(String.class));
                    dcETaddress.setText(dataSnapshot.child("Address").getValue(String.class));
                    dcETcnic.setText(dataSnapshot.child("CNIC").getValue(String.class));
                    dcETphone.setText(dataSnapshot.child("Phone").getValue(String.class));
                    dcETemail.setText(dataSnapshot.child("Email").getValue(String.class));
                    dcETpost.setText(dataSnapshot.child("Post").getValue(String.class));
                    dcETqualification.setText(dataSnapshot.child("Qualification").getValue(String.class));
                    String s = dataSnapshot.child("Hospital").getValue().toString();
                    int hsptl = Integer.parseInt(s);
                    dcSPhsptl.setSelection(hsptl);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        dcBTNeditdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dcETemail.setEnabled(true);
                dcBTNselectprofile.setVisibility(View.VISIBLE);
                dcETusername.setEnabled(true);
                dcETphone.setEnabled(true);
                dcETpost.setEnabled(true);
                dcETqualification.setEnabled(true);
                dcSPhsptl.setEnabled(true);
                dcETcnic.setEnabled(true);
                dcETaddress.setEnabled(true);
                dcBTNeditdetail.setVisibility(View.INVISIBLE);
                dcBTNeditdetailok.setVisibility(View.VISIBLE);
            }
        });
        dcBTNselectprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMG);

            }
        });
        dcBTNeditdetailok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean NIC = Pattern.matches("\\d{5}-\\d{7}-\\d{1}$", dcETcnic.getText().toString());
//                Toast.makeText(getContext(),"jjdd"+NIC,Toast.LENGTH_LONG).show();
                int s=dcSPhsptl.getSelectedItemPosition();

                if(dcETusername.getText().toString().isEmpty()){
                    dcETusername.setError("Please Enter Username");
                }
                else if(dcETphone.length()<10||dcETphone.length()>13){
                    dcETphone.setError("Please Enter Valid Phone");
                }
                else if(!dcETemail.getText().toString().contains("@") || !dcETemail.getText().toString().contains(".com")){
                    dcETemail.setError("Please Enter Valid Email");
                }
                else if(!NIC){
                    dcETcnic.setError("Please Enter Valid CNIC");
                }
                else if(dcETaddress.getText().toString().isEmpty()){
                    dcETaddress.setError("Please Enter Address");
                }
                else if(dcETpost.getText().toString().isEmpty()){
                    dcETpost.setError("Please Enter Address");
                }
                else if(dcETqualification.getText().toString().isEmpty()){
                    dcETqualification.setError("Please Enter Address");
                }
                else if(s==0){
                    TextView errorText = (TextView)dcSPhsptl.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Please Select Hospital");
                }
                else {
                    String Email = dcETemail.getText().toString();
                    String Phone = dcETphone.getText().toString();
                    String CNIC = dcETcnic.getText().toString();
                    String Address = dcETaddress.getText().toString();
                    String Username = dcETusername.getText().toString();
                    String Post = dcETpost.getText().toString();
                    String Qualification = dcETqualification.getText().toString();
                    String hsptl=String.valueOf(s);

                    dcETemail.setEnabled(false);
                    dcETusername.setEnabled(false);
                    dcETphone.setEnabled(false);
                    dcETcnic.setEnabled(false);
                    dcETaddress.setEnabled(false);
                    dcETpost.setEnabled(false);
                    dcETqualification.setEnabled(false);
                    dcSPhsptl.setEnabled(false);
                    dcBTNeditdetail.setVisibility(View.VISIBLE);
                    dcBTNeditdetailok.setVisibility(View.INVISIBLE);
                    dcBTNselectprofile.setVisibility(View.INVISIBLE);


                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    // Create a storage reference from our app root refrence
                    StorageReference storageRef = storage.getReference();
                    // Create a reference to "mountains.jpg"
                    StorageReference mountainsRef = storageRef.child("images/" + user_id + ".jpg");
                    // Get the data from an ImageView as bytes
                    dcIMGprofile.setDrawingCacheEnabled(true);
                    dcIMGprofile.buildDrawingCache();
                    Bitmap bitmap = dcIMGprofile.getDrawingCache();
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
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    });


                    Map details = new HashMap();
                    details.put("Username", Username);
                    details.put("Email", Email);
                    details.put("Phone", Phone);
                    details.put("CNIC", CNIC);
                    details.put("Address", Address);
                    details.put("Post", Post);
                    details.put("Qualification", Qualification);
                    details.put("Hospital", hsptl);
                    current_user_db.updateChildren(details);
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
                dcIMGprofile.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

}
