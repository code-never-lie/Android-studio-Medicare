package com.example.belli.medicare;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Forum extends Fragment {

    Button BTNcreatePost,BTNpostdone;
    TextView TVtags;
    EditText ETpost,ETtitle;
    AutoCompleteTextView ETtags;
    RecyclerView ForumRecycler;
    DatabaseReference forumref;
    FirebaseAuth auth;
    String userid;
    ArrayList<String> Post=new ArrayList<>();
    ArrayList<String> PostKey=new ArrayList<>();
    ArrayList<String> PostTags=new ArrayList<>();
    ArrayList<String> PostTitle=new ArrayList<>();
    ArrayList<String> PostUID=new ArrayList<>();
    ArrayList<String> Time=new ArrayList<>();
    ArrayList<String> Result=new ArrayList<>();



    public Forum() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_forum,container,false);

        forumref= FirebaseDatabase.getInstance().getReference().child("Forum");

        userid=auth.getInstance().getUid();
        BTNcreatePost=rootView.findViewById(R.id.btncreatepost);
        ForumRecycler=rootView.findViewById(R.id.forumrecycler);
        ForumRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        forumref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post.clear();
                PostKey.clear();
                PostTitle.clear();
                Time.clear();
                PostUID.clear();
                PostTags.clear();
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    PostKey.add(child.getKey().toString());
                    Post.add(child.child("Post").getValue().toString());
                    PostTitle.add(child.child("Title").getValue().toString());
                    PostTags.add(child.child("Tags").getValue().toString());
                    Time.add(child.child("Time").getValue().toString());
                    PostUID.add(child.child("UID").getValue().toString());
                }
                ForumRecycler.setAdapter(new List_of_post(getContext(),Post,PostKey,PostTitle,Time,PostUID,PostTags));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        BTNcreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.postdialogue);

                ETpost=dialog.findViewById(R.id.etpost);
                ETtitle=dialog.findViewById(R.id.ettitle);
                ETtags=dialog.findViewById(R.id.ettag);
                TVtags=dialog.findViewById(R.id.tvtags);
                BTNpostdone=dialog.findViewById(R.id.btnpostdone);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.select_dialog_item,Country.TAGS);
                ETtags.setThreshold(1);
                ETtags.setAdapter(adapter);

                ETtags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Result.add(ETtags.getText().toString());
                        if(!Result.isEmpty()){
                            TVtags.setText(Result.toString());
                        }
                        ETtags.setText("");
                    }
                });


                BTNpostdone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(ETtitle.getText().toString().isEmpty()){
                            ETtitle.setError("Please enter Title");
                        }else if(ETpost.getText().toString().isEmpty()){
                            ETpost.setError("Please Write Something...");
                        }else if(TVtags.getText().toString().isEmpty()) {
                            TVtags.setError("Please Write Atleast 1 TAG...");
                        }else{
                                final Date currentLocalTime = Calendar.getInstance().getTime();
                                DateFormat date=new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
                                String Time = date.format(currentLocalTime);

                                Map post=new HashMap();
                                post.put("Post",ETpost.getText().toString());
                                post.put("Title",ETtitle.getText().toString());
                                post.put("Tags",TVtags.getText().toString());
                                post.put("UID",userid);
                                post.put("Time",Time);
                                forumref.push().updateChildren(post);
                                Toast.makeText(getContext(),"Posted Successfully",Toast.LENGTH_LONG).show();
                                ETpost.setText("");
                                ETtitle.setText("");
                            }

                    }
                });
                dialog.show();
            }
        });

        return rootView;
    }

}
class List_of_post extends RecyclerView.Adapter<List_of_post.classViewHolder> {
    Context context;
    AutoCompleteTextView ETtags;
    TextView TVtags;
    DatabaseReference cmntref,ansref;
    EditText ETcmnt,ETanswer;
    Button BTNcmnt,BTNpanswer,BTNans;
    String userid;
    RecyclerView CmntRecycler,AnswerRecycler;
    ArrayList<String> Post = new ArrayList<>();
    ArrayList<String> PostKey=new ArrayList<>();
    ArrayList<String> PostTags=new ArrayList<>();
    ArrayList<String> PostTitle=new ArrayList<>();
    ArrayList<String> PostUID=new ArrayList<>();
    ArrayList<String> Time=new ArrayList<>();
    ArrayList<String> Comment=new ArrayList<>();
    ArrayList<String> CmntUID=new ArrayList<>();
    ArrayList<String> CmntKey=new ArrayList<>();
    ArrayList<String> Answer=new ArrayList<>();
    ArrayList<String> AnswerUID=new ArrayList<>();
    ArrayList<String> AnswerKey=new ArrayList<>();
    ArrayList<String> UP=new ArrayList<>();
    ArrayList<String> DOWN=new ArrayList<>();


    public List_of_post(Context ctx, ArrayList<String> post,ArrayList<String> postKey,ArrayList<String> postTitle,
                        ArrayList<String> time,ArrayList<String> postUID,ArrayList<String> postTags) {
        this.context = ctx;
        this.Post = post;
        this.PostKey=postKey;
        this.PostTitle=postTitle;
        this.PostUID=postUID;
        this.Time=time;
        this.PostTags=postTags;
    }

    @NonNull
    @Override
    public List_of_post.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_of_post, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List_of_post.classViewHolder holder, final int position) {
        holder.TVpost.setText(Post.get(position)+"\n\n"+"TAGS: "+PostTags.get(position)+"\n\n"+Time.get(position));
        holder.TVtitle.setText(PostTitle.get(position));

        userid=FirebaseAuth.getInstance().getUid();
        if(userid.equals(PostUID.get(position))){
            holder.BTNdown.setEnabled(false);
            holder.BTNup.setEnabled(false);
        }

        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Doctor").hasChild(PostUID.get(position))){
                    holder.TVname.setText("Post By: "+dataSnapshot.child("Doctor").child(PostUID.get(position)).child("Username").getValue().toString());
                }else if(dataSnapshot.child("Donor").hasChild(PostUID.get(position))){
                    holder.TVname.setText("Post By: "+dataSnapshot.child("Donor").child(PostUID.get(position)).child("Username").getValue().toString());
                }else {
                    holder.TVname.setText("Post By: "+dataSnapshot.child("Mstore").child(PostUID.get(position)).child("Username").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.BTNanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.answerdialogue);

                BTNpanswer=dialog.findViewById(R.id.btnpanswer);
                AnswerRecycler = dialog.findViewById(R.id.answerrecycler);
                AnswerRecycler.setLayoutManager(new LinearLayoutManager(context));

                ansref= FirebaseDatabase.getInstance().getReference().child("Forum").child(PostKey.get(position)).child("Answers");
                ansref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Answer.clear();
                        AnswerKey.clear();
                        AnswerUID.clear();
                        for(DataSnapshot child:dataSnapshot.getChildren()){
                            Answer.add(child.child("Answer").getValue().toString());
                            AnswerUID.add(child.child("UID").getValue().toString());
                            AnswerKey.add(child.getKey());
                        }
                        AnswerRecycler.setAdapter(new List_of_answer(context,Answer,AnswerKey,AnswerUID
                                ,PostKey.get(position),PostUID.get(position)));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                BTNpanswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog=new Dialog(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.postdialogue);

                        BTNans=dialog.findViewById(R.id.btnpostdone);
                        ETanswer=dialog.findViewById(R.id.etpost);
                        ETanswer.setHint("Write Your Answer...");
                        ETtags=dialog.findViewById(R.id.ettag);
                        ETtags.setVisibility(View.INVISIBLE);
                        TVtags=dialog.findViewById(R.id.tvtags);
                        TVtags.setVisibility(View.INVISIBLE);
                        dialog.findViewById(R.id.ettitle).setVisibility(View.INVISIBLE);

                        BTNans.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(ETanswer.getText().toString().isEmpty()){
                                    ETanswer.setError("Please Write Answer.");
                                }else{
                                    String userid=FirebaseAuth.getInstance().getUid();
                                    Map answer=new HashMap();
                                    answer.put("Answer",ETanswer.getText().toString());
                                    answer.put("UID",userid);
                                    FirebaseDatabase.getInstance().getReference().child("Forum").
                                            child(PostKey.get(position)).child("Answers").push().updateChildren(answer);
                                    ETanswer.setText("");
                                }
                            }
                        });
                        dialog.show();
                    }
                });
                dialog.show();
            }
        });
        holder.BTNcmnts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.commentsdialogue);

                BTNcmnt=dialog.findViewById(R.id.btncomment);
                ETcmnt = dialog.findViewById(R.id.etcomment);
                CmntRecycler = dialog.findViewById(R.id.cmntrecycler);
                CmntRecycler.setLayoutManager(new LinearLayoutManager(context));

                cmntref= FirebaseDatabase.getInstance().getReference().child("Forum").child(PostKey.get(position)).child("Comments");
                cmntref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Comment.clear();
                        CmntKey.clear();
                        CmntUID.clear();
                        for(DataSnapshot child:dataSnapshot.getChildren()){
                            Comment.add(child.child("Cmnt").getValue().toString());
                            CmntUID.add(child.child("UID").getValue().toString());
                            CmntKey.add(child.getKey());
                        }
                        CmntRecycler.setAdapter(new List_of_comment(context,Comment,CmntKey,CmntUID,
                                PostKey.get(position),PostUID.get(position)));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                BTNcmnt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(ETcmnt.getText().toString().isEmpty()){
                            ETcmnt.setError("Please Write Comment.");
                        }else{
                            String userid=FirebaseAuth.getInstance().getUid();
                            Map cmnt=new HashMap();
                            cmnt.put("Cmnt",ETcmnt.getText().toString());
                            cmnt.put("UID",userid);
                            FirebaseDatabase.getInstance().getReference().child("Forum").
                                    child(PostKey.get(position)).child("Comments").push().updateChildren(cmnt);
                            ETcmnt.setText("");
                        }
                    }
                });
                dialog.show();
            }
        });
        FirebaseDatabase.getInstance().getReference().child("ForumPoints").child(PostKey.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UP.clear();
                DOWN.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getValue().toString().equals("UP")) {
                            UP.add(child.getKey());
                        } else {
                            DOWN.add(child.getKey());
                        }
                    }
                    if (UP.contains(userid)) {
                        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortup);
                        holder.BTNup.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                    } else if (DOWN.contains(userid)) {
                        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortdown);
                        holder.BTNdown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                    }else{
                        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortdownnormal);
                        holder.BTNdown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);

                        Drawable unwrappedDrawable1 = AppCompatResources.getDrawable(context, R.drawable.sortupnormal);
                        holder.BTNup.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable1, null, null, null);
                    }
                    int U=UP.size();
                    int D=DOWN.size()*-1;
                    int Points = U+D;
                    holder.TVrate.setText(String.valueOf(Points));
                    int FinalPoints=U*15+D*5;
                FirebaseDatabase.getInstance().getReference().child("Forum").child(PostKey.get(position)).
                        child("Points").setValue(String.valueOf(FinalPoints));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.BTNup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("ForumPoints").child(PostKey.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UP.clear();
                        DOWN.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (child.getValue().toString().equals("UP")) {
                                UP.add(child.getKey());
                            } else {
                                DOWN.add(child.getKey());
                            }
                        }
                        if (UP.contains(userid)) {
                            FirebaseDatabase.getInstance().getReference().child("ForumPoints").
                                    child(PostKey.get(position)).child(userid).removeValue();
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortupnormal);
                            holder.BTNup.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                        } else if(DOWN.contains(userid)){
                            FirebaseDatabase.getInstance().getReference().child("ForumPoints").
                                    child(PostKey.get(position)).child(userid).setValue("UP");
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortup);
                            holder.BTNup.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);

                            Drawable unwrappedDrawable1 = AppCompatResources.getDrawable(context, R.drawable.sortdownnormal);
                            holder.BTNdown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable1, null, null, null);
                        } else{
                            FirebaseDatabase.getInstance().getReference().child("ForumPoints").
                                    child(PostKey.get(position)).child(userid).setValue("UP");
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortup);
                            holder.BTNup.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.BTNdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("ForumPoints").child(PostKey.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UP.clear();
                        DOWN.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (child.getValue().toString().equals("UP")) {
                                UP.add(child.getKey());
                            } else {
                                DOWN.add(child.getKey());
                            }
                        }
                        if (DOWN.contains(userid)) {
                            FirebaseDatabase.getInstance().getReference().child("ForumPoints").
                                    child(PostKey.get(position)).child(userid).removeValue();
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortdownnormal);
                            holder.BTNdown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                        }else if(UP.contains(userid)){
                            FirebaseDatabase.getInstance().getReference().child("ForumPoints").
                                    child(PostKey.get(position)).child(userid).setValue("DOWN");
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortupnormal);
                            holder.BTNup.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);

                            Drawable unwrappedDrawable1 = AppCompatResources.getDrawable(context, R.drawable.sortdown);
                            holder.BTNdown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable1, null, null, null);
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("ForumPoints").
                                    child(PostKey.get(position)).child(userid).setValue("DOWN");
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortdown);
                            holder.BTNdown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return Post.size();
    }

    public class classViewHolder extends RecyclerView.ViewHolder {
        Button BTNup,BTNdown,BTNcmnts,BTNanswer;
        TextView TVpost,TVtitle,TVrate,TVname;

        public classViewHolder(View itemView) {
            super(itemView);

            BTNup = itemView.findViewById(R.id.btnup);
            BTNdown= itemView.findViewById(R.id.btndown);
            TVpost = itemView.findViewById(R.id.tvpost);
            BTNcmnts = itemView.findViewById(R.id.btncmnt);
            BTNanswer = itemView.findViewById(R.id.btnanswer);
            TVtitle = itemView.findViewById(R.id.tvtitle);
            TVrate = itemView.findViewById(R.id.tvrate);
            TVname = itemView.findViewById(R.id.tvname);
        }
    }
}

class List_of_comment extends RecyclerView.Adapter<List_of_comment.classViewHolder> {
    Context context;
    FirebaseAuth auth;
    Button BTNreply;
    EditText ETreply;
    TextView TVheading;
    RecyclerView ReplyRecycler;
    ArrayList<String> Comment=new ArrayList<>();
    ArrayList<String> CmntUID=new ArrayList<>();
    ArrayList<String> CmntKey=new ArrayList<>();
    ArrayList<String> Reply=new ArrayList<>();
    ArrayList<String> ReplyUID=new ArrayList<>();
    ArrayList<String> ReplyKey=new ArrayList<>();

    String Postkey,PostUID,user_id;


    public List_of_comment(Context ctx, ArrayList<String> comment,ArrayList<String> cmntKey,ArrayList<String> cmntUID
                            ,String postkey,String postUID) {
        this.context = ctx;
        this.Comment = comment;
        this.CmntKey=cmntKey;
        this.CmntUID=cmntUID;
        this.Postkey=postkey;
        this.PostUID=postUID;
    }

    @NonNull
    @Override
    public List_of_comment.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_of_comment, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List_of_comment.classViewHolder holder, final int position) {
        holder.TVcomment.setText(Comment.get(position));
        user_id=auth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Doctor").hasChild(CmntUID.get(position))){
                    holder.TVuname.setText(dataSnapshot.child("Doctor").child(CmntUID.get(position)).child("Username").getValue().toString());
                }else if(dataSnapshot.child("Donor").hasChild(CmntUID.get(position))){
                    holder.TVuname.setText(dataSnapshot.child("Donor").child(CmntUID.get(position)).child("Username").getValue().toString());
                }else {
                    holder.TVuname.setText(dataSnapshot.child("Mstore").child(CmntUID.get(position)).child("Username").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.BTNreplies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.commentsdialogue);

                BTNreply=dialog.findViewById(R.id.btncomment);
                ETreply = dialog.findViewById(R.id.etcomment);
                TVheading = dialog.findViewById(R.id.tvheading);
                ReplyRecycler = dialog.findViewById(R.id.cmntrecycler);
                ReplyRecycler.setLayoutManager(new LinearLayoutManager(context));

                ETreply.setHint("Write a Reply");
                TVheading.setText("Replies");

                final DatabaseReference RepRef=FirebaseDatabase.getInstance().getReference().child("Forum").child(Postkey).child("Comments").
                        child(CmntKey.get(position));
                RepRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ReplyKey.clear();
                        Reply.clear();
                        ReplyUID.clear();
                        for(DataSnapshot child:dataSnapshot.child("Replies").getChildren()){
                            ReplyKey.add(child.getKey());
                            Reply.add(child.child("Reply").getValue().toString());
                            ReplyUID.add(child.child("UID").getValue().toString());
                        }
                        ReplyRecycler.setAdapter(new List_of_reply(context,Reply,ReplyUID,ReplyKey));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                BTNreply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(ETreply.getText().toString().isEmpty()){
                            ETreply.setError("Please Write Something");
                        }else{
                            Map reply=new HashMap();
                            reply.put("Reply",ETreply.getText().toString());
                            reply.put("UID",user_id);
                            RepRef.child("Replies").push().setValue(reply);

                            Toast.makeText(context,"You Replied",Toast.LENGTH_LONG).show();
                            ETreply.setText("");
                        }
                    }
                });
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return Comment.size();
    }

    public class classViewHolder extends RecyclerView.ViewHolder {
        TextView TVcomment,TVuname;
        Button BTNreplies;

        public classViewHolder(View itemView) {
            super(itemView);

            TVcomment= itemView.findViewById(R.id.tvcomments);
            TVuname = itemView.findViewById(R.id.tvcmntuser);
            BTNreplies = itemView.findViewById(R.id.btnreplies);
        }
    }
}

class List_of_reply extends RecyclerView.Adapter<List_of_reply.classViewHolder> {
    Context context;
    ArrayList<String> Reply=new ArrayList<>();
    ArrayList<String> ReplyUID=new ArrayList<>();
    ArrayList<String> ReplyKey=new ArrayList<>();

    String user_id;


    public List_of_reply(Context ctx, ArrayList<String> reply,ArrayList<String> replyUID,ArrayList<String> replyKey) {
        this.context = ctx;
        this.Reply=reply;
        this.ReplyKey=replyKey;
        this.ReplyUID=replyUID;
    }

    @NonNull
    @Override
    public List_of_reply.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_of_reply, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List_of_reply.classViewHolder holder, final int position) {
        holder.TVreply.setText(Reply.get(position));

        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Doctor").hasChild(ReplyUID.get(position))){
                    holder.TVreplyuname.setText(dataSnapshot.child("Doctor").child(ReplyUID.get(position)).child("Username").getValue().toString());
                }else if(dataSnapshot.child("Donor").hasChild(ReplyUID.get(position))){
                    holder.TVreplyuname.setText(dataSnapshot.child("Donor").child(ReplyUID.get(position)).child("Username").getValue().toString());
                }else {
                    holder.TVreplyuname.setText(dataSnapshot.child("Mstore").child(ReplyUID.get(position)).child("Username").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return Reply.size();
    }

    public class classViewHolder extends RecyclerView.ViewHolder {

        TextView TVreply,TVreplyuname;

        public classViewHolder(View itemView) {
            super(itemView);

            TVreply= itemView.findViewById(R.id.tvreply);
            TVreplyuname = itemView.findViewById(R.id.tvreplyuser);
        }
    }
}

class List_of_answer extends RecyclerView.Adapter<List_of_answer.classViewHolder> {
    Context context;
    ArrayList<String> Answer=new ArrayList<>();
    ArrayList<String> AnswerUID=new ArrayList<>();
    ArrayList<String> AnswerKey=new ArrayList<>();
    ArrayList<String> UP=new ArrayList<>();
    ArrayList<String> DOWN=new ArrayList<>();
    FirebaseAuth auth;
    String userid,Type,PostKey,PostUID,AnsCorrect,Verify="TRIAL";


    public List_of_answer(Context ctx, ArrayList<String> answer,ArrayList<String> answerKey,ArrayList<String> answerUID,
                          String postKey,String postUID) {
        this.context = ctx;
        this.Answer=answer;
        this.AnswerKey=answerKey;
        this.AnswerUID=answerUID;
        this.PostKey=postKey;
        this.PostUID=postUID;
    }

    @NonNull
    @Override
    public List_of_answer.classViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_of_answer, parent, false);
        return new classViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final List_of_answer.classViewHolder holder, final int position) {
        holder.TVanswer.setText(Answer.get(position));
        userid=auth.getInstance().getUid();

        if(userid.equals(PostUID)){
            holder.BTNtick.setEnabled(true);
        }
        if(userid.equals(AnswerUID.get(position))){
            holder.BTNansDown.setEnabled(false);
            holder.BTNansUP.setEnabled(false);
        }
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Doctor").hasChild(AnswerUID.get(position))){
                    holder.TVansuname.setText("Answer By: "+dataSnapshot.child("Doctor").child(AnswerUID.get(position)).child("Username").getValue().toString());
                }else if(dataSnapshot.child("Donor").hasChild(AnswerUID.get(position))){
                    holder.TVansuname.setText("Answer By: "+dataSnapshot.child("Donor").child(AnswerUID.get(position)).child("Username").getValue().toString());
                }else {
                    holder.TVansuname.setText("Answer By: "+dataSnapshot.child("Mstore").child(AnswerUID.get(position)).child("Username").getValue().toString());
                }
                if(dataSnapshot.child("Doctor").hasChild(userid)){
                    Type="Doctor";
                }else if(dataSnapshot.child("Donor").hasChild(userid)){
                    Type="Donor";
                }else {
                    Type="Mstore";
                }
                if(dataSnapshot.child("Doctor").hasChild(AnswerUID.get(position))){
                    holder.BTNverify.setText("Verified");
                    holder.BTNverify.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }
                final DatabaseReference VerifyRef=FirebaseDatabase.getInstance().getReference().child("Forum").child(PostKey)
                        .child("Answers").child(AnswerKey.get(position));
                VerifyRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child("Verify").exists()){
                                    Verify=dataSnapshot.child("Verify").getValue().toString();
                                    if(Verify.equals("Yes")){
                                        holder.BTNverify.setText("Verified");
                                        holder.BTNverify.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                    }else{
                                        holder.BTNverify.setText("Verify?");
                                        holder.BTNverify.setTextColor(Color.RED);
                                    }
                                }
                                holder.BTNverify.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(Type.equals("Doctor")){
                                            if(Verify.equals("Yes")){
                                                VerifyRef.child("Verify").setValue("No");
                                            }else{
                                                VerifyRef.child("Verify").setValue("Yes");
                                            }
                                        }else{
                                            Toast.makeText(context,"Sorrey.! Only Doctor/Nurses can verify answers",Toast.LENGTH_LONG).show();
                                        }
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
        final DatabaseReference Tickref=FirebaseDatabase.getInstance().getReference().child("Forum").child(PostKey);
        Tickref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AnsCorrect="NO";
                if(dataSnapshot.child("Correct").exists()){
                    AnsCorrect=dataSnapshot.child("Correct").getValue().toString();
                }
                if(AnsCorrect.equals(AnswerKey.get(position))){
                    holder.BTNtick.setBackgroundResource(R.drawable.tickgreen);
                }else{
                    holder.BTNtick.setBackgroundResource(R.drawable.tick);
                }
                holder.BTNtick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(AnsCorrect.equals(AnswerKey.get(position))){
                            Tickref.child("Correct").removeValue();
                        }else{
                            Tickref.child("Correct").setValue(AnswerKey.get(position));
                        }
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("AnsPoints").child(AnswerKey.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UP.clear();
                DOWN.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getValue().toString().equals("UP")) {
                        UP.add(child.getKey());
                    } else {
                        DOWN.add(child.getKey());
                    }
                }
                if (UP.contains(userid)){
                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortup);
                    holder.BTNansUP.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                } else if (DOWN.contains(userid)) {
                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortdown);
                    holder.BTNansDown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                }
                int U=UP.size();
                int D=DOWN.size()*-1;
                int Points = U+D;
                holder.TVansrate.setText(String.valueOf(Points));
                int FinalPoints=U*15+D*5;
                FirebaseDatabase.getInstance().getReference().child("Forum").child(PostKey).child("Answers").child(AnswerKey.get(position)).
                        child("Points").setValue(String.valueOf(FinalPoints));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.BTNansUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("AnsPoints").child(AnswerKey.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UP.clear();
                        DOWN.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (child.getValue().toString().equals("UP")) {
                                UP.add(child.getKey());
                            } else {
                                DOWN.add(child.getKey());
                            }
                        }
                        if (UP.contains(userid)) {
                            FirebaseDatabase.getInstance().getReference().child("AnsPoints").
                                    child(AnswerKey.get(position)).child(userid).removeValue();
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortupnormal);
                            holder.BTNansUP.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                        } else if(DOWN.contains(userid)){
                            FirebaseDatabase.getInstance().getReference().child("AnsPoints").
                                    child(AnswerKey.get(position)).child(userid).setValue("UP");
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortup);
                            holder.BTNansUP.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);

                            Drawable unwrappedDrawable1 = AppCompatResources.getDrawable(context, R.drawable.sortdownnormal);
                            holder.BTNansDown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable1, null, null, null);
                        } else{
                            FirebaseDatabase.getInstance().getReference().child("AnsPoints").
                                    child(AnswerKey.get(position)).child(userid).setValue("UP");
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortup);
                            holder.BTNansUP.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.BTNansDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("AnsPoints").child(AnswerKey.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UP.clear();
                        DOWN.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (child.getValue().toString().equals("UP")) {
                                UP.add(child.getKey());
                            } else {
                                DOWN.add(child.getKey());
                            }
                        }
                        if (DOWN.contains(userid)) {
                            FirebaseDatabase.getInstance().getReference().child("AnsPoints").
                                    child(AnswerKey.get(position)).child(userid).removeValue();
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortdownnormal);
                            holder.BTNansDown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                        }else if(UP.contains(userid)){
                            FirebaseDatabase.getInstance().getReference().child("AnsPoints").
                                    child(AnswerKey.get(position)).child(userid).setValue("DOWN");
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortupnormal);
                            holder.BTNansUP.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);

                            Drawable unwrappedDrawable1 = AppCompatResources.getDrawable(context, R.drawable.sortdown);
                            holder.BTNansDown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable1, null, null, null);
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("AnsPoints").
                                    child(AnswerKey.get(position)).child(userid).setValue("DOWN");
                            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.sortdown);
                            holder.BTNansDown.setCompoundDrawablesWithIntrinsicBounds(unwrappedDrawable, null, null, null);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return Answer.size();
    }

    public class classViewHolder extends RecyclerView.ViewHolder {
        Button BTNansUP,BTNansDown,BTNtick,BTNverify;
        TextView TVanswer,TVansrate,TVansuname;

        public classViewHolder(View itemView) {
            super(itemView);

            TVanswer= itemView.findViewById(R.id.tvans);
            TVansrate= itemView.findViewById(R.id.tvansrate);
            TVansuname = itemView.findViewById(R.id.tvansname);
            BTNansDown = itemView.findViewById(R.id.btnansdown);
            BTNansUP= itemView.findViewById(R.id.btnansup);
            BTNtick = itemView.findViewById(R.id.btntick);
            BTNverify = itemView.findViewById(R.id.btnansverify);

        }
    }
}
