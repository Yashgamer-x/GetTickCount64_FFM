package org.yashgamerx;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Main {
    static void main() {
        try (Arena arena = Arena.ofConfined()) {
            Linker linker = Linker.nativeLinker();
            SymbolLookup kernel32 = SymbolLookup.libraryLookup(
                    "kernel32.dll",
                    arena
            );
            MemorySegment getTickCount64_addr = kernel32.find("GetTickCount64")
                    .orElseThrow();
            MethodHandle getTickCount64 = linker.downcallHandle(
                    getTickCount64_addr,
                    FunctionDescriptor.of(ValueLayout.JAVA_LONG)
            );
            long tickCount = (long) getTickCount64.invokeExact();

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
