package com.mdiluca.ptdma.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdiluca.ptdma.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddToDoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddToDoFragment extends Fragment {

    private static final String ARG_PARAM1 = "withDate";
    private Boolean withDate;

    public AddToDoFragment() {
        // Required empty public constructor
    }

    public static AddToDoFragment newInstance(Boolean withDate) {
        AddToDoFragment fragment = new AddToDoFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, withDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            withDate = getArguments().getBoolean(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inf = inflater.inflate(R.layout.fragment_add_to_do, container, false);
        LinearLayout dateLayout = inf.findViewById(R.id.dateLayout);
        if(!withDate)
            dateLayout.setVisibility(View.GONE);

        TextView fragmentTitle = inf.findViewById(R.id.addTitle);
        fragmentTitle.setText(withDate ? "Add event" : "Add task");

        TextView assistantText = inf.findViewById(R.id.assistantText);
        assistantText.setText("What's the title?");
        return inf;
    }
}