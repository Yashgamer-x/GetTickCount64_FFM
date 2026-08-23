package org.yashgamerx.kernel;

import org.yashgamerx.kernel.errhandlingapi.GetLastError;
import org.yashgamerx.kernel.errhandlingapi.SetLastError;
import org.yashgamerx.kernel.oem.System_Info;
import org.yashgamerx.kernel.fileapi.GetFileAttributeW;
import org.yashgamerx.kernel.fileapi.GetLogicalDrives;
import org.yashgamerx.kernel.processenv.GetEnvironmentVariableW;
import org.yashgamerx.kernel.processthreadsapi.GetProcessTimes;
import org.yashgamerx.kernel.sysinfoapi.*;
import org.yashgamerx.kernel.timezoneapi.FileTimeToSystemTime;
import java.lang.foreign.*;

public class Kernel32 {

    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();
    private final GetTickCount64 getTickCount64;
    private final GetFileAttributeW getFileAttributeW;
    private final GetLogicalDrives getLogicalDrives;
    private final GetSystemInfo getSystemInfo;
    private final GetNativeSystemInfo getNativeSystemInfo;
    private final GetCurrentProcess getCurrentProcess;
    private final GetProcessId getProcessId;
    private final GetProcessTimes getProcessTimes;
    private final FileTimeToSystemTime fileTimeToSystemTime;
    private final GetSystemTimeAsFileTime getSystemTimeAsFileTime;
    private final GetLastError getLastError;
    private final SetLastError setLastError;
    private final GetEnvironmentVariableW getEnvironmentVariableW;

    public Kernel32(Arena arena) {
        this.kernel32 = SymbolLookup.libraryLookup(
                "kernel32.dll", arena
        );

        getTickCount64 = new GetTickCount64(kernel32);
        getFileAttributeW = new GetFileAttributeW(arena, kernel32);
        getLogicalDrives = new GetLogicalDrives(kernel32);
        getSystemInfo = new GetSystemInfo(arena, kernel32);
        getNativeSystemInfo = new GetNativeSystemInfo(arena, kernel32);
        getCurrentProcess = new GetCurrentProcess(kernel32);
        getProcessId = new GetProcessId(kernel32);
        getProcessTimes = new GetProcessTimes(kernel32);
        fileTimeToSystemTime = new FileTimeToSystemTime(kernel32);
        getSystemTimeAsFileTime = new GetSystemTimeAsFileTime(kernel32);
        getLastError = new GetLastError(kernel32);
        setLastError = new SetLastError(kernel32);
        getEnvironmentVariableW = new GetEnvironmentVariableW(kernel32);
    }

    public long getTickCount64() throws Throwable {
        return getTickCount64.invoke();
    }

    public int getFileAttributesW(String path) throws Throwable {
        return getFileAttributeW.invoke(path);
    }

    public int getLogicalDrives() throws Throwable {
        return getLogicalDrives.invoke();
    }

    public System_Info getSystemInfo() throws Throwable {
        return getSystemInfo.invoke();
    }

    public System_Info getNativeSystemInfo() throws Throwable {
        return getNativeSystemInfo.invoke();
    }

    public MemorySegment getCurrentProcess() throws Throwable {
        return getCurrentProcess.invoke();
    }

    public int getProcessId(MemorySegment process) throws Throwable {
        return getProcessId.invoke(process);
    }

    public int getProcessTimes(
            MemorySegment process,
            MemorySegment lpCreationTimeMemorySegment,
            MemorySegment lpExitTimeMemorySegment,
            MemorySegment lpKernelTimeMemorySegment,
            MemorySegment lpUserTimeMemorySegment
    ) throws Throwable {
        return getProcessTimes.invoke(
                process,
                lpCreationTimeMemorySegment,
                lpExitTimeMemorySegment,
                lpKernelTimeMemorySegment,
                lpUserTimeMemorySegment
        );
    }

    /// @param lpFileTime \[IN] FILETIME -> lpFileTime is a pointer to a FILETIME structure that contains the time to be converted.
    /// @param lpSystemTime \[OUT] LPSYSTEMTIM -> lpSystemTime is a pointer to a SYSTEMTIME structure that receives the converted time.
    /// @return BOOL -> int in java result is either 0 on failed execution or 1 on successful execution
    public int fileTimeToSystemTime(MemorySegment lpFileTime, MemorySegment lpSystemTime) throws Throwable {
        return fileTimeToSystemTime.invoke(lpFileTime, lpSystemTime);
    }

    public void getSystemTimeAsFileTime(MemorySegment lpSystemTimeAsFileTime) throws Throwable {
        getSystemTimeAsFileTime.invoke(lpSystemTimeAsFileTime);
    }

    public int getLastError() throws Throwable {
        return getLastError.invoke();
    }

    public void setLastError(int dwErrCode) throws Throwable {
        setLastError.invoke(dwErrCode);
    }

    public int getEnvironmentVariable(MemorySegment lpName, MemorySegment lpBuffer, int size) throws Throwable {
        return  getEnvironmentVariableW.invoke(lpName, lpBuffer, size);
    }
}
