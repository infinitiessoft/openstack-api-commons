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

import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.infinities.api.openstack.commons.policy.Credentials;
import com.infinities.api.openstack.commons.policy.Enforcer;
import com.infinities.api.openstack.commons.policy.Target;
import com.infinities.api.openstack.commons.policy.check.IsAdminCheck;

public class IsAdminCheckTest {

	protected Mockery context = new JUnit4Mockery() {

		{
			setThreadingPolicy(new Synchroniser());
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	private Enforcer enforcer;
	private Target target;
	private Credentials creds;
	private IsAdminCheck check;


	@Before
	public void setUp() throws Exception {
		enforcer = context.mock(Enforcer.class);
		target = context.mock(Target.class);
		creds = context.mock(Credentials.class);
		check = new IsAdminCheck();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCheck() {
		check.setKind("kind");
		check.setMatch("true");
		context.checking(new Expectations() {

			{
				oneOf(creds).getIsAdmin();
				will(returnValue(true));
			}
		});
		assertTrue(check.check(target, creds, enforcer));
	}
	
	@Test
	public void testCheck2() {
		check.setKind("kind");
		check.setMatch("false");
		context.checking(new Expectations() {

			{
				oneOf(creds).getIsAdmin();
				will(returnValue(false));
			}
		});
		assertTrue(check.check(target, creds, enforcer));
	}
	
	@Test
	public void testCheck3() {
		check.setKind("kind");
		check.setMatch("false");
		context.checking(new Expectations() {

			{
				oneOf(creds).getIsAdmin();
				will(returnValue(true));
			}
		});
		assertFalse(check.check(target, creds, enforcer));
	}
	
	@Test
	public void testCheck4() {
		check.setKind("kind");
		check.setMatch("true");
		context.checking(new Expectations() {

			{
				oneOf(creds).getIsAdmin();
				will(returnValue(false));
			}
		});
		assertFalse(check.check(target, creds, enforcer));
	}

}
