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

/**
 * @author eiathom
 *
 */
public interface RangeQueryContainerFactory {

    /**
     * builds an immutable container optimized for range queries.
     * Data is expected to be 32k items or less.
     * The position in the 'data' array represents the 'id' for that instance in question.
     * For the 'PayrollResult' example before, the 'id' might be 
     * the workers employee number, the data value is the corresponding net pay.
     * E.g, data[5]=2000 means that employee #6 has net pay of 2000.
     */
    RangeContainer createContainer(final long[] data);

}
