package com.example.belli.medicare;


import android.app.Dialog;
import android.content.Context;
import android.net.rtp.RtpStream;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
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
public class Request_Donor extends Fragment {

    EditText ETmediamount,ETmediname,ETaprxprice;
    RecyclerView DreqRecycler;
    Button BTNreqdonor,BTNrequest;
    DatabaseReference Grequest;
    String user_id;
    FirebaseAuth auth;
    ArrayList<String>Mname=new ArrayList<>();
    ArrayList<String>Mamount=new ArrayList<>();
    ArrayList<String>RTime=new ArrayList<>();


    public Request_Donor() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_request__donor,container,false);

        user_id=auth.getInstance().getUid();
        Grequest= FirebaseDatabase.getInstance().getReference().child("GRequests");

        DreqRecycler=rootView.findViewById(R.id.dreqrecycler);
        BTNreqdonor=rootView.findViewById(R.id.btnreqdonor);

        DreqRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Grequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RTime.clear();
                Mname.clear();
                Mamount.clear();
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    RTime.add(child.getKey());
                    Mname.add(child.child("MedicineName").getValue().toString());
                    Mamount.add(child.child("MedicineAmount").getValue().toString());
                }
                DreqRecycler.setAdapter(new List_of_Grequests(getContext(),Mname,Mamount,RTime));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        BTNreqdonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.request_donor);

                ETmediname =dialog.findViewById(R.id.etdmedicinename);
                ETmediamount=dialog.findViewById(R.id.etdmedicineamount);
                ETaprxprice=dialog.findViewById(R.id.etprice);
                BTNrequest=dialog.findViewById(R.id.btndrequest);

                BTNrequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ETmediname.getText().toString().equals("") || ETmediname.getText().toString().equals(" ")) {
                            ETmediname.setError("Please Enter Medicine Name");
                        }else if (ETmediamount.getText().toString().equals("") || ETmediamount.getText().toString().equals(" ")) {
                            ETmediamount.setError("Please Enter Medicine Quantity");
                        }else if (ETaprxprice.getText().toString().equals("") || ETaprxprice.getText().toString().equals(" ")) {
                            ETmediamount.setError("Please Enter Aprx. Price");
                        } else {
                            Date currentLocalTime = Calendar.getInstance().getTime();
                            DateFormat date=new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
                            final String Time=date.format(currentLocalTime);

                            Map request = new HashMap();
                            request.put("Doctor", user_id);
                            request.put("MedicineName", ETmediname.getText().toString());
                            request.put("MedicineAmount", ETmediamount.getText().toString());
                            request.put("Price", ETaprxprice.getText().toString());
                            request.put("Response", "Sent");
                            Grequest.child(Time).setValue(request);

                            ETmediname.setText("");
                            ETmediamount.setText("");
                            ETaprxprice.setText("");
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Request Sent Successfully", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();
            }
        });
        return rootView;
    }

}
class List_of_Grequests extends RecyclerView.Adapter<List_of_Grequests.classViewHolder> {
    Context context;
    ArrayList<String>Mname=new ArrayList<>();
    ArrayList<String>Mamount=new ArrayList<>();
    ArrayList<String>RTime=new ArrayList<>();
    DatabaseReference Grequest;


    public List_of_Grequests(Context ctx,ArrayList<String> mamount,ArrayList<String> mname,ArrayList<String> rtime) {
        this.context=ctx;
        this.Mamount=mamount;
        this.Mname=mname;
        this.RTime=rtime;
    }
    @NonNull
    @Override
    public List_of_Grequests.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_of_stock, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List_of_Grequests.classViewHolder holder, final int position) {
        Grequest= FirebaseDatabase.getInstance().getReference().child("GRequests");

        holder.TVmediname.setText("Request for: "+Mname.get(position)+
                                "\nAmount: "+Mamount.get(position)+
                                "\n\n"+RTime.get(position));
        Grequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Response=dataSnapshot.child(RTime.get(position)).child("Response").getValue().toString();
                holder.TVmediamount.setText(Response);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return Mname.size();
    }
    public class classViewHolder extends RecyclerView.ViewHolder {
        TextView TVmediname,TVmediamount;

        public classViewHolder(View itemView) {
            super(itemView);


            TVmediamount =itemView.findViewById(R.id.tvmediamount);
            TVmediname = itemView.findViewById(R.id.tvmediname);


        }
    }
}

