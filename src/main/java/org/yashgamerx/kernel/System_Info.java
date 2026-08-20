package org.yashgamerx.kernel;

import java.lang.foreign.MemorySegment;

public record System_Info(
        int dwOemId,
        short wProcessorArchitecture,
        short wReserved,
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
}
