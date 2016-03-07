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

import java.util.List;

public class Options {

	public static Option newOpt(String name, boolean secret, String defaultVal) {
		return new Option(name, secret, defaultVal);
	}

	public static Option newOpt(String name, String defaultVal) {
		return new Option(name, false, defaultVal);
	}

	// public static Option newOpt(String name) {
	// return new Option(name, false, "");
	// }

	public static Option newOpt(String name, int defaultValue) {
		return newOpt(name, String.valueOf(defaultValue));
	}

	public static Option newOpt(String name, boolean defaultValue) {
		return newOpt(name, String.valueOf(defaultValue));
	}

	public static Option newOpt(String name, List<String> defaultValue) {
		return newOpt(name, String.valueOf(defaultValue));
	}
	// public static Option newMultiStrOpt(String name, List<String>
	// defaultValue) {
	// return null;
	// }

}
