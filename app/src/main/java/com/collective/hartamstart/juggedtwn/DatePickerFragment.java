package com.collective.hartamstart.juggedtwn;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;


import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static final String PREFS_NAME = "einstellungen";
    private SharedPreferences settings;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("jahr", year);
        editor.putInt("tag", day);
        editor.putInt("monat", month);
        editor.commit();

        Button dat = (Button) getActivity().findViewById(R.id.datumButton);
        dat.setText(String.valueOf(settings.getInt("tag", 11)) + "." + String.valueOf(settings.getInt("monat", 8) + 1) + "." + String.valueOf(settings.getInt("jahr", 2001)).charAt(2) + String.valueOf(settings.getInt("jahr", 2001)).charAt(3));
    }
}