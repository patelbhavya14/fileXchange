package com.example.bhavya.filexchange;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhaVYa on 13/09/17.
 */

public class shareAdapter extends ArrayAdapter<fileItems> {
    private final Context context;
    String url, name, tagg, type, keyy;
    private List<fileItems> itemsArrayList;
    CardView cv;
    String b;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    ImageView imgv;
    String clickValue1,toSend;
    private ProgressDialog mProgressDialog;
    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "enc");
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
        final ImageView option = (ImageView) rowView.findViewById(R.id.img);


        final String d = itemsArrayList.get(position).getTitle();
        System.out.println("D="+d);

        String day = d.substring(6,8)+"/"+d.substring(4,6)+"/"+d.substring(0,4);

        String time = d.substring(9,11)+":"+d.substring(11,13)+":"+d.substring(13,15);

        tag.setText(itemsArrayList.get(position).getTag()+"."+itemsArrayList.get(position).getType());

        type = itemsArrayList.get(position).getType();

        if(itemsArrayList.get(position).getType().equals("pdf")) {
            img.setImageResource(R.drawable.pdf);
        }
        else if(type.equals("jpg")||type.equals("png")) {
            img.setImageResource(R.drawable.img);
        }
        else if(type.equals("txt")) {
            img.setImageResource(R.drawable.txt);
        }

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = itemsArrayList.get(position).getUrl();
                name = itemsArrayList.get(position).getName();
                tagg = itemsArrayList.get(position).getTag();
                type = itemsArrayList.get(position).getType();
                keyy = itemsArrayList.get(position).getKey();

                LayoutInflater factory = LayoutInflater.from(getContext());

                final View cView = factory.inflate(R.layout.custom_view, null);
                imgv = (ImageView) cView.findViewById(R.id.img);
                if(itemsArrayList.get(position).getType().equals("pdf")) {
                    imgv.setImageResource(R.drawable.pdf);
                }
                else if(type.equals("jpg")||type.equals("png")) {
                    imgv.setImageResource(R.drawable.img);
                }
                else if(type.equals("txt")) {
                    imgv.setImageResource(R.drawable.txt);
                }

                final TextView tv = (TextView) cView.findViewById(R.id.title);
                final View bView = factory.inflate(R.layout.custom_view_email,null);
                final View dView = factory.inflate(R.layout.password,null);
                final EditText key = (EditText) dView.findViewById(R.id.password);

                tv.setText(tagg+"."+type);

                List<BottomItems> btm = new ArrayList<>();
                final List<String> items = new ArrayList<String>();
                items.add("View File");
                items.add("Send Copy");
                items.add("Remove");

                btm.add(new BottomItems("View File", R.drawable.show));
                btm.add(new BottomItems("Send Copy", R.drawable.forward));
                btm.add(new BottomItems("Remove", R.drawable.remove));

                bottomAdapter adapter = new bottomAdapter(getContext(),btm);
                final ListView lv = (ListView) cView.findViewById(R.id.listView);
                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                        String clickValue =(String) items.get(position);
                        Toast.makeText(getContext(),clickValue,Toast.LENGTH_SHORT).show();

                        //nf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name+"_"+tagg+"."+type);
                        if(clickValue.equals("View File")) {
                            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getContext());

                            alertDialog2.setView(dView);
                            alertDialog2.setPositiveButton("Unlock",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(key.getText().toString().equals(keyy)) {
                                                startDownload();
                                                Toast.makeText(getContext(), "File Decrypted", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            alertDialog2.setTitle("Unlock File");
                            alertDialog2.show();
                        }

                        if(clickValue.equals("Remove")) {
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
                    }
                });

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(cView);
                bottomSheetDialog.show();

            }
        });


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

    private void startDownload() {
        new DownloadFileAsync().execute(url);
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Downloading file..");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {

                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(f);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {}
            return null;

        }
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            Encryption encryptFile = new Encryption(keyy, name, tagg+"."+type);
            encryptFile.decrypt();
            boolean delete = f.delete();
            mProgressDialog.dismiss();
        }
    }


}
