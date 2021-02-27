package com.joaovrmaia.playground.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class System {

    private static Logger logger = LogManager.getLogger(System.class);

    public static String getHost() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error("Failed to retrive hostname", e);
            return "unknow";
        }
    }

}
