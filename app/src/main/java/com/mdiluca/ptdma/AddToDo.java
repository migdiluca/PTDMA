package com.mdiluca.ptdma;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddToDo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddToDo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "assistantText";
    private static final String ARG_PARAM2 = "withDate";

    // TODO: Rename and change types of parameters
    private String assistantText;
    private Boolean withDate;

    public AddToDo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param assistantText Parameter 1.
     * @param withDate Parameter 2.
     * @return A new instance of fragment AddToDo.
     */
    // TODO: Rename and change types and number of parameters
    public static AddToDo newInstance(String assistantText, Boolean withDate) {
        AddToDo fragment = new AddToDo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, assistantText);
        args.putBoolean(ARG_PARAM2, withDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            assistantText = getArguments().getString(ARG_PARAM1);
            withDate = getArguments().getBoolean(ARG_PARAM2);
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
        return inf;
    }
}