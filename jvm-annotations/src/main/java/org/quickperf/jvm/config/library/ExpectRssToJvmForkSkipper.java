package org.quickperf.jvm.config.library;

import org.quickperf.jvm.annotations.ExpectRSS;
import org.quickperf.testlauncher.AnnotationToJvmForkSkipper;

public class ExpectRssToJvmForkSkipper implements AnnotationToJvmForkSkipper<ExpectRSS> {
    static final AnnotationToJvmForkSkipper INSTANCE = new ExpectRssToJvmForkSkipper();

    @Override
    public boolean disableFork(ExpectRSS annotation) {
        return !annotation.forkJvm();
    }
}
