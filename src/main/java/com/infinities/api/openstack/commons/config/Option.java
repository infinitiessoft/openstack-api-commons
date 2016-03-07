/*******************************************************************************
 * Copyright 2015 InfinitiesSoft Solutions Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.infinities.api.openstack.commons.config;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class Option {

	private final boolean secret;
	private final String name;
	private String value;


	public Option(String name, boolean secret, String value) {
		this.name = name;
		this.value = value;
		this.secret = secret;
	}

	public boolean isSecret() {
		return secret;
	}

	public void resetValue(String value) {
		setValue(value);
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String asText() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int asInteger() {
		return Integer.parseInt(value);
	}

	public boolean asBoolean() {
		return Boolean.parseBoolean(value);
	}

	public List<String> asList() {
		String regex = ",";
		value = offBucket(value);
		List<String> listValue = Arrays.asList(value.split(regex));
		return listValue;
	}

	public URI asURI() {
		throw new IllegalArgumentException("value is not a uri");
	}

	public String getDest() {
		return name.replace('-', '_');
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Option other = (Option) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	private String offBucket(String value) {
		if (value.length() < 2) {
			throw new IllegalArgumentException(value + " length < 2.");
		}
		if (value.charAt(0) != '[' || value.charAt(value.length() - 1) != ']') {
			throw new IllegalArgumentException(value + " is not a list.");
		}
		return value.substring(1, value.length() - 1).trim();
	}

}
