package edu.umbc.ebiquity.mithril.ui.fragments.prefsactivityfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.umbc.ebiquity.mithril.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PrefsActivityFragment extends Fragment {

    public PrefsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prefs, container, false);
    }
}
