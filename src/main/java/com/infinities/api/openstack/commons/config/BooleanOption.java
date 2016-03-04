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


public class BooleanOption extends Option {

	private boolean boolValue;


	public BooleanOption(String name, String value) {
		super(name, value);
		boolValue = Boolean.parseBoolean(value);
	}

	public BooleanOption(String name, boolean boolValue) {
		super(name, String.valueOf(boolValue));
		this.boolValue = boolValue;
	}

	@Override
	public boolean asBoolean() {
		return boolValue;
	}

	@Override
	public void resetValue(String value) {
		setValue(value);
		boolValue = Boolean.parseBoolean(value);
	}

}
