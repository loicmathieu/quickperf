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

package org.quickperf.jvm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MeasureRSS {

    /**
     * Whether or not to Fork the JVM to measure the RSS only for the test execution.
     * By default, QuickPerf will fork the JVM so the RSS will be measured only for your test method.
     *
     * Some use case con take advantage of not forking to globally measure the RSS of the Test class,
     * taken into account the following part of the test execution:
     * - Global initilizer (static block, @BeforeAll or @BeforeClass annotated methods)
     * - Other extension that "starts an application" for integration testing (Spring or Quarkus test support)
     */
    boolean forkJvm() default true;
}
