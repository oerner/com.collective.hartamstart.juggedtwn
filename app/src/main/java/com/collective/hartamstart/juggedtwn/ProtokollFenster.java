package com.collective.hartamstart.juggedtwn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProtokollFenster extends AppCompatActivity {

    private TableLayout liste;

    static final int ADMIN = 1;
    static final int USER = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    static final int REQUEST_LIST_HOLEN = 666;
    static final int UPLOAD_REQUEST = 1987;
    static final int DATEI_NAME_REQUEST = 13;
    static final int DOWNLOAD_REQUEST = 17;
    static final int GALLERY_REQUEST = 88;

    boolean del;

    private String mCurrentPhotoPath;

    private File fotFile;

    private ImageButton addButton;

    private int userId = USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protokoll_fenster);

        //Nav Bar Hintergrund Farbe setzen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        userId = getIntent().getIntExtra("userid", 2);



        liste = (TableLayout) findViewById(R.id.liste);

        addButton = (ImageButton) findViewById(R.id.imageButton);


        addButton.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             machFoto();
                                         }
                                     });

        if(userId == USER) {
            liste.removeView(addButton);
        }


        holeListe();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode)
        {
            case DATEI_NAME_REQUEST:
                String geholterName = intent.getStringExtra("dateiNameFertig");
                uploadStart((java.io.File)intent.getExtras().get("pfad"), geholterName);
                break;
            case GALLERY_REQUEST:
                del = fotFile.delete();
                break;
            case REQUEST_TAKE_PHOTO:
                if(resultCode == RESULT_OK)
                {
                    Intent i = new Intent(this, DateiName.class);
                    i.putExtra("pfad", fotFile);
                    startActivityForResult(i, DATEI_NAME_REQUEST);
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("kein Foto aufgenommen!");
                    builder.show();
                }
                break;
            case UPLOAD_REQUEST:
                if(resultCode == RESULT_OK)
                {
                    del = fotFile.delete();
                    holeListe();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Upload Fehlgeschlagen!");
                    builder.show();
                    del = fotFile.delete();
                }
                break;
            case DOWNLOAD_REQUEST:
                if(resultCode == RESULT_OK)
                {
                    try
                    {
                        Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
                        File file = new File(fotFile.getAbsolutePath());
                        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        myIntent.setDataAndType(Uri.fromFile(file),mimetype);
                        startActivityForResult(myIntent, GALLERY_REQUEST);
                    }
                    catch (Exception e)
                    {
                        // TODO: handle exception
                        String data = e.getMessage();
                    }
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Download Fehlgeschlagen!");
                    builder.show();
                    del = fotFile.delete();
                }
                break;
            case REQUEST_LIST_HOLEN:
                if(resultCode == RESULT_OK)
                {
                    try {
                        ArrayList<String> namenListe = intent.getExtras().getStringArrayList("namenliste");
                        ArrayList<String> idListe = intent.getExtras().getStringArrayList("idliste");
                        auflisten(namenListe, idListe);
                    }
                    catch(NullPointerException e)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(intent.getStringExtra(e.getMessage()));
                        builder.show();
                    }
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(intent.getStringExtra("fehler"));
                    builder.show();
                }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyddMM_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        fotFile = image;
        return image;
    }

    public void runterladen(String id)
    {
        Intent i = new Intent(this, DownloadFile.class);
        i.putExtra("fileId", id);
        try
        {
            i.putExtra("pfad", createImageFile());
        }
        catch (IOException e)
        {
        }
        startActivityForResult(i, DOWNLOAD_REQUEST);
    }

    public void uploadStart(File endgültigPfad, String fertigName)
    {
        Intent i = new Intent(this, UploadActivity.class);
        i.putExtra("pfad", endgültigPfad);
        i.putExtra("fertigerName", fertigName);
        startActivityForResult(i, UPLOAD_REQUEST);
    }

    public void machFoto()
    {
        Context context = ProtokollFenster.this;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fotoUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);

                List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, fotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void holeListe()
    {
        Intent i = new Intent(this, HoleProtokolle.class);
        startActivityForResult(i, REQUEST_LIST_HOLEN);
    }

    public void auflisten(ArrayList<String> namenListe, ArrayList<String> idListe)
    {
        for(int i = 0; i < namenListe.size(); i++)
        {
            zeileHinzufuegen(namenListe, idListe, i);
        }
    }

    public void zeileHinzufuegen(ArrayList<String> namenListe, ArrayList<String> idListe, int zeile)
    {
        String name = namenListe.get(zeile);
        final String id = idListe.get(zeile);

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        Button datum = new Button(this);
        datum.setText(name);

        datum.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                runterladen(id);
            }
        });

        tr.addView(datum);

        liste.addView(tr);

    }

}
