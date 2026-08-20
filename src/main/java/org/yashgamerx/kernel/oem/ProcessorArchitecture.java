package org.yashgamerx.kernel.oem;

public record ProcessorArchitecture(
        short wProcessorArchitecture,
        short wReserved
) implements OemId {
    @Override
    public String toString() {
        String architecture = switch (
                Short.toUnsignedInt(wProcessorArchitecture)
                ) {
            case 0 -> "x86";
            case 5 -> "ARM";
            case 6 -> "Intel Itanium";
            case 9 -> "x64";
            case 12 -> "ARM64";
            case 0xFFFF -> "Unknown";
            default -> "Unknown (" +
                    Short.toUnsignedInt(wProcessorArchitecture) +
                    ")";
        };

        return "%s (code=%d, reserved=%d)"
                .formatted(
                        architecture,
                        Short.toUnsignedInt(wProcessorArchitecture),
                        Short.toUnsignedInt(wReserved)
                );
    }
}
