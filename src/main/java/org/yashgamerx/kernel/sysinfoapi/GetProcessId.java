package org.yashgamerx.kernel.sysinfoapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetProcessId {

    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetProcessId(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        this.methodHandle = createGetProcessId();
    }

    private MethodHandle createGetProcessId() {
        MemorySegment getProcessId_addr = kernel32.find("GetProcessId")
                .orElseThrow();
        return linker.downcallHandle(
                getProcessId_addr,
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
        );
    }

    public int invoke(MemorySegment process) throws Throwable {
        return (int) methodHandle.invokeExact(process);
    }
}
