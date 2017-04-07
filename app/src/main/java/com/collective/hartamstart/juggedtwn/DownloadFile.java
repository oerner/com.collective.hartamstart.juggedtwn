package com.collective.hartamstart.juggedtwn;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class DownloadFile extends Activity
        implements EasyPermissions.PermissionCallbacks {

    ProgressDialog mProgress;

    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    private static final String[] SCOPES = { DriveScopes.DRIVE };

    private java.io.File pfad;

    private String id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent i = getIntent();

        id = i.getStringExtra("fileId");

        pfad = (java.io.File)getIntent().getExtras().get("pfad");

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Herunterladen...");

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
            finish();
        } else {
            new DownloadFile.MakeRequestTask().execute();
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
                    mProgress.hide();
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
                DownloadFile.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, String> {
        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;

        MakeRequestTask() {

            HttpTransport httpTransport = new NetHttpTransport();
            JacksonFactory jsonFactory = new JacksonFactory();

            try {
                InputStream privateJsonStream = getAssets().open("drivelogin.json");
                GoogleCredential serviceAccountCredential =
                        new GoogleCredential().fromStream(privateJsonStream).createScoped(Arrays.asList(SCOPES));

                mService = new Drive.Builder(httpTransport, jsonFactory, null)
                        .setHttpRequestInitializer(serviceAccountCredential)
                        .build();
            } catch (IOException e) {
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Intent i = new Intent();
                if(ladeRunter())
                {
                    setResult(RESULT_OK, i);
                }
                else
                {
                    setResult(RESULT_CANCELED, i);
                }
                finish();
                return "loift";
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                mProgress.setMessage(e.getMessage());
                finish();
                return "madig";
            }
        }

        public boolean ladeRunter()
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try
            {
                mService.files().get(id)
                        .executeMediaAndDownloadTo(outputStream);

                OutputStream fOutputStream = new FileOutputStream(pfad);
                outputStream.writeTo(fOutputStream);
                return true;
            }
            catch (IOException e)
            {
                return false;
            }
        }

        @Override
        protected void onPostExecute(String output) {
            finish();
        }
    }
}
