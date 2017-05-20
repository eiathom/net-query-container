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

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;

/**
 * @author eiathom
 *
 */
public class RangeQueryBasicTest {

    private static final int NUMBER_OF_WORKERS = 32000;

    private static final int MAXIMUM_NET_SALARY = 75000;

    private static final int MINIMUM_NET_SALARY = 1000;

    private RangeContainer container;

    @Before
    public void setUp() {
        RangeQueryContainerFactory rf = new NetRangeQueryContainerFactory();
        container = rf.createContainer(new long[]{10, 12, 17, 21, 2, 15, 16});
    }

    @Test
    public void runARangeQuery() {
        Ids ids = container.findIdsInRange(14, 17, true, true);
        assertEquals(2, ids.nextId());
        assertEquals(5, ids.nextId());
        assertEquals(6, ids.nextId());
        assertEquals(Ids.END_OF_IDS, ids.nextId());
        ids = container.findIdsInRange(14, 17, true, false);
        assertEquals(5, ids.nextId());
        assertEquals(6, ids.nextId());
        assertEquals(Ids.END_OF_IDS, ids.nextId());
        ids = container.findIdsInRange(20, Long.MAX_VALUE, false, true);
        assertEquals(3, ids.nextId());
        assertEquals(Ids.END_OF_IDS, ids.nextId());
    }

    @Test
    public void runARangeQuery_withValuesInWrongOrder_expectCorrectResult() {
        Ids ids = container.findIdsInRange(17, 14, true, true);
        assertEquals(2, ids.nextId());
        assertEquals(5, ids.nextId());
        assertEquals(6, ids.nextId());
        assertEquals(Ids.END_OF_IDS, ids.nextId());
    }

    @Test
    public void runARangeQueryExclusive() {
        Ids ids = container.findIdsInRange(14, 17, false, false);
        assertEquals(5, ids.nextId());
        assertEquals(6, ids.nextId());
        assertEquals(Ids.END_OF_IDS, ids.nextId());
    }

    @Test
    public void runARangeQuery_withEqualInputs_andInclusive_expectValidResult() {
        Ids ids = container.findIdsInRange(17, 17, true, true);
        assertEquals(2, ids.nextId());
        assertEquals(Ids.END_OF_IDS, ids.nextId());
    }

    @Test
    public void runARangeQuery_withEqualInputs_andExclusive_expectDefaultValue() {
        Ids ids = container.findIdsInRange(17, 17, false, false);
        assertEquals(Ids.END_OF_IDS, ids.nextId());
    }

    @Test
    public void runARangeQuery_withEqualInputs_andExclusive_expectDefaultValue2() {
        Ids ids = container.findIdsInRange(17, 17, false, true);
        assertEquals(Ids.END_OF_IDS, ids.nextId());
    }

    @Test
    public void runARangeQuery_withEqualInputs_andExclusive_expectDefaultValue3() {
        Ids ids = container.findIdsInRange(17, 17, false, true);
        assertEquals(Ids.END_OF_IDS, ids.nextId());
    }

    @Test
    public void runARangeQuery_withInvalidInputs_expectDefaultValue() {
        Ids ids = container.findIdsInRange(-1, -1, true, false);
        assertEquals(Ids.END_OF_IDS, ids.nextId());
    }

    @Test
    public void basicPerformanceTesting() {
        testBuilder(1000, 2000);
        testBuilder(10000, 100000);
        testBuilder(0, 1000000);
        testBuilder(1000000, 0);
    }

    private void testBuilder(final int netSalaryBottomRange, final int netSalaryTopRange) {
        final long[] data = getRandonData(NUMBER_OF_WORKERS);
        int expectedNumberOfIds = 0;
        int actualNumberOfIds = 0;
        final long[] correctedRanges = ContainerHelper.getCorrectRanges(netSalaryBottomRange, netSalaryTopRange);
        for (int range = 0; range < data.length; range++) {
            if (data[range] > correctedRanges[0] && data[range] < correctedRanges[1]) {
                expectedNumberOfIds += range;
            }
        }
        final RangeQueryContainerFactory factory = new NetRangeQueryContainerFactory();
        final RangeContainer container = factory.createContainer(data);
        final Ids ids = container.findIdsInRange(netSalaryBottomRange, netSalaryTopRange, false, false);
        short id = 0;
        while ((id = ids.nextId()) != Ids.END_OF_IDS) {
            actualNumberOfIds += id;
        }
        assertEquals(expectedNumberOfIds, actualNumberOfIds);
    }

    private long[] getRandonData(final int numberOfWorkers) {
        long[] data = new long[numberOfWorkers];
        for (int index = 0; index < numberOfWorkers; index++) {
            data[index] = ThreadLocalRandom.current().nextLong(MINIMUM_NET_SALARY, MAXIMUM_NET_SALARY);
        }
        return data;
    }

}
