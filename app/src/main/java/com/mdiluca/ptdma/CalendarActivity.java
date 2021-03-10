package com.mdiluca.ptdma;

import android.os.Bundle;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.appbar.AppBarLayout;
import com.mdiluca.ptdma.Models.Event;
import com.mdiluca.ptdma.utils.AppBarStateChangeListener;
import com.mdiluca.ptdma.utils.CalendarItemAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, dd",Locale.ENGLISH);
    private CompactCalendarView compactCalendarView;
    private boolean isExpanded = false;
    private Date selectedDate;
    private TextView datePickerTextView;

    ListView simpleList;
    List<Event> eventList = new ArrayList<>();


    private CompactCalendarView.CompactCalendarViewListener compactCalendarViewListener = new CompactCalendarView.CompactCalendarViewListener() {
        @Override
        public void onDayClick(Date dateClicked) {
            selectedDate=dateClicked;
            datePickerTextView.setText(dateFormat.format(dateClicked));
        }
        @Override
        public void onMonthScroll(Date firstDayOfNewMonth) {
            selectedDate=firstDayOfNewMonth;
            datePickerTextView.setText(dateFormat.format(firstDayOfNewMonth));
        }
    };

    private AppBarStateChangeListener appBarStateChangeListener = new AppBarStateChangeListener() {
        @Override
        public void onStateChanged(AppBarLayout appBarLayout, State state) {
            final ImageView arrow = findViewById(R.id.date_picker_arrow);
            if (state.name().compareTo("COLLAPSED")==0){
                ViewCompat.animate(arrow).rotation(180).start();
                isExpanded=false;
            }else if(state.name().compareTo("EXPANDED")==0){
                ViewCompat.animate(arrow).rotation(0).start();
                isExpanded=true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectedDate = new Date();
        appBarLayout = findViewById(R.id.app_bar_layout);
        compactCalendarView = findViewById(R.id.compactcalendar_view);
        final ImageView arrow = findViewById(R.id.date_picker_arrow);
        datePickerTextView = findViewById(R.id.date_picker_text_view);
        selectedDate=new Date();
        compactCalendarView.setLocale(TimeZone.getDefault(), Locale.ENGLISH);
        compactCalendarView.setShouldDrawDaysHeader(true);
        compactCalendarView.setCurrentDate(selectedDate);

        compactCalendarView.setListener(compactCalendarViewListener);
        appBarLayout.addOnOffsetChangedListener(appBarStateChangeListener);

        RelativeLayout datePickerButton = findViewById(R.id.date_picker_button);
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rotation = isExpanded ? 0 : 180;
                ViewCompat.animate(arrow).rotation(rotation).start();
                isExpanded = !isExpanded;
                appBarLayout.setExpanded(isExpanded, true);
            }
        });

        Date somedate = new Date();
        somedate.setHours(10);
        somedate.setMinutes(0);


        Date somedate2 = new Date();
        somedate2.setHours(8);
        somedate2.setMinutes(0);
        eventList.add(new Event("Job meeting", somedate2));
        eventList.add(new Event("Soccer practice", somedate));
        simpleList = findViewById(R.id.simpleListView);
        CalendarItemAdapter myAdapter = new CalendarItemAdapter(this,R.layout.calendar_item, eventList);
        simpleList.setAdapter(myAdapter);
    }
}