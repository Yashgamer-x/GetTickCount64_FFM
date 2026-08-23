package org.yashgamerx.kernel.sysinfoapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

public class GetFileAttributeW {
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();
    private final Arena arena;

    public GetFileAttributeW(
            Arena arena,
            SymbolLookup kernel32
    ) {
        this.arena = arena;
        this.kernel32 = kernel32;
        methodHandle = createGetFileAttributeW();
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

    public int invoke(String path) throws Throwable {
        byte[] bytes = (path + "\0").getBytes(StandardCharsets.UTF_16LE);

        MemorySegment pathSegment = arena.allocate(bytes.length);
        pathSegment.copyFrom(MemorySegment.ofArray(bytes));

        return (int) methodHandle.invokeExact(pathSegment);
    }
}
