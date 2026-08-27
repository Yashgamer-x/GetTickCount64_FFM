package org.yashgamerx.user32.winuser;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetMonitorInfoW {

    private final MethodHandle methodHandle;
    private final SymbolLookup user32;
    private final Linker linker = Linker.nativeLinker();

    public GetMonitorInfoW(SymbolLookup user32) {
        this.user32 = user32;
        methodHandle = createGetMonitorInfoW();
    }

    private MethodHandle createGetMonitorInfoW() {
        MemorySegment getMonitorInfoW_addr = user32.find("GetMonitorInfoW")
                .orElseThrow();

        return linker.downcallHandle(
                getMonitorInfoW_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // BOOL
                        ValueLayout.ADDRESS,  // [in]  HMONITOR      hMonitor,
                        ValueLayout.ADDRESS   // [out] MONITORINFOEXW lpmi
                )
        );
    }

    public int invoke(MemorySegment hMonitor, MemorySegment lpmi) throws Throwable {
        return (int) methodHandle.invokeExact(hMonitor, lpmi);
    }
}
