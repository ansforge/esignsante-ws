/**
 * (c) Copyright 1998-2023, ANS. All rights reserved.
 */

package fr.gouv.esante.api.sign.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
@Component
public class IPAddressInterceptor implements HandlerInterceptor {
	
	/**
	 * The log.
	 */
	Logger log = LoggerFactory.getLogger(IPAddressInterceptor.class);

 
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
 
        String ipAddress = request.getRemoteAddr();
        log.info(ipAddress);
 
        return true;
    }
}
