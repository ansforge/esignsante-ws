package fr.gouv.esante.api.sign.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
 
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
@Component
public class IPAddressInterceptor implements HandlerInterceptor {
 
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
 
 
        String ipAddress = request.getHeader("X-Forward-For");
 
        if(ipAddress== null){
 
            ipAddress = request.getRemoteAddr();
        }
 
        return false;
    }
}
