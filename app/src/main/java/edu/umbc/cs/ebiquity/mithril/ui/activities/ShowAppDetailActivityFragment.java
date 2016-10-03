package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.umbc.cs.ebiquity.mithril.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShowAppDetailActivityFragment extends Fragment {

    public ShowAppDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_app_detail, container, false);
    }
}
