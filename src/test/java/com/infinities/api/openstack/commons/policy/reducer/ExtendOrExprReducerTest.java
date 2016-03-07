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
import com.infinities.api.openstack.commons.policy.check.OrCheck;
import com.infinities.api.openstack.commons.policy.reducer.ExtendOrExprReducer;

public class ExtendOrExprReducerTest {

	protected Mockery context = new JUnit4Mockery() {

		{
			setThreadingPolicy(new Synchroniser());
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	private ExtendOrExprReducer reducer;
	private BaseReducer base;
	private Target target;
	private Credentials creds;
	private Enforcer enforcer;
	private BaseCheck check2, check3, check4, check5;
	private OrCheck orCheck;


	@Before
	public void setUp() throws Exception {
		base = context.mock(BaseReducer.class);
		reducer = new ExtendOrExprReducer(base);
		target = context.mock(Target.class);
		creds = context.mock(Credentials.class);
		enforcer = context.mock(Enforcer.class);
		// check1 = context.mock(BaseCheck.class, "check1");
		check2 = context.mock(BaseCheck.class, "check2");
		check3 = context.mock(BaseCheck.class, "check3");
		check4 = context.mock(BaseCheck.class, "check4");
		check5 = context.mock(BaseCheck.class, "check5");
		List<BaseCheck> checks = new ArrayList<BaseCheck>();
		checks.add(check4);
		checks.add(check5);
		orCheck = new OrCheck(checks);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetEntry() {
		List<Entry<String, BaseCheck>> entrys = new ArrayList<Entry<String, BaseCheck>>();
		Entry<String, BaseCheck> entry1 = Maps.immutableEntry("check", (BaseCheck) orCheck);
		Entry<String, BaseCheck> entry2 = Maps.immutableEntry("check2", check2);
		Entry<String, BaseCheck> entry3 = Maps.immutableEntry("check3", check3);
		entrys.add(entry1);
		entrys.add(entry2);
		entrys.add(entry3);
		Entry<String, BaseCheck> ret = reducer.getEntry(entrys);
		assertEquals("or_expr", ret.getKey());
		assertTrue(ret.getValue() instanceof OrCheck);
		OrCheck retCheck = (OrCheck) ret.getValue();

		context.checking(new Expectations() {

			{
				exactly(1).of(check3).check(target, creds, enforcer);
				will(returnValue(true));

				oneOf(check4).check(target, creds, enforcer);
				will(returnValue(true));

				exactly(1).of(check5).check(target, creds, enforcer);
				will(returnValue(true));
			}
		});
		assertTrue(retCheck.check(target, creds, enforcer));
	}

	@Test
	public void testReduce() {
		final List<Entry<String, BaseCheck>> entrys = new ArrayList<Entry<String, BaseCheck>>();
		Entry<String, BaseCheck> entry1 = Maps.immutableEntry("check", (BaseCheck) orCheck);
		Entry<String, BaseCheck> entry2 = Maps.immutableEntry("check2", check2);
		Entry<String, BaseCheck> entry3 = Maps.immutableEntry("check3", check3);
		entrys.add(entry1);
		entrys.add(entry2);
		entrys.add(entry3);
		final BaseCheck check6 = context.mock(BaseCheck.class, "check6");
		final Entry<String, BaseCheck> entry = Maps.immutableEntry("check6", check6);
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
		final List<Entry<String, BaseCheck>> entrys = new ArrayList<Entry<String, BaseCheck>>();
		Entry<String, BaseCheck> entry1 = Maps.immutableEntry("or_expr", (BaseCheck) orCheck);
		Entry<String, BaseCheck> entry2 = Maps.immutableEntry("or", check2);
		Entry<String, BaseCheck> entry3 = Maps.immutableEntry("check", check3);
		entrys.add(entry1);
		entrys.add(entry2);
		entrys.add(entry3);
		context.checking(new Expectations() {

			{

				exactly(1).of(check3).check(target, creds, enforcer);
				will(returnValue(true));

				oneOf(check4).check(target, creds, enforcer);
				will(returnValue(true));

				exactly(1).of(check5).check(target, creds, enforcer);
				will(returnValue(true));
			}
		});
		Entry<String, BaseCheck> ret = reducer.reduce(entrys);
		assertEquals("or_expr", ret.getKey());
		assertTrue(ret.getValue() instanceof OrCheck);
		assertTrue(ret.getValue().check(target, creds, enforcer));
	}

	@Test
	public void testReduce3() {
		final List<Entry<String, BaseCheck>> entrys = new ArrayList<Entry<String, BaseCheck>>();
		Entry<String, BaseCheck> entry1 = Maps.immutableEntry("or_expr", (BaseCheck) orCheck);
		Entry<String, BaseCheck> entry2 = Maps.immutableEntry("or", check2);
		Entry<String, BaseCheck> entry3 = Maps.immutableEntry("check", check3);
		entrys.add(entry1);
		entrys.add(entry2);
		entrys.add(entry3);
		context.checking(new Expectations() {

			{

				exactly(1).of(check3).check(target, creds, enforcer);
				will(returnValue(false));

				oneOf(check4).check(target, creds, enforcer);
				will(returnValue(true));

				exactly(1).of(check5).check(target, creds, enforcer);
				will(returnValue(true));
			}
		});
		Entry<String, BaseCheck> ret = reducer.reduce(entrys);
		assertEquals("or_expr", ret.getKey());
		assertTrue(ret.getValue() instanceof OrCheck);
		assertTrue(ret.getValue().check(target, creds, enforcer));
	}

	@Test
	public void testReduce4() {
		final List<Entry<String, BaseCheck>> entrys = new ArrayList<Entry<String, BaseCheck>>();
		Entry<String, BaseCheck> entry1 = Maps.immutableEntry("or_expr", (BaseCheck) orCheck);
		Entry<String, BaseCheck> entry2 = Maps.immutableEntry("or", check2);
		Entry<String, BaseCheck> entry3 = Maps.immutableEntry("check", check3);
		entrys.add(entry1);
		entrys.add(entry2);
		entrys.add(entry3);
		context.checking(new Expectations() {

			{

				exactly(1).of(check3).check(target, creds, enforcer);
				will(returnValue(true));

				oneOf(check4).check(target, creds, enforcer);
				will(returnValue(true));

				exactly(1).of(check5).check(target, creds, enforcer);
				will(returnValue(false));
			}
		});
		Entry<String, BaseCheck> ret = reducer.reduce(entrys);
		assertEquals("or_expr", ret.getKey());
		assertTrue(ret.getValue() instanceof OrCheck);
		assertTrue(ret.getValue().check(target, creds, enforcer));
	}

	@Test
	public void testReduce5() {
		final List<Entry<String, BaseCheck>> entrys = new ArrayList<Entry<String, BaseCheck>>();
		Entry<String, BaseCheck> entry1 = Maps.immutableEntry("or_expr", (BaseCheck) orCheck);
		Entry<String, BaseCheck> entry2 = Maps.immutableEntry("or", check2);
		Entry<String, BaseCheck> entry3 = Maps.immutableEntry("check", check3);
		entrys.add(entry1);
		entrys.add(entry2);
		entrys.add(entry3);
		context.checking(new Expectations() {

			{

				exactly(1).of(check3).check(target, creds, enforcer);
				will(returnValue(true));

				oneOf(check4).check(target, creds, enforcer);
				will(returnValue(false));

				exactly(1).of(check5).check(target, creds, enforcer);
				will(returnValue(true));
			}
		});
		Entry<String, BaseCheck> ret = reducer.reduce(entrys);
		assertEquals("or_expr", ret.getKey());
		assertTrue(ret.getValue() instanceof OrCheck);
		assertTrue(ret.getValue().check(target, creds, enforcer));
	}
	
	@Test
	public void testReduce6() {
		final List<Entry<String, BaseCheck>> entrys = new ArrayList<Entry<String, BaseCheck>>();
		Entry<String, BaseCheck> entry1 = Maps.immutableEntry("or_expr", (BaseCheck) orCheck);
		Entry<String, BaseCheck> entry2 = Maps.immutableEntry("or", check2);
		Entry<String, BaseCheck> entry3 = Maps.immutableEntry("check", check3);
		entrys.add(entry1);
		entrys.add(entry2);
		entrys.add(entry3);
		context.checking(new Expectations() {

			{

				exactly(1).of(check3).check(target, creds, enforcer);
				will(returnValue(false));

				oneOf(check4).check(target, creds, enforcer);
				will(returnValue(false));

				exactly(1).of(check5).check(target, creds, enforcer);
				will(returnValue(false));
			}
		});
		Entry<String, BaseCheck> ret = reducer.reduce(entrys);
		assertEquals("or_expr", ret.getKey());
		assertTrue(ret.getValue() instanceof OrCheck);
		assertFalse(ret.getValue().check(target, creds, enforcer));
	}

}
