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

import static com.workday.ContainerHelper.getAsSortedArray;
import static com.workday.ContainerHelper.getComparator;
import static com.workday.ContainerHelper.getCorrectRanges;
import static com.workday.ContainerHelper.populateContainerData;

import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * container holding worker net salary data
 * 
 * @author eiathom
 *
 */
public final class NetRangeQueryContainer implements RangeContainer {

    /**
     * for logging
     */
    private static final StringBuilder STRING_BUILDER = new StringBuilder(1);

    /**
     * default array of ids to return
     */
    private static final short[] DEFAULT_IDS_ARRAY = new short[0];

    /**
     * default Ids object to return
     */
    private static final Ids DEFAULT_IDS_OBJECT = new WorkerIds(DEFAULT_IDS_ARRAY);

    /**<p>
     * container data</br></br>
     * sorted map chosen to use data values as keys</br>
     * storing sorted values enables sub-listing of data within a range for querying</br>
     * the values, former keys in input data (and our primary retrieval objective), are now retrieved by lookup in a sorted range</br>
     * </p>
     */
    private final SortedMap<Long, Short> data = new ConcurrentSkipListMap<>(getComparator());

    /**
     * 
     * @param data the data to be contained in this container
     */
    public NetRangeQueryContainer(final long[] data) {
        populateContainerData(data, this.data);
    }

    @Override
    public Ids findIdsInRange(final long fromValue, final long toValue, final boolean fromInclusive, final boolean toInclusive) {
        if (isInValidInput(fromValue, toValue) || isUnProcessibleQuery(fromValue, toValue, fromInclusive, toInclusive)) {
            return DEFAULT_IDS_OBJECT;
        }
        final long[] ranges = getCorrectRanges(fromValue, toValue, fromInclusive, toInclusive);
        STRING_BUILDER.delete(0, STRING_BUILDER.length());
        System.out.println(STRING_BUILDER.append(Thread.currentThread().getName()).append(": ").append("start processing"));
        STRING_BUILDER.delete(0, STRING_BUILDER.length());
        final long startTimeInNanoSeconds = System.nanoTime();
        final short[] ids = getIds(ranges[0], ranges[1], fromInclusive, toInclusive);
        System.out.println(
                STRING_BUILDER.append(Thread.currentThread().getName()).append(": ")
                .append("processing took ").append((double)(System.nanoTime() - startTimeInNanoSeconds) / 1000000000.0)
                .append(" ms to find ").append(ids.length).append(" id(s)"));
        STRING_BUILDER.delete(0, STRING_BUILDER.length());
        return new WorkerIds(ids);
    }

    /**
     * 
     * @param startPosition 
     * @param endPosition 
     * @param toInclusive 
     * @param fromInclusive 
     * 
     * @return query matching ids in this container data
     */
    private short[] getIds(final long startPosition, final long endPosition, final boolean fromInclusive, final boolean toInclusive) {
        final SortedMap<Long, Short> subMapView = this.data.subMap(startPosition, endPosition);
        if (subMapView.isEmpty()) {
            return DEFAULT_IDS_ARRAY;
        }
        return getAsSortedArray(subMapView.values());
    }

    /**
     * 
     * specific case where a range distance is 0 and exclusive
     * 
     * @param fromValue
     * @param toValue
     * @param fromInclusive
     * @param toInclusive
     * @return a check on whether to proceed
     */
    private boolean isUnProcessibleQuery(final long fromValue, final long toValue, final boolean fromInclusive, final boolean toInclusive) {
        return (fromValue == toValue) && (!fromInclusive || !toInclusive);
    }

    /**
     * @param fromValue
     * @param toValue
     * 
     * @return whether input is correct to continue
     */
    private boolean isInValidInput(final long fromValue, final long toValue) {
        return fromValue < 0 && toValue < 0;
    }

}
