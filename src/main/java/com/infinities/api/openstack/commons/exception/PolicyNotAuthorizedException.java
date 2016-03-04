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

public class PolicyNotAuthorizedException extends ForbiddenException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public PolicyNotAuthorizedException() {
		super(null);
	}

	public PolicyNotAuthorizedException(String message, Object... args) {
		super(message, args);
	}

	@Override
	public String getMsgFmt() {
		return "Policy doesn't allow %s to be performed.";
	}

}
