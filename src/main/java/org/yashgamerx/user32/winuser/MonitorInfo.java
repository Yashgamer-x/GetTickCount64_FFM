package org.yashgamerx.user32.winuser;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public record MonitorInfo(
        int cbSize,
        Rect rcMonitor,
        Rect rcWork,
        int dwFlags
) {

    public static final MemoryLayout MONITOR_INFO_LAYOUT =
            MemoryLayout.structLayout(
                    ValueLayout.JAVA_INT.withName("cbSize"),
                    Rect.RECT_LAYOUT.withName("rcMonitor"),
                    Rect.RECT_LAYOUT.withName("rcWork"),
                    ValueLayout.JAVA_INT.withName("dwFlags")
            );

    private static final VarHandle CB_SIZE_HANDLE =
            MONITOR_INFO_LAYOUT.varHandle(
                    MemoryLayout.PathElement.groupElement("cbSize")
            );

    private static final VarHandle FLAGS_HANDLE =
            MONITOR_INFO_LAYOUT.varHandle(
                    MemoryLayout.PathElement.groupElement("dwFlags")
            );

    public static MonitorInfo of(MemorySegment segment) {
        int cbSize = (int) CB_SIZE_HANDLE.get(segment, 0L);

        MemorySegment rcMonitorSegment =
                segment.asSlice(
                        MONITOR_INFO_LAYOUT.byteOffset(
                                MemoryLayout.PathElement.groupElement("rcMonitor")
                        ),
                        Rect.RECT_LAYOUT.byteSize()
                );

        MemorySegment rcWorkSegment =
                segment.asSlice(
                        MONITOR_INFO_LAYOUT.byteOffset(
                                MemoryLayout.PathElement.groupElement("rcWork")
                        ),
                        Rect.RECT_LAYOUT.byteSize()
                );

        Rect rcMonitor = Rect.of(rcMonitorSegment);
        Rect rcWork = Rect.of(rcWorkSegment);

        int dwFlags = (int) FLAGS_HANDLE.get(segment, 0L);

        return new MonitorInfo(
                cbSize,
                rcMonitor,
                rcWork,
                dwFlags
        );
    }
}