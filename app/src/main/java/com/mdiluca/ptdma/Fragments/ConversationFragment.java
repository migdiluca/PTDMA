package com.mdiluca.ptdma.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdiluca.ptdma.R;
import com.mdiluca.ptdma.Tools.TextToSpeechInstance;

public class ConversationFragment extends ListenerFragment {

    private static final String assistantTextParam = "assistantTextParam";

    private String assistantText;

    private TextView assistantTextView;

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
    boolean processVoice(String resp) {
        boolean processed = super.processVoice(resp);
        if(!processed) {
            awaitsResponse = true;
            setAssistantResponse(getString(R.string.no_understand));
        }
        return true;
    }

    void setAssistantResponse(String assistantResponse) {
        assistantTextView.setText(assistantResponse);
        TextToSpeechInstance.speak(assistantResponse);
    }
}