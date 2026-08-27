package com.yashgamerx.user32;

import org.junit.jupiter.api.Test;
import org.yashgamerx.user32.User32;
import org.yashgamerx.user32.winuser.EnumChildWindowsProc;
import org.yashgamerx.user32.winuser.EnumWindowsProc;
import org.yashgamerx.user32.winuser.MonitorEnumProc;
import org.yashgamerx.user32.winuser.Rect;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void testEnumChildWindows() {
        try (Arena arena = Arena.ofConfined()) {
            User32 user32 = new User32(arena);
            EnumChildWindowsProc enumChildWindowsProc = new EnumChildWindowsProc();
            MemorySegment callback = enumChildWindowsProc.createCallback(arena);

            int result = user32.enumChildWindows(MemorySegment.NULL, callback, 0);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testEnumDisplayMonitors() {
        try(Arena arena = Arena.ofConfined()) {
            User32 user32 = new User32(arena);
            MonitorEnumProc enumChildWindowsProc = new MonitorEnumProc();
            MemorySegment callback = enumChildWindowsProc.createCallback(arena);

            int result = user32.enumDisplayMonitors(MemorySegment.NULL, MemorySegment.NULL, callback, 0);
            System.out.println("Result: "+result);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testEnumDisplayMonitorsWithRectAsValue0() {
        try(Arena arena = Arena.ofConfined()) {
            User32 user32 = new User32(arena);
            MonitorEnumProc enumChildWindowsProc = new MonitorEnumProc();
            MemorySegment callback = enumChildWindowsProc.createCallback(arena);
            MemorySegment rectMemorySegment = arena.allocate(Rect.RECT_LAYOUT);

            int result = user32.enumDisplayMonitors(MemorySegment.NULL, rectMemorySegment, callback, 0);
            System.out.println("Result: "+result);
        } catch (Throwable e) {
            fail(e);
        }
    }

    @Test
    public void testEnumDisplayMonitorsWithRectValues() {
        try(Arena arena = Arena.ofConfined()) {
            User32 user32 = new User32(arena);
            MonitorEnumProc enumChildWindowsProc = new MonitorEnumProc();
            MemorySegment callback = enumChildWindowsProc.createCallback(arena);
            MemorySegment rectMemorySegment = arena.allocate(Rect.RECT_LAYOUT);

            Rect.LEFT_HANDLE.set(rectMemorySegment, 0L, 0);
            Rect.TOP_HANDLE.set(rectMemorySegment, 0L, 0);
            Rect.RIGHT_HANDLE.set(rectMemorySegment, 0L, 1920);
            Rect.BOTTOM_HANDLE.set(rectMemorySegment, 0L, 1080);

            int result = user32.enumDisplayMonitors(MemorySegment.NULL, rectMemorySegment, callback, 0);
            System.out.println("Result: "+result);
        } catch (Throwable e) {
            fail(e);
        }
    }
}
