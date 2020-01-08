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

package org.quickperf.jvm.rss;

import org.apache.commons.io.FileUtils;
import org.quickperf.measure.AbstractComparablePerfMeasure;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

class ProcessStatus extends AbstractComparablePerfMeasure<ProcessStatus> {
    private static ProcessStatus record;
    private long rssInKb;

    static void record(){
        String statusFile = "/proc/self/status";
        try {
            List<String> status = FileUtils.readLines(new File(statusFile), "UTF-8");
            String rss = null;
            for(String line : status){
                if(line.startsWith("VmRSS")){
                    //FIXME use a regex
                    rss = line.substring(6, line.length() - 2).trim();
                }
            }
            ProcessStatus ps = new ProcessStatus();
            if(rss != null){
                ps.setRssInKb(Long.parseLong(rss));
            }
            record = ps;
        } catch (IOException e) {
            System.out.println("[QUICK PERF] - ERROR - Unable to read the status file " + statusFile + " : status file are only available on Linux");
        }
    }

    static ProcessStatus getRecord(){
        return record;
    }

    static void reset(){
        record = null;
    }

    public long getRssInKb() {
        return rssInKb;
    }

    public void setRssInKb(long rssInKb) {
        this.rssInKb = rssInKb;
    }

    @Override
    public int compareTo(ProcessStatus processStatus) {
        return 0;
    }

    @Override
    public Object getValue() {
        return record;
    }

    @Override
    public Object getUnit() {
        return "kb";
    }

    @Override
    public String getComment() {
        return null;
    }
}
