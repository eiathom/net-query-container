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

import static com.workday.ContainerHelper.getCorrectRanges;

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

    /**
     * container data array
     * would rather it be immutable - but also, prefer being space aware
     */
    private long[] data;

    /**
     * mutable length position of next available slot in the container data array
     */
    private int pointerPosition;

    /**
     * creates a memory efficient, size defined, primitive data array
     * 
     * @param data the data contained
     */
    public NetRangeQueryContainer(final long[] data) {
        this.data = new long[data.length];
        this.pointerPosition = 0;
        add(data);
    }

    @Override
    public Ids findIdsInRange(final long fromValue, final long toValue, final boolean fromInclusive, final boolean toInclusive) {
        if (isValidInput(fromValue, toValue)) {
            final long[] correctRanges = getCorrectRanges(fromValue, toValue);
            final long startTimeInNanoSeconds = System.nanoTime();
            LOG.info("start processing");
            final short[] ids = getIds(correctRanges[0], correctRanges[1], fromInclusive, toInclusive);
            LOG.info("processing took {} ns", System.nanoTime() - startTimeInNanoSeconds);
            return new WorkerIds(ids);
        }
        return new WorkerIds(new short[0]);
    }

    /**
     * @param startPosition 
     * @param endPosition 
     * @param toInclusive 
     * @param fromInclusive 
     * 
     * @return query matching ids in this container data array
     */
    private short[] getIds(final long startPosition, final long endPosition, final boolean fromInclusive, final boolean toInclusive) {
        short[] ids = new short[1];
        int idsIndex = 0;
        for (int searchIndex = 0; searchIndex < this.pointerPosition; searchIndex++) {
            short toAdd = -1;
            if (isMatching(startPosition, endPosition, searchIndex, fromInclusive, toInclusive)) {
                toAdd = (short) searchIndex;
            }
            if (toAdd != -1) {
                ids[idsIndex] = toAdd;
                idsIndex++;
                ids = checkCapacity(idsIndex, ids);
            }
        }
        short[] copy = new short[idsIndex];
        System.arraycopy(ids, 0, copy, 0, idsIndex);
        ids = copy;
        return ids;
    }

    /**
     * @param startPosition
     * @param endPosition
     * @param searchIndex
     * @param toInclusive 
     * @param fromInclusive 
     * 
     * @return match data in this container data array to a range condition
     */
    private boolean isMatching(final long startPosition, final long endPosition, final int searchIndex, final boolean fromInclusive, final boolean toInclusive) {
        if (!fromInclusive && !toInclusive) return this.data[searchIndex] > startPosition && this.data[searchIndex] < endPosition;
        if (fromInclusive && !toInclusive) return this.data[searchIndex] >= startPosition && this.data[searchIndex] < endPosition;
        if (!fromInclusive && toInclusive) return this.data[searchIndex] > startPosition && this.data[searchIndex] <= endPosition;
        return this.data[searchIndex] >= startPosition && this.data[searchIndex] <= endPosition;
    }

    /**
     * @param fromInclusive
     * @param toValue
     * 
     * @return whether input is correct to continue
     */
    private boolean isValidInput(final long fromValue, final long toValue) {
        return fromValue > -1 && toValue > -1;
    }

    /**
     * populate the container data array
     * 
     * @param data data to populate this container data array with
     */
    private void add(final long[] data) {
        add(data, 0, data.length);
    }

    /**
     * populate the container data array at a specified position
     * 
     * @param data data to populate this container data array with
     * @param insertPosition the position in the container data array to add this data
     * @param lengthOfInputDataToAdd the size of input data to populate this container data array with
     */
    private void add(final long[] data, final int insertPosition, final int lengthOfInputDataToAdd) {
        checkCapacity(this.pointerPosition + lengthOfInputDataToAdd);
        System.arraycopy(data, insertPosition, this.data, insertPosition, lengthOfInputDataToAdd);
        this.pointerPosition += lengthOfInputDataToAdd;
    }

    /**
     * provision more space for data if needed
     * 
     * @param proposedCapacity check current container data array capacity for input data
     */
    private void checkCapacity(final int proposedCapacity) {
        if (proposedCapacity >= this.data.length) {
            final int capacity = ((this.data.length * 2) > proposedCapacity) ? (this.data.length * 2) : proposedCapacity;
            final long[] temporaryData = new long[capacity];
            System.arraycopy(this.data, 0, temporaryData, 0, this.data.length);
            this.data = temporaryData;
        }
    }

    /**
     * provision more space for data if needed
     * 
     * @param proposedCapacity check current container data array capacity for input data
     * @param data the data to have it's capacity increased
     * 
     * @return the increased capacity data
     */
    private short[] checkCapacity(final int proposedCapacity, short[] data) {
        if (proposedCapacity >= data.length) {
            final int capacity = ((data.length * 2) > proposedCapacity) ? (data.length * 2) : proposedCapacity;
            final short[] temporaryData = new short[capacity];
            System.arraycopy(data, 0, temporaryData, 0, data.length);
            data = temporaryData;
        }
        return data;
    }

}
