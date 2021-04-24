package com.mdiluca.ptdma.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mdiluca.ptdma.Models.Event;
import com.mdiluca.ptdma.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CalendarItemAdapter extends BaseAdapter {
    private int layout;
    private Context context;
    private List<Event> items;


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    public CalendarItemAdapter(Context context, int layout, List<Event> items) {
        this.context = context;
        this.layout = layout;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            v = inflater.inflate(R.layout.calendar_item, null);
        }
        else
            v = view;

        Event selectedEvent = items.get(position);

        TextView titleText = v.findViewById(R.id.title);
        TextView dateText = v.findViewById(R.id.date);

        String title;
        if(selectedEvent.getTitle() != null && selectedEvent.getTitle().length() > 0) {
            title = selectedEvent.getTitle();
        } else {
            title = "This event doesn't have a name";
        }
        titleText.setText(title);

        dateText.setText(dateFormat.format(selectedEvent.getDate().getTime()));

        return v;
    }
}