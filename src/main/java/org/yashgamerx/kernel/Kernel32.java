package org.yashgamerx.kernel;

import org.yashgamerx.kernel.oem.OemId;
import org.yashgamerx.kernel.oem.ProcessorArchitecture;

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
        MemoryLayout SYSTEM_INFO_LAYOUT = System_Info.SYSTEM_INFO_LAYOUT;
        MemorySegment SYSTEM_INFO_SEGMENT = arena.allocate(SYSTEM_INFO_LAYOUT);
        getSystemInfo.invokeExact(SYSTEM_INFO_SEGMENT);

        int dwOemId = (int) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.OEM_ID_ELEMENT, System_Info.DW_OEM_ID_ELEMENT)
                .get(SYSTEM_INFO_SEGMENT, 0L);
        short wProcessorArchitecture = (short) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.OEM_ID_ELEMENT, System_Info.PROCESSOR_ARCHITECTURE_ELEMENT, System_Info.W_PROCESSOR_ARCHITECTURE_ELEMENT)
                .get(SYSTEM_INFO_SEGMENT, 0L);
        short wReserved = (short) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.OEM_ID_ELEMENT, System_Info.PROCESSOR_ARCHITECTURE_ELEMENT, System_Info.W_RESERVED_ELEMENT)
                .get(SYSTEM_INFO_SEGMENT, 0L);
        int dwPageSize = (int) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.DW_PAGE_SIZE_ELEMENT)
                .get(SYSTEM_INFO_SEGMENT, 0L);
        MemorySegment lpMinimumApplicationAddress = (MemorySegment) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.LP_MINIMUM_APPLICATION_ADDRESS_ELEMENT)
                .get(SYSTEM_INFO_SEGMENT, 0L);
        MemorySegment lpMaximumApplicationAddress = (MemorySegment) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.LP_MAXIMUM_APPLICATION_ADDRESS_ELEMENT)
                .get(SYSTEM_INFO_SEGMENT, 0L);
        long dwActiveProcessorMask = (long) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.DW_ACTIVE_PROCESSOR_MASK_ELEMENT)
                .get(SYSTEM_INFO_SEGMENT, 0L);
        int dwNumberOfProcessors = (int) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.DW_NUMBER_OF_PROCESSORS_ELEMENT)
                .get(SYSTEM_INFO_SEGMENT, 0L);
        int dwProcessorType = (int) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.DW_PROCESSOR_TYPE_ELEMENT)
                .get(SYSTEM_INFO_SEGMENT, 0L);
        int dwAllocationGranularity = (int) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.DW_ALLOCATION_GRANULARITY_ELEMENT)
                .get(SYSTEM_INFO_SEGMENT, 0L);
        short wProcessorLevel = (short) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.W_PROCESSOR_LEVEL_ELEMENT)
                .get(SYSTEM_INFO_SEGMENT, 0L);
        short wProcessorRevision = (short) SYSTEM_INFO_LAYOUT
                .varHandle(System_Info.W_PROCESSOR_REVISION_ELEMENT)
                .get(SYSTEM_INFO_SEGMENT, 0L);

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
