package com.yashgamerx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yashgamerx.kernel.Kernel32;

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

}
