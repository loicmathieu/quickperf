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

package org.quickperf.jvm.allocation;

import org.quickperf.PerfIssue;
import org.quickperf.VerifiablePerformanceIssue;
import org.quickperf.jvm.annotations.MeasureHeapAllocation;

public class MeasureHeapAllocationPerfVerifier implements VerifiablePerformanceIssue<MeasureHeapAllocation, Allocation> {

    public static final VerifiablePerformanceIssue INSTANCE = new MeasureHeapAllocationPerfVerifier();

    private final ByteAllocationMeasureFormatter byteAllocationMeasureFormatter = ByteAllocationMeasureFormatter.INSTANCE;

    private MeasureHeapAllocationPerfVerifier() { }

    @Override
    public PerfIssue verifyPerfIssue(MeasureHeapAllocation annotation, Allocation measuredAllocation) {
        String allocationAsString = byteAllocationMeasureFormatter.format(measuredAllocation);
        System.out.println("Measured heap allocation: " + allocationAsString);
        return PerfIssue.NONE;
    }

}
