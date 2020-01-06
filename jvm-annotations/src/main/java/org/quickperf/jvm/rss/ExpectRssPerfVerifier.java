package org.quickperf.jvm.rss;

import org.quickperf.PerfIssue;
import org.quickperf.VerifiablePerformanceIssue;
import org.quickperf.jvm.allocation.Allocation;
import org.quickperf.jvm.allocation.AllocationUnit;
import org.quickperf.jvm.allocation.ByteAllocationMeasureFormatter;
import org.quickperf.jvm.annotations.ExpectRSS;

public class ExpectRssPerfVerifier implements VerifiablePerformanceIssue<ExpectRSS, ProcessStatus> {

    public static final VerifiablePerformanceIssue INSTANCE = new ExpectRssPerfVerifier();
    private final ByteAllocationMeasureFormatter byteAllocationMeasureFormatter = ByteAllocationMeasureFormatter.INSTANCE;

    private ExpectRssPerfVerifier() {
    }

    @Override
    public PerfIssue verifyPerfIssue(ExpectRSS annotation, ProcessStatus processStatus) {

        Allocation maxExpectedRss = new Allocation(annotation.value(), annotation.unit());
        Allocation measuredRss = new Allocation((double) processStatus.getRssInKb() * 1024, AllocationUnit.BYTE);

        if (maxExpectedRss.isLessThan(measuredRss)) {

            String assertionMessage =
                    "Expected RSS to be less than "
                            + byteAllocationMeasureFormatter.format(maxExpectedRss)
                            + " but is " + byteAllocationMeasureFormatter.format(measuredRss) + ".";
            String description = assertionMessage + System.lineSeparator() + measuredRss.getComment();

            return new PerfIssue(description);
        }

        return PerfIssue.NONE;

    }
}