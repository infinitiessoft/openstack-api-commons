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
package com.infinities.api.openstack.commons.context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jersey.repackaged.com.google.common.collect.Sets;

import com.google.common.base.Strings;
import com.infinities.api.openstack.commons.exception.ForbiddenException;
import com.infinities.api.openstack.commons.policy.Credentials;
import com.infinities.api.openstack.commons.policy.Policy;
import com.infinities.api.openstack.commons.policy.Target;
import com.infinities.api.openstack.commons.utils.ContextUtils;
import com.infinities.keystone4j.middleware.model.Access.Service;

public class OpenstackRequestContext implements Cloneable, Credentials, Target {

	// private final static Logger logger =
	// LoggerFactory.getLogger(NovaRequestContext.class);
	private String userId;
	private String projectId;
	private String projectName;
	private Set<String> roles;
	private String readDeleted;
	private String remoteAddress;
	private Calendar timestamp;
	private String requestId;
	private String authToken;
	private List<Service> serviceCatalog;
	private boolean instanceLockChecked;
	private String quotaClass;
	private String userName;
	private Boolean isAdmin;


	// private final boolean ownerIsTenant;

	// isAdmin=none, readDeleted="no", roles=none, remoteAddress=none,
	// timestamp=none, requestId=none, authToken=none, overwrite=true,
	// quotaClass=none, userName=none, projectName=none, serviceCatalog=none,
	// instanceLockChecked=false
	public OpenstackRequestContext(String userId, String projectId, Boolean isAdmin, String readDeleted, String[] roles,
			String remoteAddress, Calendar timestamp, String requestId, String authToken, boolean overwrite,
			String quotaClass, String userName, String projectName, List<Service> serviceCatalog, boolean instanceLockChecked)
			throws Exception {
		this.userId = userId;
		this.projectId = projectId;
		if (roles == null) {
			roles = new String[] {};
		}
		this.roles = Sets.newHashSet(roles);
		setReadDeleted(readDeleted);
		this.remoteAddress = remoteAddress;
		if (timestamp == null) {
			timestamp = Calendar.getInstance();
		}
		this.timestamp = timestamp;
		if (Strings.isNullOrEmpty(requestId)) {
			requestId = ContextUtils.generateRequestId();
		}
		this.requestId = requestId;
		this.authToken = authToken;

		if (serviceCatalog != null) {
			this.serviceCatalog = new ArrayList<Service>();
			for (Service service : serviceCatalog) {
				if ("volume".equals(service.getType()) || "volumev2".equals(service.getType())) {
					serviceCatalog.add(service);
				}
			}
		} else {
			this.serviceCatalog = new ArrayList<Service>();
		}

		this.instanceLockChecked = instanceLockChecked;
		this.quotaClass = quotaClass;
		this.userName = userName;
		this.projectName = projectName;
		this.isAdmin = isAdmin;
		if (this.isAdmin == null) {
			this.isAdmin = Policy.checkIsAdmin(this);
		}
	}

	public void setReadDeleted(String readDelete) {
		Set<String> type = new HashSet<String>();
		type.add("no");
		type.add("yes");
		type.add("only");

		if (!type.contains(readDelete)) {
			throw new IllegalArgumentException("readDelete can only be one of 'no', 'yes' or 'only', not " + readDelete);
		}
		this.readDeleted = readDelete;
	}

	protected void delReadDelete() {
		this.readDeleted = null;
	}

	// readDeleted=null
	public OpenstackRequestContext elevated(String readDeleted) throws CloneNotSupportedException {
		OpenstackRequestContext context = (OpenstackRequestContext) this.clone();
		context.setIsAdmin(true);

		if (!context.getRoles().contains("admin")) {
			context.getRoles().add("admin");
		}

		if (!Strings.isNullOrEmpty(readDeleted)) {
			context.setReadDeleted(readDeleted);
		}
		return context;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public String getReadDeleted() {
		return readDeleted;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public Calendar getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public List<Service> getServiceCatalog() {
		return serviceCatalog;
	}

	public void setServiceCatalog(List<Service> serviceCatalog) {
		this.serviceCatalog = serviceCatalog;
	}

	public boolean isInstanceLockChecked() {
		return instanceLockChecked;
	}

	public void setInstanceLockChecked(boolean instanceLockChecked) {
		this.instanceLockChecked = instanceLockChecked;
	}

	public String getQuotaClass() {
		return quotaClass;
	}

	public void setQuotaClass(String quotaClass) {
		this.quotaClass = quotaClass;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		OpenstackRequestContext clone = (OpenstackRequestContext) super.clone();
		clone.setRoles(new HashSet<String>(roles));
		return clone;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	public static boolean isUserContext(OpenstackRequestContext context) {
		if (context == null) {
			return false;
		}
		if (context.getIsAdmin()) {
			return false;
		}
		if (Strings.isNullOrEmpty(context.getUserId()) || Strings.isNullOrEmpty(context.getProjectId())) {
			return false;
		}
		return true;
	}

	public static void authorizeQuotaClassContext(OpenstackRequestContext context, String quotaClass) {
		if (isUserContext(context)) {
			if (Strings.isNullOrEmpty(context.getQuotaClass())) {
				throw new ForbiddenException();
			} else if (!context.getQuotaClass().equals(quotaClass)) {
				throw new ForbiddenException();
			}
		}
	}

	public static void authorizeProjectContext(OpenstackRequestContext context, String projectid) {
		if (isUserContext(context)) {
			if (Strings.isNullOrEmpty(context.getProjectId())) {
				throw new ForbiddenException();
			} else if (!context.getProjectId().equals(projectid)) {
				throw new ForbiddenException();
			}
		}

	}

	public static void authorizeUserContext(OpenstackRequestContext context, String userid) {
		if (isUserContext(context)) {
			if (Strings.isNullOrEmpty(context.getUserId())) {
				throw new ForbiddenException();
			} else if (!context.getUserId().equals(userid)) {
				throw new ForbiddenException();
			}
		}
	}

	public boolean getShowDeleted() {
		if (getIsAdmin()) {
			return true;
		}
		return false;
	}

}
