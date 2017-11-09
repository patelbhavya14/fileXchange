package com.example.bhavya.filexchange;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageListAdapter extends ArrayAdapter<FileUpload> {

    private Context context;
    private int resource;
    private List<FileUpload> listImage;

    public ImageListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<FileUpload> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        listImage = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(resource, null);
        TextView tvName = (TextView) v.findViewById(R.id.title);
        ImageView img = (ImageView) v.findViewById(R.id.imageView);

        tvName.setText(listImage.get(position).getTag());
        Glide.with(context).load(listImage.get(position).getUrl()).into(img);

        return v;

    }

}



