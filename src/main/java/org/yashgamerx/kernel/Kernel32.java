package org.yashgamerx.kernel;

import org.yashgamerx.kernel.oem.OemId;
import org.yashgamerx.kernel.oem.OemIdValue;
import org.yashgamerx.kernel.oem.ProcessorArchitecture;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
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
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
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

        int dwOemId = (int) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.oemIdElement, System_Info.dwOemIdElement)
                .get(SYSTEM_INFO_SEGMENT);
        short wProcessorArchitecture = (short) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.oemIdElement, System_Info.wProcessorArchitectureElement)
                .get(SYSTEM_INFO_SEGMENT);
        short wReserved = (short) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.oemIdElement, System_Info.wReservedElement)
                .get(SYSTEM_INFO_SEGMENT);
        int dwPageSize = (int) SYSTEM_INFO_LAYOUT.varHandle(System_Info.dwPageSizeElement).get(SYSTEM_INFO_SEGMENT);
        MemorySegment lpMinimumApplicationAddress = (MemorySegment) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.lpMinimumApplicationAddressElement)
                .get(SYSTEM_INFO_SEGMENT);
        MemorySegment lpMaximumApplicationAddress = (MemorySegment) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.lpMaximumApplicationAddressElement)
                .get(SYSTEM_INFO_SEGMENT);
        long dwActiveProcessorMask = (long) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.dwActiveProcessorMaskElement)
                .get(SYSTEM_INFO_SEGMENT);
        int dwNumberOfProcessors = (int) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.dwNumberOfProcessorsElement)
                .get(SYSTEM_INFO_SEGMENT);
        int dwProcessorType = (int) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.dwProcessorTypeElement)
                .get(SYSTEM_INFO_SEGMENT);
        int dwAllocationGranularity = (int) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.dwAllocationGranularityElement)
                .get(SYSTEM_INFO_SEGMENT);
        short wProcessorLevel = (short) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.wProcessorLevelElement)
                .get(SYSTEM_INFO_SEGMENT);
        short wProcessorRevision = (short) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.wProcessorRevisionElement)
                .get(SYSTEM_INFO_SEGMENT);

        OemId oemId = getOemId(dwOemId, wProcessorArchitecture, wReserved);

        return new System_Info(
                oemId,
                dwPageSize,
                lpMinimumApplicationAddress,
                lpMaximumApplicationAddress,
                dwActiveProcessorMask,
                dwNumberOfProcessors,
                dwProcessorType,
                dwAllocationGranularity,
                wProcessorLevel,
                wProcessorRevision
        );
    }

    private static OemId getOemId(int dwOemId, short wProcessorArchitecture, short wReserved) {
        return switch (Short.toUnsignedInt(wProcessorArchitecture)) {
                case 0, 5, 6, 9, 12, 0xFFFF ->
                        new ProcessorArchitecture(wProcessorArchitecture, wReserved);

                default ->
                        throw new IllegalStateException(
                                "Unknown processor architecture: " +Short.toUnsignedInt(wProcessorArchitecture)
                        );
            };
    }
}
