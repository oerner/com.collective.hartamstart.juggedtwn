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

public class MainMenu extends AppCompatActivity {

    private Button okButton;
    private EditText passwortFeld;
    private String userPasswortGeholt;
    private String adminPasswortGeholt;
    private int userId = 0;

    static final int ADMIN = 1;
    static final int USER = 2;


    static final int GET_PASSWORD_FROM_DRIVE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Nav Bar Hintergrund Farbe setzen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        holePasswort();
        okButton = (Button) findViewById(R.id.okButton);

        passwortFeld = (EditText) findViewById(R.id.passwortFeld);

        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                passwortPruefen();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_CANCELED)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(intent.getStringExtra("fehler"));
            builder.show();
        }
        else if(resultCode == RESULT_OK)
        {
            String[] pws = intent.getStringArrayExtra("pw");
            userPasswortGeholt = pws[0];
            adminPasswortGeholt = pws[1];
        }
    }

    public void passwortPruefen()
    {
        String passwortGetippt = passwortFeld.getText().toString();

        if(istGleich(passwortGetippt))
        {
            hauptMenue();
        }
        else
        {
            falschesPasswort();
        }
    }

    public void holePasswort()
    {
        Intent i = new Intent(this, GetPassword.class);
        i.putExtra("art", GET_PASSWORD_FROM_DRIVE);
        startActivityForResult(i, 1);
    }

    public boolean istGleich(String getippt)
    {
        for(int i = 0; i < getippt.length(); i++)
        {
            if(getippt.charAt(i) != userPasswortGeholt.charAt(i+1))
            {
                for(int n = 0; n < getippt.length(); n++) {
                    if (getippt.charAt(n) != adminPasswortGeholt.charAt(n + 1)) {
                        return false;
                    }
                }
                userId = ADMIN;
                return true;
            }
        }
        userId = USER;
        return true;
    }

    public void hauptMenue()
    {
        Intent intent = new Intent(this, Menue.class);
        intent.putExtra("user", userId);
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
