package com.yashgamerx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yashgamerx.advapi.Advapi32;

import java.lang.foreign.Arena;

public class Advapi32Test {

    @Test
    public void testGetUsernameW() {
        try(Arena arena = Arena.ofConfined()) {
            Advapi32 advapi32 = new Advapi32(arena);
            var username = advapi32.getUserNameW();
            Assertions.assertNotNull(username);
            System.out.println(username);
        } catch (Throwable e) {
            Assertions.fail();
        }
    }

}
