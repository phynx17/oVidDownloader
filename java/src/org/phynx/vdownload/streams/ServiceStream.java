package org.phynx.vdownload.streams;

/**
 * Provides basic interface for streams. Since services may available through HTTP, FTP or other 
 * protocol, specific stream should implements this. And it'd be use and wrapped in <tt>VideoStream</tt>
 * 
 * @author pandupradhana
 *
 */
public interface ServiceStream {

	/**
	 * Close underlying stream
	 *
	 */
	void close();
	
}
