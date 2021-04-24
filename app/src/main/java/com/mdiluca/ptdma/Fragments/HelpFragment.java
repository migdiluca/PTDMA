package com.mdiluca.ptdma.Fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mdiluca.ptdma.R;
import com.mdiluca.ptdma.Tools.TextToSpeechInstance;

public class HelpFragment extends ListenerFragment {

    public HelpFragment() {
        // Required empty public constructor
    }

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    void setAssistantResponse(String resp) {
        TextToSpeechInstance.speak(resp);
    }

    @Override
    boolean processVoice(String resp) {
        boolean processed = super.processVoice(resp);
        if(!processed) {
            awaitsResponse = true;
            setAssistantResponse(getString(R.string.no_understand));
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }
}