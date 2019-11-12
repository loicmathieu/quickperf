package org.quickperf.jvm.rss;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.runner.RunWith;
import org.quickperf.junit4.QuickPerfJUnitRunner;
import org.quickperf.jvm.allocation.AllocationUnit;
import org.quickperf.jvm.annotations.ExpectNoJvmIssue;
import org.quickperf.jvm.annotations.ExpectRSS;
import org.quickperf.jvm.annotations.HeapSize;
import org.quickperf.jvm.annotations.MeasureRSS;
import org.quickperf.jvm.jmc.JmcTests;

import java.util.ArrayList;
import java.util.List;

import static org.junit.experimental.results.PrintableResult.testResult;

public class RssTests {
    @RunWith(QuickPerfJUnitRunner.class)
    public static class ClassWithRssAnnotations {

        @MeasureRSS
        @ExpectRSS(value=10, unit = AllocationUnit.MEGA_BYTE)
        @Test
        public void measure_and_expect_rss() {
        }
    }

    @RunWith(QuickPerfJUnitRunner.class)
    public static class ClassWithRssAnnotationsNoFork {

        @MeasureRSS(forkJvm = false)
        @ExpectRSS(value=10, unit = AllocationUnit.MEGA_BYTE, forkJvm = false)
        @Test
        public void measure_and_expect_rss_with_no_fork() {
        }

    }

    @Test
    public void
    rss_measure_expecting_10m() {

        // GIVEN
        Class<?> testClass = RssTests.ClassWithRssAnnotations.class;

        // WHEN
        PrintableResult printableResult = testResult(testClass);

        // THEN
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(printableResult.failureCount())
                .isEqualTo(0);
        softAssertions.assertAll();

    }

    @Test
    public void
    rss_measure_no_fork_expecting_10m() {

        // GIVEN
        Class<?> testClass = RssTests.ClassWithRssAnnotationsNoFork.class;

        // WHEN
        PrintableResult printableResult = testResult(testClass);

        // THEN
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(printableResult.failureCount())
                .isEqualTo(1);
        softAssertions.assertThat(printableResult.toString())
                .contains("Expected RSS to be less than 10.0 Mega bytes but is ");
        softAssertions.assertAll();

    }
}
