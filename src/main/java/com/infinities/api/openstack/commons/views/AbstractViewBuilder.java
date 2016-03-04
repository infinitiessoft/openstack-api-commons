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
package com.infinities.api.openstack.commons.views;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.infinities.api.openstack.commons.config.Config;
import com.infinities.api.openstack.commons.context.OpenstackRequestContext;
import com.infinities.api.openstack.commons.model.Link;

public abstract class AbstractViewBuilder {

	private final static Logger logger = LoggerFactory.getLogger(AbstractViewBuilder.class);

	private Config config;


	public AbstractViewBuilder(Config config) {
		this.config = config;
	}

	protected List<Link> getLinks(ContainerRequestContext request, String identifier, String collectionName)
			throws URISyntaxException {
		List<Link> links = new ArrayList<Link>();
		Link link = new Link();
		link.setRel("self");
		link.setHref(getHrefLink(request, identifier, collectionName));
		links.add(link);

		Link link2 = new Link();
		link2.setRel("bookmark");
		link2.setHref(getBookmarkLink(request, identifier, collectionName));
		links.add(link2);
		return links;
	}

	protected String getBookmarkLink(ContainerRequestContext request, String identifier, String collectionName)
			throws URISyntaxException {
		URI applicationUrl =
				UriBuilder.fromUri(request.getUriInfo().getBaseUri())
						.path(request.getUriInfo().getPathSegments().get(0).getPath()).build();
		URI baseUrl = removeVersionFromHref(applicationUrl);
		baseUrl = updateComputeLinkPrefix(baseUrl);
		return osPathJoin(baseUrl.toString(), getProjectId(request), collectionName, identifier);
	}

	private URI removeVersionFromHref(URI uri) {
		String urlParts[] = uri.getPath().split("/", 2);
		if (urlParts[1].matches("^v([0-9]+|[0-9]+\\.[0-9]+)(/.*|$)")) {
			urlParts = new String[] { urlParts[0] };
		}
		String newPath = Joiner.on('/').join(urlParts);
		if (newPath.equals(uri.getPath())) {
			String msg = String.format("href %s does not contain version", uri.toString());
			logger.debug("href {} does not contain version", uri.toString());
			throw new IllegalArgumentException(msg);
		}

		URI ret = UriBuilder.fromUri(uri).replacePath(newPath).build();

		return ret;
	}

	protected String getHrefLink(ContainerRequestContext request, String identifier, String collectionName)
			throws URISyntaxException {
		URI applicationUrl =
				UriBuilder.fromUri(request.getUriInfo().getBaseUri())
						.path(request.getUriInfo().getPathSegments().get(0).getPath()).build();
		String prefix = updateComputeLinkPrefix(applicationUrl).toString();
		return osPathJoin(prefix, getProjectId(request), collectionName, identifier);
	}

	protected String getProjectId(ContainerRequestContext request) {
		OpenstackRequestContext context = (OpenstackRequestContext) request.getProperty("nova.context");
		String projectId = context.getProjectId();

		if (request.getUriInfo().getRequestUri().toString().contains(projectId)) {
			return projectId;
		}
		return "";
	}

	protected URI updateComputeLinkPrefix(URI applicationUrl) throws URISyntaxException {
		return updateLinkPrefix(applicationUrl, config.getOpt("osapi_compute_link_prefix").asText());
	}

	private URI updateLinkPrefix(URI origUrl, String prefix) throws URISyntaxException {
		if (Strings.isNullOrEmpty(prefix)) {
			return origUrl;
		}

		URI prefixParts = new URI(prefix);
		URI urlParts =
				UriBuilder.fromUri(origUrl).scheme(prefixParts.getScheme()).host(prefixParts.getHost())
						.port(prefixParts.getPort()).replacePath(prefixParts.getPath()).build();
		return urlParts;
	}

	protected String osPathJoin(String... strings) {
		checkNotNull(strings);
		checkArgument(strings.length > 0);
		final CharMatcher joinCharMatcher = CharMatcher.is('/');
		return Joiner.on('/').join(Iterables.transform(Arrays.asList(strings), new Function<String, String>() {

			@Override
			public String apply(final String input) {
				return joinCharMatcher.trimFrom(input);
			}
		}));
	}

	protected String getNextLink(ContainerRequestContext requestContext, String identifier, String collectionName)
			throws URISyntaxException {
		URI applicationUrl =
				UriBuilder.fromUri(requestContext.getUriInfo().getBaseUri())
						.path(requestContext.getUriInfo().getPathSegments().get(0).getPath()).build();
		URI prefix = updateComputeLinkPrefix(applicationUrl);
		String url = osPathJoin(prefix.toString(), getProjectId(requestContext), collectionName);
		URI uri = UriBuilder.fromUri(url).queryParam("marker", identifier).build();

		return uri.toString();
	}

}
