/**
 * Copyright (c) 2016 Translation Exchange, Inc. All rights reserved.
 *
 *  _______                  _       _   _             ______          _
 * |__   __|                | |     | | (_)           |  ____|        | |
 *    | |_ __ __ _ _ __  ___| | __ _| |_ _  ___  _ __ | |__  __  _____| |__   __ _ _ __   __ _  ___
 *    | | '__/ _` | '_ \/ __| |/ _` | __| |/ _ \| '_ \|  __| \ \/ / __| '_ \ / _` | '_ \ / _` |/ _ \
 *    | | | | (_| | | | \__ \ | (_| | |_| | (_) | | | | |____ >  < (__| | | | (_| | | | | (_| |  __/
 *    |_|_|  \__,_|_| |_|___/_|\__,_|\__|_|\___/|_| |_|______/_/\_\___|_| |_|\__,_|_| |_|\__, |\___|
 *                                                                                        __/ |
 *                                                                                       |___/
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.translationexchange.struts.interceptors;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.translationexchange.core.Session;
import com.translationexchange.core.Tml;
import com.translationexchange.core.Utils;
import com.translationexchange.j2ee.servlets.LocalizedServlet;
import com.translationexchange.j2ee.utils.SecurityUtils;
import com.translationexchange.j2ee.utils.SessionUtils;

public class TmlInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = 5065298925572763728L;

    /**
     * Can be extended by the sub-class
     * @return
     */
    protected String getCurrentLocale() {
    	return null;
    }

    /**
     * Can be extended by the sub-class
     * @return
     */
    protected String getCurrentSource() {
    	return null;
    }
  	
    /**
     * Initializes session parameters
     * 
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	protected Map<String, Object> prepareSessionParams(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	Map<String, Object> params = SecurityUtils.decode(SessionUtils.getSessionCookie(SessionUtils.getCookieName(), req));
    	if (params != null) Tml.getLogger().debug(params);

    	// Locale can be forced by the user
    	String locale = getCurrentLocale();
	    if (locale == null) {
	    	// Or passed as a parameter
	    	locale = req.getParameter("locale");
	    	if (locale != null) {
	    		params.put("locale", locale);
	    		SessionUtils.setSessionCookie(SessionUtils.getCookieName(), SecurityUtils.encode(params), resp);
			    Tml.getLogger().debug("Param Locale: " + locale);
	    	} else if (params.get("locale") != null) {
	    		// Or loaded from the cookie 
	    		locale = (String) params.get("locale");
			    Tml.getLogger().debug("Cookie Locale: " + locale);
	    	} else {
	    		// Or taken from the Accepted Locale header 
	    		List<String> locales = new ArrayList<String>();
	    		Enumeration<Locale> e = req.getLocales();
	    		while (e.hasMoreElements()) {
	    			locales.add(e.nextElement().getLanguage());
	    		}
	    		locale = Utils.join(locales, ",");
	    		Tml.getLogger().debug("Header Locale: " + locale);
	    	}
	    } else {
		    Tml.getLogger().debug("User Locale: " + locale);
	    }
	    params.put("locale", locale);

	    Tml.getLogger().debug("Selected locale: " + locale);
	    
    	String source = getCurrentSource();
	    if (source == null) {
	    	URL url = new URL(req.getRequestURL().toString());
	    	source = url.getPath();
	    }
	    params.put("source", source);
	    	
	    return params;
    }
    
  	@Override
    public String intercept(ActionInvocation invocation) throws Exception {
		ActionContext context = invocation.getInvocationContext();  
	    HttpServletRequest req = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
	    HttpServletResponse resp = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);
	    
	    Session tmlSession = null;
	    Long t0 = (new Date()).getTime();
	    
	    try {

	    	tmlSession = new Session(prepareSessionParams(req, resp));
	    	req.setAttribute(LocalizedServlet.TML_SESSION_KEY, tmlSession);
	    	
	    	return invocation.invoke();
	    } finally {
		    if (tmlSession != null) 
		    	tmlSession.getApplication().submitMissingTranslationKeys();
		    
		    Long t1 = (new Date()).getTime();
		    
		    Tml.getLogger().debug("Request took: " + (t1-t0) + " mls");
	    }
    }
    
}
