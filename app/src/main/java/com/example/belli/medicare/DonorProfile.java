package com.example.belli.medicare;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.belli.medicare.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class DonorProfile extends Fragment {
    FirebaseAuth auth;
    Button doBTNeditdetailok,doBTNeditdetail;
    EditText doETusername,doETemail,doETcnic,doETaddress,doETphone;
    ImageView doIMGprofile;
    Button doBTNselectprofile,BTNrating;
    StorageReference storageRef;
    FirebaseStorage storage;
    DatabaseReference current_user_db;
    String user_id;
    TextView TVpoints;
    private static final int RESULT_LOAD_IMG = 101;

    public DonorProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_donor_profile,container,false);

        user_id= auth.getInstance().getUid();

        current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Donor").child(user_id);

        doBTNselectprofile=rootView.findViewById(R.id.DObtnselectprofile);
        doBTNeditdetail= rootView.findViewById(R.id.DObtneditinfo);
        doBTNeditdetailok=rootView.findViewById(R.id.DObtnsaveinfo);
        BTNrating=rootView.findViewById(R.id.btnrating);
        doETusername=rootView.findViewById(R.id.DOetusername);
        doETemail=rootView.findViewById(R.id.DOetemail);
        doETphone=rootView.findViewById(R.id.DOetphone);
        doETaddress=rootView.findViewById(R.id.DOetaddress);
        doETcnic=rootView.findViewById(R.id.DOetcnic);
        doIMGprofile=rootView.findViewById(R.id.DOimgprofile);
        TVpoints=rootView.findViewById(R.id.dforumpoints);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://medicare-9ed76.appspot.com/images/").child(user_id+".jpg");


        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                doIMGprofile.setImageBitmap(bitmap);
            }
        });
//////////////////////////////Forum Code ///////////////////////////////////////////////////////
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
/////////////////////////////////////IMage Basic Info //////////////////////////////////////////////////
        current_user_db.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    doETusername.setText(dataSnapshot.child("Username").getValue(String.class));
                    doETphone.setText(dataSnapshot.child("Phone").getValue(String.class));
                    doETaddress.setText(dataSnapshot.child("Address").getValue(String.class));
                    doETcnic.setText(dataSnapshot.child("CNIC").getValue(String.class));
                    doETemail.setText(dataSnapshot.child("Email").getValue(String.class));
                    if(dataSnapshot.child("Rating").exists()){
                        String Rating=dataSnapshot.child("Rating").getValue().toString();
                        int Rate=Integer.parseInt(Rating);
                        if(Rate>=1000 && Rate<2000){
                            BTNrating.setBackground(getResources().getDrawable(R.drawable.one));
                        }else if(Rate>=2000 && Rate<3000){
                            BTNrating.setBackground(getResources().getDrawable(R.drawable.two));
                        }else if(Rate>=3000 && Rate<4000){
                            BTNrating.setBackground(getResources().getDrawable(R.drawable.three));
                        }else if(Rate>=4000 && Rate<5000){
                            BTNrating.setBackground(getResources().getDrawable(R.drawable.four));
                        }else if(Rate>=5000){
                            BTNrating.setBackground(getResources().getDrawable(R.drawable.five));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        doBTNeditdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doETemail.setEnabled(true);
                doBTNselectprofile.setVisibility(View.VISIBLE);
                doETusername.setEnabled(true);
                doETaddress.setEnabled(true);
                doETcnic.setEnabled(true);
                doETphone.setEnabled(true);
                doBTNeditdetail.setVisibility(View.INVISIBLE);
                doBTNeditdetailok.setVisibility(View.VISIBLE);
            }
        });
        doBTNselectprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMG);

            }
        });
        doBTNeditdetailok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean NIC = Pattern.matches("\\d{5}-\\d{7}-\\d{1}$", doETcnic.getText().toString());

                if(doETusername.getText().toString().isEmpty()){
                    doETusername.setError("Please Enter Username");
                }
                else if(doETphone.length()<10||doETphone.length()>13){
                    doETphone.setError("Please Enter Valid Phone");
                }
                else if(!doETemail.getText().toString().contains("@") || !doETemail.getText().toString().contains(".com")){
                    doETemail.setError("Please Enter Valid Email");
                }
                else if(!NIC){
                    doETcnic.setError("Please Enter Valid CNIC");
                }
                else if(doETaddress.getText().toString().isEmpty()){
                    doETaddress.setError("Please Enter Address");
                }
                else {
                    String Email = doETemail.getText().toString();
                    String Phone = doETphone.getText().toString();
                    String Username = doETusername.getText().toString();
                    String CNIC = doETcnic.getText().toString();
                    String Address = doETaddress.getText().toString();

                    Map details=new HashMap();
                    details.put("Username",Username);
                    details.put("Email",Email);
                    details.put("Phone",Phone);
                    details.put("CNIC",CNIC);
                    details.put("Address",Address);
                    current_user_db.updateChildren(details);

                    doETemail.setEnabled(false);
                    doETusername.setEnabled(false);
                    doETaddress.setEnabled(false);
                    doETcnic.setEnabled(false);
                    doETphone.setEnabled(false);
                    doBTNeditdetail.setVisibility(View.VISIBLE);
                    doBTNeditdetailok.setVisibility(View.INVISIBLE);
                    doBTNselectprofile.setVisibility(View.INVISIBLE);


                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    // Create a storage reference from our app root refrence
                    StorageReference storageRef = storage.getReference();
                    // Create a reference to "mountains.jpg"
                    StorageReference mountainsRef = storageRef.child("images/" + user_id + ".jpg");
                    // Get the data from an ImageView as bytes
                    doIMGprofile.setDrawingCacheEnabled(true);
                    doIMGprofile.buildDrawingCache();
                    Bitmap bitmap = doIMGprofile.getDrawingCache();
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
                doIMGprofile.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

}
