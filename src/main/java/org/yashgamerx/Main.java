package org.yashgamerx;

import org.yashgamerx.kernel.Kernel32;
import org.yashgamerx.kernel.time.LpFileTime;
import org.yashgamerx.kernel.time.SystemTime;

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
            var _ = kernel32.getProcessTimes(
                    process,
                    lpCreationTimeMemorySegment,
                    lpExitTimeMemorySegment,
                    lpKernelTimeMemorySegment,
                    lpUserTimeMemorySegment
            );


            var lpCreationSystemTime = arena.allocate(SystemTime.SYSTEM_TIME_LAYOUT);
            var _ = kernel32.fileTimeToSystemTime(
                    lpCreationTimeMemorySegment,
                    lpCreationSystemTime
            );
            var lpCreationTime = SystemTime.of(lpCreationSystemTime);
            System.out.println("Creation "+lpCreationTime);

        } catch (Throwable e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}
