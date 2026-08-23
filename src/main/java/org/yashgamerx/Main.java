package org.yashgamerx;

import org.yashgamerx.kernel.Kernel32;
import org.yashgamerx.kernel.time.LpFileTime;
import org.yashgamerx.kernel.time.SystemTime;

import java.lang.foreign.*;

public class Main {
    static void main() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var systemInfo = kernel32.getSystemInfo();
            System.out.println(systemInfo);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
