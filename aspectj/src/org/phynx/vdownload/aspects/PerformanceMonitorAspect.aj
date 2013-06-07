package org.phynx.vdownload.aspects;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import org.phynx.vdownload.Monitor;
import org.phynx.vdownload.VideoService;
import org.phynx.vdownload.streams.VideoStream;

/**
 * This aspect will see of video download performance 
 * 
 * @author pandupradhana
 * 
 */
public aspect PerformanceMonitorAspect {

	private static final Log LOG = 
		LogFactory.getLog("org.phynx.vdownload.aspects.PerformanceAspect");
	
	/**
	 * Find all match joint points where Instance of 
	 * VideoService and its descendant and all methods
	 * starts with grab 
	 */
	pointcut grabbingResource(VideoService aService) 
		: call (* VideoService+.grab*(..))
			&& target(aService);

	/**
	 * While reading bytes
	 */
	pointcut readingBytes(VideoStream aStream) 
		: execution (* VideoStream+.read*(..))
			&& target(aStream);	
	
	/**
	 * Before call to grabVideo
	 */
	before() : grabbingResource(VideoService) {
		if (isMonitorEnabled(Monitor.TOTAL_READ_STREAM)) {
			LOG.info(thisJoinPoint + ": Going to grab video..");
			//LOG.info(thisJoinPointStaticPart + ": Going to grab video..");
			//System.out.println(thisJoinPoint + ": Going to read bytes..");
		}
		
	}
	
	/**
	 * Advise above joint points
	 */
	Object around(VideoService aService) 
		: grabbingResource(aService) {
		long st = System.currentTimeMillis();
		Object o = proceed(aService);
		if (isMonitorEnabled(Monitor.TOTAL_TIME_GRAB)) {
			LOG.info("Total time for grabbing video took " + (System.currentTimeMillis()- st) + " ms. ");
			//System.out.println("Total time for grabbing video took " + (System.currentTimeMillis()- st) + " ms. ");
		}
		return o;
	}

	/**
	 * Advise while reading bytes
	 */
	Object around(VideoStream aStream) 
		: readingBytes(aStream) {
		long st = System.currentTimeMillis();
		Object o = proceed(aStream);
		if (isMonitorEnabled(Monitor.TOTAL_READ_STREAM)) {
			LOG.info("Read takes: " + (System.currentTimeMillis()- st) + " ms. ");
			//System.out.println("Read takes: " + (System.currentTimeMillis()- st) + " ms. ");
		}
		return o;
	}

	/**
	 * Check whether monitor is enabled
	 */
	private boolean isMonitorEnabled(String aType) {
		return "1".equals(System.getProperty(aType)) ? true : false;
	}
}
