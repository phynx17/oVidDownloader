package org.phynx.vdownload.streams;

import org.apache.commons.httpclient.HttpMethodBase;
import org.phynx.vdownload.annotations.StreamType;

import com.google.inject.Inject;

/**
 * HTTP stream. Currently only support GET method
 * 
 * @author pandupradhana
 *
 */
public class HttpStream implements ServiceStream {

	/**
	 * Stream type. Will be injected by Guice. 
	 * We need this? :p
	 */
	@Inject @StreamType String HTTP_STREAM_TYP;

	/**
	 * Http method base
	 */
	private HttpMethodBase mMethodBase;
	
	/**
	 * Constructor with given HttpMethodBase
	 * @param aMethodBase HttpMethodBase
	 * @see org.apache.commons.httpclient.HttpMethodBase
	 */
	public HttpStream(HttpMethodBase aMethodBase) {
		mMethodBase = aMethodBase;
	}
	
	
	public void close() {
		mMethodBase.releaseConnection();
	}

}
