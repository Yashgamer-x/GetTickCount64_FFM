package org.yashgamerx;

import org.yashgamerx.exception.GetUserNameWException;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

public class Advapi32 {

    private final Arena arena;
    private final SymbolLookup advapi32;
    private final Linker linker = Linker.nativeLinker();

    private final MethodHandle getUserNameW;

    public Advapi32(Arena arena) {
        this.arena = arena;
        this.advapi32 = SymbolLookup.libraryLookup("advapi32.dll", arena);

        getUserNameW = createGetUserNameW();
    }

    private MethodHandle createGetUserNameW() {
        MemorySegment getUserNameW_addr = advapi32.find("GetUserNameW")
                .orElseThrow();
        return linker.downcallHandle(
                getUserNameW_addr,
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT, // BOOL is a typedef for INT in C
                        ValueLayout.ADDRESS, // LPWSTR - 16LE string with null-terminated at the end
                        ValueLayout.ADDRESS // LPDWORD - typedef DWORD *LPDWORD; - typedef unsigned long DWORD;
                )
        );
    }

    public String getUserNameW() throws Throwable {
        var LPWSTR = arena.allocate(255 * 2);

        var LPDWORD = arena.allocate(ValueLayout.JAVA_INT);
        LPDWORD.set(ValueLayout.JAVA_INT, 0, 255);

        int result = (int) getUserNameW.invokeExact(LPWSTR, LPDWORD);

        if (result == 0) {
            throw new GetUserNameWException("Unable to get the username");
        }

        return LPWSTR.getString(0, StandardCharsets.UTF_16LE);
    }

}
