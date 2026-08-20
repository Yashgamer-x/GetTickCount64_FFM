package org.yashgamerx;

import java.lang.foreign.*;

public class Main {
    static void main() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            long tickCount = kernel32.getTickCount64();

            long milliseconds = tickCount;

            long days = milliseconds / 86_400_000;
            milliseconds %= 86_400_000;

            long hours = milliseconds / 3_600_000;
            milliseconds %= 3_600_000;

            long minutes = milliseconds / 60_000;
            milliseconds %= 60_000;

            long seconds = milliseconds / 1_000;
            milliseconds %= 1_000;

            System.out.printf(
                    "Windows has been running for %d days, %d hours, %d minutes, %d seconds, %d milliseconds%n",
                    days, hours, minutes, seconds, milliseconds
            );
        } catch (Throwable e) {
            System.out.println(e.toString());
        }
    }
}
