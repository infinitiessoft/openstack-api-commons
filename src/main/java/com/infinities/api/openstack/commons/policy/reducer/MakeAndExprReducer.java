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
package com.infinities.api.openstack.commons.policy.reducer;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinities.api.openstack.commons.policy.BaseCheck;
import com.infinities.api.openstack.commons.policy.BaseReducer;
import com.infinities.api.openstack.commons.policy.check.AndCheck;

public class MakeAndExprReducer extends AbstractReducer {

	private final static List<List<String>> reducers = Lists.newArrayList();

	static {
		List<String> reducer1 = Lists.newArrayList();
		reducer1.add("check");
		reducer1.add("and");
		reducer1.add("check");
		reducers.add(reducer1);
	}


	public MakeAndExprReducer(BaseReducer reducer) {
		super(reducer);
	}

	@Override
	public List<List<String>> getReducers() {
		return reducers;
	}

	@Override
	public Entry<String, BaseCheck> getEntry(List<Entry<String, BaseCheck>> entry) {
		List<BaseCheck> checks = Lists.newArrayList();
		checks.add(entry.get(0).getValue());
		checks.add(entry.get(2).getValue());
		BaseCheck andCheck = new AndCheck(checks);
		return Maps.immutableEntry("and_expr", andCheck);
	}
}
