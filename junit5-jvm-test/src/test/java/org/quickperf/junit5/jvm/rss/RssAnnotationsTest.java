package org.quickperf.junit5.jvm.rss;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;

public class RssAnnotationsTest {
    public static void main(String[] args) throws IOException {
        long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
        long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
        System.out.println("totalMemory=" + totalMemory + " maxMemory=" + maxMemory + " freeMemory=" + freeMemory);
        System.out.println("Used=" + (totalMemory - freeMemory));

        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        long used = 0L;
        long committed = 0L;
        for(MemoryPoolMXBean memoryPoolMXBean: memoryPoolMXBeans){
            used += memoryPoolMXBean.getUsage().getUsed();
            committed += memoryPoolMXBean.getUsage().getCommitted();
        }
        System.out.println("used=" + used / 1024);
        System.out.println("committed=" + committed / 1024);

        long memUsed = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() + ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
        long memCommitted = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted() + ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getCommitted();
        System.out.println("memUsed=" + memUsed / 1024);
        System.out.println("memCommitted=" + memCommitted / 1024);

        String pid = ManagementFactory.getRuntimeMXBean().getName().substring(0, ManagementFactory.getRuntimeMXBean().getName().indexOf('@'));
        String statusFile = "/proc/" + pid + "/status";
        List<String> status = FileUtils.readLines(new File(statusFile), "UTF-8");
        String rss = null;
        for(String line : status){
            if(line.startsWith("VmRSS")){
                //FIXME use a regex
                rss = line.substring(6, line.length() - 2).trim();
            }
        }
        System.out.println("rss=" + rss);
    }
}
