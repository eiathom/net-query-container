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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author eiathom
 *
 */
public final class ContainerHelper {

    private static final long[] ranges = new long[2];

    private static final List<Short> values = new ArrayList<>();

    private ContainerHelper() {}

    /**
     * @param fromValue
     * @param toValue
     * 
     * @return corrected low and high range parameters
     */
    public static long[] getCorrectRanges(final long fromValue, final long toValue) {
        return getCorrectRanges(fromValue, toValue, false, false);
    }

    /**
     * @param fromValue
     * @param toValue
     * @param fromInclusive
     * @param toInclusive
     * @return correct range query parameters
     */
    public static long[] getCorrectRanges(final long fromValue, final long toValue, final boolean fromInclusive, final boolean toInclusive) {
        ranges[0] = fromValue;
        ranges[1] = toValue;
        if (toValue < fromValue) {
            ranges[0] = toValue;
            ranges[1] = fromValue;
        }
        if (!fromInclusive && !toInclusive) {
            return ranges;
        } else if (!fromInclusive && toInclusive) {
            ranges[1] = checkMaximum(ranges[1]) + 1;
            return ranges;
        } else if (fromInclusive && !toInclusive) {
            ranges[0] = ranges[0] - 1;
            return ranges;
        }
        ranges[0] = ranges[0] - 1;
        ranges[1] = checkMaximum(ranges[1]) + 1;
        return ranges;
    }

    private static long checkMaximum(final long value) {
        if (value != Long.MAX_VALUE) {
            return value;
        }
        return value - 1;
    }

    /**
     * @return a comparator of values of longs
     */
    public static final Comparator<Long> getComparator() {
        return new Comparator<Long>() {
            @Override
            public int compare(final Long o1, final Long o2) {
                return o1.longValue() < o2.longValue() ? -1 : 1;
            }
        };
    }

    /**
     * values become keys for this data
     * 
     * @param array to be added to the container
     * @param data the container data
     */
    public static void populateContainerData(final long[] array, final Map<Long, Short> data) {
        for (short index = 0; index < array.length; index++) {
            data.put(array[(int) index], index);
        }
    }

    /**
     * @param data
     * @return array representation of values
     */
    public static short[] getAsSortedArray(final Collection<Short> data) {
        values.clear();
        values.addAll(data);
        if (values.size() > 1) {
            Collections.sort(values);
        }
        return toArray(values);
    }

    /**
     * @param data list of values
     * @return an array representation of data
     */
    public static short[] toArray(final List<Short> data) {
        final int arrayLength = data.size();
        final short[] array = new short[arrayLength];
        for (int index = 0; index < arrayLength; index++) {
            array[index] = data.get(index);
        }
        return array;
    }

}
