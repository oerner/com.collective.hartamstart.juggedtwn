package com.collective.hartamstart.juggedtwn;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.GoogleApiAvailability;
        import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

        import com.google.api.client.http.HttpTransport;
        import com.google.api.client.http.javanet.NetHttpTransport;
        import com.google.api.client.json.jackson2.JacksonFactory;

        import com.google.api.services.drive.Drive;
        import com.google.api.services.drive.DriveScopes;

        import android.app.Activity;
        import android.app.Dialog;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.annotation.NonNull;

        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.util.Arrays;
        import java.util.List;

        import pub.devrel.easypermissions.EasyPermissions;

public class GetPassword extends Activity
        implements EasyPermissions.PermissionCallbacks {

    ProgressDialog mProgress;

    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    static final int ADMIN_REEQUEST = 2000;
    static final int USER_REQUEST = 2001;

    private static final String[] SCOPES = { DriveScopes.DRIVE_READONLY };

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Bitte warten ...");

        testeUmgebung();
    }


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
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
            new MakeRequestTask().execute();
        }
    }


    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
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

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                GetPassword.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Drive API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends  AsyncTask<Void, Void, String[]>{
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
            }
            catch (IOException e){
            }
        }

        /**
         * Background task to call Drive API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Intent i = new Intent();
                i.putExtra("pw", getPassword());
                setResult(RESULT_OK, i);
                mProgress.hide();
                finish();
                return getPassword();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                finish();
                return null;
            }
        }

        public String[] getPassword()
        {
            String userfileId = "13FTOwS6DYbTlvsZCrd6aJNZ0QSwxbEnRGGW3aMdxq6s";
            String adminfileId = "19olX92WCkLJPiePMc1WjK4QRzjj4Z3nN-sAZCU8Ag-8";
            String pws[] = new String[2];

            pws[0] = holen(userfileId);
            pws[1] = holen(adminfileId);

            return pws;
        }

        public String holen(String fileId)
        {
            String fileOutput;
            OutputStream outputStream = new ByteArrayOutputStream();
            try {
                mService.files().export(fileId, "text/plain")
                        .executeMediaAndDownloadTo(outputStream);
            }
            catch (IOException e)
            {

            }
            fileOutput = outputStream.toString();

            return fileOutput;
        }

        @Override
        protected void onPostExecute(String[] output) {
            finish();
        }
    }
}