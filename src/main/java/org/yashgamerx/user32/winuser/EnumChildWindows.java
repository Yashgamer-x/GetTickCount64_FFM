package org.yashgamerx.user32.winuser;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class EnumChildWindows {

    private final MethodHandle methodHandle;
    private final SymbolLookup user32;
    private final Linker linker = Linker.nativeLinker();

    public EnumChildWindows(SymbolLookup user32) {
        this.user32 = user32;
        this.methodHandle = createEnumChildWindows();
    }

    private MethodHandle createEnumChildWindows() {
        MemorySegment EnumChildWindows_addr = user32.find("EnumChildWindows")
                .orElseThrow();
        return linker.downcallHandle(
                EnumChildWindows_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // BOOL
                        ValueLayout.ADDRESS,  // [in, optional] HWND        hWndParent
                        ValueLayout.ADDRESS,  // [in]           WNDENUMPROC lpEnumFunc,
                        ValueLayout.JAVA_LONG // [in]           LPARAM      lParam
                )
        );
    }

    public int invoke(MemorySegment hWndParent, MemorySegment lpEnumFunc, long lParam) throws Throwable {
        return (int) methodHandle.invokeExact(hWndParent, lpEnumFunc, lParam);
    }
}
