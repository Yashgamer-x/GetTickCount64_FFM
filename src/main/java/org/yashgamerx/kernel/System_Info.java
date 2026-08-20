package org.yashgamerx.kernel;

import org.yashgamerx.kernel.oem.OemId;

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
}
