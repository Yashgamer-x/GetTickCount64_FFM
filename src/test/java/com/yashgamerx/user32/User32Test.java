package com.yashgamerx.user32;

import org.junit.jupiter.api.Test;
import org.yashgamerx.kernel.winuser.EnumWindowsProc;
import org.yashgamerx.user32.User32;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class User32Test {
    @Test
    public void testEnumWindows() {
        try (Arena arena = Arena.ofConfined()) {
            User32 user32 = new User32(arena);
            long lParam = 12345L;

            EnumWindowsProc enumWindowsProc = new EnumWindowsProc();
            MemorySegment callback = enumWindowsProc.createCallback(arena);

            int result = user32.enumWindows(callback, lParam);
        } catch (Throwable e) {
            fail(e);
        }
    }
}
