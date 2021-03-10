package com.mdiluca.ptdma.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mdiluca.ptdma.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "items";
    private static final String ARG_PARAM2 = "param2";

    private String[] items;
    private String title;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(String[] items, String title) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_PARAM1, items);
        args.putString(ARG_PARAM2, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            items = getArguments().getStringArray(ARG_PARAM1);
            title = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ListView shoppingList = view.findViewById(R.id.shoppingList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
        shoppingList.setAdapter(adapter);

        TextView titleTextView = view.findViewById(R.id.listTitle);
        titleTextView.setText(title);
        return view;
    }
}