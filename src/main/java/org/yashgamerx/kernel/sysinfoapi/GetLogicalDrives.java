package org.yashgamerx.kernel.sysinfoapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetLogicalDrives {
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetLogicalDrives(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        this.methodHandle = createGetLogicalDrives();
    }

    private MethodHandle createGetLogicalDrives() {
        MemorySegment getLogicalDrives_addr = kernel32.find("GetLogicalDrives")
                .orElseThrow();
        return linker.downcallHandle(
                getLogicalDrives_addr,
                FunctionDescriptor.of(ValueLayout.JAVA_INT)
        );
    }

    public int invoke() throws Throwable {
        return (int) methodHandle.invokeExact();
    }
}
