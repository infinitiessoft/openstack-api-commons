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

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class FileScanner {

	private final static Logger logger = LoggerFactory.getLogger(FileScanner.class);
	private final URL url;
	private final static Map<String, String> replaceMap;
	static {
		Map<String, String> map = new HashMap<String, String>();
		map.put("<None>", "");
		replaceMap = Collections.unmodifiableMap(map);
	}


	public FileScanner(URL url) {
		this.url = url;
	}

	public Table<String, String, String> read() throws IOException {
		logger.debug("Readig from file.");
		Table<String, String, String> table = HashBasedTable.create();
		Scanner scanner = new Scanner(url.openStream());
		try {
			String type = "DEFAULT";
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (!line.startsWith("#") && !line.isEmpty()) {
					if (line.startsWith("[") && line.endsWith("]")) {
						type = line.substring(1, line.length() - 1);
						continue;
					}
					String[] split = line.split("=", 2);
					if (split.length == 2) {
						String value = split[1].trim();
						if (replaceMap.containsKey(split[1].trim())) {
							value = replaceMap.get(value);
						}
						table.put(type, split[0].trim(), value);
					}
				}
			}
		} finally {
			scanner.close();
		}
		logger.debug("Text read in: {}", table);
		return table;
	}
}
