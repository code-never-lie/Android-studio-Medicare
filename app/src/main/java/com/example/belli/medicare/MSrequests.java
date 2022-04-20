package com.example.belli.medicare;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MSrequests extends Fragment {

    RecyclerView MSreqrecycler;
    FirebaseAuth auth;
    DatabaseReference reqref,Cuserref;
    ArrayList<String>PatientName=new ArrayList<>();
    ArrayList<String>PatientID=new ArrayList<>();
    ArrayList<String>MedicineName=new ArrayList<>();
    ArrayList<String>MedicineAmount=new ArrayList<>();
    ArrayList<String>Doctor=new ArrayList<>();
    ArrayList<String>ReqTime=new ArrayList<>();

    String MSname,user_id;


    public MSrequests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_msrequests,container,false);

        user_id=auth.getInstance().getUid();
        reqref= FirebaseDatabase.getInstance().getReference().child("Request");
        Cuserref= FirebaseDatabase.getInstance().getReference().child("Users").child("Mstore");

        MSreqrecycler=rootView.findViewById(R.id.msreqrecycler);
        MSreqrecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Cuserref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user_id).exists()) {
                    MSname = dataSnapshot.child(user_id).child("Mstore").getValue().toString();
                }
                reqref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(MSname).exists()) {
                            PatientName.clear();
                            PatientID.clear();
                            MedicineAmount.clear();
                            MedicineName.clear();
                            Doctor.clear();
                            ReqTime.clear();
                            for (DataSnapshot child : dataSnapshot.child(MSname).getChildren()) {
                                ReqTime.add(child.getKey());
                                PatientName.add(child.child("PatientName").getValue().toString());
                                PatientID.add(child.child("PatientID").getValue().toString());
                                MedicineName.add(child.child("MedicineName").getValue().toString());
                                MedicineAmount.add(child.child("MedicineAmount").getValue().toString());
                                Doctor.add(child.child("Doctor").getValue().toString());
                            }
                            MSreqrecycler.setAdapter(new list_of_request(getContext(), Doctor, PatientName, PatientID
                                    , MedicineName, MedicineAmount,ReqTime, MSname));
                        }
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


        return rootView;
    }

}
 class list_of_request extends RecyclerView.Adapter<list_of_request.classViewHolder> {
    Context context;
     ArrayList<String>PatientName=new ArrayList<>();
     ArrayList<String>PatientID=new ArrayList<>();
     ArrayList<String>MedicineName=new ArrayList<>();
     ArrayList<String>MedicineAmount=new ArrayList<>();
     ArrayList<String>Doctor=new ArrayList<>();
     ArrayList<String>ReqTime=new ArrayList<>();
     DatabaseReference RResponceref,Reqref,Stockref;
     String MSname,user_id;
     FirebaseAuth auth;


     public list_of_request(Context ctx,ArrayList<String> doctor,ArrayList<String> patinetname,
                           ArrayList<String> patientid,ArrayList<String> medicinename,
                           ArrayList<String> medicineamount,ArrayList<String> reqtime,String msname) {
        this.context=ctx;
        this.PatientID=patientid;
        this.PatientName=patinetname;
        this.MedicineAmount=medicineamount;
        this.MedicineName=medicinename;
        this.Doctor=doctor;
        this.MSname=msname;
        this.ReqTime=reqtime;
    }
    @NonNull
    @Override
    public list_of_request.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ms_req_item, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final list_of_request.classViewHolder holder, final int position) {
         user_id=auth.getInstance().getUid();
        RResponceref=FirebaseDatabase.getInstance().getReference().child("RResponse");
        Reqref=FirebaseDatabase.getInstance().getReference().child("Request").child(MSname);
        Stockref=FirebaseDatabase.getInstance().getReference().child("Stock").child(MSname);


        Reqref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Response=dataSnapshot.child(ReqTime.get(position)).child("Response").getValue().toString();
                if(Response.equals("Accepted")){
                    holder.BTNaccept.setVisibility(View.INVISIBLE);
                    holder.BTNreject.setVisibility(View.INVISIBLE);
                    holder.BTNtaskcomplete.setVisibility(View.VISIBLE);
                }else if(Response.equals("Completed")){
                    holder.BTNaccept.setVisibility(View.INVISIBLE);
                    holder.BTNreject.setVisibility(View.INVISIBLE);
                    holder.BTNtaskcomplete.setVisibility(View.INVISIBLE);
                    holder.TVcompleted.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.TVrequest.setText("Request for: "+MedicineName.get(position)+
                                    "\nAmount: "+MedicineAmount.get(position)+
                                    "\nPatinet Name: "+PatientName.get(position)+
                                    "\nPatinet ID: "+PatientID.get(position)+
                                    "\n"+ReqTime.get(position));
        holder.BTNaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RResponceref.child(Doctor.get(position)).child(ReqTime.get(position)).child("Response").setValue("Accepted");
                Reqref.child(ReqTime.get(position)).child("Response").setValue("Accepted");

                holder.BTNaccept.setVisibility(View.INVISIBLE);
                holder.BTNreject.setVisibility(View.INVISIBLE);
                holder.BTNtaskcomplete.setVisibility(View.VISIBLE);
            }
        });
        holder.BTNreject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RResponceref.child(Doctor.get(position)).child(ReqTime.get(position)).child("Response").setValue("Rejected");
                Reqref.child(ReqTime.get(position)).removeValue();
            }
        });
        holder.BTNtaskcomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog myQuittingDialogBox =new AlertDialog.Builder(context)
                        //set message, title, and icon
                        .setTitle("Completed?")
                        .setMessage("Please Cross Check the Patient Info With Your Request, If its Correct Then Mark This Task as Completed.\nAre you Sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Stockref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.child(MedicineName.get(position)).exists()){
                                            String am=dataSnapshot.child(MedicineName.get(position)).getValue().toString();
                                            int amount=Integer.parseInt(am);
                                            int deduct=Integer.parseInt(MedicineAmount.get(position));
                                            if(amount>=deduct){
                                                amount=amount-deduct;
                                                Stockref.child(MedicineName.get(position)).setValue(String.valueOf(amount));
                                                RResponceref.child(Doctor.get(position)).child(ReqTime.get(position)).child("Response").setValue("Completed");
                                                Reqref.child(ReqTime.get(position)).child("Response").setValue("Completed");
                                                holder.TVcompleted.setVisibility(View.VISIBLE);
                                                holder.BTNtaskcomplete.setVisibility(View.INVISIBLE);
                                            }else{
                                                Toast.makeText(context,"Sorrey Couldn't Complete..! Out of Stock",Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                myQuittingDialogBox.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return PatientID.size();
    }
    public class classViewHolder extends RecyclerView.ViewHolder {
        TextView TVrequest,TVcompleted;
        Button BTNaccept,BTNreject,BTNtaskcomplete;

        public classViewHolder(View itemView) {
            super(itemView);


            TVrequest =itemView.findViewById(R.id.tvrequest);
            TVcompleted =itemView.findViewById(R.id.tvcompleted);
            BTNaccept = itemView.findViewById(R.id.btnreqaccept);
            BTNreject = itemView.findViewById(R.id.btnreqreject);
            BTNtaskcomplete = itemView.findViewById(R.id.btntaskcomplete);
        }
    }
}

