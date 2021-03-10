package com.mdiluca.ptdma.Models;

import java.util.Date;

public class Event extends Task {
    private Date date;

    public Event(String title, Date date) {
        super(title);
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
