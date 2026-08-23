package org.yashgamerx.kernel.sysinfoapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetSystemTimeAsFileTime {
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetSystemTimeAsFileTime(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        this.methodHandle = createGetSystemTimeAsFileTime();
    }

    private MethodHandle createGetSystemTimeAsFileTime() {
        MemorySegment getSystemTimeAsFileTime_addr = kernel32.find("GetSystemTimeAsFileTime")
                .orElseThrow();
        return linker.downcallHandle(
                getSystemTimeAsFileTime_addr,
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
        );
    }

    public void invoke(MemorySegment lpSystemTimeAsFileTime) throws Throwable {
        methodHandle.invokeExact(lpSystemTimeAsFileTime);
    }
}
