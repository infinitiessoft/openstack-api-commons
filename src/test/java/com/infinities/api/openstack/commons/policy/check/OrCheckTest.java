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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.infinities.api.openstack.commons.policy.BaseCheck;
import com.infinities.api.openstack.commons.policy.Credentials;
import com.infinities.api.openstack.commons.policy.Enforcer;
import com.infinities.api.openstack.commons.policy.Target;
import com.infinities.api.openstack.commons.policy.check.OrCheck;

public class OrCheckTest {

	protected Mockery context = new JUnit4Mockery() {

		{
			setThreadingPolicy(new Synchroniser());
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	private Enforcer enforcer;
	private Target target;
	private Credentials creds;
	private OrCheck check;
	private BaseCheck baseCheck1, baseCheck2;


	@Before
	public void setUp() throws Exception {
		enforcer = context.mock(Enforcer.class);
		target = context.mock(Target.class);
		creds = context.mock(Credentials.class);
		baseCheck1 = context.mock(BaseCheck.class, "check1");
		baseCheck2 = context.mock(BaseCheck.class, "check2");
		List<BaseCheck> checks = new ArrayList<BaseCheck>();
		checks.add(baseCheck1);
		checks.add(baseCheck2);
		check = new OrCheck(checks);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCheck() {
		context.checking(new Expectations() {

			{
				oneOf(baseCheck1).check(target, creds, enforcer);
				will(returnValue(true));
				oneOf(baseCheck2).check(target, creds, enforcer);
				will(returnValue(true));
			}
		});
		assertTrue(check.check(target, creds, enforcer));
	}

	@Test
	public void testCheck2() {
		context.checking(new Expectations() {

			{
				oneOf(baseCheck1).check(target, creds, enforcer);
				will(returnValue(false));
				oneOf(baseCheck2).check(target, creds, enforcer);
				will(returnValue(true));
			}
		});
		assertTrue(check.check(target, creds, enforcer));
	}

	@Test
	public void testCheck3() {
		context.checking(new Expectations() {

			{
				oneOf(baseCheck1).check(target, creds, enforcer);
				will(returnValue(true));
				oneOf(baseCheck2).check(target, creds, enforcer);
				will(returnValue(false));
			}
		});
		assertTrue(check.check(target, creds, enforcer));
	}

	@Test
	public void testCheck4() {
		context.checking(new Expectations() {

			{
				oneOf(baseCheck1).check(target, creds, enforcer);
				will(returnValue(false));
				oneOf(baseCheck2).check(target, creds, enforcer);
				will(returnValue(false));
			}
		});
		assertFalse(check.check(target, creds, enforcer));
	}

}
