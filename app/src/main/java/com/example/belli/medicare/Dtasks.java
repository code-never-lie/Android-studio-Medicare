package com.example.belli.medicare;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Dtasks extends Fragment {

    FirebaseAuth auth;
    String user_id;
    DatabaseReference ResponseRef;
    ArrayList<String>Mstore=new ArrayList<>();
    ArrayList<String>MedicineName=new ArrayList<>();
    ArrayList<String>MedicineAmount=new ArrayList<>();
    ArrayList<String>Response=new ArrayList<>();
    ArrayList<String>ReqTime=new ArrayList<>();
    RecyclerView DtaskRecycler;

    public Dtasks() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_dtasks,container,false);

        user_id=auth.getInstance().getUid();
        ResponseRef= FirebaseDatabase.getInstance().getReference().child("RResponse").child(user_id);
        DtaskRecycler=rootView.findViewById(R.id.dtaskrecycler);
        DtaskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        ResponseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Mstore.clear();
                Response.clear();
                MedicineAmount.clear();
                MedicineName.clear();
                ReqTime.clear();
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    ReqTime.add(child.getKey());
                    Response.add(child.child("Response").getValue().toString());
                    MedicineName.add(child.child("Medicine").getValue().toString());
                    MedicineAmount.add(child.child("Amount").getValue().toString());
                    Mstore.add(child.child("MStore").getValue().toString());
                }
                DtaskRecycler.setAdapter(new List_of_Task(getContext(), Response, Mstore
                        , MedicineName, MedicineAmount,ReqTime));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return  rootView;
    }

}
 class List_of_Task extends RecyclerView.Adapter<List_of_Task.classViewHolder> {
    Context context;
     ArrayList<String>Mstore=new ArrayList<>();
     ArrayList<String>MedicineName=new ArrayList<>();
     ArrayList<String>MedicineAmount=new ArrayList<>();
     ArrayList<String>Response=new ArrayList<>();
     ArrayList<String>ReqTime=new ArrayList<>();
     String storename;


    public List_of_Task(Context ctx,ArrayList<String> response,ArrayList<String> mstore,ArrayList<String> medicineName,
                        ArrayList<String> medicineAmount,ArrayList<String> reqTime) {
        this.context=ctx;
        this.Mstore=mstore;
        this.MedicineAmount=medicineAmount;
        this.MedicineName=medicineName;
        this.Response=response;
        this.ReqTime=reqTime;
    }
    @NonNull
    @Override
    public List_of_Task.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_of_stock, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List_of_Task.classViewHolder holder, int position) {
        if(Mstore.get(position).equals("1")){
            storename="Attock Medical Store";
        }else if(Mstore.get(position).equals("2")){
            storename="Lahore Medical Store";
        }else{
            storename="Bilal Medical Store";
        }
        holder.TVmediname.setText("Medical Store: "+storename+
                                    "\nMedicine: "+MedicineName.get(position)+
                                    "\nAmount: "+MedicineAmount.get(position)+
                                    "\n"+ReqTime.get(position));
        holder.TVmediamount.setText(Response.get(position));
        if(Response.get(position).equals("Accepted")){
            holder.TVmediamount.setTextColor(Color.YELLOW);
        } else if(Response.get(position).equals("Rejected")){
            holder.TVmediamount.setTextColor(Color.RED);
        }else{
            holder.TVmediamount.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return ReqTime.size();
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

