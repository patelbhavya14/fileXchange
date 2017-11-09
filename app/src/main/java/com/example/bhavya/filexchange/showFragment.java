package com.example.bhavya.filexchange;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;


public class showFragment extends Fragment {
    View rootView;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ArrayList<ImageRetrieve> images = new ArrayList<>();
    ListView lv;
    private ProgressDialog progressDialog;
    private List<FileUpload> imgList;
    Bitmap bmp;
    ImageView imgv;
    String tag;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_show, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        imgList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait loading list image...");
        progressDialog.show();

        if(user != null) {
            final DatabaseReference mCust = database.getReference("Images/"+user.getUid()+"/Uploaded");
            mCust.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {
                    imgList.clear();
                    if(ds.exists()) {
                        for (DataSnapshot ds1: ds.getChildren()) {
                            FileUpload img = ds1.getValue(FileUpload.class);
                            imgList.add(img);
                        }

                        progressDialog.dismiss();
                        lv = (ListView) rootView.findViewById(R.id.listView);
                        ImageListAdapter adapter = new ImageListAdapter(getContext(), R.layout.cardview, imgList);
                        lv.setAdapter(adapter);
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
