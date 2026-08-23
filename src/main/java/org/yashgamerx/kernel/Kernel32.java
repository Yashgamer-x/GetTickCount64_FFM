package org.yashgamerx.kernel;

import org.yashgamerx.kernel.oem.OemId;
import org.yashgamerx.kernel.oem.ProcessorArchitecture;
import org.yashgamerx.kernel.oem.System_Info;
import org.yashgamerx.kernel.fileapi.GetFileAttributeW;
import org.yashgamerx.kernel.fileapi.GetLogicalDrives;
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
    private final MethodHandle getSystemInfo;
    private final MethodHandle getNativeSystemInfo;
    private final MethodHandle getCurrentProcess;
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
        getSystemInfo = createGetSystemInfo();
        getNativeSystemInfo = createGetNativeSystemInfo();
        getCurrentProcess = createGetCurrentProcess();
        getProcessId = createGetProcessId();
        getProcessTimes = createGetProcessTimes();
        fileTimeToSystemTime = createFileTimeToSystemTime();
        getSystemTimeAsFileTime = createGetSystemTimeAsFileTime();
    }

    private MethodHandle createGetSystemInfo(){
        MemorySegment getSystemInfo_addr = kernel32.find("GetSystemInfo")
                .orElseThrow();
        return linker.downcallHandle(
                getSystemInfo_addr,
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
        );
    }

    private MethodHandle createGetNativeSystemInfo() {
        MemorySegment getSystemInfo_addr = kernel32.find("GetNativeSystemInfo")
                .orElseThrow();
        return linker.downcallHandle(
                getSystemInfo_addr,
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
        );
    }

    private MethodHandle createGetCurrentProcess() {
        MemorySegment getCurrentProcess_addr = kernel32.find("GetCurrentProcess")
                .orElseThrow();
        return linker.downcallHandle(
                getCurrentProcess_addr,
                FunctionDescriptor.of(ValueLayout.ADDRESS)
        );
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
        MemoryLayout SYSTEM_INFO_LAYOUT = System_Info.SYSTEM_INFO_LAYOUT;
        MemorySegment SYSTEM_INFO_SEGMENT = arena.allocate(SYSTEM_INFO_LAYOUT);
        getSystemInfo.invokeExact(SYSTEM_INFO_SEGMENT);

        int dwOemId = (int) System_Info.DW_OEM_ID_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        short wProcessorArchitecture = (short) System_Info.W_PROCESSOR_ARCHITECTURE_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        short wReserved = (short) System_Info.W_RESERVED_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        int dwPageSize = (int) System_Info.DW_PAGE_SIZE_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        MemorySegment lpMinimumApplicationAddress =
                (MemorySegment) System_Info.LP_MINIMUM_APPLICATION_ADDRESS_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        MemorySegment lpMaximumApplicationAddress =
                (MemorySegment) System_Info.LP_MAXIMUM_APPLICATION_ADDRESS_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        long dwActiveProcessorMask = (long) System_Info.DW_ACTIVE_PROCESSOR_MASK_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        int dwNumberOfProcessors = (int) System_Info.DW_NUMBER_OF_PROCESSORS_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        int dwProcessorType = (int) System_Info.DW_PROCESSOR_TYPE_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        int dwAllocationGranularity = (int) System_Info.DW_ALLOCATION_GRANULARITY_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        short wProcessorLevel = (short) System_Info.W_PROCESSOR_LEVEL_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        short wProcessorRevision = (short) System_Info.W_PROCESSOR_REVISION_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);

        OemId oemId = getOemId(dwOemId, wProcessorArchitecture, wReserved);

        return new System_Info(
                oemId,
                dwPageSize,
                lpMinimumApplicationAddress,
                lpMaximumApplicationAddress,
                dwActiveProcessorMask,
                dwNumberOfProcessors,
                dwProcessorType,
                dwAllocationGranularity,
                wProcessorLevel,
                wProcessorRevision
        );
    }

    public System_Info getNativeSystemInfo() throws Throwable {
        MemoryLayout SYSTEM_INFO_LAYOUT = System_Info.SYSTEM_INFO_LAYOUT;
        MemorySegment SYSTEM_INFO_SEGMENT = arena.allocate(SYSTEM_INFO_LAYOUT);
        getNativeSystemInfo.invokeExact(SYSTEM_INFO_SEGMENT);

        int dwOemId = (int) System_Info.DW_OEM_ID_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        short wProcessorArchitecture = (short) System_Info.W_PROCESSOR_ARCHITECTURE_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        short wReserved = (short) System_Info.W_RESERVED_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        int dwPageSize = (int) System_Info.DW_PAGE_SIZE_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        MemorySegment lpMinimumApplicationAddress =
                (MemorySegment) System_Info.LP_MINIMUM_APPLICATION_ADDRESS_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        MemorySegment lpMaximumApplicationAddress =
                (MemorySegment) System_Info.LP_MAXIMUM_APPLICATION_ADDRESS_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        long dwActiveProcessorMask = (long) System_Info.DW_ACTIVE_PROCESSOR_MASK_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        int dwNumberOfProcessors = (int) System_Info.DW_NUMBER_OF_PROCESSORS_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        int dwProcessorType = (int) System_Info.DW_PROCESSOR_TYPE_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        int dwAllocationGranularity = (int) System_Info.DW_ALLOCATION_GRANULARITY_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        short wProcessorLevel = (short) System_Info.W_PROCESSOR_LEVEL_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);
        short wProcessorRevision = (short) System_Info.W_PROCESSOR_REVISION_VAR_HANDLE.get(SYSTEM_INFO_SEGMENT, 0L);

        OemId oemId = getOemId(dwOemId, wProcessorArchitecture, wReserved);

        return new System_Info(
                oemId,
                dwPageSize,
                lpMinimumApplicationAddress,
                lpMaximumApplicationAddress,
                dwActiveProcessorMask,
                dwNumberOfProcessors,
                dwProcessorType,
                dwAllocationGranularity,
                wProcessorLevel,
                wProcessorRevision
        );
    }

    private static OemId getOemId(int dwOemId, short wProcessorArchitecture, short wReserved) {
        return switch (Short.toUnsignedInt(wProcessorArchitecture)) {
                case 0, 5, 6, 9, 12, 0xFFFF ->
                        new ProcessorArchitecture(wProcessorArchitecture, wReserved);

                default ->
                        throw new IllegalStateException(
                                "Unknown processor architecture: " +Short.toUnsignedInt(wProcessorArchitecture)
                        );
            };
    }

    public MemorySegment getCurrentProcess() throws Throwable {
        return (MemorySegment) getCurrentProcess.invokeExact();
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
