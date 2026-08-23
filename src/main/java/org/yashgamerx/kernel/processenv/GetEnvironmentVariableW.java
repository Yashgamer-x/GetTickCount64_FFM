package org.yashgamerx.kernel.processenv;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetEnvironmentVariableW {

    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetEnvironmentVariableW(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        methodHandle = createGetEnvironmentVariableW();
    }

    private MethodHandle createGetEnvironmentVariableW() {
        MemorySegment getEnvironmentVariableW_addr = kernel32.find("GetEnvironmentVariableW")
                .orElseThrow();
        return linker.downcallHandle(
                getEnvironmentVariableW_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // returns DWORD
                        ValueLayout.ADDRESS, // [in, optional]  LPCWSTR lpName
                        ValueLayout.ADDRESS, // [out, optional] LPWSTR  lpBuffer
                        ValueLayout.JAVA_INT // [in] DWORD  nSize
                )
        );
    }

    public int invoke(MemorySegment lpName, MemorySegment lpBuffer, int size) throws Throwable {
        return (int) methodHandle.invokeExact(lpName, lpBuffer, size);
    }

}
