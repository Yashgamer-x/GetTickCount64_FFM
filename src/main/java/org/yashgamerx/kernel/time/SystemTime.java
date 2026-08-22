package org.yashgamerx.kernel.time;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public record SystemTime(
        int wYear,
        int wMonth,
        int wDayOfWeek,
        int wDay,
        int wHour,
        int wMinute,
        int wSecond,
        int wMillisecond
) {
    // Value Layouts
    public static final ValueLayout.OfShort W_YEAR_VALUE = ValueLayout.JAVA_SHORT.withName("wYear");
    public static final ValueLayout.OfShort W_MONTH_VALUE = ValueLayout.JAVA_SHORT.withName("wMonth");
    public static final ValueLayout.OfShort W_DAY_OF_WEEK_VALUE = ValueLayout.JAVA_SHORT.withName("wDayOfWeek");
    public static final ValueLayout.OfShort W_DAY_VALUE = ValueLayout.JAVA_SHORT.withName("wDay");
    public static final ValueLayout.OfShort W_HOUR_VALUE = ValueLayout.JAVA_SHORT.withName("wHour");
    public static final ValueLayout.OfShort W_MINUTE_VALUE = ValueLayout.JAVA_SHORT.withName("wMinute");
    public static final ValueLayout.OfShort W_SECOND_VALUE = ValueLayout.JAVA_SHORT.withName("wSecond");
    public static final ValueLayout.OfShort W_MILLISECOND_VALUE = ValueLayout.JAVA_SHORT.withName("wMilliseconds");

    // Memory Layouts
    public static final MemoryLayout SYSTEM_TIME_LAYOUT = MemoryLayout.structLayout(
            W_YEAR_VALUE,
            W_MONTH_VALUE,
            W_DAY_OF_WEEK_VALUE,
            W_DAY_VALUE,
            W_HOUR_VALUE,
            W_MINUTE_VALUE,
            W_SECOND_VALUE,
            W_MILLISECOND_VALUE
    );

    // Memory PathElements
    public static final MemoryLayout.PathElement W_YEAR_ELEMENT = MemoryLayout.PathElement.groupElement("wYear");
    public static final MemoryLayout.PathElement W_MONTH_ELEMENT = MemoryLayout.PathElement.groupElement("wMonth");
    public static final MemoryLayout.PathElement W_DAY_OF_WEEK_ELEMENT = MemoryLayout.PathElement.groupElement("wDayOfWeek");
    public static final MemoryLayout.PathElement W_DAY_ELEMENT = MemoryLayout.PathElement.groupElement("wDay");
    public static final MemoryLayout.PathElement W_HOUR_ELEMENT = MemoryLayout.PathElement.groupElement("wHour");
    public static final MemoryLayout.PathElement W_MINUTE_ELEMENT = MemoryLayout.PathElement.groupElement("wMinute");
    public static final MemoryLayout.PathElement W_SECOND_ELEMENT = MemoryLayout.PathElement.groupElement("wSecond");
    public static final MemoryLayout.PathElement W_MILLISECOND_ELEMENT = MemoryLayout.PathElement.groupElement("wMilliseconds");

    // VarHandles
    public static final VarHandle W_YEAR_VAR_HANDLE = SYSTEM_TIME_LAYOUT.varHandle(W_YEAR_ELEMENT);
    public static final VarHandle W_MONTH_VAR_HANDLE = SYSTEM_TIME_LAYOUT.varHandle(W_MONTH_ELEMENT);
    public static final VarHandle W_DAY_OF_WEEK_VAR_HANDLE = SYSTEM_TIME_LAYOUT.varHandle(W_DAY_OF_WEEK_ELEMENT);
    public static final VarHandle W_DAY_VAR_HANDLE = SYSTEM_TIME_LAYOUT.varHandle(W_DAY_ELEMENT);
    public static final VarHandle W_HOUR_VAR_HANDLE = SYSTEM_TIME_LAYOUT.varHandle(W_HOUR_ELEMENT);
    public static final VarHandle W_MINUTE_VAR_HANDLE = SYSTEM_TIME_LAYOUT.varHandle(W_MINUTE_ELEMENT);
    public static final VarHandle W_SECOND_VAR_HANDLE = SYSTEM_TIME_LAYOUT.varHandle(W_SECOND_ELEMENT);
    public static final VarHandle W_MILLISECOND_VAR_HANDLE = SYSTEM_TIME_LAYOUT.varHandle(W_MILLISECOND_ELEMENT);

    public static SystemTime of(MemorySegment segment) {
        short wYear = (short) W_YEAR_VAR_HANDLE.get(segment, 0L);
        short wMonth = (short) W_MONTH_VAR_HANDLE.get(segment, 0L);
        short wDayOfWeek = (short) W_DAY_OF_WEEK_VAR_HANDLE.get(segment, 0L);
        short wDay = (short) W_DAY_VAR_HANDLE.get(segment, 0L);
        short wHour = (short) W_HOUR_VAR_HANDLE.get(segment, 0L);
        short wMinute = (short) W_MINUTE_VAR_HANDLE.get(segment, 0L);
        short wSecond = (short) W_SECOND_VAR_HANDLE.get(segment, 0L);
        short wMillisecond = (short) W_MILLISECOND_VAR_HANDLE.get(segment, 0L);

        return new SystemTime(
                Short.toUnsignedInt(wYear),
                Short.toUnsignedInt(wMonth),
                Short.toUnsignedInt(wDayOfWeek),
                Short.toUnsignedInt(wDay),
                Short.toUnsignedInt(wHour),
                Short.toUnsignedInt(wMinute),
                Short.toUnsignedInt(wSecond),
                Short.toUnsignedInt(wMillisecond)
        );
    }

    @Override
    public String toString(){
        return "SystemTime:" +
                "\nwYear=" + wYear +
                "\nwMonth=" + toMonthString(wMonth) +
                "\nwDayOfWeek=" + toDayOfWeekString(wDayOfWeek) +
                "\nwDay=" + wDay +
                "\nwHour=" + wHour +
                "\nwMinute=" + wMinute +
                "\nwSecond=" + wSecond +
                "\nwMillisecond=" + wMillisecond;
    }

    private String toMonthString(int month) {
        return switch (month) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> throw new IllegalArgumentException("Invalid month: " + month);
        };
    }

    private String toDayOfWeekString(int dayOfWeek) {
        return switch (dayOfWeek) {
            case 0 -> "Sunday";
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";
            default -> throw new IllegalArgumentException("Invalid day of week: " + dayOfWeek);
        };
    }

}
