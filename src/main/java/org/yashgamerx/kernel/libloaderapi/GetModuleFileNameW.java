package org.yashgamerx.kernel.libloaderapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetModuleFileNameW {
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetModuleFileNameW(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        methodHandle = createGetModuleFileNameW();
    }

    private MethodHandle createGetModuleFileNameW() {
        MemorySegment getEnvironmentVariableW_addr = kernel32.find("GetModuleFileNameW")
                .orElseThrow();
        return linker.downcallHandle(
                getEnvironmentVariableW_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // returns DWORD
                        ValueLayout.ADDRESS,  // [in, optional] HMODULE hModule,
                        ValueLayout.ADDRESS,  // [out]          LPWSTR  lpFilename,
                        ValueLayout.JAVA_INT  // [in]           DWORD   nSize
                )
        );
    }

    public int invoke(MemorySegment hModule, MemorySegment lpFilename, int nSize) throws Throwable {
        return (int) methodHandle.invokeExact(hModule, lpFilename, nSize);
    }
}
