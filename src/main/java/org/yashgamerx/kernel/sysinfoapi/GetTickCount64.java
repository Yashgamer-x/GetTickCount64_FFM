package org.yashgamerx.kernel.sysinfoapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetTickCount64 {
    private final MethodHandle getTickCount64;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetTickCount64(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        this.getTickCount64 = createGetTickCount64();
    }

    private MethodHandle createGetTickCount64() {
        MemorySegment getTickCount64_addr = kernel32.find("GetTickCount64")
                .orElseThrow();
        return linker.downcallHandle(
                getTickCount64_addr,
                FunctionDescriptor.of(ValueLayout.JAVA_LONG)
        );
    }

    public long invoke() throws Throwable {
        return (long) getTickCount64.invokeExact();
    }
}
