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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.infinities.api.openstack.commons.policy.BaseCheck;
import com.infinities.api.openstack.commons.policy.Rules;
import com.infinities.api.openstack.commons.policy.check.TrueCheck;

public class RulesTest {

	private Rules rules;


	@Before
	public void setUp() throws Exception {
		rules = new Rules(null, null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadJson() throws IOException {
		File f = new File("config" + File.separator + "policy.json");
		String data = Files.asCharSource(f, Charsets.UTF_8).read();
		Rules r = Rules.loadJson(data, null);
		assertFalse(r.isEmpty());
	}

	@Test
	public void testGetRules() {
		BaseCheck c = new TrueCheck();
		Map<String, BaseCheck> map = new HashMap<String, BaseCheck>();
		map.put("test", c);
		assertEquals(0, rules.size());
		rules.putAll(map);
		assertEquals(1, rules.size());
		Map<String, BaseCheck> ret = rules.getRules();
		assertTrue(ret.containsKey("test"));
	}

	@Test
	public void testClear() {
		BaseCheck c = new TrueCheck();
		Map<String, BaseCheck> map = new HashMap<String, BaseCheck>();
		map.put("test", c);
		assertEquals(0, rules.size());
		rules.putAll(map);
		assertEquals(1, rules.size());
		rules.clear();
		assertEquals(0, rules.size());
	}

	@Test
	public void testContainsKey() throws IOException {
		File f = new File("config" + File.separator + "policy.json");
		String data = Files.asCharSource(f, Charsets.UTF_8).read();
		Rules r = Rules.loadJson(data, null);
		int size = r.size();
		assertTrue(r.containsKey("network:get_dns_domains"));
		r.remove("network:get_dns_domains");
		assertEquals(size - 1, r.size());
		assertFalse(r.containsKey("network:get_dns_domains"));
	}

	@Test
	public void testContainsValue() throws IOException {
		File f = new File("config" + File.separator + "policy.json");
		String data = Files.asCharSource(f, Charsets.UTF_8).read();
		Rules r = Rules.loadJson(data, null);
		assertTrue(r.containsValue(r.get("network:delete_dns_domain")));
	}

	@Test
	public void testEntrySet() {
		BaseCheck c = new TrueCheck();
		Map<String, BaseCheck> map = new HashMap<String, BaseCheck>();
		map.put("test", c);
		assertEquals(0, rules.entrySet().size());
		rules.putAll(map);
		assertEquals(1, rules.entrySet().size());
	}

	@Test
	public void testGet() {
		BaseCheck c = new TrueCheck();
		Map<String, BaseCheck> map = new HashMap<String, BaseCheck>();
		map.put("test", c);
		assertEquals(0, rules.size());
		rules.putAll(map);
		assertEquals(1, rules.size());
		assertEquals(c, rules.get("test"));
	}

	@Test
	public void testIsEmpty() {
		Map<String, BaseCheck> map = new HashMap<String, BaseCheck>();
		map.put("test", new TrueCheck());
		assertTrue(rules.isEmpty());
		rules.putAll(map);
		assertFalse(rules.isEmpty());
	}

	@Test
	public void testKeySet() {
		Map<String, BaseCheck> map = new HashMap<String, BaseCheck>();
		map.put("test", new TrueCheck());
		assertFalse(rules.keySet().contains("test"));
		rules.putAll(map);
		assertTrue(rules.keySet().contains("test"));
	}

	@Test
	public void testPut() {
		BaseCheck c = new TrueCheck();
		assertEquals(0, rules.size());
		rules.put("test", c);
		assertEquals(1, rules.size());
		assertEquals(c, rules.get("test"));
	}

	@Test
	public void testPutAll() {
		Map<String, BaseCheck> map = new HashMap<String, BaseCheck>();
		map.put("test", new TrueCheck());
		assertFalse(rules.containsKey("test"));
		rules.putAll(map);
		assertTrue(rules.containsKey("test"));
	}

	@Test
	public void testRemove() throws IOException {
		File f = new File("config" + File.separator + "policy.json");
		String data = Files.asCharSource(f, Charsets.UTF_8).read();
		Rules r = Rules.loadJson(data, null);
		int size = r.size();
		assertTrue(r.containsKey("network:get_dns_domains"));
		r.remove("network:get_dns_domains");
		assertEquals(size - 1, r.size());
		assertFalse(r.containsKey("network:get_dns_domains"));
	}

	@Test
	public void testSize() throws IOException {
		assertEquals(0, rules.size());
		File f = new File("config" + File.separator + "policy.json");
		String data = Files.asCharSource(f, Charsets.UTF_8).read();
		Rules r = Rules.loadJson(data, null);
		assertTrue(r.size() > 0);
	}

	@Test
	public void testValues() throws IOException {
		File f = new File("config" + File.separator + "policy.json");
		String data = Files.asCharSource(f, Charsets.UTF_8).read();
		Rules r = Rules.loadJson(data, null);
		assertFalse(r.values().isEmpty());
	}

}
