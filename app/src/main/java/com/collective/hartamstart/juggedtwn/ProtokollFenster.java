package com.collective.hartamstart.juggedtwn;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;

import java.io.File;
import java.io.IOException;

public class ProtokollFenster extends AppCompatActivity {

    private TableLayout liste;
    private File testPfad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prototkoll_fenster);

        //Nav Bar Hintergrund Farbe setzen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        testPfad = new File("C:\\Users\\Bernhard\\AndroidStudioProjects\\JuggeDTWN\\app\\src\\main\\res\\drawable");

        liste = (TableLayout) findViewById(R.id.liste);

        zeileHinzufuegen(testPfad);
    }

    public void zeileHinzufuegen(File pfad)
    {
        //container für titel
        final File tPfad = pfad;
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        Button datum = new Button(this);
        datum.setText(pfad.toString());

        datum.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                bildOeffnen(tPfad);
            }
        });

        tr.addView(datum);

        liste.addView(tr);

    }

    public void bildOeffnen(File pfad)
    {
        Intent intent = new Intent(this, BildAnzeigen.class);
        intent.putExtra("bildpfad", testPfad.toString());
        startActivity(intent);

    }
}
