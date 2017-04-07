package com.collective.hartamstart.juggedtwn;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;

public class DateiName extends AppCompatActivity {

    private CalendarView calendar;

    private CheckBox standardB;
    private CheckBox openairB;
    private CheckBox sonstigeB;

    private LinearLayout layout;

    private java.io.File pfad;

    private EditText sonderText;
    private EditText seite;

    private Button datumButton;

    private Button okButton;

    private Calendar c;

    private int tag;
    private int monat;
    private int jahr;

    private String datum;

    private String art = "Gremium";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datei_name);

        //Nav Bar Hintergrund Farbe setzen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        c = Calendar.getInstance();

        tag = c.get(Calendar.DAY_OF_MONTH);
        monat = c.get(Calendar.MONTH );
        jahr = c.get(Calendar.YEAR);

        datumButton = (Button) findViewById(R.id.datumButton);

        datumToString();

        pfad = (java.io.File)getIntent().getExtras().get("pfad");

        standardB = (CheckBox) findViewById(R.id.checkStandard);
        openairB = (CheckBox) findViewById(R.id.checkOpenAir);
        sonstigeB = (CheckBox) findViewById(R.id.checkSonstige);

        layout = (LinearLayout) findViewById(R.id.nameLayout);

        sonderText = (EditText) findViewById(R.id.sonstigeText);
        seite = (EditText) findViewById(R.id.seite);

        layout.removeView(sonderText);

        okButton = (Button) findViewById(R.id.nameOkButton);

        standardB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                standard();
            }
        });
        openairB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                openair();
            }
        });
        sonstigeB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sonstige();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fertig();
            }
        });
    }


    public void datumToString()
    {
        datum = String.valueOf(tag) + "." + String.valueOf(monat + 1) + "." + String.valueOf(jahr).charAt(2) + String.valueOf(jahr).charAt(3);
        datumButton.setText("▼ " + datum + " ▼");
    }


    public void standard()
    {
        layout.removeView(sonderText);
        standardB.setChecked(true);
        openairB.setChecked(false);
        sonstigeB.setChecked(false);
        art = "Gremium";
    }

    public void openair()
    {
        layout.removeView(sonderText);
        standardB.setChecked(false);
        openairB.setChecked(true);
        sonstigeB.setChecked(false);
        art = "OpenAir";
    }

    public void sonstige()
    {
        layout.addView(sonderText);
        standardB.setChecked(false);
        openairB.setChecked(false);
        sonstigeB.setChecked(true);
        art = "sonder";
    }

    public void fertig()
    {
        if(art.equals("sonder"))
        {
            art = sonderText.getText().toString();
        }
        Intent i = new Intent();
        i.putExtra("pfad", pfad);
        String name = art + "_" + datum + "_SEITE_" + seite.getText().toString() + ".jpg";
        i.putExtra("dateiNameFertig", name);
        setResult(RESULT_OK, i);
        finish();
    }
}
