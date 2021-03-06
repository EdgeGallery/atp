/*
 *    Copyright 2021 Huawei Technologies Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.edgegallery.atp.utils.exception;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IllegalRequestException extends IllegalArgumentException {

    private static final long serialVersionUID = 1311109258952411165L;

    private String message;

    private int retCode;

    private List<String> params;

    /**
     * Constructor to create IllegalRequestException with message.
     *
     * @param msg exception message
     */
    public IllegalRequestException(String msg) {
        super(msg);
    }

    /**
     * Constructor to create IllegalRequestException with message, retCode and params.
     * 
     * @param message message
     * @param retCode retCode
     * @param params params
     */
    public IllegalRequestException(String message, int retCode, List<String> params) {
        super(message);
        this.message = message;
        this.retCode = retCode;
        this.params = params;
    }

}
