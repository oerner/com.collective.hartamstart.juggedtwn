package com.collective.hartamstart.juggedtwn;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Menue extends AppCompatActivity {

    private Button gremiumProtokolleButton;
    private boolean beendeApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menue);

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
        startActivity(intent);
    }


}
