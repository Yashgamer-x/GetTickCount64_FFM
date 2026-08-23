package org.yashgamerx;

import org.yashgamerx.kernel.Kernel32;
import org.yashgamerx.kernel.time.LpFileTime;
import org.yashgamerx.kernel.time.SystemTime;

import java.lang.foreign.*;

public class Main {
    static void main() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var lpSystemTimeAsFileTimeMemorySegment = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            kernel32.getSystemTimeAsFileTime(lpSystemTimeAsFileTimeMemorySegment);
            var lpSystemTimeMemorySegment = arena.allocate(SystemTime.SYSTEM_TIME_LAYOUT);
            var _ = kernel32.fileTimeToSystemTime(
                    lpSystemTimeAsFileTimeMemorySegment,
                    lpSystemTimeMemorySegment
            );
            var lpSystemTime = SystemTime.of(lpSystemTimeMemorySegment);
            System.out.println("Current System Time "+lpSystemTime);
        } catch (Throwable e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}
