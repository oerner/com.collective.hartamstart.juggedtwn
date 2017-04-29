package com.collective.hartamstart.juggedtwn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class Menue extends AppCompatActivity {

    private Button gremiumProtokolleButton;

    private boolean beendeApp = false;

    private TextView userAnzeige;

    private String passwort;

    static final int ADMIN = 1;
    static final int USER = 2;

    static final int LOGIN_REQUEST = 123;
    static final int PRUEF_REQUEST = 312;
    public static final String PREFS_NAME = "einstellungen";

    private Menu menu;

    private SharedPreferences settings;

    private int userId = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menue);

        userAnzeige = (TextView) findViewById(R.id.textView2);

        Intent i = getIntent();

        passwort = i.getStringExtra("pw");
        userId = i.getIntExtra("user", 1);

        settings = getSharedPreferences(PREFS_NAME, 0);

        if(userId == ADMIN)
        {
            userAnzeige.setText("admin");
        }
        else
        {
            userAnzeige.setText("gast");
        }

        checkLogin();

        //Nav Bar Hintergrund Farbe setzen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        gremiumProtokolleButton = (Button) findViewById(R.id.gremiumsProtokolleButton);

        gremiumProtokolleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                protokollFensterOeffnen();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        //menu.findItem(R.id.login).setTitle("LogOut");
        inflater.inflate(R.menu.options_menu, menu);
        if(settings.getBoolean("login", true)) {
            MenuItem item = menu.findItem(R.id.login);
            if (item != null) {
                item.setTitle("LogOut");
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.login:
                login();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case LOGIN_REQUEST:
                if (resultCode == RESULT_OK) {
                    userAnzeige.setText(settings.getString("userName", "gast"));
                    userId = ADMIN;
                    MenuItem item = menu.findItem(R.id.login);
                    if(item != null)
                    {
                        item.setTitle("LogOut");
                    }
                }
                break;
            default:
                break;
        }
    }

    public void login()
    {
        if(settings.getBoolean("login", false))
        {
            logout();
        }
        else {
            Intent i = new Intent(this, Login.class);
            i.putExtra("pw", passwort);
            i.putExtra("istaktuell", false);
            startActivityForResult(i, LOGIN_REQUEST);
        }
    }

    public void logout()
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userName", "gast");
        editor.putString("pw", "gast");
        editor.putBoolean("login", false);
        editor.commit();
        userAnzeige.setText("gast");
        userId = USER;
        MenuItem item = menu.findItem(R.id.login);
        if(item != null)
        {
            item.setTitle("LogIn");
        }
    }

    public void checkLogin()
    {
        if(settings.getBoolean("login", false))
        {
            Intent i = new Intent(this, Login.class);
            i.putExtra("istaktuell", true);
            i.putExtra("pw", passwort);
            startActivityForResult(i, PRUEF_REQUEST);
            if(settings.getBoolean("login", false)) {
                userId = ADMIN;
            }
            userAnzeige.setText(settings.getString("userName", "fail"));

        }
    }

    @Override
    public void onBackPressed()
    {
        if (beendeApp) {
            beendeApp = true;
            finish();
        } else {
            Toast.makeText(this, "Zum Beenden nochmal zurück drücken.",
                    Toast.LENGTH_SHORT).show();
            beendeApp = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    beendeApp = false;
                }
            }, 3 * 1000);
        }
    }

    public void protokollFensterOeffnen()
    {
        Intent intent = new Intent(this, ProtokollFenster.class);
        intent.putExtra("userid", userId);
        intent.putExtra("pw", passwort);
        startActivity(intent);
    }
}
