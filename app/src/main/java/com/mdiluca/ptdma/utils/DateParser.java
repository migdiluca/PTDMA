package com.mdiluca.ptdma.utils;

import com.mdiluca.ptdma.Models.Enum.Months;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DateParser {

    private static final String timeExp = "at (\\d+|(\\d{1,2}:\\d{2}))";
    private static final String timeAmPmExp = "at \\d+.* [AapP]\\.[Mm]\\.";

    private static final String fullDateExp = "\\d+.* of \\w+ of \\d+ " + timeExp;
    private static final String fullDateExpMinusOf = "\\d+.* of \\w+ \\d+ " + timeExp;
    private static final String fullDateAmPmExp = "\\d+.* of \\w+ of \\d+ " + timeAmPmExp;

    private static final String fullDateInversedExp = "\\w+.* \\d+.* \\d+ " + timeExp;
    private static final String fullDateInversedAmPmExp = "\\w+.* \\d+.* \\d+ " + timeAmPmExp;

    private static final String inTimeExp = "in \\d+ ?(hours|minutes|seconds)?";
    private static final String inTimeStepExp = "in one (hour|minute|second)";

    private static final String inDateExp = "in \\d+ (days|months|years) " + timeExp;
    private static final String inDateStepExp = "in one (day|month|year) " + timeExp;
    private static final String inDateStepAmPmExp = "in one (day|month|year) " + timeAmPmExp;
    private static final String inDateAmPmExp = "in \\d+ (days|months|years) " + timeAmPmExp;

    public static Calendar parseDate(String dateString) {
        System.out.println(dateString);
        Calendar now = Calendar.getInstance();
        List<String> words = Arrays.asList(dateString.split("\\s+"));
        if (dateString.matches(fullDateExpMinusOf) || dateString.matches(fullDateExp) || dateString.matches(fullDateAmPmExp) ||
            dateString.matches(fullDateInversedExp) || dateString.matches(fullDateInversedAmPmExp)) {
            int dayIndex = 0, monthIndex = 2, yearIndex = 4, timeIndex = 6;
            if(dateString.matches(fullDateExpMinusOf)) {
                yearIndex--;
                timeIndex--;
            } else if(dateString.matches(fullDateInversedExp) || dateString.matches(fullDateInversedAmPmExp)) {
                monthIndex = 0;
                dayIndex = 1;
                yearIndex = 2;
                timeIndex = 4;
            }

            int day = Utils.extractNumber(words.get(dayIndex));
            Months monthEnum = Months.fromString(words.get(monthIndex));
            if (monthEnum == null)
                return null;
            int month = monthEnum.ordinal();

            int year;
            try {
                year = Integer.parseInt(words.get(yearIndex));
            } catch (NumberFormatException e) {
                return null;
            }

            int[] time = Utils.extractTime(words.get(timeIndex));
            if (time == null)
                return null;

            if (dateString.matches(fullDateAmPmExp) || dateString.matches(fullDateInversedAmPmExp)) {
                String ampm;
                if(dateString.matches(fullDateAmPmExp))
                    ampm = words.get(7);
                else
                    ampm = words.get(5);
                if (ampm.charAt(0) == 'a' || ampm.charAt(0) == 'A') {
                    now.set(year, month, day, time[0], time[1]);
                } else {
                    now.set(year, month, day, time[0] + 12, time[1]);
                }
            } else {
                now.set(year, month, day, time[0], time[1]);
            }

            return now;
        } else if (dateString.matches(inTimeExp) || dateString.matches(inTimeStepExp)  || dateString.matches(inDateExp)
            || dateString.matches(inDateStepExp) || dateString.matches(inDateAmPmExp) || dateString.matches(inDateStepAmPmExp)) {
            int sum;
            if(dateString.matches(inTimeStepExp) || dateString.matches(inDateStepExp) || dateString.matches(inDateStepAmPmExp)) {
                sum = 1;
            } else {
                try {
                    sum = Integer.parseInt(words.get(1));
                } catch (NumberFormatException ex) {
                    return null;
                }

                if (sum < 0) {
                    return null;
                }
            }

            if (words.size() < 3) {
                now.add(Calendar.MINUTE, sum);
                return now;
            }

            switch (words.get(2)) {
                case "second":
                case "seconds":
                    now.add(Calendar.SECOND, sum);
                    return now;
                case "minute":
                case "minutes":
                    now.add(Calendar.MINUTE, sum);
                    return now;
                case "hour":
                case "hours":
                    now.add(Calendar.HOUR, sum);
                    return now;
                case "day":
                case "days":
                    now.add(Calendar.DATE, sum);
                    return now;
                case "month":
                case "months":
                    now.add(Calendar.MONTH, sum);
                    return now;
                case "year":
                case "years":
                    now.add(Calendar.YEAR, sum);
                    return now;
            }

            if(words.size() >= 5) {
                int[] time = Utils.extractTime(words.get(4));
                if (time == null)
                    return null;

                if (words.size() >= 6) {
                    String ampm = words.get(5);
                    if (ampm.charAt(0) == 'a' || ampm.charAt(0) == 'A') {
                        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), time[0], time[1]);
                    } else {
                        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), time[0] + 12, time[1]);
                    }
                } else {
                    now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), time[0], time[1]);
                }
                return now;
            }
        }
        return null;
    }


}
