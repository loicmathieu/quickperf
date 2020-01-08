package org.quickperf.junit5.jvm.rss;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.quickperf.junit5.QuickPerfTest;
import org.quickperf.jvm.allocation.AllocationUnit;
import org.quickperf.jvm.annotations.ExpectRSS;
import org.quickperf.jvm.annotations.MeasureRSS;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

@DisabledOnOs(OS.WINDOWS)
public class RssTests {
    @QuickPerfTest
    public static class ClassWithRssAnnotations {

        @MeasureRSS
        @ExpectRSS(value=10, unit = AllocationUnit.MEGA_BYTE)
        @Test
        public void measure_and_expect_rss() {
        }
    }

    @QuickPerfTest
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
        Class<?> testClass = ClassWithRssAnnotations.class;

        // WHEN
        TestExecutionSummary printableResult = testResult(testClass);

        // THEN
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(printableResult.getFailures().size())
                .isEqualTo(0);
        softAssertions.assertAll();

    }

    @Test
    public void
    rss_measure_no_fork_expecting_10m() {

        // GIVEN
        Class<?> testClass = ClassWithRssAnnotationsNoFork.class;

        // WHEN
        TestExecutionSummary printableResult = testResult(testClass);

        // THEN
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(printableResult.getFailures().size())
                .isEqualTo(1);
        String cause = printableResult.getFailures().get(0).getException().getMessage();
        softAssertions.assertThat(cause)
                .contains("Expected RSS to be less than 10.0 Mega bytes but is ");
        softAssertions.assertAll();

    }

    private TestExecutionSummary testResult(Class<?> testClass) {
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        return listener.getSummary();
    }
}
