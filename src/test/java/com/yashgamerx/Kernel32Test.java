package com.yashgamerx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yashgamerx.kernel.Kernel32;
import org.yashgamerx.kernel.time.LpFileTime;

import java.lang.foreign.Arena;

public class Kernel32Test {

    @Test
    public void testGetTickCount64() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            long tickCount = kernel32.getTickCount64();
            Assertions.assertNotEquals(0, tickCount);
        } catch (Throwable _) {
            Assertions.fail();
        }
    }

    @Test
    public void testGetFileAttributeW() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            int perm = kernel32.getFileAttributesW("C:\\Windows");
            Assertions.assertEquals(16, perm);
        } catch (Throwable e) {
            Assertions.fail();
        }
    }

    @Test
    public void testGetLogicalDrives() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            int drives = kernel32.getLogicalDrives();
            System.out.println(Integer.toBinaryString(drives));
        } catch (Throwable e) {
            Assertions.fail();
        }
    }

    @Test
    public void testGetSystemInfo() {
        try(Arena arena = Arena.ofConfined()) {
            Kernel32 kernel32 = new Kernel32(arena);
            var systemInfo = kernel32.getSystemInfo();
            System.out.println(systemInfo);
        } catch (Throwable e) {
            Assertions.fail();
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
            Assertions.fail();
        }
    }

}
