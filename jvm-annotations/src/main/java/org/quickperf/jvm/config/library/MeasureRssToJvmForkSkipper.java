package org.quickperf.jvm.config.library;

import org.quickperf.jvm.annotations.MeasureRSS;
import org.quickperf.testlauncher.AnnotationToJvmForkSkipper;

public class MeasureRssToJvmForkSkipper implements AnnotationToJvmForkSkipper<MeasureRSS> {
    static final AnnotationToJvmForkSkipper INSTANCE = new MeasureRssToJvmForkSkipper();

    @Override
    public boolean disableFork(MeasureRSS annotation) {
        return !annotation.forkJvm();
    }
}