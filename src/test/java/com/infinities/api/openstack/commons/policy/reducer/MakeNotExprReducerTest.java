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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import jersey.repackaged.com.google.common.collect.Maps;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.infinities.api.openstack.commons.policy.BaseCheck;
import com.infinities.api.openstack.commons.policy.BaseReducer;
import com.infinities.api.openstack.commons.policy.Credentials;
import com.infinities.api.openstack.commons.policy.Enforcer;
import com.infinities.api.openstack.commons.policy.Target;
import com.infinities.api.openstack.commons.policy.check.NotCheck;
import com.infinities.api.openstack.commons.policy.check.StringCheck;
import com.infinities.api.openstack.commons.policy.reducer.MakeNotExprReducer;

public class MakeNotExprReducerTest {

	protected Mockery context = new JUnit4Mockery() {

		{
			setThreadingPolicy(new Synchroniser());
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	private MakeNotExprReducer reducer;
	private BaseReducer base;
	private Target target;
	private Credentials creds;
	private Enforcer enforcer;
	private BaseCheck check1;


	@Before
	public void setUp() throws Exception {
		base = context.mock(BaseReducer.class);
		reducer = new MakeNotExprReducer(base);
		target = context.mock(Target.class);
		creds = context.mock(Credentials.class);
		enforcer = context.mock(Enforcer.class);
		check1 = context.mock(BaseCheck.class, "check1");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetEntry() {
		List<Entry<String, BaseCheck>> entrys = new ArrayList<Entry<String, BaseCheck>>();
		Entry<String, BaseCheck> entry1 = Maps.immutableEntry("not", (BaseCheck) new StringCheck("not"));
		Entry<String, BaseCheck> entry2 = Maps.immutableEntry("check", check1);
		entrys.add(entry1);
		entrys.add(entry2);
		Entry<String, BaseCheck> ret = reducer.getEntry(entrys);
		assertEquals("check", ret.getKey());
		assertTrue(ret.getValue() instanceof NotCheck);
		NotCheck retCheck = (NotCheck) ret.getValue();

		context.checking(new Expectations() {

			{
				oneOf(check1).check(target, creds, enforcer);
				will(returnValue(true));
			}
		});
		assertFalse(retCheck.check(target, creds, enforcer));
	}

	@Test
	public void testGetEntry2() {
		List<Entry<String, BaseCheck>> entrys = new ArrayList<Entry<String, BaseCheck>>();
		Entry<String, BaseCheck> entry1 = Maps.immutableEntry("not", (BaseCheck) new StringCheck("not"));
		Entry<String, BaseCheck> entry2 = Maps.immutableEntry("check", check1);
		entrys.add(entry1);
		entrys.add(entry2);
		Entry<String, BaseCheck> ret = reducer.getEntry(entrys);
		assertEquals("check", ret.getKey());
		assertTrue(ret.getValue() instanceof NotCheck);
		NotCheck retCheck = (NotCheck) ret.getValue();

		context.checking(new Expectations() {

			{
				oneOf(check1).check(target, creds, enforcer);
				will(returnValue(false));
			}
		});
		assertTrue(retCheck.check(target, creds, enforcer));
	}

	@Test
	public void testReduce() {
		final List<Entry<String, BaseCheck>> entrys = new ArrayList<Entry<String, BaseCheck>>();
		Entry<String, BaseCheck> entry1 = Maps.immutableEntry("not", (BaseCheck) new StringCheck("not"));
		Entry<String, BaseCheck> entry2 = Maps.immutableEntry("check2", check1);
		entrys.add(entry1);
		entrys.add(entry2);
		final BaseCheck check4 = context.mock(BaseCheck.class, "check4");
		final Entry<String, BaseCheck> entry = Maps.immutableEntry("check4", check4);
		context.checking(new Expectations() {

			{
				oneOf(base).reduce(entrys);
				will(returnValue(entry));

			}
		});
		Entry<String, BaseCheck> ret = reducer.reduce(entrys);
		assertEquals(entry, ret);
	}

	@Test
	public void testReduce2() {
		List<Entry<String, BaseCheck>> entrys = new ArrayList<Entry<String, BaseCheck>>();
		Entry<String, BaseCheck> entry1 = Maps.immutableEntry("not", (BaseCheck) new StringCheck("not"));
		Entry<String, BaseCheck> entry2 = Maps.immutableEntry("check", check1);
		entrys.add(entry1);
		entrys.add(entry2);
		context.checking(new Expectations() {

			{

				oneOf(check1).check(target, creds, enforcer);
				will(returnValue(true));
			}
		});
		Entry<String, BaseCheck> ret = reducer.reduce(entrys);
		assertEquals("check", ret.getKey());
		assertTrue(ret.getValue() instanceof NotCheck);
		assertFalse(ret.getValue().check(target, creds, enforcer));
	}

	@Test
	public void testReduce3() {
		List<Entry<String, BaseCheck>> entrys = new ArrayList<Entry<String, BaseCheck>>();
		Entry<String, BaseCheck> entry1 = Maps.immutableEntry("not", (BaseCheck) new StringCheck("not"));
		Entry<String, BaseCheck> entry2 = Maps.immutableEntry("check", check1);
		entrys.add(entry1);
		entrys.add(entry2);
		context.checking(new Expectations() {

			{

				oneOf(check1).check(target, creds, enforcer);
				will(returnValue(false));
			}
		});
		Entry<String, BaseCheck> ret = reducer.reduce(entrys);
		assertEquals("check", ret.getKey());
		assertTrue(ret.getValue() instanceof NotCheck);
		assertTrue(ret.getValue().check(target, creds, enforcer));
	}

}
