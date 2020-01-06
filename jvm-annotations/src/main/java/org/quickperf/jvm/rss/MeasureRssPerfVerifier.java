package org.quickperf.jvm.rss;

import org.quickperf.PerfIssue;
import org.quickperf.VerifiablePerformanceIssue;
import org.quickperf.jvm.annotations.MeasureRSS;

public class MeasureRssPerfVerifier implements VerifiablePerformanceIssue<MeasureRSS, ProcessStatus> {
    public static final VerifiablePerformanceIssue INSTANCE = new MeasureRssPerfVerifier();

    private MeasureRssPerfVerifier(){
    }

    @Override
    public PerfIssue verifyPerfIssue(MeasureRSS annotation, ProcessStatus measure) {
        System.out.println("Measured RSS " + measure.getRssInKb() + " kb");
        return PerfIssue.NONE;
    }
}
