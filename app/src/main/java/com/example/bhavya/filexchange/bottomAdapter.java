package com.example.bhavya.filexchange;

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.DialogInterface;
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

public class bottomAdapter extends ArrayAdapter<BottomItems> {
    private final Context context;
    String url, name, tagg, type, keyy;
    private List<BottomItems> itemsArrayList;
    CardView cv;
    String b;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    String clickValue1,toSend;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "enc");

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;

    AlertDialog.Builder alertDialog;

    public bottomAdapter(Context context, List<BottomItems> itemsArrayList) {
        super(context, R.layout.bottom_list, itemsArrayList);

        this.context = context;
        this.itemsArrayList =  itemsArrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.bottom_list, parent, false);

        final TextView date = (TextView) rowView.findViewById(R.id.tv);
        final ImageView img = (ImageView) rowView.findViewById(R.id.img);

        date.setText(itemsArrayList.get(position).getS());
        img.setImageResource(itemsArrayList.get(position).getForward());
        return rowView;
    }

}