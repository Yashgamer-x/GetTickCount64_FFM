package org.yashgamerx.user32.winuser;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class EnumWindows {
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public EnumWindows(SymbolLookup user32) {
        this.kernel32 = user32;
        methodHandle = createEnumWindows();
    }

    private MethodHandle createEnumWindows() {
        MemorySegment EnumWindows_addr = kernel32.find("EnumWindows")
                .orElseThrow();
        return linker.downcallHandle(
                EnumWindows_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // BOOL
                        ValueLayout.ADDRESS,  // [in] WNDENUMPROC lpEnumFunc,
                        ValueLayout.JAVA_LONG // [in] LPARAM      lParam
                )
        );
    }

    public int invoke(MemorySegment enumWindowsProc, long lParam) throws Throwable {
        return (int) methodHandle.invokeExact(enumWindowsProc, lParam);
    }

}
