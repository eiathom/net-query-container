/**
 * Copyright 2017 eiathom
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workday;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author eiathom
 *
 */
public final class Main {

    private static final long CONTAINER_SIZE = 32000;

    private static final long MINIMUM_VALUE = 1000;

    private static final long MAXIMUM_VALUE = 1000000;

    private static final int NUMBER_OF_WORKERS = 15;

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(NUMBER_OF_WORKERS);

    public static void main(final String[] args) {
        final RangeContainer container = new NetRangeQueryContainerFactory().createContainer(getData(CONTAINER_SIZE, MINIMUM_VALUE, MAXIMUM_VALUE));
        for (int index = 0; index < NUMBER_OF_WORKERS; index++) {
            EXECUTOR_SERVICE.submit(new ThreadWorker(container, MINIMUM_VALUE, MAXIMUM_VALUE));
        }
    }

    private static long[] getData(final long size, final long minValue, final long maxValue) {
        final long[] data = new long[(int) size];
        for (int index = 0; index < data.length; index++) {
            data[index] = ThreadLocalRandom.current().nextLong(minValue, maxValue);
        }
        return data;
    }

}
