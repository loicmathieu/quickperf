package org.quickperf.jvm.rss;

import org.quickperf.TestExecutionContext;
import org.quickperf.perfrecording.RecordablePerformance;

public class ProcessStatusRecorder implements RecordablePerformance<ProcessStatus> {
    @Override
    public void startRecording(TestExecutionContext testExecutionContext) {
        //nothing to do : only record the process status at the end of the test
    }

    @Override
    public void stopRecording(TestExecutionContext testExecutionContext) {
        ProcessStatus.record();
    }

    @Override
    public ProcessStatus findRecord(TestExecutionContext testExecutionContext) {
        return ProcessStatus.getRecord();
    }

    @Override
    public void cleanResources() {
        ProcessStatus.reset();
    }
}
