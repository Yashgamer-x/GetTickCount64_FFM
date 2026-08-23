package org.yashgamerx.kernel.timezoneapi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class FileTimeToSystemTime {

    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();
    private final MethodHandle fileTimeToSystemTime;

    public FileTimeToSystemTime(SymbolLookup kernel32) {
        this.kernel32 = kernel32;
        this.fileTimeToSystemTime = createFileTimeToSystemTime();
    }

    private MethodHandle createFileTimeToSystemTime() {
        MemorySegment fileTimeToSystemTime_addr = kernel32.find("FileTimeToSystemTime")
                .orElseThrow();
        return linker.downcallHandle(
                fileTimeToSystemTime_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // BOOL
                        ValueLayout.ADDRESS, // FILETIME - *lpFileTime
                        ValueLayout.ADDRESS // LPSYSTEMTIM lpSystemTime
                )
        );
    }

    /// @param lpFileTime \[IN] FILETIME -> lpFileTime is a pointer to a FILETIME structure that contains the time to be converted.
    /// @param lpSystemTime \[OUT] LPSYSTEMTIM -> lpSystemTime is a pointer to a SYSTEMTIME structure that receives the converted time.
    /// @return BOOL -> int in java result is either 0 on failed execution or 1 on successful execution
    public int invoke(MemorySegment lpFileTime, MemorySegment lpSystemTime) throws Throwable {
        return (int) fileTimeToSystemTime.invokeExact(lpFileTime, lpSystemTime);
    }

}
