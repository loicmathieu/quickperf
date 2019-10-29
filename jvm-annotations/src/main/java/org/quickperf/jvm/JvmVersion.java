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

package org.quickperf.jvm;

public enum JvmVersion {

    JAVA7, JAVA8, JAVA9, JAVA10, JAVA11, JAVA12, JAVA13, UNKNOWN;

    private static final String JAVA_VM_SPECIFICATION_VERSION_PROPERTY = "java.vm.specification.version";
    private static final String JVM_VERSION_AS_STRING = System.getProperty(JAVA_VM_SPECIFICATION_VERSION_PROPERTY);

    public static JvmVersion getCurrentJavaVersion(){
        if(JVM_VERSION_AS_STRING.contains("1.7")){
            return JAVA7;
        }
        if(JVM_VERSION_AS_STRING.contains("1.8")){
            return JAVA8;
        }
        int jvmVersion = findJvmVersionAsInt();
        switch (jvmVersion){
            case 9: return JAVA9;
            case 10: return JAVA10;
            case 11: return JAVA11;
            case 12: return JAVA12;
            case 13: return JAVA13;
            default: return UNKNOWN;
        }
    }



    public static boolean is7() {
        return JVM_VERSION_AS_STRING.contains("1.7");
    }

    public static boolean is8() {
        return JVM_VERSION_AS_STRING.contains("1.8");
    }

    public static boolean isGreaterThanOrEqualTo9() {
        if (is7() || is8()) {
            return false;
        }
        return findJvmVersionAsInt() >= 9;
    }

    private static int findJvmVersionAsInt() {
        return Integer.parseInt(JVM_VERSION_AS_STRING);
    }

    public static boolean isGreaterThan8(){
        if (is7() || is8()) {
            return false;
        }
        return true;
    }

    public static boolean isGreaterThanOrEqualTo11() {
        if (is7() || is8()) {
            return false;
        }
        return findJvmVersionAsInt() >= 11;
    }

    public static boolean isGreaterThanOrEqualTo12() {
        if (is7() || is8()) {
            return false;
        }
        return findJvmVersionAsInt() >= 12;
    }

}
