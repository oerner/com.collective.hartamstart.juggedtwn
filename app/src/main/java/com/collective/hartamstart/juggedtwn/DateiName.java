package com.collective.hartamstart.juggedtwn;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.content.SharedPreferences;
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

    public static final String PREFS_NAME = "einstellungen";
    private SharedPreferences settings;

    private String art = "Gremium";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datei_name);

        //Nav Bar Hintergrund Farbe setzen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        settings = getSharedPreferences(PREFS_NAME, 0);

        c = Calendar.getInstance();

        tag = c.get(Calendar.DAY_OF_MONTH);
        monat = c.get(Calendar.MONTH );
        jahr = c.get(Calendar.YEAR);

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("jahr", jahr);
        editor.putInt("tag", tag);
        editor.putInt("monat", monat);
        editor.commit();

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

    public void showDatePickerDialog(View v)
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void datumToString()
    {
        datum = String.valueOf(settings.getInt("tag", 11)) + "." + String.valueOf(settings.getInt("monat", 8) + 1) + "." + String.valueOf(settings.getInt("jahr", 2001)).charAt(2) + String.valueOf(settings.getInt("jahr", 2001)).charAt(3);
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
        datumToString();
        String name = art + "_" + datum + "_SEITE_" + seite.getText().toString() + ".jpg";
        i.putExtra("dateiNameFertig", name);
        setResult(RESULT_OK, i);
        finish();
    }
}
