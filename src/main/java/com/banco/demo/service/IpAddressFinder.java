package com.banco.demo.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author oscar.morales
 */
@Service
public class IpAddressFinder {

    public Optional<String> findClientIpAddress() {
        Optional<String> ipAddress = Optional.empty();
        try {
            if (RequestContextHolder.getRequestAttributes() != null) {
                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (servletRequestAttributes != null) {
                    HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
                    ipAddress = Optional.of(httpServletRequest.getRemoteAddr());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(IpAddressFinder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ipAddress;
    }

}
