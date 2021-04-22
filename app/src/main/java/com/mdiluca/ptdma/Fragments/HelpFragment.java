package com.mdiluca.ptdma.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mdiluca.ptdma.Interfaces.FragmentSwitcher;
import com.mdiluca.ptdma.Interfaces.SimpleFunction;
import com.mdiluca.ptdma.MainActivity;
import com.mdiluca.ptdma.R;
import com.mdiluca.ptdma.Tools.ConversationVoiceRecognizer;

import java.util.ArrayList;

public class HelpFragment extends Fragment {

    private SimpleFunction stopListening;
    private FragmentSwitcher fragmentSwitcher;

    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) { }
        @Override
        public void onBeginningOfSpeech() { }
        @Override
        public void onRmsChanged(float v) { }
        @Override
        public void onBufferReceived(byte[] bytes) { }
        @Override
        public void onEndOfSpeech() { }
        @Override
        public void onError(int i) {
            stopListening.apply();
        }
        @Override
        public void onResults(Bundle bundle) {
            if(bundle != null) {
                stopListening.apply();
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String resp = data.get(0);
                ConversationVoiceRecognizer.process(resp, fragmentSwitcher, getActivity());
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) { }

        @Override
        public void onEvent(int i, Bundle bundle) { }
    };

    public HelpFragment() {
        // Required empty public constructor
    }

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity ma = (MainActivity) getActivity();
        fragmentSwitcher = ma.fragmentSwitcher;
        stopListening= ma::stopListening;
        ma.setSpeechRecognizer(recognitionListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }
}