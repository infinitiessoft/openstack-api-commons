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
package com.infinities.api.openstack.commons.policy;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.infinities.api.openstack.commons.exception.PolicyNotAuthorizedException;

public class EnforcerImpl implements Enforcer {

	private final static Logger logger = LoggerFactory.getLogger(EnforcerImpl.class);
	private Rules rules;
	private String defaultRule;
	private String policyFile;
	private boolean useConf;


	// policyFile=null,rules=null,defaultRule=null,useConf = true
	public EnforcerImpl(String policyFile, Map<String, BaseCheck> rules, String defaultRule, boolean useConf) {
		this.rules = new Rules(rules, defaultRule);
		this.defaultRule = defaultRule;
		this.policyFile = policyFile;
		this.useConf = useConf;
	}

	@Override
	public void setRules(Map<String, BaseCheck> rules, boolean overwrite, boolean useConf) {
		this.useConf = useConf;
		if (overwrite) {
			logger.debug("default rule is:{}", defaultRule);
			this.rules = new Rules(rules, defaultRule);
		} else {
			this.rules.putAll(rules);
		}
	}

	@Override
	public void clear() {
		setRules(new HashMap<String, BaseCheck>(), true, false);
		this.defaultRule = null;
	}

	@Override
	public void loadRules(boolean forceReload) throws MalformedURLException, IOException {
		if (forceReload) {
			this.useConf = forceReload;
		}

		if (useConf) {
			String data = Files.asCharSource(new File(policyFile), Charsets.UTF_8).read();
			// Resources.toString(new URL(policyPath), Charsets.UTF_8);
			// boolean reloaded = true;

			if (rules == null || rules.isEmpty()) {
				Rules rules = Rules.loadJson(data, defaultRule);
				setRules(rules.getRules(), true, false);
				logger.debug("Rule successfully reloaded");
			}
		}
	}

	@Override
	public boolean enforce(Object rule, Target target, Credentials creds, boolean doRaise, Exception ex) throws Exception {
		loadRules(false);
		boolean result = false;
		if (rule instanceof BaseCheck) {
			result = ((BaseCheck) rule).check(target, creds, this);
		} else if (rules == null) {
			result = false;
		} else {
			try {
				result = rules.get(rule).check(target, creds, this);
			} catch (Exception e) {
				logger.debug("Rule [{}] doesn't exist", rule, e);
				result = false;
			}
		}

		if (doRaise && !result) {
			if (ex != null) {
				throw ex;
			}

			throw new PolicyNotAuthorizedException(null, rule);
		}
		return result;
	}

	@Override
	public Rules getRules() {
		return this.rules;
	}
}
