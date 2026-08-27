package org.yashgamerx.user32.winuser;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class EnumDisplayMonitors {

    private final MethodHandle methodHandle;
    private final SymbolLookup user32;
    private final Linker linker = Linker.nativeLinker();

    public EnumDisplayMonitors(SymbolLookup user32) {
        this.user32 = user32;
        methodHandle = createEnumDisplayMonitors();
    }

    private MethodHandle createEnumDisplayMonitors() {
        MemorySegment EnumDisplayMonitors_addr = user32.find("EnumDisplayMonitors")
                .orElseThrow();
        return linker.downcallHandle(
                EnumDisplayMonitors_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // BOOL
                        ValueLayout.ADDRESS,  // [in] HDC             hdc,
                        ValueLayout.ADDRESS,  // [in] LPCRECT         lprcClip,
                        ValueLayout.ADDRESS,  // [in] MONITORENUMPROC lpfnEnum,
                        ValueLayout.JAVA_LONG // [in] LPARAM          dwData
                )
        );
    }

    public int invoke(MemorySegment hdc, MemorySegment lprcClip, MemorySegment lpfnEnum, long dwData) throws Throwable {
        return (int) methodHandle.invokeExact(hdc, lprcClip, lpfnEnum, dwData);
    }
}
