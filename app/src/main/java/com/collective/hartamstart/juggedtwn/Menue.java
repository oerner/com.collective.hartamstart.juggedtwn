package com.collective.hartamstart.juggedtwn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Menue extends AppCompatActivity {

    private Button gremiumProtokolleButton;
    private Button driveButton;
    private boolean beendeApp = false;

    private TextView userAnzeige;

    static final int ADMIN = 1;
    static final int USER = 2;

    private int userId = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menue);

        userAnzeige = (TextView) findViewById(R.id.textView2);

        Intent i = getIntent();

        userId = i.getIntExtra("user", 1);

        if(userId == ADMIN)
        {
            userAnzeige.setText("admin");
        }
        else
        {
            userAnzeige.setText("user");
        }




        //Nav Bar Hintergrund Farbe setzen
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        gremiumProtokolleButton = (Button) findViewById(R.id.gremiumsProtokolleButton);
        driveButton = (Button) findViewById(R.id.driveButton);
        driveButton.setText(" ");

        /*
        driveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                driveFensterOeffnen();
            }

        });
        */

        gremiumProtokolleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                protokollFensterOeffnen();
            }
        });
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
        startActivity(intent);
    }

    public void driveFensterOeffnen()
    {
        Intent intent = new Intent(this, TestAPI.class);
        startActivity(intent);
    }


}
