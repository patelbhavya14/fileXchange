package com.example.bhavya.filexchange;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static android.app.Activity.RESULT_OK;
import static junit.framework.Assert.assertEquals;

public class uploadFragment extends Fragment {
    View rootView;
    Button upload,choose;
    String Fpath;
    ImageView img;
    String type;
    final int PICK_IMAGE_REQUEST = 234;
    Uri filePath, encFile;
    File encfile;
    private StorageReference storageReference;
    StorageReference fileRef, fileEncRef;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    KeyGenerator keyGenerator = null;
    SecretKey secretKey = null;
    Cipher cipher = null;
    String masterPassword = null;
    String currentDateandTime;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText tag, key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_upload, container, false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        choose = (Button) rootView.findViewById(R.id.choose);
        upload = (Button) rootView.findViewById(R.id.upload);
        tag = (EditText) rootView.findViewById(R.id.tag);
        key = (EditText) rootView.findViewById(R.id.key);

        masterPassword = key.getText().toString();

        try {
            keyGenerator = KeyGenerator.getInstance("Blowfish");
            secretKey = new SecretKeySpec(masterPassword.getBytes(), "Blowfish");
            cipher = Cipher.getInstance("Blowfish");
        } catch (NoSuchPaddingException ex) {
            System.out.println(ex);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex);
        }


        img = (ImageView) rootView.findViewById(R.id.imageView);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
        return rootView;
    }

    private void uploadFile() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            if(user != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                final String currentDateandTime = sdf.format(new Date());

                final StorageReference riversRef = storageReference.child(user.getUid()+"/"+currentDateandTime+"_"+tag.getText().toString());

                Fpath = filePath.getLastPathSegment();
                String NewString = Fpath.replaceAll("primary:", "").trim();
                String[] words = NewString.split("/");

                String path = words[0];
                String name = words[1];

                System.out.println("NOWWWW=="+NewString);

                Encryption encryptFile = new Encryption(key.getText().toString(), user.getUid(), currentDateandTime+"_"+tag.getText().toString());
                System.out.println("Starting Encryption...");
                encryptFile.encrypt(path, name);

                final File tf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"enc");

                riversRef.putFile(Uri.fromFile(tf))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ContentResolver cR = getContext().getContentResolver();
                                MimeTypeMap mime = MimeTypeMap.getSingleton();
                                type = mime.getExtensionFromMimeType(cR.getType(filePath));

                                FileUpload im = new FileUpload(tag.getText().toString(),taskSnapshot.getDownloadUrl().toString(),type,key.getText().toString());
                                mDatabase.child("Files").child(user.getUid()).child("Uploaded").child(currentDateandTime).setValue(im);

                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                                boolean deleted = tf.delete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        });
            }
        }
        else {

        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select File"),PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() !=null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),filePath);
                img.setImageBitmap(bitmap);
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(File encryptedFile) {
        encfile = encryptedFile;
    }
}
