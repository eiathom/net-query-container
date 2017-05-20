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
 * a specialized container of records optimized for efficient range queries on an attribute of the data.
 * 
 * @author eiathom
 * 
 */
public interface RangeContainer {

    /**
     * @return the Ids of all instances found in the container that have data value between fromValue and toValue with optional inclusivity
     */
    Ids findIdsInRange(final long fromValue, final long toValue, final boolean fromInclusive, final boolean toInclusive);
}
