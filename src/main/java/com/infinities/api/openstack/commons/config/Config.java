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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class Config {

	private final Logger logger = LoggerFactory.getLogger(Config.class);
	private final Table<String, String, Option> OPTIONS = HashBasedTable.create();
	private final Pattern pattern = Pattern.compile("%\\((.*?)\\)", Pattern.DOTALL);


	public Config(String path) throws IOException {
		preSetup();
		parseConfigFiles(path);
	}

	private void preSetup() {
		// use keystone or not
		OPTIONS.put("DEFAULT", "auth_strategy", Options.newStrOpt("auth_strategy", "keystone"));
		OPTIONS.put("DEFAULT", "use_forwarded_for", Options.newBoolOpt("use_forwarded_for", false));
		// nova.openstack.common.policy
		OPTIONS.put("DEFAULT", "policy_file", Options.newStrOpt("policy_file", "policy.json"));
		OPTIONS.put("DEFAULT", "policy_default_rule", Options.newStrOpt("policy_default_rule", "default"));
		// nova.api.sizelimit
		OPTIONS.put("DEFAULT", "osapi_max_request_body_size", Options.newIntOpt("osapi_max_request_body_size", 114688));
		OPTIONS.put("DEFAULT", "fatal_exception_format_errors", Options.newBoolOpt("fatal_exception_format_errors", false));
	}

	private void parseConfigFiles(String path) throws IOException {
		FileScanner scanner = new FileScanner(getURL(path));
		Table<String, String, String> customTable = scanner.read();
		for (Cell<String, String, String> cell : customTable.cellSet()) {
			if (OPTIONS.contains(cell.getRowKey(), cell.getColumnKey())) {
				logger.debug("reset FILE_OPTIONS {}.{} = {}",
						new Object[] { cell.getRowKey(), cell.getColumnKey(), cell.getValue() });
				OPTIONS.get(cell.getRowKey(), cell.getColumnKey()).resetValue(cell.getValue());
			} else {
				OPTIONS.put(cell.getRowKey(), cell.getColumnKey(), Options.newStrOpt(cell.getValue()));
			}
		}
	}

	public Option getOpt(String type, String attr) {
		Option option = OPTIONS.get(type, attr);
		if (option instanceof StringOption) {
			Option newOption = Options.newStrOpt(option.getName(), option.getValue());
			Matcher matcher = pattern.matcher(option.asText());
			while (matcher.find()) {
				String match = matcher.group(1);

				logger.debug("sub-option pattern match: {}", match);
				Option suboption = OPTIONS.get(type, match);
				if (suboption != null) {
					String newValue = matcher.replaceFirst(suboption.getValue());
					newOption.setValue(newValue);
				}
			}
			return newOption;
		} else {
			return option;
		}
	}

	public Option getOpt(String attr) {
		return getOpt("DEFAULT", attr);
	}

	public URL getURL(String filePath) {
		File file = new File(filePath);
		logger.debug("Config file path:{}", file.getAbsolutePath());
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
