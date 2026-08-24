package org.yashgamerx.kernel.winuser;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public record Rect(
        int left,
        int top,
        int right,
        int bottom
) {
    public static final MemoryLayout RECT_LAYOUT =
            MemoryLayout.structLayout(
                    ValueLayout.JAVA_INT.withName("left"),
                    ValueLayout.JAVA_INT.withName("top"),
                    ValueLayout.JAVA_INT.withName("right"),
                    ValueLayout.JAVA_INT.withName("bottom")
            );

    // Var Handles
    public static final VarHandle LEFT_HANDLE = RECT_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("left"));
    public static final VarHandle TOP_HANDLE = RECT_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("top"));
    public static final VarHandle RIGHT_HANDLE = RECT_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("right"));
    public static final VarHandle BOTTOM_HANDLE = RECT_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("bottom"));

    public static Rect of(MemorySegment segment) {
        return new Rect(
                (int) LEFT_HANDLE.get(segment, 0L),
                (int) TOP_HANDLE.get(segment, 0L),
                (int) RIGHT_HANDLE.get(segment, 0L),
                (int) BOTTOM_HANDLE.get(segment, 0L)
        );
    }
}