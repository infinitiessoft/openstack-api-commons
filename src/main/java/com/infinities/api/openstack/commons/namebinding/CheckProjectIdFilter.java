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
package com.infinities.api.openstack.commons.namebinding;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import com.infinities.api.openstack.commons.context.OpenstackRequestContext;
import com.infinities.api.openstack.commons.middleware.Middleware;
import com.infinities.api.openstack.commons.utils.ResourceUtils;

/**
 * @author pohsun
 *
 */
@Provider
@CheckProjectId
@Priority(Middleware.CHECK_PROJECTID)
public class CheckProjectIdFilter implements ContainerRequestFilter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container
	 * .ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String projectId = requestContext.getUriInfo().getPathParameters().getFirst("projectId");
		OpenstackRequestContext novaContext = (OpenstackRequestContext) requestContext.getProperty("nova.context");
		ResourceUtils.processStack(requestContext, projectId, novaContext);
	}
}
