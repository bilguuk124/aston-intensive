package com.aston.dz2.utility;

import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerProducer {

    @Produces
    public Logger getLogger(final InjectionPoint ip){
        return LoggerFactory.getLogger(ip.getMember().getDeclaringClass());
    }

}
