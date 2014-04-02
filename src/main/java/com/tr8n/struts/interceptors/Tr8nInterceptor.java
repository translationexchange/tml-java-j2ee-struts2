package com.tr8n.struts.interceptors;

import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.tr8n.core.Session;
import com.tr8n.core.Tr8n;

public class Tr8nInterceptor extends AbstractInterceptor {
  private static final long serialVersionUID = 5065298925572763728L;
  
  	private String getSessionCookie(String key, HttpServletRequest request) throws Exception {
	    for (Cookie c : request.getCookies()) {
	    	if (c.getName().equals("tr8n_" + key))
	    		return URLDecoder.decode(c.getValue(), "UTF-8");
	    }
	    return null;
  	}

  	@Override
    public String intercept(ActionInvocation invocation) throws Exception {
		ActionContext context = invocation.getInvocationContext();  
	    HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
	    
	    Session tr8nSession = null;
	    Long t0 = (new Date()).getTime();
	    
	    try {
	    	tr8nSession = new Session();
		    request.setAttribute("tr8n", tr8nSession);
		    tr8nSession.init(getSessionCookie(tr8nSession.getApplication().getKey(), request));
		    tr8nSession.setCurrentSource(request.getRequestURI().toString());
		    
	    	return invocation.invoke();
	    } finally {
		    if (tr8nSession != null) 
		    	tr8nSession.getApplication().submitMissingTranslationKeys();
		    
		    Long t1 = (new Date()).getTime();
		    
		    Tr8n.getLogger().debug("Request took: " + (t1-t0) + " mls");
	    }
    }
    
}
