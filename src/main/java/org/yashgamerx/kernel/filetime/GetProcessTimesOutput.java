package org.yashgamerx.kernel.filetime;

public record GetProcessTimesOutput(
        LpFileTime lpCreationTime,
        LpFileTime lpExitTime,
        LpFileTime lpKernelTime,
        LpFileTime lpUserTime
) {

    @Override
    public String toString() {
        return "GetProcessTimesOutput:" +
                "\nlpCreationTime=" + lpCreationTime +
                "\nlpExitTime=" + lpExitTime +
                "\nlpKernelTime=" + lpKernelTime +
                "\nlpUserTime=" + lpUserTime;
    }

}
