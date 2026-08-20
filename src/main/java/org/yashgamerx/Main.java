package org.yashgamerx;

import java.lang.foreign.*;

public class Main {
    static void main() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            int perm = kernel32.getFileAttributesW("C:\\Windows");
            System.out.println(perm);
        } catch (Throwable e) {
            System.out.println(e.toString());
        }
    }
}
