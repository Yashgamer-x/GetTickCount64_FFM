package org.yashgamerx.kernel.errhandlingapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class SetLastError {
    private final MethodHandle methodHandle;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();

    public SetLastError(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        this.methodHandle = createGetLastError();
    }

    private MethodHandle createGetLastError() {
        MemorySegment SetLastError_addr = kernel32.find("SetLastError")
                .orElseThrow();
        return linker.downcallHandle(
                SetLastError_addr,
                FunctionDescriptor.ofVoid(
                        ValueLayout.JAVA_INT
                )
        );
    }

    public void invoke(int dwErrCode) throws Throwable {
        methodHandle.invokeExact(dwErrCode);
    }
}
