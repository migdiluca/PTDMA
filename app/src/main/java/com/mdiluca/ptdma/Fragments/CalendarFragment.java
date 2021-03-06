package com.mdiluca.ptdma.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.appbar.AppBarLayout;
import com.mdiluca.ptdma.Models.Event;
import com.mdiluca.ptdma.R;
import com.mdiluca.ptdma.Tools.TextToSpeechInstance;
import com.mdiluca.ptdma.utils.AppBarStateChangeListener;
import com.mdiluca.ptdma.utils.CalendarItemAdapter;
import com.mdiluca.ptdma.Tools.CalendarManager;
import com.mdiluca.ptdma.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarFragment extends ListenerFragment {

    private AppBarLayout appBarLayout;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MMMM, dd", Locale.ENGLISH);
    private CompactCalendarView compactCalendarView;
    private boolean isExpanded = false;
    private Date selectedDate;
    private TextView datePickerTextView;
    private TextView noEventsWarning;
    private CalendarItemAdapter myAdapter;

    ListView simpleList;
    List<Event> eventList = new ArrayList<>();

    private CompactCalendarView.CompactCalendarViewListener compactCalendarViewListener = new CompactCalendarView.CompactCalendarViewListener() {
        @Override
        public void onDayClick(Date dateClicked) {
            datePickerTextView.setText(dateFormat.format(dateClicked));
            onDateChange(dateClicked);
        }

        @Override
        public void onMonthScroll(Date firstDayOfNewMonth) {
            datePickerTextView.setText(dateFormat.format(firstDayOfNewMonth));
            onDateChange(firstDayOfNewMonth);
        }
    };

    private AppBarStateChangeListener appBarStateChangeListener = new AppBarStateChangeListener() {
        @Override
        public void onStateChanged(AppBarLayout appBarLayout, State state) {
            final ImageView arrow = getView().findViewById(R.id.date_picker_arrow);
            if (state.name().compareTo("COLLAPSED") == 0) {
                ViewCompat.animate(arrow).rotation(180).start();
                isExpanded = false;
            } else if (state.name().compareTo("EXPANDED") == 0) {
                ViewCompat.animate(arrow).rotation(0).start();
                isExpanded = true;
            }
        }
    };

    public CalendarFragment() {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        selectedDate = today.getTime();
        appBarLayout = view.findViewById(R.id.app_bar_layout);
        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        final ImageView arrow = view.findViewById(R.id.date_picker_arrow);
        datePickerTextView = view.findViewById(R.id.date_picker_text_view);
        noEventsWarning = view.findViewById(R.id.noEventsWarning);

        simpleList = view.findViewById(R.id.simpleListView);

        compactCalendarView.setLocale(TimeZone.getDefault(), Locale.ENGLISH);
        compactCalendarView.setShouldDrawDaysHeader(true);
        compactCalendarView.setCurrentDate(selectedDate);

        datePickerTextView.setText(dateFormat.format(selectedDate));

        compactCalendarView.setListener(compactCalendarViewListener);
        appBarLayout.addOnOffsetChangedListener(appBarStateChangeListener);

        onDateChange(selectedDate);

        RelativeLayout datePickerButton = view.findViewById(R.id.date_picker_button);
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rotation = isExpanded ? 0 : 180;
                ViewCompat.animate(arrow).rotation(rotation).start();
                isExpanded = !isExpanded;
                appBarLayout.setExpanded(isExpanded, true);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onDateChange(selectedDate);
    }

    private void onDateChange(Date date) {
        selectedDate = date;
        eventList = CalendarManager.getEvents(selectedDate, getActivity());
        if (!eventList.isEmpty()) {
            noEventsWarning.setVisibility(View.GONE);
        } else {
            noEventsWarning.setVisibility(View.VISIBLE);
        }

        updateList();
    }

    private void updateList() {
        myAdapter = new CalendarItemAdapter(getContext(), R.layout.calendar_item, eventList);
        simpleList.setAdapter(myAdapter);
    }

    private long deleteEvent(String name) {
        for (int i = 0; i < eventList.size(); i++) {
            Event e = eventList.get(i);
            if (e.getTitle().equalsIgnoreCase(name)) {
                CalendarManager.deleteEvent(getActivity(), e.getId());
                eventList.remove(i);
                updateList();
                if (eventList.isEmpty())
                    noEventsWarning.setVisibility(View.VISIBLE);
                setAssistantResponse(getString(R.string.event_deleted));
                return e.getId();
            }
        }
        setAssistantResponse(getString(R.string.event_not_found, name));
        return -1;
    }

    private void editEvent(String oldName, String newName) {

        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);
            if (event.getTitle().equals(oldName)) {
                CalendarManager.editEvent(getActivity(), event.getId(), newName);
                event.setTitle(newName);
                updateList();
                setAssistantResponse(getString(R.string.event_modified));
                break;
            }
        }
    }

    @Override
    boolean processVoice(String resp) {
        boolean alreadyProcessed = super.processVoice(resp);
        if (!alreadyProcessed) {
            String[] twoWords = resp.split(" ", 2);
            List<String> words = Arrays.asList(resp.split("\\s+"));
            switch (twoWords[0]) {
                case "delete":
                case "remove":
                case "erase":
                    if (twoWords.length > 1)
                        deleteEvent(twoWords[1]);
                    return true;
                case "change":
                case "edit":
                case "rename":
                    int i = words.indexOf("to");
                    if (i > 0 && words.size() >= 4) {
                        String eventName = Utils.getStringFromList(words.subList(1, i));
                        String newEventName = Utils.getStringFromList(words.subList(i + 1, words.size()));
                        if (eventName.length() > 0 && newEventName.length() > 0) {
                            editEvent(eventName, newEventName);
                        }
                    }
                    return true;
            }
            onNoCommandDetected();
            return false;
        }
        return true;
    }
}