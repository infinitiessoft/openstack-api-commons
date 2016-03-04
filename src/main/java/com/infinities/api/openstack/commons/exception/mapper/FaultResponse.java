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
package com.infinities.api.openstack.commons.exception.mapper;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class FaultResponse extends Response {

	private final static Logger logger = LoggerFactory.getLogger(FaultResponse.class);
	private final static Map<Integer, String> faultNames;

	static {
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(400, "badRequest");
		map.put(401, "unauthorized");
		map.put(403, "forbidden");
		map.put(404, "itemNotFound");
		map.put(405, "badMethod");
		map.put(409, "conflictingRequest");
		map.put(413, "overLimit");
		map.put(415, "badMediaType");
		map.put(429, "overLimit");
		map.put(501, "notImplemented");
		map.put(503, "serviceUnavailable");
		faultNames = Collections.unmodifiableMap(map);
	}

	private WebApplicationException wrappedExc;
	private int code;
	private String faultName;
	private String explanation;
	private Response response;


	public FaultResponse(WebApplicationException exception) {
		this.wrappedExc = exception;
		this.code = exception.getResponse().getStatus();
		this.explanation = this.wrappedExc.getMessage();
		logger.debug("explanation: {}", explanation);
		this.faultName = faultNames.get(code);
		if (Strings.isNullOrEmpty(faultName)) {
			this.faultName = "computeFault";
		}

		Fault fault = new Fault();
		fault.setCode(code);
		fault.setMessage(explanation);
		if (code == 413 || code == 429) {
			Object retry = wrappedExc.getResponse().getHeaders().getFirst("Retry-After");
			if (retry != null) {
				fault.setRetryAfter(retry);
			}
		}

		// throw new
		// WebApplicationException(Response.status(Status.FORBIDDEN).build());

		Map<String, Fault> faultData = new HashMap<String, Fault>();
		faultData.put(faultName, fault);

		ResponseBuilder builder = Response.status(code).entity(faultData).type(MediaType.APPLICATION_JSON_TYPE);

		for (Entry<String, List<Object>> entry : exception.getResponse().getHeaders().entrySet()) {
			for (Object obj : entry.getValue()) {
				builder.header(entry.getKey(), String.valueOf(obj));
			}
		}

		response = builder.build();
	}

	@Override
	public int getStatus() {
		return response.getStatus();
	}

	@Override
	public StatusType getStatusInfo() {
		return response.getStatusInfo();
	}

	@Override
	public Object getEntity() {
		return response.getEntity();
	}

	@Override
	public <T> T readEntity(Class<T> entityType) {
		return response.readEntity(entityType);
	}

	@Override
	public <T> T readEntity(GenericType<T> entityType) {
		return response.readEntity(entityType);
	}

	@Override
	public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
		return response.readEntity(entityType, annotations);
	}

	@Override
	public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
		return response.readEntity(entityType, annotations);
	}

	@Override
	public boolean hasEntity() {
		return response.hasEntity();
	}

	@Override
	public boolean bufferEntity() {
		return response.bufferEntity();
	}

	@Override
	public void close() {
		response.close();
	}

	@Override
	public MediaType getMediaType() {
		return response.getMediaType();
	}

	@Override
	public Locale getLanguage() {
		return response.getLanguage();
	}

	@Override
	public int getLength() {
		return response.getLength();
	}

	@Override
	public Set<String> getAllowedMethods() {
		return response.getAllowedMethods();
	}

	@Override
	public Map<String, NewCookie> getCookies() {
		return response.getCookies();
	}

	@Override
	public EntityTag getEntityTag() {
		return response.getEntityTag();
	}

	@Override
	public Date getDate() {
		return response.getDate();
	}

	@Override
	public Date getLastModified() {
		return response.getLastModified();
	}

	@Override
	public URI getLocation() {
		return response.getLocation();
	}

	@Override
	public Set<Link> getLinks() {
		return response.getLinks();
	}

	@Override
	public boolean hasLink(String relation) {
		return response.hasLink(relation);
	}

	@Override
	public Link getLink(String relation) {
		return response.getLink(relation);
	}

	@Override
	public Builder getLinkBuilder(String relation) {
		return response.getLinkBuilder(relation);
	}

	@Override
	public MultivaluedMap<String, Object> getMetadata() {
		return response.getMetadata();
	}

	@Override
	public MultivaluedMap<String, String> getStringHeaders() {
		return response.getStringHeaders();
	}

	@Override
	public String getHeaderString(String name) {
		return response.getHeaderString(name);
	}

}
