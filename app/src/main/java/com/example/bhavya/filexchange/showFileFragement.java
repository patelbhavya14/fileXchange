package com.example.bhavya.filexchange;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class showFileFragement extends Fragment {
    View rootView;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ArrayList<ImageRetrieve> images = new ArrayList<>();
    ListView lv;
    private ProgressDialog progressDialog;
    private List<fileItems> imgList;
    Bitmap bmp;
    ImageView imgv;
    String tag;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_show_file_fragement, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        imgList = new ArrayList<>();

        if(user != null) {
            final DatabaseReference mCust = database.getReference("Files/"+user.getUid()+"/Uploaded");
            mCust.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot ds) {
                    if(ds.exists()) {
                        for (DataSnapshot ds1: ds.getChildren()) {
                            String name = ds1.getKey();
                            String a = ds1.child("tag").getValue().toString();
                            String b = ds1.child("type").getValue().toString();
                            String c = ds1.child("url").getValue().toString();
                            imgList.add(new fileItems(name,ds1.getKey().toString(),a,b,c));
                            Collections.reverse(imgList);
                            fileAdapter adapter = new fileAdapter(getContext(),imgList);
                            lv = (ListView) rootView.findViewById(R.id.listView);
                            lv.setAdapter(adapter);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }


        return rootView;
    }

}
