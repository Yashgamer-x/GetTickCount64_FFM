package org.yashgamerx.kernel;

import org.yashgamerx.kernel.oem.OemId;

import java.lang.foreign.AddressLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public record System_Info(
        OemId oemId,
        int dwPageSize,
        MemorySegment lpMinimumApplicationAddress,
        MemorySegment lpMaximumApplicationAddress,
        long dwActiveProcessorMask,
        int dwNumberOfProcessors,
        int dwProcessorType,
        int dwAllocationGranularity,
        short wProcessorLevel,
        short wProcessorRevision
) {

    // Value Layouts of System_Info
    public static final ValueLayout.OfInt DW_PAGE_SIZE_VALUE = ValueLayout.JAVA_INT.withName("dwPageSize");
    public static final ValueLayout.OfInt DW_OEM_ID_VALUE = ValueLayout.JAVA_INT.withName("dwOemId");
    public static final ValueLayout.OfShort W_PROCESSOR_ARCHITECTURE_VALUE = ValueLayout.JAVA_SHORT.withName("wProcessorArchitecture");
    public static final ValueLayout.OfShort W_RESERVED_VALUE = ValueLayout.JAVA_SHORT.withName("wReserved");
    public static final AddressLayout LP_MINIMUM_APPLICATION_ADDRESS_VALUE = AddressLayout.ADDRESS.withName("lpMinimumApplicationAddress");
    public static final AddressLayout LP_MAXIMUM_APPLICATION_ADDRESS_VALUE = AddressLayout.ADDRESS.withName("lpMaximumApplicationAddress");
    public static final ValueLayout.OfLong DW_ACTIVE_PROCESSOR_MASK_VALUE = ValueLayout.JAVA_LONG.withName("dwActiveProcessorMask");
    public static final ValueLayout.OfInt DW_NUMBER_OF_PROCESSORS_VALUE = ValueLayout.JAVA_INT.withName("dwNumberOfProcessors");
    public static final ValueLayout.OfInt DW_PROCESSOR_TYPE_VALUE = ValueLayout.JAVA_INT.withName("dwProcessorType");
    public static final ValueLayout.OfInt DW_ALLOCATION_GRANULARITY_VALUE = ValueLayout.JAVA_INT.withName("dwAllocationGranularity");
    public static final ValueLayout.OfShort W_PROCESSOR_LEVEL_VALUE = ValueLayout.JAVA_SHORT.withName("wProcessorLevel");
    public static final ValueLayout.OfShort W_PROCESSOR_REVISION_VALUE = ValueLayout.JAVA_SHORT.withName("wProcessorRevision");

    // MemoryLayouts
    private static final MemoryLayout PROCESSOR_ARCHITECTURE_LAYOUT = MemoryLayout.structLayout(
            W_PROCESSOR_ARCHITECTURE_VALUE,
            W_RESERVED_VALUE
    );

    private static final MemoryLayout OEM_LAYOUT =
            MemoryLayout.unionLayout(
                    DW_OEM_ID_VALUE,
                    PROCESSOR_ARCHITECTURE_LAYOUT.withName("processorArchitecture")
            ).withName("oemId");

    public static final MemoryLayout SYSTEM_INFO_LAYOUT = MemoryLayout.structLayout(
            OEM_LAYOUT, // oemId
            DW_PAGE_SIZE_VALUE,
            LP_MINIMUM_APPLICATION_ADDRESS_VALUE,
            LP_MAXIMUM_APPLICATION_ADDRESS_VALUE,
            DW_ACTIVE_PROCESSOR_MASK_VALUE,
            DW_NUMBER_OF_PROCESSORS_VALUE,
            DW_PROCESSOR_TYPE_VALUE,
            DW_ALLOCATION_GRANULARITY_VALUE,
            W_PROCESSOR_LEVEL_VALUE,
            W_PROCESSOR_REVISION_VALUE
    );

    // MemoryLayout PathElements of System_Info
    public static final MemoryLayout.PathElement OEM_ID_ELEMENT = MemoryLayout.PathElement.groupElement("oemId");
    public static final MemoryLayout.PathElement DW_OEM_ID_ELEMENT = MemoryLayout.PathElement.groupElement("dwOemId");
    public static final MemoryLayout.PathElement PROCESSOR_ARCHITECTURE_ELEMENT = MemoryLayout.PathElement.groupElement("processorArchitecture");
    public static final MemoryLayout.PathElement W_PROCESSOR_ARCHITECTURE_ELEMENT = MemoryLayout.PathElement.groupElement("wProcessorArchitecture");
    public static final MemoryLayout.PathElement W_RESERVED_ELEMENT = MemoryLayout.PathElement.groupElement("wReserved");
    public static final MemoryLayout.PathElement DW_PAGE_SIZE_ELEMENT = MemoryLayout.PathElement.groupElement("dwPageSize");
    public static final MemoryLayout.PathElement LP_MINIMUM_APPLICATION_ADDRESS_ELEMENT = MemoryLayout.PathElement.groupElement("lpMinimumApplicationAddress");
    public static final MemoryLayout.PathElement LP_MAXIMUM_APPLICATION_ADDRESS_ELEMENT = MemoryLayout.PathElement.groupElement("lpMaximumApplicationAddress");
    public static final MemoryLayout.PathElement DW_ACTIVE_PROCESSOR_MASK_ELEMENT = MemoryLayout.PathElement.groupElement("dwActiveProcessorMask");
    public static final MemoryLayout.PathElement DW_NUMBER_OF_PROCESSORS_ELEMENT = MemoryLayout.PathElement.groupElement("dwNumberOfProcessors");
    public static final MemoryLayout.PathElement DW_PROCESSOR_TYPE_ELEMENT = MemoryLayout.PathElement.groupElement("dwProcessorType");
    public static final MemoryLayout.PathElement DW_ALLOCATION_GRANULARITY_ELEMENT = MemoryLayout.PathElement.groupElement("dwAllocationGranularity");
    public static final MemoryLayout.PathElement W_PROCESSOR_LEVEL_ELEMENT = MemoryLayout.PathElement.groupElement("wProcessorLevel");
    public static final MemoryLayout.PathElement W_PROCESSOR_REVISION_ELEMENT = MemoryLayout.PathElement.groupElement("wProcessorRevision");

    // VarHandles for System_Info
    public static final VarHandle DW_OEM_ID_VAR_HANDLE = SYSTEM_INFO_LAYOUT
            .varHandle(System_Info.OEM_ID_ELEMENT, System_Info.DW_OEM_ID_ELEMENT);
    public static final VarHandle W_PROCESSOR_ARCHITECTURE_VAR_HANDLE = SYSTEM_INFO_LAYOUT
            .varHandle(System_Info.OEM_ID_ELEMENT, System_Info.PROCESSOR_ARCHITECTURE_ELEMENT, System_Info.W_PROCESSOR_ARCHITECTURE_ELEMENT);
    public static final VarHandle W_RESERVED_VAR_HANDLE = SYSTEM_INFO_LAYOUT
            .varHandle(System_Info.OEM_ID_ELEMENT, System_Info.PROCESSOR_ARCHITECTURE_ELEMENT, System_Info.W_RESERVED_ELEMENT);
    public static final VarHandle DW_PAGE_SIZE_VAR_HANDLE = SYSTEM_INFO_LAYOUT
            .varHandle(System_Info.DW_PAGE_SIZE_ELEMENT);
    public static final VarHandle LP_MINIMUM_APPLICATION_ADDRESS_VAR_HANDLE = SYSTEM_INFO_LAYOUT
            .varHandle(System_Info.LP_MINIMUM_APPLICATION_ADDRESS_ELEMENT);
    public static final VarHandle LP_MAXIMUM_APPLICATION_ADDRESS_VAR_HANDLE = SYSTEM_INFO_LAYOUT
            .varHandle(System_Info.LP_MAXIMUM_APPLICATION_ADDRESS_ELEMENT);
    public static final VarHandle DW_ACTIVE_PROCESSOR_MASK_VAR_HANDLE = SYSTEM_INFO_LAYOUT
            .varHandle(System_Info.DW_ACTIVE_PROCESSOR_MASK_ELEMENT);
    public static final VarHandle DW_NUMBER_OF_PROCESSORS_VAR_HANDLE = SYSTEM_INFO_LAYOUT
            .varHandle(System_Info.DW_NUMBER_OF_PROCESSORS_ELEMENT);
    public static final VarHandle DW_PROCESSOR_TYPE_VAR_HANDLE = SYSTEM_INFO_LAYOUT
            .varHandle(System_Info.DW_PROCESSOR_TYPE_ELEMENT);
    public static final VarHandle DW_ALLOCATION_GRANULARITY_VAR_HANDLE = SYSTEM_INFO_LAYOUT
            .varHandle(System_Info.DW_ALLOCATION_GRANULARITY_ELEMENT);
    public static final VarHandle W_PROCESSOR_LEVEL_VAR_HANDLE = SYSTEM_INFO_LAYOUT
            .varHandle(System_Info.W_PROCESSOR_LEVEL_ELEMENT);
    public static final VarHandle W_PROCESSOR_REVISION_VAR_HANDLE = SYSTEM_INFO_LAYOUT
            .varHandle(System_Info.W_PROCESSOR_REVISION_ELEMENT);

    @Override
    public String toString() {
        return """
            System Information:
              OEM / Processor Architecture: %s
              Page Size: %d bytes
              Minimum Application Address: %s
              Maximum Application Address: %s
              Active Processor Mask: 0x%016X
              Number of Processors: %d
              Processor Type: %d
              Allocation Granularity: %d bytes
              Processor Level: %d
              Processor Revision: %d
            """.formatted(
                oemId,
                dwPageSize,
                formatAddress(lpMinimumApplicationAddress),
                formatAddress(lpMaximumApplicationAddress),
                dwActiveProcessorMask,
                dwNumberOfProcessors,
                dwProcessorType,
                dwAllocationGranularity,
                Short.toUnsignedInt(wProcessorLevel),
                Short.toUnsignedInt(wProcessorRevision)
        );
    }

    private static String formatAddress(MemorySegment address) {
        return "0x%016X".formatted(address.address());
    }
}
