package org.yashgamerx.kernel;

import org.yashgamerx.kernel.oem.OemId;
import org.yashgamerx.kernel.oem.ProcessorArchitecture;
import org.yashgamerx.kernel.oem.System_Info;
import org.yashgamerx.kernel.fileapi.GetFileAttributeW;
import org.yashgamerx.kernel.fileapi.GetLogicalDrives;
import org.yashgamerx.kernel.sysinfoapi.GetCurrentProcess;
import org.yashgamerx.kernel.sysinfoapi.GetNativeSystemInfo;
import org.yashgamerx.kernel.sysinfoapi.GetSystemInfo;
import org.yashgamerx.kernel.sysinfoapi.GetTickCount64;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Kernel32 {

    private final Arena arena;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();
    private final GetTickCount64 getTickCount64;
    private final GetFileAttributeW getFileAttributeW;
    private final GetLogicalDrives getLogicalDrives;
    private final GetSystemInfo getSystemInfo;
    private final GetNativeSystemInfo getNativeSystemInfo;
    private final GetCurrentProcess getCurrentProcess;
    private final MethodHandle getProcessId;
    private final MethodHandle getProcessTimes;
    private final MethodHandle fileTimeToSystemTime;
    private final MethodHandle getSystemTimeAsFileTime;

    public Kernel32(Arena arena) {
        this.arena = arena;
        this.kernel32 = SymbolLookup.libraryLookup(
                "kernel32.dll", arena
        );

        getTickCount64 = new GetTickCount64(kernel32);
        getFileAttributeW = new GetFileAttributeW(arena, kernel32);
        getLogicalDrives = new GetLogicalDrives(kernel32);
        getSystemInfo = new GetSystemInfo(arena, kernel32);
        getNativeSystemInfo = new GetNativeSystemInfo(arena, kernel32);
        getCurrentProcess = new GetCurrentProcess(kernel32);
        getProcessId = createGetProcessId();
        getProcessTimes = createGetProcessTimes();
        fileTimeToSystemTime = createFileTimeToSystemTime();
        getSystemTimeAsFileTime = createGetSystemTimeAsFileTime();
    }

    private MethodHandle createGetProcessId() {
        MemorySegment getProcessId_addr = kernel32.find("GetProcessId")
                .orElseThrow();
        return linker.downcallHandle(
                getProcessId_addr,
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
        );
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

    private MethodHandle createGetProcessTimes() {
        MemorySegment getProcessTimes_addr = kernel32.find("GetProcessTimes")
                .orElseThrow();
        return linker.downcallHandle(
                getProcessTimes_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // BOOL is a typedef for INT in C
                        ValueLayout.ADDRESS,  // HANDLE - hprocess;
                        ValueLayout.ADDRESS,  // LPFILETIME - lpCreationTime
                        ValueLayout.ADDRESS,  // LPFILETIME - lpExitTime
                        ValueLayout.ADDRESS,  // LPFILETIME - lpKernelTime
                        ValueLayout.ADDRESS   // LPFILETIME - lpUserTime
                )
        );
    }

    private MethodHandle createGetSystemTimeAsFileTime() {
        MemorySegment getSystemTimeAsFileTime_addr = kernel32.find("GetSystemTimeAsFileTime")
                .orElseThrow();
        return linker.downcallHandle(
                getSystemTimeAsFileTime_addr,
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
        );
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
        return (int) getProcessId.invokeExact(process);
    }

    public int getProcessTimes(
            MemorySegment process,
            MemorySegment lpCreationTimeMemorySegment,
            MemorySegment lpExitTimeMemorySegment,
            MemorySegment lpKernelTimeMemorySegment,
            MemorySegment lpUserTimeMemorySegment
    ) throws Throwable {
        return (int) getProcessTimes.invokeExact(
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
    public int fileTimeToSystemTime(
            MemorySegment lpFileTime,
            MemorySegment lpSystemTime
    ) throws Throwable {
        return (int) fileTimeToSystemTime.invokeExact(
                lpFileTime,
                lpSystemTime
        );
    }

    public void getSystemTimeAsFileTime(MemorySegment lpSystemTimeAsFileTime) throws Throwable {
        getSystemTimeAsFileTime.invokeExact(lpSystemTimeAsFileTime);
    }
}
