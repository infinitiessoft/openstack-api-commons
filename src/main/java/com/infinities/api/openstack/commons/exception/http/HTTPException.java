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
package com.infinities.api.openstack.commons.exception.http;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import com.infinities.api.openstack.commons.exception.SafeException;

public class HTTPException extends SafeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String explanation;


	public HTTPException(final String message, final Response.Status status) {
		super(message, status);
		this.setExplanation(message);
	}

	public HTTPException(final Response.Status status) {
		this(null, status);
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	@Override
	public Map<String, List<Object>> getHeaders() {
		return this.getResponse().getHeaders();
	}

	@Override
	public int getCode() {
		return this.getResponse().getStatus();
	}

	@Override
	public boolean isSafe() {
		return false;
	}

}
