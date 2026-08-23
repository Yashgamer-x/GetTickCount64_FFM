package org.yashgamerx.kernel.processthreadsapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetProcessTimes {
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetProcessTimes(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        methodHandle = createGetProcessTimes();
    }

    private MethodHandle createGetProcessTimes() {
        MemorySegment getProcessTimes_addr = kernel32.find("GetProcessTimes")
                .orElseThrow();
        return linker.downcallHandle(
                getProcessTimes_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // BOOL is a typedef for INT in C
                        ValueLayout.ADDRESS,  // HANDLE - hprocess;
                        ValueLayout.ADDRESS,  // LPFILETIME - lpCreationTime
                        ValueLayout.ADDRESS,  // LPFILETIME - lpExitTime
                        ValueLayout.ADDRESS,  // LPFILETIME - lpKernelTime
                        ValueLayout.ADDRESS   // LPFILETIME - lpUserTime
                )
        );
    }

    public int invoke(
            MemorySegment process,
            MemorySegment lpCreationTimeMemorySegment,
            MemorySegment lpExitTimeMemorySegment,
            MemorySegment lpKernelTimeMemorySegment,
            MemorySegment lpUserTimeMemorySegment
    ) throws Throwable {
        return (int) methodHandle.invokeExact(
                process,
                lpCreationTimeMemorySegment,
                lpExitTimeMemorySegment,
                lpKernelTimeMemorySegment,
                lpUserTimeMemorySegment
        );
    }
}
