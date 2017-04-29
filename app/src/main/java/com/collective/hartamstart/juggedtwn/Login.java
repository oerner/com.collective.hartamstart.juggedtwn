package com.collective.hartamstart.juggedtwn;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.io.FileOutputStream;

public class Login extends AppCompatActivity {

    static final int GET_PW_REQUEST = 177;
    public static final String PREFS_NAME = "einstellungen";

    private SharedPreferences settings;

    private EditText userName;
    private EditText userPW;
    private Button ok;

    private String passwort;
    private String pwGeholt;
    private String getippt;
    private String name;
    private String pwfail;

    private boolean aktuell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        aktuell = getIntent().getBooleanExtra("istaktuell", false);

        pwfail = "Falsches Passwort!";
        settings = getSharedPreferences(PREFS_NAME, 0);

        passwort = getIntent().getStringExtra("pw");

        if(aktuell)
        {
            String name = settings.getString("userName", "fail");
            String passw = settings.getString("pw", "fail");
            if(!name.equals("fail") && !passw.equals("fail"))
            {
                Intent i = new Intent(this, GetPassword.class);
                i.putExtra("pw", passwort);
                i.putExtra("name", name);
                i.putExtra("pass", passw);
                startActivityForResult(i, GET_PW_REQUEST);
            }
            else
            {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle(name + " " + passw);
                builder2.show();
            }
        }

        //Nav Bar Hintergrund Farbe setzen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        userName = (EditText) findViewById(R.id.userName);
        userPW = (EditText) findViewById(R.id.userPW);
        ok = (Button) findViewById(R.id.loginButton);

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                holePW();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        switch (resultCode)
        {
            case RESULT_OK:
                pwGeholt = intent.getStringExtra("pwGeholt");
                getippt = intent.getStringExtra("pwGetippt");
                name = intent.getStringExtra("user");
                checkPW();
                break;
            case RESULT_CANCELED:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("Falscher Benutzername!");
                builder2.show();
        }
    }

    public void checkPW()
    {
        if(istGleich())
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("login", true);
            editor.putString("userName", name);
            editor.putString("pw", getippt);

            // Commit the edits!
            editor.commit();

            Intent i = new Intent();
            setResult(RESULT_OK);
            finish();
        }
        else
        {
            if(aktuell) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("login", false);
                editor.putString("userName", "gast");
                editor.putString("pw", "leer");

                // Commit the edits!
                editor.commit();

                Intent i = new Intent();
                setResult(RESULT_OK);
                finish();
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setTitle(pwfail);
            builder2.show();
        }
    }

    public boolean istGleich()
    {
        for(int i = 0; i < getippt.length(); i++)
        {
            if(getippt.charAt(i) != pwGeholt.charAt(i+1))
            {
                for(int n = 0; n < getippt.length(); n++) {
                    if (getippt.charAt(n) != pwGeholt.charAt(n + 1)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return true;
    }

    public void holePW()
    {
        String name = userName.getText().toString();
        String passw = userPW.getText().toString();
        Intent i = new Intent(this, GetPassword.class);
        i.putExtra("pw", passwort);
        i.putExtra("name", name);
        i.putExtra("pass", passw);
        startActivityForResult(i, GET_PW_REQUEST);
    }
}
