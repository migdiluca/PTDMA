package com.mdiluca.ptdma.utils;

import java.util.List;

public class Utils {

    public static String getStringFromList(List<String> list) {
        StringBuilder resp = new StringBuilder();
        boolean firstWord = true;
        for(String s : list) {
            if(!firstWord)
                resp.append(" ");
            resp.append(s);
            firstWord = false;
        }
        return resp.toString();
    }

    public static boolean intArrayContains(int[] array, int elem) {
        for(int i = 0; i < array.length; i++) {
            if(array[i] == elem)
                return true;
        }
        return false;
    }

    public static int extractNumber(String s) {
        StringBuilder sb = new StringBuilder();
        for(Character c : s.toCharArray()) {
            if(Character.isDigit(c)) {
                sb.append(c);
            } else {
                if(sb.length() > 0) {
                    return Integer.parseInt(sb.toString());
                } else {
                    return -1;
                }
            }
        }

        if(sb.length() > 0) {
            return Integer.parseInt(sb.toString());
        } else {
            return -1;
        }
    }

    public static int[] extractTime(String s) {
        int [] resp = new int[2];

        int pointPosition = -1;
        boolean point = false;
        for(int i = 0; i < s.length() && !point; i++) {
            if(s.charAt(i) == ':') {
                point = true;
                pointPosition = i;
            }
        }

        int startHour = 0;
        int endHour, startMin, endMin;
        if(!point) {
            if(s.length() == 3) {
                startHour = 0;
                endHour = 1;
                startMin = 1;
                endMin = 3;
            } else {
                startHour = 0;
                endHour = 2;
                startMin = 2;
                endMin = 4;
            }
        } else {
            if(s.length() == 4) {
                startHour = 0;
                endHour = 1;
                startMin = 2;
                endMin = 4;
            } else {
                startHour = 0;
                endHour = 2;
                startMin = 3;
                endMin = 5;
            }
        }

        try {
            if(s.length() > 3) {
                resp[0] = Integer.parseInt(s.substring(startHour, endHour));
                resp[1] = Integer.parseInt(s.substring(startMin, endMin));
            } else {
                resp[0] = Integer.parseInt(s);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return resp;
    }

}
