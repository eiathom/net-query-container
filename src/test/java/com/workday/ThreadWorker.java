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

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author eiathom
 *
 */
public final class ThreadWorker implements Runnable {

    private static final StringBuilder STRING_BUILDER = new StringBuilder(1);

    private final RangeContainer rangeContainer;

    private final long minValue;
    
    private final long maxValue;

    public ThreadWorker(final RangeContainer rangeContainer, final long minValue, final long maxValue) {
        this.rangeContainer = rangeContainer;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void run() {
        System.out.println("starting worker ... ");
        try {
            while (true) {
                final long[] values = getValues(minValue, maxValue);
                STRING_BUILDER.delete(0, STRING_BUILDER.length());
                System.out.println(STRING_BUILDER.append("searching for: ").append(values[0]).append(", ").append(values[1]));
                rangeContainer.findIdsInRange(values[0], values[1], true, true);
                STRING_BUILDER.delete(0, STRING_BUILDER.length());
                System.out.println(STRING_BUILDER.append("worker is done on this run ... "));
                STRING_BUILDER.delete(0, STRING_BUILDER.length());
                Thread.sleep(1000);
            }
        } catch (final InterruptedException exception) {
            System.err.println(STRING_BUILDER.append("sleep got interrupted!").append(": ").append(exception));
            STRING_BUILDER.delete(0, STRING_BUILDER.length());
        }
    }

    private long[] getValues(final long lowerRange, final long higherRange) {
        final long[] data = new long[2];
        data[0] = ThreadLocalRandom.current().nextLong(lowerRange, higherRange / 2);
        data[1] = ThreadLocalRandom.current().nextLong(lowerRange * 2, higherRange);
        return data;
    }

}
