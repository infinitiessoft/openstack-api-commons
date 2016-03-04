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
package com.infinities.nova.extensions;

import com.google.common.base.Strings;
import com.infinities.api.openstack.commons.context.OpenstackRequestContext;
import com.infinities.api.openstack.commons.exception.ForbiddenException;
import com.infinities.api.openstack.commons.policy.Policy;
import com.infinities.api.openstack.commons.policy.Target;

/**
 * @author pohsun
 *
 */
public class Extensions {

	private Extensions() {

	}

	public static Authorizer extensionAuthorizer(String apiName, String extensionName) {
		return new CoreAuthorizer(String.format("%s_extension", apiName), extensionName);
	}

	/**
	 * @param apiName
	 * @param extensionName
	 * @return
	 */
	public static Authorizer softExtensionAuthorizer(String apiName, String extensionName) {
		final Authorizer hardAuthorize = extensionAuthorizer(apiName, extensionName);

		Authorizer softAuthorizer = new Authorizer() {

			@Override
			public boolean authorize(OpenstackRequestContext context, Target target, String action) throws Exception {
				try {
					hardAuthorize.authorize(context, null, action);
					return true;
				} catch (ForbiddenException e) {
					return false;
				}
			}

		};

		return softAuthorizer;
	}


	public static class CoreAuthorizer implements Authorizer {

		private String apiName;
		private String extensionName;


		private CoreAuthorizer(String apiName, String extensionName) {
			this.apiName = apiName;
			this.extensionName = extensionName;
		}

		@Override
		public boolean authorize(final OpenstackRequestContext context, Target target, String action) throws Exception {
			if (target == null) {
				target = context;
			}
			String act;
			if (Strings.isNullOrEmpty(action)) {
				act = String.format("%s:%s", apiName, extensionName);
			} else {
				act = String.format("%s:%s:%s", apiName, extensionName, action);
			}
			return Policy.enforce(context, act, target, true, null);
		}
	}

}
