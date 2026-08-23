package org.yashgamerx.kernel.sysinfoapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetCurrentProcess {
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetCurrentProcess(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        methodHandle = createGetCurrentProcess();
    }

    private MethodHandle createGetCurrentProcess() {
        MemorySegment getCurrentProcess_addr = kernel32.find("GetCurrentProcess")
                .orElseThrow();
        return linker.downcallHandle(
                getCurrentProcess_addr,
                FunctionDescriptor.of(ValueLayout.ADDRESS)
        );
    }

    public MemorySegment invoke() throws Throwable {
        return (MemorySegment) methodHandle.invokeExact();
    }

}
