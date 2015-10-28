package com.sandbox.iceroads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.*;


public class App 
{
    public static void main( String[] args )
    {
    	 Logger logger = LoggerFactory.getLogger(App.class);
    	 logger.info("IceRoads Scheduler Started at " + Instant.now());
    	 
    	 logger.info("IceRoads Scheduler Finihsed at " + Instant.now());
    }
}
