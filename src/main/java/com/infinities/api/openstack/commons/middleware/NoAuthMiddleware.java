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
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.infinities.api.openstack.commons.config.Config;
import com.infinities.api.openstack.commons.context.OpenstackRequestContext;

@Component
@Provider
@Priority(Middleware.AUTH)
public class NoAuthMiddleware extends Middleware {

	private final static Logger logger = LoggerFactory.getLogger(NoAuthMiddleware.class);
	@Context
	HttpServletRequest httpRequest;

	private boolean useForwardedFor;


	@Autowired
	public NoAuthMiddleware(Config config) {
		useForwardedFor = config.getOpt("use_forwarded_for").asBoolean();
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		try {
			baseCall(requestContext, true);
		} catch (Exception e) {
			logger.error("keystoneContext problem", e);
			throw new RuntimeException(e);
		}
	}

	protected void baseCall(ContainerRequestContext req, boolean projectIdInPath) throws Exception {
		logger.debug("NoAuthMiddleware begin");
		if (!req.getHeaders().containsKey("X-Auth-Token")) {
			String userId = req.getHeaderString("X-Auth-User");
			if (Strings.isNullOrEmpty(userId)) {
				userId = "admin";
			}
			String projectId = req.getHeaderString("X-Auth-Project-Id");
			if (Strings.isNullOrEmpty(projectId)) {
				projectId = "{projectId}";
			}
			String osUrl = null;
			if (projectIdInPath) {
				String url = StringUtils.removeEnd(req.getUriInfo().getRequestUri().toString(), "/");
				osUrl = Joiner.on("/").join(new String[] { url, projectId });
			} else {
				osUrl = StringUtils.removeEnd(req.getUriInfo().getRequestUri().toString(), "/");
			}
			Response res =
					Response.status(204).header("X-Auth-Token", String.format("%s:%s", projectId, userId))
							.header("X-Server-Management-Url", osUrl).type("text/plain").build();
			req.abortWith(res);
			return;
		}

		String token = req.getHeaderString("X-Auth-Token");
		String split[] = token.split(":");
		String projectId = split[0];
		String userId = split[0];
		if (split.length >= 3) {
			projectId = split[2];
		}
		String remoteAddress = httpRequest.getRemoteAddr();
		if (Strings.isNullOrEmpty(remoteAddress)) {
			remoteAddress = "127.0.0.1";
		}
		if (useForwardedFor) {
			remoteAddress = req.getHeaderString("X-Forwarded-For");
			if (Strings.isNullOrEmpty(remoteAddress)) {
				remoteAddress = "127.0.0.1";
			}
		}
		OpenstackRequestContext ctx =
				new OpenstackRequestContext(userId, projectId, true, "no", null, remoteAddress, null, null, null, true,
						null, null, null, null, false);

		req.setProperty("nova.context", ctx);
		logger.debug("NoAuthMiddleware end");
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

	}
}
