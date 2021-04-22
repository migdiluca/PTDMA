package com.mdiluca.ptdma.Tools;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.widget.Toast;

import com.mdiluca.ptdma.MainActivity;
import com.mdiluca.ptdma.Models.Event;
import com.mdiluca.ptdma.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarManager {

    private static long calId = -1;
    private static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public static boolean addEvent(Event event, Activity activity) {
        if(calId < 0)
            calId = getCalId(activity);

        if(calId >= 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());

            long start = event.getDate().getTimeInMillis();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CalendarContract.Events.DTSTART, start);
            contentValues.put(CalendarContract.Events.DTEND, start);

            contentValues.put(CalendarContract.Events.TITLE, event.getTitle());
            contentValues.put(CalendarContract.Events.CALENDAR_ID, calId);
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Madrid");

            Uri uri2 = activity.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, contentValues);
            long eventId = new Long(uri2.getLastPathSegment());

            ContentResolver contentResolver = activity.getContentResolver();
            ContentValues valuesReminder = new ContentValues();
            valuesReminder.put(CalendarContract.Reminders.MINUTES, 60 * 24);
            valuesReminder.put(CalendarContract.Reminders.EVENT_ID, eventId);
            valuesReminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            Uri uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, valuesReminder);

            valuesReminder.put(CalendarContract.Reminders.MINUTES, 30);
            uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, valuesReminder);
            return true;
        }
        return false;
    }

    public static List<Event> getEvents(Date date, Activity activity) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        long startDay = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, 1);
        calendar.add(Calendar.SECOND, -1);
        long endDay = calendar.getTimeInMillis();

        String[] projection = new String[]{BaseColumns._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART};
        String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTSTART + "<= ? AND " + CalendarContract.Events.DELETED + " != 1";
        String[] selectionArgs = new String[]{Long.toString(startDay), Long.toString(endDay)};

        Cursor cursor = activity.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null);

        List<Event> eventList = new ArrayList<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
            String title = cursor.getString(cursor.getColumnIndex(projection[1]));
            Calendar eventDate = Calendar.getInstance();
            eventDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(projection[2])));

            eventList.add(new Event(id, title, eventDate));
        }
        cursor.close();
        return eventList;
    }

    public static long getCalId(Activity activity) {
        MainActivity ma = (MainActivity) activity;

        Cursor cur;
        ContentResolver cr = activity.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{ma.getAccount(), "com.google", ma.getAccount()};
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        long calID = -1;
        while (cur.moveToNext()) {
            calID = cur.getLong(PROJECTION_ID_INDEX);
        }
        cur.close();
        return calID;
    }

    public static void deleteEvent(Activity activity, long eventId) {
        ContentResolver cr = activity.getContentResolver();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = cr.delete(deleteUri, null, null);
    }

    public static void editEvent(Activity activity, long eventId, String newName) {
        ContentResolver cr = activity.getContentResolver();
        ContentValues values = new ContentValues();
        Uri updateUri = null;
        values.put(CalendarContract.Events.TITLE, newName);
        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = cr.update(updateUri, values, null, null);
    }
}
