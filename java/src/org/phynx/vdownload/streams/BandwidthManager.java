package org.phynx.vdownload.streams;

/**
 * Bandwitdh manager. Implements this if stream bandwidth 
 * wants to be controlled at runtime.  
 * 
 * @author Pandu
 *
 */
public interface BandwidthManager {

	/**
	 * Set its bandwith limit. In kilo bytes per second measure. If set to -1, 
	 * then the client will discard it. 
	 * 
	 * @param aKBytesPerSec KBytes per second
	 */
	void setBWLimit(int aKBytesPerSec);
	
	/**
	 * Return current bandwidth limit in KBytes per second measure
	 * @return KBytes per second.
	 */
	int getCurrentLimit();
	
}
