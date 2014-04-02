/*
 * $Id: ExampleSupport.java 739661 2009-02-01 00:06:00Z davenewton $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.tr8n.struts2.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;
import com.tr8n.core.Session;

/**
 * Base Action class for the Tutorial package.
 */
public class Tr8nAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {
	private static final long serialVersionUID = 1L;
	
	protected HttpServletRequest request;
	protected HttpServletResponse response;

	public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
     
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }	 

    protected Session getTr8n() {
		return (Session) request.getAttribute("tr8n");
	}
	
}
