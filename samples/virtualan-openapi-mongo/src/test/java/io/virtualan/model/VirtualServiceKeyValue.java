/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
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

package io.virtualan.model;

public class VirtualServiceKeyValue {

	private String key;
	private String value;
	private Class type;
	private VirtualServiceType serviceType;

	public VirtualServiceKeyValue() {

	}

	public VirtualServiceKeyValue(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public VirtualServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(VirtualServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "VirtualServiceKeyValue [key=" + key + ", value=" + value + ", type=" + type + "]";
	}

}
