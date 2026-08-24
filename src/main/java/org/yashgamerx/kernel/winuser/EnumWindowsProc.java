package org.yashgamerx.kernel.winuser;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class EnumWindowsProc {
    private final Linker linker = Linker.nativeLinker();

    private static final FunctionDescriptor CALLBACK_DESCRIPTOR =
            FunctionDescriptor.of(
                    ValueLayout.JAVA_INT,   // BOOL
                    ValueLayout.ADDRESS,    // HWND
                    ValueLayout.JAVA_LONG   // LPARAM
            );

    private int callback(MemorySegment hWnd, long lParam) {
        System.out.println("HWND: " + hWnd);
        System.out.println("LPARAM: " + lParam);

        return 1;
    }

    private MethodHandle getCallbackHandle() throws NoSuchMethodException, IllegalAccessException {
        return MethodHandles.lookup().findVirtual(
                EnumWindowsProc.class,
                "callback",
                MethodType.methodType(
                        int.class,
                        MemorySegment.class,
                        long.class
                )
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
