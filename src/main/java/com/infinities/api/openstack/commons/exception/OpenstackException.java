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
package com.infinities.api.openstack.commons.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public abstract class OpenstackException extends SafeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LoggerFactory.getLogger(OpenstackException.class);
	private Map<String, List<Object>> headers = new HashMap<String, List<Object>>();
	private boolean safe = false;
	private String message;
	private int code;


	public OpenstackException(String message, Object... args) {
		this(500, message, args);
	}

	public OpenstackException(int code, String message, Object... args) {
		super(null, Status.fromStatusCode(code));
		this.code = code;
		if (Strings.isNullOrEmpty(message)) {
			try {
				if (args == null) {
					args = new String[] {};
				}
				this.message = String.format(this.getMsgFmt(), args);
			} catch (Exception e) {
				logger.error("Exception in string format operation", e);
				for (Object entry : args) {
					logger.error("{}", new Object[] { entry });
				}
				this.message = this.getMsgFmt();
			}
		}
	}

	public String getMsgFmt() {
		return "An unknown exception occurred.";
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public Map<String, List<Object>> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, List<Object>> headers) {
		this.headers = headers;
	}

	@Override
	public boolean isSafe() {
		return safe;
	}

	public void setSafe(boolean safe) {
		this.safe = safe;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
