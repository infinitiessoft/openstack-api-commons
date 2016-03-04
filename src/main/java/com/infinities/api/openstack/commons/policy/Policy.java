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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.infinities.api.openstack.commons.config.Config;
import com.infinities.api.openstack.commons.context.OpenstackRequestContext;
import com.infinities.api.openstack.commons.exception.PolicyNotAuthorizedException;

public class Policy {

	private static Enforcer enforcer = null;

	private String policyFile;
	private String policyDefaultRule;


	@Autowired
	private Policy(Config config) {
		this.policyFile = config.getOpt("policy_file").asText();
		this.policyDefaultRule = config.getOpt("policy_default_rule").asText();
		init(policyFile, null, policyDefaultRule, true);
	}

	private void init(String policyFile, Map<String, BaseCheck> rules, String defaultRule, boolean useConf) {
		if (enforcer == null) {
			enforcer = new EnforcerImpl(policyFile, rules, defaultRule, useConf);
		}
	}

	public static boolean checkIsAdmin(OpenstackRequestContext context) throws Exception {
		Credentials credentials = context;
		Target target = context;
		return enforcer.enforce("context_is_admin", target, credentials, false, null);
	}

	// doRaise=true, exc=null
	public static boolean enforce(OpenstackRequestContext context, String action, Target target, boolean doRaise,
			Exception exc) throws Exception {
		Credentials credentials = context;
		if (exc == null) {
			exc = new PolicyNotAuthorizedException(null, action);
		}
		return enforcer.enforce(action, target, credentials, doRaise, exc);

	}
}
