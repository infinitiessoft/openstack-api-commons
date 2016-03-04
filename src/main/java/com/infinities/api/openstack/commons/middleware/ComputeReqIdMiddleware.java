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
package com.infinities.api.openstack.commons.middleware;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.ext.Provider;

import com.infinities.api.openstack.commons.utils.ContextUtils;

@Provider
@Priority(Middleware.COMPUTE_REQID)
public class ComputeReqIdMiddleware extends Middleware {

	public final static String ENV_REQUEST_ID = "openstack.request_id";
	private final static String HTTP_RESP_HEADER_REQUEST_ID = "x-compute-request-id";


	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String reqId = ContextUtils.generateRequestId();
		requestContext.setProperty(ENV_REQUEST_ID, reqId);
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		if (!responseContext.getHeaders().containsKey(HTTP_RESP_HEADER_REQUEST_ID)) {
			responseContext.getHeaders().add(HTTP_RESP_HEADER_REQUEST_ID, requestContext.getProperty(ENV_REQUEST_ID));
		}
	}

}
