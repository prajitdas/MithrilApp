package edu.umbc.cs.ebiquity.mithril.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;

/**
 * Created by Prajit Kumar Das on 6/11/2016.
 */

public class PrefsFragment extends PreferenceFragment {

    private SharedPreferences sharedPreferences;
    private SwitchPreference mSwithPrefWorkingHoursEnabled;
    private EditTextPreference mEditTextPrefHomeLocation;
    private EditTextPreference mEditTextPrefWorkLocation;
    private EditTextPreference mEditTextPrefStartTime;
    private EditTextPreference mEditTextPrefEndTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        sharedPreferences = getActivity().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        mSwithPrefWorkingHoursEnabled = (SwitchPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefKeyWorkingHoursEnabled());
        mEditTextPrefHomeLocation = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefKeyHomeLoc());
        mEditTextPrefWorkLocation = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefKeyWorkLoc());
        mEditTextPrefStartTime = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefKeyStartTime());
        mEditTextPrefEndTime = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefKeyEndTime());

        setOnPreferenceChangeListener();
    }

    private void setOnPreferenceChangeListener() {
        mSwithPrefWorkingHoursEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //TODO Add code for storing preferences
                return false;
            }
        });

        mEditTextPrefHomeLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //TODO Add code for storing preferences
                return false;
            }
        });

        mEditTextPrefWorkLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //TODO Add code for storing preferences
                return false;
            }
        });

        mEditTextPrefStartTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //TODO Add code for storing preferences
                return false;
            }
        });

        mEditTextPrefEndTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //TODO Add code for storing preferences
                return false;
            }
        });
    }
}