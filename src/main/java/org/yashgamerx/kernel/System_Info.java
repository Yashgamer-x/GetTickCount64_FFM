package org.yashgamerx.kernel;

import org.yashgamerx.kernel.oem.OemId;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;

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

    public static MemoryLayout.PathElement oemIdElement = MemoryLayout.PathElement.groupElement("oemId");
    public static MemoryLayout.PathElement dwOemIdElement = MemoryLayout.PathElement.groupElement("dwOemId");
    public static MemoryLayout.PathElement wProcessorArchitectureElement = MemoryLayout.PathElement.groupElement("wProcessorArchitecture");
    public static MemoryLayout.PathElement wReservedElement = MemoryLayout.PathElement.groupElement("wReserved");
    public static MemoryLayout.PathElement dwPageSizeElement = MemoryLayout.PathElement.groupElement("dwPageSize");
    public static MemoryLayout.PathElement lpMinimumApplicationAddressElement = MemoryLayout.PathElement.groupElement("lpMinimumApplicationAddress");
    public static MemoryLayout.PathElement lpMaximumApplicationAddressElement = MemoryLayout.PathElement.groupElement("lpMaximumApplicationAddress");
    public static MemoryLayout.PathElement dwActiveProcessorMaskElement = MemoryLayout.PathElement.groupElement("dwActiveProcessorMask");
    public static MemoryLayout.PathElement dwNumberOfProcessorsElement = MemoryLayout.PathElement.groupElement("dwNumberOfProcessors");
    public static MemoryLayout.PathElement dwProcessorTypeElement = MemoryLayout.PathElement.groupElement("dwProcessorType");
    public static MemoryLayout.PathElement dwAllocationGranularityElement = MemoryLayout.PathElement.groupElement("dwAllocationGranularity");
    public static MemoryLayout.PathElement wProcessorLevelElement = MemoryLayout.PathElement.groupElement("wProcessorLevel");
    public static MemoryLayout.PathElement wProcessorRevisionElement = MemoryLayout.PathElement.groupElement("wProcessorRevision");

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
