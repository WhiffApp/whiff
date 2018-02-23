package com.app.whiff.whiff;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;


/**
 * A simple {@link Fragment} subclass.
 */
public class PacketCaptureFragment extends Fragment {

    // Buttons to launch other activities
    public Button RootScannerButton;
    public Button NonRootScannerButton;
    public Button ARPSpooferButton;
    public Button DisplayButton;
    public Switch NonRootSwitch;
    public Switch RootSwitch;
    public Switch ARPSpooferSwitch;
    public Spinner devSpinner;

    public PacketCaptureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_packet_capture, container, false);
    }

}
