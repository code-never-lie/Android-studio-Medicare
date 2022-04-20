package com.example.belli.medicare;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DonorRequests extends Fragment {

    RecyclerView DrequestRecycler;
    DatabaseReference DonorReuestdatabase;
    ArrayList<String>MedicineQty=new ArrayList<>();
    ArrayList<String>MedicineName=new ArrayList<>();
    ArrayList<String>Response=new ArrayList<>();
    ArrayList<String>Date=new ArrayList<>();
    ArrayList<String>Price=new ArrayList<>();



    public DonorRequests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_donor_requests,container,false);

        DrequestRecycler=rootView.findViewById(R.id.Drequestrecycler);
        DrequestRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        DonorReuestdatabase= FirebaseDatabase.getInstance().getReference().child("GRequests");

        DonorReuestdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Date.clear();
                MedicineName.clear();
                MedicineQty.clear();
                Response.clear();
                Price.clear();
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    Date.add(child.getKey());
                    MedicineName.add(child.child("MedicineName").getValue().toString());
                    MedicineQty.add(child.child("MedicineAmount").getValue().toString());
                    Response.add(child.child("Response").getValue().toString());
                    Price.add(child.child("Price").getValue().toString());
                }
                DrequestRecycler.setAdapter(new List_of_Drequests(getContext(),Date,MedicineName,MedicineQty,Price,Response));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

}
class List_of_Drequests extends RecyclerView.Adapter<List_of_Drequests.classViewHolder> {
    Context context;
    ArrayList<String>MedicineQty=new ArrayList<>();
    ArrayList<String>MedicineName=new ArrayList<>();
    ArrayList<String>Date=new ArrayList<>();
    ArrayList<String>Price=new ArrayList<>();
    ArrayList<String>Response=new ArrayList<>();
    Button ChooseAMS,ChooseLMS,ChooseBMS;
    DatabaseReference Donatedatabase,RequestDatabase,DonorDatabase;
    String userid;
    FirebaseAuth auth;
    int Ra;


    public List_of_Drequests(Context ctx,ArrayList<String> date,ArrayList<String> medicineName,ArrayList<String> medicineQty,
                             ArrayList<String> price,ArrayList<String> response) {
        this.context=ctx;
        this.MedicineName=medicineName;
        this.MedicineQty=medicineQty;
        this.Date=date;
        this.Price=price;
        this.Response=response;
    }
    @NonNull
    @Override
    public List_of_Drequests.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_of_drequests, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List_of_Drequests.classViewHolder holder, final int position) {
        holder.TVrequestitem.setText(" Request for: "+MedicineName.get(position)+
                                    "\n Quantity: "+MedicineQty.get(position)+
                                    "\n Price: "+Price.get(position)+
                                    "\n Please Deliver to Near Medical Store"+
                                    "\n\n "+Date.get(position));
        if(Response.get(position).equals("Donated")){
            holder.BTNdonate.setVisibility(View.INVISIBLE);
            holder.TVcompleted.setVisibility(View.VISIBLE);
        }
        holder.BTNdonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.choose_ms_stock);

                ChooseAMS=dialog.findViewById(R.id.chooseAMS);
                ChooseLMS=dialog.findViewById(R.id.chooseLMS);
                ChooseBMS=dialog.findViewById(R.id.chooseBMS);

                userid=auth.getInstance().getUid();
                Donatedatabase=FirebaseDatabase.getInstance().getReference().child("Stock");
                RequestDatabase=FirebaseDatabase.getInstance().getReference().child("GRequests");
                DonorDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child("Donor").child(userid);

                DonorDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("Rating").exists()) {
                            final String Rate = dataSnapshot.child("Rating").getValue().toString();
                            Ra= Integer.parseInt(Rate);

                        }
                            ChooseAMS.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Map stock = new HashMap();
                                    stock.put(MedicineName.get(position), MedicineQty.get(position));
                                    Donatedatabase.child("1").updateChildren(stock);

                                    RequestDatabase.child(Date.get(position)).child("Response").setValue("Donated");
                                    int P=Integer.parseInt(Price.get(position));
                                    int Final=P+Ra;
                                    String Rating=String.valueOf(Final);
                                    DonorDatabase.child("Rating").setValue(Rating);

                                    Toast.makeText(context,"Donated Succesfully",Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                            ChooseLMS.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Map stock = new HashMap();
                                    stock.put(MedicineName.get(position), MedicineQty.get(position));
                                    Donatedatabase.child("2").updateChildren(stock);

                                    RequestDatabase.child(Date.get(position)).child("Response").setValue("Donated");
                                    int P=Integer.parseInt(Price.get(position));
                                    int Final=P+Ra;
                                    String Rating=String.valueOf(Final);
                                    DonorDatabase.child("Rating").setValue(Rating);

                                    Toast.makeText(context,"Donated Succesfully",Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                            ChooseBMS.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Map stock = new HashMap();
                                    stock.put(MedicineName.get(position), MedicineQty.get(position));
                                    Donatedatabase.child("3").updateChildren(stock);

                                    RequestDatabase.child(Date.get(position)).child("Response").setValue("Donated");
                                    int P=Integer.parseInt(Price.get(position));
                                    int Final=P+Ra;
                                    String Rating=String.valueOf(Final);
                                    DonorDatabase.child("Rating").setValue(Rating);

                                    Toast.makeText(context,"Donated Succesfully",Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                        }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return Date.size();
    }
    public class classViewHolder extends RecyclerView.ViewHolder {
        TextView TVrequestitem,TVcompleted;
        Button BTNdonate;

        public classViewHolder(View itemView) {
            super(itemView);


            TVrequestitem =itemView.findViewById(R.id.requestitem);
            TVcompleted =itemView.findViewById(R.id.tvcomplete);
            BTNdonate = itemView.findViewById(R.id.btndonateitem);


        }
    }
}
