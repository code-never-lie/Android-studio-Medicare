package com.example.belli.medicare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by FaRoO on 19-Jul-19.
 */

public class List_of_Stock extends RecyclerView.Adapter<List_of_Stock.classViewHolder> {
    Context context;
    ArrayList<String>Mediname=new ArrayList<>();
    ArrayList<String>Mediamount=new ArrayList<>();

    public List_of_Stock(Context ctx,ArrayList<String> mediamount,ArrayList<String> mediname) {
        this.context=ctx;
        this.Mediamount=mediamount;
        this.Mediname=mediname;
    }
    @NonNull
    @Override
    public List_of_Stock.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_of_stock, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List_of_Stock.classViewHolder holder, int position) {
        holder.TVmediname.setText(Mediname.get(position));
        holder.TVmediamount.setText(Mediamount.get(position));
    }

    @Override
    public int getItemCount() {
        return Mediname.size();
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
