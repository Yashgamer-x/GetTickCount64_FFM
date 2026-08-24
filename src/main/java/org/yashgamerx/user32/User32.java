package org.yashgamerx.user32;

import org.yashgamerx.kernel.winuser.EnumChildWindows;
import org.yashgamerx.kernel.winuser.EnumDisplayMonitors;
import org.yashgamerx.kernel.winuser.EnumWindows;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;

public class User32 {

    private final SymbolLookup user32;
    private final EnumWindows enumWindows;
    private final EnumChildWindows enumChildWindows;
    private final EnumDisplayMonitors enumDisplayMonitors;

    public User32(Arena arena) {
        this.user32 = SymbolLookup.libraryLookup(
                "user32.dll", arena
        );

        this.enumWindows = new EnumWindows(user32);
        this.enumChildWindows = new EnumChildWindows(user32);
        this.enumDisplayMonitors = new EnumDisplayMonitors(user32);
    }

    public int enumWindows(MemorySegment hwnd, long lParam) throws Throwable {
        return enumWindows.invoke(hwnd, lParam);
    }

    public int enumChildWindows(MemorySegment hwndParent, MemorySegment hwnd, long lParam) throws Throwable {
        return enumChildWindows.invoke(hwndParent, hwnd, lParam);
    }

    public int enumDisplayMonitors(MemorySegment hdc, MemorySegment lprcClip, MemorySegment lpfnEnum, long dwData) throws Throwable {
        return enumDisplayMonitors.invoke(hdc, lprcClip, lpfnEnum, dwData);
    }
}
