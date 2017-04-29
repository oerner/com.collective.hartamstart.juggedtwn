package com.collective.hartamstart.juggedtwn;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import com.google.api.services.drive.model.*;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import pub.devrel.easypermissions.EasyPermissions;

import static com.collective.hartamstart.juggedtwn.R.id.passwortFeld;

public class HoleProtokolle extends Activity
        implements EasyPermissions.PermissionCallbacks {

    ProgressDialog mProgress;

    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    private String passwort;

    private static final String[] SCOPES = { DriveScopes.DRIVE_READONLY };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        passwort = getIntent().getStringExtra("pw");

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Bitte warten ...");

        testeUmgebung();
    }

    private void testeUmgebung() {
        mProgress.show();
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (! isDeviceOnline()) {
            Intent i = new Intent();
            i.putExtra("fehler", "Kein Internet!");
            setResult(RESULT_CANCELED, i);

            mProgress.hide();
            //finish();
        } else {
            new HoleProtokolle.MakeRequestTask().execute();
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Intent i = new Intent();
                    i.putExtra("fehler", "Installiere Google Services!");
                    setResult(RESULT_CANCELED, i);
                    //mProgress.hide();
                } else {
                    testeUmgebung();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    testeUmgebung();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                HoleProtokolle.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private class MakeRequestTask extends  AsyncTask<Void, Void, ArrayList<String>>{
        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;

        MakeRequestTask() {

            HttpTransport httpTransport = new NetHttpTransport();
            JacksonFactory jsonFactory = new JacksonFactory();

            try {
                AssetManager assManager = getApplicationContext().getAssets();
                AssetFileDescriptor fileDescriptor = assManager.openFd("drivelogin.json.encrypted");
                FileInputStream fis = fileDescriptor.createInputStream();

                SecretKeySpec sks = new SecretKeySpec(passwort.getBytes(), "AES");
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, sks);
                //CipherInputStream cis = new CipherInputStream(fis, cipher);

                InputStream privateJsonStream = new CipherInputStream(fis, cipher);

                GoogleCredential serviceAccountCredential =
                        new GoogleCredential().fromStream(privateJsonStream).createScoped(Arrays.asList(SCOPES));

                mService = new Drive.Builder(httpTransport, jsonFactory, null)
                        .setHttpRequestInitializer(serviceAccountCredential)
                        .build();
            }
            catch (Exception e)
            {
                mProgress.setMessage(e.getMessage());
            }
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            try {
                Intent i = new Intent();
                ArrayList<String> namenListeduHuan = new ArrayList<>();
                namenListeduHuan = holeNamen();
                ArrayList<String> idListeDuHuan = new ArrayList<>();
                idListeDuHuan = holeIds();
                String kot = "gna";
                if(idListeDuHuan != null && namenListeduHuan != null) {
                    try {
                        i.putStringArrayListExtra("idliste", idListeDuHuan);
                        i.putStringArrayListExtra("namenliste", namenListeduHuan);
                        i.putExtra("wau", kot);
                        setResult(RESULT_OK, i);
                    }
                    catch (NullPointerException e)
                    {

                    }
                }
                else
                {
                    i.putExtra("fehler", "arraylists sind leer");
                    setResult(RESULT_CANCELED, i);
                }
                //mProgress.hide();
                finish();
                return holeIds();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                finish();
                return null;
            }
        }

        public ArrayList<String> holeIds()
        {
            ArrayList<String> namen = new ArrayList<String>();
            ArrayList<String> ids = new ArrayList<String>();
            try {
                FileList result = mService.files().list()
                        .setQ("'0B091eVDb0xXGbmE4YU13RmFfYzg' in parents")
                        .setPageSize(10)
                        .setFields("nextPageToken, files(id, name)")
                        .execute();
                List<File> files = result.getFiles();
                if (files != null) {
                    for (File file : files) {
                        namen.add(file.getName());
                        ids.add(file.getId());
                    }
                }
            }
            catch (IOException e)
            {

            }
            return ids;
        }

        public ArrayList<String> holeNamen()
        {
            ArrayList<String> namen = new ArrayList<String>();
            ArrayList<String> ids = new ArrayList<String>();
            try {
                FileList result = mService.files().list()
                        .setQ("'0B091eVDb0xXGbmE4YU13RmFfYzg' in parents")
                        .setPageSize(10)
                        .setFields("nextPageToken, files(id, name)")
                        .execute();
                List<File> files = result.getFiles();
                if (files != null) {
                    for (File file : files) {
                        namen.add(file.getName());
                        ids.add(file.getId());
                    }
                }
            }
            catch (IOException e)
            {
            }
            return namen;
        }

        @Override
        protected void onPostExecute(ArrayList<String> output) {
            finish();
        }
    }
}
