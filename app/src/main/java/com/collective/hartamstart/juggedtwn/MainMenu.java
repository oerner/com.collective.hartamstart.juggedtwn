package com.collective.hartamstart.juggedtwn;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class MainMenu extends AppCompatActivity {

    private Button okButton;
    private EditText passwortFeld;

    private boolean richtigesPw = false;

    private File storageDir;

    static final int USER = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Nav Bar Hintergrund Farbe setzen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        okButton = (Button) findViewById(R.id.okButton);

        passwortFeld = (EditText) findViewById(R.id.passwortFeld);

        final Context context = this;

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    decrypt();
                    /*
                    ProgressDialog mProgress = new ProgressDialog(context);
                    mProgress.setMessage("laaft");
                    mProgress.show();
                    */
                }
                catch (Exception e)
                {
                    richtigesPw = false;
                    ProgressDialog mProgress = new ProgressDialog(context);
                    mProgress.setMessage("Falsches Passwort!");
                    mProgress.show();
                }
                hauptMenue();
            }
        });
    }


    public void encrypt() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        AssetManager assManager = getApplicationContext().getAssets();
        AssetFileDescriptor fileDescriptor = assManager.openFd("drivelogin.json.encrypted");
        FileInputStream fis = fileDescriptor.createInputStream();

        File outputFile = File.createTempFile(
                "encrypted.json",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        FileOutputStream fos = new FileOutputStream(outputFile);

        // Length is 16 byte
        // Careful when taking user input!!! http://stackoverflow.com/a/3452620/1188357
        SecretKeySpec sks = new SecretKeySpec("".getBytes(), "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
        //decrypt();
    }

    public void decrypt() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        AssetManager assManager = getApplicationContext().getAssets();
        AssetFileDescriptor fileDescriptor = assManager.openFd("drivelogin.json.encrypted");
        FileInputStream fis = fileDescriptor.createInputStream();

        File jsonFile = File.createTempFile(
                "drive",  /* prefix */
                ".json",         /* suffix */
                storageDir      /* directory */
        );

        FileOutputStream fos = new FileOutputStream(jsonFile);
        SecretKeySpec sks = new SecretKeySpec(passwortFeld.getText().toString().getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
        richtigesPw = true;
        boolean delete = jsonFile.delete();
    }

    public void hauptMenue()
    {
        if(richtigesPw)
        {
            Intent intent = new Intent(this, Menue.class);
            intent.putExtra("pw", passwortFeld.getText().toString());
            intent.putExtra("user", USER);
            startActivity(intent);
            finish();
        }
    }

    //Wahrscheinlich unnötig
    static {
        System.loadLibrary("native-lib");
    }
}
