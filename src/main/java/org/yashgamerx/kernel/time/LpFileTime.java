package org.yashgamerx.kernel.time;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public record LpFileTime(
        int  dwLowDateTime,
        int  dwHighDateTime
) {
    // Value Layouts
    public static final ValueLayout.OfInt DW_LOW_DATE_TIME_VALUE = ValueLayout.JAVA_INT.withName("dwLowDateTime");
    public static final ValueLayout.OfInt DW_HIGH_DATE_TIME_VALUE = ValueLayout.JAVA_INT.withName("dwHighDateTime");

    // Memory Layouts
    public static final MemoryLayout LP_FILE_TIME_LAYOUT = MemoryLayout.structLayout(
            DW_LOW_DATE_TIME_VALUE,
            DW_HIGH_DATE_TIME_VALUE
    );

    // Memory PathElements
    public static final MemoryLayout.PathElement DW_LOW_DATE_TIME_ELEMENT = MemoryLayout.PathElement.groupElement("dwLowDateTime");
    public static final MemoryLayout.PathElement DW_HIGH_DATE_TIME_ELEMENT = MemoryLayout.PathElement.groupElement("dwHighDateTime");

    // VarHandles
    public static final VarHandle DW_LOW_DATE_TIME_VAR_HANDLE = LP_FILE_TIME_LAYOUT.varHandle(DW_LOW_DATE_TIME_ELEMENT);
    public static final VarHandle DW_HIGH_DATE_TIME_VAR_HANDLE = LP_FILE_TIME_LAYOUT.varHandle(DW_HIGH_DATE_TIME_ELEMENT);

    public static LpFileTime of(MemorySegment segment) {
        int dwLowDateTime = (int) DW_LOW_DATE_TIME_VAR_HANDLE.get(segment, 0L);
        int dwHighDateTime = (int) DW_HIGH_DATE_TIME_VAR_HANDLE.get(segment, 0L);
        return new LpFileTime(dwLowDateTime, dwHighDateTime);
    }

    public long lowUnsigned() {
        return Integer.toUnsignedLong(dwLowDateTime);
    }

    public long highUnsigned() {
        return Integer.toUnsignedLong(dwHighDateTime);
    }

    public long toMilliseconds() {
        long value = (highUnsigned() << 32) | lowUnsigned();
        return value / 10_000;
    }

    @Override
    public String toString() {
        return "LpFileTime{" +
                "dwLowDateTime=" + lowUnsigned() +
                ", dwHighDateTime=" + highUnsigned() +
                '}';
    }
}
