package com.example.belli.medicare;


import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MSstock extends Fragment {
    EditText ETmedicine,ETamount,ETpatientname,ETpatientid,ETmediamount;
    Button BTNaddmedicine,BTNreqmedicine,BTNrequest;
    RecyclerView StockRecycler;
    DatabaseReference stockref,Cuserref,reqref,responseref;
    FirebaseAuth auth;
    Spinner SPmediname;
    String user_id,MSname="10",FLAG="NO";
    ArrayList<String>Mediname=new ArrayList<>();
    ArrayList<String>Mediamount=new ArrayList<>();


    public MSstock() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_msstock,container,false);

        Bundle bundle = getArguments();
        if(bundle!=null) {
            MSname=bundle.getString("STORE");
        }

        user_id=auth.getInstance().getUid();
        stockref= FirebaseDatabase.getInstance().getReference().child("Stock");
        reqref= FirebaseDatabase.getInstance().getReference().child("Request");
        responseref= FirebaseDatabase.getInstance().getReference().child("RResponse");
        Cuserref= FirebaseDatabase.getInstance().getReference().child("Users").child("Mstore").child(user_id);


        ETamount=rootView.findViewById(R.id.etmediamount);
        ETmedicine=rootView.findViewById(R.id.etmediname);
        BTNaddmedicine=rootView.findViewById(R.id.btnaddmedi);
        BTNreqmedicine=rootView.findViewById(R.id.btnreqmedi);
        StockRecycler=rootView.findViewById(R.id.stockrecycler);

        StockRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Cuserref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(MSname.equals("1")||MSname.equals("2")||MSname.equals("3")){
                    ETmedicine.setVisibility(View.INVISIBLE);
                    ETamount.setVisibility(View.INVISIBLE);
                    BTNaddmedicine.setVisibility(View.INVISIBLE);
                    FLAG="YES";
                }else{
                    MSname=dataSnapshot.child("Mstore").getValue().toString();
                }
                stockref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(MSname).exists()){
                            Mediname.clear();
                            Mediamount.clear();
                            for(DataSnapshot child:dataSnapshot.child(MSname).getChildren()){
                                if(!child.getValue().toString().equals("0")){
                                    Mediname.add(child.getKey());
                                    Mediamount.add(child.getValue().toString());
                                }
                            }
                            if(!Mediname.isEmpty()&&FLAG.equals("YES")){
                                BTNreqmedicine.setVisibility(View.VISIBLE);
                            }
                            StockRecycler.setAdapter(new List_of_Stock(getContext(),Mediamount,Mediname));

                        }else{
                            Toast.makeText(getContext(),"Out of Stock",Toast.LENGTH_LONG).show();
                        }
                        BTNaddmedicine.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Map stock = new HashMap();
                                stock.put(ETmedicine.getText().toString(), ETamount.getText().toString());
                                stockref.child(MSname).updateChildren(stock);
                                ETamount.setText("");
                                ETmedicine.setText("");
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        BTNreqmedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.request_medicine);

                ETpatientname=dialog.findViewById(R.id.etpatientname);
                ETpatientid=dialog.findViewById(R.id.etpatientid);
                SPmediname =dialog.findViewById(R.id.spmedicinename);
                ETmediamount=dialog.findViewById(R.id.etmedicineamount);
                BTNrequest=dialog.findViewById(R.id.btnrequest);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                        Mediname);
                SPmediname.setAdapter(adapter);

                BTNrequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ETpatientname.getText().toString().equals("") || ETpatientname.getText().toString().equals(" ")) {
                            ETpatientname.setError("Please Enter Patient Name");
                        } else if (ETpatientid.getText().toString().equals("") || ETpatientid.getText().toString().equals(" ")) {
                            ETpatientid.setError("Please Enter Patient ID");
                        }else if (ETmediamount.getText().toString().equals("") || ETmediamount.getText().toString().equals(" ")) {
                            ETmediamount.setError("Please Enter Medicine Amount");
                        } else {
                            Date currentLocalTime = Calendar.getInstance().getTime();
                            DateFormat date=new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
                            final String Time=date.format(currentLocalTime);

                            Map request = new HashMap();
                            request.put("Doctor", user_id);
                            request.put("PatientName", ETpatientname.getText().toString());
                            request.put("PatientID", ETpatientid.getText().toString());
                            request.put("MedicineName", SPmediname.getSelectedItem().toString());
                            request.put("MedicineAmount", ETmediamount.getText().toString());
                            request.put("Response", "Sent");
                            reqref.child(MSname).child(Time).setValue(request);

                            Map rresponse = new HashMap();
                            rresponse.put("Medicine", SPmediname.getSelectedItem().toString());
                            rresponse.put("MStore", MSname);
                            rresponse.put("Amount", ETmediamount.getText().toString());
                            rresponse.put("Response", "Sent");
                            responseref.child(user_id).child(Time).setValue(rresponse);

                            ETamount.setText("");
                            ETmedicine.setText("");
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Request Sent Successfully", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();
            }
        });

        return  rootView;
    }

}
