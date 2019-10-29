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

package org.quickperf.jvm.allocation.bytewatcher;

import org.quickperf.TestExecutionContext;
import org.quickperf.jvm.JvmVersion;
import org.quickperf.jvm.allocation.Allocation;
import org.quickperf.jvm.allocation.AllocationRepository;
import org.quickperf.perfrecording.RecordablePerformance;

public class ByteWatcherRecorder implements RecordablePerformance<Allocation> {

    private static final boolean IS_JVM_VERSION_AT_LEAST_12 = JvmVersion.isGreaterThanOrEqualTo12();

    private static JvmVersion jvmVersion = JvmVersion.getCurrentJavaVersion();

    private static int junit4ByteOffset = 40;
    private static int java12ByteOffsetForJunit4 = 72;

    private static int junit5ByteOffset = 13_728;
    private static int java9ByteOffsetForJunit5 =  12_120;
    private static int java10ByteOffsetForJunit5 =  11_744;
    private static int java12ByteOffsetForJunit5 = 11_296;
    private static int java13ByteOffsetForJunit5 = 11_264;

    //determine junit4 and junit5 offset statically to avoid as much allocation as possible when calling startRecording
    private static int calculatedJunit4ByteOffset;
    static {
        if(IS_JVM_VERSION_AT_LEAST_12){
            calculatedJunit4ByteOffset = java12ByteOffsetForJunit4;
        }
        else {
            calculatedJunit4ByteOffset = junit4ByteOffset;
        }
    }
    private static int calculatedJunit5ByteOffset;
    static {
        switch(jvmVersion){
            //java 7 is not supported in JUnit5
            case JAVA8: calculatedJunit5ByteOffset = junit5ByteOffset; break;
            case JAVA9: calculatedJunit5ByteOffset =  java9ByteOffsetForJunit5; break;
            case JAVA10:
            case JAVA11: calculatedJunit5ByteOffset =  java10ByteOffsetForJunit5; break;
            case JAVA12: calculatedJunit5ByteOffset = java12ByteOffsetForJunit5; break;
            default: calculatedJunit5ByteOffset = java13ByteOffsetForJunit5;//Java version superior thant 13 will use Java 13 offset
        }
    }

    private AllocationRepository allocationRepository;

    private ByteWatcherSingleThread byteWatcher;

    private int junitByteOffset;

    @Override
    public void startRecording(TestExecutionContext testExecutionContext) {
        junitByteOffset = retrieveJunitByteOffset(testExecutionContext);
        allocationRepository = new AllocationRepository();
        byteWatcher = new ByteWatcherSingleThread(Thread.currentThread());
        byteWatcher.reset();
    }

    @Override
    public void stopRecording(TestExecutionContext testExecutionContext) {
        long allocationInBytes = byteWatcher.calculateAllocations() - junitByteOffset;
        allocationRepository.saveAllocationInBytes(allocationInBytes, testExecutionContext);
    }

    private int retrieveJunitByteOffset(TestExecutionContext testExecutionContext) {
        switch (testExecutionContext.getjUnitVersion()){
            case JUNIT4: return calculatedJunit4ByteOffset;
            case JUNIT5: return calculatedJunit5ByteOffset;
            default: throw new RuntimeException("Unandled JUnit version");
        }

    }

    @Override
    public Allocation findRecord(TestExecutionContext testExecutionContext) {
        allocationRepository = new AllocationRepository();
        return allocationRepository.findAllocation(testExecutionContext);
    }

    @Override
    public void cleanResources() {
    }

}
