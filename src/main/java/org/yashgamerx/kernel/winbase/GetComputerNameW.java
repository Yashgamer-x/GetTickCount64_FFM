package org.yashgamerx.kernel.winbase;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetComputerNameW {
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetComputerNameW(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        methodHandle = createGetComputerNameW();
    }

    private MethodHandle createGetComputerNameW() {
        MemorySegment getEnvironmentVariableW_addr = kernel32.find("GetComputerNameW")
                .orElseThrow();
        return linker.downcallHandle(
                getEnvironmentVariableW_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // returns BOOL
                        ValueLayout.ADDRESS, // [out]     LPWSTR  lpBuffer
                        ValueLayout.ADDRESS // [in, out] LPDWORD nSize
                )
        );
    }

    public int invoke(MemorySegment lpBuffer, MemorySegment size) throws Throwable {
        return (int) methodHandle.invokeExact(lpBuffer, size);
    }
}
