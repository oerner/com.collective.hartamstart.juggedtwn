package com.collective.hartamstart.juggedtwn;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static com.collective.hartamstart.juggedtwn.R.id.okButton;

public class MainMenu extends AppCompatActivity {

    private Button okButton;
    private String leerPasswort = "1860";
    private EditText passwortFeld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Nav Bar Hintergrund Farbe setzen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        okButton = (Button) findViewById(R.id.okButton);

        passwortFeld = (EditText) findViewById(R.id.passwortFeld);

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(passwortPruefen())
                {
                    hauptMenue();
                }
                else
                {
                    falschesPasswort();
                }
            }
        });
    }

    public boolean passwortPruefen()
    {
        String passwort = passwortFeld.getText().toString();
        if(passwort.equals(leerPasswort)) {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void hauptMenue()
    {
        Intent intent = new Intent(this, Menue.class);
        startActivity(intent);
        finish();
    }

    public void falschesPasswort()
    {
        //Falsches Passwort Benachrichtigung
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Falsches Passwort!");
        builder.show();
    }

    //Wahrscheinlich unnötig
    static {
        System.loadLibrary("native-lib");
    }
}
