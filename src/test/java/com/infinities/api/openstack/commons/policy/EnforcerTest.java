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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import com.infinities.api.openstack.commons.policy.EnforcerImpl;
import com.infinities.api.openstack.commons.policy.Target;

public class EnforcerTest {

	protected Mockery context = new JUnit4Mockery() {

		{
			setThreadingPolicy(new Synchroniser());
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	private EnforcerImpl enforcer;
	private Target target;
	private Credentials creds;


	@Before
	public void setUp() throws Exception {
		target = context.mock(Target.class);
		creds = context.mock(Credentials.class);
		enforcer = new EnforcerImpl(null, null, null, true);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetRules() throws MalformedURLException, IOException {
		enforcer.loadRules(true);
		Map<String, BaseCheck> map = enforcer.getRules();
		assertNotNull(map);
		assertFalse(map.isEmpty());
		int size = map.size();
		enforcer.setRules(new HashMap<String, BaseCheck>(), false, false);
		assertEquals(size, enforcer.getRules().size());
		enforcer.setRules(new HashMap<String, BaseCheck>(), false, true);
		assertEquals(size, enforcer.getRules().size());
		enforcer.setRules(new HashMap<String, BaseCheck>(), true, true);
		assertEquals(0, enforcer.getRules().size());
		enforcer.setRules(new HashMap<String, BaseCheck>(), true, false);
		assertTrue(enforcer.getRules().isEmpty());
	}

	@Test
	public void testClear() throws MalformedURLException, IOException {
		enforcer.loadRules(true);
		Map<String, BaseCheck> map = enforcer.getRules();
		assertNotNull(map);
		assertFalse(map.isEmpty());
		enforcer.clear();
		map = enforcer.getRules();
		assertTrue(map.isEmpty());

	}

	@Test
	public void testLoadRules() throws MalformedURLException, IOException {
		enforcer.loadRules(true);
	}

	@Test(expected = IllegalStateException.class)
	public void testEnforce() throws Exception {
		enforcer.loadRules(true);

		final Set<String> roles = new HashSet<String>();
		roles.add("demo");
		context.checking(new Expectations() {

			{
				oneOf(creds).getRoles();
				will(returnValue(roles));
			}
		});
		enforcer.enforce("context_is_admin", target, creds, true, new IllegalStateException("test"));
	}

	@Test
	public void testEnforce2() throws Exception {
		enforcer.loadRules(true);

		final Set<String> roles = new HashSet<String>();
		roles.add("demo");
		context.checking(new Expectations() {

			{
				oneOf(creds).getRoles();
				will(returnValue(roles));
			}
		});
		assertFalse(enforcer.enforce("context_is_admin", target, creds, false, new IllegalStateException("test")));
	}

	@Test
	public void testEnforce3() throws Exception {
		enforcer.loadRules(true);

		final Set<String> roles = new HashSet<String>();
		roles.add("admin");
		context.checking(new Expectations() {

			{
				oneOf(creds).getRoles();
				will(returnValue(roles));
			}
		});
		assertTrue(enforcer.enforce("context_is_admin", target, creds, false, new IllegalStateException("test")));
	}

	@Test
	public void testEnforce4() throws Exception {
		enforcer.loadRules(true);

		context.checking(new Expectations() {

			{
				oneOf(creds).getIsAdmin();
				will(returnValue(true));
			}
		});
		assertTrue(enforcer.enforce("admin_or_owner", target, creds, false, new IllegalStateException("test")));
	}

	@Test
	public void testEnforce5() throws Exception {
		MockTarget target = new MockTarget("test");
		MockCredentials creds = new MockCredentials("test");
		enforcer.loadRules(true);

		assertTrue(enforcer.enforce("admin_or_owner", target, creds, false, new IllegalStateException("test")));
	}

	@Test
	public void testEnforce6() throws Exception {
		MockTarget target = new MockTarget("test2");
		MockCredentials creds = new MockCredentials("test");
		enforcer.loadRules(true);

		assertFalse(enforcer.enforce("admin_or_owner", target, creds, false, new IllegalStateException("test")));
	}

	@Test
	public void testEnforce7() throws Exception {
		MockTarget target = new MockTarget("test");
		MockCredentials creds = new MockCredentials("test2");
		enforcer.loadRules(true);

		assertFalse(enforcer.enforce("admin_or_owner", target, creds, false, new IllegalStateException("test")));
	}

	@Test
	public void testEnforce8() throws Exception {
		MockTarget target = new MockTarget("test");
		MockCredentials creds = new MockCredentials("test");
		enforcer.loadRules(true);

		assertTrue(enforcer.enforce("compute:start", target, creds, false, new IllegalStateException("test")));
	}

	@Test
	public void testEnforce9() throws Exception {
		context.checking(new Expectations() {

			{
				oneOf(creds).getIsAdmin();
				will(returnValue(true));
			}
		});
		enforcer.loadRules(true);

		assertTrue(enforcer.enforce("compute:unlock_override", target, creds, false, new IllegalStateException("test")));
	}
	
	@Test
	public void testEnforce10() throws Exception {
		context.checking(new Expectations() {

			{
				oneOf(creds).getIsAdmin();
				will(returnValue(false));
			}
		});
		enforcer.loadRules(true);

		assertFalse(enforcer.enforce("compute:unlock_override", target, creds, false, new IllegalStateException("test")));
	}

	@Test
	public void testGetRules() throws MalformedURLException, IOException {
		enforcer.loadRules(true);
		Map<String, BaseCheck> map = enforcer.getRules();
		assertNotNull(map);
		assertFalse(map.isEmpty());
	}


	public static class MockTarget implements Target {

		private String project;


		public MockTarget(String project) {
			this.project = project;
		}

		public String getProjectId() {
			return project;
		}

		@Override
		public String getName() {
			return "mock target";
		}

	}

	public static class MockCredentials implements Credentials {

		private String project;


		public MockCredentials(String project) {
			this.project = project;
		}

		public String getProjectId() {
			return project;
		}

		@Override
		public boolean getIsAdmin() {
			return false;
		}

		@Override
		public Set<String> getRoles() {
			return new HashSet<String>();
		}

	}

}
