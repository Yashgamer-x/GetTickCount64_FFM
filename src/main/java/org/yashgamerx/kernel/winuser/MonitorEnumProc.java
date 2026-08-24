package org.yashgamerx.kernel.winuser;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class MonitorEnumProc {
    private final Linker linker = Linker.nativeLinker();

    private static final FunctionDescriptor CALLBACK_DESCRIPTOR =
            FunctionDescriptor.of(
                    ValueLayout.JAVA_INT, // BOOL
                    ValueLayout.ADDRESS,  // HMONITOR unnamedParam1,
                    ValueLayout.ADDRESS,  // HDC unnamedParam2,
                    ValueLayout.ADDRESS,  // LPRECT unnamedParam3,
                    ValueLayout.JAVA_LONG // LPARAM unnamedParam4
            );

    public int callback(MemorySegment hMonitor, MemorySegment hdc, MemorySegment lprc, long dwData) {
        System.out.println("hMonitor: " + hMonitor);
        System.out.println("hdc: " + hdc);
        System.out.println("lprc: " + lprc);
        System.out.println("dwData: " + dwData);
        System.out.println("----------------------------------------");
        return 1;
    }

    private MethodHandle getCallbackHandle() throws NoSuchMethodException, IllegalAccessException {
        return MethodHandles.lookup().findVirtual(
                MonitorEnumProc.class,
                "callback",
                CALLBACK_DESCRIPTOR.toMethodType()
        ).bindTo(this);
    }

    public MemorySegment createCallback(Arena arena) throws Throwable {
        MethodHandle callbackHandle = getCallbackHandle();

        return linker.upcallStub(
                callbackHandle,
                CALLBACK_DESCRIPTOR,
                arena
        );
    }
}
