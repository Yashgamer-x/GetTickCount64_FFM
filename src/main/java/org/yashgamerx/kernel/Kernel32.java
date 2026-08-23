package org.yashgamerx.kernel;

import org.yashgamerx.kernel.oem.OemId;
import org.yashgamerx.kernel.oem.ProcessorArchitecture;
import org.yashgamerx.kernel.oem.System_Info;
import org.yashgamerx.kernel.time.SystemTime;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

public class Kernel32 {

    private final Arena arena;
    private final SymbolLookup kernel32;
    private final Linker linker = Linker.nativeLinker();
    private final MethodHandle getTickCount64;
    private final MethodHandle getFileAttributeW;
    private final MethodHandle getLogicalDrives;
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

        getTickCount64 = createGetTickCount64();
        getFileAttributeW = createGetFileAttributeW();
        getLogicalDrives = createGetLogicalDrives();
        getSystemInfo = createGetSystemInfo();
        getNativeSystemInfo = createGetNativeSystemInfo();
        getCurrentProcess = createGetCurrentProcess();
        getProcessId = createGetProcessId();
        getProcessTimes = createGetProcessTimes();
        fileTimeToSystemTime = createFileTimeToSystemTime();
        getSystemTimeAsFileTime = createGetSystemTimeAsFileTime();
    }

    private MethodHandle createGetTickCount64() {
        MemorySegment getTickCount64_addr = kernel32.find("GetTickCount64")
                .orElseThrow();
        return linker.downcallHandle(
                getTickCount64_addr,
                FunctionDescriptor.of(ValueLayout.JAVA_LONG)
        );
    }

    private MethodHandle createGetFileAttributeW() {
        MemorySegment getFileAttributesW_addr = kernel32.find("GetFileAttributesW")
                .orElseThrow();
        return linker.downcallHandle(
                getFileAttributesW_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT,
                        ValueLayout.ADDRESS
                )
        );
    }

    private MethodHandle createGetLogicalDrives() {
        MemorySegment getLogicalDrives_addr = kernel32.find("GetLogicalDrives")
                .orElseThrow();
        return linker.downcallHandle(
                getLogicalDrives_addr,
                FunctionDescriptor.of(ValueLayout.JAVA_INT)
        );
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
        return (long) getTickCount64.invokeExact();
    }

    public int getFileAttributesW(String path) throws Throwable {
        byte[] bytes = (path + "\0").getBytes(StandardCharsets.UTF_16LE);

        MemorySegment pathSegment = arena.allocate(bytes.length);
        pathSegment.copyFrom(MemorySegment.ofArray(bytes));

        return (int) getFileAttributeW.invokeExact(pathSegment);
    }

    public int getLogicalDrives() throws Throwable {
        return (int) getLogicalDrives.invokeExact();
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
