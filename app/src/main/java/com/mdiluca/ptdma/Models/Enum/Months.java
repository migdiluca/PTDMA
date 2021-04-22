package com.mdiluca.ptdma.Models.Enum;

public enum Months {
    JAN ("January"),
    FEB ("February"),
    MAR ("March"),
    APR ("April"),
    MAY ("May"),
    JUN ("June"),
    JUL ("July"),
    AUG ("August"),
    SEP ("September"),
    OCT ("October"),
    NOV ("November"),
    DEC ("December");

    private final String name;

    private Months(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public static Months fromString(String text) {
        for (Months b : Months.values()) {
            if (b.name.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public String toString() {
        return this.name;
    }
}
