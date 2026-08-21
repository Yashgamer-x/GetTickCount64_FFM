package org.yashgamerx;

import org.yashgamerx.advapi.Advapi32;
import org.yashgamerx.kernel.Kernel32;

import java.lang.foreign.*;

public class Main {
    static void main() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var process = kernel32.getCurrentProcess();
            System.out.println(kernel32.getProcessId(process));
        } catch (Throwable e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}
