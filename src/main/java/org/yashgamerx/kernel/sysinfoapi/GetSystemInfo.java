package org.yashgamerx.kernel.sysinfoapi;

import org.yashgamerx.kernel.oem.OemId;
import org.yashgamerx.kernel.oem.ProcessorArchitecture;
import org.yashgamerx.kernel.oem.System_Info;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class GetSystemInfo {
    private final Arena arena;
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public GetSystemInfo(
            Arena arena,
            SymbolLookup kernel32
    ) {
        this.arena = arena;
        this.kernel32 = kernel32;
        this.methodHandle = createGetSystemInfo();
    }

    private MethodHandle createGetSystemInfo(){
        MemorySegment getSystemInfo_addr = kernel32.find("GetSystemInfo")
                .orElseThrow();
        return linker.downcallHandle(
                getSystemInfo_addr,
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
        );
    }

    public System_Info invoke() throws Throwable {
        MemoryLayout SYSTEM_INFO_LAYOUT = System_Info.SYSTEM_INFO_LAYOUT;
        MemorySegment SYSTEM_INFO_SEGMENT = arena.allocate(SYSTEM_INFO_LAYOUT);
        methodHandle.invokeExact(SYSTEM_INFO_SEGMENT);

        int dwOemId = (int) System_Info.DW_OEM_ID_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        short wProcessorArchitecture = (short) System_Info.W_PROCESSOR_ARCHITECTURE_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        short wReserved = (short) System_Info.W_RESERVED_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        int dwPageSize = (int) System_Info.DW_PAGE_SIZE_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        MemorySegment lpMinimumApplicationAddress =
                (MemorySegment) System_Info.LP_MINIMUM_APPLICATION_ADDRESS_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        MemorySegment lpMaximumApplicationAddress =
                (MemorySegment) System_Info.LP_MAXIMUM_APPLICATION_ADDRESS_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        long dwActiveProcessorMask = (long) System_Info.DW_ACTIVE_PROCESSOR_MASK_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        int dwNumberOfProcessors = (int) System_Info.DW_NUMBER_OF_PROCESSORS_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        int dwProcessorType = (int) System_Info.DW_PROCESSOR_TYPE_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        int dwAllocationGranularity = (int) System_Info.DW_ALLOCATION_GRANULARITY_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        short wProcessorLevel = (short) System_Info.W_PROCESSOR_LEVEL_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        short wProcessorRevision = (short) System_Info.W_PROCESSOR_REVISION_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);

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
