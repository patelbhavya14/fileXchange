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

public class shareAdapter extends ArrayAdapter<fileItems> {
    private final Context context;
    private List<fileItems> itemsArrayList;
    CardView cv;
    String b;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    String clickValue1,toSend;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    AlertDialog.Builder alertDialog;

    public shareAdapter(Context context, List<fileItems> itemsArrayList) {
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(intent);
            }
        });
        date.setText(day+" "+time);

        return rowView;


    }


}
