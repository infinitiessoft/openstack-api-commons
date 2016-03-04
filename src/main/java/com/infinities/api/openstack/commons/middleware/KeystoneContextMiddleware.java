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
import java.util.List;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.infinities.api.openstack.commons.config.Config;
import com.infinities.api.openstack.commons.context.OpenstackRequestContext;
import com.infinities.keystone4j.middleware.model.Access;
import com.infinities.skyport.util.JsonUtil;

@Component
@Provider
@Priority(Middleware.AUTH)
public class KeystoneContextMiddleware extends Middleware {

	private final static Logger logger = LoggerFactory.getLogger(KeystoneContextMiddleware.class);
	@Context
	HttpServletRequest httpRequest;

	private boolean useForwardedFor;


	@Autowired
	public KeystoneContextMiddleware(Config config) {
		useForwardedFor = config.getOpt("use_forwarded_for").asBoolean();
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		logger.debug("KeystonecontextMiddleware begin");
		try {
			call(requestContext);
		} catch (Exception e) {
			logger.error("keystoneContext problem", e);
			throw new RuntimeException(e);
		}
		logger.debug("KeystonecontextMiddleware end");
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

	}

	public void call(ContainerRequestContext env) throws Exception {
		logger.debug("NovaKeystoneContextMiddleware begin");
		String userId = env.getHeaderString("X-User-Id");
		if (Strings.isNullOrEmpty(userId)) {
			userId = env.getHeaderString("X-User");
		}

		if (Strings.isNullOrEmpty(userId)) {
			logger.debug("Neither X-User-Id nor X-User found in request");
			env.abortWith(Response.status(Status.UNAUTHORIZED).build());
			return;
		}

		String[] roles = getRoles(env);
		logger.debug("Roles in headers: {}", new Object[] { roles });
		String projectId;

		if (env.getHeaders().containsKey("X-Tenant-Id")) {
			projectId = env.getHeaderString("X-Tenant-Id");
		} else {
			projectId = env.getHeaderString("X-Tenant");
		}

		String projectName = env.getHeaderString("X-Tenant-Name");
		String userName = env.getHeaderString("X-User-Name");

		String reqId = (String) env.getProperty(ComputeReqIdMiddleware.ENV_REQUEST_ID);

		logger.debug("projectId: {}, projectName: {}, userName: {}, requestId: {}", new Object[] { projectId, projectName,
				userName, reqId });

		String authToken = env.getHeaderString("X-Auth-Token");
		if (Strings.isNullOrEmpty(authToken)) {
			authToken = env.getHeaderString("X-Storage-Token");
		}

		String remoteAddress = null;
		remoteAddress = httpRequest.getRemoteAddr();
		if (useForwardedFor) {
			remoteAddress = env.getHeaderString("X-Forwarded-For");
			if (Strings.isNullOrEmpty(remoteAddress)) {
				remoteAddress = httpRequest.getRemoteAddr();
			}
		}
		logger.debug("remoteAddress: {}", new Object[] { remoteAddress });

		List<Access.Service> serviceCatalog = null;
		if (env.getHeaders().containsKey("X-Service-Catalog")) {
			try {
				String catalogHeader = env.getHeaderString("X-Service-Catalog");
				serviceCatalog = JsonUtil.readJson(catalogHeader, new TypeReference<List<Access.Service>>() {
				});
			} catch (Exception e) {
				env.abortWith(Response.status(Status.INTERNAL_SERVER_ERROR).entity("Invalid service catalog json").build());
				return;
			}
		}

		OpenstackRequestContext ctx =
				new OpenstackRequestContext(userId, projectId, null, "no", roles, remoteAddress, null, reqId, authToken,
						true, null, userName, projectName, serviceCatalog, false);
		env.setProperty("nova.context", ctx);
		logger.debug("NovaKeystoneContex end");
	}

	private String[] getRoles(ContainerRequestContext env) {
		String rolesStr;
		if (env.getHeaders().containsKey("X-Roles")) {
			rolesStr = env.getHeaderString("X-Roles");
		} else {
			rolesStr = env.getHeaderString("X-Role");
			if (Strings.isNullOrEmpty(rolesStr)) {
				rolesStr = "";
			}
			logger.warn("Sourcing roles from deprecated X-Role HTTP header");
		}
		String[] roles = rolesStr.split(",");
		for (int i = 0; i < roles.length; i++) {
			roles[i] = roles[i].trim();
		}
		return roles;
	}

}
