package org.yashgamerx;

import org.yashgamerx.advapi.Advapi32;
import org.yashgamerx.kernel.Kernel32;

import java.lang.foreign.*;

public class Main {
    static void main() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            System.out.println(kernel32.getSystemInfo());
        } catch (Throwable e) {
            System.out.println(e.toString());
        }
    }
}
