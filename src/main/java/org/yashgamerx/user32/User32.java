package org.yashgamerx.user32;

import org.yashgamerx.kernel.winuser.EnumWindows;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;

public class User32 {

    private final SymbolLookup user32;
    private final EnumWindows enumWindows;

    public User32(Arena arena) {
        this.user32 = SymbolLookup.libraryLookup(
                "user32.dll", arena
        );

        this.enumWindows = new EnumWindows(user32);
    }

    public int enumWindows(MemorySegment hwnd, long lParam) throws Throwable {
        return enumWindows.invoke(hwnd, lParam);
    }
}
