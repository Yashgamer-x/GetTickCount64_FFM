package org.yashgamerx.kernel.oem;

public record ProcessorArchitecture(
        short architecture,
        short reserved
) implements OemId {
}
