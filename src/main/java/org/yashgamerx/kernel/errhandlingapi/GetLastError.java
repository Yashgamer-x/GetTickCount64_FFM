package org.yashgamerx.kernel.errhandlingapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetLastError {
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetLastError(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        this.methodHandle = createGetLastError();
    }

    private MethodHandle createGetLastError() {
        MemorySegment GetLastError_addr = kernel32.find("GetLastError")
                .orElseThrow();
        return linker.downcallHandle(
                GetLastError_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT
                )
        );
    }

    public int invoke() throws Throwable {
        return (int) methodHandle.invokeExact();
    }
}
