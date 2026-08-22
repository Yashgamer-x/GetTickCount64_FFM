package org.yashgamerx;

import org.yashgamerx.advapi.Advapi32;
import org.yashgamerx.kernel.Kernel32;
import org.yashgamerx.kernel.filetime.LpFileTime;

import java.lang.foreign.*;

public class Main {
    static void main() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var process = kernel32.getCurrentProcess();
            var lpCreationTimeMemorySegment = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var lpExitTimeMemorySegment = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var lpKernelTimeMemorySegment = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var lpUserTimeMemorySegment = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var processTimes = kernel32.getProcessTimes(
                    process,
                    lpCreationTimeMemorySegment,
                    lpExitTimeMemorySegment,
                    lpKernelTimeMemorySegment,
                    lpUserTimeMemorySegment
            );
            var lpCreationTime = LpFileTime.of(lpCreationTimeMemorySegment);
            var lpExitTime = LpFileTime.of(lpExitTimeMemorySegment);
            var lpKernelTime = LpFileTime.of(lpKernelTimeMemorySegment);
            var lpUserTime = LpFileTime.of(lpUserTimeMemorySegment);
            System.out.println(processTimes);
            System.out.println(lpCreationTime);
            System.out.println(lpExitTime);
            System.out.println(lpKernelTime);
            System.out.println(lpUserTime);
        } catch (Throwable e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}
