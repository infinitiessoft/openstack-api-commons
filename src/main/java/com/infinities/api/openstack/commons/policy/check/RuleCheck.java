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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinities.api.openstack.commons.policy.Credentials;
import com.infinities.api.openstack.commons.policy.Enforcer;
import com.infinities.api.openstack.commons.policy.Target;

public class RuleCheck extends Check {

	private final static Logger logger = LoggerFactory.getLogger(RuleCheck.class);


	@Override
	public String getRule() {
		return "rule";
	}

	@Override
	public boolean check(Target target, Credentials creds, Enforcer enforcer) {
		try {
			return enforcer.getRules().get(this.getMatch()).check(target, creds, enforcer);
		} catch (Exception e) {
			logger.warn("invalid match", e);
			return false;
		}
	}

	@Override
	public Check newInstance(String kind, String match) {
		logger.debug("new rule check: {}, {}", new Object[] { kind, match });
		Check check = new RuleCheck();
		check.setKind(kind);
		check.setMatch(match);
		return check;
	}
}
