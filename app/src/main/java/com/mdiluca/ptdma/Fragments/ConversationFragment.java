package com.mdiluca.ptdma.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdiluca.ptdma.Interfaces.FragmentSwitcher;
import com.mdiluca.ptdma.Interfaces.SimpleFunction;
import com.mdiluca.ptdma.MainActivity;
import com.mdiluca.ptdma.R;
import com.mdiluca.ptdma.Tools.ConversationVoiceRecognizer;
import com.mdiluca.ptdma.Tools.TextToSpeechInstance;

import java.util.ArrayList;

public class ConversationFragment extends Fragment {

    private static final String assistantTextParam = "assistantTextParam";

    private String assistantText;

    private TextView assistantTextView;

    private FragmentSwitcher fragmentSwitcher;

    private boolean awaitsResponse = false;
    private SimpleFunction startListening;
    private SimpleFunction stopListening;

    private UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onDone(String utteranceId) {
            if(awaitsResponse) {
                Handler mainHandler = new Handler(getContext().getMainLooper());
                Runnable myRunnable = () -> startListening.apply();
                mainHandler.post(myRunnable);
                awaitsResponse = false;
            }
        }

        @Override
        public void onError(String utteranceId) {

        }

        @Override
        public void onStart(String utteranceId) {

        }
    };

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
                boolean entered  = ConversationVoiceRecognizer.process(resp, fragmentSwitcher, getActivity());
                if(!entered) {
                    awaitsResponse = true;
                    setAssistantResponse(getString(R.string.no_understand));
                }
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) { }

        @Override
        public void onEvent(int i, Bundle bundle) { }
    };

    public ConversationFragment() {
        // Required empty public constructor
    }

    public static ConversationFragment newInstance(String assistantText) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putString(assistantTextParam, assistantText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            assistantText = getArguments().getString(assistantTextParam);
        }

        MainActivity mainActivity = (MainActivity) getActivity();
        fragmentSwitcher = mainActivity.fragmentSwitcher;

        mainActivity.setSpeechRecognizer(recognitionListener);

        startListening = mainActivity::startListening;
        stopListening = mainActivity::stopListening;

        TextToSpeechInstance.setOnUtteranceProgressListener(utteranceProgressListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setAssistantResponse(assistantText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inf = inflater.inflate(R.layout.fragment_conversation, container, false);
        assistantTextView = inf.findViewById(R.id.assistantText);
        return inf;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setSpeechRecognizer(recognitionListener);
    }

    private void setAssistantResponse(String assistantResponse) {
        assistantTextView.setText(assistantResponse);
        TextToSpeechInstance.speak(assistantResponse);
    }
}