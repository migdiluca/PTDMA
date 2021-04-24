package com.mdiluca.ptdma.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.UtteranceProgressListener;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mdiluca.ptdma.Interfaces.FragmentSwitcher;
import com.mdiluca.ptdma.Interfaces.SimpleFunction;
import com.mdiluca.ptdma.MainActivity;
import com.mdiluca.ptdma.R;
import com.mdiluca.ptdma.Tools.ConversationVoiceRecognizer;
import com.mdiluca.ptdma.Tools.TextToSpeechInstance;

import java.util.ArrayList;

public abstract class ListenerFragment extends Fragment {

    protected SimpleFunction stopListening;
    protected SimpleFunction startListening;
    protected SimpleFunction stopListeningUser;
    protected FragmentSwitcher fragmentSwitcher;
    protected boolean awaitsResponse;

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
            if(i == SpeechRecognizer.ERROR_NO_MATCH)
                onResults(null);
        }
        @Override
        public void onResults(Bundle bundle) {
            stopListening.apply();
            if(bundle != null) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String resp = data.get(0);
                processVoice(resp);
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) { }

        @Override
        public void onEvent(int i, Bundle bundle) { }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        awaitsResponse = false;

        MainActivity mainActivity = (MainActivity) getActivity();

        mainActivity.setSpeechRecognizer(recognitionListener);

        startListening = mainActivity::startListening;
        stopListening = mainActivity::stopListening;
        stopListeningUser = mainActivity::stopListeningUser;
        fragmentSwitcher = mainActivity.fragmentSwitcher;

        TextToSpeechInstance.setOnUtteranceProgressListener(utteranceProgressListener);
    }

    abstract void setAssistantResponse(String resp);

    boolean processVoice(String resp) {
        return ConversationVoiceRecognizer.process(resp, fragmentSwitcher, getActivity());
    }

    void onNoCommandDetected() {
        awaitsResponse = true;
        setAssistantResponse(getString(R.string.no_understand));
    }
}
