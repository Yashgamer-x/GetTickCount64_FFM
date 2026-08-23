package com.yashgamerx.kernel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yashgamerx.kernel.Kernel32;
import org.yashgamerx.kernel.time.LpFileTime;
import org.yashgamerx.kernel.time.SystemTime;

import java.lang.foreign.Arena;

public class Kernel32Test {

    @Test
    public void testGetTickCount64() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            long tickCount = kernel32.getTickCount64();
            Assertions.assertNotEquals(0, tickCount);
        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testGetFileAttributeW() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            int perm = kernel32.getFileAttributesW("C:\\Windows");
            Assertions.assertEquals(16, perm);
        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testGetLogicalDrives() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            int drives = kernel32.getLogicalDrives();
            System.out.println(Integer.toBinaryString(drives));
        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testGetSystemInfo() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var systemInfo = kernel32.getSystemInfo();
            System.out.println(systemInfo);
        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testGetNativeSystemInfo() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var systemInfo = kernel32.getNativeSystemInfo();
            System.out.println(systemInfo);
        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testGetProcess() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var process = kernel32.getCurrentProcess();
            System.out.println(process);
        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testGetProcessId() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var process = kernel32.getCurrentProcess();
            var processId = kernel32.getProcessId(process);
            System.out.println(processId);
        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testGetProcessTimes(){
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var process = kernel32.getCurrentProcess();
            var lpCreationTimeMemorySegment = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var lpExitTimeMemorySegment = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var lpKernelTimeMemorySegment = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var lpUserTimeMemorySegment = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var processTimes = kernel32.getProcessTimes(
                    process,
                    lpCreationTimeMemorySegment,
                    lpExitTimeMemorySegment,
                    lpKernelTimeMemorySegment,
                    lpUserTimeMemorySegment
            );
            Assertions.assertNotEquals(0, processTimes);
            var lpCreationTime = LpFileTime.of(lpCreationTimeMemorySegment);
            var lpExitTime = LpFileTime.of(lpExitTimeMemorySegment);
            var lpKernelTime = LpFileTime.of(lpKernelTimeMemorySegment);
            var lpUserTime = LpFileTime.of(lpUserTimeMemorySegment);
            System.out.println(lpCreationTime);
            System.out.println(lpExitTime);
            System.out.println(lpKernelTime);
            System.out.println(lpUserTime);
        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testCreationSystemTime() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);

            var process = kernel32.getCurrentProcess();

            var creationMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var exitMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var kernelMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var userMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);

            kernel32.getProcessTimes(
                    process,
                    creationMemory,
                    exitMemory,
                    kernelMemory,
                    userMemory
            );

            var systemTimeMemory = arena.allocate(SystemTime.SYSTEM_TIME_LAYOUT);
            int result = kernel32.fileTimeToSystemTime(
                    creationMemory,
                    systemTimeMemory
            );

            Assertions.assertNotEquals(0, result);

            var systemTime = SystemTime.of(systemTimeMemory);

            Assertions.assertTrue(systemTime.wYear() >= 2025);
            Assertions.assertTrue(systemTime.wMonth() >= 1 && systemTime.wMonth() <= 12);
            Assertions.assertTrue(systemTime.wDay() >= 1 && systemTime.wDay() <= 31);
            Assertions.assertTrue(systemTime.wHour() >= 0 && systemTime.wHour() <= 23);
            Assertions.assertTrue(systemTime.wMinute() >= 0 && systemTime.wMinute() <= 59);
            Assertions.assertTrue(systemTime.wSecond() >= 0 && systemTime.wSecond() <= 59);
            Assertions.assertTrue(systemTime.wMillisecond() >= 0 && systemTime.wMillisecond() <= 999);

            System.out.println(systemTime);

        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testUserTime() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);

            var process = kernel32.getCurrentProcess();

            var creationMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var exitMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var kernelMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var userMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);

            kernel32.getProcessTimes(
                    process,
                    creationMemory,
                    exitMemory,
                    kernelMemory,
                    userMemory
            );

            var userTime = LpFileTime.of(userMemory);

            Assertions.assertTrue(userTime.toMilliseconds() >= 0);

            System.out.println(
                    "User CPU Time: " + userTime.toMilliseconds() + " ms"
            );

        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testKernelTime() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);

            var process = kernel32.getCurrentProcess();

            var creationMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var exitMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var kernelMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var userMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);

            kernel32.getProcessTimes(
                    process,
                    creationMemory,
                    exitMemory,
                    kernelMemory,
                    userMemory
            );

            var kernelTime = LpFileTime.of(kernelMemory);

            Assertions.assertTrue(kernelTime.toMilliseconds() >= 0);

            System.out.println(
                    "Kernel CPU Time: " + kernelTime.toMilliseconds() + " ms"
            );

        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testExitTime() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);

            var process = kernel32.getCurrentProcess();

            var creationMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var exitMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var kernelMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            var userMemory = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);

            kernel32.getProcessTimes(
                    process,
                    creationMemory,
                    exitMemory,
                    kernelMemory,
                    userMemory
            );

            var exitTime = LpFileTime.of(exitMemory);

            Assertions.assertEquals(0, exitTime.dwLowDateTime());
            Assertions.assertEquals(0, exitTime.dwHighDateTime());

            System.out.println("Exit Time: " + exitTime);

        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testGetSystemTimeAsFileTime() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var lpSystemTimeAsFileTimeMemorySegment = arena.allocate(LpFileTime.LP_FILE_TIME_LAYOUT);
            kernel32.getSystemTimeAsFileTime(lpSystemTimeAsFileTimeMemorySegment);
            var lpSystemTimeMemorySegment = arena.allocate(SystemTime.SYSTEM_TIME_LAYOUT);
            var result = kernel32.fileTimeToSystemTime(
                    lpSystemTimeAsFileTimeMemorySegment,
                    lpSystemTimeMemorySegment
            );
            if (result == 0) {
                Assertions.fail("Unable to get the system time");
            }
            var lpSystemTime = SystemTime.of(lpSystemTimeMemorySegment);
            System.out.println("Current System Time "+lpSystemTime);
        } catch (Throwable e) {
            Assertions.fail(e);
        }
    }

}
