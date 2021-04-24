package com.mdiluca.ptdma.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mdiluca.ptdma.Fragments.Pickers.DatePickerFragment;
import com.mdiluca.ptdma.Fragments.Pickers.TimePickerFragment;
import com.mdiluca.ptdma.Models.Enum.ConversationState;
import com.mdiluca.ptdma.MainActivity;
import com.mdiluca.ptdma.Models.Event;
import com.mdiluca.ptdma.R;
import com.mdiluca.ptdma.Tools.CalendarManager;
import com.mdiluca.ptdma.utils.DateParser;
import com.mdiluca.ptdma.Tools.TextToSpeechInstance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddToDoFragment extends ListenerFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String ARG_PARAM1 = "withDate";
    private static final String ARG_PARAM2 = "shoppingList";
    private Boolean shoppingList;
    private Boolean withDate;

    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd MMMM yyyy", Locale.US);

    private Button cancelButton;
    private Button createButton;

    private TextView assistantText;
    private EditText titleText;
    private EditText dateText;

    private boolean userTouchedInputs = false;

    private Calendar selectedDate = null;

    private ConversationState conversationState = ConversationState.ASK_TITLE;

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

    public static AddToDoFragment newInstance() {
        AddToDoFragment fragment = new AddToDoFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM2, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            withDate = getArguments().getBoolean(ARG_PARAM1);
            shoppingList = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inf = inflater.inflate(R.layout.fragment_add_to_do, container, false);
        LinearLayout dateLayout = inf.findViewById(R.id.dateLayout);
        if (withDate == null || !withDate)
            dateLayout.setVisibility(View.GONE);

        TextView fragmentTitle = inf.findViewById(R.id.cardTitle);
        if (shoppingList)
            fragmentTitle.setText(getString(R.string.add_shopping_list_title));
        else
            fragmentTitle.setText(withDate ? getString(R.string.add_event_title) : getString(R.string.add_task_title));

        cancelButton = inf.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener((view -> {
            onInputClick();
            fragmentSwitcher.switcher(ConversationFragment.newInstance(getString(R.string.add_canceled)));
        }));

        createButton = inf.findViewById(R.id.createButton);

        createButton.setOnClickListener((view -> {
            onInputClick();
            boolean hasTitle = titleText.getText().length() > 0;
            if (withDate) {
                if (hasTitle && selectedDate != null)
                    addEvent(selectedDate);
                else
                    setAssistantResponse(getString(R.string.fields_empty));
            } else {
                if (hasTitle) {
                    if (shoppingList)
                        addShoppingList();
                    else
                        addTask();
                } else {
                    setAssistantResponse(getString(R.string.fields_empty));
                }
            }
        }));

        titleText = inf.findViewById(R.id.titleText);
        dateText = inf.findViewById(R.id.dateText);

        titleText.setOnTouchListener(((v, e) -> {
            onInputClick();
            return false;
        }));

        dateText.setOnClickListener((v -> {
            onInputClick();
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.setTargetFragment(this, 0);
            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        }));


        assistantText = inf.findViewById(R.id.assistantText);
        assistantText.setText(getString(R.string.ask_title));
        TextToSpeechInstance.speak(getString(R.string.ask_title));
        return inf;
    }

    private void addShoppingList() {
        MainActivity ma = (MainActivity) getActivity();
        Map<String, ArrayList<String>> shoppingLists = ma.getShoppingLists();
        if (shoppingLists == null) {
            shoppingLists = new HashMap<>();
        }

        shoppingLists.put(titleText.getText().toString(), new ArrayList<>());
        ma.setShoppingLists(shoppingLists);

        fragmentSwitcher.switcher(ConversationFragment.newInstance(getString(R.string.shopping_list_added, titleText.getText())));
    }

    private void onInputClick() {
        stopListeningUser.apply();
        userTouchedInputs = true;
    }

    private void addTask() {
        MainActivity ma = (MainActivity) getActivity();
        List<String> taskList = ma.getTaskList();
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        taskList.add(titleText.getText().toString());
        ma.setTaskList(taskList);

        fragmentSwitcher.switcher(ConversationFragment.newInstance(getString(R.string.task_added, titleText.getText())));
    }

    void setAssistantResponse(String assistantResponse) {
        if (!userTouchedInputs) {
            assistantText.setText(assistantResponse);
            TextToSpeechInstance.speak(assistantResponse);
        }
    }

    private void addEvent(Calendar date) {
        String title = titleText.getText().toString();
        if (!title.isEmpty()) {
            boolean added = CalendarManager.addEvent(new Event(title, date), getActivity());
            if (added)
                fragmentSwitcher.switcher(ConversationFragment.newInstance(getString(R.string.event_added, title, dateFormat.format(date.getTime()))));
            else
                fragmentSwitcher.switcher(ConversationFragment.newInstance(getString(R.string.calendar_error)));
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = Calendar.getInstance();
        selectedDate.set(year, monthOfYear, dayOfMonth);

        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
        selectedDate.set(Calendar.HOUR, hours);
        selectedDate.set(Calendar.MINUTE, minutes);

        dateText.setText(dateFormat.format(selectedDate.getTime()));
    }

    @Override
    boolean processVoice(String resp) {
        awaitsResponse = true;
        if (resp.equals("cancel")) {
            fragmentSwitcher.switcher(ConversationFragment.newInstance(getString(R.string.add_canceled)));
        } else {
            switch (conversationState) {
                case ASK_TITLE:
                    if (resp.length() > 0) {
                        titleText.setText(resp);
                        if (withDate) {
                            conversationState = ConversationState.ASK_DATE;
                            setAssistantResponse(getString(R.string.ask_date));
                        } else {
                            if (shoppingList)
                                addShoppingList();
                            else
                                addTask();
                        }
                    } else {
                        setAssistantResponse(getString(R.string.empty_title));
                    }
                    break;
                case ASK_DATE:
                    Calendar date = DateParser.parseDate(resp);
                    if (date == null) {
                        setAssistantResponse(getString(R.string.no_understand));
                    } else {
                        dateText.setText(resp);
                        addEvent(date);
                    }
                    break;
            }
        }
        return true;
    }
}