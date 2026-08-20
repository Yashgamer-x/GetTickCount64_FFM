package org.yashgamerx.kernel;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

public class Kernel32 {

    private final Arena arena;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();
    private final MethodHandle getTickCount64;
    private final MethodHandle getFileAttributeW;
    private final MethodHandle getLogicalDrives;
    private final MethodHandle getSystemInfo;

    public Kernel32(Arena arena) {
        this.arena = arena;
        this.kernel32 = SymbolLookup.libraryLookup(
                "kernel32.dll", arena
        );

        getTickCount64 = createGetTickCount64();
        getFileAttributeW = createGetFileAttributeW();
        getLogicalDrives = createGetLogicalDrives();
        getSystemInfo = createGetSystemInfo();
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

    private MethodHandle createGetLogicalDrives() {
        MemorySegment getLogicalDrives_addr = kernel32.find("GetLogicalDrives")
                .orElseThrow();
        return linker.downcallHandle(
                getLogicalDrives_addr,
                FunctionDescriptor.of(ValueLayout.JAVA_INT)
        );
    }

    private MethodHandle createGetSystemInfo(){
        MemorySegment getSystemInfo_addr = kernel32.find("GetSystemInfo")
                .orElseThrow();
        return linker.downcallHandle(
                getSystemInfo_addr,
                FunctionDescriptor.of(ValueLayout.JAVA_INT)
        );
    }

    public long getTickCount64() throws Throwable {
        return (long) getTickCount64.invokeExact();
    }

    public int getFileAttributesW(String path) throws Throwable {
        byte[] bytes = (path + "\0").getBytes(StandardCharsets.UTF_16LE);

        MemorySegment pathSegment = arena.allocate(bytes.length);
        pathSegment.copyFrom(MemorySegment.ofArray(bytes));

        return (int) getFileAttributeW.invokeExact(pathSegment);
    }

    public int getLogicalDrives() throws Throwable {
        return (int) getLogicalDrives.invokeExact();
    }

    public System_Info getSystemInfo() throws Throwable {
        final var OEM_LAYOUT = MemoryLayout.unionLayout(
                ValueLayout.JAVA_INT.withName("dwOemId"),
                ValueLayout.JAVA_SHORT.withName("wProcessorArchitecture"),
                ValueLayout.JAVA_SHORT.withName("wReserved")
        ).withName("oemId");
        MemoryLayout SYSTEM_INFO_LAYOUT = MemoryLayout.structLayout(
                OEM_LAYOUT, // oemId
                ValueLayout.JAVA_INT.withName("dwPageSize"),

                ValueLayout.ADDRESS.withName("lpMinimumApplicationAddress"),

                ValueLayout.ADDRESS.withName("lpMaximumApplicationAddress"),

                ValueLayout.JAVA_LONG.withName("dwActiveProcessorMask"),

                ValueLayout.JAVA_INT.withName("dwNumberOfProcessors"),

                ValueLayout.JAVA_INT.withName("dwProcessorType"),

                ValueLayout.JAVA_INT.withName("dwAllocationGranularity"),

                ValueLayout.JAVA_SHORT.withName("wProcessorLevel"),

                ValueLayout.JAVA_SHORT.withName("wProcessorRevision")
        );
        MemorySegment SYSTEM_INFO_SEGMENT = arena.allocate(SYSTEM_INFO_LAYOUT);
        getSystemInfo.invokeExact(SYSTEM_INFO_SEGMENT);

        return new System_Info(

        );
    }
}
