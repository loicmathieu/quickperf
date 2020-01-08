package org.quickperf.junit5.jvm.rss;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.quickperf.junit5.JUnit5Tests;
import org.quickperf.junit5.JUnit5Tests.JUnit5TestsResult;
import org.quickperf.junit5.QuickPerfTest;
import org.quickperf.jvm.allocation.AllocationUnit;
import org.quickperf.jvm.annotations.ExpectRSS;
import org.quickperf.jvm.annotations.MeasureRSS;

import static org.assertj.core.api.Assertions.assertThat;

@DisabledOnOs({OS.WINDOWS, OS.MAC})
public class JUnit5RssTests {

    @QuickPerfTest
    public static class ClassWithRssAnnotations {

        @MeasureRSS
        @ExpectRSS(value=10, unit = AllocationUnit.MEGA_BYTE)
        @Test
        public void measure_and_expect_rss() {
        }

    }

    @Test public void
    rss_measure_expecting_10m() {

        // GIVEN
        Class<?> testClass = ClassWithRssAnnotations.class;
        JUnit5Tests jUnit5Tests = JUnit5Tests.createInstance(testClass);

        // WHEN
        JUnit5TestsResult jUnit5TestsResult = jUnit5Tests.run();

        // THEN
        assertThat(jUnit5TestsResult.getNumberOfFailures()).isEqualTo(0);

    }

    @QuickPerfTest
    public static class ClassWithRssAnnotationsNoFork {

        @MeasureRSS(forkJvm = false)
        @ExpectRSS(value=10, unit = AllocationUnit.MEGA_BYTE, forkJvm = false)
        @Test
        public void measure_and_expect_rss_with_no_fork() {
        }

    }

    @Test public void
    rss_measure_no_fork_expecting_10m() {

        // GIVEN
        Class<?> testClass = ClassWithRssAnnotationsNoFork.class;
        JUnit5Tests jUnit5Tests = JUnit5Tests.createInstance(testClass);

        // WHEN
        JUnit5TestsResult jUnit5TestsResult = jUnit5Tests.run();

        // THEN
        assertThat(jUnit5TestsResult.getNumberOfFailures()).isEqualTo(1);

        String errorReport = jUnit5TestsResult.getErrorReport();
        assertThat(errorReport)
                .contains("Expected RSS to be less than 10.0 Mega bytes but is ");

    }

}
