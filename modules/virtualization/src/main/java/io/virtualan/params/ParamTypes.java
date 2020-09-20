/*
 * Copyright 2018 Virtualan Contributors (https://virtualan.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.virtualan.params;

import java.math.BigDecimal;

/**
 * This is Virtual Service ParamTypes supported.
 * 
 * @author  Elan Thangamani
 * 
 **/
public enum ParamTypes {
    BOOLEAN("java.lang.Boolean") {
        @Override
        public Object getValidMockRequestBody(Param param) {
            return new Boolean(param.getActualValue());
        }

        @Override
        public String getDefaultMessageBody(Param requestBody) {
            return "true/false";
        }

        @Override
        public boolean compareParam(Param param) {
            return Boolean.parseBoolean(param.actualValue) == Boolean
                    .parseBoolean(param.getExpectedValue());
        }
    },
    LONG("java.lang.Long") {
        @Override
        public Object getValidMockRequestBody(Param param) {
            return new Long(param.getActualValue());
        }

        @Override
        public String getDefaultMessageBody(Param requestBody) {
            return "0L";
        }

        @Override
        public boolean compareParam(Param param) {
            return Long.parseLong(param.actualValue) == Long.parseLong(param.getExpectedValue());
        }
    },
    INTEGER("java.lang.Integer") {
        @Override
        public Object getValidMockRequestBody(Param param) {
            return new Integer(param.getActualValue());
        }

        @Override
        public String getDefaultMessageBody(Param requestBody) {
            return "0";
        }

        @Override
        public boolean compareParam(Param param) {
            return Integer.parseInt(param.actualValue) == Integer
                    .parseInt(param.getExpectedValue());
        }
    },
    BYTE("java.lang.Byte") {
        @Override
        public Object getValidMockRequestBody(Param param) {
            return new Byte(param.getActualValue());
        }

        @Override
        public String getDefaultMessageBody(Param requestBody) {
            return "0";
        }

        @Override
        public boolean compareParam(Param param) {
            return Byte.parseByte(param.actualValue) == Byte.parseByte(param.getExpectedValue());
        }
    },
    FLOAT("java.lang.Float") {
        @Override
        public Object getValidMockRequestBody(Param param) {
            return new Float(param.getActualValue());
        }

        @Override
        public String getDefaultMessageBody(Param requestBody) {
            return "0.0";
        }

        @Override
        public boolean compareParam(Param param) {
            return Float.parseFloat(param.actualValue) == Float
                    .parseFloat(param.getExpectedValue());
        }
    },
    DOUBLE("java.lang.Double") {
        @Override
        public Object getValidMockRequestBody(Param param) {
            return new Double(param.getActualValue());
        }

        @Override
        public String getDefaultMessageBody(Param requestBody) {
            return "0.0";
        }

        @Override
        public boolean compareParam(Param param) {
            return Double.parseDouble(param.actualValue) == Double
                    .parseDouble(param.getExpectedValue());
        }
    },
    BIGDECIMAL("java.math.BigDecimal") {
        @Override
        public Object getValidMockRequestBody(Param param) {
            return new BigDecimal(param.getActualValue());
        }

        @Override
        public String getDefaultMessageBody(Param requestBody) {
            return "0.0";
        }

        @Override
        public boolean compareParam(Param param) {
            return new BigDecimal(param.getExpectedValue())
                    .compareTo(new BigDecimal(param.getActualValue())) == 0;
        }
    },

    DEFAULT("Default") {
        @Override
        public Object getValidMockRequestBody(Param param) {
            return param.getActualValue();
        }

        @Override
        public String getDefaultMessageBody(Param param) {
            return "Enter your data";
        }

        @Override
        public boolean compareParam(Param param) {
            return param.getExpectedValue().equals(param.getActualValue());
        }
    };



    String type;

    public String getType() {
        return type;
    }

    ParamTypes(String type) {
        this.type = type;
    }

    public abstract Object getValidMockRequestBody(Param param);

    public abstract String getDefaultMessageBody(Param param);

    public abstract boolean compareParam(Param param);

    public static ParamTypes fromString(String paramType) {
        for (ParamTypes currentType : ParamTypes.values()) {
            if (paramType.equals(currentType.getType())) {
                return currentType;
            }
        }
        return DEFAULT;
    }


}
