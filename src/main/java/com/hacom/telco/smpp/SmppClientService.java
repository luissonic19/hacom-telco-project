package com.hacom.telco.smpp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SmppClientService {
    private static final Logger logger = LogManager.getLogger(SmppClientService.class);

    // In a real scenario, this would configure cloudhopper-smpp DefaultSmppClient
    public void sendSms(String phoneNumber, String message) {
        // Simulating the SMPP SMS sending using cloudhopper library concepts
        logger.info("Sending SMS via SMPP to {}: '{}'", phoneNumber, message);
        // SmppSession session = client.bind(...)
        // session.submit(...)
        logger.debug("SMS sent successfully.");
    }
}
