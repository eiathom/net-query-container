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

import java.util.Collections;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * container holding worker net salary data
 * 
 * @author eiathom
 *
 */
public final class NetRangeQueryContainer implements RangeContainer {

    private static final Logger LOG = LoggerFactory.getLogger(NetRangeQueryContainer.class);

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
            return new WorkerIds(new short[0]);
        }
        final long[] ranges = getCorrectRanges(fromValue, toValue, fromInclusive, toInclusive);
        final long startTimeInNanoSeconds = System.nanoTime();
        LOG.info("start processing");
        final short[] ids = getIds(ranges[0], ranges[1], fromInclusive, toInclusive);
        LOG.info("processing took {}ms to find {} id(s)", (double)(System.nanoTime() - startTimeInNanoSeconds) / 1000000000.0, ids.length);
        return new WorkerIds(ids);
    }

    /**
     * @return the size of data in this container
     */
    int getContainerSize() {
        return this.data.size();
    }

    /**
     * @return the data in this container
     */
    SortedMap<Long, Short> getContainerData() {
        return Collections.unmodifiableSortedMap(this.data);
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
        final SortedMap<Long, Short> subMapView = getContainerData().subMap(startPosition, endPosition);
        if (subMapView.isEmpty()) {
            return new short[0];
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
