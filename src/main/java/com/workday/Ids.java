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
 * 
 * and iterator of Ids
 * 
 * @author eiathom
 *
 */
public interface Ids {

    static final short END_OF_IDS = -1;

    /** 
     * The ids should be in sorted order (from lower to higher) to facilitate the query distribution into multiple containers.
     * 
     * @return the next id in sequence, or -1 if at end of data
     * */
    short nextId();

}
