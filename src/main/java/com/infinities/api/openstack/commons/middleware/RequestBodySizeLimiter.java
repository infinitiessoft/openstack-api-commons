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
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.infinities.api.openstack.commons.config.Config;

@Component
@Priority(1003)
@PreMatching
public class RequestBodySizeLimiter extends Middleware {

	private final int maxRequestSize;


	@Autowired
	public RequestBodySizeLimiter(Config config) {
		maxRequestSize = config.getOpt("osapi_max_request_body_size").asInteger();
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (requestContext.getLength() > maxRequestSize) {
			requestContext.abortWith(Response.status(Response.Status.REQUEST_ENTITY_TOO_LARGE)
					.entity("Request is too large.").build());
			return;
		}

		if (requestContext.getEntityStream().available() > maxRequestSize) {
			requestContext.abortWith(Response.status(Response.Status.REQUEST_ENTITY_TOO_LARGE)
					.entity("Request is too large.").build());
			return;
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

	}

}
