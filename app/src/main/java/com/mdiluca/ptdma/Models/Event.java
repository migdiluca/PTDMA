package com.mdiluca.ptdma.Models;

import java.util.Calendar;
import java.util.Date;

public class Event extends Task {
    private Calendar date;
    private long id;

    public Event(String title, Calendar date) {
        super(title);
        this.date = date;
    }

    public Event(long id, String title, Calendar date) {
        super(title);
        this.date = date;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}
