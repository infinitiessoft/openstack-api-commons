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
package com.infinities.api.openstack.commons.policy.check;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinities.api.openstack.commons.policy.Credentials;
import com.infinities.api.openstack.commons.policy.Enforcer;
import com.infinities.api.openstack.commons.policy.Target;

public class GenericCheck extends Check {

	private final static Logger logger = LoggerFactory.getLogger(GenericCheck.class);


	@Override
	public String getRule() {
		return "none";
	}

	@Override
	public boolean check(Target target, Credentials creds, Enforcer enforcer) {
		// try {
		String match = this.getMatch();
		Object expect = null;
		Pattern pattern = Pattern.compile("%\\((.+?)\\)s");
		Matcher matcher = pattern.matcher(match);
		if(matcher.matches()){
			match = matcher.group(1);
		}
		
		String[] split = match.split("\\.");
		logger.debug("match: {}, split size: {}", new Object[] { match, split.length });
		if (match.startsWith("target")) {
			if (split.length < 2) {
				logger.warn("unreadable policy");
				return false;
			}
			split = Arrays.copyOfRange(split, 1, split.length);
		}
		try {
			expect = reflectValue(target, split);
		} catch (Exception e) {
			logger.warn("reflect value from tartget failed", e);
			return false;
		}

		if (expect == null) {
			logger.warn("unreadable policy");
			return false;
		}

		split = this.getKind().split("\\.");
		try {
			logger.debug("split size: {}", split.length);
			Object leftval = reflectValue(creds, split);
			logger.debug("expect: {}, actual: {}", new Object[] { expect, leftval });

			return expect.equals(leftval);
		} catch (Exception e) {
			logger.warn("reflect value from credentials failed", e);
			return false;
		}
	}

	private String concentrateMethodName(String orig) {
		if (orig.contains("_")) {
			String[] split = orig.split("_");
			orig = "";
			for (String s : split) {
				orig += Character.toUpperCase(s.charAt(0)) + s.substring(1);
			}
		}
		return "get" + orig;
	}

	private Object reflectValue(Object target, String[] split) throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method m = null;
		Object obj = target;
		for (int i = 0; i < split.length; i++) {
			String methodName = concentrateMethodName(split[i]);
			logger.debug("reflect method: {}", methodName);
			m = target.getClass().getMethod(methodName);
			obj = m.invoke(obj);
		}

		return obj;
	}

	@Override
	public Check newInstance(String kind, String match) {
		Check check = new GenericCheck();
		check.setKind(kind);
		check.setMatch(match);
		return check;
	}
}