package com.banco.demo.utils;

import com.banco.demo.service.IpAddressFinder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author oscar.morales
 */
@Getter
@Setter
@Component
public class LogManager {

    
    private static final String NOT_FOUND = "NotFound";

    @Autowired
    private IpAddressFinder ipAddressFinder;

    @Autowired
    private WebServiceLogger webServiceLogger;

    public synchronized void logTransactionInfo(final String message) {
        webServiceLogger.logLine("TransactionLog",
                "INFO  | ClientIP: " + ipAddressFinder.findClientIpAddress().orElse(NOT_FOUND)
                + " | " + " | " + message);
    }

    public synchronized void logTransactionWarning(final String message) {
        webServiceLogger.logLine("TransactionLog",
                "WARN  | ClientIP: " + ipAddressFinder.findClientIpAddress().orElse(NOT_FOUND)
                + " | " + " | " + message);
    }

    public synchronized void logTransactionError(final String message,
            final Exception exception) {
        webServiceLogger.logLine("TransactionLog",
                "ERROR | ClientIP: " + ipAddressFinder.findClientIpAddress().orElse(NOT_FOUND)
                + " | " + " | " + message + " - " + exception);
    }

}
