/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2019-2019 the original author or authors.
 */

package org.quickperf.junit5;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.quickperf.*;
import org.quickperf.config.library.QuickPerfConfigs;
import org.quickperf.config.library.QuickPerfConfigsLoader;
import org.quickperf.config.library.SetOfAnnotationConfigs;
import org.quickperf.measure.PerfMeasure;
import org.quickperf.perfrecording.PerfRecord;
import org.quickperf.perfrecording.RecordablePerformance;
import org.quickperf.perfrecording.ViewablePerfRecordIfPerfIssue;
import org.quickperf.reporter.ConsoleReporter;
import org.quickperf.testlauncher.NewJvmTestLauncher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuickPerfTestExtension implements BeforeEachCallback, InvocationInterceptor {

    private final QuickPerfConfigs quickPerfConfigs =  QuickPerfConfigsLoader.INSTANCE.loadQuickPerfConfigs();
    private final IssueThrower issueThrower = IssueThrower.INSTANCE;
    private final NewJvmTestLauncher newJvmTestLauncher = NewJvmTestLauncher.INSTANCE;
    private final JUnit5FailuresRepository jUnit5FailuresRepository = JUnit5FailuresRepository.INSTANCE;
    private final ConsoleReporter consoleReporter = ConsoleReporter.INSTANCE;
    private final PerfIssuesEvaluator perfIssuesEvaluator = PerfIssuesEvaluator.INSTANCE;

    private TestExecutionContext testExecutionContext;

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        testExecutionContext = TestExecutionContext.buildFrom(quickPerfConfigs, extensionContext.getRequiredTestMethod(), JUnitVersion.JUNIT5);
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {
        // FIXME use a system property to avoid recursive forking
        boolean inAFork = "true".equals(System.getProperty("quickPerfInAFork", "false"));
        if(inAFork){
            invocation.proceed();
            return;
        }

        //retrieve the quickperf annotation and log about JVM forking
        QuickPerfTest quickPerfTest = invocationContext.getExecutable().getDeclaringClass().getDeclaredAnnotation(QuickPerfTest.class);
        boolean forkDisabled = quickPerfTest != null && quickPerfTest.disableFork();
        if (testExecutionContext.testExecutionUsesTwoJVMs()) {
            if (forkDisabled) {
                System.out.println("[QUICK PERF] WARNING forking is explicitly disabled, this can cause inconcistent results");
            } else {
                System.out.println("[QUICK PERF] INFO forking the VM, it is done later on JUnit5 and can cause issues on your test, " +
                        "if it occurs you can use '@QuickPerfTest(disableFork = true)' to disable forking");
            }
        }

        Throwable businessThrowable = null;
        List<RecordablePerformance> perfRecordersToExecuteBeforeTestMethod = testExecutionContext.getPerfRecordersToExecuteBeforeTestMethod();
        List<RecordablePerformance> perfRecordersToExecuteAfterTestMethod = testExecutionContext.getPerfRecordersToExecuteAfterTestMethod();


        if (testExecutionContext.testExecutionUsesTwoJVMs() && !forkDisabled) {
            if(testExecutionContext.isQuickPerfDisabled()){
                startRecording(perfRecordersToExecuteBeforeTestMethod);
            }
            newJvmTestLauncher.run( invocationContext.getExecutable()
                    , testExecutionContext.getWorkingFolder()
                    , testExecutionContext.getJvmOptions()
                    , QuickPerfJunit5Core.class);
            if(testExecutionContext.isQuickPerfDisabled()){
                stopRecording(perfRecordersToExecuteAfterTestMethod);
            }
            WorkingFolder workingFolder = testExecutionContext.getWorkingFolder();
            businessThrowable = jUnit5FailuresRepository.find(workingFolder);
        }
        else {
            try{
                if(testExecutionContext.isQuickPerfDisabled()){
                    startRecording(perfRecordersToExecuteBeforeTestMethod);
                }
                invocation.proceed();
            }
            catch (Throwable throwable){
                businessThrowable = throwable;
            }
            finally {
                if(testExecutionContext.isQuickPerfDisabled()){
                    stopRecording(perfRecordersToExecuteAfterTestMethod);
                }
            }
        }

        SetOfAnnotationConfigs testAnnotationConfigs = quickPerfConfigs.getTestAnnotationConfigs();
        Collection<PerfIssuesToFormat> groupOfPerfIssuesToFormat = perfIssuesEvaluator.evaluatePerfIssues(testAnnotationConfigs, testExecutionContext, RetrievableFailure.NONE);

        cleanResources();

        if(testExecutionContext.areQuickPerfAnnotationsToBeDisplayed()) {
            consoleReporter.displayQuickPerfAnnotations(testExecutionContext.getPerfAnnotations());
        }

        if (testExecutionContext.isQuickPerfDebugMode()) {
            consoleReporter.displayQuickPerfDebugInfos();
        }

        issueThrower.throwIfNecessary(businessThrowable, groupOfPerfIssuesToFormat);
    }

    private void startRecording(List<RecordablePerformance> perfRecordersToExecuteBeforeTestMethod) {
        for (int i = 0; i < perfRecordersToExecuteBeforeTestMethod.size(); i++) {
            RecordablePerformance recordablePerformance = perfRecordersToExecuteBeforeTestMethod.get(i);
            recordablePerformance.startRecording(testExecutionContext);
        }
    }

    private void stopRecording(List<RecordablePerformance> perfRecordersToExecuteAfterTestMethod) {
        for (int i = 0; i < perfRecordersToExecuteAfterTestMethod.size() ; i++) {
            RecordablePerformance recordablePerformance = perfRecordersToExecuteAfterTestMethod.get(i);
            recordablePerformance.stopRecording(testExecutionContext);
        }
    }

    private void cleanResources() {
        List<RecordablePerformance> perfRecorders = testExecutionContext.getPerfRecordersToExecuteAfterTestMethod();
        for (RecordablePerformance perfRecorder : perfRecorders) {
            perfRecorder.cleanResources();
        }
    }

}