package org.yashgamerx;

import java.lang.foreign.*;

public class Main {
    static void main() {
        try (Arena arena = Arena.ofConfined()) {
            Advapi32 advapi32 = new Advapi32(arena);
            String perm = advapi32.getUserNameW();
            System.out.println(perm);
        } catch (Throwable e) {
            System.out.println(e.toString());
        }
    }
}
