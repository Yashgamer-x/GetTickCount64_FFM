package org.yashgamerx.kernel.fileapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetTempPathW {
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetTempPathW(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        methodHandle = createGetTempPathW();
    }

    private MethodHandle createGetTempPathW() {
        MemorySegment getTempPathW_addr = kernel32.find("GetTempPathW")
                .orElseThrow();

        return linker.downcallHandle(
                getTempPathW_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // returns DWORD
                        ValueLayout.JAVA_INT, // [in] DWORD nBufferLength
                        ValueLayout.ADDRESS   // [out] LPWSTR lpBuffer
                )
        );
    }

    public int invoke(int nBufferLength, MemorySegment lpBuffer) throws Throwable {
        return (int) methodHandle.invokeExact(nBufferLength, lpBuffer);
    }
}
