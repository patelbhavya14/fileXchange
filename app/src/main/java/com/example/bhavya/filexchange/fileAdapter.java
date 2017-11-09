package com.example.bhavya.filexchange;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bhaVYa on 13/09/17.
 */

public class fileAdapter extends ArrayAdapter<fileItems> {
    private final Context context;
    private List<fileItems> itemsArrayList;
    CardView cv;
    String b;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    String clickValue1,toSend;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    AlertDialog.Builder alertDialog;

    public fileAdapter(Context context, List<fileItems> itemsArrayList) {
        super(context, R.layout.cardview, itemsArrayList);

        this.context = context;
        this.itemsArrayList =  itemsArrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.cardview, parent, false);

        cv = (CardView) rowView.findViewById(R.id.card_view);
        final TextView tag = (TextView) rowView.findViewById(R.id.title);
        final TextView date = (TextView) rowView.findViewById(R.id.date);
        final ImageView img = (ImageView) rowView.findViewById(R.id.imageView);
        final ImageView imgO = (ImageView) rowView.findViewById(R.id.imageView2);

        imgO.setImageResource(R.drawable.option);

        final String d = itemsArrayList.get(position).getTitle();
        System.out.println("D="+d);

        String day = d.substring(6,8)+"/"+d.substring(4,6)+"/"+d.substring(0,4);

        String time = d.substring(9,11)+":"+d.substring(11,13)+":"+d.substring(13,15);

        tag.setText(itemsArrayList.get(position).getTag()+"."+itemsArrayList.get(position).getType());

        String type = itemsArrayList.get(position).getType();

        if(itemsArrayList.get(position).getType().equals("pdf")) {
            img.setImageResource(R.drawable.pdf_icon);
        }
        else if(type.equals("jpg")||type.equals("png")) {
            img.setImageResource(R.drawable.image);
        }



        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final String url = itemsArrayList.get(position).getUrl();
                final String name = itemsArrayList.get(position).getName();
                final String tag = itemsArrayList.get(position).getTag();
                final String type = itemsArrayList.get(position).getType();

                LayoutInflater factory = LayoutInflater.from(getContext());

                final View cView = factory.inflate(R.layout.custom_view, null);
                final View bView = factory.inflate(R.layout.custom_view_email,null);
                final List<String> items = new ArrayList<String>();
                items.add("View");
                items.add("Share");
                items.add("Delete");

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
                final ListView lv = (ListView) cView.findViewById(R.id.listView);
                lv.setAdapter(adapter);

                final AdapterView.OnItemClickListener click = new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        String clickValue =(String) (lv.getItemAtPosition(position));
                        Toast.makeText(getContext(),clickValue,Toast.LENGTH_SHORT).show();

                        if(clickValue.equals("View")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            getContext().startActivity(intent);
                        }

                        if(clickValue.equals("Delete")) {
                            mAuth = FirebaseAuth.getInstance();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String nd = itemsArrayList.get(position).getTitle();
                            final DatabaseReference mDelete = database.getReference("Files/"+user.getUid()+"/Uploaded/"+nd);

                            mDelete.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot ds) {
                                    mDelete.removeValue();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        if(clickValue.equals("Share")) {
                            mAuth = FirebaseAuth.getInstance();
                            final List<String> emails = new ArrayList<String>();
                            final List<String> uids = new ArrayList<String>();
                            final ListView lv1 = (ListView) bView.findViewById(R.id.listView);

                            DatabaseReference mUsers = database.getReference("Users");

                            mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dsp) {
                                    for (DataSnapshot ds1: dsp.getChildren()) {
                                        String email = ds1.child("email").getValue().toString();
                                        System.out.println("EMAIL="+email);
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if(!user.getEmail().equals(email)) {
                                            emails.add(email);
                                            uids.add(ds1.getKey());
                                        }
                                    }
                                    ArrayAdapter<String> adapterEmail = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, emails);
                                    lv1.setAdapter(adapterEmail);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            final AdapterView.OnItemClickListener click = new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView parent, View v, int position, long id) {
                                    clickValue1 =(String) (lv1.getItemAtPosition(position));
                                    toSend = uids.get(position);
                                    Toast.makeText(getContext(),uids.get(position),Toast.LENGTH_SHORT).show();
                                }
                            };
                            lv1.setOnItemClickListener(click);
                            final FirebaseUser user = mAuth.getCurrentUser();
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(getContext());

                            alertDialog1.setView(bView);
                            alertDialog1.setPositiveButton("SEND",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            mDatabase.child("Files").child(user.getUid()).child("Shared With").child(toSend).child(name).child("tag").setValue(tag);
                                            mDatabase.child("Files").child(user.getUid()).child("Shared With").child(toSend).child(name).child("type").setValue(type);
                                            mDatabase.child("Files").child(user.getUid()).child("Shared With").child(toSend).child(name).child("url").setValue(url);
                                            mDatabase.child("Files").child(toSend).child("Shared By").child(user.getUid()).child(name).child("tag").setValue(tag);
                                            mDatabase.child("Files").child(toSend).child("Shared By").child(user.getUid()).child(name).child("type").setValue(type);
                                            mDatabase.child("Files").child(toSend).child("Shared By").child(user.getUid()).child(name).child("url").setValue(url);

                                        }
                                    });
                            alertDialog1.setTitle("Users");
                            alertDialog1.show();
                        }

                    }
                };
                lv.setOnItemClickListener(click);

                alertDialog = new AlertDialog.Builder(getContext());

                alertDialog.setView(cView);

                alertDialog.show();

            }
        });
        date.setText(day+" "+time);

        return rowView;
    }
}