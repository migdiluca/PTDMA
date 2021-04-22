package com.mdiluca.ptdma.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.appbar.AppBarLayout;
import com.mdiluca.ptdma.Interfaces.ApplyFunction;
import com.mdiluca.ptdma.Interfaces.FragmentSwitcher;
import com.mdiluca.ptdma.MainActivity;
import com.mdiluca.ptdma.Models.Event;
import com.mdiluca.ptdma.R;
import com.mdiluca.ptdma.utils.AppBarStateChangeListener;
import com.mdiluca.ptdma.utils.CalendarItemAdapter;
import com.mdiluca.ptdma.Tools.CalendarManager;
import com.mdiluca.ptdma.Tools.ConversationVoiceRecognizer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarFragment extends Fragment {

    private AppBarLayout appBarLayout;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MMMM, dd", Locale.ENGLISH);
    private CompactCalendarView compactCalendarView;
    private boolean isExpanded = false;
    private Date selectedDate;
    private TextView datePickerTextView;
    private TextView noEventsWarning;
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
        public void onError(int i) { }

        @Override
        public void onResults(Bundle bundle) {
            String resp = "";
            if (bundle != null) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                System.out.println(data.get(0));
                resp = data.get(0);
            }

            applyFunction.apply(resp);
        }

        @Override
        public void onPartialResults(Bundle bundle) { }

        @Override
        public void onEvent(int i, Bundle bundle) { }
    };

    private ApplyFunction applyFunction = (String resp) -> {
        String[] twoWords = resp.split(" ", 2);
        List<String> words = Arrays.asList(resp.split("\\s+"));
        switch (twoWords[0]) {
            case "delete":
                long eventId = findEventIdToDelete(twoWords[1]);
                if(eventId != -1)
                    CalendarManager.deleteEvent(getActivity(), eventId);
                break;
            default:
                ConversationVoiceRecognizer.process(resp, fragmentSwitcher, getActivity());
                break;
        }
    };

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
            if (state.name().compareTo("COLLAPSED")==0){
                ViewCompat.animate(arrow).rotation(180).start();
                isExpanded=false;
            }else if(state.name().compareTo("EXPANDED")==0){
                ViewCompat.animate(arrow).rotation(0).start();
                isExpanded=true;
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

        MainActivity mainActivity = (MainActivity) getActivity();
        fragmentSwitcher = mainActivity.fragmentSwitcher;

        mainActivity.setSpeechRecognizer(recognitionListener);
        mainActivity.setApplyFunction(applyFunction);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        selectedDate = new Date();
        selectedDate.setHours(0);
        selectedDate.setMinutes(0);
        selectedDate.setSeconds(0);
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
        if(!eventList.isEmpty()) {
            noEventsWarning.setVisibility(View.GONE);
        } else {
            noEventsWarning.setVisibility(View.VISIBLE);
        }

        updateList();
    }

    private void updateList() {
        CalendarItemAdapter myAdapter = new CalendarItemAdapter(getContext(),R.layout.calendar_item, eventList);
        simpleList.setAdapter(myAdapter);
    }

    private long findEventIdToDelete(String name) {
        for(int i = 0; i < eventList.size(); i++) {
            Event e = eventList.get(i);
            if(e.getTitle().equals(name)) {
                eventList.remove(i);
                updateList();
                if(eventList.isEmpty())
                    noEventsWarning.setVisibility(View.VISIBLE);
                return e.getId();
            }
        }
        return -1;
    }
}