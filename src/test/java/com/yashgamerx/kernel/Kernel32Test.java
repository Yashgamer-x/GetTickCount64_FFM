package com.yashgamerx.kernel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yashgamerx.kernel.Kernel32;
import org.yashgamerx.kernel.time.LpFileTime;
import org.yashgamerx.kernel.time.SystemTime;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class Kernel32Test {

    @Test
    public void testGetTickCount64() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            long tickCount = kernel32.getTickCount64();
            Assertions.assertNotEquals(0, tickCount);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testGetFileAttributeW() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            int perm = kernel32.getFileAttributesW("C:\\Windows");
            assertEquals(16, perm);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testGetLogicalDrives() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            int drives = kernel32.getLogicalDrives();
            System.out.println(Integer.toBinaryString(drives));
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testGetSystemInfo() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var systemInfo = kernel32.getSystemInfo();
            System.out.println(systemInfo);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testGetNativeSystemInfo() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var systemInfo = kernel32.getNativeSystemInfo();
            System.out.println(systemInfo);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testGetProcess() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var process = kernel32.getCurrentProcess();
            System.out.println(process);
        } catch (Throwable e) {
            fail(e);
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
            fail(e);
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
            fail(e);
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

            assertTrue(systemTime.wYear() >= 2025);
            assertTrue(systemTime.wMonth() >= 1 && systemTime.wMonth() <= 12);
            assertTrue(systemTime.wDay() >= 1 && systemTime.wDay() <= 31);
            assertTrue(systemTime.wHour() >= 0 && systemTime.wHour() <= 23);
            assertTrue(systemTime.wMinute() >= 0 && systemTime.wMinute() <= 59);
            assertTrue(systemTime.wSecond() >= 0 && systemTime.wSecond() <= 59);
            assertTrue(systemTime.wMillisecond() >= 0 && systemTime.wMillisecond() <= 999);

            System.out.println(systemTime);

        } catch (Throwable e) {
            fail(e);
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

            assertTrue(userTime.toMilliseconds() >= 0);

            System.out.println(
                    "User CPU Time: " + userTime.toMilliseconds() + " ms"
            );

        } catch (Throwable e) {
            fail(e);
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

            assertTrue(kernelTime.toMilliseconds() >= 0);

            System.out.println(
                    "Kernel CPU Time: " + kernelTime.toMilliseconds() + " ms"
            );

        } catch (Throwable e) {
            fail(e);
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

            assertEquals(0, exitTime.dwLowDateTime());
            assertEquals(0, exitTime.dwHighDateTime());

            System.out.println("Exit Time: " + exitTime);

        } catch (Throwable e) {
            fail(e);
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
                fail("Unable to get the system time");
            }
            var lpSystemTime = SystemTime.of(lpSystemTimeMemorySegment);
            System.out.println("Current System Time "+lpSystemTime);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testGetLastError() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var lastError = kernel32.getLastError();
            System.out.println("Last Error: " + lastError);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testSetLastError() {
        try (Arena arena = Arena.ofConfined()) {
            final var expectedError = 12345;
            Kernel32 kernel32 = new Kernel32(arena);
            kernel32.setLastError(expectedError);
            var lastError = kernel32.getLastError();
            assertEquals(expectedError, lastError);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testInvalidGetFileAttributesW() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var perm = kernel32.getFileAttributesW("C:\\does-not-exists");
            assertEquals(-1, perm);
            var lastError = kernel32.getLastError();
            assertEquals(2, lastError);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testGetEnvironmentVariableW() {
        try (Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);

            final String name = "USERNAME";
            int bufferSize = 32_767;

            MemorySegment lpNameSegment = arena.allocateFrom(name + "\0", StandardCharsets.UTF_16LE);
            MemorySegment lpBufferSegment = arena.allocate((long) bufferSize * 2);
            int length = kernel32.getEnvironmentVariable(lpNameSegment, lpBufferSegment, bufferSize);

            String value = lpBufferSegment.getString(0, StandardCharsets.UTF_16LE);
            if (length == 0) {
                fail("Unable to get the environment variable");
            }
            System.out.println("Length: " + length);
            System.out.println("Value: " + value);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testGetComputerNameW() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            int characterSize = 16;
            MemorySegment lpBuffer = arena.allocate((long)characterSize*2);
            MemorySegment size = arena.allocate(4); // 32-bit pointer
            size.set(ValueLayout.JAVA_INT, 0, characterSize);
            int result = kernel32.getComputerNameW(lpBuffer, size);
            if (result == 0) {
                fail("Unable to get the computer name");
            }
            int length = size.get(ValueLayout.JAVA_INT, 0);
            String computerName = lpBuffer.getString(0, StandardCharsets.UTF_16LE);
            assertNotEquals(0, length);
            assertNotNull(computerName);
            System.out.println("Computer Name: " + computerName);
            System.out.println("Computer Name Length: " + length);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testGetSystemDirectoryW() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            int characterSize = 2048;
            MemorySegment lpBuffer = arena.allocate((long)characterSize*2);
            int result = kernel32.getSystemDirectoryW(lpBuffer, characterSize);
            if (result == 0) {
                fail("Unable to get the system directory");
            }
            String systemDirectory = lpBuffer.getString(0, StandardCharsets.UTF_16LE);
            assertNotNull(systemDirectory);
            System.out.println("System Directory: " + systemDirectory);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testGetModuleFileNameW() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            int characterSize = 2048;
            MemorySegment lpBuffer = arena.allocate((long)characterSize*2);
            int result = kernel32.getModuleFileNameW(MemorySegment.NULL, lpBuffer, characterSize);

            if (result == 0) {
                fail("Unable to get the module file name");
            }

            String moduleFileName = lpBuffer.getString(0, StandardCharsets.UTF_16LE);
            assertEquals(result, moduleFileName.length());
            System.out.println("Module File Name: " + moduleFileName);
            System.out.println("Character Count: " + result);
        } catch (Throwable e) {
            fail(e);
        }
    }
}
