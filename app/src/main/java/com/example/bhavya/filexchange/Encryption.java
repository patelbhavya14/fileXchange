package com.example.bhavya.filexchange;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by bhaVYa on 13/11/17.
 */

class Encryption {
    KeyGenerator keyGenerator = null;
    SecretKey secretKey = null;
    Cipher cipher = null;
    String masterPassword = null, userID, fileName;
    private StorageReference storageReference;


    public Encryption(String masterPassword, String userID, String fileName) {
        this.masterPassword = masterPassword;
        this.userID = userID;
        this.fileName = fileName;
        try {
            keyGenerator = KeyGenerator.getInstance("Blowfish");
            secretKey = new SecretKeySpec(masterPassword.getBytes(), "Blowfish");
            cipher = Cipher.getInstance("Blowfish");
        } catch (NoSuchPaddingException ex) {
            System.out.println(ex);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex);
        }
    }

    void encrypt(String srcPath, String fname) {
        File f = null;
        if(srcPath.equals("Download")) {
            f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            System.out.println("IN ENCRYPTION:"+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        }
        
        File rawFile = new File(f, fname);
        File encryptedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "enc");
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            inStream = new FileInputStream(rawFile);
            outStream = new FileOutputStream(encryptedFile);
            byte[] buffer = new byte[1024];

            System.out.println("SEE HERE");

            int len;
            while ((len = inStream.read(buffer)) > 0) {
                outStream.write(cipher.update(buffer, 0, len));
                System.out.println("HERE=="+cipher.update(buffer, 0, len));
                outStream.flush();
            }
            outStream.write(cipher.doFinal());
            inStream.close();
            outStream.close();
            uploadFragment sf = new uploadFragment();
            sf.send(encryptedFile);

        } catch (IllegalBlockSizeException ex) {
            System.out.println("Invalid key");
            System.exit(0);
        } catch (BadPaddingException ex) {
            System.out.println("Invalid key");
            System.exit(0);
        } catch (InvalidKeyException ex) {
            System.out.println("Invalid key");
            System.exit(0);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            System.exit(0);
        } catch (IOException ex) {
            System.out.println(ex);
            System.exit(0);
        }
    }

    void decrypt() {
        File encryptedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "enc");
        File decryptedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), userID+"_"+fileName);
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            /**
             * Initialize the cipher for decryption
             */
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            /**
             * Initialize input and output streams
             */
            inStream = new FileInputStream(encryptedFile);
            outStream = new FileOutputStream(decryptedFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) > 0) {
                outStream.write(cipher.update(buffer, 0, len));
                outStream.flush();
            }
            outStream.write(cipher.doFinal());
            inStream.close();
            outStream.close();
        } catch (IllegalBlockSizeException ex) {
            System.out.println(ex);
        } catch (BadPaddingException ex) {
            System.out.println(ex);
        } catch (InvalidKeyException ex) {
            System.out.println(ex);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
