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

import java.util.List;

import jersey.repackaged.com.google.common.base.Joiner;

import com.infinities.api.openstack.commons.policy.BaseCheck;
import com.infinities.api.openstack.commons.policy.Credentials;
import com.infinities.api.openstack.commons.policy.Enforcer;
import com.infinities.api.openstack.commons.policy.Target;

public class AndCheck implements BaseCheck {

	private final List<BaseCheck> rules;


	public AndCheck(List<BaseCheck> rules) {
		this.rules = rules;
	}

	@Override
	public String getRule() {
		return "and";
	}

	@Override
	public boolean check(Target target, Credentials creds, Enforcer enforcer) {

		for (BaseCheck rule : rules) {
			if (!rule.check(target, creds, enforcer)) {
				return false;
			}
		}
		return true;
	}

	public void addCheck(BaseCheck check) {
		this.rules.add(check);
	}

	public String toString() {
		String join = Joiner.on(" and ").join(rules);
		return String.format("(%s)", join);
	}
}
