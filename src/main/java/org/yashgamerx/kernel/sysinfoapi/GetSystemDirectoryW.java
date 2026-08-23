package org.yashgamerx.kernel.sysinfoapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetSystemDirectoryW {
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetSystemDirectoryW(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        methodHandle = createGetSystemDirectoryW();
    }

    private MethodHandle createGetSystemDirectoryW(){
        MemorySegment getSystemDirectoryW_addr = kernel32.find("GetSystemDirectoryW")
                .orElseThrow();
        return linker.downcallHandle(
                getSystemDirectoryW_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // UINT 32-bit
                        ValueLayout.ADDRESS, // [out] LPWSTR lpBuffer,
                        ValueLayout.JAVA_INT // [in]  UINT   uSize
                )
        );
    }

    public int invoke(MemorySegment lpBuffer, int uSize) throws Throwable {
        return (int) methodHandle.invokeExact(lpBuffer, uSize);
    }
}
