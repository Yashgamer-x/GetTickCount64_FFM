package org.yashgamerx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Kernel32 {

    private final Arena arena;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();
    private final MethodHandle getTickCount64;
    private final MethodHandle getFileAttributeW;

    public Kernel32(Arena arena) {
        this.arena = arena;
        this.kernel32 = SymbolLookup.libraryLookup(
                "kernel32.dll", arena
        );

        getTickCount64 = createGetTickCount64();
        getFileAttributeW = createGetFileAttributeW();
    }

    private MethodHandle createGetTickCount64() {
        MemorySegment getTickCount64_addr = kernel32.find("GetTickCount64")
                .orElseThrow();
        return linker.downcallHandle(
                getTickCount64_addr,
                FunctionDescriptor.of(ValueLayout.JAVA_LONG)
        );
    }

    private MethodHandle createGetFileAttributeW() {
        MemorySegment getFileAttributesW_addr = kernel32.find("GetFileAttributesW")
                .orElseThrow();
        return linker.downcallHandle(
                getFileAttributesW_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT,
                        ValueLayout.ADDRESS
                )
        );
    }

    public long getTickCount64() throws Throwable {
        return (long) getTickCount64.invokeExact();
    }

    public int GetFileAttributesW() {
        return 0;
    }
}
