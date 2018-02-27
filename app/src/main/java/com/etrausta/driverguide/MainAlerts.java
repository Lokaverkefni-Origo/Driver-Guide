package com.etrausta.driverguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jonth on 14.2.2018.
 */

public class MainAlerts extends Fragment {
    public static MainAlerts newInstance() {
        MainAlerts fragment = new MainAlerts();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_alerts, container, false);
    }
}